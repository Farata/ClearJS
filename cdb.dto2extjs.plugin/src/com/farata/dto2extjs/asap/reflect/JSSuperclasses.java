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

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;

import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.annotations.JSIgnore;
import com.farata.dto2extjs.asap.types.JSTypeReflector;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Messager;

import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;

import com.sun.mirror.type.DeclaredType;
import com.sun.mirror.type.MirroredTypeException;
import com.sun.mirror.type.MirroredTypesException;
import com.sun.mirror.util.SourcePosition;

public class JSSuperclasses {
	final private TypeDeclaration      _selfClass;
	final private Set<DeclaredType>    _allSuperclasses;
	final private Set<TypeDeclaration> _ignoredSuperclasses;

	
	private int _ignoreAnyCount  = 0;
	
	public JSSuperclasses(final TypeDeclaration selfClass, final AnnotationProcessorEnvironment apt) {
		_selfClass = selfClass;
		_allSuperclasses = new HashSet<DeclaredType>( selfClass.getSuperinterfaces() );
		
		/*
		_inheritance = new LinkedHashSet<DeclaredType>();
		collectInherited(selfClass, _inheritance, new HashSet<DeclaredType>());
		*/
		
		if (selfClass instanceof ClassDeclaration) {
			final ClassDeclaration cdecl = (ClassDeclaration)selfClass;
			_allSuperclasses.add( cdecl.getSuperclass() );
			
		}
		
		final JSClass jsClass = selfClass.getAnnotation(JSClass.class);
		final Set<TypeDeclaration> ignoredSuperclasses = new HashSet<TypeDeclaration>();
		if (null != jsClass) {
			try {
				Object superclasses = jsClass.ignoreSuperclasses();
				Class<?>[] ignoredSuperclassArray = superclasses instanceof Class ? 
					new Class[]{(Class<?>)superclasses} : (Class[])superclasses;
				if (null == ignoredSuperclassArray || ignoredSuperclassArray.length == 0) {
				}
				else  {
					for (final Class<?> cls : ignoredSuperclassArray) {
						if ( cls == JSIgnore.any.class ) {
							_ignoreAnyCount++;
						} else
							ignoredSuperclasses.add( apt.getTypeDeclaration(cls.getName()) );
					}
				}
			} catch (final MirroredTypeException ex) {
				final String ignoreAnyClassName = JSIgnore.any.class.getName().replace('$', '.');
				if ( ignoreAnyClassName.equals(ex.getQualifiedName()) ) {
					_ignoreAnyCount++;
				} else
					ignoredSuperclasses.add( apt.getTypeDeclaration(ex.getQualifiedName()) );
			} catch (final MirroredTypesException ex) {
				Collection<String> ignoredSuperclassArray = ex.getQualifiedNames();
				if (null == ignoredSuperclassArray || ignoredSuperclassArray.size() == 0) {
				}
				else  {
					final String ignoreAnyClassName = JSIgnore.any.class.getName().replace('$', '.'); 
					for (final String className : ignoredSuperclassArray) {
						if ( ignoreAnyClassName.equals(className) ) {
							_ignoreAnyCount++;
						} else
							ignoredSuperclasses.add( apt.getTypeDeclaration(className) );
					}
				}
			}
		}
		
		_ignoredSuperclasses = ignoredSuperclasses;
	}
	
	// Inheritance processing is not necessary
	/*
	final private Set<DeclaredType>    _inheritance;
	public Collection<DeclaredType> inheritance() {
		return _inheritance;
	}
	
	static void collectInherited(final TypeDeclaration current, final Set<DeclaredType> types, final Set<DeclaredType> visitedInterfaces) {
		if (current instanceof ClassDeclaration) {
			final ClassDeclaration cdecl = ClassDeclaration.class.cast(current);
			final ClassType ctype = cdecl.getSuperclass();
			if (null != ctype) {
				types.add(ctype);
				collectInherited(ctype.getDeclaration(), types, visitedInterfaces);
			}
		}
		for (final InterfaceType itype : current.getSuperinterfaces()) {
			if (visitedInterfaces.contains(itype))
				continue;
			
			types.add(itype);
			collectInherited(itype.getDeclaration(), types, visitedInterfaces);
		}
		
	}
	*/
	
	public boolean validate(final AnnotationProcessorEnvironment apt) {
		boolean valid = true;
		final Messager messager = apt.getMessager();
		final SourcePosition ignoreSuperclassesPosition = ignoreSuperclassesPosition();
		if (_ignoreAnyCount > 1) {
			valid = false;
			
			messager.printError(
				ignoreSuperclassesPosition, 
				"<IGNORE_ANY_CLASS> tag may be defined only once"
			);
		}
		if (_ignoreAnyCount >= 1 && _ignoredSuperclasses.size() > 0) {
			valid = false;
			messager.printError(
				ignoreSuperclassesPosition, 
				"<IGNORE_ANY_CLASS> may not be combined with explicit class list"
			);
		}
		final Set<TypeDeclaration> ignoredSuperclasses = new HashSet<TypeDeclaration>(_ignoredSuperclasses);
		
		for (final DeclaredType type : _allSuperclasses) {
			final TypeDeclaration declaration = type.getDeclaration();
			final boolean wasIgnored = ignoredSuperclasses.remove( declaration );
			if (wasIgnored && null != declaration.getAnnotation(JSClass.class)) {
				messager.printError(
					ignoreSuperclassesPosition, 
					declaration.getQualifiedName() + " may not be excluded, it is annotated as @JSClass"
				);
			}
		}
		
		for (final TypeDeclaration declaration : ignoredSuperclasses) {
			messager.printWarning(
				ignoreSuperclassesPosition, 
				"Class \"" + declaration.getQualifiedName() + "\" is not used as interface/superclass"
			);
		}

		return valid;
	}
	
	private SourcePosition ignoreSuperclassesPosition() {
		return JSTypeReflector.getJSClassAnnotationAttributePosition(_selfClass, "ignoreSuperclasses");
	}
	
	public boolean ignored(final TypeDeclaration declaration) {
		final JSClass jsClass = declaration.getAnnotation(JSClass.class);
		return (null == jsClass && _ignoreAnyCount > 0) || _ignoredSuperclasses.contains( declaration );
	}
	
	public boolean superInterfaceIgnored(final TypeDeclaration declaration) {
		if ( ignored(declaration) )
			return true;
		final String qname = declaration.getQualifiedName();
		return qname.startsWith("java.") || qname.startsWith("javax.");
	}
	
	public boolean superClassIgnored(final TypeDeclaration declaration) {
		if ( ignored(declaration) )
			return true;
		final String qname = declaration.getQualifiedName();
		return qname.equals("java.lang.Object");
	}
	
}