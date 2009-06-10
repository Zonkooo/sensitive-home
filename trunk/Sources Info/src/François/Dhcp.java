package François;

import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import com.developpez.adiguba.shell.Shell;

public class Dhcp {

	
	public static void rechercheAdresseXport(){
		Shell sh = new Shell(); 
		GregorianCalendar calendar = new GregorianCalendar();
		String resultLog = "pas de retour";
		try {
			resultLog = sh.command("tail /var/log/syslog | grep ker").consumeAsString();
			if(resultLog.equals("s")){
				System.out.println(calendar.getTime()+" -> pas de requetes reçues par le DHCP");
			} else {
				StringTokenizer stLog = new StringTokenizer(resultLog);
				while (stLog.hasMoreTokens()) {
					
					//On regarde si la mac qui tente une requete dhcp est un module xport
					//si oui on l'ajoute au dhcpd.conf
					String stCouranteLog = stLog.nextToken();
					//taille d'une adresse MAC
					if(stCouranteLog.length()==17){
						System.out.println(stCouranteLog);
						if(stCouranteLog.substring(0, 8).equals("00:20:4A")){
							
						}
						//on regarde si la MAC existe déjà dans le dhcpd.conf
						//si oui on ne fait rien, sinon on l'ajoute au fichier 
						String resultDhcp = sh.command("cat /etc/dhcp3/dhcpd.conf | grep hardware").consumeAsString();
						StringTokenizer stDhcp = new StringTokenizer(resultDhcp);
						while (stDhcp.hasMoreTokens()) {
							String stCouranteDhcp = stDhcp.nextToken();
						if(stCouranteDhcp.equals("")){
							
						}
					}


			     }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		

//		DHCPDISCOVER from 00:1c:23:82:e2:f1 (Laptop) via eth0

//		XPORT MAC address: 00:20:4A:xx:xx:xx
	      
//	      host XPORT1  {
//	    	     hardware ethernet 00:06:9a:f3:07:01; fixed-address 192.168.0.10;
//	    	     }
	}
}
