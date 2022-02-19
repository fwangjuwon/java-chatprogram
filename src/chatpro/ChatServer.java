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
	List<고객전담스레드> 고객리스트;

	public ChatServer() {

		try {
			serverSocket = new ServerSocket(5000);
			고객리스트 = new ArrayList<>();
			while (true) {
				Socket socket = serverSocket.accept(); // main 스레드
				System.out.println("클라이언트 연결됨");
				고객전담스레드 t = new 고객전담스레드(socket);
				고객리스트.add(t);
				System.out.println("고객리스트 크기 : " + 고객리스트.size());
				new Thread(t).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 클라이언트 스레드
	class 고객전담스레드 implements Runnable {

		String username;
		Socket socket;
		BufferedReader reader;
		BufferedWriter writer;
		boolean isLogin;

		public 고객전담스레드(Socket socket) {
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

					// 메시지 받았으니까 List<고객전담스레드> 고객리스트 <== 여기에 담긴
					// 모든 클라이언트에게 메시지 전송 (for문 돌려서!!)
					for (고객전담스레드 t : 고객리스트) {
						if (t != this) {
							t.writer.write(inputData + "\n");
							t.writer.flush();
						}
					}

				} catch (Exception e) {
					try {
						System.out.println("통신 실패 : " + e.getMessage());
						isLogin = false;
						고객리스트.remove(this);
						reader.close();
						writer.close();
						socket.close();
					} catch (Exception e1) {
						System.out.println("연결해제 프로세스 실패 " + e1.getMessage());
					}
				}
			}

		}

	}

	public static void main(String[] args) {
		new ChatServer();
	}

}