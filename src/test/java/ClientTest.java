import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import org.junit.Test;

public class ClientTest {
  @Test
  public void test() {
    assertThat("default", equalTo("Default"));
  }
}
