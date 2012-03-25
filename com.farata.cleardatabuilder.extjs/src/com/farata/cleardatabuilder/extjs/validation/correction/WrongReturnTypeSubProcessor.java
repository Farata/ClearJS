package com.farata.cleardatabuilder.extjs.validation.correction;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.text.correction.ASTResolving;
import org.eclipse.jdt.internal.ui.text.correction.CorrectionMessages;
import org.eclipse.jdt.internal.ui.text.correction.proposals.LinkedCorrectionProposal;
import org.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;

public class WrongReturnTypeSubProcessor {

	public static void wrongReturnTypeProposals(IInvocationContext context,
			IProblemLocation problem,
			Collection<IJavaCompletionProposal> proposals) {
		try {
			addTypeMismatchProposal(context, problem, proposals, "List");
			addTypeMismatchProposal(context, problem, proposals, "Collection");
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public static void addTypeMismatchProposal(IInvocationContext context,
			IProblemLocation problem, Collection proposals, String simpleType)
			throws CoreException {
		String args[] = problem.getProblemArguments();
		if (args.length != 2)
			return;
		org.eclipse.jdt.core.ICompilationUnit cu = context.getCompilationUnit();
		CompilationUnit astRoot = context.getASTRoot();
		AST ast = astRoot.getAST();
		ASTNode selectedNode = problem.getCoveredNode(astRoot);
		BodyDeclaration decl = ASTResolving
				.findParentBodyDeclaration(selectedNode);
		if (decl instanceof MethodDeclaration) {
			MethodDeclaration methodDeclaration = (MethodDeclaration) decl;
			ASTRewrite rewrite = ASTRewrite.create(ast);
			String label = Messages
					.format(
							CorrectionMessages.TypeMismatchSubProcessor_changereturntype_description,
							BasicElementLabels.getJavaElementName("java.util."
									+ simpleType));
			org.eclipse.swt.graphics.Image image = JavaPluginImages
					.get("org.eclipse.jdt.ui.correction_change.gif");
			LinkedCorrectionProposal proposal = new LinkedCorrectionProposal(
					label, cu, rewrite, 6, image);
			ImportRewrite imports = proposal.createImportRewrite(astRoot);
			String newReturnType = imports.addImport("java.util." + simpleType);
			rewrite.replace(methodDeclaration.getReturnType2(), rewrite
					.createStringPlaceholder(simpleType + "<?>", 43), null);
			proposals.add(proposal);
		}

	}

}
