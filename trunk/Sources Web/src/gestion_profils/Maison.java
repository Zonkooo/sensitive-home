/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gestion_profils;

import java.util.ArrayList;

/**
 *
 * @author raphael
 */
public class Maison 
{
	private static ProfilGlobal basicProfil = new ProfilGlobal("defaut", 18, 0);
	
	//Singleton
	private static Maison maison;	
	public static Maison getMaison()
	{
		if(maison == null)
			maison = new Maison();
		
		return maison;
	}
	
	private ArrayList<Salle> salles;
	private ArrayList<ProfilGlobal> availablesProfils;
	private ProfilGlobal currentProfil;
	
	private Maison()
	{
		this.salles = new ArrayList<Salle>();
		this.availablesProfils = new ArrayList<ProfilGlobal>();
		
		this.availablesProfils.add(basicProfil);
		this.currentProfil = basicProfil;
	}
	
	public void addSalle(Salle s)
	{
		this.salles.add(s);
	}
	
	public void removeSalle(Salle s)
	{
		this.salles.remove(s);
	}

	public ArrayList<ProfilGlobal> getAvailablesProfils()
	{
		return availablesProfils;
	}

	public void setCurrentProfil(ProfilGlobal currentProfil) {
		this.currentProfil = currentProfil;
		for (Salle salle : salles)
		{
			salle.setProfilGlobal(currentProfil);
		}
	}

	public ProfilGlobal getCurrentProfil()
	{
		return currentProfil;
	}

	public ArrayList<Salle> getSalles()
	{
		return salles;
	}
	
	public void addProfil(ProfilGlobal p)
	{
		this.availablesProfils.add(p);
	}
	
	public void removeProfil(ProfilGlobal p)
	{
		if(!(p.equals(currentProfil)))
			this.availablesProfils.remove(p);
		else
			System.err.println("le profil " + p + "est utilisé actuellement");
	}
}
