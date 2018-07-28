import java.io.*;
import java.sql.Timestamp;
import java.util.*;

class Logger {
	private Queue<String> logHistory = new LinkedList<>();
	private Timestamp timestamp;

	void add(String log) {
		timestamp = new Timestamp(System.currentTimeMillis());
		logHistory.add(timestamp + log);
	}

	void display() {
		for (String log : logHistory) {
			System.out.println(log);
		}
	}

	void save() {
		timestamp = new Timestamp(System.currentTimeMillis());
		String filename = "Log History " + timestamp;
		try {
			FileOutputStream fout = new FileOutputStream(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
