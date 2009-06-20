package francois;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import sensitive.Bouton;
import sensitive.Capture;
import gestion_profils.AbstractProfil;
import gestion_profils.ProfilGlobal;

public class Interfacage extends Thread implements ActionListener
{
//	public static void main(String[] args)
//	{
//
//		Interfacage flipflop = new Interfacage();
//		flipflop.addBoutonToProfil((new ProfilGlobal("salon",20,20)), true);
//		flipflop.addBoutonToProfil((new ProfilGlobal("cuisine",20,20)), true);
//		//		flipflop.addBouton(bouton);
//
//		flipflop.runSensitive(flipflop.capture);
//	}

	private ArrayList<Bouton> btns;
	private ArrayList<AbstractProfil> profils;
	private Capture capture;

	public Interfacage()
	{
		btns = new ArrayList<Bouton>();
		profils = new ArrayList<AbstractProfil>();
		capture = new Capture();
//		runSensitive(capture);
	}
	
	public void run() {
		runSensitive(capture);
	}

	public void addBouton(Bouton b)
	{
		this.btns.add(b);
		b.addListener(this);
	}
	
	public void addProfil(AbstractProfil p)
	{
		this.profils.add(p);
	}

	public void runSensitive(Capture capture)
	{
		while (true)
		{
			Bouton.traiterSignal(capture.getTap(), btns);
		}

	}

	public void actionPerformed(ActionEvent e)
	{
//		System.out.println("bouton " + e.getID());
		System.out.println("changement de profil vers: " +  profils.get(e.getID()));
	}

	public void addBoutonToProfil(AbstractProfil profil, boolean veuxUnBouton) {
		//normalement l'index de l'arraylist de profils est le meme que pour les boutons
		addProfil(profil);
		if(veuxUnBouton) {
			addBouton(new Bouton(capture));
		}
	}
}
