/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.lib.ij;

import ij.IJ;
import imagingbook.lib.ij.IjLogStream;
import imagingbook.lib.ij.IjLogStream;
import imagingbook.lib.ij.IjLogStream;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * This class provides the functionality to divert output sent to the System.out
 * and System.err streams to ImageJ's log console. The purpose is to allow 
 * use of existing Java classes or writing new generic Java classes that only 
 * output to System.out and are thus less dependent on ImageJ.
 * See the ImageJ plugin {@link Redirect_System_Streams} for usage examples.
 *
 * @author Wilhelm Burger (wilbur@ieee.org)
 * @version 2013-02
 */
public class IjLogStream extends PrintStream {
	
	private static String outPrefix = "out> ";	// prefix string for System.out
	private static String errPrefix = "err >";	// prefix string for System.err
	
	private static PrintStream originalSystemOut = null;
	private static PrintStream originalSystemErr = null;
	private static PrintStream temporarySystemOut = null;
	private static PrintStream temporarySystemErr = null;
	
	/**
	 * Redirects all output sent to <code>System.out</code> and <code>System.err</code> to ImageJ's log console
	 * using the default prefixes.
	 * Alternatively use 
	 * {@link #redirectSystemOut(String)} and {@link #redirectSystemErr(String)}
	 * to redirect the streams separately and to specify individual prefixes.
	 */
	public static void redirectSystem() {
		redirectSystemOut(outPrefix);
		redirectSystemErr(errPrefix);
	}
	
	/**
	 * Redirects all output sent to <code>System.out</code> to ImageJ's log console.
	 * @param prefix The prefix string inserted at the start of each output line. 
	 * Pass <code>null</code>  to use the default prefix or an empty string to 
	 * remove the prefix.
	 */
	public static void redirectSystemOut(String prefix) {
		if (originalSystemOut == null) {		// has no effect if System.out is already replaced
			originalSystemOut = System.out;		// remember the original System.out stream
			temporarySystemOut = new IjLogStream(prefix);
			System.setOut(temporarySystemOut);
		}
	}
	
	/**
	 * Redirects all output sent to <code>System.err</code> to ImageJ's log console.
	 * @param prefix The prefix string inserted at the start of each output line. 
	 * Pass <code>null</code>  to use the default prefix or an empty string to 
	 * remove the prefix.
	 */
	public static void redirectSystemErr(String prefix) {
		if (originalSystemErr == null) {		// has no effect if System.out is already replaced
			originalSystemErr = System.err;		// remember the original System.out stream
			temporarySystemErr = new IjLogStream(prefix);
			System.setErr(temporarySystemErr);
		}
	}
	
	/**
	 * Returns the redirection stream for {@code System.out} if it exists.
	 * Note that a reference to the current output stream can also be obtained directly from 
	 * the {@code System.out} field.
	 * @return A reference to the {@code PrintStream} object currently substituting {@code System.out}
	 * or {@code null} of if {@code System.out} is currently not redirected.
	 */
	public static PrintStream getCurrentOutStream() {
		return temporarySystemOut;
	}
	
	/**
	 * Returns the redirection stream for {@code System.err} if it exists.
	 * Note that a reference to the current output stream can also be obtained directly from 
	 * the {@code System.err} field.
	 * @return A reference to the {@code PrintStream} object currently substituting {@code System.err}
	 * or {@code null} of if {@code System.err} is currently not redirected.
	 */
	public static PrintStream getCurrentErrStream() {
		return temporarySystemErr;
	}

	/**
	 * Use this method to revert both <code>System.out</code> and <code>System.err</code> 
	 * to their original output streams.
	 */
	public static void revertSystem() {
		revertSystemOut();
		revertSystemErr();
	}
	
	/**
	 * Use this method to revert<code>System.out</code>
	 * to the original output stream.
	 */
	public static void revertSystemOut() {
		if (originalSystemOut != null && temporarySystemOut != null) {
			temporarySystemOut.flush();
			temporarySystemOut.close();
			System.setOut(originalSystemOut);
			originalSystemOut = null;
			temporarySystemOut = null;
		}
	}
	
	/**
	 * Use this method to revert<code>System.err</code>
	 * to the original output stream.
	 */
	public static void revertSystemErr() {
		if (originalSystemErr != null && temporarySystemErr != null) {
			temporarySystemErr.flush();
			temporarySystemErr.close();
			System.setErr(originalSystemErr);
			originalSystemErr = null;
			temporarySystemErr = null;
		}
	}
	
	// ----------------------------------------------------------------
	
	private final String endOfLineSystem = System.getProperty("line.separator"); 
	private final String endOfLineShort = String.format("\n"); 	
	private final ByteArrayOutputStream byteStream;
	private final String prefix;
	
	/**
	 * The only constructor, not to be used from outside (private).
	 * @param prefix The prefix string preceding every output line produced by this stream.
	 */
	private IjLogStream(String prefix) {
		super(new ByteArrayOutputStream());
		this.byteStream = (ByteArrayOutputStream) this.out;
		this.prefix = (prefix == null) ? "" : prefix;
	}
	
	@Override
	// ever called?
	public void write(byte[] b) {
		this.write(b, 0, b.length);
	}
	
	@Override
	public void write(byte[] b, int off, int len) {
		String msg = new String(b, off, len);
		if (msg.equals(endOfLineSystem) || msg.equals(endOfLineShort)) { // this is a newline sequence only
			ejectBuffer();
		}
		else {
			byteStream.write(b, off, len);	// append message to buffer
			if (msg.endsWith(endOfLineSystem) || msg.endsWith(endOfLineShort)) { // line terminated by Newline
				// note that this does not seem to happen ever (even with format)!?
				ejectBuffer();
			}
		}
	}
	
	@Override
	// ever called?
	public void write(int b) {
		byteStream.write(b);
	}

	@Override
	public void flush() {
		if (byteStream.size() > 0) {
			ejectBuffer();
		}
		super.flush();
	}
	
	@Override
	public void close() {
		super.close();
	}
	
	private void ejectBuffer() {
		IJ.log(prefix + byteStream.toString());
		byteStream.reset();
	}
	
}
