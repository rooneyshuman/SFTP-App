import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class MenuTest {
  @Test
  public void mainMenuHappyPathOne() {
    var menu = new Menu("1");
    int ret = menu.mainMenu();
    assertThat(ret, equalTo(1));
  }

  @Test
  public void mainMenuHappyPathTwo() {
    var menu = new Menu("2");
    int ret = menu.mainMenu();
    assertThat(ret, equalTo(2));
  }

  @Test
  public void mainMenuReturnsZeroIfAlpha() {
    var menu = new Menu("a");
    int ret = menu.mainMenu();
    assertThat(ret, equalTo(0));
  }

  @Test
  public void workingMenuHappyPaths() {
    Menu menu;
    int ret;
    String input;
    for (int i = 1; i < 11; ++i) {
      input = "" + i;
      menu = new Menu(input);
      ret = menu.workingMenu();
      assertThat(ret, equalTo(i));
    }
  }

  @Test
  public void localOrRemoteMenuHappyPaths() {
    Menu menu;
    int ret;
    String input;
    for (int i = 1; i < 8; ++i) {
      input = "" + i;
      menu = new Menu(input);
      ret = menu.localOrRemoteMenu("test");
      assertThat(ret, equalTo(i));
    }
  }

  @Test
  public void intCheckReturnsInt() {
    var menu = new Menu("1");
    int result = menu.intCheck();
    assertThat(result, equalTo(1));
  }

  @Test
  public void intCheckReturnsZeroForAlpha() {
    var menu = new Menu("a");
    int result = menu.intCheck();
    assertThat(result, equalTo(0));
  }

  @Test
  public void paramaterizedConstructorGetsValue() {
    var menu = new Menu("A");
    String result = menu.sc.next();
    assertThat(result, equalTo("A"));
  }

  @Test
  public void defaultConstructorHappyPath() {
    new Menu();
  }

}
