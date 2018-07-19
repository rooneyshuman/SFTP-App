import com.jcraft.jsch.*;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;


class Client {
  Scanner scanner = new Scanner(System.in);
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
  public void promptConnectionInfo() {
    user.getUsername();
    user.getPassword();
    user.getHostname();
  }

  /**
   * Initiates connection
   */
  public void connect() throws JSchException {
    session = jsch.getSession(user.username, user.hostname, 22);
    session.setPassword(user.password);
    Properties config = new Properties();
    config.put("StrictHostKeyChecking", "no");
    session.setConfig(config);

    System.out.println("Establishing Connection...");
    session.connect(TIMEOUT);

    Channel channel = session.openChannel("sftp");
    channel.setInputStream(null);
    channel.connect(TIMEOUT);
    cSftp = (ChannelSftp) channel;

    System.out.println("Successful SFTP connection");
  }

  /**
   * Terminates connection
   */
  public void disconnect() {
    cSftp.exit();
    session.disconnect();
  }

  /**
   * Lists all directories and files on the user's local machine (from the current directory).
   */
  public int displayLocalFiles(File dir) {
    try {
      File[] files = dir.listFiles();
      if(files != null) {
        for (File file : files) {
          if (file.isDirectory()) {
            System.out.println("Directory: " + file.getCanonicalPath());
            displayLocalFiles(file);
          } else {
            System.out.println("     File: " + file.getCanonicalPath());
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
  public void displayRemoteFiles() throws SftpException {
    String path = ".";
    Vector remoteDir = cSftp.ls(path);
    if (remoteDir != null) {
      for (int i = 0; i < remoteDir.size(); ++i) {
        Object dirEntry = remoteDir.elementAt(i);
        if (dirEntry instanceof ChannelSftp.LsEntry) {
          System.out.println(((ChannelSftp.LsEntry) dirEntry).getFilename());
        }
      }
    }
  }

  /**
   * Create a directory on the user's remote machine.
   */
  public void createRemoteDir () throws SftpException {
    System.out.println("Enter the name of the new directory: ");
    String newDir = scanner.next();
    cSftp.mkdir(newDir);
  }


}


