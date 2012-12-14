package org.concord.mwbackend.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class Unzipper {

	private Unzipper() {
	}

	public static void unzip(ZipInputStream zis, File dir) {
		int amount = 0;
		FileOutputStream fos = null;
		byte[] b = new byte[1024];
		File file = null, parent = null;
		ZipEntry ze = null;
		try {
			while ((ze = zis.getNextEntry()) != null) {
				if (!ze.isDirectory()) {
					file = new File(dir, ze.getName());
					if (!file.exists()) {
						parent = file.getParentFile();
						if (parent != null && !parent.exists())
							parent.mkdirs();
					}
					fos = new FileOutputStream(file);
					while ((amount = zis.read(b)) != -1) {
						fos.write(b, 0, amount);
					}
					file.setLastModified(ze.getTime());
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) { // lese the last fos is not closed
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (zis != null) {
				try {
					zis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
