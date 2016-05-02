package com.ADK.processes;

import java.util.ArrayList;
import java.util.Scanner;

import com.ADK.utils.Reader;
import com.ADK.utils.Writer;

public class Router {

	int id;
	ArrayList<Integer> lanIDs;
	ArrayList<Reader> readers;
	String inFile;
	Writer writer;

	public Router(int id, ArrayList<Integer> lanIDs) {
		super();
		this.id = id;
		this.lanIDs = lanIDs;
		this.inFile = "rout" + id + ".txt";
		readers = new ArrayList<>();
		for(int i=0;i<lanIDs.size();i++){
			readers.add(new Reader());
		}
		startRouter();
	}

	private void startRouter() {
		int count = 0;
		System.out.println("Starting Router "+id);
		try {
			while (true) {
				Thread.sleep(10000);
				for (int i = 0; i < lanIDs.size(); i++) {
					readers.get(i).readFile("lan" + lanIDs.get(i) + ".txt",this.inFile);
				}
				System.out.println("Read from adjacent LANs");
				count++;
				if (count == 5) {
					count = 0;
					// send DV message
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args){
		//router-id lan-ID lan-ID lan-ID
		int length = args.length-1;
		int routerID = Integer.parseInt(args[0]);
		ArrayList<Integer> lanIDS = new ArrayList<>();
		for(int i=1;i<=length;i++){
			lanIDS.add(Integer.parseInt(args[i]));		
		}
		Router router = new Router(1,lanIDS);
		
	}

}
