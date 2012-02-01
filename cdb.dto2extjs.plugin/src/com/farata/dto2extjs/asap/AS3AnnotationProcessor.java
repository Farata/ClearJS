/**
 * Copyright (c) 2009 Farata Systems  http://www.faratasystems.com
 *
 * Licensed under The MIT License
 * Re-distributions of files must retain the above copyright notice.
 *
 * @license http://www.opensource.org/licenses/mit-license.php The MIT License
 *
 */
package com.farata.dto2extjs.asap;

import java.io.ByteArrayInputStream;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLFilterImpl;

import com.farata.dto2extjs.annotations.FXClass;
import com.farata.dto2extjs.annotations.FXClassKind;
import com.farata.dto2extjs.asap.reflect.AS3ClassDeclarationReflector;
import com.farata.dto2extjs.asap.reflect.AS3EnumDeclarationReflector;
import com.farata.dto2extjs.asap.reflect.AS3InterfaceDeclarationReflector;
import com.farata.dto2extjs.asap.reflect.AS3TypeDeclarationReflector;
import com.farata.dto2extjs.asap.types.AS3TypeReflector;
import com.farata.dto2extjs.asap.types.InvalidJavaTypeException;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Messager;

import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.TypeDeclaration;
import com.sun.mirror.declaration.InterfaceDeclaration;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.EnumDeclaration;

public class AS3AnnotationProcessor implements AnnotationProcessor {
	
	final protected AnnotationProcessorEnvironment _environment;
	final protected AS3AnnotationProcessorOptions  _options;
	
	public AS3AnnotationProcessor(final AnnotationProcessorEnvironment environment, final AS3AnnotationProcessorOptions options) {
		_environment = environment;
		_options     = options;
	}

	public void process() {
		final Messager messager = _environment.getMessager();
		// obtain the declaration of the annotation we want to process
		final AnnotationTypeDeclaration fxClass 
			= (AnnotationTypeDeclaration)_environment.getTypeDeclaration(FXClass.class.getName());
		
		// get the annotated types
		final Collection<Declaration> annotatedTypes = _environment.getDeclarationsAnnotatedWith(fxClass);
		final Collection<TypeDeclaration> typeDeclarations = new ArrayList<TypeDeclaration>( annotatedTypes.size() );
		for (final Declaration decl : annotatedTypes) 
			if ( decl instanceof TypeDeclaration ) typeDeclarations.add( (TypeDeclaration)decl );

		final Workset workset = new Workset(_environment, typeDeclarations);
		final AS3TypeReflector _typeReflector = new AS3TypeReflector(
			_environment, workset, 
			_options.defaultClassKind(), _options.defaultEnumKind(), _options.numberAsString()
		);
	
		for (TypeDeclaration declaration = workset.next(); null != declaration; declaration = workset.next() ) {
			
			final AS3TypeDeclarationReflector reflector;
			final Templates templates;
			
			final FXClassKind fxClassKind = _typeReflector.resolveTypeOf(declaration, true); 
			if ( declaration instanceof EnumDeclaration ) {
				final EnumDeclaration eDeclaration = (EnumDeclaration)declaration;
				reflector = new AS3EnumDeclarationReflector( eDeclaration, _typeReflector );
				switch (fxClassKind) {
					case REMOTE:
						templates = AS3TemplatesCache.as3EnumFarata();
						break;
					case STRING_CONSTANTS:
						templates = AS3TemplatesCache.as3EnumAdobe();
						break;
					default:
						continue;
				}
				
			} else if (declaration instanceof InterfaceDeclaration) {
				final InterfaceDeclaration iDeclaration = (InterfaceDeclaration)declaration;
				reflector = new AS3InterfaceDeclarationReflector( iDeclaration, _typeReflector );
				templates = AS3TemplatesCache.as3Interface();
			} else if (declaration instanceof ClassDeclaration) {
				switch (fxClassKind) {
					case REMOTE:
						templates = AS3TemplatesCache.as3RemoteClass();
						break;
					case MANAGED:
						templates = AS3TemplatesCache.as3ManagedClass();
						break;
					default:
						continue;
				}
				final ClassDeclaration cDeclaration = (ClassDeclaration)declaration;
				reflector = new AS3ClassDeclarationReflector( cDeclaration, _typeReflector );
				
			} else {
				continue;
			}
			
			try {
				final Transformer serializer = templates.newTransformer();
				
				try {
					serializer.setParameter("base", _options.output().getCanonicalPath());
					serializer.setParameter("metadata-dump", _options.dumpMetadata() ? "yes" : "no");
				} catch (final Exception e) {
					
				}
				
				try {
					final Source source = new SAXSource(reflector, noInput());
					final Result result = noResult();
					serializer.transform(source, result);
				} finally {
				}
			} catch (final TransformerException ex) {
				ex.printStackTrace();				
				messager.printError( ex.getLocalizedMessage() );
			} catch (final InvalidJavaTypeException ex) {
				// Reported by AS3TypeReflector
			} catch (final Exception ex) {
				ex.printStackTrace();
				messager.printError( ex.getLocalizedMessage() );				
			}
		}
	}
	
	final private static byte[] VOID_BYTES = {};
	private static InputSource noInput() { 
		return new InputSource( new ByteArrayInputStream(VOID_BYTES) ); 
	}
	
	private static Result noResult() {
		return new SAXResult( new XMLFilterImpl() );
	}
}

