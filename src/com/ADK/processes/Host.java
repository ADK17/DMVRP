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
		this.outFile = "hout" + id + ".txt";
		this.inLANFile = "lan" + lanID + ".txt";
		this.inFile = "hin" + id + ".txt";
		reader = new Reader();
		writer = new Writer();
		startHost();
	}

	public void startHost() {
		if (this.type.equals("receiver")) {
			startReceiver();
		} else if (this.type.equals("sender")) {
			startSender();
		}
	}

	private void startSender() {
		Long startTime = System.currentTimeMillis();
		Long endTime = System.currentTimeMillis();
		try {
			System.out.println("Starting Sender "+id);
			Thread.sleep(tts * 1000);
			while (endTime - startTime<100000) {
				writer.writeFile("data "+ lanID +" " +lanID, outFile);
				System.out.println("Data written by sender to hout"+id);
				Thread.sleep(period * 1000);
				endTime = System.currentTimeMillis();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void startReceiver() {
		Long startTime = System.currentTimeMillis();
		Long endTime = System.currentTimeMillis();
		int count=0;
		System.out.println("Starting Receiver "+id);
		sendMembershipMessage();
		try {
			while(endTime - startTime<100000){
				Thread.sleep(5000);
				reader.readFile(inLANFile, inFile);
				System.out.println("Receiver "+id+" read data from its LAN file "+inLANFile);
				count++;
				if(count==10){
					count=0;
					sendMembershipMessage();
				}
				endTime = System.currentTimeMillis();
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private void sendMembershipMessage(){
		writer.writeFile("receiver "+String.valueOf(lanID), inLANFile);
		System.out.println("Receiver "+id+" sent membership message to "+inLANFile);
	}
	public static void main(String args[]) {
		// host-id lan-id type time-to-start period
		if(args[2].equals("receiver")){
			Host host = new Host(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2], 0,
					0);
		}
		else{
			Host host = new Host(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]),
					Integer.parseInt(args[4]));
		}
		

	}
}
