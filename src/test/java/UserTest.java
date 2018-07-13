import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import org.junit.Test;

public class UserTest {
  User user = new User();

  @Test
  public void userStartsNull() {
    assertThat(user.password, equalTo(null));
    assertThat(user.hostname, equalTo(null));
    assertThat(user.username, equalTo(null));
  }

  @Test
  public void verifyHostNameDoesntAllowAt () {
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
  public void verifyHostNameCatchesUnderscore () {
    boolean valid = user.verifyHostName("linux_cs.pdx.edu");
    assertThat(valid, equalTo(false));
  }

  @Test
  public void verifyHostNameAllowsValid() {
    boolean valid = user.verifyHostName("linux.cs.pdx.edu");
    assertThat(valid, equalTo(true));
  }

  @Test
  public void verifyUsernameCatchesShortName() {
    boolean valid = user.verifyUsername("notVal");
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
    assertThat("Username was valid",valid);
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

  /*@Test
  public void getUsernamePrintsErrorEmpty() {

  }*/
}
