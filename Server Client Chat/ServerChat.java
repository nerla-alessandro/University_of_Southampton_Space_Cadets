import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerChat {
	
	int portNumber; //Use ports above 1024 to avoid conflicts (Or use Doom's port for the nostalgia)
	
	public ServerChat(int portNumber) {
		this.portNumber = portNumber;
	}
	
	public void run() {
		try {
			ServerSocket ss = new ServerSocket(portNumber);
			Socket connectionSocket = ss.accept();
			Scanner textInput = new Scanner(System.in);
			DataInputStream socketIn =  new DataInputStream(connectionSocket.getInputStream());
			DataOutputStream socketOut = new DataOutputStream(connectionSocket.getOutputStream());
			String messageIn = "";
			String messageOut = "";
			System.out.println("Online");
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
					messageOut = "[Server]: ";
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
			ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ServerChat sc = new ServerChat(666);
		sc.run();
	}

}
