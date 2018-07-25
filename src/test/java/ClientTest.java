import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.io.File;
import org.junit.Test;

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
    int actual = client.displayLocalFiles(directory);
    assertThat(expected, equalTo(actual));
  }

}
