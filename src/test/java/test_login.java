import org.junit.Test;

public class test_login {

    @Test
    public void test_hello_world() {
        login login_class = new login();
        assert(login_class.truth());
    }

    @Test
    public void connectionShouldFail(){
        login login_class = new login();

    }
}
