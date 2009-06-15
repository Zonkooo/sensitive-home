package GestionProfils;

public class Multiprise
{
	private long ID;
	private Prise[] prises;

	public Multiprise(long ID, Prise[] prises)
	{
		this.ID = ID;
		this.prises = prises.clone();
	}

	public void setPrise(Prise p, int index)
	{
		prises[index] = p;
	}

	public Prise getPrise(int index)
	{
		return prises[index];
	}

	public long getID()
	{
		return ID;
	}
}
