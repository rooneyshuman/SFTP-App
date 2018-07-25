import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import org.junit.Test;

public class MainTest {
  @Test
  public void defaultTest() {
    assertThat("Default",equalTo("Default"));
  }
}
