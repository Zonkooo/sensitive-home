package francois;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur {

    public static void main ( String [] argv ){

        Socket sock = null;
        ServerSocket ss = null;
		try {
			ss = new ServerSocket(2004);
		} catch (IOException e) {
			e.printStackTrace();
		}
        while(true) {
            try {
				sock = ss.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
            new Connexion(sock).start();
            // on lance le thread de gestion de la connexion
        }
    }
}