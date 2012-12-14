package org.concord.mwbackend.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import javax.servlet.http.HttpServletRequest;

public final class Utilities {

	public final static byte COPY_SUCCESS = 0;
	public final static byte SOURCE_NOT_FOUND = 1;
	public final static byte FILE_ACCESS_ERROR = 2;
	public final static byte WRITING_ERROR = 3;

	private final static String FILE_SEPARATOR = System.getProperty("file.separator");

	private Utilities() {
	}

	public static boolean isEmptyEntry(String s) {
		if (s == null)
			return true;
		if (s.equals("null"))
			return true;
		if (s.trim().equals(""))
			return true;
		return false;
	}

	public static void save(InputStream is, File file) throws IOException {
		int amount = 0;
		FileOutputStream fos = new FileOutputStream(file);
		byte[] b = new byte[1024];
		while ((amount = is.read(b)) != -1) {
			fos.write(b, 0, amount);
		}
		fos.close();
	}

	public static void email(String subject, String content, String[] address, String contentType) {
		email(subject, content, address, contentType, null);
	}

	public static void email(String subject, String content, String[] address, String contentType, String senderAddress) {

		if (address == null || address.length <= 0)
			return;

		Session session = null;
		try {
			session = (Session) PortableRemoteObject.narrow(new InitialContext().lookup("java:/Mail"), Session.class);
		} catch (javax.naming.NamingException e) {
			e.printStackTrace();
		}

		try {
			MimeMessage m = new MimeMessage(session);
			if (senderAddress == null || senderAddress.trim().equals("")) {
				m.setFrom();
			} else {
				InternetAddress ia = new InternetAddress(senderAddress);
				m.setFrom(ia);
				m.setSender(ia);
			}

			Address[] to = new InternetAddress[address.length];
			for (int i = 0; i < address.length; i++) {
				to[i] = new InternetAddress(address[i]);
			}
			m.setRecipients(Message.RecipientType.TO, to);
			m.setSubject(subject);
			m.setSentDate(new Date());
			m.setContent(content, contentType);
			Transport.send(m);
		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}

	/** return the parent path of this path. */
	public static String getParent(String path) {
		if (path == null)
			return null;
		if (path.toLowerCase().indexOf("http://") != -1) {
			int i = path.lastIndexOf('/');
			if (i == -1)
				return null;
			return path.substring(0, i + 1);
		}
		int i = path.lastIndexOf(FILE_SEPARATOR);
		if (i == -1)
			i = path.lastIndexOf("/");
		if (i == -1)
			return null;
		return path.substring(0, i + 1);
	}

	public static String getBaseURL(HttpServletRequest request) {
		String s = request.getRequestURL().toString();
		String xfh = request.getHeader("x-forwarded-host");
		if (xfh == null)
			return s;
		String s1 = s.substring(7);
		s1 = s1.substring(s1.indexOf("/"));
		return "http://" + xfh + s1;
	}

	public static String currentTimeToDashedString() {
		Calendar c = Calendar.getInstance();
		String s = Integer.toString(c.get(Calendar.YEAR)).substring(2);
		return s + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE) + "-" + c.get(Calendar.HOUR_OF_DAY)
				+ "-" + c.get(Calendar.MINUTE);
	}

	public static String currentTimeToHexString() {
		return Long.toHexString(System.currentTimeMillis());
	}

	public static String changeExtension(String fileName, String ext) {
		StringBuffer sb = new StringBuffer(fileName);
		int dot = sb.lastIndexOf(".");
		sb.replace(dot + 1, sb.length(), ext);
		return new String(sb);
	}

	/** return the file name of this path */
	public static String getFileName(String path) {
		if (path == null)
			return null;
		int i = path.lastIndexOf("/");
		if (i == -1)
			i = path.lastIndexOf("\\");
		if (i == -1)
			i = path.lastIndexOf(FILE_SEPARATOR);
		if (i == -1)
			return path;
		return path.substring(i + 1, path.length());
	}

	public static String removeSuffix(String fileName) {
		int doc = fileName.lastIndexOf(".");
		if (doc == -1)
			return fileName;
		return fileName.substring(0, doc);
	}

	/**
	 * copy a file. This method should be used for renaming a file instead of
	 * <code>File.renameTo()</code>. The latter involves native methods which cannot be predicted
	 * within a Java application.
	 * 
	 * @param s
	 *            the source
	 * @param d
	 *            the destination
	 * @return COPY_SUCCESS if copied successfully, SOURCE_NOT_FOUND if the source is not found,
	 *         FILE_ACCESS_ERROR if access to the destination is not allowed.
	 */
	public static byte copy(File s, File d) {
		if (s == null || d == null)
			throw new IllegalArgumentException("File cannot be null.");
		if (!s.exists() || s.length() == 0)
			throw new IllegalArgumentException("File " + s + " does not exist, or is empty.");
		FileInputStream is = null;
		try {
			is = new FileInputStream(s);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return SOURCE_NOT_FOUND;
		}
		File dest = null;
		if (s.equals(d)) {
			dest = new File(d.getAbsolutePath() + ".tmp");
		} else {
			dest = d;
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(dest);
		} catch (IOException e) {
			e.printStackTrace();
			return FILE_ACCESS_ERROR;
		}
		byte b[] = new byte[1024];
		int amount;
		boolean returnError = false;
		try {
			while ((amount = is.read(b)) != -1) {
				fos.write(b, 0, amount);
			}
		} catch (IOException e) {
			e.printStackTrace();
			returnError = true;
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
			try {
				fos.close();
			} catch (IOException e) {
			}
		}
		if (returnError)
			return WRITING_ERROR;
		if (dest != d) {
			copy(dest, d);
			dest.deleteOnExit();
		}
		return COPY_SUCCESS;
	}

}
