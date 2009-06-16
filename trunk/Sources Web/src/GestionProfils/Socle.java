package GestionProfils;

/**
 *
 * @author raphael
 */
public abstract  class Socle 
{
	protected int ID;
	protected Morceau[] morceaux;
	
	public Socle(int ID,int taille)
	{
		this.ID = ID;
		morceaux = new Morceau[taille];
	}

	public Socle(int ID, Morceau[] morceaux)
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

	public int getID()
	{
		return ID;
	}
	
	public int getCapacity()
	{
		return this.morceaux.length;
	}
}
