package GestionProfils;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class Xml_manipulation {

	private static org.jdom.Document document;
	private static Element racine;

	static public Element get_racine(String fichier) {
		// On crée une instance de SAXBuilder
		SAXBuilder sxb = new SAXBuilder();
		try {
			// On crée un nouveau document JDOM avec en argument le fichier XML
			// Le parsing est terminé ;)
			document = sxb.build(new File(fichier));
		} catch (Exception e) {
			System.out.println("erreur dans le chargement du fichier XML");
			return null;
		}
		return document.getRootElement();
	}

	static public String test(String fichier) {
		// On crée une instance de SAXBuilder
		SAXBuilder sxb = new SAXBuilder();
		try {
			// On crée un nouveau document JDOM avec en argument le fichier XML
			// Le parsing est terminé ;)
			document = sxb.build(new File(fichier));
		} catch (Exception e) {
			System.out.println(e.toString());
			return e.toString();
		}
		return "ok";
	}

	static public void get_list_capteurs() {

	}

	static public HashMap<String, Salle> creation_Hashmap(String fichier) {
		HashMap<String, Salle> hashSalle = new HashMap<String, Salle>();
		HashMap<String, ModuleCapteurs> hashModule = new HashMap<String, ModuleCapteurs>();
		HashMap<String, Multiprise> hashMultiprise = new HashMap<String, Multiprise>();
		// On initialise un nouvel élément racine avec l'élément racine du
		// document.
		racine = get_racine(fichier);
		System.out.println("racine: " + racine.getName());
		// Méthode définie dans la partie 3.2. de cet article

		// On cree une List contenant tous les noeuds "salle" de l'Element
		// racine
		List listSalles = racine.getChildren("salle");
		// On crée un Iterator sur notre liste
		Iterator i = listSalles.iterator();
		while (i.hasNext()) {

			Element courant = (Element) i.next();
			Salle salle_courante;
			Multiprise multiprise_courante;
			ModuleCapteurs module_courant;
			System.out.println(courant.getAttributeValue("name"));
			if (courant.getName().equalsIgnoreCase("salle")) {
				salle_courante = new Salle(courant.getAttributeValue("name"));
				hashSalle.put(courant.getAttributeValue("name"), salle_courante);

				List mpList = courant.getChildren("multiprise");
				Iterator imp = mpList.iterator();
				while (imp.hasNext()) {
					Element mpCourante = (Element) imp.next();
					multiprise_courante = new Multiprise(Integer.parseInt(mpCourante.getAttributeValue("id")),5);
					Element[] prises_xml = (Element[]) mpCourante.getChildren().toArray(new Element[mpCourante.getChildren().size()]);
					for (int j = 0; j < prises_xml.length; j++) {
						multiprise_courante.setPrise(new Prise(getType(prises_xml[j].getAttributeValue("type")),multiprise_courante,j));
						//	new Prise(prises_xml[j].getAttributeValue("type"), 0, false);
					}
					
					salle_courante.addMultiprise(multiprise_courante);
				}

				List mcList = courant.getChildren("module_capteurs");
				Iterator imc = mcList.iterator();
				while (imc.hasNext()) {
					Element mcCourant = (Element) imc.next();
					module_courant = new ModuleCapteurs(Integer.parseInt(mcCourant.getAttributeValue("id")),4);
					Element[] capteurs_xml = (Element[]) mcCourant.getChildren().toArray(new Element[mcCourant.getChildren().size()]);
					Capteur[] capteurs = new Capteur[mcCourant.getChildren().size()];
					for (int j = 0; j < capteurs_xml.length; j++) {						
						module_courant.setCapteur(new Capteur(getType(capteurs_xml[j].getAttributeValue("type")),module_courant,j ));
					}
					salle_courante.addModule(module_courant);
				}
			}
		}
		return hashSalle;
	}
	private static TypeMorceau getType(String type){
		char type_c = type.charAt(0);
		TypeMorceau typeMorceau;
		switch(type_c){
		case 'T':
			typeMorceau = TypeMorceau.TEMPERATURE;
			break;
		case 'L':
			typeMorceau = TypeMorceau.LUMINOSITE;
			break;
		case 'V':
			typeMorceau = TypeMorceau.VIDE;
			break;
		default:
			typeMorceau = TypeMorceau.AUTRE;
			break;
		}
		return typeMorceau;
	}
}

