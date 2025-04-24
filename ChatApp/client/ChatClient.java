package ChatApp.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
	private static final String SERVER_IP = "localhost";

	public static void main(String[] args) {
		try {

			Socket socket = new Socket(SERVER_IP, 1234);
			System.out.println("Conectado ao servidor.");

			int clientPort = socket.getLocalPort();
			System.out.println("Cliente usando porta: " + clientPort);

			// Configura os fluxos de entrada e saída
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

			// Thread para receber mensagens do servidor
			new Thread(() -> {
				try {
					String msg;
					while ((msg = in.readLine()) != null) {
						System.out.println(msg);
					}
				} catch (IOException e) {
					System.out.println("Conexão encerrada.");
				}
			}).start();

			// Enviar mensagens para o servidor
			BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
			String input;
			while ((input = userInput.readLine()) != null) {
				if (input.equalsIgnoreCase("sair")) {
					System.out.print("Você tem certeza que deseja sair? (s/n): ");
					String confirm = userInput.readLine();
					if (confirm.equalsIgnoreCase("s")) {
						System.out.println("Saindo do chat...");
						socket.close();
						break;
					}
				} else {
					out.println(input);
				}
			}

			socket.close();
		} catch (IOException e) {
			System.err.println("Erro ao conectar-se ao servidor: " + e.getMessage());
		}
	}
}
