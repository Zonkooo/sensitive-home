import francois.Interfacage;
import francois.Lanceur;
import gestion_profils.Maison;
import gestion_profils.ModuleCapteurs;
import gestion_profils.Multiprise;
import gestion_profils.ProfilGlobal;
import gestion_profils.Salle;
import gestion_profils.Xml_manipulation;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
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
			out.println("<style type=\"text/css\">");
			out.println(" @import url(style_iphone.css);");
			out.println("</style>");
		} else {
			out.println("<style type=\"text/css\">");
			out.println(" @import url(style.css);");
			out.println("</style>");
		}
	}

	public static void logo(PrintWriter out, String userAgent)
			throws IOException {
		out
				.println("<div id=\"logo\"><img src=\"logo_info.png\" width=\"500\" height=\"115\" /></div>");
	}

	public static void plot_menu(PrintWriter out) throws IOException {
		out.print("<div id=\"menu\">");
		out
				.println("<div class=\"lien_menu\"><a href=\"?page=1\">Accueil</a></div>");
		out
				.println("<div class=\"lien_menu\"><a href=\"?page=2\">Configuration Maison</a></div>");
		out
				.println("<div class=\"lien_menu\"><a href=\"?page=3\">Changement profil</a></div>");
		out
				.println("<div class=\"lien_menu\"><a href=\"?page=4\">Sauvegarder la configuration</a></div>");
		out
				.println("<div class=\"lien_menu\"><a href=\"?page=5\">Configuration profils</a></div>");
		out
				.println("<div class=\"lien_menu\"><a href=\"?page=6\">Creer un profil</a></div>");
		out.println("</div>");
	}

	public static void plot_main(PrintWriter out, HttpServletRequest request)
			throws IOException {
		String s_page = request.getParameter("page");
		if (s_page == null) {
			s_page = "1";
		}
		int page = Integer.parseInt(s_page);
		switch (page) {
		case 1:
			info_maison(out, request);

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
			 * if (!last_salle.equals(request.getParameter("salle"))) { force =
			 * true; last_salle = request.getParameter("salle"); }
			 */
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
			if (request.getParameter("profil") != null) {
				Maison.getMaison().setCurrentProfil(
						Interface.getHashProfil().get(
								request.getParameter("profil")));
			}
			Formulaires.choix_profils(out, request);
			break;
		case 4:
			Xml_manipulation
					.serialize(Interface.getHashSalle(),
							"../webapps/web_interface/WEB-INF/classes/francois/config.xml");
			Xml_manipulation.etat_actuel(Interface.getHashSalle(),
					"../webapps/web_interface/etat.xml");
			Xml_manipulation
					.serialize_profils(Interface.getHashProfil(),
							"../webapps/web_interface/WEB-INF/classes/gestion_profils/profils.xml");
			out.print("<h1>Sauvegarde</h1>");
			break;
		case 5:
			if (last_profil == null) {
				last_profil = Interface.getHashProfil().values().iterator()
						.next().getNom();
			}
			if (last_profil.equals(request.getParameter("profil"))) {
				update_profil(request);
			} else {
				last_profil = request.getParameter("profil");
			}
			Formulaires.profils(out, request);
			break;
		case 6:
			if (request.getParameter("nom") != null) {
				creation_profil(out, request);
			}
			Formulaires.creer_profil(out, request);
			break;
		}
	}

	public static void enumeration(PrintWriter out, HttpServletRequest request) {
		// On r�cup�re la liste des noms de param�tres
		Enumeration<String> e = request.getParameterNames();
		List<String> prog = new ArrayList<String>();

		// On parcours cette liste
		while (e.hasMoreElements()) {
			String key = e.nextElement();

			// On v�rifie les valeurs des checkbox : 'on' signifie que la
			// checkbox est coch�e
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
						.get(request.getParameter("module")) != null) {
					hashSalle.get(request.getParameter("salle")).getModules()
							.get(request.getParameter("module")).getCapteur(i)
							.setType(Xml_manipulation.getType(type));
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
								request.getParameter("multiprise")) != null) {
					hashSalle.get(request.getParameter("salle"))
							.getMultiprises().get(
									request
											.getParameter("multiprise"))
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
				.getModules().get(request.getParameter("module"));
		hashSalle.get(request.getParameter("salle_capteur")).addModule(mc);

		hashSalle.get(request.getParameter("salle")).getModules().remove(
				request.getParameter("module"));
	}

	public static void bouger_prise(HttpServletRequest request) {
		HashMap<String, Salle> hashSalle = Interface.getHashSalle();
		Multiprise mp = hashSalle.get(request.getParameter("salle"))
				.getMultiprises().get(request.getParameter("multiprise"));
		hashSalle.get(request.getParameter("salle_prise")).addMultiprise((mp));

		hashSalle.get(request.getParameter("salle")).getMultiprises().remove(
				request.getParameter("multiprise"));
	}

	public static void update_profil(HttpServletRequest request) {
		HashMap<String, ProfilGlobal> hashProfil = Interface.getHashProfil();
		hashProfil.get(request.getParameter("profil")).setTemperature(
				Integer.parseInt(request.getParameter("temperature")));
		hashProfil.get(request.getParameter("profil")).setLuminosite(
				Integer.parseInt(request.getParameter("luminosite")));
	}

	public static void info_maison(PrintWriter out, HttpServletRequest request) {
		out.println("<div id=\"flash\">");
		out
				.println("	<object classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" codebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=10,0,0,0\" width=\"902\" height=\"518\" id=\"etat\" align=\"middle\">");
		out
				.println("	<param name=\"allowScriptAccess\" value=\"sameDomain\" />");
		out.println("	<param name=\"allowFullScreen\" value=\"false\" />");
		out
				.println("	<param name=\"movie\" value=\"etat.swf\" /><param name=\"quality\" value=\"high\" /><param name=\"bgcolor\" value=\"#ffffff\" />	<embed src=\"etat.swf\" quality=\"high\" bgcolor=\"#ffffff\" width=\"902\" height=\"518\" name=\"etat\" align=\"middle\" wmode=\"window\" allowScriptAccess=\"sameDomain\" allowFullScreen=\"false\" type=\"application/x-shockwave-flash\" pluginspage=\"http://www.adobe.com/go/getflashplayer_fr\" />");
		out.println("	</object>");
		out.println("</div>");

		HashMap<String, Salle> hashSalle = Interface.getHashSalle();
		Iterator it = hashSalle.values().iterator();
		Salle salle_courante;
		out.println("<div id=\"pas_flash\">");

		while (it.hasNext()) {
			salle_courante = (Salle) it.next();
			out.println("<h1>" + salle_courante + "</h1>");
			out.println("<p>temparature ");
			if (salle_courante.temperature_actuelle() == -1) {
				out.println("non connue");
			} else {
				out.println(salle_courante.temperature_actuelle() / 10);
			}
			out.println("luminosite ");
			if (salle_courante.luminosite_actuelle() == -1) {
				out.println("non connue");
			} else {
				out.println(salle_courante.luminosite_actuelle());
			}
			out.println("</p>");
		}
		out.println("</div>");
	}

	public static void creation_profil(PrintWriter out,
			HttpServletRequest request) {
		ProfilGlobal p = new ProfilGlobal(request.getParameter("nom"), Integer
				.parseInt(request.getParameter("temperature")), Integer
				.parseInt(request.getParameter("luminosite")));
		HashMap<String, ProfilGlobal> hashProfil = Interface.getHashProfil();
		hashProfil.put(p.getNom(), p);
		if (request.getParameter("bouton") != null) {
			Lanceur.getInterfacage().addBoutonToProfil(p);
		}
	}
}
