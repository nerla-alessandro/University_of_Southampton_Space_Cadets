import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class ChallengeEmail {

	public static String input() {
		Scanner input =  new Scanner(System.in);
		System.out.print("ID: ");
		String s = input.nextLine();
		input.close();
		return s;
	}
	
	public static String fetchHTML() {
		String s = null;
		try {
			URL urlID = new URL("https://www.ecs.soton.ac.uk/people/"+input());
			Scanner sc = new Scanner(urlID.openStream());
			boolean stopFlag = false;
			while(!stopFlag) {
				s = sc.nextLine();
				if(s.indexOf("<title>") != -1) {
					stopFlag=true;
				}
			}
			sc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
		
	}
	
	public static String fetchName(String s) {
		s = (String) s.subSequence(s.indexOf('>')+1, s.indexOf('|')-1);
		return s;
	}
	
	public static void main(String[] args) {
		System.out.println(fetchName(fetchHTML()));

	}

}
