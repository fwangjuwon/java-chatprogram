package chatpro;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

	ServerSocket serverSocket;
	List<�����㽺����> ������Ʈ;

	public ChatServer() {

		try {
			serverSocket = new ServerSocket(5000);
			������Ʈ = new ArrayList<>();
			while (true) {
				Socket socket = serverSocket.accept(); // main ������
				System.out.println("Ŭ���̾�Ʈ �����");
				�����㽺���� t = new �����㽺����(socket);
				������Ʈ.add(t);
				System.out.println("������Ʈ ũ�� : " + ������Ʈ.size());
				new Thread(t).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Ŭ���̾�Ʈ ������
	class �����㽺���� implements Runnable {

		String username;
		Socket socket;
		BufferedReader reader;
		BufferedWriter writer;
		boolean isLogin;

		public �����㽺����(Socket socket) {
			this.socket = socket;

			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		public void run() {

			while (isLogin) {
				try {
					String inputData = reader.readLine();

					// �޽��� �޾����ϱ� List<�����㽺����> ������Ʈ <== ���⿡ ���
					// ��� Ŭ���̾�Ʈ���� �޽��� ���� (for�� ������!!)
					for (�����㽺���� t : ������Ʈ) {
						if (t != this) {
							t.writer.write(inputData + "\n");
							t.writer.flush();
						}
					}

				} catch (Exception e) {
					try {
						System.out.println("��� ���� : " + e.getMessage());
						isLogin = false;
						������Ʈ.remove(this);
						reader.close();
						writer.close();
						socket.close();
					} catch (Exception e1) {
						System.out.println("�������� ���μ��� ���� " + e1.getMessage());
					}
				}
			}

		}

	}

	public static void main(String[] args) {
		new ChatServer();
	}

}