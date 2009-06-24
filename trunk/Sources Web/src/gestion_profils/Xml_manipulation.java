package gestion_profils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

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
					multiprise_courante = new Multiprise(mpCourante.getAttributeValue("id"), 5,
							mpCourante.getAttributeValue("ip"));
					Element[] prises_xml = (Element[]) mpCourante
							.getChildren().toArray(
									new Element[mpCourante.getChildren().size()]);
					for (int j = 0; j < prises_xml.length; j++) {
						multiprise_courante
								.setPrise(new Prise(getType(prises_xml[j]
										.getAttributeValue("type")),
										multiprise_courante, j));
						// new Prise(prises_xml[j].getAttributeValue("type"), 0,
						// false);
					}

					salle_courante.addMultiprise(multiprise_courante);
				}

				List mcList = courant.getChildren("module_capteurs");
				Iterator imc = mcList.iterator();
				while (imc.hasNext()) {
					Element mcCourant = (Element) imc.next();
					module_courant = new ModuleCapteurs(mcCourant.getAttributeValue("id"), 4);
					Element[] capteurs_xml = (Element[]) mcCourant
							.getChildren()
							.toArray(
									new Element[mcCourant.getChildren().size()]);
					Capteur[] capteurs = new Capteur[mcCourant.getChildren()
							.size()];
					for (int j = 0; j < capteurs_xml.length; j++) {
						module_courant.setCapteur(new Capteur(
								getType(capteurs_xml[j]
										.getAttributeValue("type")),
								module_courant, j));
					}
					salle_courante.addModule(module_courant);
				}
			}
		}
		return hashSalle;
	}
	
	static public HashMap<String, ProfilGlobal> creation_Hashmap_profils(String fichier) {
		HashMap<String, ProfilGlobal> hashProfil = new HashMap<String, ProfilGlobal>();
		// On initialise un nouvel élément racine avec l'element racine du
		// document.
		racine = get_racine(fichier);
		System.out.println("racine: " + racine.getName());
		// On cree une List contenant tous les noeuds "salle" de l'Element
		// racine
		List listSalles = racine.getChildren("profil");
		// On crée un Iterator sur notre liste
		Iterator i = listSalles.iterator();
		while (i.hasNext()) {
			Element courant = (Element) i.next();
			ProfilGlobal profil_courant;
			System.out.println(courant.getAttributeValue("nom"));
			if (courant.getName().equalsIgnoreCase("profil")) {
				profil_courant = new ProfilGlobal(courant.getAttributeValue("nom"),Integer.parseInt(courant.getAttributeValue("temperature")),Integer.parseInt(courant.getAttributeValue("luminosite")));
				hashProfil.put(courant.getAttributeValue("nom"), profil_courant);
			}
		}
		Maison.getMaison().setCurrentProfil(hashProfil.values().iterator().next());
		return hashProfil;
	}

	static public void serialize(HashMap<String, Salle> hashSalle,String fichier) {
		Iterator it = hashSalle.values().iterator();
		Iterator itm;
		Element racine = new Element("maison");
		Salle salle_courante;
		ModuleCapteurs module_courant;
		Capteur capteur_courant;
		Multiprise multiprise_courante;
		Prise prise_courante;
		Element salle;
		Element module;
		Element capteur;
		Element multiprise;
		Element prise;
		Attribute at;
		//serialize les salles
		while (it.hasNext()) {
			salle_courante = (Salle) it.next();
			salle = new Element("salle");
			at = new Attribute("name",salle_courante.toString());
			salle.setAttribute(at);
			
			//serialize les capteurs
			itm = salle_courante.getModules().values().iterator();
			while(itm.hasNext()){
				module_courant = (ModuleCapteurs) itm.next();
				module = new Element("module_capteurs");
				at = new Attribute("id",module_courant.getID());
				module.setAttribute(at);
				for(int i=0;i<module_courant.getCapacity();i++){
					capteur_courant = module_courant.getCapteur(i);
					capteur = new Element("capteur");
					at = new Attribute("type", capteur_courant.getType().name().substring(0, 1));
					capteur.setAttribute(at);
					module.addContent(capteur);
				}
				salle.addContent(module);
			}
			
			//serialize les prises
			itm = salle_courante.getMultiprises().values().iterator();
			while(itm.hasNext()){
				multiprise_courante = (Multiprise) itm.next();
				multiprise = new Element("module_prise");
				at = new Attribute("id",multiprise_courante.getID());
				multiprise.setAttribute(at);
				at = new Attribute("ip",multiprise_courante.getIp());
				multiprise.setAttribute(at);
				for(int i=0;i<multiprise_courante.getCapacity();i++){
					prise_courante = multiprise_courante.getPrise(i);
					prise = new Element("prise");
					at = new Attribute("type", prise_courante.getType().name().substring(0, 1));
					prise.setAttribute(at);
					multiprise.addContent(prise);
				}
				salle.addContent(multiprise);
			}
			racine.addContent(salle);		
		}
		Document document = new Document(racine);
		affiche(document);
		enregistre(fichier, document);
		
	}

	static public void etat_actuel(HashMap<String, Salle> hashSalle,String fichier) {
		Iterator it = hashSalle.values().iterator();
		Element racine = new Element("maison");
		Salle salle_courante;
		Element salle;
		Attribute at;
		//serialize les salles
		while (it.hasNext()) {
			salle_courante = (Salle) it.next();
			salle = new Element("salle");
			at = new Attribute("name",salle_courante.toString());
			salle.setAttribute(at);
			if(salle_courante.temperature_actuelle()==-1){
				at = new Attribute("temp_actuelle","nc");
			}else{
				at = new Attribute("temp_actuelle",Integer.toString(salle_courante.temperature_actuelle()/10));
			}
			salle.setAttribute(at);
			if(salle_courante.temperature_actuelle()==-1){
				at = new Attribute("lum_actuelle","nc");
			}else{
				at = new Attribute("lum_actuelle",Integer.toString(salle_courante.luminosite_actuelle()));
			}
			salle.setAttribute(at);		
			racine.addContent(salle);		
		}
		
		Document document = new Document(racine);
//		affiche(document);
		enregistre(fichier, document);
	}

	
	static public void serialize_profils(HashMap<String, ProfilGlobal> hashSalle,String fichier) {
		Iterator it = hashSalle.values().iterator();
		Element racine = new Element("profils");
		ProfilGlobal profil_courant;
		Element profil;
		Attribute at;
		//serialize les salles
		while (it.hasNext()) {
			profil_courant = (ProfilGlobal) it.next();
			profil = new Element("profil");
			at = new Attribute("nom",profil_courant.getNom());
			profil.setAttribute(at);
			at = new Attribute("temperature",Integer.toString(profil_courant.getTemperature()));
			profil.setAttribute(at);
			at = new Attribute("luminosite",Integer.toString(profil_courant.getLuminosite()));
			profil.setAttribute(at);
			racine.addContent(profil);		
		}
		Document document = new Document(racine);
		affiche(document);
		enregistre(fichier, document);
		
	}

	
	public static TypeMorceau getType(String type) {
		char type_c = type.charAt(0);
		TypeMorceau typeMorceau;
		switch (type_c) {
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
	
	static void affiche(Document document)
	{ 
	   try
	   {
	      //On utilise ici un affichage classic avec getPrettyFormat()
	      XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
	      sortie.output(document, System.out);
	   }
	   catch (java.io.IOException e){}
	}

	static void enregistre(String fichier,Document document)
	{ 
	   try
	   {
	      XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
	      sortie.output(document, new FileOutputStream(fichier));
	   }
	   catch (java.io.IOException e){
	   }
	} 
}
