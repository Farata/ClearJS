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

import java.util.Comparator;
import java.util.Collection;
import java.util.TreeSet;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.farata.dto2extjs.asap.types.AS3TypeReflector;

import com.sun.mirror.declaration.EnumDeclaration;
import com.sun.mirror.declaration.EnumConstantDeclaration;

import com.sun.mirror.util.SourcePosition;

public class AS3EnumDeclarationReflector extends AS3TypeDeclarationReflector {
	
	public AS3EnumDeclarationReflector(final EnumDeclaration enumDeclaration, final AS3TypeReflector typeReflector) {
		super(enumDeclaration, typeReflector);
	}
	
	protected TypeDeclarationVisitor createVisitor() {
		return new EnumDeclarationVisitor();
	}
	
	public class EnumDeclarationVisitor extends TypeDeclarationVisitor {
		
		protected TypeDeclarationKind getTypeKind() { return TypeDeclarationKind.ENUM; }
		
		@Override protected void _processTypeDeclaration() {
			final EnumDeclaration enumValues = (EnumDeclaration)source;
			final Collection<EnumConstantDeclaration> decls 
				= new TreeSet<EnumConstantDeclaration>(EnumConstantDeclarationComparator);
			decls.addAll( enumValues.getEnumConstants() );
			for (final EnumConstantDeclaration enumValue : decls ) {
				processEnumConstantDeclaration(enumValue);
			}
		}
		
		protected void processEnumConstantDeclaration(final EnumConstantDeclaration enumConst) {
			final AttributesImpl typeAttrs = new AttributesImpl();
			typeAttrs.addAttribute("", "name", "name", "NMTOKEN", enumConst.getSimpleName());
			
			final String qName = NS_DTO2extjs + ':' + "enum-entry"; 
			try {
				startElement(URI_DTO2extjs, "enum-entry", qName, typeAttrs);
				endElement(URI_DTO2extjs, "enum-entry", qName);
			} catch (final SAXException ex) {
				throw new SAXRuntimeException(ex);
			}
		}
	}
	
	final private static Comparator<EnumConstantDeclaration> EnumConstantDeclarationComparator = new Comparator<EnumConstantDeclaration>() {
		public int compare(final EnumConstantDeclaration a, final EnumConstantDeclaration b) {
			final SourcePosition sa = a.getPosition();
			final SourcePosition sb = b.getPosition();
			int delta = sa.line() - sb.line();
			if (delta == 0)
				delta = sa.column() - sb.column();
			return delta < 0 ? -1 : delta > 0 ? 1 : 0; 
		}
	};
}
