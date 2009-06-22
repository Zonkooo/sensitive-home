package web;

import francois.Interfacage;
import gestion_profils.ProfilGlobal;
import gestion_profils.Salle;
import gestion_profils.Xml_manipulation;

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
		

		interfacage = new Interfacage();

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
		 
		Timer timer = new Timer(1000, new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                //appels à faire
            }
        });
        timer.setRepeats(true);
        timer.start();
		 */
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
