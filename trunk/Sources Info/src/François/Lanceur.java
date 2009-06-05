package François;

import java.io.*;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.filter.*;
import java.util.List;
import java.util.Iterator;


public class Lanceur {

	
	   static org.jdom.Document document;
	   static Element racine;

	public static void main(String[] args){
		//new Profil("hiver");
	      //On crée une instance de SAXBuilder
	      SAXBuilder sxb = new SAXBuilder();
	      try
	      {
	         //On crée un nouveau document JDOM avec en argument le fichier XML
	         //Le parsing est terminé ;)
	         document = sxb.build(new File("/home/cnous3/coding/PR302/Sensitive Home Info/src/François/config.xml"));
	      }
	      catch(Exception e){}

	      //On initialise un nouvel élément racine avec l'élément racine du document.
	      racine = document.getRootElement();
	      System.out.println(racine.getName());
	      //Méthode définie dans la partie 3.2. de cet article
	      //afficheALL();

		
	}
	
	//Ajouter cette méthodes à la classe JDOM2
	static void afficheALL()
	{
	   //On crée une List contenant tous les noeuds "etudiant" de l'Element racine
	   List listEtudiants = racine.getChildren("salle");
	   System.out.println(listEtudiants);
	   //On crée un Iterator sur notre liste
	   Iterator i = listEtudiants.iterator();
	   while(i.hasNext())
	   {
	      //On recrée l'Element courant à chaque tour de boucle afin de
	      //pouvoir utiliser les méthodes propres aux Element comme :
	      //selectionner un noeud fils, modifier du texte, etc...
	      Element courant = (Element)i.next();
	      //On affiche le nom de l'element courant
	      System.out.println(courant.getChild("capteur").getText());
	   }
	}
}
