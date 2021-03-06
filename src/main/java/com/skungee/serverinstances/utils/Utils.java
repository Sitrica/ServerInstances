package com.skungee.serverinstances.utils;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class Utils {

	public static void copyDirectory(File source, File destination) throws IOException {
		if (source.isDirectory()) {
			if (!destination.exists()) destination.mkdir();
			String[] files = source.list();
			for (String file : files) {
				copyDirectory(new File(source, file), new File(destination, file));
			}
		} else if (source.exists()) {
			InputStream in = new FileInputStream(source);
			OutputStream out = new FileOutputStream(destination);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}

	public static void deleteDirectory(File folder) {
		try {
			Files.walk(folder.toPath())
					.sorted(Comparator.reverseOrder())
					.map(Path::toFile)
					.forEach(File::delete);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isReachable(SocketAddress address) {
		Socket socket = new Socket();
		try {
			socket.connect(address, 500);
			socket.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static boolean isTaken(InetSocketAddress address) {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket();
			socket.bind(address);
			socket.setReuseAddress(true);
			return false;
		} catch (IOException e) {
			return true;
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {}
			}
		}
	}

	public static int findPort(InetAddress address, int start, int max) {
		int port = start;
		while (port < max) {
			if (!isTaken(new InetSocketAddress(address, port)))
				return port;
			port++;
		}
		return -1;
	}

}
