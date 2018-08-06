import com.jcraft.jsch.*;

import java.io.*;
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
  void connect() throws JSchException {
    logger = new Logger();
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

    logger.log("Successful SFTP connection made");
    out.println("Successful SFTP connection");
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
  void displayRemoteFiles() throws SftpException {
    logger.log("displayRemoteFiles called");
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
            cSftp.mkdir(dirName);
            out.println(dirName + " has been overwritten");
            repeat = false;
          } catch (SftpException e) {
            out.println("Error overwriting file");
          }
        }
      } else {
        cSftp.mkdir(dirName);
        out.println(dirName + " has been created");
        repeat = false;
      }
    }
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
   * Change current working local path
   */
  void changeLocalWorkingDir() throws SftpException {
    logger.log("changeLocalWorkingDir called");
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
    logger.log("changeRemoteWorkingDir called");
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
   * Uploads file(s) to the current working remote directory from the current working local directory.
   *
   * @param filename -- The string containing the name(s) of the file(s) you wish to work with.
   * @throws SftpException -- General errors/exceptions
   */
  public int uploadFile(String filename) throws SftpException {
    logger.log("uploadFile called w/ argument '" + filename + "'");
    if (filename.contains(",")) {
      //multiple files are wanted.

      //take the string and separate out the files.
      String removeWhitespace = filename.replaceAll("\\s", "");
      String[] arr = removeWhitespace.split(",");
      String output = new String();
      String pwd = cSftp.pwd();
      for (String file : arr) {
        cSftp.put(file, file);
        output += file + " has been uploaded to: " + pwd + "\n";
      }
      out.println(output);
      return 1;
    } else {
      cSftp.put(filename, filename);
      String pwd = cSftp.pwd();
      out.println(filename + " has been uploaded to: " + pwd);
      return 1;
    }
  }

  /**
   * Downloads file(s) from the current working remote directory to the current working local directory.
   *
   * @param filename
   * @throws SftpException
   */
  public int downloadFile(String filename) throws SftpException {
    logger.log("downloadFile called w/ argument '" + filename + "'");
    if (filename.contains(",")) {
      //multiple files are wanted.

      //take the string and separate out the files.
      String removeWhitespace = filename.replaceAll("\\s", "");
      String[] arr = removeWhitespace.split(",");
      String output = new String();
      String lpwd = cSftp.lpwd();
      for (String file : arr) {
        cSftp.get(file, file);
        output += file + " has been downloaded to: " + lpwd + "\n";
      }
      out.println(output);
      return 1;
    } else {
      cSftp.get(filename, filename);
      String lpwd = cSftp.lpwd();
      out.println("The file has been downloaded to: " + lpwd);
      return 1;
    }
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
   * Rename local files/directories
   */
  void renameLocal() {
    boolean repeat = true;
    String input;
    while (repeat) {
      //get original file name
      out.println("Enter the original file or directory name (e.g., file.txt or directoryName): ");
      String filename = scanner.next();
      //append the file name to the current path
      String originalPath = cSftp.lpwd() + "/" + filename;
      File originalFile = new File(originalPath);
      //get the new file name
      out.println("Enter the new file or directory name (e.g., renamed.txt or directoryRenamed): ");
      String newFilename = scanner.next();
      //append the file name to the current path
      String renamePath = cSftp.lpwd() + "/" + newFilename;
      File renamedFile = new File(renamePath);
      //check for a duplicate file/directory name
      if (renamedFile.exists()) {
        out.println("A file or directory by this name already exists. Overwrite? (yes/no)");
        input = scanner.next();
        if ((input.equalsIgnoreCase("yes") || (input.equalsIgnoreCase("y")))) {
          if (originalFile.renameTo(renamedFile)) {
            out.println(filename + " has been overwritten.\n");
          } else {
            out.println("Error: rename unsuccessful.\n");
          }
          repeat = false;
        }
      }
      if (!renamedFile.exists()) {
        if (originalFile.renameTo(renamedFile)) {
          out.println(filename + " has been renamed to: " + newFilename + "\n");
        } else {
          out.println("Error: rename unsuccessful.\n");
        }
        repeat = false;
      }
    }
  }

  /**
   * Executes a command on the remote server.
   * @param command -- The text command that you'd like to execute. (Ex: "ls -a" or "cd mydirectory")
   */
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
        String line = null;

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

  /**
   * Rename file/directory on remote server
   */
  void renameRemote() throws SftpException {
    boolean repeat = true;
    String input;
    SftpATTRS attrs = null;
    while (repeat) {
      //get original file name
      out.println("Enter the original file name: ");
      String filename = scanner.next();
      //append the file name to the current path
      String originalPath = cSftp.pwd() + "/" + filename;
      File originalFile = new File(originalPath);
      //get the new file name
      out.println("Enter the new file name: ");
      String newFilename = scanner.next();
      //append the file name to the current path
      String renamedPath = cSftp.pwd() + "/" + newFilename;
      File renamedFile = new File(renamedPath);
      //convert file object to string to pass to JSch rename method
      String file = originalFile.toString();
      String renamed = renamedFile.toString();
      try {
        attrs = cSftp.stat(newFilename);
      } catch (Exception e) {
        out.println();
      }
      if (attrs != null) {
        out.println("A file or directory by this name already exists. Overwrite? (yes/no)");
        input = scanner.next();
        attrs = null;
        if (input.equalsIgnoreCase("yes") || (input.equalsIgnoreCase("y"))) {
          try {
            cSftp.rename(file, renamed);
            out.println(filename + " has been successfully overwritten.\n");
          } catch (SftpException e) {
            out.println("Error: rename unsuccessful.\n");
          }
          repeat = false;
        }
      } else {
        try {
          cSftp.rename(file, renamed);
          out.println(filename + " has been renamed to: " + newFilename + "\n");
        } catch (SftpException e) {
          out.println("Error: rename unsuccessful.\n");
        }
        repeat = false;
      }
    }
  }
  
  /**
   * Create a directory on the user's local machine.
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
          if (newDir.delete() && newDir.mkdir())
            out.println(dirName + " has been overwritten");
          else
            out.println("Error overwriting file");
          repeat = false;
        }
      } else {
        if (!newDir.mkdir())
          out.println("Error creating local directory.");
        out.println(dirName + " has been created");
        repeat = false;
      }
    }
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
  void deleteRemoteFile(String files){
    String pwd = new String();
    if (files.contains(",")) {
      //multiple files are wanted.
      //take the string and separate out the files.
      String removeWhitespace = files.replaceAll("\\s", "");
      String[] arr = removeWhitespace.split(",");
      String output = new String();
      try {
        pwd = cSftp.pwd();

        for (String file : arr) {
          cSftp.rm(file);
          output += file + " has been deleted from: " + pwd + "\n";
        }
      } catch (Exception e) {
      }
      out.println(output);
    } else {
      try {
        cSftp.rm(files);
        pwd = cSftp.pwd();
      } catch (Exception e) {
      }
      out.println("The file has been deleted from: " + pwd);
    }
  }
}