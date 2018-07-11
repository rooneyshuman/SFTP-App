import java.util.Scanner;

public class newUser {
    String pw;
    String un;
    String hn;
    Scanner scanner = new Scanner(System.in);
    String error = "Error";

    public boolean promptPassword() {
        System.out.println("Enter your password:");
        pw = scanner.next();
        if (pw == null || pw.isEmpty())
            return false;
        return true;
    }

    public String getPassword() {
        if (promptPassword())
            return pw;
        else
            return error;
    }

    public boolean promptUsername() {
        System.out.println("Enter your username:");
        un = scanner.next();
        if (un == null || un.isEmpty())
            return false;
        return true;
    }

    public String getUsername() {
        if (promptUsername())
            return un;
        else
            return error;
    }

    public boolean promptHostname() {
        System.out.println("Enter your hostname:");
        hn = scanner.next();
        if (hn == null || hn.isEmpty())
            return false;
        return true;
    }

    public String getHostname() {
        if (promptHostname())
            return hn;
        else
            return error;
    }
}

