import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class LoggerTest {
  private String test = "Test";

  @Test
  public void logPrintsCorrectly() {
    var log = new Logger();
    assertThat(log.logHistory.size(), equalTo(0));
    log.log(test);
    assertThat(log.logHistory.size(), equalTo(1));
  }

  @Test
  public void display() {
    OutputStream os = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(os);
    var log = new Logger(ps);
    log.log(test);
    log.display();
    assertThat(new SimpleDateFormat("MM/dd/yy hh:mm a").format(log.timestamp) + ": " + test + "\n",equalTo(os.toString()));
  }
}
