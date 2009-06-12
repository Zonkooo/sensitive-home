import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import francois.Module;
import francois.Salle;

public class Formulaires {
	public static void capteurs(PrintWriter out, HttpServletRequest request) {
		HashMap<String, Salle> hashSalle = Interface.getHashSalle();

		out.println("<FORM name= \"formulaire_capteur\" method=post>");
		out.println("Configuration d'un module de capteur");
		out.println("<TABLE BORDER=0>");
		// choix de la salle
		out.println("<TR>");
		out.println("	<TD>Choix de la salle</TD>");
		out.println("	<TD>");
		out
				.println("	<SELECT name=\"salle\" onchange=\"document.formulaire_capteur.submit()\">");

		String selected_salle = request.getParameter("salle");
		Iterator it = hashSalle.values().iterator();
		Salle salle_courante= new Salle("");
		while (it.hasNext()) {
			salle_courante = (Salle) it.next();
			if (selected_salle == null) {
				selected_salle = salle_courante.getNom();
			}
			String temp = "";
			if (selected_salle.equals(salle_courante.getNom())) {
				temp = "selected ";
			} else {
				temp = "";
			}
			out.println("<OPTION " + temp + "VALUE=\""
					+ salle_courante.getNom() + "\">" + salle_courante.getNom()
					+ "</OPTION>");
		}
		out.println("	</SELECT>");
		out.println("	</TD>");
		out.println("</TR>");
		// choix du module
		out.println("<TR>");
		out.println("	<TD>Choix du module</TD>");
		out.println("	<TD>");
		out
				.println("	<SELECT name=\"module\" onchange=\"document.formulaire_capteur.submit()\">");

		Module module_courant;
		it = salle_courante.getHash_module().values().iterator();
		String selected_module = request.getParameter("module");
		while (it.hasNext()) {
			String temp = "";
			module_courant = (Module) it.next();
			if (selected_module == null) {
				selected_module = module_courant.getNom();
			}
			if (selected_module.equals(module_courant.getNom()) ) {
				temp = "selected ";
			} else {
				temp = "";
			}
			out.println("<OPTION " + temp + "VALUE=\"" + module_courant.getNom() + "\">module " + module_courant.getNom()
					+ "</OPTION>");
		}
		out.println("	</SELECT>");
		out.println("	</TD>");
		out.println("</TR>");
		out.println("");
		out.println("<TR>");
		out.println("	<TD>Capteur 1</TD>");
		out.println("	<TD>");
		out.println("	<SELECT name=\"type1\">");
		out.println("		<OPTION VALUE=\"V\">Vide</OPTION>");
		out.println("		<OPTION VALUE=\"T\">Temperature</OPTION>");
		out.println("		<OPTION VALUE=\"L\">Lumiere</OPTION>");
		out.println("	</SELECT>");
		out.println("	</TD>");
		out.println("</TR>");
		out.println("");
		out.println("<TR>");
		out.println("	<TD>Capteur 2</TD>");
		out.println("	<TD>");
		out.println("	<SELECT name=\"type2\">");
		out.println("		<OPTION VALUE=\"V\">Vide</OPTION>");
		out.println("		<OPTION VALUE=\"T\">Temperature</OPTION>");
		out.println("		<OPTION VALUE=\"L\">Lumiere</OPTION>");
		out.println("	</SELECT>");
		out.println("	</TD>");
		out.println("</TR>");
		out.println("");
		out.println("<TR>");
		out.println("	<TD>Capteur 3</TD>");
		out.println("	<TD>");
		out.println("	<SELECT name=\"type3\">");
		out.println("		<OPTION VALUE=\"V\">Vide</OPTION>");
		out.println("		<OPTION VALUE=\"T\">Temperature</OPTION>");
		out.println("		<OPTION VALUE=\"L\">Lumiere</OPTION>");
		out.println("	</SELECT>");
		out.println("	</TD>");
		out.println("</TR>");
		out.println("");
		out.println("<TR>");
		out.println("	<TD>Capteur 4</TD>");
		out.println("	<TD>");
		out.println("	<SELECT name=\"type4\">");
		out.println("		<OPTION VALUE=\"V\">Vide</OPTION>");
		out.println("		<OPTION VALUE=\"T\">Temperature</OPTION>");
		out.println("		<OPTION VALUE=\"L\">Lumiere</OPTION>");
		out.println("	</SELECT>");
		out.println("	</TD>");
		out.println("</TR>");
		out.println("");
		out.println("<TR>");
		out.println("	<TD COLSPAN=2>");
		out.println("	<INPUT type=\"submit\" value=\"Envoyer\">");
		out.println("	</TD>");
		out.println("</TR>");
		out.println("</TABLE>");
		out.println("</FORM>");
	}
}
