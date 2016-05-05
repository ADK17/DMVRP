package com.ADK.utils;

import java.io.*;

public class Reader {

	int count;

	public Reader(){
		count = 0;
	}

	public void readFile(String inputFile, String outputFile) {
		try {
			String str;
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			int temp = 0;
			while ((str = reader.readLine()) != null) {
				++temp;
				if (temp > count) {
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
	
	public String readOnly(String inputFile){
		
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
	
	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		try {
			Reader reader = new Reader();
		} catch (Exception e) {
			System.out.println(e + " in reader main()");
		}
	}
}
