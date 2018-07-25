import static java.lang.System.out;

public class Main {

  public static void main(String[] args) {
    Client client = new Client(); //init client class
    int option;
    var menu = new Menu();

    do {
      option = menu.mainMenu();
      switch (option) {
        case 1:
          try {
            client.promptConnectionInfo();
            client.connect();

            do {
              option = menu.workingMenu();
              switch (option) {
                case 1: //list directories: local and remote option
                  client.listDirectories();
                  break;

                case 2: //get file/files: which files, put where
                  out.println("Changing local directory...");
                  client.changeLocalWorkingDir(); // why is this here?
                  break;

                case 3: //put file/files: which files put where
                  out.println("Putting Files...");
                  break;

                case 4: //create remote directory in current dir: name
                  out.println("Creating directory...");
                  client.createDirectory();
                  break;

                case 5: //delete file/directory
                  out.println("Deleting directories...");
                  client.delete();
                  break;

                case 6: //change permissions
                  out.println("Changing permissions...");
                  client.changePermission();
                  break;

                case 7: //copy directory
                  out.println("Copying directories...");
                  break;

                case 8: //rename file
                  out.println("Renaming files...");
                  client.rename();
                  break;

                case 9: //view log history
                  out.println("Viewing log history...");
                  break;

                case 10: //exit
                  out.println("Closing connection...");
                  client.disconnect();
                  break;

                default:
                  System.err.println("You did not enter a valid option");
                  break;
              }
            } while (option != 10);
          } catch (Exception e) {
            System.err.println("Client error:" + e.getLocalizedMessage());
            e.printStackTrace();
            System.exit(1);
          }

        case 2:
          break;

        default:
          System.err.println("You did not enter a valid option");
          break;
      }
    } while (option != 2);
    out.println("Goodbye");

  }
}
