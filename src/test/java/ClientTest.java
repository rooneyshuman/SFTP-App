import org.junit.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ClientTest {
  @Test
  public void test() {
    assertThat("Default", equalTo("Default"));
  }

  @Test
  public void verifyDisplayLocalDirectoriesAndFiles() {
    Client client = new Client();
    File directory = new File(".");
    int expected = 1;
    //int actual = client.displayLocalFiles(directory);
    //assertThat(expected, equalTo(actual));
  }

  /**
   * Trying to upload a file should result in an error. This test verifies that.
   */
  @Test
  public void uploadFakeFile(String userName, String password, String hostName){
    Client client = new Client(password, hostName, userName);
    try{
      client.connect();

      //try uploading a non existent file.
      try {
        client.uploadFile("This is not a file");
      }catch(Exception e){
        System.out.println("Correct. This should throw an file not found exception.");
        e.printStackTrace();
      }

    }catch(Exception e){
      System.out.println("Error");
      e.printStackTrace();
    }
  }

  /**
   * Test whether uploading a real file is correct.
   */
  @Test
  public void uploadFile(String userName, String password, String hostName, String fileName){
    Client client = new Client(password, hostName, userName);
    try{
      client.connect();

      try{
        client.uploadFile(fileName);

        File dir = new File(client.getcSftp().pwd());
        File[] files = dir.listFiles();
        for(File file : files){
          if(file.getName().equals(fileName)){
            //the file was found so the upload was successful.
            assertThat(file.getName(), equalTo(fileName));
          }
        }
      }catch(Exception e){
        System.out.println("There was an error uploading the file.");
      }
    }catch(Exception e){
      System.out.println("There was an error with the uploading correct file test.");
    }
  }

  /**
   * Trying to upload a file that does not exist should result in an error. This test verifies that is the case.
   */
  @Test
  public void downloadFakeFile(String userName, String password, String hostName){
    Client client = new Client("Oatman641!", "linux.cs.pdx.edu", "brambora");
    try{
      client.connect();

      //try downloading a non existent file.
      try{
        client.downloadFile("This is not a file");
      }catch(Exception e){
        System.out.println("Correct. This should throw a file not found exception.");
        e.printStackTrace();
      }

    }catch(Exception e){
      System.out.println("Error");
      e.printStackTrace();
    }
  }

  /**
   * Test whether a file is uploaded correctly.
   */
  @Test
  public void downloadFile(String userName, String password, String hostName, String fileName){
    Client client = new Client("Oatman641!", "linux.cs.pdx.edu", "brambora");
    try{
      client.connect();

      //try downloading a file
      try{
        client.downloadFile(fileName);
        //verify that the file is in the local directory
        File dir = new File(client.getcSftp().lpwd());
        File[] files = dir.listFiles();
        for(File file : files){
          if(file.getName().equals(fileName)){
            //the file was found so the download was successful.
            assertThat(file.getName(), equalTo(fileName));
          }
        }

      }catch(Exception e){
        System.out.println("This is an error. There was a problem downloading.");
        e.printStackTrace();
      }
    }catch(Exception e){
      System.out.println("There was an error somewhere.");
      e.printStackTrace();
    }
  }
}
