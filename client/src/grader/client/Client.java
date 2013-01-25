package grader.client;

import grader.server.Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Client implements Runnable {

	private static final Logger log = LogManager.getLogger(Client.class); 
	
	public static void main(String[] args) {
		new Thread(new Client()).start();
	}
	
	private final String host;
	private final int port;
	
	public Client() {
		this("localhost", Server.PORT);
	}
	
	public Client(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public void run() {
		Socket conn = null;
		BufferedWriter out = null;
		try {
			conn = new Socket(host, port);
			out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
			
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			while(true) {
				String s = in.readLine();
				if (s == null || "q".equals(s)) {
					break;
				}
				out.write(s);
				out.newLine();
				out.flush();
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(conn);
		}
	}
	
	
	
}
