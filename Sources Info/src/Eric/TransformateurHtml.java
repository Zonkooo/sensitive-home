package eric;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

	

public class TransformateurHtml {
	private static String fichier_Html = "fichier_html.html";
	private static String fichier_sortie = "fichier_java.txt";
	public static void main(String[] args) {
		try {
			InputStream ips = new FileInputStream(fichier_Html);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			OutputStream ops = new FileOutputStream(fichier_sortie);
			OutputStreamWriter opsw = new OutputStreamWriter(ops);
			BufferedWriter  bw = new BufferedWriter(opsw);
			String ligne;
			ligne = br.readLine();
			while(ligne !=null){
				ligne=ligne.replace("\"", "\\\"");
				System.out.println(ligne);
				ligne = "out.println(\""+ligne+"\");\n";
				bw.write(ligne);
				ligne = br.readLine();
			}
			bw.close();
			br.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}
