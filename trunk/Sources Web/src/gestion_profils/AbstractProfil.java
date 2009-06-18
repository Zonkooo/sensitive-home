/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gestion_profils;

/**
 *
 * @author raphael
 */
public abstract class AbstractProfil 
{	
	protected String nom;
	protected int temperature;
	protected int luminosite;

	public AbstractProfil(String nom, int temperature, int luminosite)
	{
		this.nom = nom;
		this.temperature = temperature;
		this.luminosite = luminosite;
	}
	
	//GETTERS
	public int getLuminosite()
	{
		return luminosite;
	}
	public String getNom()
	{
		return nom;
	}
	public int getTemperature()
	{
		return temperature;
	}
	
	//SETTERS
	public void setLuminosite(int luminosite)
	{
		this.luminosite = luminosite;
	}
	public void setNom(String nom)
	{
		this.nom = nom;
	}
	public void setTemperature(int temperature)
	{
		this.temperature = temperature;
	}
	
	@Override public String toString()
	{
		return this.nom;
	}
		
	@Override
	public boolean equals(Object o)
	{
		if(o == null)
			return false;
		else
			return (this.toString()).equals(o.toString());
	}
}
