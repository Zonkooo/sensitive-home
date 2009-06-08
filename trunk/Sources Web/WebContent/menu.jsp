<%@ page import="java.lang.*,java.io.*" %>
<%!
public void plot_Accueil(JspWriter out) throws IOException{
	out.println("<div id=\"menu\">");
	out.println("<div class=\"lien_menu\"><a href=\"index.jsp\">Accueil</a></div>");
	out.println("<div class=\"lien_menu\"><a href=\"chg_profil.jsp\">Changement profil</a></div>");
	out.println("<div class=\"lien_menu\"><a href=\"conf_maison.jsp\">Configuration maison</a></div>");
	out.println("<div class=\"lien_menu\"><a href=\"conf_profils.jsp\">Configuration profils</a></div>");
	out.println("</div>");
}
public void plot_menu(JspWriter out) throws IOException{
	out.println("<div id=\"menu\">");
	out.println("<div class=\"lien_menu\"><a href=\"index.jsp\">Accueil</a></div>");
	out.println("<div class=\"lien_menu\"><a href=\"chg_profil.jsp\">Changement profil</a></div>");
	out.println("<div class=\"lien_menu\"><a href=\"conf_maison.jsp\">Configuration maison</a></div>");
	out.println("<div class=\"lien_menu\"><a href=\"conf_profils.jsp\">Configuration profils</a></div>");
	out.println("</div>");
}

%>
