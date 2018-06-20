import javax.swing.JOptionPane;

/*
 * Author: Shai Gotlieb
 * Date: 19/06/2018 
 */

public class Main {

	public static void main(String[] args) {
		String mailStoreType = "imaps";
		String host = "imap.gmail.com";
		String userName = JOptionPane.showInputDialog("Enter UserName: ");
		String password = JOptionPane.showInputDialog("Enter Password: ");
		JOptionPane.showMessageDialog(null, "The file will be save in this path: C:/Users/Public/gmail_parse_database.csv" +
									"\n" + "File name is: gmail_parse_database.csv" + "\n" + "Press OK to continue", "Hi Dan, find your file", 2 );
		CheckingMail email = new CheckingMail(host,mailStoreType,userName,password);

	}//end of main
}//end of class Main