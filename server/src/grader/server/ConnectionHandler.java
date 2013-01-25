package grader.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConnectionHandler implements Runnable {

	private static final Logger log = LogManager.getLogger(ConnectionHandler.class);
	
	private final Socket conn;
	private final Server server;
	
	public ConnectionHandler(Socket conn, Server server) {
		this.conn = conn;
		this.server = server;
	}
	
	@Override
	public void run() {
		log.info("Connection Opened");
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while(true) {
				String line = br.readLine();
				handle(line);
				if (end(line)) {
					break;
				}
			}
			br.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		
		IOUtils.closeQuietly(conn);
		log.info("Connection Closed");
	}
	
	private void handle(String line) {
		System.out.println(line);
	}
	
	private boolean end(String line) {
		return line == null;
	}

}
