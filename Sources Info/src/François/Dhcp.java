package François;

import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import com.developpez.adiguba.shell.Shell;

public class Dhcp {

	
	public static void rechercheAdresseXport(){
		Shell sh = new Shell(); 
		GregorianCalendar calendar = new GregorianCalendar();
		String result = "pas de retour";
		try {
			result = sh.command("tail /var/log/syslog | grep ker").consumeAsString();
			if(result.equals("s")){
				System.out.println(calendar.getTime()+" -> pas de requetes reçues par le DHCP");
			} else {
				StringTokenizer st = new StringTokenizer(result);
				while (st.hasMoreTokens()) {
					
					//On regarde si la mac qui tente une requete dhcp est un module xport
					//si oui on l'ajoute au dhcpd.conf
					String stCourante = st.nextToken();
					//taille d'une adresse MAC
					if(stCourante.length()==17){
						System.out.println(stCourante);
						if(stCourante.substring(0, 8).equals("00:20:4A")){
							
						}
						//on regarde si la MAC existe déjà dans le dhcpd.conf
						//si oui on ne fait rien, sinon on l'ajoute au fichier 
						
						if(stCourante.equals("")){
							
						}
					}


			     }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	      System.out.println(result);
		

//		DHCPDISCOVER from 00:1c:23:82:e2:f1 (Laptop) via eth0

//		XPORT MAC address: 00:20:4A:xx:xx:xx
	}
}
