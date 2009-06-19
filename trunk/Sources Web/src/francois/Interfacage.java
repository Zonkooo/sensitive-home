package francois;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import sensitive.Bouton;
import sensitive.Capture;

public class Interfacage implements ActionListener
{

	public static void main(String[] args)
	{
		Capture capture = new Capture();
		Interfacage flipflop = new Interfacage();
		
		Bouton bouton = new Bouton(capture);
		flipflop.addBouton(bouton);
		
		flipflop.runSensitive(capture);
	}

	private ArrayList<Bouton> btns;
	
	public Interfacage()
	{
		btns = new ArrayList<Bouton>();
	}
	
	public void addBouton(Bouton b)
	{
		this.btns.add(b);
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
		System.out.println("bouton " + e.getID());
	}
}
