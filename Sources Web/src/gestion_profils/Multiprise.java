package gestion_profils;

import francois.Communication;

public class Multiprise extends Socle {
	private Communication communication;
	private String ip;

	public Multiprise(String ID, int taille, String adresse) {
		super(ID,taille);
		ip = adresse;
		communication = new Communication(ip);
		
		//TODO: seulement des tests
//		communication.addMessageToQueue("/REQ:0:001\\");
//		communication.addMessageToQueue("/REQ:2:255\\");
//		communication.addMessageToQueue("/001111111\\");
		//TODO: start est retiré pour les tests
		communication.start();

	}

	public Multiprise(String ID, Prise[] morceaux, String adresse) {
		super(ID, morceaux);
		ip = adresse;
		communication = new Communication(ip);
	}

	public Communication getCommunication() {
		return communication;
	}

	public void setCommunication(Communication communication) {
		this.communication = communication;
	}

	public String getIp() {
		return ip;
	}

	public void setPrise(Prise p) {
		super.setMorceau(p);
	}

	public Prise getPrise(int index) {
		return (Prise) (super.getMorceau(index));
	}

	public void setEtatPrise(Etat e, int index) {
		((Prise) (morceaux[index])).setEtat(e);
	}

	public void sendMessage(int prisePosition, int valeur) {
		String decalage="000";
		if(valeur<10) decalage="00";
		else if(valeur<100) decalage="0";
		communication.addMessageToQueue("/REQ:"+prisePosition+":"+decalage+valeur+"\\");
	}
}
