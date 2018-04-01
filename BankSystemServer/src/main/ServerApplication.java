package main;

import java.io.IOException;
import java.net.SocketException;

import bank.Bank;
import services.BalanceUpdate;
import services.CreateAccountService;

public class ServerApplication {
	private static Server server;
	private static Bank bank;
	public static void main(String[] args){
		try {
			System.out.println("Starting server");
			bank = new Bank();
			server = new Server(8000);
			server.addServiceToServer(0, new CreateAccountService());
			server.addServiceToServer(2, new BalanceUpdate());
			server.start();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
