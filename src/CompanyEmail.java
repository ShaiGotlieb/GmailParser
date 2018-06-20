import javax.mail.Message;
import javax.mail.MessagingException;

public class CompanyEmail {

	String from;
	String subject;
	Message message;
	String token;

	public CompanyEmail(Message msg, String tkn) {
		message = msg;
		subject ="";
		token = tkn;
		try {
			this.from = message.getFrom()[0].toString().trim();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}//end of class CompanyEmail
