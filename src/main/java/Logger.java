import java.io.*;
import java.sql.Timestamp;
import java.util.*;

/**
 * Logger class for SFTP project.
 *
 * Used to record actions performed by the
 * program and save them to a .txt file upon
 * connection close.
 */
class Logger {
	/**
	 * data structure holding all logs as strings
	 */
	private Queue<String> logHistory = new LinkedList<>();
	/**
	 * field to hold a new timestamp when needed
	 */
	private Timestamp timestamp;

	/**
	 * produces a timestamp and appends the log message to it
	 * saving it to the Queue
	 *
	 * @param log <- represents the message recorded
	 */
	void log(String log) {
		timestamp = new Timestamp(System.currentTimeMillis());
		logHistory.add(timestamp + ": " + log);
	}

	/**
	 * displays each item in log chronologically
	 */
	void display() {
		for (String log : logHistory) {
			System.out.println(log);
		}
	}

	/**
	 * creates a new .txt file to store the logs in and saves it to
	 * the local machine's Downloads directory
	 *
	 * @param userhost used to name the file (i.e. username@hostmachine.org)
	 * @return
	 * 0 for success
	 * 1 for error
	 */
	int save(String userhost) {
		timestamp = new Timestamp(System.currentTimeMillis());
		String filename = "SFTP Log History - " + userhost + " " + timestamp + ".txt";
		BufferedWriter writer = null;
		try {
			String home = System.getProperty("user.home");
			writer = new BufferedWriter(new FileWriter(home + "/Downloads/" + filename));
			for (String log : logHistory) {
				writer.write(log + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return 1;
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
}
