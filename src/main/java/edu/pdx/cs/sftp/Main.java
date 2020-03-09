package edu.pdx.cs.sftp;

import com.jcraft.jsch.SftpException;

import java.util.Scanner;

import static java.lang.System.out;

public class Main {


  public static void main(String[] args) {
    Client client = new Client(); //init client class
    int option;
    var menu = new Menu();
    Scanner scanner = new Scanner(System.in);

    do {
      option = menu.mainMenu();

      switch (option) {
        case 1:
          try {
            client.promptConnectionInfo();
            if (client.connect()) {

              do {
                option = menu.workingMenu();
                switch (option) {
                  case 1: //list directories: local and remote option
                    listDirectories(client);
                    break;

                  case 2: //download a file
                    try {
                      out.println("Listing remote directories and files...");
                      client.displayRemoteFiles();
                      out.println("Please enter the name of the file(s) you'd like to download. Example usage: file1.txt, file2.txt");
                      String filename = scanner.nextLine();
                      client.downloadFile(filename);
                    } catch (SftpException e) {
                      out.println("Error downloading file");
                    }
                    break;

                  case 3: //upload a file
                    try {
                      out.println("Listing local directories and files...");
                      client.displayLocalFiles();
                      out.println("Please enter the name of the file(s) you'd like to download. Example usage: file1.txt, file2.txt");
                      String filename = scanner.nextLine();
                      client.uploadFile(filename);
                    } catch (SftpException e) {
                      out.println("Error uploading file");
                    }

                    break;

                  case 4: //create remote directory in current dir: name
                    out.println("Creating directory...");
                    createDirectory(client);
                    break;

                  case 5: //delete file/directory
                    out.println("Deleting directories...");
                    delete(client);
                    break;

                  case 6: //change permissions
                    out.println("Changing permissions...");
                    changePermission(client);
                    break;

                  case 7: //copy directory
                    out.println("Copying directories...");
                    break;

                  case 8: //rename file
                    out.println("Renaming files...");
                    rename(client);
                    break;

                  case 9: //view log history
                    out.println("Viewing log history...");
                    client.displayLogHistory();
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
            }
          } catch (Exception e) {
            System.err.println("Client error:" + e.getLocalizedMessage());
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

  /**
   * Switch statement that controls local and remote rename options based on localOrRemote() menu
   *
   * @throws SftpException from JSCH
   */
  private static void rename(Client client) throws SftpException {
    Scanner scanner = new Scanner(System.in);
    var menu = new Menu();
    int opt;

    do {
      opt = menu.localOrRemoteMenu("Rename");
      switch (opt) {
        case 1:
          System.out.println("View local directory");
          client.printLocalWorkingDir();
          break;
        case 2:
          System.out.println("View remote directory");
          client.printRemoteWorkingDir();
          break;
        case 3:
          System.out.println("Changed local directory");
          client.changeLocalWorkingDir();
          break;
        case 4:
          System.out.println("Changed remote directory");
          client.changeRemoteWorkingDir();
          break;
        case 5:
          System.out.println("Rename local directory/file...");
          client.displayLocalFiles();
          client.renameLocal();
          break;
        case 6:
          System.out.println("Rename remote directory/file...");
          client.displayRemoteFiles();
          try {
            out.println("Please enter the original name of the file to rename.");
            String oldFilename = scanner.nextLine();
            out.println("Please enter the new name of the file to rename.");
            String newFilename = scanner.nextLine();
            client.renameRemoteFile(oldFilename, newFilename);
          } catch (SftpException e) {
            out.println("Error renaming file");
          }
          break;

        case 7: //return to previous menu
          break;
        default:
          System.err.println("You did not enter a valid option");
          break;
      }
    } while (opt != 7);
  }

  /**
   * Switch Statement that controls local and remote permission options based on localOrRemote() menu
   *
   * @throws SftpException from JSCH
   */
  private static void changePermission(Client client) throws SftpException {
    var menu = new Menu();
    int opt;
    do {
      opt = menu.localOrRemoteMenu("Change permissions");
      switch (opt) {
        case 1:
          System.out.println("View local directory");
          client.printLocalWorkingDir();
          break;
        case 2:
          System.out.println("View remote directory");
          client.printRemoteWorkingDir();
          break;
        case 3:
          System.out.println("Changed local directory");
          client.changeLocalWorkingDir();
          break;
        case 4:
          System.out.println("Changed remote directory");
          client.changeRemoteWorkingDir();
          break;
        case 5:
          System.out.println("Change permissions local directory...");
          break;
        case 6:
          System.out.println("Change permissions remote directory...");
          break;
        case 7: //return to previous
          break;
        default:
          System.err.println("You did not enter a valid option");
          break;
      }
    } while (opt != 7);
  }

  /**
   * Switch Statement that controls local and remote delete options based on localOrRemote() menu
   *
   * @throws SftpException from JSCH
   */
  private static void delete(Client client) throws SftpException {
    var menu = new Menu();
    int opt;
    Scanner scanner = new Scanner(System.in);
    do {
      opt = menu.localOrRemoteMenu("Delete");
      switch (opt) {
        case 1:
          System.out.println("View local directory");
          client.printLocalWorkingDir();
          break;
        case 2:
          System.out.println("View remote directory");
          client.printRemoteWorkingDir();
          break;
        case 3:
          System.out.println("Changed local directory");
          client.changeLocalWorkingDir();
          break;
        case 4:
          System.out.println("Changed remote directory");
          client.changeRemoteWorkingDir();
          break;
        case 5:
          System.out.println("Delete local directory/file...");
          break;
        case 6:
          System.out.println("Delete remote directory/file...");
          out.println("Listing local directories and files...");
          client.displayRemoteFiles();
          out.println("Please enter the name of the file(s) you'd like to delete. Example usage: file1.txt, file2.txt");
          String filename = scanner.nextLine();
          client.deleteRemoteFile(filename);
          break;
        case 7: //return to previous menu
          break;
        default:
          System.err.println("You did not enter a valid option");
          break;
      }
    } while (opt != 7);
  }

  /**
   * Switch Statement that controls local and remote create dir options based on localOrRemote() menu
   *
   * @throws SftpException from JSCH
   */
  private static void createDirectory(Client client) throws SftpException {
    Scanner scanner = new Scanner(System.in);
    var menu = new Menu();
    int opt;
    do {
      opt = menu.localOrRemoteMenu("Create");
      switch (opt) {
        case 1:
          System.out.println("View local directory");
          client.printLocalWorkingDir();
          break;
        case 2:
          System.out.println("View remote directory");
          client.printRemoteWorkingDir();
          break;
        case 3:
          System.out.println("Changed local directory");
          client.changeLocalWorkingDir();
          break;
        case 4:
          System.out.println("Changed remote directory");
          client.changeRemoteWorkingDir();
          break;
        case 5:
          System.out.println("Create local directory...");
          out.println("Enter the name of the new directory: ");
          String dirName = scanner.nextLine();
          client.createLocalDir(dirName);
          break;
        case 6:
          client.createRemoteDir();
          out.println("Your directory has been created");
          break;
        case 7: //return to previous menu
          break;
        default:
          System.err.println("You did not enter a valid option");
          break;
      }
    } while (opt != 7);
  }

  /**
   * Switch Statement that controls local and remote list options based on localOrRemote() menu
   *
   * @throws SftpException from JSCH
   */
  private static void listDirectories(Client client) throws SftpException {
    var menu = new Menu();
    int opt;
    do {
      opt = menu.localOrRemoteMenu("List");
      switch (opt) {
        case 1:
          System.out.println("View local directory");
          client.printLocalWorkingDir();
          break;
        case 2:
          System.out.println("View remote directory");
          client.printRemoteWorkingDir();
          break;
        case 3:
          System.out.println("Changed local directory");
          client.changeLocalWorkingDir();
          break;
        case 4:
          System.out.println("Changed remote directory");
          client.changeRemoteWorkingDir();
          break;
        case 5:
          System.out.println("Listing local directories and files...");
          client.displayLocalFiles();
          break;
        case 6:
          out.println("Listing remote directories and files...");
          client.displayRemoteFiles();
          break;
        case 7: //return to previous menu
          break;
        default:
          System.err.println("You did not enter a valid option");
          break;
      }
    } while (opt != 7);
  }
}
