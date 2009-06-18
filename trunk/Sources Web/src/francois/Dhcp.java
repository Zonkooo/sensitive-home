package francois;

import com.developpez.adiguba.shell.Shell;
import gestion_profils.Maison;
import gestion_profils.Multiprise;
import gestion_profils.Salle;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Dhcp {
	//TODO: virer le main qui ne sert que pour les tests

	public static void main(String[] args) {
		rechercheAdresseXport();
	}

	public static void rechercheAdresseXport(){
		Shell sh = new Shell(); 
		GregorianCalendar calendar = new GregorianCalendar();
		String resultLog = "pas de retour";
		try {
			resultLog = sh.command("tail /var/log/syslog | grep DHCPDISCOVER").consumeAsString();
			if(resultLog.equals("")){
				System.out.println(calendar.getTime()+" -> pas de requetes reçues par le DHCP");
			} else {
				System.out.println(resultLog);
				StringTokenizer stLog = new StringTokenizer(resultLog);
				while (stLog.hasMoreTokens()) {

					//On regarde si la mac qui tente une requete dhcp est un module xport
					//si oui on l'ajoute au dhcpd.conf
					String stCouranteLog = stLog.nextToken();
					//longueur d'une adresse MAC
					if(stCouranteLog.length()==17){
						if(stCouranteLog.substring(0, 8).equalsIgnoreCase("00:20:4A")){
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
									//TODO: vérifier que la salle où l'on ajoute les nouveaux éléments s'appelle vide
									for (Salle s : Maison.getMaison().getSalles()) {
										if(s.toString().equals("vide")){
											s.addMultiprise(new Multiprise(((Integer)numeroXPORTaAjouter).toString(),5,"192.168.0.1"+numeroXPORTaAjouter));
										}
									}
									//On relance le serveur dhcp pour attribuer l'adresse ip au nouveau module xport
									if(sh.command("sudo /etc/init.d/dhcp3-server force-reload").consumeAsString().equals(" * Starting DHCP server dhcpd3\n * check syslog for diagnostics.\n   ...fail!")){
										System.out.println("Serveur DHCP n'a pas réussi à se relancer");
									} else {
										System.out.println("Serveur DHCP relancé");
									}
								}
								catch (Exception e){
									System.out.println(e.toString());
								}
							}
						} else {
							System.out.println("Une requete a été reçue mais ne vient pas d'un module XPORT");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
