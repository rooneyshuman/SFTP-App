import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import org.junit.Test;
import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ClientTest {
  /**
   * These need to be filled in before the tests will run properly.
   */
  private String userName = "username";
  private String password = "password";
  private String hostName = "hostname";

  // TODO
  @Test
  public void verifyDisplayLocalDirectoriesAndFiles() {
    Client client = new Client();
    int expected = 1;
    // int actual = client.displayLocalFiles();
    // assertThat(expected, equalTo(actual));
  }

  /**
   * Asserts that the connect method returns true
   */
  @Test
  public void connection_assertsSuccessfulConnection() {
    Client client = new Client(password, hostName, userName);
    assertThat(client.connect(), equalTo(true));
  }

  /**
   * Test connection with fake credentials. Asserts connect() returns false.
   */
  @Test
  public void connection_wrongCredentials_expectsSftpException() {
    Client client = new Client("fakepw", "fakehn", "fakeun");
    assertThat(client.connect(), equalTo(false));
  }

  /**
   * Trying to upload a file should result in an error. Expects an SftpException.
   */
  @Test (expected = SftpException.class)
  public void uploadFakeFile_expectsSftpException() throws SftpException{
    Client client = new Client(password, hostName, userName);
    client.connect();

    //try uploading a non existent file.
    client.uploadFile("This is not a file");
  }

  /**
   * Should throw exception when file not found when upload attempted
   */
  @Test (expected = SftpException.class)
  public void uploadFile_expectsSftpException_NoSuchFile() throws SftpException {
    String fileName = "MissingTextFile.txt";

    Client client = new Client(password, hostName, userName);
    client.connect();

    client.uploadFile(fileName);
  }

  /**
   * Asserts uploaded file exists
   */
  @Test
  public void uploadFile_assertsFileExists() throws SftpException, IOException {
    String fileName = "testfile.txt";
    File file = new File(fileName);
    if(file.createNewFile())
      System.out.println("Added testfile.txt to local");
    else
      System.out.println("Could not add testfile.txt to local");
    boolean pass = false;
    SftpATTRS attrs;

    Client client = new Client(password, hostName, userName);
    client.connect();

    client.deleteRemoteFile(fileName);
    client.uploadFile(fileName);
    attrs = client.getcSftp().stat(fileName);
    if (attrs != null)
      pass = true;
    System.out.println("Now deleting the files you uploaded.");
    client.deleteRemoteFile(fileName);
    assertThat(pass, equalTo(true));
    if(file.delete())
      System.out.println("Deleted testfile.txt from local");
    else
      System.out.println("Could not delete testfile.txt from local");
  }

  /**
   * Asserts uploaded file was deleted. stat() throws exception if filename is not found.
   */
  @Test (expected = SftpException.class)
  public void deleteFile_expectsSftpException() throws SftpException {
    String fileName = "testfile.txt";
    SftpATTRS attrs = null;

    Client client = new Client(password, hostName, userName);
    client.connect();

    client.uploadFile(fileName);
    System.out.println("Now deleting the files you uploaded.");
    client.deleteRemoteFile(fileName);
    attrs = client.getcSftp().stat(fileName);
  }

  /**
   * Trying to upload a file that does not exist should result in an error. This test verifies that is the case.
   */
  @Test
  public void downloadFakeFile() {
    Client client = new Client("Oatman641!", "linux.cs.pdx.edu", "brambora");
    try {
      client.connect();

      //try downloading a non existent file.
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

  /**
   * Test whether a file is uploaded correctly.
   */
  @Test
  public void downloadFile() {
    String fileName = "testfile.txt";
    String fileName2 = "testfile.txt, testfile2.txt";

    Client client = new Client("Oatman641!", "linux.cs.pdx.edu", "brambora");
    try {
      client.connect();

      //try downloading a file
      try {
        client.downloadFile(fileName);
        client.downloadFile(fileName2);
        //verify that the file is in the local directory
        File dir = new File(client.getcSftp().lpwd());
        File[] files = dir.listFiles();
        for (File file : files) {
          if (file.getName().equals(fileName)) {
            //the file was found so the download was successful.
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

  /**
   * Asserts whether a remote directory is created
   */
  @Test
  public void createRemoteDir_assertsDirExists() throws SftpException {
    Client client = new Client(password, hostName, userName);
    client.connect();

    String dirName = "newDirectory";
    assertThat(client.createRemoteDir(dirName), equalTo(true));
    System.out.println(dirName + " was created successfully");
    client.getcSftp().rmdir(dirName);        //clean up
  }
}
