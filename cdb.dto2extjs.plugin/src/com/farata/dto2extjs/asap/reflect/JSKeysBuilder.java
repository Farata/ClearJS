package com.farata.dto2extjs.asap.reflect;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import com.farata.dto2extjs.annotations.JSKeyColumn;
import com.farata.dto2extjs.asap.types.JSTypeReflector;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.Declaration;

public class JSKeysBuilder {
	private String syntheticKey = null;
	private Collection<String> semanticKey = null;	
	private List<SemanticKeyPart> semanticKeyParts = null;
	
	protected AnnotationMirror firstSyntheticKeyDecl = null;
	
	final protected AnnotationProcessorEnvironment apt;
	
	public JSKeysBuilder(final AnnotationProcessorEnvironment apt) {
		this.apt = apt;
	}

	public String syntheticKey() { return syntheticKey; }
	public Collection<String> semanticKey() { return semanticKey; }
	
	public void enlist(final IJSPropertyDefinition property) {
		final Declaration member = property.origin();
		final String propertyName = property.name();
		
	
		final AnnotationMirror jsSemanticKeyPart = JSTypeReflector.
			getAnnotationMirror(member, JSKeyColumn.class.getName());
		
		if (null != jsSemanticKeyPart) {
			if (null == semanticKeyParts)
				semanticKeyParts = new ArrayList<SemanticKeyPart>();
			
			semanticKeyParts.add(
				new SemanticKeyPart(
					propertyName,
					member.getAnnotation(JSKeyColumn.class).part(),
					jsSemanticKeyPart.getPosition().line(), 
					jsSemanticKeyPart.getPosition().column()
				)
			);
		}
	}
	
	public void commit() {
		if (null == semanticKeyParts)
			semanticKey = null;
		else {
			Collections.sort(semanticKeyParts);
			semanticKey = new ArrayList<String>(semanticKeyParts.size());
			for (final SemanticKeyPart p : semanticKeyParts)
				semanticKey.add(p.name);
		}
	}

	protected static class SemanticKeyPart implements Comparable<SemanticKeyPart>{
		final public String name;
		final public int order;
		final public int line;
		final public int column;
		
		public SemanticKeyPart(final String name, final int order, final int line, final int column) {
			this.name = name; this.order = order; this.line = line; this.column = column;
		}
		
		public int compareTo(final SemanticKeyPart other) {
			int delta;
			
			delta = order - other.order;
			if (0 != delta) return delta;
			
			delta = line - other.line;
			if (0 != delta) return delta;
			
			delta = column - other.column;
			if (0 != delta) return delta;

			return 0;
		}
	}
}
