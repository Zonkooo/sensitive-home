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
				hashSalle
						.put(courant.getAttributeValue("name"), salle_courante);

				List mpList = courant.getChildren("multiprise");
				Iterator imp = mpList.iterator();
				while (imp.hasNext()) {
					Element mpCourante = (Element) imp.next();
					Element[] prises_xml = (Element[]) mpCourante.getChildren().toArray(new Element[mpCourante.getChildren().size()]);
					Prise[] prises = new Prise[mpCourante.getChildren().size()];
					for (int j = 0; j < prises_xml.length; j++) {
						char type = prises_xml[j].getAttributeValue("type").charAt(0);
						TypeMorceau typeMorceau;
						switch(type){
						case 'T':
							typeMorceau = ;
							break;
						case 'L':
							typeMorceau = Appareil.LAMPE;
							break;
						case 'V':
							typeMorceau = Appareil.VIDE;
							break;
						case 'A':
							typeMorceau = Appareil.AUTRE;
							break;
						}
						prises[j] = new Prise(appareil,multiprise_courante,j);
						//	new Prise(prises_xml[j].getAttributeValue("type"), 0, false);
					}
					multiprise_courante = new Multiprise(Integer.parseInt(mpCourante.getAttributeValue("id")), prises);

					salle_courante.addMultiprise(multiprise_courante);
				}

				List mcList = courant.getChildren("module_capteurs");
				Iterator imc = mcList.iterator();
				while (imc.hasNext()) {
					Element mcCourant = (Element) imc.next();
					Element[] capteurs_xml = (Element[]) mcCourant
							.getChildren()
							.toArray(
									new Element[mcCourant.getChildren().size()]);
					Capteur[] capteurs = new Capteur[mcCourant.getChildren()
							.size()];
					for (int j = 0; j < capteurs_xml.length; j++) {
						char type = capteurs_xml[j].getAttributeValue("type").charAt(0);
						TypeMorceau type_capteur;
						switch(type){
						case 'T':
							type_capteur = TypeMorceau.TEMPERATURE;
							break;
						case 'L':
							type_capteur = TypeMorceau.LUMINOSITE;
							break;
						case 'V':
							type_capteur = TypeMorceau.RIEN;
							break;
						}
						capteurs[j] = new Capteur(type_capteur );
					}
					module_courant = new ModuleCapteurs(Integer.parseInt(mcCourant
							.getAttributeValue("id")), capteurs);
					System.out.println(mcCourant.getAttributeValue("id"));
					salle_courante.addModule(module_courant);
				}
			}
		}
		return hashSalle;
	}
}
