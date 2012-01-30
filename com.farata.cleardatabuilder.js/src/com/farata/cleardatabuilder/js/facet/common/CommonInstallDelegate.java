package com.farata.cleardatabuilder.js.facet.common;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.events.IProjectFacetActionEvent;
import org.eclipse.wst.jsdt.web.core.internal.project.JsWebNature;
import org.eclipse.jpt.jpa.core.internal.facet.JpaFacetActionDelegate;
public class CommonInstallDelegate implements IDelegate {

	@Override
	public void execute(final IProject project, IProjectFacetVersion projectFacetVersion, Object context,
			final IProgressMonitor monitor) throws CoreException {
		final IFacetedProjectListener listener = new IFacetedProjectListener() {
			
			@Override
			public void handleEvent(IFacetedProjectEvent event) {
				IProjectFacetActionEvent evt = (IProjectFacetActionEvent) event;
				if ("wst.jsdt.web"
						.equals(evt.getProjectFacet().getId())) {
					try {
						FacetedProjectFramework.removeListener(this);
						//project.delete(true, monitor);
						Installer.install(project.getName(), true, monitor);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		IFacetedProjectEvent.Type types = IFacetedProjectEvent.Type.POST_INSTALL;
		FacetedProjectFramework.addListener(listener, types);
	}
}