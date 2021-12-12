package com.mihnita.test.swt_clipboard;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorMap;
import java.awt.datatransfer.SystemFlavorMap;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ClipboardDumper {
	private final static String DUMP_FOLDER = "target/clipdump/";

	public static void dump() {
		System.out.println("AWT Clipboard content:");

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		int i = 0;
		FlavorMap flavorMap = SystemFlavorMap.getDefaultFlavorMap();
		DataFlavor[] allDataFlavors = clipboard.getAvailableDataFlavors();
		Map<DataFlavor, String> flavorToNative = flavorMap.getNativesForFlavors(allDataFlavors);
		
		for (Entry<DataFlavor, String> e : flavorToNative.entrySet()) {
			System.out.println("    flavor to native map: " + e.getKey() + " : " + e.getValue());
		}

		try {
			Files.createDirectories(Paths.get(DUMP_FOLDER));
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		Map<String, String> fileNames = new TreeMap<>();
		for (DataFlavor flavor : allDataFlavors) {		
			String shortFileName = String.format("clip.%05d", i++);
			String fileName = DUMP_FOLDER + shortFileName;
			fileNames.put(shortFileName, flavor.getMimeType() + " // " + flavorToNative.get(flavor));
			try {
				Object data = clipboard.getData(flavor);
				switch (data.getClass().getSimpleName()) {
					case "byte[]":
						dumpBytes(fileName, (byte[]) data);
						break;
					case "HeapByteBuffer":
						dumpBytes(fileName, ((ByteBuffer)data).array());
						break;
					case "char[]":
						dumpString(fileName, new String((char[])data));
						break;
					case "String":
						dumpString(fileName, (String) data);
						break;
					case "StringCharBuffer":
						dumpString(fileName, ((CharBuffer)data).toString());
						break;
					case "ReencodingInputStream":
						dumpInputStream(fileName, (InputStream) data);
						break;
					case "ByteArrayInputStream":
						dumpInputStream(fileName, (InputStream) data);
						break;
					case "InputStreamReader":
						InputStreamReader isr = (InputStreamReader)data;
						dumpReader(fileName, isr, isr.getEncoding());
						break;
					case "StringReader":
						dumpReader(fileName, (Reader) data, "utf-8");
						break;
					default:
						System.out.println("Don't know how to dump this: " + data.getClass().getSimpleName());
				}
			} catch (Exception e) {
				System.out.println("data: ERROR");
			}
		}

		try (FileWriter fw = new FileWriter(DUMP_FOLDER + "/Descript.ion", StandardCharsets.UTF_8)) {
			for (Entry<String, String> e : fileNames.entrySet()) {			
				fw.append(e.getKey() + "\t" + e.getValue() + "\n");
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private static void dumpReader(String fileName, Reader reader, String encoding) {
		StringBuilder str = new StringBuilder();
		try {
			do {
				int ch = reader.read();
				if (ch == -1)
					break;
				str.append((char) ch);
			} while (true);
			dumpBytes(fileName, str.toString().getBytes(encoding));
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void dumpInputStream(String fileName, InputStream is) {
		try {
			dumpBytes(fileName, is.readAllBytes());
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void dumpString(String fileName, String data) {
		dumpBytes(fileName, data.getBytes(StandardCharsets.UTF_8));
	}

	static void dumpBytes(String fileName, byte[] allBytes) {
		try (FileOutputStream os = new FileOutputStream(fileName)) {
			os.write(allBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
