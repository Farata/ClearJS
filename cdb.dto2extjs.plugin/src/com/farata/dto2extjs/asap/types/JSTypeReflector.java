/**
 * Copyright (c) 2009 Farata Systems  http://www.faratasystems.com
 *
 * Licensed under The MIT License
 * Re-distributions of files must retain the above copyright notice.
 *
 * @license http://www.opensource.org/licenses/mit-license.php The MIT License
 *
 */
package com.farata.dto2extjs.asap.types;

import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.annotations.JSClassKind;
import com.farata.dto2extjs.asap.INameTransformer;
import com.farata.dto2extjs.asap.JSAnnotationProcessorOptions;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.*;
import com.sun.mirror.type.*;
import com.sun.mirror.util.SourcePosition;
import com.sun.mirror.util.Types;

import java.util.*;

public class JSTypeReflector {
	
	final private AnnotationProcessorEnvironment _environment;
	final private JSClassKind                    _defaultClassKind; 
	final private JSClassKind                    _defaultEnumKind;
	final private IWorkset                       _workset;
	final private Types                          _types;
	final private boolean                        _numberAsString;
	final private INameTransformer          _classNameTransformer;                        
	public JSTypeReflector(final AnnotationProcessorEnvironment environment, final IWorkset workset, final JSAnnotationProcessorOptions options) {
		_environment          = environment;
		_workset              = workset;
		_defaultClassKind     = options.defaultClassKind();
		_defaultEnumKind      = options.defaultEnumKind();
		_types                = environment.getTypeUtils();
		_numberAsString       = options.numberAsString();
		_classNameTransformer = options.classNameTransformer();
	}
	
	public AnnotationProcessorEnvironment environment() { return _environment; }
	
	public boolean isVoid(final TypeMirror type) {
		return type instanceof VoidType;
	}
	
	public boolean isBoolean(final TypeMirror type) {
		return type instanceof PrimitiveType && ((PrimitiveType)type).getKind() == PrimitiveType.Kind.BOOLEAN;
	}
	
	public boolean isAbstract(final Declaration declaration) {
		return declaration instanceof InterfaceDeclaration || declaration.getModifiers().contains( Modifier.ABSTRACT );
	}
	
	public JSClassKind resolveTypeOf(final TypeDeclaration declaration) throws InvalidJavaTypeException {
		return resolveTypeOf(declaration, false);
	}
	
	public JSClassKind resolveTypeOf(final TypeDeclaration declaration, final boolean selfCheck) throws InvalidJavaTypeException {
		final JSClass jsClass = declaration.getAnnotation(JSClass.class);
		if (null == jsClass) {
			throw new InvalidJavaTypeException();
		}
		
		final JSClassKind jsClassKind = jsClass.kind(); 
		switch (jsClassKind) {
			case EXT_JS:
				if (declaration instanceof EnumDeclaration) {
					if (selfCheck) {
						_environment.getMessager().printWarning(
							JSTypeReflector.getJSClassAnnotationAttributePosition(declaration, "type"), 
							"Explicit usage of JSClassKind.EXT_JS for enum is deprecated."
						);
					}
				}
				return jsClassKind;
			case STRING_CONSTANTS:
				if (!(declaration instanceof EnumDeclaration)) {
					if (selfCheck) {
						_environment.getMessager().printError(
							JSTypeReflector.getJSClassAnnotationAttributePosition(declaration, "type"), 
							"Invalid usage of JSClassKind.STRING_CONSTANTS, string constants are allowed only for enum."
						);
						throw new InvalidJavaTypeException();
					}
				}
				return jsClassKind;
			case CLASSIC:
				if (declaration instanceof EnumDeclaration) {
					if (selfCheck) {
						_environment.getMessager().printError(
							JSTypeReflector.getJSClassAnnotationAttributePosition(declaration, "type"), 
							"Invalid usage of JSClassKind.CLASSIC, only DEFAULT or STRING_CONSTANTS kinds are allowed for enum."
						);
						throw new InvalidJavaTypeException();
					}
					else
						return JSClassKind.EXT_JS;
				}
				return jsClassKind;
			case DEFAULT:
			default:
				if (declaration instanceof EnumDeclaration) {
					if (_defaultEnumKind != JSClassKind.DEFAULT)
						return _defaultEnumKind;
					else
						return JSClassKind.EXT_JS; 
				} else if (_defaultClassKind != JSClassKind.DEFAULT)
					return _defaultClassKind;
				else if (null != getAnnotationMirror(declaration, "javax.persistence.Entity"))
					// ExtJS in any case
					return JSClassKind.EXT_JS;
				else
					return JSClassKind.EXT_JS;
		}
	}
	
	public static AnnotationMirror getJSClassAnnotationMirror(final TypeDeclaration type) {
		return JSTypeReflector.getAnnotationMirror(type, JSClass.class.getName());
	}	
	
	public static AnnotationMirror getAnnotationMirror(final Declaration type, final String annotationTypeName) {
		for (final AnnotationMirror anno : type.getAnnotationMirrors() ) {
			if ( annotationTypeName.equals( anno.getAnnotationType().getDeclaration().getQualifiedName() ) )
				return anno;
		}
		return null;
	}	
	
	public static SourcePosition getJSClassAnnotationAttributePosition(final TypeDeclaration type, final String attributeName) {
		return getAnnotationAttributePosition(type, JSClass.class.getName(), attributeName);
	}
	
	public static SourcePosition getAnnotationAttributePosition(final TypeDeclaration type, final String annotationTypeName, final String attributeName) {
		final AnnotationMirror annotationMirror = getAnnotationMirror(type, annotationTypeName);
		if (null == annotationMirror)
			return null;
		final Map<AnnotationTypeElementDeclaration, AnnotationValue> attributes = annotationMirror.getElementValues();
		for (final Map.Entry<AnnotationTypeElementDeclaration, AnnotationValue> e : attributes.entrySet()) {
			if (attributeName.equals(e.getKey().getSimpleName()) ) {
				final SourcePosition result = e.getValue().getPosition();
				return null == result ? annotationMirror.getPosition() : result;
			}
		}
		return annotationMirror.getPosition();
	}
	
	public IJSType getJSType(final Class<?> type) {
		System.out.println(type);
		final TypeDeclaration t= _environment.getTypeDeclaration(type.getName()); 
		return getJSType(t);
	}
	
	public IJSType getJSType(final TypeDeclaration type) {
		return getJSType(_types.getDeclaredType(type), type.getPosition());
	}
	
	public IJSType getJSType(final TypeMirror type, final SourcePosition pos) 
		throws InvalidJavaTypeException {
		return getJSType(type, pos, false);
	}
	
	public IJSType getJSType(final TypeMirror type, final SourcePosition pos, final boolean tolerateUnknown)
		throws InvalidJavaTypeException {
		
		if ( type instanceof PrimitiveType ) {
			final PrimitiveType pType = (PrimitiveType)type;
			switch ( pType.getKind() ) {
				case BOOLEAN: 
					return JSBuiltinType.BOOLEAN;
				case BYTE: 
				case SHORT:
				case INT:
					return JSBuiltinType.INTEGER;
				case FLOAT:
					return JSBuiltinType.FLOAT;
				case LONG:
					return JSBuiltinType.NUMBER;
				case DOUBLE:
					return _numberAsString ? JSBuiltinType.STRING : JSBuiltinType.NUMBER;
				case CHAR:
					return JSBuiltinType.STRING;
				default:
					throw new UnsupportedOperationException("Unknown Java primitive type: " + pType.getKind());
			}
		} else if ( type instanceof VoidType ) {
			return JSBuiltinType.AUTO;
		} else if ( type instanceof ReferenceType ) {
			if ( type instanceof ArrayType ) {
				final ArrayType aType = (ArrayType)type;
				final TypeMirror componentType = aType.getComponentType();
				if ( componentType instanceof PrimitiveType ) {
					final PrimitiveType pComponentType = (PrimitiveType)componentType;
					if ( pComponentType.getKind() == PrimitiveType.Kind.BYTE )
						return FLASH_BYTE_ARRAY;
				} else if ( componentType instanceof DeclaredType ) {
					final DeclaredType dComponentType = (DeclaredType)componentType;
					final TypeDeclaration tdComponentType = dComponentType.getDeclaration();
					
					if (null == tdComponentType) {
						// throw error for behavior coherent with non-array types;
						throw new InvalidJavaTypeException(missingTypeDefiniton(dComponentType));
					}
					
					
					final String qName = tdComponentType.getQualifiedName();
					if ( "java.lang.Character".equals(qName) )
						return JSBuiltinType.STRING;
					else if ( "java.lang.Byte".equals(qName) )
						return FLASH_BYTE_ARRAY;
				}
					
				return JSBuiltinType.AUTO
				/*new IJSType() {
					public String id() { return "Array"; }
					public JSClassKind classKind() { return null; }
					public boolean isContainer() { return true; }
					public boolean isEnum() { return false; }
					public IJSType contentType() { 
						return getJSType(componentType, pos, tolerateUnknown);
					}
				}*/;
			} else if ( type instanceof DeclaredType ) {
				final DeclaredType dType = (DeclaredType)type;
				final TypeDeclaration cDeclaration = dType.getDeclaration(); 
				
				if (null == cDeclaration) {
					throw new InvalidJavaTypeException(missingTypeDefiniton(dType));
				}
				
				final String qName = cDeclaration.getQualifiedName();
				if ( "java.lang.Object".equals(qName) )
					return JSBuiltinType.AUTO;
				if ( "java.lang.String".equals( qName ) )
					return JSBuiltinType.STRING;
				else if ( "java.lang.Boolean".equals(qName) )
					return JSBuiltinType.BOOLEAN;
				else if ( knownNumberWrappers().contains(qName) )
					return _numberAsString ? JSBuiltinType.STRING
							: JSBuiltinType.NUMBER;
				else if ( isSubtypeOf(dType, "java.util.Date", false) )
					return JSBuiltinType.DATE;
				else if ( isSubtypeOf(dType, "java.util.Calendar", false) )
					return JSBuiltinType.DATE;
				else if ( isSubtypeOf(dType, "java.util.Map", true))
					return JSBuiltinType.AUTO;

				else if ( isSubtypeOf(dType, "java.util.Collection", true)) {
					final String jsCollectionClassName;
					if ( isSubtypeOf(dType, "java.util.List", true) )
						jsCollectionClassName = "mx.collections.ArrayCollection";
					else
						jsCollectionClassName = "mx.collections.ICollectionView";
					final Collection<TypeMirror> elementTypes = dType.getActualTypeArguments();
					// exactly one, we can't guess if more for some ad-hoc collection subclass
					if (null != elementTypes && elementTypes.size() == 1) { 
						return new JSCustomType(
							jsCollectionClassName, 
							getJSType(elementTypes.iterator().next(), pos, tolerateUnknown)
						);
					} else {
						return new JSCustomType(jsCollectionClassName);
					}
				}
		
				else if ( isSubtypeOf(dType, "java.lang.Number", false) )
					return _numberAsString ? JSBuiltinType.STRING
							: JSBuiltinType.NUMBER;
				else if ( isSubtypeOf(dType, "org.w3c.dom.Document", true ) )
					return JSBuiltinType.STRING;
				
				final JSClass jsClass = cDeclaration.getAnnotation(JSClass.class);
				if (null == jsClass) {
					_environment.getMessager().printError(pos, unsupportedTypeError(type));					
					throw new InvalidJavaTypeException(unsupportedTypeError(type));
				}
				else {
					_workset.enlist( cDeclaration );
					if ( cDeclaration instanceof EnumDeclaration && 
						 resolveTypeOf(cDeclaration) == JSClassKind.STRING_CONSTANTS
					   ) {
						return JSBuiltinType.STRING; 
					} else {
						return new JSCustomType(cDeclaration, _classNameTransformer, resolveTypeOf(cDeclaration) );
					}
				}
			}
		}
		if (tolerateUnknown)
			return null;
		else {
			_environment.getMessager().printError(pos, unsupportedTypeError(type));
			throw new InvalidJavaTypeException(unsupportedTypeError(type));
		}
	}
	
	private String missingTypeDefiniton(DeclaredType type) {
		final String MISSING_TYPE = "Can't find class: '%1$s'";
		return String.format(MISSING_TYPE, type);
	}
	
	private String unsupportedTypeError(TypeMirror type) {
		final String UNSUPPORTED_TYPE="Unsupported type: '%1$s' is not annotated with @JSClass"; 
		return String.format(UNSUPPORTED_TYPE, type);
		
	}
	
	public boolean isSubtypeOf(final DeclaredType type, String targetType, boolean checkInterfaces) {
		if (null == type)
			return false;
		
		final TypeMirror otherType = _types.getDeclaredType( 
			_environment.getTypeDeclaration( targetType ) 
		);
		
		return _types.isSubtype(type, otherType);
	}
	
	public Set<String> knownNumberWrappers() {
		return KNOWN_NUMBER_WRAPPERS;
	}
	
	final private static IJSType FLASH_BYTE_ARRAY = new JSCustomType("flash.utils.ByteArray");
	
	final private static Set<String> KNOWN_NUMBER_WRAPPERS = new HashSet<String>(
		Arrays.asList(
			"java.lang.Byte",
			"java.lang.Short",
			"java.lang.Integer",
			"java.lang.Long",
			"java.lang.Float",
			"java.lang.Double"
		)
	);
	
}
