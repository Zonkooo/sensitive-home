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
	
	public void switchProfil(ProfilGlobal p)
	{
		for (Salle salle : salles)
		{
			salle.setProfilGlobal(currentProfil);
		}
	}
}
