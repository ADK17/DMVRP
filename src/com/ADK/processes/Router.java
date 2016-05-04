package com.ADK.processes;

import java.util.ArrayList;
import java.util.HashMap;

import com.ADK.utils.Reader;
import com.ADK.utils.Writer;

public class Router {

	int id;
	ArrayList<Integer> lanIDs;
	ArrayList<Reader> readers;
	HashMap<Integer, TableEntry> routingTable;
	String inFile;
	Writer writer;

	public Router(int id, ArrayList<Integer> lanIDs) {

		super();
		this.id = id;
		this.lanIDs = lanIDs;
		this.inFile = "rout" + id + ".txt";
		routingTable = new HashMap<>();
		readers = new ArrayList<>();

		for (int i = 0; i < lanIDs.size(); i++) {
			readers.add(new Reader());
			TableEntry entry = new TableEntry(10, 10);
			routingTable.put(lanIDs.get(i), entry);
		}

		startRouter();
	}

	private void startRouter() {
		int count = 0;
		System.out.println("Starting Router " + id);
		//TODO Send NMR messages every 10 secs if receiver stops sending receiver messages
		//TODO Check if a child router has stopped sending NMR messages
		try {
			while (true) {
				Thread.sleep(10000);
				readLANFiles();
				count++;
				if (count == 5) {
					count = 0;
					sendDVMessage();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private void readLANFiles() {

		for (int i = 0; i < lanIDs.size(); i++) {

			String content = readers.get(i).readOnly("lan" + lanIDs.get(i) + ".txt");
			String[] messages = content.split("\n");
			if (messages[0].equals("")) {
				return;
			}
			for (int j = 0; j < messages.length; j++) {
				String[] currentMessage = messages[j].split(" ");

				switch (currentMessage[0]) {

				case "data":
					handleDataMessage(currentMessage, lanIDs.get(i));
					break;
				case "receiver":
					handleMembershipMessage(lanIDs.get(i));
					break;
				case "DV":
					handleDVMessage(currentMessage);
					break;

				case "NMR":
					handleNMRMessage(currentMessage);
					break;
				}
			}
		}
		System.out.println("Read from adjacent LANs");
	}

	private void handleDataMessage(String[] message, int incomingLANID) {

		for (int i = 0; i < lanIDs.size(); i++) {
			int outgoingLANID = lanIDs.get(i);
			if (incomingLANID != outgoingLANID && routingTable.get(outgoingLANID).hopCount == 0) {
				String temp = message[1];
				message[1] = "lan" + outgoingLANID;
				String messageToSend = joinString(message);
				message[1] = temp;
				writer.writeFile(messageToSend, inFile);
			}
		}
	}

	private void handleDVMessage(String[] message) {

		int sourceLANID = Integer.parseInt(message[1]);
		int sourceRouterID = Integer.parseInt(message[2]);

		routingTable.get(sourceLANID).hopCount = 0;
		routingTable.get(sourceLANID).nextHopRouter = Math.min(sourceRouterID,
				routingTable.get(sourceLANID).nextHopRouter);
		
		for(int i=3; i<message.length-1;i++){
			int currentHopCount = Integer.parseInt(message[i]);
			i++;
			int currentNextHopRouter = Integer.parseInt(message[i]);
		}
	}

	private void handleNMRMessage(String[] message) {

		int sourceLANID = Integer.parseInt(message[3]);
		routingTable.get(sourceLANID).hopCount = 10;
		routingTable.get(sourceLANID).nextHopRouter = 10;
	}

	private void handleMembershipMessage(int lanID) {
		// TODO receiver timing?
		routingTable.get(lanID).hopCount = 0;
		routingTable.get(lanID).nextHopRouter = id;
	}

	private void sendDVMessage() {

		StringBuffer sb = new StringBuffer();
		for (Integer lanID : routingTable.keySet()) {
			TableEntry entry = routingTable.get(lanID);
			sb.append(" ").append(entry.hopCount).append(" ").append(entry.nextHopRouter);
		}
		for (int i = 0; i < lanIDs.size(); i++) {
			StringBuffer message = new StringBuffer();
			message.append("DV ").append(lanIDs.get(i)).append(" ").append(sb);
			writer.writeFile(message.toString(), inFile);
		}
	}

	public String joinString(String[] message) {

		StringBuffer sb = new StringBuffer();
		int i;
		for (i = 0; i < message.length - 1; i++) {
			sb.append(message[i]).append(" ");
		}
		sb.append(message[i]);
		return sb.toString();
	}

	public static void main(String[] args) {
		// router-id lan-ID lan-ID lan-ID
		int length = args.length - 1;
		int routerID = Integer.parseInt(args[0]);
		ArrayList<Integer> lanIDS = new ArrayList<>();
		for (int i = 1; i <= length; i++) {
			lanIDS.add(Integer.parseInt(args[i]));
		}
		Router router = new Router(1, lanIDS);

	}

	class TableEntry {

		int hopCount;
		int nextHopRouter;

		public TableEntry(int hopCount, int nextHopRouter) {
			super();
			this.hopCount = hopCount;
			this.nextHopRouter = nextHopRouter;
		}

	}
}
