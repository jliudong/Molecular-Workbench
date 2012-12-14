package org.concord.mwbackend.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.BitSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import org.concord.mwbackend.business.EssayQuestion;
import org.concord.mwbackend.business.MultipleChoice;
import org.concord.mwbackend.business.Person;

/**
 * @web.servlet name = "Grade" display-name = "Grade Servlet" description = "Grade"
 * @web.servlet-mapping url-pattern="/grade"
 * @web.servlet-init-param name="driverName" value="com.mysql.jdbc.Driver"
 * @web.servlet-init-param name="connectionURLPrefix" value="jdbc:mysql://"
 * @web.servlet-init-param name="dbserver" value="localhost"
 * @web.servlet-init-param name="dbname" value="molo_qxie"
 * @web.servlet-init-param name="user" value="qxie"
 * @web.servlet-init-param name="pswd" value="123456"
 */

public class GradeServlet extends HttpServlet {

	/** SAX parser */
	protected SAXParser saxParser;

	/** Default SAX2 event handler for XML parsing */
	protected DefaultHandler saxHandler;

	private String title;
	private long start, finish;
	private Person student;
	private List<Object> questions;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		questions = new ArrayList<Object>();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		student = new Person();
		questions.clear();

		if (saxParser == null) {
			try {
				saxParser = SAXParserFactory.newInstance().newSAXParser();
			} catch (SAXException e) {
				e.printStackTrace(System.err);
				return;
			} catch (ParserConfigurationException e) {
				e.printStackTrace(System.err);
				return;
			}
			saxHandler = new XMLHandler();
		}

		try {
			saxParser.parse(new InputSource(request.getInputStream()), saxHandler);
		} catch (IOException e) {
			e.printStackTrace(System.err);
			return;
		} catch (SAXException e) {
			e.printStackTrace(System.err);
			return;
		}

		storeInDataBase();

		response.setContentType("text/html");

		PrintWriter out = response.getWriter();

		StringBuffer sb = new StringBuffer();
		sb.append("<html><body>");
		sb.append("<h1>" + title + "</h1>");
		sb.append("<hr>");
		sb.append("<p>Student name: " + student.getFullName() + "<br>");
		sb.append("E-mail: " + student.getEmailAddress() + "<br>");
		sb.append("Teacher name: " + student.getTeacher() + "<br>");
		sb.append("School: " + student.getInstitution() + "<br>");
		if (start > 0)
			sb.append("Test started at: " + new Date(start) + "<br>\n");
		if (finish > 0)
			sb.append("Test completed at: " + new Date(finish) + "<br>\n");
		sb.append("Graded by: " + request.getRequestURL());
		sb.append("<hr>");
		if (!questions.isEmpty()) {
			Object o = null;
			for (Iterator it = questions.iterator(); it.hasNext();) {
				o = it.next();
				if (o instanceof MultipleChoice) {
					MultipleChoice mc = (MultipleChoice) o;
					if (mc.getID() == 0)
						sb.append("<p><font color=\"#0000ff\"><i>Page: " + mc.getAddress()
								+ "</i></font></p>");
					sb.append("<p><b>" + mc.getQuestion() + "</b></p>");
					int n = mc.getChoiceCount();
					char c = 'a';
					sb.append("<p>");
					for (int i = 0; i < n; i++) {
						sb.append("(" + c + ") " + mc.getChoice(i) + "<br>");
						c++;
					}
					sb.append("</p>");
					String correctString = mc.getCorrect();
					if (correctString == null || correctString.trim().equals("")) {
						sb
								.append("<p>The correct answer is not set. Please contact your teacher.</p>");
					} else {
						String selectionString = mc.getSelection();
						sb.append("<p>The correct answer is: " + correctString + ".<br>");
						if (!correctString.equals(selectionString)) {
							if (selectionString == null || selectionString.trim().equals("")) {
								sb
										.append("<font color=\"#ff0000\">You did not answer this question.</font></p>");
							} else {
								sb.append("<font color=\"#ff0000\">You answer is: "
										+ selectionString + ".</font></p>");
								sb.append("<font size=\"2\">Time: " + new Date(mc.getTimestamp())
										+ "</font></p>");
							}
						} else {
							sb.append("You answer is: " + selectionString + ".</p>");
							sb.append("<font size=\"2\">Time: " + new Date(mc.getTimestamp())
									+ "</font></p>");
						}
					}
					n = mc.getGuessCount();
					if (n > 0) {
						sb.append("<p>Your history of guesses:<br>");
						for (int i = 0; i < n; i++) {
							sb.append("(" + (i + 1) + ") " + mc.getGuess(i) + "<br>");
						}
						sb.append("</p>");
					}
				} else if (o instanceof EssayQuestion) {
					EssayQuestion eq = (EssayQuestion) o;
					if (eq.getID() == 0)
						sb.append("<p><font color=\"#0000ff\"><i>Page: " + eq.getAddress()
								+ "</i></font></p>");
					sb.append("<p><b>" + eq.getQuestion() + "</b></p>");
					if (eq.getReferenceAnswer() != null
							&& !eq.getReferenceAnswer().trim().equals("")) {
						sb.append("<p>The correct answer is: " + eq.getReferenceAnswer() + "<br>");
					}
					if ("NOT ANSWERED.".equals(eq.getAnswer())) {
						sb
								.append("<font color=\"#ff0000\">You did not answer this question.</font></p>");
					} else {
						sb.append("Your answer is: " + eq.getAnswer() + "<br>");
						sb.append("<font size=\"2\">Time: " + new Date(eq.getTimestamp())
								+ "</font></p>");
					}
				}
			}
		}
		sb.append("</body></html>");
		out.println(sb);
		// getServletContext().log(sb.toString());

	}

	private void storeInDataBase() {

	}

	class XMLHandler extends DefaultHandler {

		private String str;
		private StringBuffer buffer;
		private String attribName, attribValue;
		private List<String> choices, guesses;
		private boolean correct, selected;
		private int iChoice;
		private BitSet correctSet = new BitSet(10), selectedSet = new BitSet(10);
		private String page;
		private String question;
		private String answer, referenceAnswer;
		private String firstName, lastName;
		private int id;
		private long timestamp = -1;

		XMLHandler() {
			buffer = new StringBuffer();
		}

		public void startDocument() {
		}

		public void endDocument() {
		}

		public void startElement(String uri, String localName, String qName, Attributes attrib) {

			buffer.setLength(0);

			if (qName.equals("choice")) {
				if (attrib != null) {
					for (int i = 0, n = attrib.getLength(); i < n; i++) {
						attribName = attrib.getQName(i);
						attribValue = attrib.getValue(i);
						if (attribName.equals("correct")) {
							correct = Boolean.valueOf(attribValue).booleanValue();
						} else if (attribName.equals("selected")) {
							selected = Boolean.valueOf(attribValue).booleanValue();
						}
					}
				}
			}

		}

		public void endElement(String uri, String localName, String qName) {

			str = buffer.toString();

			if (qName.equals("title")) {
				title = str;
			} else if (qName.equals("start")) {
				try {
					start = Long.parseLong(str);
				} catch (NumberFormatException e) {
					start = -1;
				}
			} else if (qName.equals("finish")) {
				try {
					finish = Long.parseLong(str);
				} catch (NumberFormatException e) {
					finish = -1;
				}
			} else if (qName.equals("email")) {
				student.setEmailAddress(str);
			} else if (qName.equals("school")) {
				student.setInstitution(str);
			} else if (qName.equals("first")) {
				firstName = str;
			} else if (qName.equals("last")) {
				lastName = str;
			} else if (qName.equals("page")) {
				page = str;
			} else if (qName.equals("id")) {
				id = Integer.parseInt(str);
			} else if (qName.equals("timestamp")) {
				timestamp = Long.parseLong(str);
			} else if (qName.equals("student")) {
				if (firstName != null) {
					student.setFirstName(firstName);
					firstName = null;
				}
				if (lastName != null) {
					student.setLastName(lastName);
					lastName = null;
				}
			} else if (qName.equals("teacher")) {
			} else if (qName.equals("question")) {
				if (str.startsWith("<html>")) {
					int i = str.indexOf("<body>");
					int j = str.indexOf("</body>");
					if (i != -1 && j != -1) {
						str = str.substring(i + 7, j);
					} else {
						i = str.indexOf("<html>");
						j = str.indexOf("</html>");
						str = str.substring(i + 7, j);
					}
				}
				question = str;
			} else if (qName.equals("choice")) {
				if (choices == null)
					choices = new ArrayList<String>();
				choices.add(str);
				correctSet.set(iChoice, correct);
				selectedSet.set(iChoice, selected);
				correct = false;
				selected = false;
				iChoice++;
			} else if (qName.equals("guess")) {
				if (guesses == null)
					guesses = new ArrayList<String>();
				guesses.add(str);
			} else if (qName.equals("multiplechoice")) {
				MultipleChoice mc = new MultipleChoice();
				questions.add(mc);
				mc.setAddress(page);
				mc.setID(id);
				mc.setQuestion(question);
				if (choices != null && !choices.isEmpty()) {
					for (Iterator it = choices.iterator(); it.hasNext();)
						mc.addChoice((String) it.next());
					choices.clear();
					StringBuffer sb1 = new StringBuffer();
					StringBuffer sb2 = new StringBuffer();
					char c;
					for (byte i = 0; i < iChoice; i++) {
						if (correctSet.get(i)) {
							c = (char) ('a' + i);
							sb1.append(c + " ");
						}
						if (selectedSet.get(i)) {
							c = (char) ('a' + i);
							sb2.append(c + " ");
						}
					}
					mc.setCorrect(sb1.toString().trim());
					mc.setSelection(sb2.toString().trim());
				}
				if (guesses != null && !guesses.isEmpty()) {
					for (Iterator it = guesses.iterator(); it.hasNext();)
						mc.addGuess((String) it.next());
					guesses.clear();
				}
				if (timestamp > 0) {
					mc.setTimestamp(timestamp);
					timestamp = -1;
				}
				iChoice = 0;
			} else if (qName.equals("answer")) {
				answer = str;
			} else if (qName.equals("referenceanswer")) {
				referenceAnswer = str;
			} else if (qName.equals("qanda")) {
				EssayQuestion eq = new EssayQuestion();
				questions.add(eq);
				eq.setAddress(page);
				eq.setID(id);
				eq.setQuestion(question);
				eq.setAnswer(answer);
				if (timestamp > 0) {
					eq.setTimestamp(timestamp);
					timestamp = -1;
				}
				if (referenceAnswer != null) {
					eq.setReferenceAnswer(referenceAnswer);
					referenceAnswer = null;
				}
			}

		}

		public void characters(char[] ch, int start, int length) {
			str = new String(ch, start, length);
			buffer.append(str);
		}

		public void warning(SAXParseException e) {
			e.printStackTrace(System.err);
		}

		public void error(SAXParseException e) {
			e.printStackTrace(System.err);
		}

		public void fatalError(SAXParseException e) {
			e.printStackTrace(System.err);
		}

	}

}
