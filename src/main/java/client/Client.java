package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Client{
    private final Logger logger = LoggerFactory.getLogger(Client.class);
    private static Client instance;

    public synchronized static Client getInstance() {
        if (instance == null) {
            instance = new Client();
        }
        return instance;
    }

    public static void main(String[] args) {
        try {
            String keyFile = "./cert/client_keystore.p12";
            String keyPassword = "1234567";

            System.setProperty("javax.net.ssl.trustStore", keyFile);
            System.setProperty("javax.net.ssl.trustStorePassword", keyPassword);

            SSLSocketFactory clientFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) clientFactory.createSocket("localhost", 5000);

            var outStream = socket.getOutputStream();
            var inStream = socket.getInputStream();

            PrintWriter out = new PrintWriter(outStream, true);
            BufferedReader in = new BufferedReader(new InputStreamReader(inStream));

            out.println("Hola");
            String response = in.readLine();

            Thread.sleep(15000);

            out.println(response);

            System.out.println("Respuesta servidor " + response);

            System.out.println(in.readLine());
            Thread.sleep(1000);

            in.close();
            out.close();
            socket.close();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
