package GestionProfils;

/**
 *
 * @author raphael
 */
public abstract  class Socle 
{
	protected long ID;
	protected Morceau[] morceaux;

	public Socle(long ID, Morceau[] morceaux)
	{
		this.ID = ID;
		this.morceaux = morceaux;
	}
		
	protected void setMorceau(Morceau m)
	{
		morceaux[m.getPosition()] = m;
	}
	
	protected Prise getMorceau(int index)
	{
		return morceaux[index];
	}

	public long getID()
	{
		return ID;
	}
}
