package web;

import francois.Interfacage;
import gestion_profils.ModuleCapteurs;
import gestion_profils.Multiprise;
import gestion_profils.Prise;
import gestion_profils.ProfilGlobal;
import gestion_profils.Salle;
import gestion_profils.Xml_manipulation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.Timer;

/**
 * Servlet implementation class Menu
 */
public class Interface extends HttpServlet {
	private static HashMap<String, Salle> hashSalle;
	private static HashMap<String, ProfilGlobal> hashProfil;
	private static final long serialVersionUID = 1L;

	static Interfacage interfacage;
	
	public static HashMap<String, ProfilGlobal> getHashProfil() {
		return hashProfil;
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Interface() {
		super();
		hashSalle =	Xml_manipulation.creation_Hashmap("../webapps/web_interface/WEB-INF/classes/francois/config.xml");
		hashProfil = Xml_manipulation.creation_Hashmap_profils("../webapps/web_interface/WEB-INF/classes/gestion_profils/profils.xml");
		
//		hashSalle =	Xml_manipulation.creation_Hashmap("/home/cnous3/coding/PR302/web_interface/src/francois/config.xml");
//		hashProfil = Xml_manipulation.creation_Hashmap_profils("/home/cnous3/coding/PR302/web_interface/src/gestion_profils/profils.xml");
		
		
		// Si des modules sont déjà existants dans le xml,
		//on envoie un message à la multiprise pour que le module commence à envoyer des données
		//TODO: pour l'instant on envoie à la première multiprise (vu qu'il n'y en a qu'une!!!:-))
		for (Salle s : hashSalle.values()) {
			for (ModuleCapteurs mc : s.getModules().values()) {
				hashSalle.get("salon").getMultiprises().get("1").getCommunication().addMessageToQueue("/"+mc.getID()+"\\");
			}
		}
		

		
		
		interfacage = new Interfacage();
//		interfacage.addBoutonToProfil((new ProfilGlobal("salon",20,20)));
		interfacage.start();

		//on redéfinit la sortie sur un fichier pour logguer ce qui se passe
		try {
			System.setOut(new PrintStream(new FileOutputStream("/home/cnous3/Desktop/out.log")));
			System.setErr(new PrintStream(new FileOutputStream("/home/cnous3/Desktop/err.log")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		/*
		 * ceci est un timer
		 * 
		 */
		
		Timer timer = new Timer(2000, new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				//appels à faire
				for (Salle salle : hashSalle.values()) {
					//luminosité
					HashMap<Prise, Integer> hashCommandes = salle.getCommandesLampes();
					for (Prise p : hashCommandes.keySet()) {
						p.getOwner().sendMessage(p.getPosition(), hashCommandes.get(p));
					}
					//température
					salle.analyse();
					
				}
				
			}
		});
        timer.setRepeats(true);
        timer.start();
	}

	public static Interfacage getInterfacage() {
		return interfacage;
	}

	public static HashMap<String, Salle> getHashSalle() {
		return hashSalle;
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		String page = request.getParameter("page");
		PrintWriter out = response.getWriter();
		out.println("<HTML>");
		out.println("<HEAD><TITLE>" + page+  "</TITLE></HEAD>");
		out.println("<BODY>");
		out.println("<div id=\"content\">");
		out.println("<script src=\"ajax.js\" type=\"text/javascript\"></script>");
		String userAgent = request.getHeader("user-agent");
		CommunHtml.style(out, userAgent);
		CommunHtml.logo(out, userAgent);
		CommunHtml.plot_menu(out);
		CommunHtml.plot_main(out, request);	
		//CommunHtml.enumeration(out, request);
		out.println("</div>");
		out.println("</BODY></HTML>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
