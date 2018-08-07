import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class UserTest {
  private User user = new User();

  @Test
  public void userStartsNull() {
    assertThat(user.password, equalTo(null));
    assertThat(user.hostname, equalTo(null));
    assertThat(user.username, equalTo(null));
  }

  @Test
  public void verifyHostNameDoesntAllowAt() {
    boolean valid = user.verifyHostName("@linux.cs.pdx.edu");
    assertThat(valid, equalTo(false));
  }

  @Test
  public void verifyHostNameDoesntAllowLeadingOrEndingPeriod() {
    boolean valid = user.verifyHostName(".linux.cs.pdx.edu");
    assertThat(valid, equalTo(false));
  }

  @Test
  public void verifyHostNameCatchesLongSegment() {
    boolean valid = user.verifyHostName("01234567890123456789012345678901234567890123456789012345678901234");
    assertThat(valid, equalTo(false));
  }

  @Test
  public void verifyHostNameCatchesUnderscore() {
    boolean valid = user.verifyHostName("linux_cs.pdx.edu");
    assertThat(valid, equalTo(false));
  }

  @Test
  public void verifyHostNameAllowsValid() {
    boolean valid = user.verifyHostName("linux.cs.pdx.edu");
    assertThat(valid, equalTo(true));
  }

  @Test
  public void verifyHostNameCatchesTooLong() {
    boolean valid = user.verifyHostName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    assertThat(valid, equalTo(false));
  }

  @Test
  public void verifyUsernameCatchesShortName() {
    boolean valid = user.verifyUsername("n");
    assertThat(valid, equalTo(false));
  }

  @Test
  public void verifyUsernameCatchesLongName() {
    boolean valid = user.verifyUsername("thisIsWayToLongOfUserName");
    assertThat(valid, equalTo(false));
  }

  @Test
  public void verifyUserNameAllowsUnderscore() {
    boolean valid = user.verifyUsername("is_Valid");
    assertThat(valid, equalTo(true));
  }

  @Test
  public void verifyUserNameDoesntAllowDoubleUnderscore() {
    boolean valid = user.verifyUsername("not__Valid");
    assertThat(valid, equalTo(false));
  }

  @Test
  public void verifyUserNameAllowsPeriod() {
    boolean valid = user.verifyUsername("is.Valid");
    assertThat(valid, equalTo(true));
  }

  @Test
  public void verifyUserNameDoesntAllowDoublePeriod() {
    boolean valid = user.verifyUsername("not..Valid");
    assertThat(valid, equalTo(false));
  }

  @Test
  public void vefifyUserNameAllowsAlphaNumeric() {
    boolean valid = user.verifyUsername("ThisIsValid");
    assertThat("Username was valid", valid);
  }

  @Test
  public void verifyUserNameDoesntAllowLeadingPeriodOrUnderscore() {
    boolean valid = user.verifyUsername(".NotValid");
    assertThat(valid, equalTo(false));
    valid = user.verifyUsername("_NotValid");
    assertThat(valid, equalTo(false));
  }

  @Test
  public void verifyUserNameDoesntAllowEndingPeriodOrUnderscore() {
    boolean valid = user.verifyUsername("NotValid.");
    assertThat(valid, equalTo(false));
    valid = user.verifyUsername("notValid_");
    assertThat(valid, equalTo(false));
  }

  @Test
  public void getUsernameHappyPath() {
    String name = "Brent";
    var user = new User(name);
    String result = user.getUsername();
    assertThat(result, equalTo(name));
  }

  @Test
  public void getUserNameUnHappyPath() {
    String name = "Bre..nt\nBrent";
    var user = new User(name);
    String result = user.getUsername();
    assertThat(result, equalTo("Brent"));
  }

  @Test
  public void getHostnameHappyPath() {
    String host = "linux.cs.pdx.edu";
    var user = new User(host);
    String result = user.getHostname();
    assertThat(result, equalTo(host));
  }

  @Test
  public void getHostnameUnHappyPath() {
    String host = "@linux.cs.pdx.edu\nlinux.cs.pdx.edu";
    var user = new User(host);
    String result = user.getHostname();
    assertThat(result, equalTo("linux.cs.pdx.edu"));
  }

  @Test
  public void getPasswordHappyPath() {
    String password = "PA$$w0rd";
    var user = new User(password);
    String result = user.getPassword();
    assertThat(result, equalTo(password));
  }

  @Test
  public void getPasswordUnHappyPath() {
    String password = "test this\nBrent";
    var user = new User(password);
    String result = user.getPassword();
    assertThat(result, equalTo("Brent"));
  }

  @Test
  public void getPasswordEmpytTest() {
    String password = "\nBrent";
    var user = new User(password);
    String result = user.getPassword();
    assertThat(result, equalTo("Brent"));
  }

  @Test
  public void paramaterizedConstructorWorks() {
    String validPass = "dafa";
    String validUser = "thisGuy";
    String validHost = "linux.cs.pdx.edu";
    var user = new User(validPass,validHost,validUser);
    assertThat(user.password, equalTo(validPass));
    assertThat(user.hostname, equalTo(validHost));
    assertThat(user.username, equalTo(validUser));
  }
}
