import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerChat {
	
	int portNumber;
	
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
					messageOut = "[Server]: ";
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
