package François;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connexion extends Thread {

	Socket sock;

	Connexion ( Socket s) { sock = s; }

	public void run() {

		PrintWriter out = null;
		BufferedReader in = null;
		try {

			out = new PrintWriter(sock.getOutputStream(),true);
			in = new BufferedReader( new InputStreamReader(sock.getInputStream()));

			String s = in.readLine();
			System.out.println("message reçu de "+sock.toString()+" : "+s);
			out.println(s); // la reponse

		} catch(Exception e) {
			System.out.println("MARCHE PAS!!!!!:");
			System.err.println(e);
		}

	}
}