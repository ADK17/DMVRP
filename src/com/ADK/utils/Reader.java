package com.ADK.utils;

import java.io.*;

/*
 */
public class Reader {

	int count;
	String inputFileName;
	String outputFileName;

	/** Creates a new instance of Reader */
	public Reader(String inputFileName, String outputFileName) {
		System.out.println("Constructor");
		this.inputFileName = inputFileName;
		this.outputFileName = outputFileName;
		count = 0;
	}

	public String readFile() {
		try {
			String str;
			BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
			int temp = 0;
			while ((str = reader.readLine()) != null) {
				++temp;
				if (temp > count) {
					String filePath = outputFileName;
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
		return "lala";
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		try {
			Reader reader = new Reader("sample.txt","sampleout.txt");
			for (int i = 1; i < 3; ++i) {
				Thread.sleep(10000);
				reader.readFile();
			}
		} catch (Exception e) {
			System.out.println(e + " in reader main()");
		}
	}
}
