import com.jcraft.jsch.*;

import java.util.Scanner;

public class login {
    public static void main(String[] args) {
        login test = new login();
        test.create_connection();

        System.out.print("Finished");
    }

    public Boolean truth() {
        return true;
    }

    public void create_connection() {
        JSch jsch = new JSch();

        try {
            newUser user = new newUser();
            String username = user.getUsername();
            String password = user.getPassword();
            String hostname = user.getHostname();
            Session session = jsch.getSession(username, hostname, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            System.out.println("Establishing Connection...");
            session.connect();

            Channel channel =session.openChannel("sftp");

            channel.connect();

            System.out.println("Made a sftp connection");
        }
        catch (Exception e){
            System.out.println("Oh no. Something broke");
        }
    }

    public static class newUser {
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

}
