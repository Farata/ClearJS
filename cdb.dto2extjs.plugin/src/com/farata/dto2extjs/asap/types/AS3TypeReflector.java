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

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

import com.farata.dto2extjs.annotations.FXClass;
import com.farata.dto2extjs.annotations.FXClassKind;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;

import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeElementDeclaration;
import com.sun.mirror.declaration.AnnotationValue;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.EnumDeclaration;
import com.sun.mirror.declaration.InterfaceDeclaration;
import com.sun.mirror.declaration.Modifier;
import com.sun.mirror.declaration.TypeDeclaration;

import com.sun.mirror.type.ArrayType;
import com.sun.mirror.type.DeclaredType;
import com.sun.mirror.type.PrimitiveType;
import com.sun.mirror.type.ReferenceType;
import com.sun.mirror.type.TypeMirror;
import com.sun.mirror.type.VoidType;

import com.sun.mirror.util.SourcePosition;
import com.sun.mirror.util.Types;

public class AS3TypeReflector {
	
	final private AnnotationProcessorEnvironment _environment;
	final private FXClassKind                    _defaultClassKind; 
	final private FXClassKind                    _defaultEnumKind;
	final private IWorkset                       _workset;
	final private Types                          _types;
	final private boolean                        _numberAsString;
	
	public AS3TypeReflector(final AnnotationProcessorEnvironment environment, final IWorkset workset, final FXClassKind defaultClassKind, final FXClassKind defaultEnumKind, final boolean numberAsString) {
		_environment      = environment;
		_workset          = workset;
		_defaultClassKind = defaultClassKind;
		_defaultEnumKind  = defaultEnumKind;
		_types            = environment.getTypeUtils();
		_numberAsString   = numberAsString;
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
	
	public FXClassKind resolveTypeOf(final TypeDeclaration declaration) throws InvalidJavaTypeException {
		return resolveTypeOf(declaration, false);
	}
	
	public FXClassKind resolveTypeOf(final TypeDeclaration declaration, final boolean selfCheck) throws InvalidJavaTypeException {
		final FXClass fxClass = declaration.getAnnotation(FXClass.class);
		if (null == fxClass) {
			throw new InvalidJavaTypeException();
		}
		
		final FXClassKind fxClassKind = fxClass.kind(); 
		switch (fxClassKind) {
			case REMOTE:
				if (declaration instanceof EnumDeclaration) {
					if (selfCheck) {
						_environment.getMessager().printWarning(
							AS3TypeReflector.getFXClassAnnotationAttributePosition(declaration, "type"), 
							"Explicit usage of FXClassKind.REMOTE for enum is deprecated."
						);
					}
				}
				return fxClassKind;
			case STRING_CONSTANTS:
				if (!(declaration instanceof EnumDeclaration)) {
					if (selfCheck) {
						_environment.getMessager().printError(
							AS3TypeReflector.getFXClassAnnotationAttributePosition(declaration, "type"), 
							"Invalid usage of FXClassKind.STRING_CONSTANTS, string constants are allowed only for enum."
						);
						throw new InvalidJavaTypeException();
					}
				}
				return fxClassKind;
			case MANAGED:
				if (declaration instanceof EnumDeclaration) {
					if (selfCheck) {
						_environment.getMessager().printError(
							AS3TypeReflector.getFXClassAnnotationAttributePosition(declaration, "type"), 
							"Invalid usage of FXClassKind.MANAGED, only DEFAULT or STRING_CONSTANTS kinds are allowed for enum."
						);
						throw new InvalidJavaTypeException();
					}
					else
						return FXClassKind.REMOTE;
				}
				return fxClassKind;
			case DEFAULT:
			default:
				if (declaration instanceof EnumDeclaration) {
					if (_defaultEnumKind != FXClassKind.DEFAULT)
						return _defaultEnumKind;
					else
						return FXClassKind.REMOTE; 
				} else if (_defaultClassKind != FXClassKind.DEFAULT)
					return _defaultClassKind;
				else if (null != getAnnotationMirror(declaration, "javax.persistence.Entity"))
					return FXClassKind.MANAGED;
				else
					return FXClassKind.REMOTE;
		}
	}
	
	
	public static AnnotationMirror getFXClassAnnotationMirror(final TypeDeclaration type) {
		return AS3TypeReflector.getAnnotationMirror(type, FXClass.class.getName());
	}	
	
	public static AnnotationMirror getAnnotationMirror(final Declaration type, final String annotationTypeName) {
		for (final AnnotationMirror anno : type.getAnnotationMirrors() ) {
			if ( annotationTypeName.equals( anno.getAnnotationType().getDeclaration().getQualifiedName() ) )
				return anno;
		}
		return null;
	}	
	
	public static SourcePosition getFXClassAnnotationAttributePosition(final TypeDeclaration type, final String attributeName) {
		return getAnnotationAttributePosition(type, FXClass.class.getName(), attributeName);
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
	
	public IAS3Type getAS3Type(final TypeMirror type, final SourcePosition pos) 
		throws InvalidJavaTypeException {
		return getAS3Type(type, pos, false);
	}
	
	public IAS3Type getAS3Type(final TypeMirror type, final SourcePosition pos, final boolean tolerateUnknown)
		throws InvalidJavaTypeException {
		
		if ( type instanceof PrimitiveType ) {
			final PrimitiveType pType = (PrimitiveType)type;
			switch ( pType.getKind() ) {
				case BOOLEAN: 
					return AS3BuiltinType.Boolean;
				case BYTE: 
				case SHORT:
				case INT:
					return AS3BuiltinType.Int;
				case LONG:
				case FLOAT:
				case DOUBLE:
					return _numberAsString ? AS3BuiltinType.String
						: AS3BuiltinType.Number;
				case CHAR:
					return AS3BuiltinType.String;
				default:
					throw new UnsupportedOperationException("Unknown Java primitive type: " + pType.getKind());
			}
		} else if ( type instanceof VoidType ) {
			return AS3BuiltinType.Void;
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
						return AS3BuiltinType.String;
					else if ( "java.lang.Byte".equals(qName) )
						return FLASH_BYTE_ARRAY;
				}
					
				return new IAS3Type() {
					public String id() { return "Array"; }
					public FXClassKind classKind() { return null; }
					public boolean isContainer() { return true; }
					public boolean isEnum() { return false; }
					public IAS3Type contentType() { 
						return getAS3Type(componentType, pos, tolerateUnknown);
					}
				};
			} else if ( type instanceof DeclaredType ) {
				final DeclaredType dType = (DeclaredType)type;
				final TypeDeclaration cDeclaration = dType.getDeclaration(); 
				
				if (null == cDeclaration) {
					throw new InvalidJavaTypeException(missingTypeDefiniton(dType));
				}
				
				final String qName = cDeclaration.getQualifiedName();
				if ( "java.lang.Object".equals(qName) )
					return AS3BuiltinType.Object;
				if ( "java.lang.String".equals( qName ) )
					return AS3BuiltinType.String;
				else if ( "java.lang.Boolean".equals(qName) )
					return AS3BuiltinType.Boolean;
				else if ( knownNumberWrappers().contains(qName) )
					return _numberAsString ? AS3BuiltinType.String
							: AS3BuiltinType.Number;
				else if ( isSubtypeOf(dType, "java.util.Date", false) )
					return AS3BuiltinType.Date;
				else if ( isSubtypeOf(dType, "java.util.Calendar", false) )
					return AS3BuiltinType.Date;
				else if ( isSubtypeOf(dType, "java.util.Map", true))
					return AS3BuiltinType.Object;

				else if ( isSubtypeOf(dType, "java.util.Collection", true)) {
					final String as3CollectionClassName;
					if ( isSubtypeOf(dType, "java.util.List", true) )
						as3CollectionClassName = "mx.collections.ArrayCollection";
					else
						as3CollectionClassName = "mx.collections.ICollectionView";
					final Collection<TypeMirror> elementTypes = dType.getActualTypeArguments();
					// exactly one, we can't guess if more for some ad-hoc collection subclass
					if (null != elementTypes && elementTypes.size() == 1) { 
						return new AS3CustomType(
							as3CollectionClassName, 
							getAS3Type(elementTypes.iterator().next(), pos, tolerateUnknown)
						);
					} else {
						return new AS3CustomType(as3CollectionClassName);
					}
				}
		
				else if ( isSubtypeOf(dType, "java.lang.Number", false) )
					return _numberAsString ? AS3BuiltinType.String
							: AS3BuiltinType.Number;
				else if ( isSubtypeOf(dType, "org.w3c.dom.Document", true ) )
					return AS3BuiltinType.XML;
				
				final FXClass fxClass = cDeclaration.getAnnotation(FXClass.class);
				if (null == fxClass) {
					_environment.getMessager().printError(pos, unsupportedTypeError(type));					
					throw new InvalidJavaTypeException(unsupportedTypeError(type));
				}
				else {
					_workset.enlist( cDeclaration );
					if ( cDeclaration instanceof EnumDeclaration && 
						 resolveTypeOf(cDeclaration) == FXClassKind.STRING_CONSTANTS
					   )
						return AS3BuiltinType.String; 
					else
						return new AS3CustomType(cDeclaration, resolveTypeOf(cDeclaration) );
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
		final String UNSUPPORTED_TYPE="Unsupported type: '%1$s' is not annotated with @FXClass"; 
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
	
	final private static IAS3Type FLASH_BYTE_ARRAY = new AS3CustomType("flash.utils.ByteArray");
	
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
