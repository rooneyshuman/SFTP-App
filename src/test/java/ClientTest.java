import org.junit.Test;
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
    int expected = 1;
    // int actual = client.displayLocalFiles();
    // assertThat(expected, equalTo(actual));
  }
}
