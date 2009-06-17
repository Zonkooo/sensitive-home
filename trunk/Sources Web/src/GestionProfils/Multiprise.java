package GestionProfils;

import francois.Communication;

public class Multiprise extends Socle {
	Communication communication;
	String ip;

	public Multiprise(int ID, int taille, String adresse) {
		super(ID,taille);
		ip = adresse;
		communication = new Communication(ip);
	}

	public Multiprise(int ID, Prise[] morceaux, String adresse) {
		super(ID, morceaux);
		ip = adresse;
		communication = new Communication(ip);
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

	/*
	 * envoie un message à la multiprise de la forme /numéro de prise:valeur [0-255]\ /x:xxx\
	 */
	public void sendMessage(String message) {
		communication.addMessageToQueue(message);
	}
}
