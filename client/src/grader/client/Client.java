package grader.client;

import grader.common.SubmissionProtos.Submission;
import grader.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Sets;

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
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(System.in));
			while(true) {
				String line = in.readLine();
				if (end(line)) {
					break;
				} else {
					handle(line, in);
				}
			}
			in.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			IOUtils.closeQuietly(in);
		}
	}

	private void handle(String line, BufferedReader in) {
		/*
		 *  TODO Better to open and close connections, or to hold onto one
		 *  throughout the life of the client?
		 *  
		 *  ProtoBuffers seem to only write to the output stream correctly
		 *  after it is closed. That's my initial reasoning for this behavior
		 */
		Socket conn = null;
		OutputStream out = null;
		
		if (Submission.class.getName().equals(line)) {
			try {
				conn = getConnection();
				out = conn.getOutputStream();
				Submission sub = Submission.newBuilder().setPath(in.readLine()).build();
				writeln(line, out);
				sub.writeTo(out);
				out.flush();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			} finally {
				IOUtils.closeQuietly(out);
				IOUtils.closeQuietly(conn);
			}
		}
	}

	private void writeln(String line, OutputStream out) throws IOException {
		out.write((line + "\r\n").getBytes());
		out.flush();
	}

	private Socket getConnection() throws IOException {
		return new Socket(host, port);
	}

	private static final Set<String> end = Sets.newHashSet("q", "quit", "exit");
	private boolean end(String line) {
		return line == null || end.contains(line.toLowerCase());
	}
}
