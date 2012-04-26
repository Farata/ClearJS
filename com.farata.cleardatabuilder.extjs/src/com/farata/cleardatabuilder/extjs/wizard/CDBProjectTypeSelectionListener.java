package com.farata.cleardatabuilder.extjs.wizard;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class CDBProjectTypeSelectionListener implements SelectionListener, CDBFacetDataModelProperties {

	private CDBProjectFirstPage cdbProjectFirstPage;
	private IDataModel model;

	public CDBProjectTypeSelectionListener(CDBProjectFirstPage cdbProjectFirstPage, IDataModel model) {
		this.cdbProjectFirstPage = cdbProjectFirstPage;
		this.model = model;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void widgetSelected(SelectionEvent arg0) {
		if (cdbProjectFirstPage.newProject.getSelection()) {
			model.setProperty(CDB_PROJECT_TYPE, "new");
		} else if (cdbProjectFirstPage.hibernateExampleProject.getSelection()) {
			model.setProperty(CDB_PROJECT_TYPE, "hibernateExample");
		} else if (cdbProjectFirstPage.javaExampleProject.getSelection()) {
			model.setProperty(CDB_PROJECT_TYPE, "javaExample");
		} else if (cdbProjectFirstPage.myBatisExampleProjct.getSelection()) {
			model.setProperty(CDB_PROJECT_TYPE, "myBatisExample");
		}
	}

}
