package com.pramati.usercommitcrawler.servlets;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.pramati.usercommitcrawler.beans.UserCommitHistory;
import com.pramati.usercommitcrawler.constants.UserCommitCrawlerConstants;
import com.pramati.usercommitcrawler.mutex.CustomizedConnection;
import com.pramati.usercommitcrawler.mutex.TimeManager;
import com.pramati.usercommitcrawler.utils.RepositoryCrawler;
import com.sun.istack.internal.logging.Logger;

/**
 * Servlet implementation class UserCommitCrawlerServlet
 */
@WebServlet("/UserCommitCrawlerServlet")
public class UserCommitCrawlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final Logger LOGGER = Logger
			.getLogger(UserCommitCrawlerServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserCommitCrawlerServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		long methodStartTime = System.currentTimeMillis();
		if (ServletFileUpload.isMultipartContent(request)) {
			try {

				TimeManager.get_userThreadCodeExecutionTime().set(0);
				TimeManager.get_userThreadSytemExecutionTime().set(0);
				CustomizedConnection.getNetworkConnectionTime().set(0);
				CustomizedConnection.getTotalthreadsSet().clear();
				RepositoryCrawler repositoryCrawler = new RepositoryCrawler();
				StringBuilder fileInput = repositoryCrawler
						.readMultiPartRequest(request);

				request.setAttribute("message",
						"file uploaded successfully not");
				List<UserCommitHistory> usersCommitHistoryList = repositoryCrawler
						.getUsersCommitHistory(fileInput);
				request.setAttribute("userCommitHistoryList",
						usersCommitHistoryList);
				ServletContext servletContext = getServletContext();
				servletContext.getRequestDispatcher(
						UserCommitCrawlerConstants.JSP_PATH).forward(request,
						response);

				long methodEndTime = System.currentTimeMillis();
				LOGGER.info("Thraed User Code ExecutionTime is : "
						+ TimeManager.get_userThreadCodeExecutionTimeValue()
						/ 1000000);
				LOGGER.info("Thraed System Time For Executinh user code ExecutionTime is : "
						+ TimeManager.get_userThreadSytemExecutionTimeValue()
						/ 1000000);
				LOGGER.info("Thraed ExecutionTime is : "
						+ TimeManager.getTotalExecutionTime() / 1000000);
				LOGGER.info("NetworkExecution Time is in millisecond : "
						+ CustomizedConnection.getNetworkConnectionTimeValue()
						/ 1000000);
				LOGGER.info("Total execution Time of the application is in millisecond : "
						+ (methodEndTime - methodStartTime));
				LOGGER.info("Total Thread is " + CustomizedConnection.getTotalthreads());
			} catch (FileUploadException exception) {

				if (LOGGER.isLoggable(Level.SEVERE)) {
					LOGGER.log(Level.SEVERE, "Exception Occured due to : ",
							exception);
				}
			}
		}

		else {
			request.setAttribute("message",
					"This servlet only handles file upload.");
		}
	}

}
