import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientChat {
	
	String hostName;
	int portNumber;
	
	public ClientChat(String hostName, int portNumber) {
		this.portNumber = portNumber;
		this.hostName = hostName;
	}
	
	public void run() {
		try {
			Socket connectionSocket = new Socket(hostName, portNumber);
			
			DataInputStream socketIn =  new DataInputStream(connectionSocket.getInputStream());
			DataOutputStream socketOut = new DataOutputStream(connectionSocket.getOutputStream());
			Scanner textInput = new Scanner(System.in);
			String messageIn = "";
			String messageOut = "";
			System.out.println("Connected to Server");
			while(!connectionSocket.isClosed()) {
				if(socketIn.available() != 0) {
					messageIn = DataInputStream.readUTF(socketIn);
					if(messageIn.contains("END CONNECTION")) {
						connectionSocket.close();
					} else {
						System.out.println(messageIn);
					}
				}
				if(System.in.available() != 0) {
					messageOut = "[Client]: ";
					messageOut += textInput.nextLine();
					if(messageOut.contains("END CONNECTION")) {
						connectionSocket.close();
					} else {
						socketOut.writeUTF(messageOut);
					}
				}
			}
			System.out.println("End of Connection");
			textInput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args) {
		ClientChat cc = new ClientChat("localhost", 666);
		cc.run();
	}

}
