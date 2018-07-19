import com.jcraft.jsch.SftpException;
import java.io.File;

public class Main {
  public static void main(String[] args) {
    Client client = new Client(); //init client class
    int option;
    var menu = new Menu();

    do {
      option = menu.mainMenu();
      if (option == 1) {
        try {
          client.promptConnectionInfo();
          client.connect();

          do {
            option = menu.workingMenu();
            switch (option) {
              case 1: //list directories: local and remote option
                option = menu.displayFilesMenu();
                //List remote directories
                if (option == 1) {
                  try {
                    System.out.println("Listing remote directories and files...");
                    client.displayRemoteFiles();
                  } catch (SftpException e) {
                    System.out.println("Error displaying remote files");
                  }
                }

                //List local directories
                if (option == 2) {
                  System.out.println("Listing local directories and files...");
                  File currentDir = new File(".");
                  client.displayLocalFiles(currentDir);
                }


                break;

              case 2: //get file/files: which files, put where
                System.out.println("Getting files...");
                break;

              case 3: //put file/files: which files put where
                System.out.println("Putting Files...");
                break;

              case 4: //create remote directory in current dir: name
                System.out.println("Creating directory...");
                try {
                  client.createRemoteDir();
                } catch (SftpException e) {
                  System.out.println("Error creating new directory");
                }
                System.out.println("Your directory has been created");
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
                client.disconnect();
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
}
