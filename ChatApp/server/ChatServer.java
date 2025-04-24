package ChatApp.server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
	private static ServerSocket serverSocket;
	private static Set<ClientHandler> clients = Collections.synchronizedSet(new HashSet<>()); // Armazena os clientes
																								// conectados

	public static void main(String[] args) throws IOException {
		// Cria o ServerSocket na porta 0 (portas dinâmicas)
		serverSocket = new ServerSocket(0); // A porta será escolhida automaticamente pelo sistema operacional
		int serverPort = serverSocket.getLocalPort(); // Obtém a porta atribuída pelo sistema operacional

		System.out.println("Servidor ouvindo na porta " + serverPort + "...");

		// Agora o servidor precisa informar os clientes sobre essa porta dinâmica
		// Aqui você pode informar a porta para os clientes de várias maneiras
		// Para fins de exemplo, vamos apenas exibir a porta no console.

		// Loop para aceitar novos clientes
		while (true) {
			Socket clientSocket = serverSocket.accept(); // Aceita a conexão do cliente
			System.out.println("Novo cliente conectado: " + clientSocket);

			ClientHandler handler = new ClientHandler(clientSocket);
			clients.add(handler);
			handler.start(); // Inicia uma nova thread para o cliente
		}
	}

	static void broadcast(String message, ClientHandler sender) {
		synchronized (clients) {
			for (ClientHandler client : clients) {
				if (client != sender) {
					client.sendMessage(message); // Envia a mensagem para todos os outros clientes
				}
			}
		}
	}

	static class ClientHandler extends Thread {
		private Socket socket;
		private PrintWriter out;
		private BufferedReader in;
		private String name;

		public ClientHandler(Socket socket) throws IOException {
			this.socket = socket;
			this.out = new PrintWriter(socket.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}

		public void sendMessage(String message) {
			out.println(message); // Envia mensagem para o cliente
		}

		public void run() {
			try {
				out.println("Digite seu nome:");
				name = in.readLine();
				broadcast(name + " entrou no chat!", this);

				String msg;
				while ((msg = in.readLine()) != null) {
					if (msg.equalsIgnoreCase("/sair")) {
						break;
					}
					broadcast("<" + name + ">: " + msg, this);
				}
			} catch (IOException e) {
				System.out.println("Erro com cliente " + name);
			} finally {
				try {
					clients.remove(this); // Remove o cliente da lista
					socket.close();
					broadcast(name + " saiu do chat.", this);
				} catch (IOException e) {
				}
			}
		}
	}
}
