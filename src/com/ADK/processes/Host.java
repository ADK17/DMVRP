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
		try {
			System.out.println("Starting Sender "+id);
			Thread.sleep(tts * 1000);
			while (true) {
				writer.writeFile("data "+ lanID +" " +lanID, outFile);
				System.out.println("Data written to file");
				Thread.sleep(period * 1000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void startReceiver() {
		System.out.println("Starting Receiver "+id);
		try {
			while(true){
				Thread.sleep(1000);
				reader.readFile(inLANFile, inFile);
				System.out.println("Read data");
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static void main(String args[]) {
		// host-id lan-id type time-to-start period
		Host host = new Host(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]),
				Integer.parseInt(args[4]));

	}
}
