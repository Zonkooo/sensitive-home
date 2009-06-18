package gestion_profils;

/**
 *
 * @author raphael
 */
public abstract  class Socle 
{
	protected String ID;
	protected Morceau[] morceaux;
	
	public Socle(String id2,int taille)
	{
		this.ID = id2;
		morceaux = new Morceau[taille];
	}

	public Socle(String ID, Morceau[] morceaux)
	{
		this.ID = ID;
		this.morceaux = morceaux;
	}
		
	protected void setMorceau(Morceau m)
	{
		morceaux[m.getPosition()] = m;
	}
	
	protected Morceau getMorceau(int index)
	{
		return morceaux[index];
	}

	public String getID()
	{
		return ID;
	}
	
	public int getCapacity()
	{
		return this.morceaux.length;
	}
}
