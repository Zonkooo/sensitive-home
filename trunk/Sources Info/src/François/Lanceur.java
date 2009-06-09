package François;



public class Lanceur {

	
	public static void main(String[] args){
		new Config("/home/cnous3/coding/PR302/Sensitive Home Info/src/François/config.xml");
		while(true){
		Dhcp.rechercheAdresseXport();
		}
	}
}
