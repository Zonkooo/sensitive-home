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
			     StringTokenizer st = new StringTokenizer("Roamed from BSSID 00:12:44:BB:01:70");
			     while (st.hasMoreTokens()) {
			         System.out.println(st.nextToken());
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
