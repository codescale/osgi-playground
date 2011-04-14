package org.codescale.bundle.internal.a;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * @author CodeScale
 * 
 */
public class InternalBundleClass {

	private final static String DEFAULT_RESOURCE = "/resources/bundle.file";

	public InternalBundleClass() {
		this(DEFAULT_RESOURCE);
	}

	public InternalBundleClass(String resource) {
		System.out.println("Created instance of " + getClass().getName());
		URL url = getClass().getResource(resource);
		if (url != null) {
			System.out.println("The resource " + resource + " was found.");
			try {
				InputStream inputStream = url.openStream();
				ReadableByteChannel readChannel = Channels.newChannel(inputStream);
				ByteBuffer byteBuffer = ByteBuffer.allocate(inputStream.available());
				readChannel.read(byteBuffer);
				System.out.print("Content > ");
				System.out.println(new String(byteBuffer.array()));
			} catch (IOException e) {
				System.err.println("Can not open a stream to the resource " + resource);
			}
		} else {
			System.err.println("The resource " + resource + " couldn't be found.");
		}
	}

	public void loadClass(String fqn) {
		try {
			getClass().getClassLoader().loadClass(fqn);
			System.out.println("Bundle A could load " + fqn);
		} catch (ClassNotFoundException e) {
			System.err.println("Bundle A could not load " + fqn);
		}
	}

}
