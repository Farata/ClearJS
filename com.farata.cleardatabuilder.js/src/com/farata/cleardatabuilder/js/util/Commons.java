/**
 * Copyright (c) 2009 Farata Systems  http://www.faratasystems.com
 *
 * Licensed under The MIT License
 * Re-distributions of files must retain the above copyright notice.
 *
 * @license http://www.opensource.org/licenses/mit-license.php The MIT License
 *
 */
package com.farata.cleardatabuilder.js.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;

/**
 * Set of utilities solving common tasks of different aspects of plug-ins
 * development.
 * 
 * 
 */
public final class Commons {
	private static final IProgressMonitor DEFAULT_MONITOR = new IProgressMonitor() {

		public void beginTask(final String name, final int totalWork) {
		}

		public void done() {
		}

		public void internalWorked(final double work) {
		}

		public boolean isCanceled() {
			return false;
		}

		public void setCanceled(final boolean value) {
		}

		public void setTaskName(final String name) {
		}

		public void subTask(final String name) {
		}

		public void worked(final int work) {
		}

	};

	private Commons() {
	}

	/**
	 * Returns always nonnull instance of {@link IProgressMonitor}. If specified
	 * <tt>progressMonitor</tt> is <tt>null</tt> then default instance will be
	 * returned, otherwise the specified argument will be returned.
	 * 
	 * @param progressMonitor
	 *            the monitor to check.
	 * @return nonnull instance of {@link IProgressMonitor}.
	 */
	public static IProgressMonitor prepareMonitor(
			final IProgressMonitor progressMonitor) {
		final IProgressMonitor result;
		if (progressMonitor != null) {
			result = progressMonitor;
		} else {
			result = Commons.DEFAULT_MONITOR;
		}
		return result;
	}

	/**
	 * Returns a File representing the specified entry in the given plug-in. The
	 * classloader is not used here, only the contents of the plug-in is
	 * searched for the specified entry. A specified path of "/" indicates the
	 * root of the plug-in.
	 * 
	 * 
	 * @param bundle
	 *            Bundle which contents to search for the entry.
	 * @param entry
	 *            The name of the entry. See java.lang.ClassLoader.getResource
	 *            for a description of the format of a resource name.
	 * @return a File representing the specified entry in the given plug-in, or
	 *         <tt>null</tt> if no entry could be found or if the caller does
	 *         not have the appropriate permission.
	 * @throws CoreException
	 *             When failed to convert the URL of the entry to the URL using
	 *             the file protocol.
	 * @throws IllegalStateException
	 *             If this plug-in has been uninstalled.
	 * @throws NullPointerException
	 *             When specified arguments are <tt>null</tt>.
	 */
	public static File getBundleEntry(final Bundle bundle,
			final String entryName) throws Exception {
		final File result;
		final URL entry = bundle.getEntry(entryName);
		if (entry != null) {
			final URL fileUrl = FileLocator.toFileURL(entry);
			result = urlToFile(fileUrl);
		} else {
			result = null;
		}
		return result;
	}

	/**
	 * See {@link #getBundleEntry(Bundle, String)} and
	 * {@link Bundle#findEntries(String, String, boolean)}.
	 */
	public static List<File> getBundleEntries(final Bundle bundle,
			final String path, final String filePattern, final boolean recurse)
			throws Exception {
		final Enumeration<?> entries = bundle.findEntries(path, filePattern,
				recurse);
		final List<File> result = new ArrayList<File>();
		while (entries.hasMoreElements()) {
			final URL entry = (URL) entries.nextElement();
			final URL fileUrl = FileLocator.toFileURL(entry);
			result.add(urlToFile(fileUrl));
		}
		return result;
	}

	/**
	 * Returns a File representing the specified entry in the given plug-in. The
	 * classloader is not used here, only the contents of the plug-in is
	 * searched for the specified entry. A specified path of "/" indicates the
	 * root of the plug-in.
	 * 
	 * 
	 * @param plugin
	 *            Plug-in which contents to search for the entry.
	 * @param entry
	 *            The name of the entry. See java.lang.ClassLoader.getResource
	 *            for a description of the format of a resource name.
	 * @return a File representing the specified entry in the given plug-in, or
	 *         <tt>null</tt> if no entry could be found or if the caller does
	 *         not have the appropriate permission.
	 * @throws CoreException
	 *             When failed to convert the URL of the entry to the URL using
	 *             the file protocol.
	 * @throws IllegalStateException
	 *             If this plug-in has been uninstalled.
	 * @throws NullPointerException
	 *             When specified arguments are <tt>null</tt>.
	 */
	public static File getPluginEntry(final Plugin plugin,
			final String entryName) throws Exception {
		return getBundleEntry(plugin.getBundle(), entryName);
	}

	/**
	 * Converts {@link URL} to {@link File}.
	 * <p>
	 * <b>Developers Note:</b><br>
	 * The correct way to do it is:
	 * 
	 * <pre>
	 * File file = new File(url.toURI());
	 * </pre>
	 * 
	 * There is no other solution. If this method fails, then only because the
	 * URL was broken. This method tries to repair the URL assuming that path
	 * component contains unencoded unsafe characters (e.g. whitespaces) and
	 * thus constructing new URI by component.
	 * <p>
	 * Example of correct url: <tt>file:///c:/Program%20Files/</tt><br>
	 * 
	 * Example of incorrect but possible for convertation url:
	 * <tt>file:///c:/Program Files/</tt>
	 * 
	 * @param url
	 *            The url to be converted.
	 * @return The instance of {@link File} corresponding to the given url.
	 * @throws CoreException
	 *             When the specified url is broken beyond automatic repair and
	 *             cannot be converted to URI (RFC2396).
	 */
	public static File urlToFile(final URL url) throws Exception {
		URI uri;
		try {
			// this is the step that can fail, and so
			// it should be this step that should be fixed
			uri = url.toURI();
		} catch (final URISyntaxException e) {
			// OK if we are here, then obviously the URL did
			// not comply with RFC 2396. This can only
			// happen if we have illegal unescaped characters.
			// If we have one unescaped character, then
			// the only automated fix we can apply, is to assume
			// all characters are unescaped.
			// If we want to construct a URI from unescaped
			// characters, then we have to use the component
			// constructors:
			uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(),
					url.getPort(), url.getPath(), url.getQuery(), url.getRef());
		}
		return new File(uri);
	}

	public static void close(final InputStream stream) {
		if (stream == null) {
			return;
		}
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void close(final OutputStream stream) {
		if (stream == null) {
			return;
		}
		try {
			stream.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/** Reads first line of file with buffered reader using UTF-8 encoding. */
	public static String readFirstLine(final IFile file) throws IOException,
			CoreException {
		InputStream contents = null;
		try {
			contents = file.getContents();
			return new BufferedReader(new InputStreamReader(contents, "UTF8"))
					.readLine();
		} finally {
			close(contents);
		}
	}

	/** Reads all lines of file with buffered reader using UTF-8 encoding. */
	public static List<String> readToStringsList(final InputStream stream)
			throws IOException {
		final List<String> result = new ArrayList<String>();
		try {
			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(stream, "UTF8"));
			String line;
			while ((line = reader.readLine()) != null) {
				final String trimmed = line.trim();
				if (trimmed.length() > 0)
					result.add(trimmed);
			}
		} finally {
			close(stream);
		}
		return result;
	}

	public static List<IPath> toPaths(final List<String> strings) {
		final List<IPath> result = new ArrayList<IPath>(strings.size());
		for (final String string : strings) {
			result.add(new Path(string));
		}
		return result;
	}
}