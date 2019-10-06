import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class ChallengeEmail {

	public static String input(String request) { //Deals with Inputs
		Scanner input =  new Scanner(System.in);
		System.out.print(request);
		String s = input.nextLine();
		input.close();
		return s;
	}
	
	public static String fetchHTML() { //Fetches the line containing the "<title>" HTML tag
		String s = null;
		try {
			URL urlID = new URL("https://www.ecs.soton.ac.uk/people/"+input("ID: ));
			Scanner sc = new Scanner(urlID.openStream());
			boolean stopFlag = false;
			while(!stopFlag) {
				s = sc.nextLine();
				if(s.indexOf("<title>") != -1) { //Sets the stopFlag as true if it encounters the "<title>" HTML tag
					stopFlag=true;
				}
			}
			sc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
		
	}
	
	public static String fetchName(String s) { //Snips the name and title of the person from the "<title>" HTML tag
		s = (String) s.subSequence(s.indexOf('>')+1, s.indexOf('|')-1);
		return s;
	}
	
	public static void main(String[] args) {
		System.out.println(fetchName(fetchHTML())); //I love nested method calls <3

	}

}
