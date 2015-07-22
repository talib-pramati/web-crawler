package com.pramati.usercommitcrawler.utils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import org.joda.time.DateTime;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.pramati.usercommitcrawler.beans.RepositoryCommitHistory;
import com.pramati.usercommitcrawler.constants.UserCommitCrawlerConstants;
import com.pramati.usercommitcrawler.mutex.CustomizedConnection;
import com.pramati.usercommitcrawler.mutex.TimeManager;
import com.sun.istack.internal.logging.Logger;

public class ExtractCommittedTextThread implements
		Callable<RepositoryCommitHistory> {

	String repositoryCommitHistoryPage;
	List<String> pagesContainingCommittedText;
	Long sum = new Long(0);

	public static final Logger LOGGER = Logger
			.getLogger(ExtractCommittedTextThread.class);

	ExtractCommittedTextThread(String repositoryCommitHistoryPage,
			List<String> pagesContainingCommittedText) {
		this.repositoryCommitHistoryPage = repositoryCommitHistoryPage;
		this.pagesContainingCommittedText = pagesContainingCommittedText;
	}

	@Override
	public RepositoryCommitHistory call() {

		long userThraedExecutionStartTime = TimeManager.getUserTime(Thread
				.currentThread().getId());
		long systemThraedExecutionStartTime = TimeManager.getSystemTime(Thread
				.currentThread().getId());
		RepositoryCommitHistory repositoryCommitHistory = new RepositoryCommitHistory();
		repositoryCommitHistory.setRepoSitoryName(repositoryCommitHistoryPage
				.split("/")[4]);

		for (String pageContainingCommittedText : pagesContainingCommittedText) {

			CustomizedConnection customizedConnection = new CustomizedConnection();

			try {
				Connection connect = customizedConnection
						.makeConnection(pageContainingCommittedText);
				Document document = connect.get();
				String date = document.select("time").attr("datetime");

				DateTime committedPagesdDateTime = new DateTime(date);
				DateTime today = DateTime.now();
				DateTime yesterday = DateTime.now().minusDays(1);

				if (committedPagesdDateTime.getYear() == today.getYear()
						&& committedPagesdDateTime.getMonthOfYear() == today
								.getMonthOfYear()
						&& committedPagesdDateTime.getDayOfMonth() == today
								.getDayOfMonth()) {
					appendCommitText(repositoryCommitHistory,
							CommitHistoryFields.TODAY,
							pageContainingCommittedText);
				}

				else if (committedPagesdDateTime.getYear() == yesterday
						.getYear()
						&& committedPagesdDateTime.getMonthOfYear() == yesterday
								.getMonthOfYear()
						&& committedPagesdDateTime.getDayOfMonth() == yesterday
								.getDayOfMonth()) {
					appendCommitText(repositoryCommitHistory,
							CommitHistoryFields.YESTERDAY,
							pageContainingCommittedText);
				}

				else {

					appendCommitText(repositoryCommitHistory,
							CommitHistoryFields.DAYS_BEFORE_YESTERDAY,
							pageContainingCommittedText);
				}
			} catch (SocketTimeoutException socketTimeoutException) {
				if (LOGGER.isLoggable(Level.WARNING)) {
					LOGGER.log(Level.WARNING,
							"SocketTimeOut Exception has occured. ");
				}
			} catch (Exception exception) {
				if (LOGGER.isLoggable(Level.WARNING)) {
					LOGGER.log(Level.WARNING,
							"SocketTimeOut Exception has occured. ", exception);
				}
			}
		}

		// TimeManager.addTime(Thread.currentThread().getId());
		long userThraedExecutionEndTime = TimeManager.getUserTime(Thread
				.currentThread().getId());
		long systemThraedExecutionEndTime = TimeManager.getSystemTime(Thread
				.currentThread().getId());
		TimeManager.addUserTime(userThraedExecutionEndTime - userThraedExecutionStartTime);
		TimeManager.addSystemTime(systemThraedExecutionEndTime - systemThraedExecutionStartTime);

		/*System.out
				.println("This thread should take long time."
						+ ((userThraedExecutionEndTime - userThraedExecutionStartTime) + (systemThraedExecutionEndTime - systemThraedExecutionStartTime)));*/
		//System.out.println("Name of the Thread is " + Thread.currentThread().getId());
		CustomizedConnection.countThread(Thread.currentThread().getId());
		System.out.println("name = " + Thread.currentThread().getName());
		return repositoryCommitHistory;
	}

	public void appendCommitText(
			RepositoryCommitHistory repositoryCommitHistory,
			CommitHistoryFields fieldValue, String url) throws IOException {

		try {

			CustomizedConnection customizedConnection = new CustomizedConnection();
			Connection connect = customizedConnection.makeConnection(url);
			Document document = connect.get();
			Elements commitTexts = document
					.select(UserCommitCrawlerConstants.TEXT_SELECTOR_REGEX);
			StringBuilder commitTextStringBuilder = new StringBuilder();
			for (Element committedText : commitTexts) {
				String text = committedText.text();
				text = text.replace("<", "<'").replace("+", "<BR>+")
						.replace("-", "<BR>-").replace(";", "<BR>");
				commitTextStringBuilder.append(text);
			}

			switch (fieldValue) {
			case YESTERDAY:
				repositoryCommitHistory.getYesterdaysCommit().append(
						commitTextStringBuilder);
				break;
			case TODAY:
				repositoryCommitHistory.getTodaysCommit().append(
						commitTextStringBuilder);
				break;
			case DAYS_BEFORE_YESTERDAY:
				repositoryCommitHistory.getDayBeforeYesrdaysCommit().append(
						commitTextStringBuilder);
				break;
			}

		} catch (SocketTimeoutException exception) {
			if (LOGGER.isLoggable(Level.WARNING)) {
				LOGGER.log(Level.WARNING,
						"SocketTimeOut Exception has occured. ");
			}
		}

	}

}
