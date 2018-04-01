package main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;

import message.BytePacker;
import services.CallbackHandlerClass;
import services.Service;
import socket.NormalSocket;
import socket.Socket;

public class Server {
	private HashMap<Integer, Service> idToServiceMap;
	private Socket designatedSocket;
	private int portNumber;
	private String ipAddress;
	private final int bufferSize = 2048;
	private byte[] buffer;
	private CallbackHandlerClass callbackHandler;
	
	public Server(int portNumber) throws SocketException{
		this.idToServiceMap = new HashMap<>(); //Hashmap containing service id as key, service object as value
		this.portNumber = portNumber; //Port number server is listening at
		this.designatedSocket = new NormalSocket(new DatagramSocket(this.portNumber));
		this.buffer = new byte[bufferSize]; //Buffer to store received data
		this.callbackHandler = new CallbackHandlerClass(this.designatedSocket);
	}
	
	public void addServiceToServer(int id, Service service){
		if(!this.idToServiceMap.containsKey(id)){
			this.idToServiceMap.put(id, service);
			System.out.println("Service added");
		}
		else{
			System.out.printf("There is an existing service using service id %d, please use a different id.\n",id);
		}		
	}
	
	public void start() throws IOException{
		//Constantly blocks for new requests
		while(true){
			DatagramPacket p = receive(); /*Create DatagramPacket to receive requests from clients*/
			byte[] data = p.getData();
			InetAddress clientAddress = p.getAddress();
			int clientPortNumber = p.getPort();
			//Service ID from client is the first byte in the byte array sent from client
			int serviceRequested = data[0];
			Service service = null;
			if(idToServiceMap.containsKey(serviceRequested)){
				service = idToServiceMap.get(serviceRequested);
				BytePacker replyToRequest = service.handleService(clientAddress,clientPortNumber, data, this.designatedSocket);
				this.designatedSocket.send(replyToRequest, clientAddress, clientPortNumber); //send reply to client that made request
				this.callbackHandler.broadcast(replyToRequest); //send update to all clients that registered for auto-monitoring.
			}
			else{
				System.out.println("Invalid Service ID");
			}
		}
	}
	
	public DatagramPacket receive() throws IOException{
		Arrays.fill(buffer, (byte) 0);	//empty buffer
		DatagramPacket p = new DatagramPacket(buffer, buffer.length);
		System.out.println("Waiting for requests.....");
		this.designatedSocket.receive(p);
		System.out.println("Received request.");
		return p;
	}
}
