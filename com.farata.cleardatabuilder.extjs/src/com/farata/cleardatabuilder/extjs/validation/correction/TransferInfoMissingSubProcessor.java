package com.farata.cleardatabuilder.extjs.validation.correction;

import java.util.Collection;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.internal.ui.text.correction.LocalCorrectionsSubProcessor;
import org.eclipse.jdt.internal.ui.text.correction.ProblemLocation;
import org.eclipse.jdt.internal.ui.text.correction.proposals.MissingAnnotationAttributesProposal;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;

import com.farata.cleardatabuilder.extjs.validation.correction.proposals.AddTransferInfoProposal;

public class TransferInfoMissingSubProcessor {
	public static void transferInfoMissingProposals(IInvocationContext context, IProblemLocation problem, Collection<IJavaCompletionProposal> proposals) {
		addValueForAnnotationProposals(context, problem, proposals);
		//LocalCorrectionsSubProcessor.addValueForAnnotationProposals(context, problem, proposals);
	}
	public static void addValueForAnnotationProposals(IInvocationContext context, IProblemLocation problem, Collection proposals)
    {
        ICompilationUnit cu = context.getCompilationUnit();
        ASTNode selectedNode = problem.getCoveringNode(context.getASTRoot());
        if (!(selectedNode instanceof Annotation)) {
        	selectedNode = selectedNode.getParent();
        }
        if(selectedNode instanceof Annotation)
        {
            Annotation annotation = (Annotation)selectedNode;
            
            if(annotation.resolveTypeBinding() == null)
                return;
            AddTransferInfoProposal proposal = new AddTransferInfoProposal(cu, annotation, 10);
            proposals.add(proposal);
            
            MethodDeclaration method = (MethodDeclaration) annotation.getParent();
            Type returnType = method.getReturnType2();
           
            ProblemLocation problem2 = new ProblemLocation(returnType.getStartPosition(), returnType.getLength(), 16777748, problem.getProblemArguments(), true, problem.getMarkerType());
            
            LocalCorrectionsSubProcessor.addTypePrametersToRawTypeReference(context, problem2, proposals);
        }
    }
}
