import com.jcraft.jsch.*;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;


class Client {
  private static final int TIMEOUT = 10000;

  private void Sftp() {
    var jsch = new JSch();
    int option;
    var menu = new Menu();

    do {
      option = menu.mainMenu();
      if (option == 1) {
        try {
          var user = new User();
          String username = user.getUsername();
          String password = user.getPassword();
          String hostname = user.getHostname();
          Session session = jsch.getSession(username, hostname, 22);
          session.setPassword(password);

          Properties config = new Properties();
          config.put("StrictHostKeyChecking", "no");
          session.setConfig(config);

          System.out.println("Establishing Connection...");
          session.connect(TIMEOUT);

          Channel channel = session.openChannel("sftp");
          channel.setInputStream(null);
          channel.connect(TIMEOUT);
          ChannelSftp cSftp = (ChannelSftp) channel;

          System.out.println("Successful SFTP connection");

          do {
            option = menu.workingMenu();
            switch (option) {
              case 1: //list directories: local and remote option
                option = menu.displayFilesMenu();
                //List remote directories
                if (option == 1) {
                  System.out.println("Listing remote directories and files...");
                  displayRemoteFiles(cSftp);
                }

                //List local directories
                if (option == 2) {
                  System.out.println("Listing local directories and files...");
                  File currentDir = new File(".");
                  displayLocalFiles(currentDir);
                }
                break;

              case 2: //get file/files: which files, put where
                System.out.println("Getting files...");
                break;

              case 3: //put file/files: which files put where
                System.out.println("Putting Files...");
                break;

              case 4: //create directory: name?, where?
                System.out.println("Creating directories...");
                break;

              case 5: //delete file/directory
                System.out.println("Deleting directories...");
                break;

              case 6: //change permissions
                System.out.println("Changing permissions...");
                break;

              case 7: //copy directory
                System.out.println("Copying directories...");
                break;

              case 8: //rename file
                System.out.println("Renaming files...");
                break;

              case 9: //view log history
                System.out.println("Viewing log history...");
                break;

              case 10: //exit
                System.out.println("Closing connection...");
                cSftp.exit();
                session.disconnect();
                break;

              default:
                System.out.println("Try again");
                break;
            }
          } while (option != 10);
        } catch (Exception e) {
          System.err.println("Client error");
          e.printStackTrace();
          System.exit(1);
        }
      }
    } while (option != 2);
    System.out.println("Goodbye");
  }

  public static void main(String[] args) {
    var connection = new Client();
    connection.Sftp();
  }

  /**
   * Lists all directories and files on the user's local machine (from the current directory).
   */
  public static int displayLocalFiles(File dir) {
    try {
      File[] files = dir.listFiles();
      for (File file : files) {
        if (file.isDirectory()) {
          System.out.println("Directory: " + file.getCanonicalPath());
          displayLocalFiles(file);
        } else {
          System.out.println("     File: " + file.getCanonicalPath());
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
  private static int displayRemoteFiles(ChannelSftp cSftp) {
    try {
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
      return 1;
    } catch (SftpException e) {
      System.err.println(e.toString());
      return -1;
    }
  }
}

