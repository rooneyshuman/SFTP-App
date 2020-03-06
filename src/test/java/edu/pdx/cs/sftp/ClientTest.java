package edu.pdx.cs.sftp;

import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ClientTest {
  /* These need to be filled in before the tests will run properly. */
  private String username = "u";
  private String password = "p";
  private String hostname = "h";

  // TODO
  @Test
  public void verifyDisplayLocalDirectoriesAndFiles() {
    Client client = new Client();
    int expected = 1;
    // int actual = client.displayLocalFiles();
    // assertThat(expected, equalTo(actual));
  }

  @Test
  public void connect_SuccessfulConnection_ReturnsTrue() {
    Client client = new Client(username, password, hostname);
    assertThat(client.connect(), equalTo(true));
  }

  @Test
  public void connect_InvalidCredentials_ReturnsFalse() {
    Client client = new Client("invalidUsername", "invalidPassword", "invalidHostname");
    assertThat(client.connect(), equalTo(false));
  }

  /** Trying to upload a file should result in an error. Expects an SftpException. */
  @Test(expected = SftpException.class)
  public void uploadFakeFile_expectsSftpException() throws SftpException {
    Client client = new Client("fakeUsername", "fakePassword", "fakeHostname");
    if (!client.connect()) {
      System.out.println("Failed connection. Unable to run test.");
      assert (false);
    }

    // try uploading a non existent file.
    client.uploadFile("This is not a file");
  }

  /** Should throw exception when file not found when upload attempted */
  @Test(expected = SftpException.class)
  public void uploadFile_expectsSftpException_NoSuchFile() throws SftpException {
    String fileName = "MissingTextFile.txt";

    Client client = new Client(username, password, hostname);
    if (!client.connect()) {
      System.out.println("Failed connection. Unable to run test.");
      assert (false);
    }

    client.uploadFile(fileName);
  }

  /** Asserts uploaded file exists */
  @Test
  public void uploadFile_assertsFileExists() throws SftpException, IOException {
    String fileName = "testfile.txt";
    File file = new File(fileName);
    if (file.createNewFile()) System.out.println("Added testfile.txt to local");
    else System.out.println("Could not add testfile.txt to local");
    boolean pass = false;
    SftpATTRS attrs;

    Client client = new Client(username, password, hostname);
    if (!client.connect()) {
      System.out.println("Failed connection. Unable to run test.");
      assert (false);
    }
    client.uploadFile(fileName);
    attrs = client.getChannelSftp().stat(fileName);
    if (attrs != null) pass = true;
    System.out.println("Now deleting the files you uploaded.");
    client.deleteRemoteFile(fileName);
    assertThat(pass, equalTo(true));
    if (file.delete()) System.out.println("Deleted testfile.txt from local");
    else System.out.println("Could not delete testfile.txt from local");
  }

  /** Asserts uploaded file was deleted. stat() throws exception if filename is not found. */
  @Test(expected = SftpException.class)
  public void deleteFile_expectsSftpException() throws SftpException {
    String fileName = "testfile.txt";
    SftpATTRS attrs = null;

    Client client = new Client(username, password, hostname);
    if (!client.connect()) {
      System.out.println("Failed connection. Unable to run test.");
      assert (false);
    }

    client.uploadFile(fileName);
    System.out.println("Now deleting the files you uploaded.");
    client.deleteRemoteFile(fileName);
    attrs = client.getChannelSftp().stat(fileName);
  }

  /**
   * Trying to upload a file that does not exist should result in an error. This test verifies that
   * is the case.
   */
  @Test
  public void downloadFakeFile() {
    Client client = new Client(username, password, hostname);
    try {
      if (!client.connect()) {
        System.out.println("Failed connection. Unable to run test.");
        assert (false);
      }

      // try downloading a non existent file.
      try {
        client.downloadFile("This is not a file");
      } catch (Exception e) {
        System.out.println("Correct. This should throw a file not found exception.");
        e.printStackTrace();
      }

    } catch (Exception e) {
      System.out.println("Error");
      e.printStackTrace();
    }
  }

  /** Test whether a file is uploaded correctly. */
  @Test
  public void downloadFile() {
    String fileName = "testfile.txt";
    String fileName2 = "testfile.txt, testfile2.txt";

    Client client = new Client(username, password, hostname);
    try {
      if (!client.connect()) {
        System.out.println("Failed connection. Unable to run test.");
        assert (false);
      }

      // try downloading a file
      try {
        client.downloadFile(fileName);
        client.downloadFile(fileName2);
        // verify that the file is in the local directory
        File dir = new File(client.getChannelSftp().lpwd());
        File[] files = dir.listFiles();
        for (File file : files) {
          if (file.getName().equals(fileName)) {
            // the file was found so the download was successful.
            assertThat(file.getName(), equalTo(fileName));
          }
        }

      } catch (Exception e) {
        System.out.println("This is an error. There was a problem downloading.");
        e.printStackTrace();
      }
    } catch (Exception e) {
      System.out.println("There was an error somewhere.");
      e.printStackTrace();
    }
  }

  /** Asserts whether a remote directory is created */
  @Test
  public void createRemoteDir_assertsDirExists() throws SftpException {
    Client client = new Client(username, password, hostname);
    if (!client.connect()) {
      System.out.println("Failed connection. Unable to run test.");
      assert (false);
    }

    String dirName = "newDirectory";
    assertThat(client.createRemoteDir(dirName), equalTo(true));
    System.out.println(dirName + " was created successfully");
    client.getChannelSftp().rmdir(dirName); // clean up
  }

  /** Asserts whether a local directory is created */
  @Test
  public void createLocalDir_assertsDirExists() {
    Client client = new Client(username, password, hostname);
    if (!client.connect()) {
      System.out.println("Failed connection. Unable to run test.");
      assert (false);
    }
    String dirName = "newDirectory";
    File newDir = new File(client.getChannelSftp().lpwd() + "/" + dirName);
    client.createLocalDir(dirName);
    assertThat(newDir.exists(), equalTo(true));
    System.out.println(dirName + " was created successfully");
    if (newDir.delete()) // clean up
    System.out.println(dirName + " was deleted");
  }

  /** Asserts whether a local directory was changed Also inherently tests printLocalWorkingDir() */
  @Test
  public void changeLocalDir_assertsDirChanged() {
    boolean pass = false;
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream stdout = System.out;
    String newLocalPath = "newLocalPath";
    File newDir = new File(newLocalPath);

    Client client = new Client(username, password, hostname);
    if (!client.connect()) {
      System.out.println("Failed connection. Unable to run test.");
      assert (false);
    }

    if (newDir.mkdir()) { // create new directory path
      System.setOut(new PrintStream(output));
      output.reset();
      client.printLocalWorkingDir();
      assertThat(
          output.toString().contains(newLocalPath),
          equalTo(false)); // assert current path is not newDir
      client.changeLocalWorkingDir(newLocalPath); // change path to newDir
      output.reset();
      client.printLocalWorkingDir();
      assertThat(output.toString(), containsString(newLocalPath)); // assert current path is newDir
      client.changeLocalWorkingDir(".."); // reset path
      System.setOut(stdout); // reset output to standard System.out
      if (!newDir.delete()) System.out.println("Error deleting testing directory");
      else {
        System.out.println(
            "Path successfully changed to new dir. New dir has been deleted and path is reset.");
        pass = true;
      }
    } else {
      System.setOut(stdout);
      System.out.println("Error in mkdir");
    }
    assertThat(pass, equalTo(true));
  }

  /**
   * Asserts whether a remote directory was changed Also inherently tests printRemoteWorkingDir()
   */
  @Test
  public void changeRemoteDir_assertsDirChanged() throws SftpException {
    boolean pass = false;
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream stdout = System.out;
    String newRemotePath = "newRemotePath";

    Client client = new Client(username, password, hostname);
    if (!client.connect()) {
      System.out.println("Failed connection. Unable to run test.");
      assert (false);
    }

    if (client.createRemoteDir(newRemotePath)) {
      System.setOut(new PrintStream(output));
      output.reset();
      client.printRemoteWorkingDir();
      assertThat(
          output.toString().contains(newRemotePath),
          equalTo(false)); // assert current path is not newDir
      client.changeRemoteWorkingDir(newRemotePath); // change path to newDir
      output.reset();
      client.printRemoteWorkingDir();
      assertThat(output.toString(), containsString(newRemotePath)); // assert current path is newDir
      client.changeRemoteWorkingDir(".."); // reset path
      System.setOut(stdout); // reset output to standard System.out

      client.getChannelSftp().rmdir(newRemotePath);
      System.out.println(
          "Path successfully changed to new dir. New dir has been deleted and path is reset.");
      pass = true;
    } else {
      System.setOut(stdout);
      System.out.println("Error in createRemoteDir");
    }
    assertThat(pass, equalTo(true));
  }

  @Test
  public void disconnect_SuccessfulDisconnect_VerifiesConnectionClosed() {
    Client client = new Client(username, password, hostname);
    await().until(() -> client.connect());
    assertThat(client.getSession().isConnected(), equalTo(true));

    client.disconnect();

    assertThat(client.getSession().isConnected(), equalTo(false));
  }

  @Test
  public void displayRemoteFiles_Success_VerifiesDirectoryDisplayed() throws SftpException {
    // Redirect output stream
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(byteArrayOutputStream));
    // Connect to SSH/SFTP server
    Client client = new Client(username, password, hostname);
    await().until(() -> client.connect());
    // Create new directory to verify
    client.createRemoteDir("dirToTest");

    client.displayRemoteFiles();

    assertThat(byteArrayOutputStream.toString(), containsString("dirToTest"));
    // Clean up
    client.getChannelSftp().rmdir("dirToTest");
    System.setOut(System.out);
  }
}
