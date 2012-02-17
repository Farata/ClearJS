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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.annotations.JSIgnore;
import com.farata.dto2extjs.annotations.JSManyToOne;
import com.farata.dto2extjs.annotations.JSOneToMany;
import com.farata.dto2extjs.asap.types.JSTypeReflector;
import com.farata.dto2extjs.asap.types.IJSType;
import com.farata.dto2extjs.asap.types.InvalidJavaTypeException;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.MemberDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.Modifier;
import com.sun.mirror.declaration.TypeDeclaration;

import com.sun.mirror.type.DeclaredType;
import com.sun.mirror.type.EnumType;
import com.sun.mirror.type.InterfaceType;
import com.sun.mirror.util.SimpleDeclarationVisitor;

abstract public class JSTypeDeclarationReflector extends XMLFilterImpl {
	
	final protected TypeDeclaration  _declaration;
	final protected JSTypeReflector _types;
	
	public JSTypeDeclarationReflector(final TypeDeclaration declaration, final JSTypeReflector types) {
		_declaration = declaration;
		_types       = types;
	}
	
	abstract protected TypeDeclarationVisitor createVisitor();
	
	@Override public void setFeature(final String name, final boolean value) {}
	@Override public void parse(final InputSource ignore) throws SAXException {
		startDocument();
		try {
			_processor.init( _declaration, _types.environment() );
			_declaration.accept(new SimpleDeclarationVisitor() {
				@Override public void visitTypeDeclaration(final TypeDeclaration type) {
					_processor.processTypeDeclaration(type);
				}
			});
		} catch (final SAXRuntimeException ex) {
			throw ex.saxException();
		}
		endDocument();
	}
	
	protected static class SAXRuntimeException extends RuntimeException {
		SAXRuntimeException(final SAXException cause) {
			super(cause);
		}
		
		SAXException saxException() { return (SAXException)getCause(); }
		
		final private static long serialVersionUID = 1L;
	}
	
	final private TypeDeclarationVisitor _processor = createVisitor(); 
	
	abstract public class TypeDeclarationVisitor {
		
		final protected Map<String, IJSPropertyDefinition> 
			_properties = new HashMap<String, IJSPropertyDefinition>();
		
		final protected Set<MemberDeclaration> 
			_propertyMembers = new HashSet<MemberDeclaration>();
		
		final protected Set<MemberDeclaration> 
			_propertyDeclaringMembers = new HashSet<MemberDeclaration>();

		protected AnnotationProcessorEnvironment apt;
		
		protected TypeDeclaration source;
		protected JSSuperclasses superclasses;
		protected JSKeysBuilder  keysBuilder; 
		
		final void init(final TypeDeclaration source, final AnnotationProcessorEnvironment apt) {
			this.source  = source;
			this.apt     = apt;
			superclasses = new JSSuperclasses(source, apt);
			keysBuilder  = new JSKeysBuilder(apt);
			preprocess();
		}
		
		abstract protected TypeDeclarationKind getTypeKind();
		
		protected void preprocess() {}
		
		protected void processTypeDeclaration(final TypeDeclaration type) {			
			try {
				if ( !superclasses.validate(apt) )
					return;
				
				final AttributesImpl attrs = new AttributesImpl();
				attrs.addAttribute("", "javaClass", "javaClass", "NMTOKEN", 
					type.getQualifiedName() );
				
				final Collection<Modifier> modifiers = type.getModifiers();
				if ( modifiers.contains(Modifier.ABSTRACT) )
					attrs.addAttribute("", "abstract", "abstract", "NMTOKEN", "true");
				if ( modifiers.contains(Modifier.FINAL) )
					attrs.addAttribute("", "final", "final", "NMTOKEN", "true");
				
				
				final IJSType jsType = _types.getJSType(type);
				
				attrs.addAttribute("", "name", "name", "NMTOKEN", 
					jsType.id() );
				attrs.addAttribute("", "kind", "kind", "NMTOKEN", 
					jsType.classKind().name().toLowerCase() );
				
				final TypeDeclarationKind declarationKind = getTypeKind();
				final String typeQName = NS_DTO2JS + ':' + declarationKind.id();
				startElement(URI_DTO2JS, declarationKind.id(), typeQName, attrs);
				
				//processInheritance();
				processSuperinterfaces();
				_processTypeDeclaration();
				
				for (final MethodDeclaration method : type.getMethods() )
					processMethodDeclaration(method);
				
				keysBuilder.commit();

				if (null != keysBuilder.syntheticKey()) {
					final char[] syntheticKey = keysBuilder.syntheticKey().toCharArray();
					final String syntheticKeyQName = NS_DTO2JS + ':' + "synthetic-key"; 
					startElement(URI_DTO2JS, "synthetic-key", syntheticKeyQName, NO_ATTRS);
					characters(syntheticKey, 0, syntheticKey.length);
					endElement(URI_DTO2JS, "synthetic-key", syntheticKeyQName);
				}
				
				if (null != keysBuilder.semanticKey()) {
					final String semanticKeyQName = NS_DTO2JS + ':' + "semantic-key"; 
					final String semanticKeyPartQName = NS_DTO2JS + ':' + "semantic-key-part";
					startElement(URI_DTO2JS, "semantic-key", semanticKeyQName, NO_ATTRS);
					for (final String partName : keysBuilder.semanticKey()) {
						final char[] partChars = partName.toCharArray();
						startElement(URI_DTO2JS, "semantic-key-part", semanticKeyPartQName, NO_ATTRS);
						characters(partChars, 0, partChars.length);
						endElement(URI_DTO2JS, "semantic-key-part", semanticKeyPartQName);
					}
					endElement(URI_DTO2JS, "semantic-key", semanticKeyQName);
				}
				
				endElement(URI_DTO2JS, declarationKind.id(), typeQName);
				
			} catch (final SAXException ex) {
				throw new SAXRuntimeException(ex);
			}
		}
		
		protected void _processTypeDeclaration() {}
		
		/*
		protected void processInheritance() throws SAXException {
			final JSClass jsClass = source.getAnnotation( JSClass.class );
			if (null == jsClass)
				return;
			
			try {
				final String qname = NS_DTO2extjs + ':' + "inheritance";
				startElement(URI_DTO2extjs, "inheritance", qname, NO_ATTRS);
				for (final DeclaredType type : superclasses.inheritance() ) {
					processSuperInterfaceOrClass( type );
				}
				endElement(URI_DTO2extjs, "inheritance", qname);
			} catch (final SAXException ex) {
				throw new SAXRuntimeException(ex);
			}
		}
		*/
		
		protected void processSuperinterfaces() throws SAXException {
			final JSClass jsClass = source.getAnnotation( JSClass.class );
			if (null == jsClass)
				return;
			
			try {
				final String qname = NS_DTO2JS + ':' + "interfaces";
				startElement(URI_DTO2JS, "interfaces", qname, NO_ATTRS);
				for (final InterfaceType intf : source.getSuperinterfaces() ) {
					processSuperInterfaceOrClass( intf );
				}
				endElement(URI_DTO2JS, "interfaces", qname);
			} catch (final SAXException ex) {
				throw new SAXRuntimeException(ex);
			}
		}
		
		protected void processSuperInterfaceOrClass(final DeclaredType baseDeclaredType) {
			processSuperInterfaceOrClass(baseDeclaredType, null);
		}
		
		protected void processSuperInterfaceOrClass(final DeclaredType baseDeclaredType, final String suggestedElementType) {
			final TypeDeclaration decl = baseDeclaredType.getDeclaration();
			
			if ( superclasses.superInterfaceIgnored(decl) )
				return;
			
			final IJSType type;
			try {
				type = _types.getJSType(baseDeclaredType, source.getPosition());
			} catch (final InvalidJavaTypeException ex) {
				return;
			}
			
			final AttributesImpl attrs = new AttributesImpl();
			attrs.addAttribute("", "name", "name", "NMTOKEN", 
				type.id() );
			attrs.addAttribute("", "javaClass", "javaClass", "NMTOKEN", 
				decl.getQualifiedName() );
			attrs.addAttribute("", "kind", "kind", "NMTOKEN", 
				type.classKind().name().toLowerCase());
			
			if (_types.isAbstract(baseDeclaredType.getDeclaration()))
				attrs.addAttribute("", "abstract", "abstract", "NMTOKEN", "true");
			
			final String elementType;
			if (null != suggestedElementType)
				elementType = suggestedElementType;
			else if (decl instanceof InterfaceType)
				elementType = "interface";
			else if (decl instanceof EnumType)
				elementType = "enum";
			else
				elementType = "class";
			
			final String elementTypeQName = NS_DTO2JS + ':' + elementType;
			try {
				startElement(URI_DTO2JS, elementType, elementTypeQName, attrs);
				_processSuperInterfaceOrClass(baseDeclaredType);
				endElement(URI_DTO2JS, elementType, elementTypeQName);
			} catch (final SAXException ex) {
				throw new SAXRuntimeException(ex);
			}
		}
		
		protected void _processSuperInterfaceOrClass(final DeclaredType intf) {}		
		
		protected void processMethodDeclaration(final MethodDeclaration method) {
			try {
				final JSIgnore ignore = method.getAnnotation( JSIgnore.class );
				if (null != ignore)
					return;
				
				final Collection<Modifier> modifiers = method.getModifiers();
				if ( !modifiers.contains(Modifier.PUBLIC) || modifiers.contains(Modifier.STATIC) )
					return;
				
				if (_propertyMembers.contains(method)) {
					final String propertyName = propertyNameByMethod(method);
					final IJSPropertyDefinition propertyDefinition =
						null != propertyName ? _properties.get( propertyName ) : null;
				
					if (null != propertyDefinition)
						declareJSProperty(propertyDefinition);
					else {
						// Nothing
					}
				} else {
					// Action definition
					final AttributesImpl actionAttrs = new AttributesImpl();
					if ( modifiers.contains(Modifier.ABSTRACT))
						actionAttrs.addAttribute("", "abstract", "abstract", "NMTOKEN", "true" );
					if ( modifiers.contains(Modifier.FINAL))
						actionAttrs.addAttribute("", "final", "final", "NMTOKEN", "true" );
					
					actionAttrs.addAttribute("", "name", "name", "NMTOKEN", method.getSimpleName());
					final String actionQName = NS_DTO2JS + ':' + "action";
					startElement(URI_DTO2JS, "action", actionQName, actionAttrs);
					_processActionDeclaration(method);
					endElement(URI_DTO2JS, "action", actionQName);					
				}
				
			} catch (final SAXException ex) {
				throw new SAXRuntimeException(ex);
			}
		}
		
		protected String propertyNameByMethod(final MemberDeclaration method) {
			final String name = method.getSimpleName();
			boolean declareProperty = false;
			int prefixLength = 3;
			if ( name.startsWith("get") && name.length() > 3 ) {
				declareProperty = true;
			} else if ( name.startsWith("is") && name.length() > 2 ) {
				declareProperty = true;
				prefixLength = 2;
			} else if ( _propertyDeclaringMembers.contains(method) && name.startsWith("set") && name.length() > 3 ) {
				declareProperty = true;
				prefixLength = 3;
			} else
				declareProperty = false;
			
			if (declareProperty) {
				final String propertyName = Character.toLowerCase( name.charAt(prefixLength) ) + name.substring(prefixLength + 1);
				return propertyName;
			} else
				return null;
		}
		
		protected void _processPropertyDeclaration(	final String propertyName,
				final Declaration origin) throws SAXException {
			
			JSOneToMany oneToManyAnnotation = origin.getAnnotation(JSOneToMany.class);
			if (oneToManyAnnotation != null) {
				final AttributesImpl propertyAttrs = new AttributesImpl();
				final String propertyQName = NS_DTO2JS + ':' + "OneToMany";
				
				final String storeType = oneToManyAnnotation.storeType();
				propertyAttrs.addAttribute("", "storeType", "storeType", "NMTOKEN",  storeType);

				final boolean autoLoad = oneToManyAnnotation.autoLoad();
				propertyAttrs.addAttribute("", "autoLoad", "autoLoad", "NMTOKEN",  autoLoad?"true":"false");

				final String getter = oneToManyAnnotation.getter();
				propertyAttrs.addAttribute("", "getter", "getter", "NMTOKEN",  getter);


				
				final String primaryKey = oneToManyAnnotation.primaryKey();
				propertyAttrs.addAttribute("", "primaryKey", "primaryKey", "NMTOKEN", primaryKey);

				final String foreignKey = oneToManyAnnotation.foreignKey();
				propertyAttrs.addAttribute("", "foreignKey", "foreignKey", "NMTOKEN", foreignKey);

				final JSOneToMany.SyncType sync = oneToManyAnnotation.sync();
				propertyAttrs.addAttribute("", "sync", "sync", "NMTOKEN", sync.toString());
				
				final int ranking = oneToManyAnnotation.ranking();
				propertyAttrs.addAttribute("", "ranking", "ranking", "NMTOKEN", ""+ranking);
				
				startElement(URI_DTO2JS, "OneToMany", propertyQName, propertyAttrs);
				endElement(URI_DTO2JS, "OneToMany", propertyQName);
			}
			
			JSManyToOne manyToOneAnnotation = origin.getAnnotation(JSManyToOne.class);
			if (manyToOneAnnotation != null) {
				final AttributesImpl propertyAttrs = new AttributesImpl();
				final String propertyQName = NS_DTO2JS + ':' + "ManyToOne";
				/*
				IJSType parentType;
				try {
					final Class<?> parentClass = manyToOneAnnotation.parent();
					parentType = _types.getJSType(parentClass);
				} catch (final MirroredTypeException ex) {
					parentType = _types.getJSType(ex.getTypeMirror(), origin.getPosition());
				}
				if (parentType != null) {
					propertyAttrs.addAttribute("", "parent", "parent", "NMTOKEN", parentType.id());
				}
				*/
				final String primaryKey = manyToOneAnnotation.primaryKey();
				propertyAttrs.addAttribute("", "primaryKey", "primaryKey", "NMTOKEN", primaryKey);

				final String foreignKey = manyToOneAnnotation.foreignKey();
				propertyAttrs.addAttribute("", "foreignKey", "foreignKey", "NMTOKEN", foreignKey);
				
				startElement(URI_DTO2JS, "ManyToOne", propertyQName, propertyAttrs);
				endElement(URI_DTO2JS, "ManyToOne", propertyQName);
			}
		}
		
		protected void _processActionDeclaration(final MethodDeclaration method) throws SAXException {}
		
		protected void declareJSProperty(final IJSPropertyDefinition property) throws SAXException {
			keysBuilder.enlist(property);

			if (
				property.declareGetter() == JSMethodDeclarationKind.SKIP &&
				property.declareSetter() == JSMethodDeclarationKind.SKIP
				) {
				return;
			}
			
			final IJSType type;
			try {
				type = _types.getJSType(property.type(), property.origin().getPosition());
			} catch (final InvalidJavaTypeException ex) {
				return;
			}			
			final AttributesImpl propertyAttrs = new AttributesImpl();
			propertyAttrs.addAttribute("", "name", "name", "NMTOKEN", property.name());
			propertyAttrs.addAttribute("", "label", "label", "NMTOKEN", property.label());
			propertyAttrs.addAttribute("", "resource", "resource", "NMTOKEN", property.resource());
			propertyAttrs.addAttribute("", "formatString", "formatString", "NMTOKEN", property.formatString());
			propertyAttrs.addAttribute("", "type", "type", "NMTOKEN", type.id() );
			if ( type.isContainer() )
				propertyAttrs.addAttribute("", "contentType", "contentType", "NMTOKEN", type.contentType().id() );
			if ( type.isEnum() )
				propertyAttrs.addAttribute("", "enum", "enum", "NMTOKEN", "true" );
			if ( property.isAbstract() )
				propertyAttrs.addAttribute("", "abstract", "abstract", "NMTOKEN", "true" );

			switch (property.declareGetter()) {
				case OVERRIDE:
					propertyAttrs.addAttribute("", "override-getter", "override-get", "NMTOKEN", "true" );
				case DECLARE:
					propertyAttrs.addAttribute("", "declare-getter", "readable", "NMTOKEN", "true" );
			}
			
			switch (property.declareSetter()) {
				case OVERRIDE:
					propertyAttrs.addAttribute("", "override-setter", "override-set", "NMTOKEN", "true" );
				case DECLARE:
					propertyAttrs.addAttribute("", "declare-setter", "writeable", "NMTOKEN", "true" );
			}
				
			
			/*
			if ( modifiers.contains(Modifier.FINAL))
				propertyAttrs.addAttribute("", "final", "final", "NMTOKEN", "true" );
			*/
			
			final String propertyQName = NS_DTO2JS + ':' + "property";
			startElement(URI_DTO2JS, "property", propertyQName, propertyAttrs);
			_processPropertyDeclaration(property.name(), property.origin());
			endElement(URI_DTO2JS, "property", propertyQName);
		}
	};
	
	final protected static String     NS_DTO2JS  = "dto2extjs";
	final protected static String     URI_DTO2JS = "http://dto2extjs.faratasystems.com/";	
	final protected static Attributes NO_ATTRS    = new AttributesImpl();
}
