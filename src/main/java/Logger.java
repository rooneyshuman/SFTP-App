import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Logger class for SFTP project.
 *
 * Used to record actions performed by the
 * program and save them to a .txt file upon
 * connection close.
 */
class Logger {

	Logger(PrintStream test) {
		System.setOut(test);
	}

	Logger() {
	}

	/**
	 * data structure holding all logs as strings
	 */
	 Queue<String> logHistory = new LinkedList<>();

	/**
	 * field to hold a new timestamp when needed
	 */
	Date timestamp;

	/**
	 * produces a timestamp and appends the log message to it
	 * saving it to the Queue
	 *
	 * @param log <- represents the message recorded
	 */
	void log(String log) {
		timestamp = new Date(System.currentTimeMillis());
		logHistory.add(new SimpleDateFormat("MM/dd/yy HH:mm:ss").format(timestamp) + ": " + log);
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
	 */
	void save(String userhost) {
		timestamp = new Date(System.currentTimeMillis());
		String filename = "SFTP Log History - " + userhost + ".txt";
		try {
      var file = new File(System.getProperty("user.home") + "/Downloads/" + filename);
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			for (String log : logHistory) {
				writer.println(log);
			}
			writer.close();
		} catch (IOException e) {
			System.err.println(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
}
