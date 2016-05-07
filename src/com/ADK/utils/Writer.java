package com.ADK.utils;

import java.io.*;

/**
 * Writer Class - used for writing data to various files
 *
 */
public class Writer {

	/**
	 * Writes data to file
	 * @param data: Data to be written
	 * @param fileName: path and name of the file to which data is to be written
	 */
	public void writeFile(String data, String fileName) {
		try {
			BufferedWriter WriteFile = new BufferedWriter(new FileWriter(fileName, true));
			WriteFile.write(data);
			WriteFile.write("\n");
			WriteFile.close();

		} catch (Exception e) {
			System.out.println(e + " in writeFile()");
		}
	}

}
