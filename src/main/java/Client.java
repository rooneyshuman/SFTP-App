import com.jcraft.jsch.*;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;
import static java.lang.System.out;


class Client {
  private Scanner scanner = new Scanner(System.in);
  private static final int TIMEOUT = 10000;
  private User user;
  private JSch jsch;
  private Session session;
  private ChannelSftp cSftp;

  /**
   * Class constructor
   */
  public Client() {
    user = new User();
    jsch = new JSch();
    session = null;
    cSftp = new ChannelSftp();
  }

  /**
   * Prompts for connection information
   */
  void promptConnectionInfo() {
    user.getUsername();
    user.getPassword();
    user.getHostname();
  }

  /**
   * Initiates connection
   */
  void connect() throws JSchException {
    session = jsch.getSession(user.username, user.hostname, 22);
    session.setPassword(user.password);
    Properties config = new Properties();
    config.put("StrictHostKeyChecking", "no");
    session.setConfig(config);

    out.println("Establishing Connection...");
    session.connect(TIMEOUT);

    Channel channel = session.openChannel("sftp");
    channel.setInputStream(null);
    channel.connect(TIMEOUT);
    cSftp = (ChannelSftp) channel;

    out.println("Successful SFTP connection");
  }

  /**
   * Terminates connection
   */
  void disconnect() {
    cSftp.exit();
    session.disconnect();
  }

  /**
   * Lists all directories and files on the user's local machine (from the current directory).
   */
  int displayLocalFiles(File dir) {
    try {
      printLocalWorkingDir();
      File[] files = dir.listFiles();
      if(files != null) {
        for (File file : files) {
          if (file.isDirectory()) {
            out.println("Directory: " + file.getCanonicalPath());
          } else {
            out.println("     File: " + file.getCanonicalPath());
          }
        }
      }
      return 1;
    } catch (IOException e) {
      e.printStackTrace();
      return -1;
    }
  }

  /**
   * Lists all directories and files on the user's remote machine.
   */
  void displayRemoteFiles() throws SftpException {
    printRemoteWorkingDir();
    String path = ".";
    Vector remoteDir = cSftp.ls(path);
    if (remoteDir != null) {
      for (int i = 0; i < remoteDir.size(); ++i) {
        Object dirEntry = remoteDir.elementAt(i);
        if (dirEntry instanceof ChannelSftp.LsEntry) {
          out.println(((ChannelSftp.LsEntry) dirEntry).getFilename());
        }
      }
    }
  }

  /**
   * Create a directory on the user's remote machine.
   */
  void createRemoteDir () throws SftpException {
    out.println("Enter the name of the new directory: ");
    String newDir = scanner.next();
    cSftp.mkdir(newDir);
  }

  /**
   * List current working local path
   */
  void printLocalWorkingDir() {
    String lpwd = cSftp.lpwd();
    out.println("This is your current local working directory: " + lpwd + "\n");
  }

  /**
   * List current working remote path
   */
  void printRemoteWorkingDir() throws SftpException {
    String pwd = cSftp.pwd();
    out.println("This is your current remote working directory: " + pwd + "\n");
  }



}


