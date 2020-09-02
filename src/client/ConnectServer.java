package client;

public class ConnectServer {

    public static boolean clientOpen(String host, int port) {
        if (Client.getInstance() == null) {
            System.out.println("host: " + host);
            System.out.println("port: " + String.valueOf(port));
            Client.createInstance(host, port);
            if (Client.getInstance() == null) {
                System.out.println("Connect Err");
                // client = null;
                return false;
            } else
                return true;
        } else
            return false;
    }

}