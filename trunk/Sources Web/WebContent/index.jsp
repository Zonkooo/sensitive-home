<%-- Ceci est un commentaire JSP --%>
<%@page contentType="text/html"%>
<%@page errorPage="erreur.jsp"%>
<%-- Importation d'un paquetage (package) --%>
<%@page import="java.util.*"%>
<html>
<head><title>Page JSP</title>
<style type="text/css">
   @import url(style.css);
</style>
</head>
<body>
      <div id="all">
      <%-- Déclaration d'une variable globale à la classe --%>
<%! int nombreVisites = 0; %>

<%-- Définition de code Java --%>
<% //Il est possible d'écrire du code Java ici
    Date date = new Date();
    // On peut incrémenter une variable globale pour compter le nombre
    // d'affichage, par exemple.
    nombreVisites++;
%>


          <div id="haut">
            <div id="photo"><img src="photo.png" alt="picture of the place"/></div>
            <div id="ban"><img src="ban.png" alt="logo and banner"/></div>
          </div>
          <div id="centre">
              <div id="main" class="style1">   
<%-- Impression de variables --%>
					<p>Au moment de l'exécution de ce script, nous sommes le <%= date %>.</p>
					<p>Cette page a été affichée <%= nombreVisites %> fois!</p>
                    <p> to the home of the 7th Val d'Isère Advanced Course on Shoulder 
                        Arthroscopy. As many of you may know, this edition will take place in Val 
                        d'Isère from <em>January 18th to January 23rd 2009</em>. </p>
                    <p><em>Your attention please: 350 seats available and numbers of places are limited we count now 300 registered persons. Do not waiting if you want take part</em></p>
                    <p> We look forward to seeing you again in Val d'Isère. More informations will be 
                        posted very soon. Please report back to this webpage for online applications 
                    very soon...</p>
                    <p>&nbsp;</p>
                    <p>&nbsp;</p>
                    <p class="style1 style2"><strong><cite>&quot;Thank you all for the invitation to 
                        participate in what&nbsp;I consider to be the world's best shoulder arthroscopy 
                        course.&quot;<br>
                        Gary Gartsman</cite></strong></p>
                    <p>U.E.M.S. : 33 CME Credits</p>
                    <p>&nbsp;</p>
                    <p>&nbsp;</p>
              </div>
              <div id="menu">
              <div class="lien_menu">
                <em id="select">Home</em>
              </div>
              <div class="lien_menu">
                <em><a href="scprog.html">The Scientific Program</a></em>
              </div>
              <div class="lien_menu">
                <em><a href="orga.html"><small>Organization</small> Committee</a></em>
              </div>
              <div class="lien_menu">
                <em><a href="faculty.html">The Faculty</a></em>
              </div>
              <div class="lien_menu">
                <em><a href="http://www.valdisere-congres.com/fr/info/arthro.php">Apply Now!</a></em>
              </div>
          </div>
          </div>
      </div>
      
</body>
</html>
