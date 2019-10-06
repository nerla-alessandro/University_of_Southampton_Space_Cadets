import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientChat {
	
	String hostName; //localhost if the server is hosted on your PC
	int portNumber; //Port through which you connect to the server
	
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
			while(!connectionSocket.isClosed()) { //Loops while the connection is open
				if(socketIn.available() != 0) { //Executes if there is an inbound message
					messageIn = DataInputStream.readUTF(socketIn);
					if(messageIn.contains("END CONNECTION")) { //Recieving "END CONNECTION" closes the connection
						connectionSocket.close();
					} else {
						System.out.println(messageIn);
					}
				}
				if(System.in.available() != 0) { //Executes if there is an outbound message
					messageOut = "[Client]: ";
					messageOut += textInput.nextLine();
					if(messageOut.contains("END CONNECTION")) { //Sending "END CONNECTION" closes the connection
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
