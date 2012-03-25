package com.farata.cleardatabuilder.extjs.validation.correction;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.apt.core.util.EclipseMessager;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickFixProcessor;

import com.farata.cleardatabuilder.extjs.validation.IValidationConstants;

public class CDBQuickFixProcessor implements IQuickFixProcessor,
		IValidationConstants {

	@Override
	public IJavaCompletionProposal[] getCorrections(IInvocationContext context,
			IProblemLocation[] locations) throws CoreException {
		if (locations == null || locations.length == 0)
			return null;

		locations[0].getProblemArguments();

		HashSet<String> handledProblems = new HashSet<String>(locations.length);
		ArrayList<IJavaCompletionProposal> resultingCollections = new ArrayList<IJavaCompletionProposal>();
		for (int i = 0; i < locations.length; i++) {
			IProblemLocation curr = locations[i];
			String[] args = curr.getProblemArguments();
			if (args.length<2) {
				continue;
			}
			if (PLUGIN_ID.equals(args[0])) {
				String id = args[1];
				if (handledProblems.add(id)) {
					process(id, context, curr, resultingCollections);
				}
			}
		}

		return (IJavaCompletionProposal[]) resultingCollections
				.toArray(new IJavaCompletionProposal[resultingCollections
						.size()]);
	}

	private void process(String id, IInvocationContext context, IProblemLocation problem,
			ArrayList<IJavaCompletionProposal> proposals) {
		if (ERROR_transferInfo_is_missing.equals(id)) {
			TransferInfoMissingSubProcessor.transferInfoMissingProposals(context, problem, proposals);
		} else if (ERROR_wrong_return_type.equals(id)) {
			WrongReturnTypeSubProcessor.wrongReturnTypeProposals(context, problem, proposals);
		}
	}

	@Override
	public boolean hasCorrections(ICompilationUnit icompilationunit, int i) {
		if (i == EclipseMessager.APT_QUICK_FIX_PROBLEM_ID) {
			return true;
		}
		return false;
	}
}