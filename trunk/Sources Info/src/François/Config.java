package François;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class Config {

	static org.jdom.Document document;
	static Element racine;
	HashMap<String, Salle> hashSalle;
	
	public Config(String fichier){
		
		hashSalle = new HashMap<String,Salle>();
		
		//new Profil("hiver");
		//On crée une instance de SAXBuilder
		SAXBuilder sxb = new SAXBuilder();
		try
		{
			//On crée un nouveau document JDOM avec en argument le fichier XML
			//Le parsing est terminé ;)
			document = sxb.build(new File(fichier));
		}
		catch(Exception e){
			System.out.println("erreur dans le chargement du fichier XML");
		}
	
		//On initialise un nouvel élément racine avec l'élément racine du document.
		racine = document.getRootElement();
		System.out.println("racine: "+racine.getName());
		//Méthode définie dans la partie 3.2. de cet article
	
	
		//On crée une List contenant tous les noeuds "etudiant" de l'Element racine
		List listSalles = racine.getChildren("salle");
		//On crée un Iterator sur notre liste
		Iterator i = listSalles.iterator();
		while(i.hasNext())
		{
			Element courant = (Element)i.next();
			if(courant.getName().equalsIgnoreCase("salle")){
				hashSalle.put(courant.getAttributeValue("name"), new Salle(courant.getAttributeValue("name")));
				
				List mpList = courant.getChildren("multiprise");
				List mcList = courant.getChildren("module_capteurs");
				
				Iterator imp = mpList.iterator();
				while(imp.hasNext())
				{
					Element mpCourante = (Element)imp.next();
					Prise[] prises = new Prise[5];
						prises[0] = new Prise(mpCourante.getChild("prise1").getAttributeValue("type"),0,false);
						prises[1] = new Prise(mpCourante.getChild("prise2").getAttributeValue("type"),0,false);
						prises[2] = new Prise(mpCourante.getChild("prise3").getAttributeValue("type"),0,false);
						prises[3] = new Prise(mpCourante.getChild("prise4").getAttributeValue("type"),0,false);
						prises[4] = new Prise(mpCourante.getChild("prise5").getAttributeValue("type"),0,false);
						hashSalle.get(courant.getAttributeValue("name")).addMultiprise(new Multiprise("mp_"+courant.getAttributeValue("name"),prises));
						
				}
			}
		}
	}
}
