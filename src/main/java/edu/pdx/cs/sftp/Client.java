package edu.pdx.cs.sftp;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;

import static java.lang.System.err;
import static java.lang.System.out;
import static java.lang.System.err;

/**
 * Represents the SSH File Transfer Protocol (SFTP) client. Supports the full security and
 * authentication functionality of SSH.
 */
public class Client {
  private Scanner scanner = new Scanner(System.in);
  private static final int TIMEOUT =
      60_000; // Set default timeout to 60 seconds to accommodate slow servers
  private User user;
  private JSch jsch;
  private Session session;
  private ChannelSftp channelSftp;
  private Logger logger;

  /** Class constructor. */
  public Client() {
    user = new User();
    jsch = new JSch();
    session = null;
    channelSftp = new ChannelSftp();
    logger = new Logger();
  }

  /**
   * Class constructor specifying the username, password, and hostname required to create a
   * connection to the server.
   *
   * @param username the input containing the user's login name
   * @param password the input containing the user's password
   * @param hostname the hostname specified by the user (e.g. linux.cs.pdx.edu)
   */
  public Client(String username, String password, String hostname) {
    user = new User(username, password, hostname);
    jsch = new JSch();
    session = null;
    channelSftp = new ChannelSftp();
    logger = new Logger();
  }

  /** Prompts the user to enter connection information such as username, password, and hostname. */
  void promptConnectionInfo() {
    user.getUsername();
    user.getPassword();
    user.getHostname();
  }

  /**
   * Establishes a connection to an SSH server containing a channel connected to an SFTP server.
   *
   * @return <code>true</code> on successful connection to the SSH/SFTP server; <code>false</code>
   *     otherwise.
   */
  public boolean connect() {
    if (createSshConnection() && createSftpChannel()) {
      logger.log("Successfully connected to the SSH/SFTP server");
      out.println("Successfully connected to the SSH/SFTP server");
      return true;
    } else {
      logger.log("Error occurred when attempting to connect to the SSH/SFTP server");
      return false;
    }
  }

  /**
   * Creates a session, which represents a connection to an SSH server.
   *
   * <p>One session can contain multiple channels of various types.
   *
   * @return <code>true</code> on successful creation of session object representing a connection to
   *     an SSH server; <code>false</code> otherwise.
   */
  protected boolean createSshConnection() {
    try {
      session = jsch.getSession(user.username, user.hostname, 22);
      session.setPassword(user.password);
      Properties config = new Properties();
      // Do not verify public key of the SSH/SFTP server; connecting within a trusted network
      config.put("StrictHostKeyChecking", "no");
      session.setConfig(config);
      session.connect(TIMEOUT);
      logger.log(
          String.format("Establishing a connection for %s@%s...", user.username, user.hostname));
      out.println("Establishing a connection...");
    } catch (JSchException e) {
      out.println("Failed to connect to the server");
      logger.log("Exception occurred when attempting to connect to the SSH server");
      return false;
    }
    return true;
  }

  /**
   * Creates a channel connected to an SFTP server (as a subsystem of the SSH server), which
   * supports the client side of the SFTP protocol.
   *
   * @return <code>true</code> on creating a channel connected to an SFTP server; <code>false</code>
   *     otherwise.
   */
  protected boolean createSftpChannel() {
    try {
      Channel channel = session.openChannel("sftp");
      channel.setInputStream(null);
      channel.connect(TIMEOUT);
      channelSftp = (ChannelSftp) channel;
      logger.log("Establishing a connection to the SFTP server");
    } catch (JSchException e) {
      out.println("Failed to establish the SFTP connection");
      logger.log("Exception occurred when attempting to connect to the SFTP server");
      return false;
    }
    return true;
  }

  /**
   * Terminates the connection to the SSH/SFTP server by first exiting the SFTP channel, then
   * closing the session maintaining the connection to the server.
   */
  void disconnect() {
    channelSftp.exit();
    session.disconnect();

    logger.log("SFTP connection closed");
    logger.save(user.username + "@" + user.hostname);

    out.println("SFTP connection closed");
    out.println("A log file has been saved to your local Downloads directory");
  }

  /** @return a Channel object connected to an SFTP server. */
  ChannelSftp getChannelSftp() {
    return channelSftp;
  }

  /** @return a Session object representing a connection to an SSH server. */
  Session getSession() {
    return session;
  }

  /**
   * Lists all directories and files in the user's current local directory.
   *
   * @return <code>true</code> if directories and/or files in the user's current local directory are
   *     listed; otherwise, return <code>false</code> to indicate the user's current local directory
   *     is empty.
   */
  boolean displayLocalFiles() {
    logger.log("displayLocalFiles called");
    printLocalWorkingDir();

    File dir = new File(channelSftp.lpwd());
    File[] files = dir.listFiles();

    if (files != null) {
      Arrays.sort(files);
      for (File file : files) {
        out.println(file.getName());
      }
      out.println();
      return true;
    } else {
      out.println("Your current directory is empty.");
      return false;
    }
  }

  /**
   * Lists all directories and files in the user's current remote directory.
   *
   * @return <code>true</code> if directories and/or files in the user's current remote directory
   *     are listed; otherwise, return <code>false</code> to indicate the user's current remote
   *     directory is empty.
   */
  boolean displayRemoteFiles() {
    logger.log("displayRemoteFiles called");

    try {
      printRemoteWorkingDir();
      Vector remoteDir = channelSftp.ls(channelSftp.pwd());
      if (remoteDir != null) {
        for (int i = 0; i < remoteDir.size(); ++i) {
          Object dirEntry = remoteDir.elementAt(i);
          if (dirEntry instanceof ChannelSftp.LsEntry)
            out.println(((ChannelSftp.LsEntry) dirEntry).getFilename());
        }
        out.println();
        return true;
      } else {
        out.println("Your current directory is empty.");
      }
    } catch (SftpException e) {
      err.println("Error displaying remote files");
    }
    return false;
  }

  /**
   * Creates a new remote directory. If the directory does not exist, it is created. If the
   * directory already exists, it asks the user whether they wish to overwrite the directory.
   */
  void createRemoteDir() {
    logger.log("createRemoteDir called");
    boolean dirNotCreated = true;
    SftpATTRS attrs = null;
    String answer;
    String dirName;

    while (dirNotCreated) {
      out.println("Enter the name of the new directory: ");
      dirName = scanner.next();
      try {
        // Retrieve attributes of the directory
        attrs = channelSftp.stat(dirName);
      } catch (Exception e) {
        out.println("A directory by this name doesn't exist; it will now be created");
      }
      // Directory does not exist
      if (attrs == null) {
        if (createRemoteDir(dirName)) {
          out.println(dirName + " has been created");
        }
        dirNotCreated = false;
      } else {
        // Directory already exists
        out.println("A directory by this name already exists. Overwrite? (yes/no)");
        answer = scanner.next();
        // Reset attributes of the remote file manipulated by SFTP
        attrs = null;
        if (answer.equalsIgnoreCase("yes")) {
          try {
            channelSftp.rmdir(dirName);
            if (createRemoteDir(dirName)) {
              out.println(dirName + " has been overwritten");
            }
            dirNotCreated = false;
          } catch (SftpException e) {
            err.println("Error overwriting directory");
          }
        }
      }
    }
  }

  /**
   * Creates a new remote directory in the user's current path.
   *
   * @param dirName the name of the new directory to be created
   * @return <code>true</code> if the file was successfully created; <code>false</code> otherwise.
   */
  boolean createRemoteDir(String dirName) {
    SftpATTRS attrs = null;
    try {
      channelSftp.mkdir(dirName);
      // Retrieve attributes of the directory
      attrs = channelSftp.stat(dirName);
    } catch (Exception e) {
      err.println("Error creating directory");
    }
    return attrs != null;
  }

  /** Print current working local path */
  void printLocalWorkingDir() {
    logger.log("printLocalWorkingDir called");
    String lpwd = channelSftp.lpwd();
    out.println("This is your current local working directory: " + lpwd + "\n");
  }

  /** Print current working remote path */
  void printRemoteWorkingDir() throws SftpException {
    logger.log("printRemoteWorkingDir called");
    String pwd = channelSftp.pwd();
    out.println("This is your current remote working directory: " + pwd + "\n");
  }

  /** Wrapper for changing current working local path */
  void changeLocalWorkingDir() {
    logger.log("changeLocalWorkingDir called");
    String newDir;
    String lpwd = channelSftp.lpwd();
    out.println("This is your current local working directory: " + lpwd + "\n");
    out.println("Enter the path of the directory you'd like to change to: ");
    newDir = scanner.next();
    if (changeLocalWorkingDir(newDir)) {
      lpwd = channelSftp.lpwd();
      out.println("This is your new current local working directory: " + lpwd + "\n");
    }
  }

  /**
   * Called by changeLocalWorkingDir() to change current working local path
   *
   * @param newDir -- String of the new path name
   * @return true if successful
   */
  boolean changeLocalWorkingDir(String newDir) {
    boolean pass = false;
    try {
      channelSftp.lcd(newDir);
      pass = true;
    } catch (SftpException e) {
      out.println("Error changing your directory");
    }
    return pass;
  }

  /** Change current working remote path */
  void changeRemoteWorkingDir() throws SftpException {
    logger.log("changeRemoteWorkingDir called");
    String newDir;
    out.println("This is your current remote working directory: " + channelSftp.pwd() + "\n");
    out.println("Enter the path of the directory you'd like to change to: ");
    newDir = scanner.next();
    if (changeRemoteWorkingDir(newDir))
      out.println("This is your new current remote working directory: " + channelSftp.pwd() + "\n");
  }

  /**
   * Called by changeRemoteWorkingDir() to change current working remote path
   *
   * @param newDirPath -- String of new path name
   */
  boolean changeRemoteWorkingDir(String newDirPath) {
    boolean pass = false;
    try {
      channelSftp.cd(newDirPath);
      pass = true;
    } catch (SftpException e) {
      System.out.println("Error changing your directory");
    }
    return pass;
  }

  /**
   * Uploads file(s) to the current working remote directory from the current working local
   * directory.
   *
   * @param filename -- The string containing the name(s) of the file(s) you wish to work with.
   * @throws SftpException -- General errors/exceptions
   */
  void uploadFile(String filename) throws SftpException {
    logger.log("uploadFile called w/ argument '" + filename + "'");
    if (filename.contains(",")) {
      // multiple files are wanted.

      // take the string and separate out the files.
      String removeWhitespace = filename.replaceAll("\\s", "");
      String[] arr = removeWhitespace.split(",");
      String pwd = channelSftp.pwd();
      StringBuilder sb = new StringBuilder();
      for (String file : arr) {
        channelSftp.put(file, file);
        sb.append(file);
        sb.append(" has been uploaded to: ");
        sb.append(pwd);
        sb.append("\n");
      }
      String output = sb.toString();
      out.println(output);
    } else {
      channelSftp.put(filename, filename);
      String pwd = channelSftp.pwd();
      out.println(filename + " has been uploaded to: " + pwd);
    }
  }

  /**
   * Downloads file(s) from the current working remote directory to the current working local
   * directory.
   *
   * @param filename -- The string containing the name(s) of the file(s) you wish to work with.
   * @throws SftpException -- General errors/exceptions
   */
  void downloadFile(String filename) throws SftpException {
    logger.log("downloadFile called w/ argument '" + filename + "'");
    if (filename.contains(",")) {
      // multiple files are wanted.

      // take the string and separate out the files.
      String removeWhitespace = filename.replaceAll("\\s", "");
      String[] arr = removeWhitespace.split(",");
      String lpwd = channelSftp.lpwd();
      StringBuilder sb = new StringBuilder();
      for (String file : arr) {
        channelSftp.get(file, file);
        sb.append(file);
        sb.append(" has been downloaded to: ");
        sb.append(lpwd);
        sb.append("\n");
      }
      String output = sb.toString();
      out.println(output);
    } else {
      channelSftp.get(filename, filename);
      String lpwd = channelSftp.lpwd();
      out.println("The file has been downloaded to: " + lpwd);
    }
  }

  /** Rename local files/directories */
  void renameLocal() {
    boolean repeat = true;
    String input;
    while (repeat) {
      // get original file name
      String oldFilename = "";
      while (oldFilename.equals("")) {
        out.println("Enter the original local file name (e.g., file.txt or directoryName): ");
        oldFilename = scanner.nextLine();
        if (oldFilename.equals("")) // check for empty input
        System.err.println("You did not enter a file name.");
      }
      File originalFile = new File(channelSftp.lpwd() + "/" + oldFilename);
      // get the new file name
      String newFilename = "";
      while (newFilename.equals("")) {
        out.println("Enter the new local file name (e.g., file.txt or directoryName): ");
        newFilename = scanner.nextLine();
        if (newFilename.equals("")) // check for empty input
        System.err.println(("You did not enter a file name."));
      }
      File renamedFile = new File(channelSftp.lpwd() + "/" + newFilename);
      // check for a duplicate file/directory name
      boolean rename = false;
      if (renamedFile.exists()) {
        out.println("A file or directory by this name already exists. Overwrite? (yes/no)");
        input = scanner.next();
        if ((input.equalsIgnoreCase("yes") || (input.equalsIgnoreCase("y")))) {
          rename = true;
        }
      } else {
        rename = true;
      }
      if (rename) {
        if (originalFile.renameTo(renamedFile)) {
          out.println(oldFilename + " has been renamed to: " + newFilename + "\n");
        } else {
          out.println("Error: rename unsuccessful.\n");
        }
        repeat = false;
      }
    }
  }

  /*
  /**
   * Executes a command on the remote server.
   *
   * @param command -- The text command that you'd like to execute. (Ex: "ls -a" or "cd mydirectory")
   */
  /*
  void remoteExec(String command) {
    logger.log("remoteExec called w/ argument '" + command + "'");
    try {
      Channel channel = session.openChannel("Exec");
      ((ChannelExec) channel).setCommand(command);
      channel.setInputStream(null);
      ((ChannelExec) channel).setErrStream(System.err);


      channel.connect();
      InputStream input = channel.getInputStream();
      try {
        InputStreamReader inputReader = new InputStreamReader(input);
        BufferedReader bufferedReader = new BufferedReader(inputReader);
        String line;

        while ((line = bufferedReader.readLine()) != null) {
          System.out.println(line);
        }
        bufferedReader.close();
        inputReader.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }

      channel.disconnect();
      session.disconnect();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  */

  /** Rename file/directory on remote server */
  void renameRemote() throws SftpException {
    boolean repeat = true;
    String input;
    SftpATTRS attrs = null;
    while (repeat) {
      // get original file name
      String oldFilename = "";
      while (oldFilename.equals("")) {
        out.println("Enter the original remote file name (e.g., file.txt or directoryName): ");
        oldFilename = scanner.nextLine();
        if (oldFilename.equals("")) // check for empty input
        System.err.println("You did not enter a file name.");
      }
      String oldFilePath = channelSftp.pwd() + "/" + oldFilename;
      // get the new file name
      String newFilename = "";
      while (newFilename.equals("")) {
        out.println("Enter the new remote file name (e.g., file.txt or directoryName): ");
        newFilename = scanner.nextLine();
        if (newFilename.equals("")) // check for empty input
        System.err.println(("You did not enter a file name."));
      }
      String newFilePath = channelSftp.pwd() + "/" + newFilename;
      try {
        attrs = channelSftp.stat(newFilePath);
      } catch (Exception e) {
        out.println();
      }
      boolean rename = false;
      if (attrs != null) {
        out.println("A file or directory by this name already exists. Overwrite? (yes/no)");
        input = scanner.next();
        attrs = null;
        if (input.equalsIgnoreCase("yes") || (input.equalsIgnoreCase("y"))) rename = true;
      } else {
        rename = true;
      }
      if (rename) {
        try {
          channelSftp.rename(oldFilePath, newFilePath);
          out.println(oldFilename + " has been renamed to: " + newFilename);
        } catch (SftpException e) {
          out.println("Error: rename unsuccessful.");
        }
        repeat = false;
      }
    }
  }

  /**
   * Creates a directory in the user's current path. If a directory with the same name already
   * exists, the user is prompted to determine whether or not to overwrite it.
   *
   * @param dirName the string containing the name of the directory to be created.
   */
  void createLocalDir(String dirName) {
    logger.log("createLocalDir called");
    File newDir = new File(channelSftp.lpwd() + "/" + dirName);

    if (newDir.exists()) {
      out.println("A directory by this name exists. Overwrite? (yes/no)");
      if (scanner.next().equalsIgnoreCase("yes")) {
        // Overwrite existing directory by deleting and then recreating it
        if (newDir.delete())
          try {
            newDir.mkdir();
            out.println(dirName + " has been overwritten");
          } catch (Exception e) {
            out.println("Error creating directory");
          }
        else out.println("Error overwriting directory");
      }
    } else {
      try {
        newDir.mkdir();
        out.println(dirName + " has been created");
      } catch (Exception e) {
        out.println("Error creating directory.");
      }
    }
  }

  /** Displays log history to user and logs being invoked */
  void displayLogHistory() {
    logger.display();
    logger.log("displayLogHistory called");
  }

  /**
   * Deletes the specified file(s) from the remote server. Multiple file names can be included
   * through a comma-separated list.
   *
   * @param filename the string containing the name(s) of the file(s) to be deleted.
   */
  void deleteRemoteFile(String filename) {
    logger.log("deleteRemoteFile called");

    String workingDir;
    if (filename.contains(",")) {
      // Delete multiple files. Parse list of file names into string array and trim whitespace.
      String[] filesToDelete = filename.replaceAll("\\s", "").split(",");

      try {
        workingDir = channelSftp.pwd();
        for (String file : filesToDelete) {
          channelSftp.rm(file);
          out.println(file + " has been deleted from: " + workingDir);
        }
      } catch (Exception e) {
        out.println("Error deleting remote files.");
      }

    } else {
      // Only one file to delete.
      try {
        channelSftp.rm(filename);
        workingDir = channelSftp.pwd();
        out.println(filename + " has been deleted from: " + workingDir);
      } catch (Exception e) {
        out.println("Error deleting remote files.");
      }
    }
  }
}
