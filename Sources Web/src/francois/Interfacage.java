package francois;

import gestion_profils.Maison;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import sensitive.Bouton;
import sensitive.Capture;

public class Interfacage implements ActionListener{

	
	public static void main(String[] args) {
		ArrayList<Bouton> btns = new ArrayList<Bouton>();
		Capture capture = new Capture();
		Bouton bouton =new Bouton(capture);
		btns.add(bouton);
		
		new Interfacage(bouton);
		while(true){
			Bouton.traiterSignal(capture.getTap(), btns);			
		}
	}
	
	public Interfacage(Bouton bouton) {
		
		bouton.addListener(this);
	}
	
	public void actionPerformed(ActionEvent e) {
		e.get ?
		Maison.getMaison().switchProfil();
		
		
	}

	
}
