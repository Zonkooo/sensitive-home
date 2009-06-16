/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package GestionProfils;

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
	
	public void addProfil(ProfilGlobal p)
	{
		this.availablesProfils.add(p);
	}
	
	public void removeProfil(ProfilGlobal p)
	{
		//TODO check si on ne remove pas le current
		this.availablesProfils.remove(p);
	}
	
	public void switchProfil(ProfilGlobal p)
	{
		//TODO
	}
}
