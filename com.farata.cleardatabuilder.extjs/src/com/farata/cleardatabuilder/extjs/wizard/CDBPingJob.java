package com.farata.cleardatabuilder.extjs.wizard;

import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.connectivity.ui.PingJob;
import org.eclipse.swt.widgets.Shell;

public class CDBPingJob extends PingJob {

	private Shell shell;
	private String url;
	private boolean checkExternal;

	public CDBPingJob(Shell shell, String url, boolean checkExternal) {
		super(shell, null);
		this.shell = shell;
		this.url = url;
		this.checkExternal = checkExternal;
	}

	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Pinging the server...", -1);
		monitor.done();
		(new PingUIJob(shell, validateURL())).schedule();
		return Status.OK_STATUS;
	}

	private Throwable validateURL() {
		IStatus status = new Status(0, "unknown", "Ext JS location is valid.");
		boolean valid = url != null && url.trim().length() > 0;
		if (!valid) {
			return new Exception("Ext JS location is empty.");
		}
		if (!checkExternal || isExternalURL(url)) {
			if (url.endsWith("/")) {
				url = url.substring(0, url.length() - 1);
			}
			try {
				URL u = new URL(url + "/ext-all.js");
				u.openStream();
			} catch (Exception e) {
				return new RuntimeException(url + " is not valid", e);
			}
		}
		return null;
	}

	public static boolean isExternalURL(String sPath) {
		return sPath.startsWith("http");
	}
}
