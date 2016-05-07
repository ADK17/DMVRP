package com.ADK.processes;

import com.ADK.utils.Reader;
import com.ADK.utils.Writer;

/**
 * Host Class (Sender and Receiver)
 * 
 * @author adk
 *
 */
public class Host {
	// Host ID
	int id;
	// ID of adjoining LAN
	int lanID;
	// sender or receiver
	String type;
	// time to start
	int tts;
	// period between sender's multicasts
	int period;
	// hin file of the host
	String inFile;
	// hout file of the host
	String outFile;
	// lan file of adjoining LAN
	String inLANFile;
	// reader instance
	Reader reader;
	// writer instance
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
/**
 * Starts a Sender
 */
	private void startSender() {
		Long startTime = System.currentTimeMillis();
		Long endTime = System.currentTimeMillis();
		try {
			System.out.println("Starting Sender " + id);
			//Wait for time to start
			Thread.sleep(tts * 1000);
			//run for 100 secs
			while (endTime - startTime < 100000) {
				//send data message to hout file
				writer.writeFile("data " + lanID + " " + lanID, outFile);
				System.out.println("Data written by sender to hout" + id);
				//wait for 1 sec
				Thread.sleep(period * 1000);
				endTime = System.currentTimeMillis();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
/**
 * Starts a Receiver
 */
	private void startReceiver() {
		Long startTime = System.currentTimeMillis();
		Long endTime = System.currentTimeMillis();
		int count = 0;
		System.out.println("Starting Receiver " + id);
		//Send a 'receiver' message at startup
		sendMembershipMessage();
		try {
			//run for 100 secs
			while (endTime - startTime < 120000) {
				//wait for 1 sec
				Thread.sleep(1000);
				//Check LAN for data
				reader.readFile(inLANFile, inFile, "receiver");
				System.out.println("Receiver " + id + " read data from its LAN file " + inLANFile);
				count++;
				//Send 'receive' message every 10 secs
				if (count == 10) {
					count = 0;
					sendMembershipMessage();
				}
				endTime = System.currentTimeMillis();
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Sends a 'receiver' message
	 */
	private void sendMembershipMessage() {
		writer.writeFile("receiver " + String.valueOf(lanID), outFile);
		System.out.println("Receiver " + id + " sent membership message to " + inLANFile);
	}

	/**
	 * 
	 * @param args: host-id lan-id type time-to-start period
	 */
	public static void main(String args[]) {
		if (args[2].equals("receiver")) {
			Host host = new Host(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2], 0, 0);
		} else {
			Host host = new Host(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2],
					Integer.parseInt(args[3]), Integer.parseInt(args[4]));
		}

	}
}
