package edu.pdx.cs.sftp;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;

import static java.lang.System.out;

/**
 * Represents the SSH File Transfer Protocol (SFTP) client.
 * Supports the full security and authentication functionality of SSH.
 */
public class Client {
  private Scanner scanner = new Scanner(System.in);
  private static final int TIMEOUT = 10000;
  private User user;
  private JSch jsch;
  private Session session;
  private ChannelSftp cSftp;
  private Logger logger;

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
   * A constructor taking username, password and hostname to facilitate creating a connection quickly.
   *
   * @param password -- Your password
   * @param hostName -- Your host
   * @param userName -- Your username
   */
  public Client(String password, String hostName, String userName) {
    user = new User(password, hostName, userName);
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
  boolean connect() {
    logger = new Logger();
    try {
      session = jsch.getSession(user.username, user.hostname, 22);
      session.setPassword(user.password);
      Properties config = new Properties();
      config.put("StrictHostKeyChecking", "no");
      session.setConfig(config);
      logger.log("Establishing connection for " + user.username + "@" + user.hostname + "...");
      out.println("Establishing Connection...");
      session.connect(TIMEOUT);

      Channel channel = session.openChannel("sftp");
      channel.setInputStream(null);
      channel.connect(TIMEOUT);
      cSftp = (ChannelSftp) channel;
    } catch (JSchException e) {
      out.println("Connection failed.");
      return false;
    }

    logger.log("Successful SFTP connection made");
    out.println("Successful SFTP connection");
    return true;
  }

  /**
   * Terminates connection
   */
  void disconnect() {
    cSftp.exit();
    session.disconnect();
    logger.log("SFTP connection closed");
    logger.save(user.username + "@" + user.hostname);
  }

  /**
   * Simple getter for cSftp for use in test suite.
   *
   * @return -- returns the cSftp object.
   */
  ChannelSftp getcSftp() {
    return cSftp;
  }

  /**
   * Simple getter for session for use in test suite.
   *
   * @return -- returns the Session object.
   */
  Session getSession() {
    return session;
  }

  /**
   * Lists all directories and files on the user's local machine (from the current directory).
   */
  int displayLocalFiles() {
    logger.log("displayLocalFiles called");
    File dir = new File(cSftp.lpwd());
    printLocalWorkingDir();
    File[] files = dir.listFiles();
    if (files != null) {
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
  boolean displayRemoteFiles() {
    logger.log("displayRemoteFiles called");

    try {
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
      return true;
    } catch (SftpException e) {
      System.out.println("Error displaying remote files");
      return false;
    }
  }

  /**
   * Create a directory on the user's remote machine.
   */
  void createRemoteDir() {
    logger.log("createRemoteDir called");
    boolean repeat = true;
    String answer;
    String dirName;
    SftpATTRS attrs = null;

    while (repeat) {
      out.println("Enter the name of the new directory: ");
      dirName = scanner.next();
      try {
        attrs = cSftp.stat(dirName);
      } catch (Exception e) {
        out.println("A directory by this name doesn't exist, it will now be created.");
      }
      if (attrs != null) {            //directory exists
        out.println("A directory by this name exists. Overwrite? (yes/no)");
        answer = scanner.next();
        attrs = null;               //reset attrs for loop
        if (answer.equalsIgnoreCase("yes")) {
          try {
            cSftp.rmdir(dirName);
            if (createRemoteDir(dirName))
              out.println(dirName + " has been overwritten");
            repeat = false;
          } catch (SftpException e) {
            out.println("Error overwriting directory");
          }
        }
      } else {
        if (createRemoteDir(dirName))
          out.println(dirName + " has been created");
        repeat = false;
      }
    }
  }

  /**
   * Called by createRemoteDir() to make a new remote directory in current remote path
   *
   * @return true if file was successfully created
   */
  boolean createRemoteDir(String dirName) {
    SftpATTRS attrs = null;
    try {
      cSftp.mkdir(dirName);
      attrs = cSftp.stat(dirName);
    } catch (Exception e) {
      out.println("Error creating directory.");
    }
    return attrs != null;
  }

  /**
   * Print current working local path
   */
  void printLocalWorkingDir() {
    logger.log("printLocalWorkingDir called");
    String lpwd = cSftp.lpwd();
    out.println("This is your current local working directory: " + lpwd + "\n");
  }

  /**
   * Print current working remote path
   */
  void printRemoteWorkingDir() throws SftpException {
    logger.log("printRemoteWorkingDir called");
    String pwd = cSftp.pwd();
    out.println("This is your current remote working directory: " + pwd + "\n");
  }

  /**
   * Wrapper for changing current working local path
   */
  void changeLocalWorkingDir() {
    logger.log("changeLocalWorkingDir called");
    String newDir;
    String lpwd = cSftp.lpwd();
    out.println("This is your current local working directory: " + lpwd + "\n");
    out.println("Enter the path of the directory you'd like to change to: ");
    newDir = scanner.next();
    if (changeLocalWorkingDir(newDir)) {
      lpwd = cSftp.lpwd();
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
      cSftp.lcd(newDir);
      pass = true;
    } catch (SftpException e) {
      out.println("Error changing your directory");
    }
    return pass;
  }

  /**
   * Change current working remote path
   */
  void changeRemoteWorkingDir() throws SftpException {
    logger.log("changeRemoteWorkingDir called");
    String newDir;
    out.println("This is your current remote working directory: " + cSftp.pwd() + "\n");
    out.println("Enter the path of the directory you'd like to change to: ");
    newDir = scanner.next();
    if(changeRemoteWorkingDir(newDir))
      out.println("This is your new current remote working directory: " + cSftp.pwd() + "\n");
  }

  /**
   * Called by changeRemoteWorkingDir() to change current working remote path
   *
   * @param newDirPath -- String of new path name
   */
  boolean changeRemoteWorkingDir(String newDirPath) {
    boolean pass = false;
    try {
      cSftp.cd(newDirPath);
      pass = true;
    } catch (SftpException e) {
      System.out.println("Error changing your directory");
    }
    return pass;
  }

  /**
   * Uploads file(s) to the current working remote directory from the current working local directory.
   *
   * @param filename -- The string containing the name(s) of the file(s) you wish to work with.
   * @throws SftpException -- General errors/exceptions
   */
  void uploadFile(String filename) throws SftpException {
    logger.log("uploadFile called w/ argument '" + filename + "'");
    if (filename.contains(",")) {
      //multiple files are wanted.

      //take the string and separate out the files.
      String removeWhitespace = filename.replaceAll("\\s", "");
      String[] arr = removeWhitespace.split(",");
      String pwd = cSftp.pwd();
      StringBuilder sb = new StringBuilder();
      for (String file : arr) {
        cSftp.put(file, file);
        sb.append(file);
        sb.append(" has been uploaded to: ");
        sb.append(pwd);
        sb.append("\n");
      }
      String output = sb.toString();
      out.println(output);
    } else {
      cSftp.put(filename, filename);
      String pwd = cSftp.pwd();
      out.println(filename + " has been uploaded to: " + pwd);
    }
  }

  /**
   * Downloads file(s) from the current working remote directory to the current working local directory.
   *
   * @param filename -- The string containing the name(s) of the file(s) you wish to work with.
   * @throws SftpException -- General errors/exceptions
   */
  void downloadFile(String filename) throws SftpException {
    logger.log("downloadFile called w/ argument '" + filename + "'");
    if (filename.contains(",")) {
      //multiple files are wanted.

      //take the string and separate out the files.
      String removeWhitespace = filename.replaceAll("\\s", "");
      String[] arr = removeWhitespace.split(",");
      String lpwd = cSftp.lpwd();
      StringBuilder sb = new StringBuilder();
      for (String file : arr) {
        cSftp.get(file, file);
        sb.append(file);
        sb.append(" has been downloaded to: ");
        sb.append(lpwd);
        sb.append("\n");
      }
      String output = sb.toString();
      out.println(output);
    } else {
      cSftp.get(filename, filename);
      String lpwd = cSftp.lpwd();
      out.println("The file has been downloaded to: " + lpwd);
    }
  }

  /**
   * Rename local files/directories
   */
  void renameLocal() {
    boolean repeat = true;
    String input;
    while (repeat) {
      //get original file name
      String oldFilename = "";
      while(oldFilename.equals("")) {
        out.println("Enter the original local file name (e.g., file.txt or directoryName): ");
        oldFilename = scanner.nextLine();
        if(oldFilename.equals("")) //check for empty input
          System.err.println("You did not enter a file name.");
      }
      File originalFile = new File(cSftp.lpwd() + "/" + oldFilename);
      //get the new file name
      String newFilename = "";
      while(newFilename.equals("")) {
        out.println("Enter the new local file name (e.g., file.txt or directoryName): ");
        newFilename = scanner.nextLine();
        if(newFilename.equals("")) //check for empty input
          System.err.println(("You did not enter a file name."));
      }
      File renamedFile = new File(cSftp.lpwd() + "/" + newFilename);
      //check for a duplicate file/directory name
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

  /**
   * Rename file/directory on remote server
   */
  void renameRemote() throws SftpException {
    boolean repeat = true;
    String input;
    SftpATTRS attrs = null;
    while (repeat) {
      //get original file name
      String oldFilename = "";
      while(oldFilename.equals("")) {
        out.println("Enter the original remote file name (e.g., file.txt or directoryName): ");
        oldFilename = scanner.nextLine();
        if(oldFilename.equals("")) //check for empty input
          System.err.println("You did not enter a file name.");
      }
      String oldFilePath = cSftp.pwd() + "/" + oldFilename;
      //get the new file name
      String newFilename = "";
      while(newFilename.equals("")) {
        out.println("Enter the new remote file name (e.g., file.txt or directoryName): ");
        newFilename = scanner.nextLine();
        if(newFilename.equals("")) //check for empty input
          System.err.println(("You did not enter a file name."));
      }
      String newFilePath = cSftp.pwd() + "/" + newFilename;
      try {
        attrs = cSftp.stat(newFilePath);
      } catch (Exception e) {
        out.println();
      }
      boolean rename = false;
      if (attrs != null) {
        out.println("A file or directory by this name already exists. Overwrite? (yes/no)");
        input = scanner.next();
        attrs = null;
        if (input.equalsIgnoreCase("yes") || (input.equalsIgnoreCase("y")))
          rename = true;
      } else {
        rename = true;
      }
      if(rename) {
        try {
          cSftp.rename(oldFilePath, newFilePath);
          out.println(oldFilename + " has been renamed to: " + newFilename);
        } catch (SftpException e) {
          out.println("Error: rename unsuccessful.");
        }
        repeat = false;
      }
    }
  }

  /**
   * Wrapper to create a directory on the user's local machine.
   */
  void createLocalDir() {
    logger.log("createLocalDir called");
    boolean repeat = true;
    String dirName;
    String answer;

    while (repeat) {
      out.println("Enter the name of the new directory: ");
      dirName = scanner.next();
      String path = cSftp.lpwd() + "/" + dirName;
      File newDir = new File(path);
      if (newDir.exists()) {          //directory exists
        out.println("A directory by this name exists. Overwrite? (yes/no)");
        answer = scanner.next();
        if (answer.equalsIgnoreCase("yes")) {
          if (newDir.delete() && createLocalDir(newDir))
            out.println(dirName + " has been overwritten");
          else
            out.println("Error overwriting file");
          repeat = false;
        }
      } else {
        if (!createLocalDir(newDir))
          out.println("Error creating local directory.");
        out.println(dirName + " has been created");
        repeat = false;
      }
    }
  }

  /**
   * Called by createLocalDir() to make a new local directory in current local path
   *
   * @param newDir -- File to create in current local path
   * @return true if file was successfully created
   */
  boolean createLocalDir(File newDir) {
    boolean pass = false;
    try {
      pass = newDir.mkdir();
    } catch (Exception e) {
      out.println("Error creating directory.");
    }
    return pass;
  }

  /**
   * Displays log history to user
   */
  void displayLogHistory() {
    logger.display();
    logger.log("displayLogHistory called");
  }

  /**
   * Deletes a file from the remote server. Can take one or multiple files in the format "testfile.txt, testfile2.txt"
   *
   * @param files -- The string read in main containing the names of the files.
   */
  void deleteRemoteFile(String files) {
    String pwd;
    if (files.contains(",")) {
      //multiple files are wanted.
      //take the string and separate out the files.
      String removeWhitespace = files.replaceAll("\\s", "");
      String[] arr = removeWhitespace.split(",");
      String output = "";
      try {
        pwd = cSftp.pwd();
        StringBuilder sb = new StringBuilder();
        for (String file : arr) {
          cSftp.rm(file);
          sb.append(file);
          sb.append(" has been deleted from:");
          sb.append(pwd);
          sb.append("\n");
        }
        output = sb.toString();
        out.println("The files have been deleted from: " + pwd);
      } catch (Exception e) {
        out.println("Error deleting remote files.");
      }
      out.println(output);
    } else {
      try {
        cSftp.rm(files);
        pwd = cSftp.pwd();
        out.println("The file has been deleted from: " + pwd);
      } catch (Exception e) {
        out.println("Error deleting remote files.");
      }
    }
  }
}