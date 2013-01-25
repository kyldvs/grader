package grader.server;

import grader.common.SubmissionProtos.Submission;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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
			InputStream in = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			while(true) {
				String line = br.readLine();
				if (end(line)) {
					break;
				} else {
					handle(line, in);
				}
			}
			br.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		
		IOUtils.closeQuietly(conn);
		log.info("Connection Closed");
	}
	
	private void handle(String line, InputStream in) {
		if (Submission.class.getName().equals(line)) {
			try {
				Submission sub = Submission.parseFrom(in);
				System.out.println("Grading: " + sub.getPath());
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		} else {
			System.out.println("Invalid command: " + line);
		}
	}
	
	private boolean end(String line) {
		return line == null;
	}

}
