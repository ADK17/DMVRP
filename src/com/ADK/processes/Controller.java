package com.ADK.processes;

import java.io.File;
import java.util.ArrayList;

import com.ADK.utils.Reader;
import com.ADK.utils.Writer;

public class Controller {
	
	ArrayList<Integer> hosts;
	ArrayList<Integer> routers;
	ArrayList<Integer> lans;
	ArrayList<Reader> hostReaders;
	ArrayList<Reader> routerReaders;
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
		}
		for(int i=0;i<routers.size();i++){
			routerReaders.add(new Reader());
		}
		
		for(int i=0;i<lans.size();i++){
			File file = new File("lan"+lans.get(i)+".txt");
		}
		
		writer = new Writer();
		startController();
	}
	
	public void startController(){
			String content;
			System.out.println("Starting Controller");
			try {
				while(true){
				Thread.sleep(10000);
				for(int i=0;i<hosts.size();i++){
					content = hostReaders.get(i).readOnly("hout"+hosts.get(i)+".txt");
					parseHostMessage(content);				
				}
//				for(int i=0;i<routers.size();i++){
//					content = routerReaders.get(i).readOnly("rout"+routers.get(i)+".txt");
//					parseRouterMessage(content);
//				}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	
	private void parseHostMessage(String message){
		
		String[] messages = message.split("\n");
		if(messages[0].equals("")){
			//System.out.println("No new messages");
			return;
		}
		
		for(int i=0;i<messages.length;i++){
			String[] currentMessage = messages[i].split(" ");			
			int destLanID = Integer.parseInt(currentMessage[1]);
			writer.writeFile(messages[i], "lan"+destLanID+".txt");
			System.out.println("Message written by Controller from Host to LAN"+destLanID);
		}
	}
	private void parseRouterMessage(String message){
		
	}
	
	public static void main(String[] args){
		//"host" id id id . . . id "router" id id . . . id "lan" id id . .  . id
		
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
