package com.ADK.processes;

import com.ADK.utils.Reader;
import com.ADK.utils.Writer;

public class Host {
	
	int id;
	int lanID;
	String type;
	int tts;
	int period;
	String inFile;
	String outFile;
	String inLANFile;
	Reader reader;
	Writer writer;
	
	public Host(int id, int lanID, String type, int tts, int period) {
		super();
		this.id = id;
		this.lanID = lanID;
		this.type = type;
		this.tts = tts;
		this.period = period;
		this.outFile="hout"+id+".txt";
		this.inLANFile="lan"+lanID+".txt";
		this.inFile="hin"+id+".txt";
		reader = new Reader(inLANFile,inFile);
		writer = new Writer(outFile);
	}
	
	public void startHost(){
		if(this.type=="receiver"){
			startReceiver();
		}
		else if(this.type=="sender"){
			startSender();
		}
	}
	
	private void startSender(){
		try {
			Thread.sleep(tts*1000);
			while(true){
				
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void startReceiver(){
		
		try {
			Thread.sleep(1000);
			reader.readFile();
			
		} catch (InterruptedException e) {	
			e.printStackTrace();
		}
		
	}
	
	public static void main(String args[]){
		Host host = new Host(1, 2, "receiver", 0, 0);
		host.startHost();
	}
}
