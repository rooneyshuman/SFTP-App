import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import org.junit.Test;

public class UserTest {
  @Test
  public void userStartsNull() {
    User user = new User();
    assertThat(user.password, equalTo(null));
    assertThat(user.hostName, equalTo(null));
    assertThat(user.userName, equalTo(null));
  }

  @Test
  public void verifyUsernameCatchesShortName() {
    var user = new User();
    boolean valid = user.verifyUsername("notVal");
    assertThat(valid, equalTo(false));
  }

  @Test
  public void verifyUsernameCatchesLongName() {
    var user = new User();
    boolean valid = user.verifyUsername("thisIsWayToLongOfUserName");
    assertThat(valid, equalTo(false));
  }

  @Test
  public void verifyUserNameAllowsUnderscore() {
    var user = new User();
    boolean valid = user.verifyUsername("is_Valid");
    assertThat(valid, equalTo(true));
  }

  @Test
  public void verifyUserNameDoesntAllowDoubleUnderscore() {
    var user = new User();
    boolean valid = user.verifyUsername("not__Valid");
    assertThat(valid, equalTo(false));
  }

  @Test
  public void verifyUserNameAllowsPeriod() {
    var user = new User();
    boolean valid = user.verifyUsername("is.Valid");
    assertThat(valid, equalTo(true));
  }

  @Test
  public void verifyUserNameDoesntAllowDoublePeriod() {
    var user = new User();
    boolean valid = user.verifyUsername("not..Valid");
    assertThat(valid, equalTo(false));
  }

  @Test
  public void vefifyUserNameAllowsAlphaNumeric() {
    var user = new User();
    boolean valid = user.verifyUsername("ThisIsValid");
    assertThat("Username was valid",valid);
  }

  @Test
  public void verifyUserNameDoesntAllowLeadingPeriodOrUnderscore() {
    var user = new User();
    boolean valid = user.verifyUsername(".NotValid");
    assertThat(valid, equalTo(false));
    valid = user.verifyUsername("_NotValid");
    assertThat(valid, equalTo(false));
  }

  @Test
  public void verifyUserNameDoesntAllowEndingPeriodOrUnderscore() {
    var user = new User();
    boolean valid = user.verifyUsername("NotValid.");
    assertThat(valid, equalTo(false));
    valid = user.verifyUsername("notValid_");
    assertThat(valid, equalTo(false));
  }

}
