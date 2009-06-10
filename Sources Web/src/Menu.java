import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.*;
import org.jdom.output.*;


/**
 * Servlet implementation class Menu
 */
public class Menu extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static String response() {

		return "ok test";
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Menu() {
		super();
		// TODO Auto-generated constructor stub
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
		out.println("<HEAD><TITLE>" + page + "</TITLE></HEAD>");
		out.println("<BODY>");
		out.println("<script src=\"ajax.js\" type=\"text/javascript\"></script>");
		String userAgent = request.getHeader("user-agent");
		CommunHtml.style(out, userAgent);
		CommunHtml.plot_menu(out);
		CommunHtml.plot_main(out, request);
		CommunHtml.enumeration(out, request);
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
