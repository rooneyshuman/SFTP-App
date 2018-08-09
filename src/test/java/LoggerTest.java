import org.junit.Test;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class LoggerTest {
  private String test = "Test";
  Logger log = new Logger();

  @Test
  public void logPrintsCorrectly() {
    assertThat(log.logHistory.size(), equalTo(0));
    log.log(test);
    assertThat(log.logHistory.size(), equalTo(1));
  }

  @Test
  public void displaySingleLogCorrectly() {
    OutputStream os = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(os);
    var log = new Logger(ps);
    log.log(test);
    log.display();
    assertThat(new SimpleDateFormat("MM/dd/yy HH:mm:ss").format(log.timestamp) + ": " + test + "\n",equalTo(os.toString()));
  }

  @Test
  public void saveCreatesCorrectFile() {
    log = new Logger();
    log.log(test);
    var timestamp = new Date(System.currentTimeMillis());
    String home = System.getProperty("user.home");
    String filename = "SFTP Log History - " + test + " " + new SimpleDateFormat("MM/dd/yy HH:mm").format(timestamp) + ".txt";
    File file = new File(home + "/Downloads/" + filename);
    log.save(test);
    assertThat(file.exists(), equalTo(true));
  }
}
