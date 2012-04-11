package com.farata.cleardatabuilder.extjs.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class CDBProjectWizard extends Wizard implements INewWizard {

	private CDBProjectWizardPageOne fFirstPage;
	private CDBProjectWizardPageTwo fSecondPage;

	@Override
	public boolean performFinish() {
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
		
	}
	
    public void addPages()
    {
       if(fFirstPage == null)
           fFirstPage = new CDBProjectWizardPageOne();
       addPage(fFirstPage);

       if(fSecondPage == null)
          fSecondPage = new CDBProjectWizardPageTwo();
       addPage(fSecondPage);
       //fFirstPage.init(getSelection(), getActivePart());
    }

}
