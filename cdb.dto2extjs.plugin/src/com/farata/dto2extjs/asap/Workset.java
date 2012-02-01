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
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import com.farata.dto2extjs.asap.types.IWorkset;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.TypeDeclaration;

class Workset implements IWorkset {
	final private SortedSet<TypeDeclaration> _types;
	final private SortedSet<TypeDeclaration> _processed = new TreeSet<TypeDeclaration>(TypeDeclarationComparator);
	
	final private AnnotationProcessorEnvironment _env;
	
	Workset(final AnnotationProcessorEnvironment env, Collection<? extends TypeDeclaration> initial) {
		_env   = env;
		_types = new TreeSet<TypeDeclaration>(TypeDeclarationComparator);
		_types.addAll(initial);
	}
	
	TypeDeclaration next() {
		if ( _types.size() > 0) {
			final TypeDeclaration decl = _types.first();
			_processed.add( decl );
			_types.remove(decl);
			return decl;
		}
		return null;
	}
	
	public boolean enlist(final String qualifiedTypeName) {
		return enlist( _env.getTypeDeclaration(qualifiedTypeName) );
	}
	
	public boolean enlist(final TypeDeclaration decl) {
		return false;
		/*
		if ( _processed.contains(decl) )
			return false;
		else {
			return _types.add(decl);
		}
		*/
	}
	
	final private static Comparator<TypeDeclaration> TypeDeclarationComparator = new Comparator<TypeDeclaration>() {
		public int compare(final TypeDeclaration a, final TypeDeclaration b) {
			return a.getQualifiedName().compareTo( b.getQualifiedName() );
		}
	};
}
