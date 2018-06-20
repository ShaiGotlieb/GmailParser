import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

public class CheckingMail {

	private static StringBuilder sb;
	private static PrintWriter pw;

	public static Properties props;
	public static Session mySession;
	public static Store store;
	public static Message[] messages;

	private static final String FIFA = "FIFA World Cup Ticketing Centre <noreply@2018fwctc.com>";

	/** constructor - initialize program by calling method checkMail **/
	public CheckingMail(String host, String mailStoreStype, String userName, String password) {
		checkMail(host, mailStoreStype, userName, password);
	}

	/** checkMail handles logic of program **/
	public static void checkMail(String host, String storeType, String user, String password) 
	{
		try {
			props = System.getProperties();
			props.setProperty( "mail.store.protocol", "imaps" );

			pw = new PrintWriter(new File("C:/Users/Public/gmail_parse_database.csv"));
			sb = new StringBuilder();

			//create menu bar for database file
			initDataToFile(sb);

			mySession = Session.getDefaultInstance( props, null );
			store = mySession.getStore("imaps");
			store.connect(host, user, password);

			//create the folder to open in email
			Folder emailFolder = store.getFolder("INBOX");
			emailFolder.open(Folder.READ_ONLY);

			// retrieve the messages from the folder in an array
			messages = emailFolder.getMessages();
			System.out.println("messages length is: " + messages.length);

			//handle messages parsing
			parseMassages(messages);


			//close the store and folder objects
			pw.close();
			emailFolder.close(false);
			store.close();
			
			System.out.println("Program is done! Please check database file!");

		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void parseMassages(Message[] messages) throws MessagingException {
		for (int i = 0, n = messages.length; i < n; i++) {
			Message message = messages[i];
			int intSubject;

			//FIFA email
			CompanyEmail fifaEmail = new CompanyEmail(message,"Confirmation");
			if(fifaEmail.from.equals(FIFA)) {
				intSubject = message.getSubject().indexOf(fifaEmail.token);
				
				//sender email has same token
				if (intSubject > 0) {
					parseFIFA(message, fifaEmail);
				}
			}
			
			//print email number to get an idea where the program is
			System.out.println("Email Number " + (i + 1) + "/" + messages.length);
		}

	}

	/** parseFIFA handle specific company email  received messages named "FIFA" **/
	private static void parseFIFA(Message message, CompanyEmail companyEmail) throws MessagingException {
		String[] senderSplited = companyEmail.from.split("\\s+");
		String messageContent = getMessageContent(message);
		String parseContent ="";
		
		//add file date, email address & company name to database
		sb.append(message.getReceivedDate());
		sb.append(',');
		sb.append(senderSplited[5]);
		sb.append(',');
		sb.append(senderSplited[0] + " " + senderSplited[1] + " " + senderSplited[2] + " " + senderSplited[3] + " " + senderSplited[4]);
		sb.append(',');

		//get product details from email
		int intIndex = messageContent.indexOf("Match");
		//found token in email
		if(intIndex >= 0) {
			parseContent = messageContent.substring(intIndex, intIndex+30);
			parseContent = parseContent.replace("\n", "").replace("\r", "");
			
			//add product detail to database
			sb.append(parseContent);
			sb.append(',');
		}

		//get price of a product from email
		intIndex = messageContent.indexOf("Total Amount:");
		//found token in email
		if(intIndex >= 0) {
			parseContent = messageContent.substring(intIndex+13, intIndex+20);
			parseContent = parseContent.replace("\n", "").replace("\r", "");
			
			//add price to database
			sb.append("$" + parseContent);
		}

		sb.append('\n');
		pw.write(sb.toString());
	}

/** initDataToFile creates the menu bar in database **/
	private static void initDataToFile(StringBuilder sb) {
		sb.append("Date");
		sb.append(',');
		sb.append("Email");
		sb.append(',');
		sb.append("Company");
		sb.append(',');
		sb.append("Product");
		sb.append(',');
		sb.append("Price");
		sb.append('\n');
	}

	/** getMessageContent get the content of the email after verified the email is supposed to be checked **/
	private static String getMessageContent(Message message) throws MessagingException {
		try {
			Object content = message.getContent();
			if (content instanceof Multipart) {
				StringBuffer messageContent = new StringBuffer();
				Multipart multipart = (Multipart) content;
				for (int i = 0; i < multipart.getCount(); i++) {
					Part part = multipart.getBodyPart(i);
					if (part.isMimeType("text/plain")) {
						messageContent.append(part.getContent().toString());
					}
				}
				return messageContent.toString();
			}
			return content.toString();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

}//end of class CheckingMail
