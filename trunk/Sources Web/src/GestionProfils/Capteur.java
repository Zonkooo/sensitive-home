package GestionProfils;

/**
 *
 * @author raphael
 */
public class Capteur 
{
	public final static int VALEUR_INCONNUE = Integer.MIN_VALUE;
	
	TypeCapteur type;
	int lastValeur;

	public Capteur(TypeCapteur type)
	{
		this.type = type;
	}

	public void updateValeur(int valeur)
	{
		this.lastValeur = valeur;
	}
}
