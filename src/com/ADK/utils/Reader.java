package com.ADK.utils;

import java.io.*;

/**
 * Reader Class
 *
 */
public class Reader {

	int count;

	public Reader() {
		count = 0;
	}

	/**
	 * Reads new data from a file and writes it to another
	 * 
	 * @param inputFile:
	 *            Input data file
	 * @param outputFile:
	 *            Output file
	 * @param type:
	 *            type of Process doing the reading
	 */
	public void readFile(String inputFile, String outputFile, String type) {
		try {
			String str;
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			int temp = 0;
			while ((str = reader.readLine()) != null) {
				++temp;
				if (temp > count) {
					// If it is receiver reading from LAN, ignore DV and
					// receiver messages
					if (type == "receiver" && (str.startsWith("DV") || str.startsWith("receiver")))
						continue;
					String filePath = outputFile;
					BufferedWriter WriteFile = new BufferedWriter(new FileWriter(filePath, true));
					WriteFile.write(str);
					WriteFile.write("\n");
					WriteFile.close();
					System.out.println(str);
				}
			}
			count = temp;
			reader.close();

		} catch (Exception e) {
			System.out.println(e + " in readFile()");
		}
	}

	/**
	 * Reads new data from a file and returns it
	 * 
	 * @param inputFile:
	 *            Input data file
	 * @return The data which is read
	 */
	public String readOnly(String inputFile) {

		StringBuffer stringBuffer = new StringBuffer();
		try {
			String str, output;
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			int temp = 0;
			while ((str = reader.readLine()) != null) {
				++temp;
				if (temp > count) {
					stringBuffer.append(str);
					stringBuffer.append("\n");
				}
			}
			count = temp;
			reader.close();

		} catch (Exception e) {
			System.out.println(e + " in readFile()");
		}
		return stringBuffer.toString();
	}

}
