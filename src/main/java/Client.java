import com.jcraft.jsch.*;

import java.io.File;
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
  int displayLocalFiles() {
    File dir = new File(cSftp.lpwd());
    printLocalWorkingDir();
    File[] files = dir.listFiles();
    if(files != null) {
      int count = 0;
      for (File file : files) {
        if (count == 5) {
          count = 0;
          out.println();
        }
        out.print(file.getName() + "    ");
        ++count;
      }
      out.println("\n");
    }
    return 1;
  }

  /**
   * Lists all directories and files on the user's remote machine.
   */
  void displayRemoteFiles() throws SftpException {
    printRemoteWorkingDir();
    Vector remoteDir = cSftp.ls(cSftp.pwd());
    if (remoteDir != null) {
      int count = 0;
      for (int i = 0; i < remoteDir.size(); ++i) {
        if (count == 5) {
          count = 0;
          out.println();
        }
        Object dirEntry = remoteDir.elementAt(i);
        if (dirEntry instanceof ChannelSftp.LsEntry)
          out.print(((ChannelSftp.LsEntry) dirEntry).getFilename() + "    ");
        ++count;
      }
      out.println("\n");
    }
  }

  /**
   * Create a directory on the user's remote machine.
   */
  void createRemoteDir() throws SftpException {
    out.println("Enter the name of the new directory: ");
    String newDir = scanner.next();
    cSftp.mkdir(newDir);
  }

  /**
   * Print current working local path
   */
  void printLocalWorkingDir() {
    String lpwd = cSftp.lpwd();
    out.println("This is your current local working directory: " + lpwd + "\n");
  }

  /**
   * Print current working remote path
   */
  void printRemoteWorkingDir() throws SftpException {
    String pwd = cSftp.pwd();
    out.println("This is your current remote working directory: " + pwd + "\n");
  }

  /**
   * Change current working local path
   */
  void changeLocalWorkingDir() throws SftpException {
    String newDir;
    String lpwd = cSftp.lpwd();
    out.println("This is your current local working directory: " + lpwd + "\n");
    out.println("Enter the path of the directory you'd like to change to: ");
    newDir = scanner.next();
    cSftp.lcd(newDir);
    lpwd = cSftp.lpwd();
    out.println("This is your new current local working directory: " + lpwd + "\n");
  }

  /**
   * Change current working remote path
   */
  void changeRemoteWorkingDir() throws SftpException {
    String newDir;
    String pwd = cSftp.pwd();
    out.println("This is your current local working directory: " + pwd + "\n");
    out.println("Enter the path of the directory you'd like to change to: ");
    newDir = scanner.next();
    cSftp.cd(newDir);
    pwd = cSftp.pwd();
    out.println("This is your new current local working directory: " + pwd + "\n");
  }

  /**
   * Upload file to current remote directory path
   */
  void uploadFile(String filename) throws SftpException {
    cSftp.put(filename, filename);
    String pwd = cSftp.pwd();
    out.println("The file has been uploaded to: " + pwd);
  }

  /**
   * Download file to current local directory path
   */
  void downloadFile(String filename) throws SftpException{
    cSftp.get(filename,filename);
    String lpwd = cSftp.lpwd();
    out.println("The file has been downloaded to: " + lpwd);
  }

  /**
   * Rename file on remote directory
   */
  void renameRemoteFile() throws SftpException {
    out.println("Enter the original file name: ");
    String filename = scanner.next();
    out.println("Enter the new file name: ");
    String newFilename = scanner.next();
    cSftp.rename(filename, newFilename);
    out.println(filename + " has been renamed to: " + newFilename + "\n");
  }

  /**
   * Wrapper for renaming local files/directories
   */
  void renameLocal() {
    System.out.println("Enter the original file name: ");
    String filename = scanner.nextLine();
    System.out.println("Enter the new file name: ");
    String newFilename = scanner.nextLine();
    if (renameLocalFile(filename, newFilename))
      out.println(filename + " has been renamed to: " + newFilename + "\n");
    else
      out.println("Error: rename unsuccessful");
  }

  /**
   * Rename file on local directory
   */
  boolean renameLocalFile(String filename, String newFilename) {
    File file = new File(filename);
    File newFile = new File(newFilename);

    if (file.renameTo(newFile)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Create a directory on the user's local machine.
   */
  void createLocalDir() {
	out.println("Enter the name of the new directory: ");
	String dirName = scanner.next();
    String path = cSftp.lpwd() + "/" + dirName;
    File newDir = new File(path);
    if (!newDir.mkdir())
      out.println("Error creating local directory.");
  }
}