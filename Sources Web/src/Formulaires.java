import gestion_profils.Maison;
import gestion_profils.ModuleCapteurs;
import gestion_profils.Multiprise;
import gestion_profils.ProfilGlobal;
import gestion_profils.Salle;
import gestion_profils.TypeMorceau;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

public class Formulaires {
	public static void capteurs(PrintWriter out, HttpServletRequest request) {
		HashMap<String, Salle> hashSalle = Interface.getHashSalle();
		out.println("<DIV ID=\"formu\">");
		out.println("<FORM name= \"formulaire_capteur\" method=post>");
		out.println("<h1>Configuration de la maison</h1>");
		// creation d'une salle
		out.println("<UL>");
		out.println("<TD><label for=\"nom_salle\">Nouvelle salle</label>");
		out
				.println(" <input id=\"nom_salle\" name=\"nom_salle\" type=\"text\" /></TD>");
		out.println("</UL>");
		// selection d'une salle
		out.println("<UL>");
		out.println("	<TD>Choix de la salle</TD>");
		out.println("	<TD>");
		out
				.println("	<SELECT name=\"salle\" onchange=\"document.formulaire_capteur.submit()\">");
		String selected_salle = request.getParameter("salle");
		Iterator it = hashSalle.values().iterator();
		Salle salle_courante = new Salle("");
		while (it.hasNext()) {
			salle_courante = (Salle) it.next();
			if (selected_salle == null) {
				selected_salle = salle_courante.toString();
			}
			String temp = "";
			if (selected_salle.equals(salle_courante.toString())) {
				temp = "selected ";
			} else {
				temp = "";
			}
			out.println("<OPTION " + temp + "VALUE=\""
					+ salle_courante.toString() + "\">"
					+ salle_courante.toString() + "</OPTION>");
		}
		Salle salle_selection = hashSalle.get(selected_salle);
		out.println("	</SELECT>");
		out.println("	</TD>");
		out.println("</UL>");

		// selection d'un module de capteurs
		out.println("<UL>");
		out.println("	<TD>Choix du module</TD>");
		out.println("	<TD>");
		out
				.println("	<SELECT name=\"module\" onchange=\"document.formulaire_capteur.submit()\">");

		ModuleCapteurs module_courant = null;
		ModuleCapteurs module_selection = null;
		it = hashSalle.get(selected_salle).getModules().values().iterator();
		String selected_module;
			selected_module = request.getParameter("module");


		while (it.hasNext()) {
			String select = "";
			module_courant = (ModuleCapteurs) it.next();
			if (module_selection == null) {
				module_selection = module_courant;
			}
			if (selected_module == null
					|| hashSalle.get(selected_salle).getModules() == null) {
				selected_module = module_courant.getID();
			}
			if (selected_module == module_courant.getID()) {
				module_selection = module_courant;
				select = "selected";
			} else {
				select = "";
			}
			out.println("<OPTION " + select + " VALUE=\""
					+ module_courant.getID() + "\">module "
					+ module_courant.getID() + "</OPTION>");
		}
		out.println("	</SELECT>");
		out.println("	</TD>");
		out.println("</UL>");

		if (module_courant != null) {
			out.println("<UL>");
			// changer salle capteur
			out.println("	<TD>Changer salle</TD>");
			out.println("	<TD>");
			out
					.println("	<SELECT name=\"salle_capteur\" onchange=\"document.formulaire_capteur.submit()\">");
			it = hashSalle.values().iterator();
			while (it.hasNext()) {
				salle_courante = (Salle) it.next();
				String temp = "";
				if (selected_salle.equals(salle_courante.toString())) {
					temp = "selected VALUE=\"salle_actuelle\">";
				} else {
					temp = "VALUE=\"" + salle_courante.toString() + "\">";
					;
				}
				out.println("<OPTION " + temp + salle_courante.toString()
						+ "</OPTION>");

			}
			out.println("	</SELECT>");
			out.println("	</TD>");
			out.println("</UL>");
			out.println("");

			// choix du type de capteur
			for (int i = 0; i < 4; i++) {
				TypeMorceau type;
				if (hashSalle.get(selected_salle).getModules() != null) {
					type = hashSalle.get(selected_salle).getModules().get(
							module_selection.getID()).getCapteur(i).getType();
				} else {
					type = TypeMorceau.AUTRE;
				}
				out.println("<UL>");
				out.println("	<TD>Capteur " + i + "</TD>");
				out.println("	<TD>");
				out
						.println("	<SELECT name=\"type"
								+ i
								+ "\" onchange=\"document.formulaire_capteur.submit()\">");
				String select;
				for (int j = 0; j < TypeMorceau.values().length; j++) {
					char value = TypeMorceau.values()[j].toString().charAt(0);
					if (value == type.name().charAt(0)) {
						select = "selected";
					} else {
						select = "";
					}
					out.println("		<OPTION " + select + " VALUE=\"" + value
							+ "\">" + TypeMorceau.values()[j] + "</OPTION>");
				}
				out.println("	</SELECT>");
				out.println("	</TD>");
				out.println("</UL>");
				out.println("");
			}
		}

		// selection d'une multiprise
		out.println("<UL>");
		out.println("	<TD>Choix d'une multiprise</TD>");
		out.println("	<TD>");
		out
				.println("	<SELECT name=\"multiprise\" onchange=\"document.formulaire_capteur.submit()\">");

		Multiprise multiprise_courante = null;
		Multiprise multiprise_selection = null;
		it = hashSalle.get(selected_salle).getMultiprises().values().iterator();
		String selected_multiprise;

		selected_multiprise = request.getParameter("multiprise");

		while (it.hasNext()) {
			String select = "";
			multiprise_courante = (Multiprise) it.next();
			if (multiprise_selection == null) {
				multiprise_selection = multiprise_courante;
			}
			if (selected_multiprise == null
					|| hashSalle.get(selected_salle).getMultiprises() == null) {
				selected_multiprise = multiprise_courante.getID();
			}
			if (selected_multiprise == multiprise_courante.getID()) {
				multiprise_selection = multiprise_courante;
				select = "selected";
			} else {
				select = "";
			}
			out.println("<OPTION " + select + " VALUE=\""
					+ multiprise_courante.getID() + "\">multiprise "
					+ multiprise_courante.getID() + "</OPTION>");
		}
		out.println("	</SELECT>");
		out.println("	</TD>");
		out.println("</UL>");

		if (multiprise_selection != null) {
			// changer salle multiprise
			out.println("<UL>");
			out.println("	<TD>Changer salle Multiprise</TD>");
			out.println("	<TD>");
			out
					.println("	<SELECT name=\"salle_prise\" onchange=\"document.formulaire_capteur.submit()\">");
			it = hashSalle.values().iterator();
			while (it.hasNext()) {
				salle_courante = (Salle) it.next();
				String temp = "";
				if (selected_salle.equals(salle_courante.toString())) {
					temp = "selected VALUE=\"salle_actuelle\">";
				} else {
					temp = "VALUE=\"" + salle_courante.toString() + "\">";
					;
				}
				out.println("<OPTION " + temp + salle_courante.toString()
						+ "</OPTION>");
			}
			out.println("	</SELECT>");
			out.println("	</TD>");
			out.println("</UL>");
			out.println("");

			// choix du type d'appareil
			for (int i = 0; i < multiprise_selection.getCapacity(); i++) {
				TypeMorceau type;
				if (hashSalle.get(selected_salle).getModules() != null) {
					type = hashSalle.get(selected_salle).getMultiprises().get(
							multiprise_selection.getID()).getPrise(i).getType();
				} else {
					type = TypeMorceau.AUTRE;
				}
				out.println("<UL>");
				out.println("	<TD>Multiprise " + i + "</TD>");
				out.println("	<TD>");
				out
						.println("	<SELECT name=\"type_mp"
								+ i
								+ "\" onchange=\"document.formulaire_capteur.submit()\">");
				String select;
				for (int j = 0; j < TypeMorceau.values().length; j++) {
					char value = TypeMorceau.values()[j].toString().charAt(0);
					if (value == type.name().charAt(0)) {
						select = "selected";
					} else {
						select = "";
					}
					out.println("		<OPTION " + select + " VALUE=\"" + value
							+ "\">" + TypeMorceau.values()[j] + "</OPTION>");
				}
				out.println("	</SELECT>");
				out.println("	</TD>");
				out.println("</UL>");
				out.println("");
			}
		}
		out.println("</FORM>");
		out.println("</DIV>");
	}
	public static void choix_profils(PrintWriter out, HttpServletRequest request){
		HashMap<String, ProfilGlobal> hashProfil = Interface.getHashProfil();
		out.println("<DIV ID=\"formu\">");
		out.println("<h1>Choix d'un profil</h1>");
		out.println("<FORM name= \"formulaire_profil\" method=post>");
		// selection d'un profil
		out.println("<UL>");
		out.println("	<TD>Selectionnez un profil</TD>");
		out.println("	<TD>");
		out.println("	<SELECT name=\"profil\" onchange=\"document.formulaire_profil.submit()\">");
		String selected_profil = 	Maison.getMaison().getCurrentProfil().getNom();
		Iterator it = hashProfil.values().iterator();
		ProfilGlobal profil_courant = null;
		ProfilGlobal profil_selection = null;
		while (it.hasNext()) {
			profil_courant = (ProfilGlobal) it.next();
			if (selected_profil == null) {
				selected_profil = profil_courant.getNom();
			}
			String temp = "";
			if (selected_profil.equals(profil_courant.getNom())) {
				temp = "selected ";
			} else {
				temp = "";
			}
			out.println("<OPTION " + temp + "VALUE=\""
					+ profil_courant.getNom() + "\">" + profil_courant.getNom()
					+ "</OPTION>");
		}
		profil_selection = hashProfil.get(selected_profil);
		out.println("	</SELECT>");

		out.println("</UL>");

		out.println("</FORM>");
		out.println("</DIV>");
	}
	public static void profils(PrintWriter out, HttpServletRequest request) {
		HashMap<String, ProfilGlobal> hashProfil = Interface.getHashProfil();
		out.println("<DIV ID=\"formu\">");
		out.println("<h1>Configuration des profils</h1>");
		out.println("<FORM name= \"formulaire_profil\" method=post>");
		// selection d'un profil
		out.println("<UL>");
		out.println("	<TD>Choix d'un profil</TD>");
		out.println("	<TD>");
		out
				.println("	<SELECT name=\"profil\" onchange=\"document.formulaire_profil.submit()\">");
		String selected_profil = request.getParameter("profil");
		Iterator it = hashProfil.values().iterator();
		ProfilGlobal profil_courant = null;
		ProfilGlobal profil_selection = null;
		while (it.hasNext()) {
			profil_courant = (ProfilGlobal) it.next();
			if (selected_profil == null) {
				selected_profil = profil_courant.getNom();
			}
			String temp = "";
			if (selected_profil.equals(profil_courant.getNom())) {
				temp = "selected ";
			} else {
				temp = "";
			}
			out.println("<OPTION " + temp + "VALUE=\""
					+ profil_courant.getNom() + "\">" + profil_courant.getNom()
					+ "</OPTION>");
		}
		profil_selection = hashProfil.get(selected_profil);
		out.println("	</SELECT>");

		out.println("</UL>");

		out.println("<UL>");
		out.println("	<TD>Temperature</TD>");
		out
				.println(" <TD><input id=\"temperature\" name=\"temperature\" type=\"text\" value=\""
						+ profil_selection.getTemperature() + "\" /></TD>");
		out.println("</UL>");

		out.println("<UL>");
		out.println("	<TD>Luminosite</TD>");
		out
				.println(" <TD><input id=\"luminosite\" name=\"luminosite\" type=\"text\" value=\""
						+ profil_selection.getLuminosite() + "\" /></TD>");
		out.println("</UL>");

		out.println("<UL>");
		out.println(" <TD><INPUT TYPE=\"submit\"  VALUE=\"Valider\"></TD>");
		out.println("</UL>");
		out.println("</FORM>");
		out.println("</DIV>");
	}
	public static void creer_profil(PrintWriter out, HttpServletRequest request){
		HashMap<String, ProfilGlobal> hashProfil = Interface.getHashProfil();
		out.println("<DIV ID=\"formu\">");
		out.println("<h1>Creer un nouveau profil</h1>");
		out.println("<FORM name= \"formulaire_creer_profil\" method=post>");

		//nom
		out.println("<UL>");
		out.println("	<TD>Nom du profil</TD>");
		out.println(" <TD><input id=\"nom\" name=\"nom\" type=\"text\" value=\"\" /></TD>");
		out.println("</UL>");
		
		//bouton tactile
		out.println("<UL>");
		out.println("	<TD>Configurer un bouton?</TD>");
		out.println(" <TD><input id=\"bouton\" name=\"bouton\" type=\"checkbox\" value=\"\" /></TD>");
		out.println("</UL>");

		//temperature
		out.println("<UL>");
		out.println("	<TD>Temperature</TD>");
		out.println(" <TD><input id=\"temperature\" name=\"temperature\" type=\"text\" value=\"\" /></TD>");
		out.println("</UL>");

		//luminosite
		out.println("<UL>");
		out.println("	<TD>Luminosite</TD>");
		out.println(" <TD><input id=\"luminosite\" name=\"luminosite\" type=\"text\" value=\"\" /></TD>");
		out.println("</UL>");

		//valider
		out.println("<UL>");
		out.println(" <TD><INPUT TYPE=\"submit\"  VALUE=\"Valider\"></TD>");
		out.println("</UL>");
		out.println("</FORM>");
		out.println("</DIV>");
	}
}
