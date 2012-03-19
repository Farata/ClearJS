package com.farata.cleardatabuilder.js.ui;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.apt.core.util.AptConfig;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.viewsupport.FilteredElementTreeSelectionDialog;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;

public class ClearPreferences extends PreferencePage implements
		IWorkbenchPropertyPage {

	private Label label = null;
	private Text extjsLocation = null;
	private Button button = null;
	private IProject project;
	private IPath currentLocation;
	private Group group;

	@Override
	protected Control createContents(final Composite parent) {
		currentLocation = getCurrentLocation();
		
		
	    group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Location of the generated code");
		parent.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
			      Rectangle area = parent.getClientArea();
			      group.setSize(area.width, 80);
			}
		});
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = false;
		group.setLayout(gridLayout);
		
		label = new Label(group, SWT.NONE);
		label.setText("ExtJS Nodel:");

		extjsLocation = new Text(group, SWT.BORDER);
		extjsLocation.setText(pathToString(currentLocation));
		extjsLocation.setEditable(false);
		extjsLocation.setLayoutData(gridData);
		extjsLocation.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		button = new Button(group, SWT.NONE);
		button.setText("Browse...");
				
		
		button.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						IPath selectedPath = chooseLocation(group
								.getShell(), currentLocation);
						if (selectedPath != null) {
							String text = pathToString(selectedPath);
							extjsLocation.setText(text);
						}
					}
				});
		return group;
	}

	private IPath getCurrentLocation() {
		IJavaProject jproject = JavaCore.create(project);
		String sPath = AptConfig.getProcessorOptions(jproject).get(
				"-Acom.faratasystems.dto2extjs.output");
		return stringToPath(sPath);
	}

	private static IPath stringToPath(String sPath) {
		if (sPath == null) {
			return null;
		}
		Path path = new Path("/" + sPath);
		return path;
	}

	@Override
	public IAdaptable getElement() {
		return project;
	}

	@Override
	public void setElement(IAdaptable project) {
		if (project instanceof IProject) {
			this.project = (IProject) project;
		} else if (project instanceof IJavaProject) {
			this.project = ((IJavaProject) project).getProject();
		}
	}

	public static IPath chooseLocation(Shell shell, IPath initialSelection) {
		Class acceptedClasses[] = { org.eclipse.core.resources.IFolder.class };
		TypedElementSelectionValidator validator = new TypedElementSelectionValidator(
				acceptedClasses, true);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource focus = initialSelection == null ? null : root
				.findMember(initialSelection);
		FilteredElementTreeSelectionDialog dialog = new FilteredElementTreeSelectionDialog(
				shell, new WorkbenchLabelProvider(),
				new WorkbenchContentProvider());
		dialog.setHelpAvailable(false);
		dialog.setValidator(validator);
		dialog.setTitle("Select folder");
		dialog.setMessage("Folder for the generated ExtJS Model:");
		dialog.setInput(root);
		dialog.setComparator(new ResourceComparator(1));
		dialog.setInitialSelection(focus);
		dialog.setAllowMultiple(false);
		if (dialog.open() == 0) {
			Object elements[] = dialog.getResult();
			IPath res[] = new IPath[elements.length];
			for (int i = 0; i < res.length; i++) {
				IResource elem = (IResource) elements[i];
				res[i] = elem.getFullPath();
			}

			return res.length > 0 ? res[0] : null;
		} else {
			return null;
		}
	}

	public boolean performOk() {
		String location = extjsLocation.getText();
		if (location != null && location.length() > 0) {
			try {
				if (!location.equals(pathToString(getCurrentLocation()))) {
					storeExtJSModelLocation(location);
					storeExtJSStoreLocation(location);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	protected void performDefaults() {
		super.performDefaults();
		currentLocation = new Path(project.getName() + "/WebContent");
		extjsLocation.setText(pathToString(currentLocation));
	}

	private void storeExtJSModelLocation(String location) throws CoreException {
		IJavaProject jproject = JavaCore.create(project);
		AptConfig.addProcessorOption(jproject,
				"-Acom.faratasystems.dto2extjs.output", location);
	}

	private void storeExtJSStoreLocation(String location) throws IOException,
			CoreException {
		IFile propFile = project.getFolder("cdb_build").getFile(
				"build.properties");
		FileInputStream fis = new FileInputStream(propFile.getLocation()
				.toFile());
		Properties props = new Properties();
		props.load(fis);
		fis.close();
		props.setProperty("extjs-output-folder", "${project-root}/../"
				+ location);
		FileOutputStream fos = new FileOutputStream(propFile.getLocation()
				.toFile());
		props.store(fos, "");
		fos.close();
		propFile.refreshLocal(IResource.DEPTH_INFINITE, null);
	}

	private static String pathToString(IPath selectedPath) {
		if (selectedPath == null) {
			return "";
		}
		String text = selectedPath.toString();
		if (text.startsWith("/")) {
			text = text.substring(1);
		}
		return text;
	}
}