package aQute.bnd.osgi;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

import aQute.bnd.osgi.URLResource.JarURLUtil;

public interface Resource extends Closeable {
	InputStream openInputStream() throws Exception;

	void write(OutputStream out) throws Exception;

	long lastModified();

	void setExtra(String extra);

	String getExtra();

	long size() throws Exception;

	ByteBuffer buffer() throws Exception;

	static Resource fromURL(URL url) throws IOException {
		if (url.getProtocol().equalsIgnoreCase("file")) {
			Path path = Paths.get(Paths.get("").toUri().resolve(url.getPath()));
			return new FileResource(path);
		}
		if (url.getProtocol().equals("jar")) {
			JarURLUtil util = new JarURLUtil(url);
			URL jarFileURL = util.getJarFileURL();
			if (jarFileURL.getProtocol().equalsIgnoreCase("file")) {
				Path path = Paths.get(Paths.get("").toUri().resolve(jarFileURL.getPath()));
				String entryName = util.getEntryName();
				if (entryName == null) {
					return new FileResource(path);
				}
				return new ZipResource(path, entryName);
			}
		}
		return new URLResource(url);
	}
}
