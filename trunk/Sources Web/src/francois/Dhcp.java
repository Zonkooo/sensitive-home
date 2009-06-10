package francois;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.developpez.adiguba.shell.Shell;


public class Dhcp {

	public static void main(String[] args) {
		rechercheAdresseXport();
	}

	public static void rechercheAdresseXport(){
		Shell sh = new Shell(); 
		GregorianCalendar calendar = new GregorianCalendar();
		String resultLog = "pas de retour";
		try {
			System.out.println(sh.command("/etc/init.d/dhcp3-server start").consumeAsString());
			System.out.println(sh.command("whoami").consumeAsString());
			resultLog = sh.command("tail /var/log/syslog").consumeAsString();
			if(resultLog.equals("")){
				System.out.println(calendar.getTime()+" -> pas de requetes reçues par le DHCP");
			} else {
				StringTokenizer stLog = new StringTokenizer(resultLog);
				while (stLog.hasMoreTokens()) {

					//On regarde si la mac qui tente une requete dhcp est un module xport
					//si oui on l'ajoute au dhcpd.conf
					String stCouranteLog = stLog.nextToken();
					//longueur d'une adresse MAC
					if(stCouranteLog.length()==17){
						if(stCouranteLog.substring(0, 8).equals("00:20:4A")){
							//on regarde si la MAC existe déjà dans le dhcpd.conf
							//si oui on ne fait rien, sinon on l'ajoute au fichier 
							String resultDhcp = sh.command("cat /etc/dhcp3/dhcpd.conf | grep hardware").consumeAsString();
							int numeroXPORTaAjouter=0;
							//on regarde si la chaine est déjà enregistrée
							Pattern p = Pattern.compile(stCouranteLog);
							Matcher m = p.matcher(resultDhcp);
							boolean addressExist = false;
							// lancement de la recherche
							while(m.find()) {
								System.out.println("'" + m.group() + "' trouvé à " + m.start() + " fin à " + m.end());
								addressExist = true;
							}

							if(addressExist){
								System.out.println("on fait rien");
							}
							else {
								System.out.println("l'adresse n'existe pas, on va la rajouter");
								//on cherche le numéro du module XPORT à ajouter
								String resultXPORT = sh.command("cat /etc/dhcp3/dhcpd.conf | grep XPORT").consumeAsString();
								StringTokenizer stXPORT = new StringTokenizer(resultXPORT);
								while (stXPORT.hasMoreTokens()) {
									String stCouranteXPORT = stXPORT.nextToken();
									if(stCouranteXPORT.length()>5){
										numeroXPORTaAjouter = Integer.parseInt(stCouranteXPORT.substring(5, 6)) + 1;
									}
								}
								//on écrit dans le dhcpd.conf
								try {
									FileWriter fw = new FileWriter ("/etc/dhcp3/dhcpd.conf",true);
									BufferedWriter bw = new BufferedWriter (fw);
									PrintWriter fichierSortie = new PrintWriter (bw);
									fichierSortie.println("host XPORT"+numeroXPORTaAjouter+"  {\n	hardware ethernet "+stCouranteLog+" ; fixed-address 192.168.0.1"+numeroXPORTaAjouter+";\n}");
									fichierSortie.close();
									System.out.println(sh.command("/etc/init.d/dhcp3-server").consumeAsString());
								}
								//On relance le serveur dhcp pour attribuer l'adresse ip au nouveau module xport
								catch (Exception e){
									System.out.println(e.toString());
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
