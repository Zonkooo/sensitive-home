<%-- Ceci est un commentaire JSP --%>
<%@page contentType="text/html"%>
<%@page errorPage="erreur.jsp"%>
<%-- Importation d'un paquetage (package) --%>
<%@page import="java.util.*"%>
<%@ include file="menu.jsp"%>
<html>
<head><title>SENSITIVE HOME</title>
<%
	String userAgent = request.getHeader("user-agent");
	if (userAgent.contains("iPhone")) {
		out.write("<style type=\"text/css\">");
		out.write(" @import url(style_iphone.css);");
		out.write("</style>");
	} else {
		out.write("<style type=\"text/css\">");
		out.write(" @import url(style.css);");
		out.write("</style>");
	}
%>
</head>
<body>
      <div id="all">
      <%-- Déclaration d'une variable globale à la classe --%>
<%!int nombreVisites = 0;%>

<%-- Définition de code Java --%>
<%
	//Il est possible d'écrire du code Java ici
	Date date = new Date();
	// On peut incrémenter une variable globale pour compter le nombre
	// d'affichage, par exemple.
	nombreVisites++;
%>


          <div id="centre">
              <div id="main" class="style1">   
<%-- Impression de variables --%>
					<p>Au moment de l'execution de ce script, nous sommes le <%=date + Menu.response()%>.</p>
					<p>Cette page a &eacute;t&eacute; affich&eacute;e <%=nombreVisites%> fois!</p>
              </div>

              <%
              	plot_menu(out);
              %>
          </div>
      </div>
      
</body>
</html>
