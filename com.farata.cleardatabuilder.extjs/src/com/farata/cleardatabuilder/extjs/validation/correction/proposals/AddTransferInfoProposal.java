package com.farata.cleardatabuilder.extjs.validation.correction.proposals;

import java.util.*;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.text.correction.CorrectionMessages;
import org.eclipse.jdt.internal.ui.text.correction.proposals.MissingAnnotationAttributesProposal;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.jface.text.contentassist.ICompletionProposal ;

// Referenced classes of package org.eclipse.jdt.internal.ui.text.correction.proposals:
//            LinkedCorrectionProposal

public class AddTransferInfoProposal extends
		MissingAnnotationAttributesProposal {

	public AddTransferInfoProposal(ICompilationUnit cu, Annotation annotation,
			int relevance) {
		super(cu, annotation, relevance);
		fAnnotation = annotation;
	}

	protected ASTRewrite getRewrite() throws CoreException {
		AST ast = fAnnotation.getAST();
		ASTRewrite rewrite = ASTRewrite.create(ast);
		createImportRewrite((CompilationUnit) fAnnotation.getRoot());
		ListRewrite listRewrite;
		if (fAnnotation instanceof NormalAnnotation) {
			listRewrite = rewrite.getListRewrite(fAnnotation,
					NormalAnnotation.VALUES_PROPERTY);
		} else {
			NormalAnnotation newAnnotation = ast.newNormalAnnotation();
			newAnnotation.setTypeName((Name) rewrite
					.createMoveTarget(fAnnotation.getTypeName()));
			rewrite.replace(fAnnotation, newAnnotation, null);
			listRewrite = rewrite.getListRewrite(newAnnotation,
					NormalAnnotation.VALUES_PROPERTY);
		}
		addMissingAtributes(fAnnotation.resolveTypeBinding(), listRewrite);
		return rewrite;
	}

	private void addMissingAtributes(ITypeBinding binding,
			ListRewrite listRewriter) {
		Set implementedAttribs = new HashSet();
		if (fAnnotation instanceof NormalAnnotation) {
			List list = ((NormalAnnotation) fAnnotation).values();
			for (int i = 0; i < list.size(); i++) {
				MemberValuePair curr = (MemberValuePair) list.get(i);
				implementedAttribs.add(curr.getName().getIdentifier());
			}

		} else if (fAnnotation instanceof SingleMemberAnnotation) {
			implementedAttribs.add("value");
		}
		ASTRewrite rewriter = listRewriter.getASTRewrite();
		AST ast = rewriter.getAST();
		IMethodBinding declaredMethods[] = binding.getDeclaredMethods();
		for (int i = 0; i < declaredMethods.length; i++) {
			IMethodBinding curr = declaredMethods[i];
			if (!implementedAttribs.contains(curr.getName()) && curr.getName().equals("transferInfo")) {
				MemberValuePair pair = ast.newMemberValuePair();
				pair.setName(ast.newSimpleName(curr.getName()));
				pair.setValue(newDefaultExpression(ast, curr.getReturnType()));
				listRewriter.insertLast(pair, null);
				addLinkedPosition(rewriter.track(pair.getName()), false,
						"val_name_" + i);
				addLinkedPosition(rewriter.track(pair.getValue()), false,
						"val_type_" + i);
			}
		}

	}

	private Expression newDefaultExpression(AST ast, ITypeBinding type) {
		if (type.isPrimitive()) {
			String name = type.getName();
			if ("boolean".equals(name))
				return ast.newBooleanLiteral(false);
			else
				return ast.newNumberLiteral("0");
		}
		if (type == ast.resolveWellKnownType("java.lang.String"))
			return ast.newStringLiteral();
		if (type.isArray()) {
			ArrayInitializer initializer = ast.newArrayInitializer();
			initializer.expressions().add(
					newDefaultExpression(ast, type.getElementType()));
			return initializer;
		}
		if (type.isAnnotation()) {
			MarkerAnnotation annotation = ast.newMarkerAnnotation();
			annotation.setTypeName(ast.newName(getImportRewrite().addImport(
					type)));
			return annotation;
		} else {
			return ast.newNullLiteral();
		}
	}

	private Annotation fAnnotation;
}
