package com.farata.cleardatabuilder.extjs.wizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jpt.jpa.core.internal.facet.JpaFacetDataModelProperties;
import org.eclipse.jst.servlet.ui.project.facet.WebProjectFirstPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;
import org.eclipse.wst.common.frameworks.internal.operations.IProjectCreationPropertiesNew;

public class CDBProjectSecondPage extends WebProjectFirstPage implements CDBFacetDataModelProperties {

	private Button springCheckbox;
	private Group persistancePlatformGroup;
	private Group extJSGroup;
	private Group sampleDBGroup;
	private Label warningLabel;
	private Composite connectionGroup;
	private Text appNameText;

	public CDBProjectSecondPage(IDataModel model, String pageName) {
		super(model, pageName);
		setShouldAddEARComposite(false);
	}

	protected String[] getValidationPropertyNames() {

		String superProperties[] = super.getValidationPropertyNames();
		List list = Arrays.asList(superProperties);
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.addAll(list);
		arrayList.add(CDB_APPLICATION_NAME);
		arrayList.add(CDB_EXTJS_FOLDER);
		arrayList.add(CDB_SAMPLEDB_FOLDER);
		arrayList.add(CDB_PERSISTANCE_PLATFORM);
		arrayList.add(JpaFacetDataModelProperties.CONNECTION_ACTIVE);
		return (String[]) arrayList.toArray(new String[0]);
	}

	protected Composite createTopLevelComposite(Composite parent) {
		final Composite top = new Composite(parent, 0);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(top,
		// getInfopopID());
		top.setLayout(new GridLayout());
		top.setLayoutData(new GridData(1808));
		createProjectGroup(top);
		Control[] children = top.getChildren();
		Composite locationGroup = (Composite) children[children.length - 1];
		excludeControl(locationGroup);
		createApplicationGroup(top);
		createExtJSGroup(top);
		createServerTargetComposite(top);
		createSampleDBGroup(top);
		createPersistancePlatformGroup(top);
		CDBJpaFacetInstallPage.getConnectionGroup(top, (CDBProjectWizard) getWizard());
		children = top.getChildren();
		connectionGroup = (Composite) children[children.length - 1];
		for (int i = 0; i < 9; i++) {
			int toRemove = connectionGroup.getChildren().length - i - 1;
			excludeControl(connectionGroup.getChildren()[toRemove]);
		}
		createSpringIntegrationGroup(top);
		createWarningLabel(top);

		IDataModelListener idatamodellistener = new IDataModelListener() {

			@Override
			public void propertyChanged(DataModelEvent datamodelevent) {
				if (CDB_PROJECT_TYPE.equals(datamodelevent.getPropertyName())) {
					switchControls(model.getStringProperty(CDB_PROJECT_TYPE), top);
				} else if (IProjectCreationPropertiesNew.PROJECT.equals(datamodelevent.getPropertyName())) {
					IProject prj = (IProject) model.getProperty(IProjectCreationPropertiesNew.PROJECT);
					appNameText.setText(prj.getName());
					model.setStringProperty(CDB_APPLICATION_NAME, prj.getName());
				}
			}
		};
		model.addListener(idatamodellistener);
		model.setProperty(CDB_PROJECT_TYPE, "new");

		return top;
	}

	private void createApplicationGroup(Composite parent) {
		Composite appGroup = new Composite(parent, 0);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		appGroup.setLayout(layout);
		appGroup.setLayoutData(new GridData(768));
		Label appNameLabel = new Label(appGroup, SWT.NONE);
		appNameLabel.setText("Application:   ");
		appNameText = new Text(appGroup, SWT.BORDER);
		appNameText.setLayoutData(new GridData(768));
		appNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				model.setProperty(CDB_APPLICATION_NAME, appNameText.getText());
			}
		});
	}

	private void createExtJSGroup(final Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_IN);
		group.setText("Ext JS Location");
		group.setLayoutData(gdhfill());
		group.setLayout(new GridLayout(3, false));

		createLocalFolderGroup(group);
		createLocalURLGroup(group);
		createCDNGroup(group);

		model.setStringProperty(CDB_EXTJS_LOCATION_TYPE, TYPE_LOCAL_FOLDER);

		extJSGroup = group;
	}

	private void createLocalFolderGroup(final Group parent) {
		final Button localFolderButton = new Button(parent, SWT.RADIO);
		localFolderButton.setText("Local Folder:");
		localFolderButton.setSelection(true);

		final Text extJSPath = new Text(parent, SWT.BORDER);
		extJSPath.setLayoutData(gdhfill());
		extJSPath.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				model.setProperty(CDB_EXTJS_FOLDER, extJSPath.getText());
			}
		});

		localFolderButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (localFolderButton.getSelection()) {
					model.setStringProperty(CDB_EXTJS_LOCATION_TYPE, TYPE_LOCAL_FOLDER);
					model.setProperty(CDB_EXTJS_FOLDER, extJSPath.getText());
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		final Button button = new Button(parent, SWT.NONE);
		button.setText("   Browse...   ");
		button.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(parent.getShell());
				if (!"".equals(extJSPath.getText())) {
					String initialDir = extJSPath.getText();
					dialog.setFilterPath(initialDir);
				}
				String result = dialog.open();
				if (result != null) {
					extJSPath.setText(result.toString());
					model.setProperty(CDB_EXTJS_FOLDER, extJSPath.getText());
				}
			}
		});

		model.addListener(new IDataModelListener() {

			@Override
			public void propertyChanged(DataModelEvent e) {
				if (e.getPropertyName().equals(CDB_EXTJS_LOCATION_TYPE)) {
					String type = model.getStringProperty(CDB_EXTJS_LOCATION_TYPE);
					boolean enabled = type.equals(TYPE_LOCAL_FOLDER);
					extJSPath.setEnabled(enabled);
					button.setEnabled(enabled);
				}
			}
		});
	}

	private void createLocalURLGroup(final Group parent) {
		final Button localFolderButton = new Button(parent, SWT.RADIO);
		localFolderButton.setText("Local URL:");

		final Text extJSPath = new Text(parent, SWT.BORDER);
		extJSPath.setLayoutData(gdhfill());
		extJSPath.setText("/extjs");
		extJSPath.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				model.setProperty(CDB_EXTJS_FOLDER, extJSPath.getText());
			}
		});

		localFolderButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (localFolderButton.getSelection()) {
					model.setStringProperty(CDB_EXTJS_LOCATION_TYPE, TYPE_LOCAL_URL);
					model.setProperty(CDB_EXTJS_FOLDER, extJSPath.getText());
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		Label l = new Label(parent, SWT.NONE);

		model.addListener(new IDataModelListener() {

			@Override
			public void propertyChanged(DataModelEvent e) {
				if (e.getPropertyName().equals(CDB_EXTJS_LOCATION_TYPE)) {
					String type = model.getStringProperty(CDB_EXTJS_LOCATION_TYPE);
					boolean enabled = type.equals(TYPE_LOCAL_URL);
					extJSPath.setEnabled(enabled);
				}
			}
		});
	}

	private void createCDNGroup(final Group parent) {
		final Button localFolderButton = new Button(parent, SWT.RADIO);
		localFolderButton.setText("CDN:");

		final Text extJSPath = new Text(parent, SWT.BORDER);
		extJSPath.setLayoutData(gdhfill());
		extJSPath.setText("http://cdn.sencha.io/ext-4.1.0-gpl");
		extJSPath.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				model.setProperty(CDB_EXTJS_FOLDER, extJSPath.getText());
			}
		});

		localFolderButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (localFolderButton.getSelection()) {
					model.setStringProperty(CDB_EXTJS_LOCATION_TYPE, TYPE_CDN);
					model.setProperty(CDB_EXTJS_FOLDER, extJSPath.getText());
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		Label l = new Label(parent, SWT.NONE);

		model.addListener(new IDataModelListener() {

			@Override
			public void propertyChanged(DataModelEvent e) {
				if (e.getPropertyName().equals(CDB_EXTJS_LOCATION_TYPE)) {
					String type = model.getStringProperty(CDB_EXTJS_LOCATION_TYPE);
					boolean enabled = type.equals(TYPE_CDN);
					extJSPath.setEnabled(enabled);
				}
			}
		});
	}

	private void createPersistancePlatformGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_IN);
		group.setText("Persistance Platform");
		group.setLayoutData(gdhfill());
		group.setLayout(new GridLayout(3, false));

		final Button myBatis = new Button(group, SWT.RADIO);
		myBatis.setText("MyBatis");
		myBatis.setSelection(true);
		final Button hibernate = new Button(group, SWT.RADIO);
		hibernate.setText("Hibernate");
		final Button none = new Button(group, SWT.RADIO);
		none.setText("None");
		model.setStringProperty(CDB_PERSISTANCE_PLATFORM, "myBatis");
		SelectionListener listener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				if (event.widget == myBatis) {
					model.setProperty(CDB_PERSISTANCE_PLATFORM, "myBatis");
				} else if (event.widget == hibernate) {
					model.setProperty(CDB_PERSISTANCE_PLATFORM, "hibernate");
				} else if (event.widget == none) {
					model.setProperty(CDB_PERSISTANCE_PLATFORM, "none");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		};
		myBatis.addSelectionListener(listener);
		hibernate.addSelectionListener(listener);
		none.addSelectionListener(listener);

		persistancePlatformGroup = group;
	}

	private void createSampleDBGroup(final Composite parent) {
		Group group = new Group(parent, SWT.NO_RADIO_GROUP);
		group.setText("Folder to install sample database - cleardb");
		group.setLayoutData(gdhfill());
		group.setLayout(new GridLayout(2, false));

		String home = System.getProperty("user.home");
		String os = System.getProperty("os.name").toLowerCase();
		String defaultDBFolder = "";
		if (os.indexOf("win") >= 0) {
			defaultDBFolder = home + "\\Application Data\\ClearDataBuilder";
		} else if (os.indexOf("mac") >= 0) {
			defaultDBFolder = home + "/Library/Application Support/ClearDataBuilder";
		}
		;

		final Text sampleDB = new Text(group, SWT.BORDER);
		sampleDB.setText(defaultDBFolder);
		model.setStringProperty(CDB_SAMPLEDB_FOLDER, defaultDBFolder);

		sampleDB.setLayoutData(gdhfill());
		sampleDB.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				model.setProperty(CDB_SAMPLEDB_FOLDER, sampleDB.getText());
			}
		});
		Button sampleDBbtn = new Button(group, SWT.NONE);
		sampleDBbtn.setText("   Browse...   ");
		sampleDBbtn.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(parent.getShell());
				if (!"".equals(sampleDB.getText())) {
					String initialDir = sampleDB.getText();
					dialog.setFilterPath(initialDir);
				}
				String result = dialog.open();
				if (result != null) {
					sampleDB.setText(result.toString());
					model.setProperty(CDB_SAMPLEDB_FOLDER, sampleDB.getText());
				}
			}
		});
		sampleDBGroup = group;
	}

	private void createSpringIntegrationGroup(Composite parent) {
		springCheckbox = new Button(parent, SWT.CHECK);
		springCheckbox.setLayoutData(new GridData());
		springCheckbox.setText("Spring Integration");
		springCheckbox.setSelection(true);
		springCheckbox.setEnabled(false);
		model.setBooleanProperty(CDB_SPRING_INTEGRATION, true);
		springCheckbox.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				model.setBooleanProperty(CDB_SPRING_INTEGRATION, springCheckbox.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		
		model.addListener(new IDataModelListener() {

			@Override
			public void propertyChanged(DataModelEvent e) {
				if (e.getPropertyName().equals(CDB_PERSISTANCE_PLATFORM)) {
					String platform = model.getStringProperty(CDB_PERSISTANCE_PLATFORM);
					boolean enabled = platform.equals("hibernate") || platform.equals("none");
					springCheckbox.setEnabled(enabled);
					if (!enabled) {
						springCheckbox.setSelection(true);
						model.setBooleanProperty(CDB_SPRING_INTEGRATION, true);
					}
				}
			}
		});
	}

	private void createWarningLabel(Composite parent) {
		warningLabel = new Label(parent, SWT.WRAP);
		warningLabel.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		warningLabel.setText(WARNING_TEXT);
	}

	private void switchControls(String property, Composite top) {
		boolean isNew = "new".equals(property);
		boolean isHibernateExample = "hibernateExample".equals(property);
		boolean isJavaExample = "javaExample".equals(property);
		boolean isMyBatisExample = "myBatisExample".equals(property);

		excludeControl(sampleDBGroup, !(isHibernateExample || isMyBatisExample));
		excludeControl(warningLabel, !(isHibernateExample || isMyBatisExample));
		excludeControl(persistancePlatformGroup, !isNew);
		excludeControl(connectionGroup, !isNew);
		excludeControl(springCheckbox, !isNew);

		if (isNew) {
			setTitle("New CDB Project");
			setDescription("New CDB Project");
		}

		if (isHibernateExample || isMyBatisExample) {
			setTitle("Example CDB Project (with persistance)");
			setDescription("Example CDB Project (with persistance)");
		}

		if (isJavaExample) {
			setTitle("Example CDB Project (w/out persistance)");
			setDescription("Example CDB Project (w/out persistance)");
		}

		top.layout();
		Display.getCurrent().update();
	}

	private void excludeControl(Control c, boolean exclude) {
		if (exclude) {
			excludeControl(c);
		} else {
			includeControl(c);
		}
	}

	private void excludeControl(Control c) {
		Object ld = c.getLayoutData();
		if (ld instanceof GridData) {
			GridData gld = (GridData) ld;
			gld.exclude = true;
		}
		c.setVisible(false);
	}

	private void includeControl(Control c) {
		Object ld = c.getLayoutData();
		if (ld instanceof GridData) {
			GridData gld = (GridData) ld;
			gld.exclude = false;
		}
		c.setVisible(true);
	}
}