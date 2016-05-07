package com.ADK.processes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.ADK.utils.Reader;
import com.ADK.utils.Writer;
/**
 * Router Class
 * @author adk
 *
 */
public class Router {
	//Router ID
	int id;
	//List of connected LANS
	ArrayList<Integer> lanIDs;
	//Reader instances for each connected LAN
	ArrayList<Reader> readers;
	//Routing Table
	LinkedHashMap<Integer, TableEntry> routingTable;
	//rout file
	String inFile;
	//writer instance
	Writer writer;
	//tracks last timestamp of 'receiver' message
	Long[] receiverTracker;
	//tracks non-responsive receivers
	boolean[] NMRLans;

	public Router(int id, ArrayList<Integer> lanIDs) {

		super();
		this.id = id;
		this.lanIDs = lanIDs;
		this.inFile = "rout" + id + ".txt";
		receiverTracker = new Long[10];
		NMRLans = new boolean[10];
		writer = new Writer();
		routingTable = new LinkedHashMap<>();
		readers = new ArrayList<>();

		for (int i = 0; i < lanIDs.size(); i++) {
			readers.add(new Reader());
		}
		//initializing routing table
		for (int i = 0; i < 10; i++) {
			TableEntry entry = new TableEntry(10, 10);
			routingTable.put(i, entry);
			receiverTracker[i] = (long) 0;
			NMRLans[i] = false;
		}

		startRouter();
	}

	/**
	 * Starts a Router
	 */
	private void startRouter() {
		int count1 = 0, count2 = 0;
		System.out.println("Starting Router " + id);
		Long startTime = System.currentTimeMillis();
		Long endTime = System.currentTimeMillis();
		// TODO Send NMR messages every 10 secs if receiver stops sending
		// receiver messages
		// TODO Check if a child router has stopped sending NMR messages
		try {
			//run for 100 secs
			while (endTime - startTime < 100000) {
				Thread.sleep(1000);
				//read LAN files
				readLANFiles();
				//check for any receiver timeouts
				checkReceiverTimeout();
				count1++;
				count2++;
				//send a DV message every 5 secs
				if (count1 == 5) {
					count1 = 0;
					sendDVMessage();
				}
				//send NMR messages(if any) every 10 secs
				if (count2 == 10) {
					count2 = 0;
					sendNMRMessages();
				}
				endTime = System.currentTimeMillis();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Checks for any receivers timing out
	 */
	private void checkReceiverTimeout() {
		Long currentTime = System.currentTimeMillis();
		for (int i = 0; i < receiverTracker.length; i++) {
			if (receiverTracker[i] > 0 && currentTime - receiverTracker[i] > 20000) {
				if (NMRLans[i] == false)
					sendNMRNow(i);
				NMRLans[i] = true;
			} else if (receiverTracker[i] > 0 && currentTime - receiverTracker[i] < 20000) {
				NMRLans[i] = false;
			}
		}
	}
	/**
	 * Checks and sends NMR mesaages
	 */
	private void sendNMRMessages() {
		for (int i = 0; i < 10; i++) {
			if (NMRLans[i]==true) {
				for (int j = 0; j < lanIDs.size(); j++) {
					if (lanIDs.get(j) == i)
						continue;
					writer.writeFile("NMR " + lanIDs.get(j) + " " + String.valueOf(id) + " " + i, inFile);
				}
			}
		}
	}

	/**
	 * Sends an NMR message immediately after detection
	 * @param hostLANID
	 */
	private void sendNMRNow(int hostLANID) {
		for (int j = 0; j < lanIDs.size(); j++) {
			if (lanIDs.get(j) == hostLANID)
				continue;
			writer.writeFile("NMR " + lanIDs.get(j) + " " + String.valueOf(id) + " " + hostLANID, inFile);
		}

	}

	/**
	 * Reads from adjacent LAN files
	 */
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

	/**
	 * Handles incoming 'data' messages
	 * @param message: the data message
	 * @param incomingLANID: LAN from which it is received
	 */
	private void handleDataMessage(String[] message, int incomingLANID) {

		int hostLANID = Integer.parseInt(message[2]);
		//Ignoring message from children in the multicast tree
		if (routingTable.get(hostLANID).childMap[incomingLANID] == 1) {
//			for (int i = 0; i < 10; i++)
//				System.out.print(routingTable.get(hostLANID).childMap[i] + " ");
			return;
		}
		//forwarding data message to relevant LANS
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
	/**
	 * Handles incoming DV message
	 * @param message: the DV message
	 */
	private void handleDVMessage(String[] message) {

		int sourceLANID = Integer.parseInt(message[1]);
		int sourceRouterID = Integer.parseInt(message[2]);
		int lanIndex = 0;
		//ignoring own DV message sent previously and read back
		if (id == sourceRouterID) {
			//System.out.println("Router " + id + " ignoring self DV message from LAN " + sourceLANID);
			return;
		}

		routingTable.get(sourceLANID).hopCount = 0;
		// routingTable.get(sourceLANID).nextHopRouter =
		// Math.min(sourceRouterID,
		// routingTable.get(sourceLANID).nextHopRouter);
		routingTable.get(sourceLANID).nextHopRouter = id;
		routingTable.get(sourceLANID).childMap[sourceLANID] = 0;
		
		// updating routing table
		for (int i = 3; i < message.length - 1; i++) {
			int currentHopCount = Integer.parseInt(message[i]);
			i++;
			int currentNextHopRouter = Integer.parseInt(message[i]);
			//new shortest path found
			if (routingTable.get(lanIndex).hopCount > currentHopCount + 1) {
				routingTable.put(lanIndex, new TableEntry(currentHopCount + 1, sourceRouterID));
				routingTable.get(lanIndex).childMap[sourceLANID] = 0;
			} else if (routingTable.get(lanIndex).hopCount == currentHopCount + 1) { //same hop count, comparing router ids
				if (routingTable.get(lanIndex).nextHopRouter > currentNextHopRouter) {
					routingTable.put(lanIndex, new TableEntry(currentHopCount + 1, sourceRouterID));
					routingTable.get(lanIndex).childMap[sourceLANID] = 0;
					for (int j = 0; j < 10; j++)
						System.out.print(routingTable.get(lanIndex).childMap[j] + " ");
				}
			}
			lanIndex++;
		}
	}
	/**
	 * Handles incoming NMR messages
	 * @param message: the NMR message
	 */
	private void handleNMRMessage(String[] message) {

		int sourceLANID = Integer.parseInt(message[3]);
		routingTable.get(sourceLANID).hopCount = 10;
		routingTable.get(sourceLANID).nextHopRouter = 10;
	}

	/**
	 * Handles incoming 'receiver' messages
	 * @param lanID: Incoming LAN ID
	 */
	private void handleMembershipMessage(int lanID) {
		receiverTracker[lanID] = System.currentTimeMillis();
		routingTable.get(lanID).hopCount = 0;
		routingTable.get(lanID).nextHopRouter = id;
	}

	//Sends a DV message
	private void sendDVMessage() {

		StringBuffer sb = new StringBuffer();
		//constructing data from table
		for (Integer lanID : routingTable.keySet()) {
			TableEntry entry = routingTable.get(lanID);
			sb.append(" ").append(String.valueOf(entry.hopCount)).append(" ")
					.append(String.valueOf(entry.nextHopRouter));
		}
		for (int i = 0; i < lanIDs.size(); i++) {
			StringBuffer message = new StringBuffer();
			//prepending values for specific outgoing LAN
			message.append("DV ").append(String.valueOf(lanIDs.get(i))).append(" ").append(id).append(sb);
			//writing to rout file
			writer.writeFile(message.toString(), inFile);
			System.out.println("Router " + id + " sent DV message");
		}
	}
	
	//utility function to join array of strings into a single string
	public String joinString(String[] message) {

		StringBuffer sb = new StringBuffer();
		int i;
		for (i = 0; i < message.length - 1; i++) {
			sb.append(message[i]).append(" ");
		}
		sb.append(message[i]);
		return sb.toString();
	}

	/**
	 * 
	 * @param args router-id lan-ID lan-ID lan-ID
	 */
	public static void main(String[] args) {
		int length = args.length - 1;
		int routerID = Integer.parseInt(args[0]);
		ArrayList<Integer> lanIDS = new ArrayList<>();
		for (int i = 1; i <= length; i++) {
			lanIDS.add(Integer.parseInt(args[i]));
		}
		Router router = new Router(routerID, lanIDS);

	}

	/**
	 * Class for Routing Table Entry
	 * @author adk
	 *
	 */
	class TableEntry {
		
		int hopCount;
		int nextHopRouter;
		//child bitmap
		int[] childMap;
		//leaf bitmap
		int[] leafMap;

		public TableEntry(int hopCount, int nextHopRouter) {
			super();
			this.hopCount = hopCount;
			this.nextHopRouter = nextHopRouter;
			childMap = new int[10];
			leafMap = new int[10];
			for (int i = 0; i < 10; i++) {
				childMap[i] = 1;
				leafMap[i] = 0;
			}
		}

	}
}
