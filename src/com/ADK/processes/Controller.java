package com.ADK.processes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.ADK.utils.Reader;
import com.ADK.utils.Writer;
/**
 * Controller Class
 * @author adk
 *
 */
public class Controller {
	//list of hosts
	ArrayList<Integer> hosts;
	//list of routers
	ArrayList<Integer> routers;
	//list of LANS
	ArrayList<Integer> lans;
	//reader instances for each host
	ArrayList<Reader> hostReaders;
	//reader instances for each router
	ArrayList<Reader> routerReaders;
	//writer instance
	Writer writer;

	public Controller(ArrayList<Integer> hosts, ArrayList<Integer> routers, ArrayList<Integer> lans) {
		super();
		this.hosts = hosts;
		this.routers = routers;
		this.lans = lans;
		hostReaders = new ArrayList<>();
		routerReaders = new ArrayList<>();
		
		for(int i=0;i<hosts.size();i++){
			hostReaders.add(new Reader());
			File file = new File("hout"+hosts.get(i)+".txt");
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}
		for(int i=0;i<routers.size();i++){
			routerReaders.add(new Reader());
			File file = new File("rout"+routers.get(i)+".txt");
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		for(int i=0;i<lans.size();i++){
			File file = new File("lan"+lans.get(i)+".txt");
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		writer = new Writer();
		startController();
	}
	
	/**
	 * Starts a Controller
	 */
	public void startController(){
			String content;
			System.out.println("Starting Controller");
			Long startTime = System.currentTimeMillis();
			Long endTime = System.currentTimeMillis();
			try {
				//run for 100 secs
				while(endTime - startTime<100000){
				Thread.sleep(10000);
				//read from all the host files
				for(int i=0;i<hosts.size();i++){
					content = hostReaders.get(i).readOnly("hout"+hosts.get(i)+".txt");
					parseMessage(content);		
					System.out.println("Message written by Controller from Host "+hosts.get(i)+" to LAN");
				}
				//read from all the router files
				for(int i=0;i<routers.size();i++){
					content = routerReaders.get(i).readOnly("rout"+routers.get(i)+".txt");
					parseMessage(content);
					System.out.println("Message written by Controller from Router "+routers.get(i)+" to LAN ");
				}
				endTime = System.currentTimeMillis();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	
	/**
	 * Parses a message from host or router files and forwards to LANS accordingly
	 * @param message: incoming message
	 */
	private void parseMessage(String message){
		
		String[] messages = message.split("\n");
		if(messages[0].equals("")){
			return;
		}
		
		for(int i=0;i<messages.length;i++){
			String[] currentMessage = messages[i].split(" ");
			//obtain destination LAN
			int destLanID = Integer.parseInt(currentMessage[1]);
			//write to LAN file
			writer.writeFile(messages[i], "lan"+destLanID+".txt");
			
		}
	}
	
	/**
	 * 
	 * @param args "host" id id id . . . id "router" id id . . . id "lan" id id . .  . id
	 */
	public static void main(String[] args){
		
		int i=1;
		ArrayList<Integer> hosts = new ArrayList<>();
		ArrayList<Integer> routers = new ArrayList<>();
		ArrayList<Integer> lans = new ArrayList<>();
		while(!args[i].equals("router")){
			hosts.add(Integer.parseInt(args[i]));
			i++;
		}
		i++;
		while(!args[i].equals("lan")){
			routers.add(Integer.parseInt(args[i]));
			i++;
		}
		i++;
		while(i<args.length){
			lans.add(Integer.parseInt(args[i]));
			i++;
		}
		
		Controller controller = new Controller(hosts, routers, lans);
	}
	
	
}
