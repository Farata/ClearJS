package com.farata.cleardatabuilder.extjs.wizard;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
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
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;
import org.eclipse.wst.common.frameworks.internal.operations.IProjectCreationPropertiesNew;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerPort;

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
		setPageComplete(false);
	}

	@Override
	public void storeDefaultSettings() {
		super.storeDefaultSettings();
		IDialogSettings settings = getDialogSettings();
		String locationType = model.getStringProperty(CDB_EXTJS_LOCATION_TYPE);
		String extjsFolder = model.getStringProperty(CDB_EXTJS_FOLDER);
		String extjsPath = model.getStringProperty(CDB_EXTJS_URL);
		String extjsCDN = model.getStringProperty(CDB_EXTJS_CDN);
		String platform = model.getStringProperty(CDB_PERSISTANCE_PLATFORM);
		String connection = model.getStringProperty(CONNECTION);
		String springIntegration = String.valueOf(model.getBooleanProperty(CDB_SPRING_INTEGRATION));
		settings.put(CDB_EXTJS_LOCATION_TYPE, locationType);
		settings.put(CDB_EXTJS_FOLDER, extjsFolder);
		settings.put(CDB_EXTJS_URL, extjsPath);
		settings.put(CDB_EXTJS_CDN, extjsCDN);
		settings.put(CDB_PERSISTANCE_PLATFORM, platform);
		settings.put(CONNECTION, connection);
		settings.put(CDB_SPRING_INTEGRATION, springIntegration);
	}

	@Override
	public void restoreDefaultSettings() {
		super.restoreDefaultSettings();
		IDialogSettings settings = getDialogSettings();
		String locationType = settings.get(CDB_EXTJS_LOCATION_TYPE);
		String extjsFolder = settings.get(CDB_EXTJS_FOLDER);
		String extjsPath = settings.get(CDB_EXTJS_URL);
		String extjsCDN = settings.get(CDB_EXTJS_CDN);
		String platform = settings.get(CDB_PERSISTANCE_PLATFORM);
		String connection = settings.get(CONNECTION);
		String springIntegration = settings.get(CDB_SPRING_INTEGRATION);
		model.setProperty(CDB_EXTJS_LOCATION_TYPE, isEmpty(locationType) ? TYPE_LOCAL_FOLDER : locationType);
		model.setProperty(CDB_EXTJS_FOLDER, extjsFolder);
		model.setProperty(CDB_EXTJS_URL, isEmpty(extjsPath) ? "/extjs" : extjsPath);
		model.setProperty(CDB_EXTJS_CDN, isEmpty(extjsCDN) ? "http://cdn.sencha.io/ext-4.1.1a-gpl" : extjsCDN);
		model.setProperty(CDB_PERSISTANCE_PLATFORM, isEmpty(platform) ? "myBatis" : platform);
		try {
			model.setProperty(CONNECTION, connection);
		} catch (Exception e) {
		}
		model.setProperty(CDB_SPRING_INTEGRATION, isEmpty(springIntegration) ? true : Boolean.parseBoolean(springIntegration));
	}

	private boolean isEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}

	@Override
	protected String[] getValidationPropertyNames() {
		String superProperties[] = super.getValidationPropertyNames();
		List list = Arrays.asList(superProperties);
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.addAll(list);
		arrayList.add(CDB_APPLICATION_NAME);
		arrayList.add(CDB_EXTJS_FOLDER);
		arrayList.add(CDB_EXTJS_CDN);
		arrayList.add(CDB_EXTJS_URL);
		arrayList.add(CDB_EXTJS_LOCATION_TYPE);
		arrayList.add(CDB_SAMPLEDB_FOLDER);
		arrayList.add(CDB_PERSISTANCE_PLATFORM);
		arrayList.add(JpaFacetDataModelProperties.CONNECTION_ACTIVE);
		return (String[]) arrayList.toArray(new String[0]);
	}

	@Override
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
					localFolderButton.setSelection(enabled);
				} else if (e.getPropertyName().equals(CDB_EXTJS_FOLDER)) {
					String s = model.getStringProperty(CDB_EXTJS_FOLDER);
					if (isEmpty(s) || !s.equals(extJSPath.getText())) {
						extJSPath.setText(s);
					}
				}
			}
		});
	}

	private void createLocalURLGroup(final Group parent) {
		final Button localFolderButton = new Button(parent, SWT.RADIO);
		localFolderButton.setText("Local URL:");

		final Text extJSPath = new Text(parent, SWT.BORDER);
		extJSPath.setLayoutData(gdhfill());
		model.setProperty(CDB_EXTJS_URL, "/extjs");
		extJSPath.setText("/extjs");
		extJSPath.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				model.setProperty(CDB_EXTJS_URL, extJSPath.getText());
			}
		});

		localFolderButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (localFolderButton.getSelection()) {
					model.setStringProperty(CDB_EXTJS_LOCATION_TYPE, TYPE_LOCAL_URL);
					model.setProperty(CDB_EXTJS_URL, extJSPath.getText());
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		final Button button = new Button(parent, SWT.NONE);
		button.setText("   Validate      ");
		button.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				String sUrl = extJSPath.getText();
				if (!CDBPingJob.isExternalURL(sUrl)) {
					sUrl = sUrl.startsWith("/") ? sUrl : "/" + sUrl;
					IRuntime runtime = (IRuntime) model.getProperty(IFacetProjectCreationDataModelProperties.FACET_RUNTIME);
					sUrl = "http://localhost:" + suggestHttpPort(runtime.getName()) + sUrl;
				}
				new CDBPingJob(getShell(), sUrl, false).schedule();
			}
		});

		model.addListener(new IDataModelListener() {

			@Override
			public void propertyChanged(DataModelEvent e) {
				if (e.getPropertyName().equals(CDB_EXTJS_LOCATION_TYPE)) {
					String type = model.getStringProperty(CDB_EXTJS_LOCATION_TYPE);
					boolean enabled = type.equals(TYPE_LOCAL_URL);
					extJSPath.setEnabled(enabled);
					button.setEnabled(enabled);
					localFolderButton.setSelection(enabled);
				} else if (e.getPropertyName().equals(CDB_EXTJS_URL)) {
					String type = model.getStringProperty(CDB_EXTJS_LOCATION_TYPE);
					boolean enabled = type.equals(TYPE_LOCAL_URL);
					button.setEnabled(enabled);

					String s = model.getStringProperty(CDB_EXTJS_URL);
					if (isEmpty(s) || !s.equals(extJSPath.getText())) {
						extJSPath.setText(s);
					}
				}
			}
		});
	}

	private void createCDNGroup(final Group parent) {
		final Button localFolderButton = new Button(parent, SWT.RADIO);
		localFolderButton.setText("CDN:");

		final Text extJSPath = new Text(parent, SWT.BORDER);
		extJSPath.setLayoutData(gdhfill());
		extJSPath.setText("http://cdn.sencha.io/ext-4.1.1a-gpl");
		model.setProperty(CDB_EXTJS_CDN, extJSPath.getText());
		extJSPath.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				model.setProperty(CDB_EXTJS_CDN, extJSPath.getText());
			}
		});

		localFolderButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (localFolderButton.getSelection()) {
					model.setStringProperty(CDB_EXTJS_LOCATION_TYPE, TYPE_CDN);
					model.setProperty(CDB_EXTJS_CDN, extJSPath.getText());
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		final Button button = new Button(parent, SWT.NONE);
		button.setText("   Validate      ");
		button.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				new CDBPingJob(getShell(), extJSPath.getText(), false).schedule();
			}
		});

		model.addListener(new IDataModelListener() {

			@Override
			public void propertyChanged(DataModelEvent e) {
				if (e.getPropertyName().equals(CDB_EXTJS_LOCATION_TYPE)) {
					String type = model.getStringProperty(CDB_EXTJS_LOCATION_TYPE);
					boolean enabled = type.equals(TYPE_CDN);
					extJSPath.setEnabled(enabled);
					button.setEnabled(enabled);
					localFolderButton.setSelection(enabled);
				} else if (e.getPropertyName().equals(CDB_EXTJS_CDN)) {
					String s = model.getStringProperty(CDB_EXTJS_CDN);
					if (isEmpty(s) || !s.equals(extJSPath.getText())) {
						extJSPath.setText(s);
					}
				}
			}
		});
	}

	private void createPersistancePlatformGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_IN);
		group.setText("Persistence Platform");
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

		model.addListener(new IDataModelListener() {

			@Override
			public void propertyChanged(DataModelEvent e) {
				if (e.getPropertyName().equals(CDB_PERSISTANCE_PLATFORM)) {
					String platform = model.getStringProperty(CDB_PERSISTANCE_PLATFORM);
					myBatis.setSelection("myBatis".equals(platform));
					hibernate.setSelection("hibernate".equals(platform));
					none.setSelection("none".equals(platform));
				}
			}
		});

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
				} else if (e.getPropertyName().equals(CDB_SPRING_INTEGRATION)) {
					springCheckbox.setSelection(model.getBooleanProperty(CDB_SPRING_INTEGRATION));
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
			setTitle("Example CDB Project (with persistence)");
			setDescription("Example CDB Project (with persistence)");
		}

		if (isJavaExample) {
			setTitle("Example CDB Project (w/out persistence)");
			setDescription("Example CDB Project (w/out persistence)");
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

	private ServerPort findHttpPort(IServer server, IProgressMonitor monitor) {
		final ServerPort[] serverPorts = server.getServerPorts(monitor);
		ServerPort serverPort = null;

		for (int i = 0; i < serverPorts.length; i++) {
			if ("HTTP".equalsIgnoreCase(serverPorts[i].getProtocol())) {
				serverPort = serverPorts[i];
			}
		}

		return serverPort;
	}

	public int suggestHttpPort(String runtimeId) {
		try {
			final ServerPort httpPort = findHttpPort(findServer(runtimeId), null);
			if (httpPort != null) {
				return httpPort.getPort();
			} else {
				return 8080;
			}
		} catch (Throwable e) {
			return 8080;
		}
	}

	private static IServer findServer(final String runtimeId) {
		final IServer[] servers = ServerCore.getServers();
		for (int i = 0; i < servers.length; i++) {
			final String serverRuntimeName = servers[i].getRuntime().getName();
			final String serverRuntimeId = servers[i].getRuntime().getId();
			if (serverRuntimeId.equals(runtimeId) || serverRuntimeName.equals(runtimeId)) {
				return servers[i];
			}
		}
		return null;
	}
}