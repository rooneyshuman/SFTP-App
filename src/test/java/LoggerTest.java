import org.junit.Test;

import java.io.*;
import java.text.SimpleDateFormat;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class LoggerTest {
  private String test = "Test";
  private Logger log = new Logger();
  private String filename = "SFTP Log History - " + test + ".txt";

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
    log.log(test);
    log.save(test);
    var file = new File(System.getProperty("user.home") + "/Downloads/" + filename);
    assertThat(file.exists(), equalTo(true));
    assert(file.delete());
  }

  @Test
  public void saveWritesCorrectInfo() throws IOException {
    log = new Logger();
    String save = "This should be written to file.";
    log.log(save);
    log.save(test);
    File file = new File(System.getProperty("user.home") + "/Downloads/" + filename);
    var reader = new BufferedReader(new FileReader(file));
    String fromfile = reader.readLine();
    assertThat(fromfile,containsString(save));
    assert(file.delete());
  }
}
