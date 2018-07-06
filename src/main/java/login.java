import com.jcraft.jsch.*;

public class login {
    public static void main(String[] args) {
        System.out.print("Hello World");

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

            Session session = jsch.getSession("dantruth", "babbage.cs.pdx.edu", 22);
            session.connect();

            Channel channel =session.openChannel("sftp");

            channel.connect();

            System.out.print("Made a sftp connection");
        }
        catch (Exception e){
            System.out.print("Oh no. Something broke");
        }
    }

}
