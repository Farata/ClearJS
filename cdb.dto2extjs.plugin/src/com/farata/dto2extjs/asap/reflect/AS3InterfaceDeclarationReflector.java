/**
 * Copyright (c) 2009 Farata Systems  http://www.faratasystems.com
 *
 * Licensed under The MIT License
 * Re-distributions of files must retain the above copyright notice.
 *
 * @license http://www.opensource.org/licenses/mit-license.php The MIT License
 *
 */
package com.farata.dto2extjs.asap.reflect;

import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.HashSet;

import com.farata.dto2extjs.annotations.FXClass;
import com.farata.dto2extjs.annotations.FXIgnore;
import com.farata.dto2extjs.asap.types.AS3TypeReflector;

import com.sun.mirror.declaration.InterfaceDeclaration;
import com.sun.mirror.declaration.MemberDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.Modifier;
import com.sun.mirror.declaration.ParameterDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;

import com.sun.mirror.type.InterfaceType;
import com.sun.mirror.type.TypeMirror;
import com.sun.mirror.util.Declarations;

public class AS3InterfaceDeclarationReflector extends AS3TypeDeclarationReflector {
	
	public AS3InterfaceDeclarationReflector(final InterfaceDeclaration interfaceDeclaration, final AS3TypeReflector typeReflector) {
		super(interfaceDeclaration, typeReflector);
	}
	
	protected TypeDeclarationVisitor createVisitor() {
		return new TypeDeclarationVisitor() {
			protected TypeDeclarationKind getTypeKind() { return TypeDeclarationKind.INTERFACE; }
			
			@Override protected void preprocess() {
				final TypeDeclaration type = source;
				final Map<String, IAS3PropertyDefinition> properties = _properties;
				final Set<MemberDeclaration> propertyMembers = _propertyMembers;
				final Set<MemberDeclaration> propertyDeclaringMembers = _propertyDeclaringMembers;

				properties.clear();
				propertyMembers.clear();
				propertyDeclaringMembers.clear();
				
				final Map<String, MethodDeclaration> getters = new TreeMap<String, MethodDeclaration>();
				final Map<String, Collection<MethodDeclaration>> setters = new TreeMap<String, Collection<MethodDeclaration>>();
				for (final MethodDeclaration method : type.getMethods() ) {
					final String name = method.getSimpleName();
					final int nameLength = name.length();

					final Collection<Modifier> modifiers = method.getModifiers();
					if ( modifiers.contains(Modifier.STATIC) || !modifiers.contains(Modifier.PUBLIC) )
						continue;
					
					final Collection<ParameterDeclaration> params = method.getParameters();
					final TypeMirror returnType = method.getReturnType();
					if ( 
						 params.size() == 0 && 
						 (nameLength > 3 && name.startsWith("get") && !_types.isVoid(returnType) || 
						  nameLength > 2 && name.startsWith("is") && _types.isBoolean(returnType) ) 
						 
					   )
						getters.put(name, method);
					else if (
							name.length() > 3 &&
							name.startsWith("set") && 
							params.size() == 1 && 
							_types.isVoid( returnType ) 
						  ) {
						Collection<MethodDeclaration> settersByName = setters.get(name);
						if (null == settersByName) {
							settersByName = new HashSet<MethodDeclaration>();
							setters.put(name, settersByName);
						}
						settersByName.add(method);
					}
				}
				
				// Iterate getters, then find corresponding setter
				// Create read-write property if matching pair found or read-only property
				for (final Iterator<Map.Entry<String, MethodDeclaration>> i = getters.entrySet().iterator(); i.hasNext(); ) {
					final Map.Entry<String, MethodDeclaration> entry = i.next();				
					final String getterName = entry.getKey();
					final MethodDeclaration getter = entry.getValue();
					
					final int prefixLength;
					if ( getterName.startsWith("get") )
						prefixLength = 3;
					else if ( getterName.startsWith("is") )
						prefixLength = 2;
					else
						prefixLength = 0;

					if ( prefixLength > 0) {
						final String setterName = "set" + getterName.substring(prefixLength);
						
						final Collection<MethodDeclaration> settersByName = setters.get(setterName);
						MethodDeclaration setter = null;
						
						if (null != settersByName) {
							boolean oneFound = false;
							for (final MethodDeclaration nextSetter : settersByName) {
								if ( nextSetter.getAnnotation(FXIgnore.class) != null )
									continue;

								if (oneFound) {
									// If more then one setter found then should be an error
									break;
								} else {
									final TypeMirror argumentType = nextSetter.getParameters().iterator().next().getType();
									if ( getter.getReturnType().equals(argumentType) ) {
										setter = nextSetter;
										oneFound = true;
									}
								}
							}
						}

						if (null != setter)
							setters.remove(setterName);

						if ( getter.getAnnotation(FXIgnore.class) != null )
							continue;
						
						final AS3MethodDeclarationKind declareGetter 
							= checkOverride(getter);
						final AS3MethodDeclarationKind declareSetter 
							= setter != null ? checkOverride(setter) : AS3MethodDeclarationKind.SKIP;
							
						if (declareGetter != AS3MethodDeclarationKind.SKIP ||
							declareSetter != AS3MethodDeclarationKind.SKIP) {
							final String propertyName = 
								Character.toLowerCase( getterName.charAt(prefixLength) ) + 
								getterName.substring(prefixLength + 1);
							
							properties.put(propertyName, 
								new AS3PropertyDefinition(
									getter, propertyName,
									getter.getReturnType(),
									declareGetter, declareSetter, 
									AS3MethodDeclarationKind.SKIP == declareGetter    ||
									AS3MethodDeclarationKind.SKIP == declareSetter    ||
									getter.getModifiers().contains(Modifier.ABSTRACT) || 
									setter.getModifiers().contains(Modifier.ABSTRACT)
								)
							);
						}
						propertyMembers.add( getter );
						if (null != setter)
							propertyMembers.add( setter );
						propertyDeclaringMembers.add( getter );
					}
				}
				
				
				// Iterate setters without matching getters 
				// and create write-only properties 
				for (final Iterator<Map.Entry<String, Collection<MethodDeclaration>>> i = setters.entrySet().iterator(); i.hasNext(); ) {
					final Map.Entry<String, Collection<MethodDeclaration>> entry = i.next();
					final String name = entry.getKey();
					final Collection<MethodDeclaration> settersByName = entry.getValue();
					boolean oneFound = false;
					for (final MethodDeclaration setter : settersByName) {
						if ( setter.getAnnotation(FXIgnore.class) != null )
							continue;
						if (oneFound) {
							// If more then one setter found then should be an error
							break;
						} else {
							final AS3MethodDeclarationKind declareSetter = checkOverride(setter);
							if (AS3MethodDeclarationKind.SKIP != declareSetter) {
								final String propertyName = Character.toLowerCase( name.charAt(3) ) + name.substring(4);
								properties.put(propertyName, 
									new AS3PropertyDefinition(
										setter, propertyName, setter.getParameters().iterator().next().getType(),
										AS3MethodDeclarationKind.SKIP, declareSetter, true 
									)
								);
							}
							propertyMembers.add( setter );
							propertyDeclaringMembers.add( setter );
							oneFound = true;
						}
					}
				}				
			}
			
			protected AS3MethodDeclarationKind checkOverride(final MethodDeclaration method) {
				final TypeDeclaration type = method.getDeclaringType();
				for (final InterfaceType superInterface : type.getSuperinterfaces() ) {
					final TypeDeclaration superDeclaration = superInterface.getDeclaration();
					if ( wasDefinedInInterface(method, superDeclaration) )
						return AS3MethodDeclarationKind.SKIP;
				}
				return AS3MethodDeclarationKind.DECLARE;		
			}
			
			protected boolean wasDefinedInInterface(final MethodDeclaration method, final TypeDeclaration intf) {
				final Declarations declarations = apt.getDeclarationUtils();
				for (final MethodDeclaration other : intf.getMethods() ) {
					if ( null != other.getAnnotation(FXIgnore.class) )
						continue;
					if ( declarations.overrides(method, other) )
						return true;
				}
				for (final InterfaceType superInterface : intf.getSuperinterfaces() ) {
					final TypeDeclaration superDeclaration = superInterface.getDeclaration();
					if ( null == superDeclaration.getAnnotation(FXClass.class) || null != superDeclaration.getAnnotation(FXIgnore.class) )
						continue;
					if ( wasDefinedInInterface(method, superDeclaration) )
						return true;
				}
				return false;
			}
			
		};
	}
}
