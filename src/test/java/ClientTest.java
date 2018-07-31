import com.jcraft.jsch.SftpException;
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

  @Test
  public void renameRemoteFilesHappyPath() throws SftpException {
    Client client = new Client();
    boolean expected = false;
    boolean actual = client.renameRemoteFile("original", "rename");
    assertThat(actual, equalTo(expected));
  }
}
