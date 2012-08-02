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

import java.util.Collection;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.WeakHashMap;
import java.util.Arrays;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.AnnotationProcessorListener;
import com.sun.mirror.apt.AnnotationProcessors;
import com.sun.mirror.apt.RoundCompleteEvent;
import com.sun.mirror.apt.RoundCompleteListener;
import com.sun.mirror.apt.RoundState;

import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;

import com.farata.dto2extjs.annotations.JSClass;
import com.farata.dto2extjs.asap.env.AptEnvironmentInspector;
import com.farata.dto2extjs.asap.env.IEnvironmentInspector;

public class JSAnnotationProcessorFactory implements AnnotationProcessorFactory {
	
	public Collection<String> supportedOptions() {
		return JSAnnotationProcessorOptions.SUPPORTED_OPTIONS;
	}

	public Collection<String> supportedAnnotationTypes() {
		return SUPPORTED_ANNOTATIONS;
	}

	final private AnnotationProcessorListener _listener = new RoundCompleteListener() {
		public void roundComplete(final RoundCompleteEvent e) {
			final RoundState state = e.getRoundState();
			/*
			System.out.println("EVENT:" +
					" errors = "      + state.errorRaised() +
					", newClasses = " + state.classFilesCreated() +
					", newSources = " + state.sourceFilesCreated() +
					", final = "      + state.finalRound()
			);
			*/
			boolean anyRoundComplete = true;
			if ( anyRoundComplete || state.finalRound() ) {
				final AnnotationProcessorEnvironment env = e.getSource();
				final Set<TypeDeclaration> visited = _envs.remove( env );
				if (null != visited) 
					visited.clear();
				env.removeListener(this);
			}
		}
	};
	
	final private Map<AnnotationProcessorEnvironment, Set<TypeDeclaration>> _envs = new WeakHashMap<AnnotationProcessorEnvironment, Set<TypeDeclaration>>();
	
	public AnnotationProcessor getProcessorFor(final Set<AnnotationTypeDeclaration> atds, final AnnotationProcessorEnvironment env) {
		final JSAnnotationProcessorOptions options = new JSAnnotationProcessorOptions(env, INSPECTOR);
		if ( !options.parse() )
			return AnnotationProcessors.NO_OP;
		
		if ( INSPECTOR.isReconciliation(env) ) {
			/*
			System.out.println("RECONCILE: " + env.getSpecifiedTypeDeclarations());
			*/
			if ( !options.reconcile() )
				return AnnotationProcessors.NO_OP;
		}
		
		final Collection<TypeDeclaration> targets = env.getSpecifiedTypeDeclarations();		
		Set<TypeDeclaration> visited = _envs.get(env);
		final boolean firstCall = null == visited;
		
		if (firstCall) {
			/*
			System.out.println(System.identityHashCode(env) + " -- ROUND START");			
			*/
			env.addListener(_listener);
		}
		
		if ( firstCall || !visited.containsAll(targets) ) {
			/*
			System.out.println(System.identityHashCode(env) + ": NEW TARGET: " + targets + ", " + visited);
			*/
			if (firstCall)
				_envs.put(env, visited = new HashSet<TypeDeclaration>(targets));
			else
				visited.addAll( targets );
			
			return new JSAnnotationProcessor(env, options, INSPECTOR);
			
		} else {
			/*
			System.out.println("IGNORED DUPLICATE: " + env.getSpecifiedTypeDeclarations());
			*/
			return AnnotationProcessors.NO_OP;
		}
	}
	
	static IEnvironmentInspector INSPECTOR = AptEnvironmentInspector.INSTANCE;

	final private static Collection<String> SUPPORTED_ANNOTATIONS 
		= Arrays.asList( JSClass.class.getName() );

}