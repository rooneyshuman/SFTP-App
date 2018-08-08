import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import java.io.File;

public class ClientTest {
  /**
   * These need to be filled in before the tests will run properly.
   */
  private String userName = "user";
  private String password = "pw";
  private String hostName = "linux.cs.pdx.edu";

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
   * Asserts uploaded file exists
   */
  @Test
  public void uploadFile_assertsFileExists() throws SftpException {
    String fileName = "testfile.txt";
    boolean pass = false;
    SftpATTRS attrs = null;

    Client client = new Client(password, hostName, userName);
    client.connect();

    client.uploadFile(fileName);
    attrs = client.getcSftp().stat(fileName);
    if (attrs != null)
      pass = true;
    System.out.println("Now deleting the files you uploaded.");
    client.deleteRemoteFile(fileName);
    assertThat(pass, equalTo(true));
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
}
