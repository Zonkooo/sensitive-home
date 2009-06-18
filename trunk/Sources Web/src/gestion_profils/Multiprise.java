package gestion_profils;

import francois.Communication;

public class Multiprise extends Socle {
	private Communication communication;
	private String ip;

	public Multiprise(int ID, int taille, String adresse) {
		super(ID,taille);
		ip = adresse;
		//communication = new Communication(ip);
	}

	public Multiprise(int ID, Prise[] morceaux, String adresse) {
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

	public void sendMessage(String message) {
		for (int i = 0; i < message.length(); i++) {
			communication.sendSomeData((byte) message.charAt(i));
		}
	}
}
