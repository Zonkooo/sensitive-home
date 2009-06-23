package gestion_profils;

public class ModuleCapteurs extends Socle
{
	public ModuleCapteurs(String ID,int taille)
	{
		super(ID,taille);
	}
	
	public ModuleCapteurs(String ID, Capteur[] capteurs)
	{
		super(ID, capteurs);
	}

	public void setCapteur(Capteur c)
	{
		super.setMorceau(c);
	}
	
	public Capteur getCapteur(int index)
	{
		return (Capteur)(super.getMorceau(index));
	}

	public int getTempMoy(){
		int temp =0;
		int nb =0;
		Capteur c;
		for(int i=0;i<morceaux.length;i++){
			if(morceaux[i].getType()==TypeMorceau.TEMPERATURE){
				c = (Capteur)morceaux[i];
				temp+=c.data();
				nb++;
			}
		}
		if(nb==0){
			return -1;
		}
		temp/=nb;
		return temp;
	}
	
	public int getLumMoy(){
		int temp =0;
		int nb =0;
		Capteur c;
		for(int i=0;i<morceaux.length;i++){	
			if(morceaux[i].getType()==TypeMorceau.LUMINOSITE){
				c = (Capteur)morceaux[i];
				temp+=c.data();
				nb++;
			}
		}
		if(nb==0){
			return -1;
		}
		temp/=nb;
		return temp;
	}
}
