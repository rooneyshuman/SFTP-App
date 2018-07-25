import static java.lang.System.out;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.File;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;



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
  private void displayRemoteFiles() throws SftpException {
    printRemoteWorkingDir();
    String path = ".";
    Vector remoteDir = cSftp.ls(path);
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
  private void createRemoteDir () throws SftpException {
    out.println("Enter the name of the new directory: ");
    String newDir = scanner.next();
    cSftp.mkdir(newDir);
  }

  /**
   * Print current working local path
   */
  private void printLocalWorkingDir() {
    String lpwd = cSftp.lpwd();
    out.println("This is your current local working directory: " + lpwd + "\n");
  }

  /**
   * Print current working remote path
   */
  private void printRemoteWorkingDir() throws SftpException {
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
   * Switch statement that controls local and remote rename options based on localOrRemote() menu
   * @throws SftpException  from JSCH
   */
  void rename() throws SftpException {
    var menu = new Menu();
    int opt;
    do {
      opt = menu.localOrRemoteMenu("Rename");
      switch (opt) {
        case 1:
          System.out.println("View local directory");
          printLocalWorkingDir();
          break;
        case 2:
          System.out.println("View remote directory");
          printRemoteWorkingDir();
          break;
        case 3:
          System.out.println("Changed local directory");
          changeLocalWorkingDir();
          break;
        case 4:
          System.out.println("Changed remote directory");
          //change remote directory
          break;
        case 5:
          System.out.println("Rename local directory/file...");
          break;
        case 6:
          System.out.println("Rename remote directory/file...");
          break;
        case 7:
          break;
        default:
          System.err.println("You did not enter a valid option");
          break;
      }
    } while (opt != 7);
  }

  /**
   * Switch Statement that controls local and remote permission options based on localOrRemote() menu
   * @throws SftpException  from JSCH
   */
  void changePermission() throws SftpException {
    var menu = new Menu();
    int opt;
    do {
      opt = menu.localOrRemoteMenu("Change permissions");
      switch (opt) {
        case 1:
          System.out.println("View local directory");
          printLocalWorkingDir();
          break;
        case 2:
          System.out.println("View remote directory");
          printRemoteWorkingDir();
          break;
        case 3:
          System.out.println("Changed local directory");
          changeLocalWorkingDir();
          break;
        case 4:
          System.out.println("Changed remote directory");
          //change remote directory
          break;
        case 5:
          System.out.println("Change permissions local directory...");
          break;
        case 6:
          System.out.println("Change permissions remote directory...");
          break;
        case 7:
          break;
        default:
          System.err.println("You did not enter a valid option");
          break;
      }
    } while (opt != 7);
  }

  /**
   * Switch Statement that controls local and remote delete options based on localOrRemote() menu
   * @throws SftpException  from JSCH
   */
  void delete() throws SftpException {
    var menu = new Menu();
    int opt;
    do {
      opt = menu.localOrRemoteMenu("Delete");
      switch (opt) {
        case 1:
          System.out.println("View local directory");
          printLocalWorkingDir();
          break;
        case 2:
          System.out.println("View remote directory");
          printRemoteWorkingDir();
          break;
        case 3:
          System.out.println("Changed local directory");
          changeLocalWorkingDir();
          break;
        case 4:
          System.out.println("Changed remote directory");
          //change remote dir
          break;
        case 5:
          System.out.println("Delete local directory/file...");
          break;
        case 6:
          System.out.println("Delete remote directory/file...");
          break;
        case 7:
          break;
        default:
          System.err.println("You did not enter a valid option");
          break;
      }
    } while (opt != 7);
  }

  /**
   * Switch Statement that controls local and remote create dir options based on localOrRemote() menu
   * @throws SftpException  from JSCH
   */
  void createDirectory() throws SftpException {
    var menu = new Menu();
    int opt;
    do {
      opt = menu.localOrRemoteMenu("Create");
      switch (opt) {
        case 1:
          System.out.println("View local directory");
          printLocalWorkingDir();
          break;
        case 2:
          System.out.println("View remote directory");
          printRemoteWorkingDir();
          break;
        case 3:
          System.out.println("Changed local directory");
          changeLocalWorkingDir();
          break;
        case 4:
          System.out.println("Changed remote directory");
          //change remote directory
          break;
        case 5:
          System.out.println("Create local directory...");
          break;
        case 6:
          try {
            createRemoteDir();
          } catch (SftpException e) {
            System.err.println("Error creating new directory");
          }
          out.println("Your directory has been created");
          break;
        case 7:
          break;
        default:
          System.err.println("You did not enter a valid option");
          break;
      }
    } while (opt != 7);
  }

  /**
   * Switch Statement that controls local and remote list options based on localOrRemote() menu
   * @throws SftpException  from JSCH
   */
  void listDirectories() throws SftpException {
    var menu = new Menu();
    int opt;
    do {
      opt = menu.localOrRemoteMenu("List");
      switch (opt) {
        case 1:
          System.out.println("View local directory");
          printLocalWorkingDir();
          break;
        case 2:
          System.out.println("View remote directory");
          printRemoteWorkingDir();
          break;
        case 3:
          System.out.println("Changed local directory");
          changeLocalWorkingDir();
          break;
        case 4:
          System.out.println("Changed remote directory");
          //change remote directory
          break;
        case 5:
          System.out.println("Listing local directories and files...");
          File currentDir = new File(".");
          displayLocalFiles(currentDir);
          break;
        case 6:
          try {
            out.println("Listing remote directories and files...");
            displayRemoteFiles();
          } catch (SftpException e) {
            System.err.println("Error displaying remote files");
          }
          break;
        case 7:
          break;
        default:
          System.err.println("You did not enter a valid option");
          break;
      }
    } while (opt != 7);
  }

}