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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeMap;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.farata.dto2extjs.annotations.FXIgnore;
import com.farata.dto2extjs.annotations.FXMetadata;
import com.farata.dto2extjs.asap.types.AS3TypeReflector;

import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.MemberDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.Modifier;
import com.sun.mirror.declaration.ParameterDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;
import com.sun.mirror.declaration.ClassDeclaration;

import com.sun.mirror.type.ClassType;
import com.sun.mirror.type.DeclaredType;
import com.sun.mirror.type.TypeMirror;

import com.sun.mirror.util.Declarations;

public class AS3ClassDeclarationReflector extends AS3TypeDeclarationReflector {
	
	public AS3ClassDeclarationReflector(final ClassDeclaration classDeclaration, final AS3TypeReflector typeReflector) {
		super(classDeclaration, typeReflector);
	}
	
	protected TypeDeclarationVisitor createVisitor() {
		return new TypeDeclarationVisitor() {
			protected TypeDeclarationKind getTypeKind() { return TypeDeclarationKind.CLASS; }

			private boolean isInstancePublic(final MemberDeclaration member) {
				final Collection<Modifier> modifiers = member.getModifiers();
				return 
					!(modifiers.contains(Modifier.STATIC) || modifiers.contains(Modifier.TRANSIENT)) &&
					modifiers.contains(Modifier.PUBLIC);
			}
			
			protected boolean isGetterMethod(final MethodDeclaration method) {
				if (!isInstancePublic(method))
					return false;
				
				final String name = method.getSimpleName();
				final int nameLength = name.length();
				
				final Collection<ParameterDeclaration> params = method.getParameters();
				final TypeMirror returnType = method.getReturnType();
				
				return 	params.size() == 0 && 
				        (nameLength > 3 && name.startsWith("get") && !_types.isVoid(returnType) || 
						nameLength > 2 && name.startsWith("is") && _types.isBoolean(returnType)) 
				;
			}
			
			protected boolean isSetterMethod(final MethodDeclaration method) {
				if (!isInstancePublic(method))
					return false;
				
				final String name = method.getSimpleName();
				
				final Collection<ParameterDeclaration> params = method.getParameters();
				final TypeMirror returnType = method.getReturnType();
				
				return name.length() > 3 &&
				       name.startsWith("set") && 
				       params.size() == 1 && 
				       _types.isVoid( returnType ) 
 				;
			}
			
			
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
					if (isGetterMethod(method)) {
						getters.put(name, method);
					} else if (isSetterMethod(method)) {
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

						final boolean superSetter;
						if (setter != null) {
							setters.remove(setterName);
							superSetter = false;
						} else {
							setter = findSuperSetterOf(getter, getterName.substring(prefixLength));
							superSetter = true;
						}

						if ( getter.getAnnotation(FXIgnore.class) != null )
							continue;
						
						final String propertyName = 
							Character.toLowerCase( getterName.charAt(prefixLength) ) + 
							getterName.substring(prefixLength + 1);
						
						enlistProperty(propertyName, getter, false, setter, superSetter, properties);
						
						propertyMembers.add( getter );
						if (null != setter && !superSetter)
							propertyMembers.add( setter );
						propertyDeclaringMembers.add(getter);
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
							final String propertyName = Character.toLowerCase( name.charAt(3) ) + name.substring(4);
							enlistProperty(propertyName, findSuperGetterOf(setter, name.substring(3)), true, setter, false, properties);
							propertyMembers.add( setter );
							propertyDeclaringMembers.add( setter );
							oneFound = true;
						}
					}
				}				
			}
			
			@Override protected void _processTypeDeclaration() {
				final ClassDeclaration selfClassDeclaration = (ClassDeclaration)source;
				
				final DeclaredType superClass = selfClassDeclaration.getSuperclass();
				final boolean declareSuperclass = !superclasses.superClassIgnored(superClass.getDeclaration());
				if (declareSuperclass) {
					processSuperInterfaceOrClass(superClass, "superclass");
				}
				
				final Map<AnnotationTypeDeclaration, TypeDeclaration> features 
					= collectPropertiesAnnotations(source, false);
				
				if (null != features && !features.isEmpty()) {
					final String featuresTypeQName = NS_DTO2extjs + ':' + "features";
					final String featureTypeQName = NS_DTO2extjs + ':' + "feature";
					try {
						startElement(URI_DTO2extjs, "features", featuresTypeQName, new AttributesImpl());
						for (final Map.Entry<AnnotationTypeDeclaration, TypeDeclaration> feature : features.entrySet()) {
							final AttributesImpl attrs = new AttributesImpl();
							attrs.addAttribute(
								"", "name", "feature", "NMTOKEN", feature.getKey().getQualifiedName());
							attrs.addAttribute(
								"", "declared-by", "feature", "NMTOKEN", feature.getValue().getQualifiedName());
							
							startElement(URI_DTO2extjs, "feature", featureTypeQName, attrs);
							endElement(URI_DTO2extjs, "feature", featureTypeQName);
						}
						endElement(URI_DTO2extjs, "features", featuresTypeQName);
					} catch (final SAXException ex) {
						throw new SAXRuntimeException(ex);
					}
				}
				
				for (final FieldDeclaration field : source.getFields() )
					processFieldDeclaration(field);
			}
			
			protected void processFieldDeclaration(final FieldDeclaration field) {
				if (isInstancePublic(field)) {
					
					final String propertyName = field.getSimpleName();
					// Do not redefine property defined by pair of getter / setter
					if ( !_properties.containsKey(propertyName) ) {
						try {
							declareAS3Property(new AS3PropertyDefinition(field, propertyName, field.getType()));
						} catch (final SAXException ex) {
							throw new SAXRuntimeException(ex);
						}
					}
				}
				_processFieldDeclaration(field);
			}
			
			protected void _processFieldDeclaration(final FieldDeclaration field) {
				final String propertyName = field.getSimpleName();
				AS3PropertyDefinition prop = (AS3PropertyDefinition)_properties.get(propertyName);
				if ( field.getAnnotation(FXMetadata.class) != null && prop != null) { //TODO : Why prop is null for computed fields?
					prop.setMetadata( field.getAnnotation(FXMetadata.class).label(), removeCurlyBrackets(field.getAnnotation(FXMetadata.class).resource()), field.getAnnotation(FXMetadata.class).formatString());
				}
			}
			
			private String removeCurlyBrackets( final String  resourceClass) {
				if (resourceClass != null && resourceClass.indexOf("{") == 0)
					return resourceClass.substring(1, resourceClass.length()-1);
				else
					return resourceClass;
			}
			protected void enlistProperty(
					final String propertyName, 
					final MethodDeclaration getter, 
					final boolean inheritedGetter, 
					final MethodDeclaration setter,
					final boolean inheritedSetter,
					final Map<String, IAS3PropertyDefinition> properties) {
				
				final MethodDeclaration superGetter = inheritedGetter ? getter : null == getter ? null : superMethodOf(getter);
				final MethodDeclaration superSetter = inheritedSetter ? setter : null == setter ? null : superMethodOf(setter);
				final AS3MethodDeclarationKind declareGetter;
				final AS3MethodDeclarationKind declareSetter;
				
				/* DECLARATION MATRIX -- START */
				if (null != superGetter) {
					if (null == setter) {
						// Read-only and getter was defined previously
						declareGetter = AS3MethodDeclarationKind.SKIP;
						declareSetter = AS3MethodDeclarationKind.SKIP;
					} else if (null == superSetter) {
						// Setter will be ultimately defined, not declared previously
						declareSetter = AS3MethodDeclarationKind.DECLARE;
						if ( _types.isAbstract(setter) ) {
							declareGetter = AS3MethodDeclarationKind.SKIP;
						} else {
							// First full property definition
							// if getter is not abstract
							declareGetter = _types.isAbstract(getter) ?
								AS3MethodDeclarationKind.SKIP : AS3MethodDeclarationKind.OVERRIDE;
						}
					} else {
						// Both getter and setter are redefined
						// Declare property only if this is full property definition.
						final boolean isDef = 
							!_types.isAbstract(getter) && !_types.isAbstract(setter) && 
							(_types.isAbstract(superGetter) || _types.isAbstract(superSetter));
						if (isDef)
							declareGetter = declareSetter = AS3MethodDeclarationKind.OVERRIDE;
						else
							declareGetter = declareSetter = AS3MethodDeclarationKind.SKIP;
					}
				} else {
					// Getter will be ultimately defined, not declared previously
					declareGetter = null == getter ? AS3MethodDeclarationKind.SKIP : AS3MethodDeclarationKind.DECLARE;
					if (null == setter) {
						declareSetter = AS3MethodDeclarationKind.SKIP;
					} else if (null != superSetter) {
						if ( null == getter || _types.isAbstract(getter) ) {
							// Has super setter here and no property definition
							// while getter is abstract, so skip setter redefinition
							declareSetter = AS3MethodDeclarationKind.SKIP;
						} else {
							if ( _types.isAbstract(setter) )
								declareSetter = AS3MethodDeclarationKind.SKIP;
							else {
								// First full property definition
								declareSetter = AS3MethodDeclarationKind.OVERRIDE;
							}
						}
					} else {
						declareSetter = AS3MethodDeclarationKind.DECLARE;
					}
				}
				/* DECLARATION MATRIX -- END */

				if (declareGetter != AS3MethodDeclarationKind.SKIP ||
					declareSetter != AS3MethodDeclarationKind.SKIP) {
					
					properties.put(propertyName, 
						new AS3PropertyDefinition(
							null != getter ? getter : setter, 
							propertyName, 
							null != getter ? getter.getReturnType() : setter.getParameters().iterator().next().getType(),
							declareGetter, declareSetter, 
							AS3MethodDeclarationKind.SKIP == declareGetter    ||
							AS3MethodDeclarationKind.SKIP == declareSetter    ||
							_types.isAbstract(getter) || 
							_types.isAbstract(setter)
						)
					);
				}
			}
			
			protected Map<AnnotationTypeDeclaration, TypeDeclaration> collectPropertiesAnnotations(final TypeDeclaration type, final boolean startFromSuper) {
				final Map<AnnotationTypeDeclaration, TypeDeclaration> result = 
					new HashMap<AnnotationTypeDeclaration, TypeDeclaration>();
				
				abstract class CollectAnnotations<M extends MemberDeclaration> implements IMemberVisitor<Map<AnnotationTypeDeclaration, TypeDeclaration>, M> {
					
					public boolean visit(final M member) {
						if (matches(member)) {
							final TypeDeclaration type = member.getDeclaringType();
							for (final AnnotationMirror a : member.getAnnotationMirrors()) {
								final AnnotationTypeDeclaration decl = a.getAnnotationType().getDeclaration();
								if (!"com.farata.dto2extjs.annotations.FXIgnore".equals(decl.getQualifiedName())) {
									result.put(decl, type);									
								}
							}
						}
						// Drill-down always
						return false;
					}
					
					public Map<AnnotationTypeDeclaration, TypeDeclaration> result() {
						return result;
					}
					
					abstract protected boolean matches(M member);
				}
				
				final Map<IGetMemberDeclarations<MemberDeclaration>, CollectAnnotations<MemberDeclaration>> visitors = 
					new LinkedHashMap<IGetMemberDeclarations<MemberDeclaration>, CollectAnnotations<MemberDeclaration>>();
				
				@SuppressWarnings("unchecked")
				final Map<IGetMemberDeclarations<? extends MemberDeclaration>, CollectAnnotations<? extends MemberDeclaration>> unsafe = 
					(Map<IGetMemberDeclarations<? extends MemberDeclaration>, CollectAnnotations<? extends MemberDeclaration>>)(Map<?, ?>)visitors;
				
				unsafe.put(GET_METHODS, new CollectAnnotations<MethodDeclaration>() {
					public boolean matches(final MethodDeclaration method) {
						return
							(isGetterMethod(method) || isSetterMethod(method)) &&
						    null == method.getAnnotation(FXIgnore.class)
						;
					}
				});
				unsafe.put(GET_FIELDS, new CollectAnnotations<FieldDeclaration>() {
					public boolean matches(final FieldDeclaration field) {
						return
							isInstancePublic(field) &&
						    null == field.getAnnotation(FXIgnore.class)
						;
					}
				});
				visitMembersOf(type, visitors, startFromSuper);
				return result;
			}
		
			protected MethodDeclaration superMethodOf(final MethodDeclaration method) {
				final Declarations declarations = apt.getDeclarationUtils(); 
				return visitSuperMethodsOf(method, new MethodLookup(){
					public boolean visit(final MethodDeclaration other) {
						if (declarations.overrides(method, other)) {
							final Collection<Modifier> otherModifiers = other.getModifiers();
							if (otherModifiers.contains(Modifier.PUBLIC) &&
								null == other.getAnnotation(FXIgnore.class) )
								_result = other;
							return true;
						}
						return false;
					}
				});						
			}	
			
			protected MethodDeclaration findSuperSetterOf(final MethodDeclaration getter, final String propertySuffix) {
				final String setterName = "set" + propertySuffix;
				final TypeMirror propertyType = getter.getReturnType();
				return visitSuperMethodsOf(getter, new MethodLookup(){
					public boolean visit(final MethodDeclaration other) {
						if (setterName.equals(other.getSimpleName()) &&
								_types.isVoid(other.getReturnType()) ) {
							final Collection<ParameterDeclaration> params = other.getParameters();
							if (params != null && params.size() == 1 && propertyType.equals( params.iterator().next().getType() ) ) {
								final Collection<Modifier> otherModifiers = other.getModifiers();
								if (otherModifiers.contains(Modifier.PUBLIC) &&
									null == other.getAnnotation(FXIgnore.class) )
									_result = other;
								return true;
							}
						}
						return false;
					}
				});				
			}
			
			protected MethodDeclaration findSuperGetterOf(final MethodDeclaration setter, final String propertySuffix) {
				final TypeMirror propertyType = setter.getParameters().iterator().next().getType();
				final String getterName1 = "get" + propertySuffix;
				final String getterName2 = _types.isBoolean(propertyType) ? "is" + propertySuffix : "";
				return visitSuperMethodsOf(setter, new MethodLookup(){
					public boolean visit(final MethodDeclaration other) {
						final String getterName = other.getSimpleName(); 
						if ( (getterName1.equals(getterName) || getterName2.equals(getterName)) &&
							other.getReturnType().equals(propertyType) ) {
							final Collection<ParameterDeclaration> params = other.getParameters();
							if (params == null || params.size() == 0) {
								final Collection<Modifier> otherModifiers = other.getModifiers();
								if (otherModifiers.contains(Modifier.PUBLIC) && 
									null == other.getAnnotation(FXIgnore.class) 
								    )
								    _result = other;
								return true;
							}
						}
						return false;
					}
				});
			}	

			protected <T> T visitSuperMethodsOf(final MethodDeclaration method, final IMethodVisitor<T> visitor) {
				return visitSuperMethodsOf(method.getDeclaringType(), visitor);
			}
			
			protected <T> T visitSuperMethodsOf(final TypeDeclaration type, final IMethodVisitor<T> visitor) {
				return visitMethodsOf(type, visitor, true);
			}

			protected <T> T visitMethodsOf(
					final TypeDeclaration type, 
					final IMemberVisitor<T, MethodDeclaration> visitor, 
					final boolean startFromSuper) {
				return visitMembersOf(type, GET_METHODS, visitor, startFromSuper); 
			}
			
			@SuppressWarnings("unused")
			protected <T> T visitSuperFieldsOf(final MethodDeclaration method, final IFieldVisitor<T> visitor) {
				return visitSuperFieldsOf(method.getDeclaringType(), visitor);
			}
			
			protected <T> T visitSuperFieldsOf(final TypeDeclaration type, final IFieldVisitor<T> visitor) {
				return visitFieldsOf(type, visitor, true);
			}

			protected <T> T visitFieldsOf(
					final TypeDeclaration type, 
					final IMemberVisitor<T, FieldDeclaration> visitor, 
					final boolean startFromSuper) {
				return visitMembersOf(type, GET_FIELDS, visitor, startFromSuper); 
			}			

			protected <T, M extends MemberDeclaration> T visitMembersOf(
					final TypeDeclaration type,
					final IGetMemberDeclarations<M> reader,
					final IMemberVisitor<T, M> visitor, 
					final boolean startFromSuper
					) {
				final Map<IGetMemberDeclarations<M>, IMemberVisitor<T, M>> visitors = 
					Collections.singletonMap(reader, visitor);
				final Collection<T> results = visitMembersOf(type, visitors, startFromSuper);
				if (null == results || results.isEmpty())
					return null;
				else
					return results.iterator().next();
			}	
			
			protected <T, M extends MemberDeclaration> Collection<T> visitMembersOf(
					final TypeDeclaration type, 
					final Map<IGetMemberDeclarations<M>, ? extends IMemberVisitor<T, M>> visitors, 
					final boolean startFromSuper) {
				
				if (type instanceof ClassDeclaration) {
					final ClassDeclaration selfClass  = (ClassDeclaration)type;
					TypeDeclaration inheritedType = startFromSuper ? selfClass : null;
					
					ClassType superClass = selfClass.getSuperclass();
					ClassDeclaration superType;
					if (startFromSuper)
						superType = null != superClass ? superClass.getDeclaration() : null;
					else
						superType = selfClass;

					while (true) {
						if (null == superType)
							return toResults(visitors);
						

						final AS3Superclasses childSuperclasses = null == inheritedType ? null : new AS3Superclasses(inheritedType, apt);
						inheritedType = superType;
						
						if (null != childSuperclasses) {
							if ( childSuperclasses.superClassIgnored(superType) )
								return toResults(visitors);
						}
						
						for (final Map.Entry<IGetMemberDeclarations<M>, ? extends IMemberVisitor<T, M>> e : visitors.entrySet()) {
							final IGetMemberDeclarations<M> reader = e.getKey();
							final IMemberVisitor<T, M> visitor = e.getValue();
							for (final M member : reader.read(superType) ) {
								if ( visitor.visit(member) )
									return toResults(visitors);
							}
						}
						
						superClass = superType.getSuperclass();
						superType  = null != superClass ? superClass.getDeclaration() : null;
					}
				} else
					return toResults(visitors);
				
			}	
			
			private <T> Collection<T> toResults(final Map<?, ? extends IMemberVisitor<T, ?>> visitors) {
				final Collection<T> results = new ArrayList<T>();
				for (final IMemberVisitor<T, ?> visitor : visitors.values()) {
					results.add(visitor.result());
				}
				return results;
			}
		
		};
	}
	
	interface IGetMemberDeclarations<M extends MemberDeclaration> {
		abstract public <C extends TypeDeclaration> Collection<? extends M> read(final C type);
	}
	
	final static IGetMemberDeclarations<MethodDeclaration> GET_METHODS = new IGetMemberDeclarations<MethodDeclaration>() {
		public <C extends TypeDeclaration> Collection<? extends MethodDeclaration> read(final C type) {
			return type.getMethods();
		}
	};

	final static IGetMemberDeclarations<FieldDeclaration> GET_FIELDS = new IGetMemberDeclarations<FieldDeclaration>() {
		public <C extends TypeDeclaration> Collection<? extends FieldDeclaration> read(final C type) {
			return type.getFields();
		}
	};
	
	interface IMemberVisitor<T, M extends MemberDeclaration> {
		T result();
		boolean visit(M member);
	}
	
	interface IMethodVisitor<T> extends IMemberVisitor<T, MethodDeclaration> {
	}
	
	interface IFieldVisitor<T> extends IMemberVisitor<T, FieldDeclaration> {
	}
	
	abstract static class MethodLookup implements IMethodVisitor<MethodDeclaration> {
		protected MethodDeclaration _result;
		final public MethodDeclaration result() { return _result; }
	}
}
