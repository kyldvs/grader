package grader.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server implements Runnable {

	private static final Logger log = LogManager.getLogger(Server.class);
	
	public static final int PORT = 1332;

	public static void main(String[] args) throws IOException {
		new Thread(new Server(PORT)).start();
	}

	private final ServerSocket server;
	
	public Server() throws IOException {
		this(PORT);
	}
	
	public Server(int port) throws IOException {
		server = new ServerSocket(port);
	}
	
	@Override
	public void run() {
		log.info("Server Started");
		System.out.println("Server Started");
		
		while(true) {
			Socket conn;
			try {
				conn = server.accept();
			} catch (IOException e) {
				continue;
			}
			new Thread(new ConnectionHandler(conn, this)).start();
		}
	}
	
	public int getPort() {
		return server.getLocalPort();
	}
}
