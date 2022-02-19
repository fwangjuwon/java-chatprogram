package chatpro;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ScrollPane;
import java.awt.TextArea;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ChatClient extends JFrame {

	// JPanel 기본 레이아웃 = FlowLayout
	JPanel northPanel, southPanel;
	TextArea chatList; // JTextArea 버그 있네...
	ScrollPane scroll;
	JTextField txtHost, txtPort, txtMsg;
	JButton btnConnect, btnSend;
	BufferedReader in;
	String user;
	BufferedWriter out;

	public ChatClient() {
		setTitle("MyChat1.0");
		setSize(400, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		northPanel = new JPanel();
		southPanel = new JPanel();

		chatList = new TextArea(10, 30);
		chatList.setBackground(Color.ORANGE);
		chatList.setForeground(Color.BLUE);
		scroll = new ScrollPane();
		scroll.add(chatList); // 스크롤에 textarea 달기

		txtHost = new JTextField(20); // 사이즈 20
		txtHost.setText("127.0.0.1");
		txtPort = new JTextField(5);
		txtPort.setText("5000");

		txtMsg = new JTextField(25); // 사이즈 20

		btnConnect = new JButton("Connect");
		btnSend = new JButton("Send");

		northPanel.add(txtHost);
		northPanel.add(txtPort);
		northPanel.add(btnConnect);

		southPanel.add(txtMsg);
		southPanel.add(btnSend);

		add(northPanel, BorderLayout.NORTH);
		add(scroll, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);

		initListener();

		setVisible(true);
	}

	private void initListener() {
		btnConnect.addActionListener((e) -> {
			String host = txtHost.getText();
			String port = txtPort.getText();
			System.out.println(host + "서버쪽 " + port + "포트로 연결합니다.");
		});

		btnSend.addActionListener((e) -> {
			String msg = txtMsg.getText();
			chatList.append(msg + "\n");
			txtMsg.setText(""); // 비우기
			txtMsg.requestFocus(); // 커서 두기
		});
	}

	public void connect() {
		try {
			String host = txtHost.getText();
			int port = Integer.parseInt(txtPort.getText());

			Socket socket = new Socket(host, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			new Thread(() -> {
				read();
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void read() {
		try {
			while (true) {
				String msg = in.readLine();
				chatList.append("[받음]" + msg + "\n");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send() {
		try {
			String msg = txtMsg.getText();
			chatList.append(msg + "\n");
			txtMsg.setText("");
			txtMsg.requestFocus();

			out.write(msg + "\n");
			out.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new ChatClient();
	}

}
