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
import java.io.File;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLFilterImpl;

import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.annotations.JSClassKind;
import com.farata.dto2extjs.asap.reflect.JSClassDeclarationReflector;
import com.farata.dto2extjs.asap.reflect.JSEnumDeclarationReflector;
import com.farata.dto2extjs.asap.reflect.JSInterfaceDeclarationReflector;
import com.farata.dto2extjs.asap.reflect.JSTypeDeclarationReflector;
import com.farata.dto2extjs.asap.types.JSTypeReflector;
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

public class JSAnnotationProcessor implements AnnotationProcessor {
	
	final protected AnnotationProcessorEnvironment _environment;
	final protected JSAnnotationProcessorOptions  _options;
	
	public JSAnnotationProcessor(final AnnotationProcessorEnvironment environment, final JSAnnotationProcessorOptions options) {
		_environment = environment;
		_options     = options;
	}

	public void process() {
		final Messager messager = _environment.getMessager();
		// obtain the declaration of the annotation we want to process
		final AnnotationTypeDeclaration jsClass 
			= (AnnotationTypeDeclaration)_environment.getTypeDeclaration(JSClass.class.getName());
		
		// get the annotated types
		final Collection<Declaration> annotatedTypes = _environment.getDeclarationsAnnotatedWith(jsClass);
		final Collection<TypeDeclaration> typeDeclarations = new ArrayList<TypeDeclaration>( annotatedTypes.size() );
		for (final Declaration decl : annotatedTypes) 
			if ( decl instanceof TypeDeclaration ) typeDeclarations.add( (TypeDeclaration)decl );

		final Workset workset = new Workset(_environment, typeDeclarations);
		final JSTypeReflector _typeReflector = new JSTypeReflector(_environment, workset, _options); 
	
		for (TypeDeclaration declaration = workset.next(); null != declaration; declaration = workset.next() ) {
			
			final JSTypeDeclarationReflector reflector;
			final Templates templates;
			
			final JSClassKind jsClassKind = _typeReflector.resolveTypeOf(declaration, true); 
			if ( declaration instanceof EnumDeclaration ) {
				final EnumDeclaration eDeclaration = (EnumDeclaration)declaration;
				reflector = new JSEnumDeclarationReflector( eDeclaration, _typeReflector );
				switch (jsClassKind) {
					case EXT_JS:
						templates = JSTemplatesCache.jsEnumObjects();
						break;
					case STRING_CONSTANTS:
						templates = JSTemplatesCache.jsEnumString();
						break;
					default:
						continue;
				}
				
			} else if (declaration instanceof InterfaceDeclaration) {
				final InterfaceDeclaration iDeclaration = (InterfaceDeclaration)declaration;
				reflector = new JSInterfaceDeclarationReflector( iDeclaration, _typeReflector );
				templates = JSTemplatesCache.jsInterface();
			} else if (declaration instanceof ClassDeclaration) {
				switch (jsClassKind) {
					case EXT_JS:
						templates = JSTemplatesCache.jsExtJSClass();
						break;
					case CLASSIC:
						templates = JSTemplatesCache.jsClassicJSClass();
						break;
					default:
						continue;
				}
				final ClassDeclaration cDeclaration = (ClassDeclaration)declaration;
				reflector = new JSClassDeclarationReflector( cDeclaration, _typeReflector );
				
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
				
				final Source source = new SAXSource(reflector, noInput());
				final Result result = noResult();
				XsltUtils.pushPackagePathResolver(_options.packagePathTransformer());
				try {
					serializer.transform(source, result);
					refresh(_options.output());
				} finally {
					XsltUtils.popPackagePathResolver();
				}
			} catch (final TransformerException ex) {
				ex.printStackTrace();				
				messager.printError( ex.getLocalizedMessage() );
			} catch (final InvalidJavaTypeException ex) {
				// Reported by JSTypeReflector
			} catch (final Exception ex) {
				ex.printStackTrace();
				messager.printError( ex.getLocalizedMessage() );				
			}
		}
	}

	private void refresh(File file) {
		try {
			IWorkspace workspace= ResourcesPlugin.getWorkspace();    
			IPath location= Path.fromOSString(file.getAbsolutePath()); 
			IFile ifile= workspace.getRoot().getFileForLocation(location);
			if (ifile != null)
				ifile.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (Throwable e) {
			e.printStackTrace();
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

