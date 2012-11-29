/**
 * Copyright (c) 2009 Farata Systems  http://www.faratasystems.com
 *
 * Licensed under The MIT License
 * Re-distributions of files must retain the above copyright notice.
 *
 * @license http://www.opensource.org/licenses/mit-license.php The MIT License
 *
 */
package com.farata.cleardatabuilder.extjs.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;

import com.farata.cleardatabuilder.extjs.Activator;


/**
 * Util class.
 */
public class Util {

	public static final int OVERRIDE_NEVER = 0;

	public static final int OVERRIDE_UPDATE = 1;

	public static final int OVERRIDE_ALWAYS = 2;

	public static final int ERROR_STATUS_CODE = 123;

	public static void copyFile(final String srcDir, final String destDir,
			final String fileName, final int override)
			throws FileNotFoundException, IOException {
		copyFile(new File(srcDir, fileName), new File(destDir, fileName),
				override);
	}

	/**
	 * Copy file. The parameters <code>srcFile</code> and <code>destFile</code>
	 * can't be directory.
	 * 
	 * @param srcFile
	 * @param destFile
	 * @param override
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void copyFile(final File srcFile, final File destFile,
			final int override) throws FileNotFoundException, IOException {
		if (override == OVERRIDE_NEVER && destFile.exists()) {
			return;
		}
		if (override == OVERRIDE_UPDATE
				&& destFile.lastModified() > srcFile.lastModified()) {
			return;
		}

		if (!destFile.exists()) {
			File parent = destFile.getParentFile();
			if (parent != null) {
				parent.mkdirs();
			}
		}
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(srcFile);
			fos = new FileOutputStream(destFile);
			fis.getChannel().transferTo(0, srcFile.length(), fos.getChannel());
		} finally {
			close(fis);
			close(fos);
		}
	}

	/**
	 * Recursive copy all files from specified directory.
	 * 
	 * @param srcDir
	 * @param destDir
	 * @param override
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void copyFiles(final File srcDir, final File destDir,
			final int override, final String[] exceptFiles) throws FileNotFoundException, IOException {
		copyFiles(srcDir, srcDir.list(), destDir, override, exceptFiles);
	}

	/**
	 * Recursive copy list of files from specified directory.
	 * 
	 * @param srcDir
	 * @param files
	 * @param destDir
	 * @param override
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void copyFiles(final File srcDir, final String[] files,
			final File destDir, final int override, final String[] exceptFiles)
			throws FileNotFoundException, IOException {
		destDir.mkdirs();
		for (int index = 0; index < files.length; index++) {
			final String fileName = files[index];
			final File srcFile = new File(srcDir, fileName);
			if (srcFile.isDirectory()) {
				if (fileName.equals("CVS") || fileName.toLowerCase().equals(".svn"))
					continue;
				copyFiles(srcFile, new File(destDir, fileName), override, exceptFiles);
				continue;
			} else {
				boolean exclude = false;
				for (String excFileName:exceptFiles) {
					if (fileName.equals(excFileName)) {
						exclude = true;
						break;
					}
				}
				if (exclude) {
					continue;
				}
			}
			copyFile(srcFile, new File(destDir, fileName), override);
		}
	}

	/*
	 * Helper method to ensure an array is converted into an ArrayList.
	 */
	public static String[] getArray(final String args) {
		final StringBuffer sb = new StringBuffer();
		boolean waitingForQuote = false;
		final ArrayList<String> result = new ArrayList<String>();
		for (final StringTokenizer tokens = new StringTokenizer(args, ", \"", //$NON-NLS-1$
				true); tokens.hasMoreTokens();) {
			final String token = tokens.nextToken();
			if (waitingForQuote) {
				if (token.equals("\"")) { //$NON-NLS-1$
					result.add(sb.toString());
					sb.setLength(0);
					waitingForQuote = false;
				} else {
					sb.append(token);
				}
			} else if (token.equals("\"")) { //$NON-NLS-1$
				// test if we have something like -Dproperty="value"
				if (result.size() > 0) {
					final int index = result.size() - 1;
					final String last = result.get(index);
					if (last.charAt(last.length() - 1) == '=') {
						result.remove(index);
						sb.append(last);
					}
				}
				waitingForQuote = true;
			} else if (!(token.equals(",") || token.equals(" "))) { //$NON-NLS-1$ //$NON-NLS-2$
				result.add(token);
			}
		}
		return result.toArray(new String[result.size()]);
	}

	/**
	 * Close specified input stream and catch and ignore all Throwable.
	 * 
	 * @param stream
	 */
	public static void close(final InputStream stream) {
		try {
			stream.close();
		} catch (final Throwable thIgnore) {
		}
	}

	/**
	 * Close specified output stream and catch and ignore all Throwable.
	 * 
	 * @param stream
	 */
	public static void close(final OutputStream stream) {
		try {
			stream.close();
		} catch (final Throwable thIgnore) {
		}
	}

	/**
	 * Close specified reader and catch and ignore all Throwable.
	 * 
	 * @param stream
	 */
	public static void close(final Reader reader) {
		try {
			reader.close();
		} catch (final Throwable thIgnore) {
		}
	}

	/**
	 * Close specified writer and catch and ignore all Throwable.
	 * 
	 * @param stream
	 */
	public static void close(final Writer writer) {
		try {
			writer.close();
		} catch (final Throwable thIgnore) {
		}
	}

	/**
	 * Close specified socked and catch and ignore all Throwable.
	 * 
	 * @param stream
	 */
	public static void close(final Socket soc) {
		try {
			soc.close();
		} catch (final Throwable thIgnore) {
		}
	}

	public static void refreshProject(final IProject project, final int depth) {
		if (project != null) {
			try {
				project.refreshLocal(depth, null);
			} catch (final Throwable th) {
			}
		}
	}

	public static void unpack(final String destDir, final String srcFileName)
			throws IOException {
		unpack(destDir, srcFileName, OVERRIDE_UPDATE);
	}

	public static void unpack(final String destDir, final String srcFileName,
			final int override) throws IOException {
		final JarFile flexWarFile = new JarFile(srcFileName);
		for (final Enumeration<JarEntry> entries = flexWarFile.entries(); entries
				.hasMoreElements();) {
			unpackEntry(destDir, srcFileName, flexWarFile, entries
					.nextElement(), override);
		}
	}

	private static void unpackEntry(final String destDir,
			final String srcFileName, final JarFile flexWarFile,
			final JarEntry entry, final int override) throws IOException {
		final File destFile = new File(destDir + "/" + entry.getName()); //$NON-NLS-1$
		File parent = destFile.getParentFile();
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		}
		if (entry.isDirectory()) {
			destFile.mkdirs();
			return;
		}

		if (destFile.exists()
				&& (override == OVERRIDE_NEVER || override == OVERRIDE_UPDATE
						&& destFile.lastModified() >= entry.getTime())) {
			return;
		}

		InputStream is = null;
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(destFile);
			is = flexWarFile.getInputStream(entry);
			final ReadableByteChannel inChannel = Channels.newChannel(is);
			final WritableByteChannel outChannel = Channels.newChannel(os);
			final ByteBuffer buffer = ByteBuffer
					.allocate((int) entry.getSize());
			inChannel.read(buffer);
			buffer.position(0);
			outChannel.write(buffer);
			os.flush();
		} finally {
			Util.close(is);
			Util.close(os);
			destFile.setLastModified(entry.getTime());
		}
	}

	public static void copyFiles(final List<File> srcFiles, final File destDir,
			final int overrideOption, final String[] exceptFiles) throws FileNotFoundException, IOException {
		destDir.mkdirs();
		for (final File srcFile : srcFiles) {
			if (srcFile.isDirectory()) {
				copyFiles(srcFile, new File(destDir, srcFile.getName()),
						overrideOption, exceptFiles);
				continue;
			}
			copyFile(srcFile, new File(destDir, srcFile.getName()),
					overrideOption);
		}
	}

	public static boolean compareFiles(final InputStream file1,
			final InputStream file2) throws IOException {
		final byte[] buffer1 = new byte[1024 * 16];
		final byte[] buffer2 = new byte[1024 * 16];
		int readBytes1;
		int readBytes2;

		try {
			do {
				readBytes1 = file1.read(buffer1);
				readBytes2 = file2.read(buffer2);

				if (readBytes1 != readBytes2)
					return false;

				for (int i = 0; i < readBytes1; i++)
					if (buffer1[i] != buffer2[i])
						return false;

			} while (readBytes1 > 0);
		} finally {
			file1.close();
			file2.close();
		}
		return true;
	}

	public static void createParentFolders(final IResource resource,
			final IProgressMonitor monitor) throws CoreException {

		createParentFolders(resource.getProject(), resource
				.getProjectRelativePath(), monitor);
	}

	public static void createParentFolders(final IProject project,
			final IPath projectRelativePath, final IProgressMonitor monitor)
			throws CoreException {
		String[] segments = projectRelativePath.segments();

		if (!project.exists()) {
			throw new CoreException(new Status(Status.ERROR,
					Activator.PLUGIN_ID, projectRelativePath
							+ " is not within existent project"));
		}

		int lastFolderSegment = segments.length - 2;
		if (lastFolderSegment < 0)
			return;// EARLY EXIT!

		IFolder currFolder = project.getFolder(segments[0]);
		for (int i = 0; i <= lastFolderSegment; i++) {
			if (!currFolder.exists()) {
				currFolder.create(true, true, monitor);
			}
			if (i < lastFolderSegment)
				currFolder = currFolder.getFolder(segments[i + 1]);
		}
	}
}
