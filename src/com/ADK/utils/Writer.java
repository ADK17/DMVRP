package com.ADK.utils;

import java.io.*;


public class Writer {

	int count;

	/** Creates a new instance of writer */
	public Writer() {

		try {
			count = 0;
		} catch (Exception e) {
			System.out.println(e + "in writer");
		}
	}

	public void writeFile(String data, String fileName) {
		try {
			//String str = "Count " + count;
			String filePath = fileName;
			BufferedWriter WriteFile = new BufferedWriter(new FileWriter(filePath, true));
			WriteFile.write(data);
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
			Writer writer = new Writer();
			for (int i = 1; i < 5; ++i) {
				Thread.sleep(10000);
			//	writer.writeFile("lala");
			}
		} catch (Exception e) {
			System.out.println(e + " in writer main()");
		}
	}
}
