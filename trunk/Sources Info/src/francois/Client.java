package francois;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

	public static void main ( String [] argv ) throws Exception {
//		Socket echoSocket = new Socket("192.168.182.21",7);
		Socket echoSocket = new Socket("localhost",2004);
		PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),true);
		// true -> autoflush
		BufferedReader in = new BufferedReader( new InputStreamReader(echoSocket.getInputStream()));
		BufferedReader clavier = new BufferedReader( new InputStreamReader ( System.in));
		String s;
		while((s=clavier.readLine())!=null) {
			out.println(s);
			System.out.println(in.readLine());
		}echoSocket.close();
	}
}
