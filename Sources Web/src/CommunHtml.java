import gestion_profils.ModuleCapteurs;
import gestion_profils.Multiprise;
import gestion_profils.Salle;
import gestion_profils.ProfilGlobal;
import gestion_profils.Xml_manipulation;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


public class CommunHtml {
	private static String last_salle;
	private static String last_module;
	private static String last_multiprise;
	private static String last_profil;
	private static boolean force;

	public static void style(PrintWriter out, String userAgent)
			throws IOException {
		if (userAgent.contains("iPhone")) {
			out.write("<style type=\"text/css\">");
			out.write(" @import url(style_iphone.css);");
			out.write("</style>");
		} else {
			out.write("<style type=\"text/css\">");
			out.write(" @import url(style.css);");
			out.write("</style>");
		}
	}

	public static void plot_menu(PrintWriter out) throws IOException {
		out.print("<div id=\"menu\">");
		out
				.print("<div class=\"lien_menu\"><a href=\"?page=1\">Accueil</a></div>");
		out
				.print("<div class=\"lien_menu\"><a href=\"?page=2\">Configuration Maison</a></div>");
		out
				.print("<div class=\"lien_menu\"><a href=\"?page=3\">Changement profil</a></div>");
		out
				.print("<div class=\"lien_menu\"><a href=\"?page=4\">Sauvegarder la configuration</a></div>");
		out
				.print("<div class=\"lien_menu\"><a href=\"?page=5\">Configuration profils</a></div>");
		out.print("</div>");
	}

	public static void plot_main(PrintWriter out, HttpServletRequest request)
			throws IOException {
		out.print("<div id=\"retour_ajax\"></div>");
		out.print("<div id=\"main\">");
		String s_page = request.getParameter("page");
		if (s_page == null) {
			s_page = "1";
		}
		int page = Integer.parseInt(s_page);
		switch (page) {
		case 1:
			out.print("<p>Accueil</p>");
			break;
		case 2:
			// ajout d'une salle
			if (request.getParameter("nom_salle") != null
					&& !request.getParameter("nom_salle").equals("")) {
				creer_salle(request.getParameter("nom_salle"));
			}
			// deplacement d'un capteur
			if (request.getParameter("salle_capteur") != null
					&& !request.getParameter("salle_capteur").equals(
							"salle_actuelle")) {
				bouger_capteur(request);
			}
			// deplacement d'une multiprise
			if (request.getParameter("salle_prise") != null
					&& !request.getParameter("salle_prise").equals(
							"salle_actuelle")) {
				bouger_prise(request);
			}
			if (last_salle == null) {
				last_salle = "";
			}
			if (last_module == null) {
				last_module = "";
			}
			if (last_multiprise == null) {
				last_multiprise = "";
			}
			if (force) {
				force = false;
				updateCapteurs(request);
				updateMultiprises(request);
			}
			/*
			if (!last_salle.equals(request.getParameter("salle"))) {
				force = true;
				last_salle = request.getParameter("salle");
			}*/
			if (last_module.equals(request.getParameter("module"))) {
				updateCapteurs(request);
			} else {
				last_module = request.getParameter("module");
			}

			if (last_multiprise.equals(request.getParameter("multiprise"))) {
				updateMultiprises(request);
			} else {
				last_multiprise = request.getParameter("multiprise");

			}
			Formulaires.capteurs(out, request);

			break;
		case 3:
			break;
		case 4:
			Xml_manipulation.serialize(Interface.getHashSalle(),"../webapps/web_interface/WEB-INF/classes/francois/config.xml");
			Xml_manipulation.serialize_profils(Interface.getHashProfil(),"../webapps/web_interface/WEB-INF/classes/gestion_profils/profils2.xml");
			out.print("<p>Sauvegarde</p>");
			break;
		case 5:
			if (last_profil == null) {
				last_profil = Interface.getHashProfil().values().iterator().next().getNom();
			}
			if(last_profil.equals(request.getParameter("profil"))){
				update_profil(request);
			}else{
				last_profil=request.getParameter("profil");
			}
			Formulaires.profils(out, request);
			break;
		}
		out.print("</div>");
	}

	public static void enumeration(PrintWriter out, HttpServletRequest request) {
		// On récupère la liste des noms de paramètres
		Enumeration<String> e = request.getParameterNames();
		List<String> prog = new ArrayList<String>();

		// On parcours cette liste
		while (e.hasMoreElements()) {
			String key = e.nextElement();

			// On vérifie les valeurs des checkbox : 'on' signifie que la
			// checkbox est cochée
			if (request.getParameter(key).equals("on")) {
				prog.add(key);
			} else {
				out.println("<p><strong>" + key + " : </strong>"
						+ request.getParameter(key) + "</p>");
			}
		}

		// Si nous avons au moins un langage de programmation
		if (prog.size() > 0)
			out.println("<p><strong>Je programme en : </strong>");

		out.println("<ul>");

		for (String str : prog)
			out.println("<li>" + str + "</li>");

		out.println("</ul>");
	}

	public static void updateCapteurs(HttpServletRequest request) {
		HashMap<String, Salle> hashSalle = Interface.getHashSalle();
		for (int i = 0; i < 4; i++) {
			String type = request.getParameter("type" + i);
			if (type != null) {
				if (hashSalle.get(request.getParameter("salle")).getModules()
						.get(Integer.parseInt(request.getParameter("module"))) != null) {
					hashSalle.get(request.getParameter("salle")).getModules()
							.get(
									Integer.parseInt(request
											.getParameter("module")))
							.getCapteur(i).setType(
									Xml_manipulation.getType(type));
				}
			}
		}
	}

	public static void updateMultiprises(HttpServletRequest request) {
		HashMap<String, Salle> hashSalle = Interface.getHashSalle();
		for (int i = 0; i < 5; i++) {
			String type = request.getParameter("type_mp" + i);
			if (type != null) {
				if (hashSalle.get(request.getParameter("salle"))
						.getMultiprises().get(
								Integer.parseInt(request
										.getParameter("multiprise"))) != null) {
					hashSalle.get(request.getParameter("salle"))
							.getMultiprises().get(
									Integer.parseInt(request
											.getParameter("multiprise")))
							.getPrise(i)
							.setType(Xml_manipulation.getType(type));
				}
			}
		}
	}

	public static void creer_salle(String nom) {
		HashMap<String, Salle> hashSalle = Interface.getHashSalle();
		hashSalle.put(nom, new Salle(nom));
	}

	public static void bouger_capteur(HttpServletRequest request) {
		HashMap<String, Salle> hashSalle = Interface.getHashSalle();
		ModuleCapteurs mc = hashSalle.get(request.getParameter("salle"))
				.getModules().get(
						Integer.parseInt(request.getParameter("module")));
		hashSalle.get(request.getParameter("salle_capteur")).addModule(mc);

		hashSalle.get(request.getParameter("salle")).getModules().remove(
				Integer.parseInt(request.getParameter("module")));
	}
	
	public static void bouger_prise(HttpServletRequest request) {
		HashMap<String, Salle> hashSalle = Interface.getHashSalle();
		Multiprise mp = hashSalle.get(request.getParameter("salle")).getMultiprises().get(Integer.parseInt(request.getParameter("multiprise")));
		hashSalle.get(request.getParameter("salle_prise")).addMultiprise((mp));

		hashSalle.get(request.getParameter("salle")).getMultiprises().remove(
				Integer.parseInt(request.getParameter("multiprise")));
	}
	
	public static void update_profil(HttpServletRequest request) {
		HashMap<String, ProfilGlobal> hashProfil = Interface.getHashProfil();
		hashProfil.get(request.getParameter("profil")).setTemperature(Integer.parseInt(request.getParameter("temperature")));
		hashProfil.get(request.getParameter("profil")).setLuminosite(Integer.parseInt(request.getParameter("luminosite")));
	}
}
