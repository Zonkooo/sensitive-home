package GestionProfils;

/**
 *
 * @author raphael
 */
public class Capteur 
{
	public final static int VALEUR_INCONNUE = Integer.MIN_VALUE;
	
	private TypeCapteur type;
	private int lastValeur;

	public Capteur(TypeCapteur type)
	{
		this.type = type;
	}

	public int getLastValeur()
	{
		return lastValeur;
	}

	public TypeCapteur getType()
	{
		return type;
	}

	public void setLastValeur(int lastValeur)
	{
		this.lastValeur = lastValeur;
	}

	public void setType(TypeCapteur type)
	{
		this.type = type;
	}
	
	
}
