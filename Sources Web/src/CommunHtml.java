import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import francois.Xml_manipulation;

public class CommunHtml {
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
				.print("<div class=\"lien_menu\"><a href=\"?page=2\">Configuration capteurs</a></div>");
		out
				.print("<div class=\"lien_menu\"><a href=\"?page=3\">Changement profil</a></div>");
		out
				.print("<div class=\"lien_menu\"><a href=\"?page=4\">Configuration maison</a></div>");
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
			Formulaires.capteurs(out,request);
			break;
		case 3:

			break;
		case 4:
			out.print("<p>Configuration des profils</p>");
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
}
