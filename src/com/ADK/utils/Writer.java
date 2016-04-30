package com.ADK.utils;

import java.io.*;


public class Writer {

	int count;
	String fileName;

	/** Creates a new instance of writer */
	public Writer(String fileName) {

		try {
			// String pathname = "SharedFile";
			// File SharedFile = new File(pathname);
			// FileWriter SFile = new FileWriter(SharedFile);
			// SFile.close();
			count = 0;
			this.fileName = fileName;

		} catch (Exception e) {
			System.out.println(e + "in writer");
		}
	}

	void writeFile(String data) {
		try {
			String str = "Count " + count;
			String filePath = fileName;
			BufferedWriter WriteFile = new BufferedWriter(new FileWriter(filePath, true));
			WriteFile.write(str);
			WriteFile.write("\n");
			WriteFile.close();
			count++;

		} catch (Exception e) {
			System.out.println(e + " in writeFile()");
		}
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		try {
			Writer writer = new Writer("sampleout.txt");
			for (int i = 1; i < 5; ++i) {
				Thread.sleep(10000);
				writer.writeFile("lala");
			}
		} catch (Exception e) {
			System.out.println(e + " in writer main()");
		}
	}
}
