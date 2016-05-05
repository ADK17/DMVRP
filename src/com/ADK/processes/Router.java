package com.ADK.processes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.ADK.utils.Reader;
import com.ADK.utils.Writer;

public class Router {

	int id;
	ArrayList<Integer> lanIDs;
	ArrayList<Reader> readers;
	LinkedHashMap<Integer, TableEntry> routingTable;
	String inFile;
	Writer writer;
	Long[] receiverTracker;

	public Router(int id, ArrayList<Integer> lanIDs) {

		super();
		this.id = id;
		this.lanIDs = lanIDs;
		this.inFile = "rout" + id + ".txt";
		receiverTracker = new Long[10];
		writer = new Writer();
		routingTable = new LinkedHashMap<>();
		readers = new ArrayList<>();

		for (int i = 0; i < lanIDs.size(); i++) {
			readers.add(new Reader());
		}
		for (int i = 0; i < 10; i++) {
			TableEntry entry = new TableEntry(10, 10);
			routingTable.put(i, entry);
			receiverTracker[i] = (long) 0;
		}

		startRouter();
	}

	private void startRouter() {
		int count1 = 0, count2 = 0;
		System.out.println("Starting Router " + id);
		Long startTime = System.currentTimeMillis();
		Long endTime = System.currentTimeMillis();
		// TODO Send NMR messages every 10 secs if receiver stops sending
		// receiver messages
		// TODO Check if a child router has stopped sending NMR messages
		try {
			while (endTime - startTime < 100000) {
				Thread.sleep(1000);
				readLANFiles();
				// checkReceiverTimeout();
				count1++;
				count2++;
				if (count1 == 5) {
					count1 = 0;
					sendDVMessage();
				}
				if (count2 == 10) {
					count2 = 0;
					// sendNMRMessages();
				}
				endTime = System.currentTimeMillis();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private void checkReceiverTimeout() {
		Long currentTime = System.currentTimeMillis();
		for (int i = 0; i < receiverTracker.length; i++) {
			if (receiverTracker[i] > 0 && currentTime - receiverTracker[i] > 20000) {

			}
		}
	}

	private void sendNMRMessages() {

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
					// System.out.println(messages[j]);
					handleDVMessage(currentMessage);
					break;
				case "NMR":
					handleNMRMessage(currentMessage);
					break;
				}
			}
		}
		System.out.println("Router " + id + " read from adjacent LANs");
	}

	private void handleDataMessage(String[] message, int incomingLANID) {

		int hostLANID = Integer.parseInt(message[2]);
		if(routingTable.get(hostLANID).childMap[incomingLANID] == 1){
			System.out.println("Router"+id+" ignoring data message from LAN"+incomingLANID);
			for(int i=0;i<10;i++)
				System.out.print(routingTable.get(hostLANID).childMap[i]+" ");
			return;
		}
		for (int i = 0; i < lanIDs.size(); i++) {
			int outgoingLANID = lanIDs.get(i);
			if (incomingLANID != outgoingLANID && routingTable.get(outgoingLANID).hopCount == 0) {
				String temp = message[1];
				message[1] = String.valueOf(outgoingLANID);
				String messageToSend = joinString(message);
				message[1] = temp;
				writer.writeFile(messageToSend, inFile);
			}
		}
	}

	private void handleDVMessage(String[] message) {

		int sourceLANID = Integer.parseInt(message[1]);
		int sourceRouterID = Integer.parseInt(message[2]);
		int lanIndex = 0;

		routingTable.get(sourceLANID).hopCount = 0;
		routingTable.get(sourceLANID).nextHopRouter = Math.min(sourceRouterID,
				routingTable.get(sourceLANID).nextHopRouter);
		routingTable.get(sourceLANID).childMap[sourceLANID] = 0;

		for (int i = 3; i < message.length - 1; i++) {
			int currentHopCount = Integer.parseInt(message[i]);
			i++;
			int currentNextHopRouter = Integer.parseInt(message[i]);
			if (routingTable.get(lanIndex).hopCount > currentHopCount + 1) {
				routingTable.put(lanIndex, new TableEntry(currentHopCount + 1, sourceRouterID));
				routingTable.get(lanIndex).childMap[sourceLANID] = 0;
				System.out.println("Updated child map of Router"+id+" for lan"+lanIndex);
				for(int j=0;j<10;j++)
					System.out.print(routingTable.get(lanIndex).childMap[j]+" ");
			} else if (routingTable.get(lanIndex).hopCount == currentHopCount + 1) {
				if (routingTable.get(lanIndex).nextHopRouter > currentNextHopRouter) {
					routingTable.put(lanIndex, new TableEntry(currentHopCount + 1, sourceRouterID));
					routingTable.get(lanIndex).childMap[sourceLANID] = 0;
					for(int j=0;j<10;j++)
						System.out.print(routingTable.get(lanIndex).childMap[j]+" ");
				}
			}
			lanIndex++;
		}
	}

	private void handleNMRMessage(String[] message) {

		int sourceLANID = Integer.parseInt(message[3]);
		routingTable.get(sourceLANID).hopCount = 10;
		routingTable.get(sourceLANID).nextHopRouter = 10;
	}

	private void handleMembershipMessage(int lanID) {
		receiverTracker[lanID] = System.currentTimeMillis();
		routingTable.get(lanID).hopCount = 0;
		routingTable.get(lanID).nextHopRouter = id;
	}

	private void sendDVMessage() {

		StringBuffer sb = new StringBuffer();
		for (Integer lanID : routingTable.keySet()) {
			TableEntry entry = routingTable.get(lanID);
			sb.append(" ").append(String.valueOf(entry.hopCount)).append(" ")
					.append(String.valueOf(entry.nextHopRouter));
		}
		for (int i = 0; i < lanIDs.size(); i++) {
			StringBuffer message = new StringBuffer();
			message.append("DV ").append(String.valueOf(lanIDs.get(i))).append(sb);
			writer.writeFile(message.toString(), inFile);
			System.out.println("Router " + id + " sent DV message");
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
		Router router = new Router(routerID, lanIDS);

	}

	class TableEntry {

		int hopCount;
		int nextHopRouter;
		int[] childMap;

		public TableEntry(int hopCount, int nextHopRouter) {
			super();
			this.hopCount = hopCount;
			this.nextHopRouter = nextHopRouter;
			childMap = new int[10];
			for (int i = 0; i < 10; i++)
				childMap[i] = 1;
		}

	}
}
