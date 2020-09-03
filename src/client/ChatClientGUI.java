package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import static client.Constants.*;

public class ChatClientGUI extends JPanel implements Runnable, ActionListener, State {

	JTextField input_area;
	JTextArea free_area;

	static Thread thread = null;
	JButton close_but, quit_but;
	JScrollPane scrollpane = null;

	JButton private_but, send_but, receive_but;
	JTextField private_area;

	private static int port_for_FTP;
	private static String FTP_receive_folder = null;
	private static String main_FTP_receive_folder = "";

	JButton request_but;
	String request_file_name = null;

	// Client client = null;
	private static ChatClientGUI chat_client_panel = new ChatClientGUI();

	public static void main(String args[]) {
		JFrame chat_client_frame = new JFrame("ChatClient");
		chat_client_panel.setBackground(Color.white);
		chat_client_panel.setPreferredSize(new Dimension(400, 300));
		chat_client_frame.add("Center", chat_client_panel);
		chat_client_frame.setTitle("ChatClientGUI");
		chat_client_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		chat_client_frame.setResizable(true);
		chat_client_frame.pack();
		chat_client_frame.setVisible(true);

	}

	private ChatClientGUI() {
		setLayout(new BorderLayout());
		JPanel p = new JPanel();
		JPanel connect_info_panel = new JPanel();
		JPanel exit_panel = new JPanel();
		p.setLayout(new BorderLayout());
		connect_info_panel.setLayout(new GridLayout(1, 5, 10, 10));
		exit_panel.add(close_but = new JButton("ログアウト"));
		exit_panel.add(quit_but = new JButton("終了"));
		p.add("North", connect_info_panel);
		p.add("Center", exit_panel);

		JPanel private_panel = new JPanel();
		private_panel.setLayout(new BorderLayout());
		private_panel.add("West", new JLabel("Person:"));
		private_panel.add("Center", private_area = new JTextField("", 5));
		JPanel manipulate_panel = new JPanel();
		manipulate_panel.add(private_but = new JButton("Private"));
		manipulate_panel.add(send_but = new JButton("SendFile"));
		manipulate_panel.add(request_but = new JButton("SearchFile"));
		private_panel.add("East", manipulate_panel);

		JPanel message = new JPanel();
		message.setLayout(new BorderLayout());
		message.add("West", new JLabel("Send:"));
		message.add("Center", input_area = new JTextField("", 30));

		free_area = new JTextArea();
		scrollpane = new JScrollPane(free_area, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollpane.setVisible(true);

		JPanel all_mesage_panel = new JPanel();
		all_mesage_panel.setLayout(new BorderLayout());
		all_mesage_panel.add("North", message);
		all_mesage_panel.add("South", private_panel);

		free_area.setEditable(false);
		add("North", all_mesage_panel);
		add("Center", scrollpane);
		add("South", p);

		close_but.addActionListener(this);
		quit_but.addActionListener(this);
		input_area.addActionListener(this);

		private_but.addActionListener(this);
		send_but.addActionListener(this);
		request_but.addActionListener(this);

	}

	public void launchSetFTPServer() {
		setFTPServer();
	}

	private static void setFTPServer() {

		try (java.net.Socket socket = new java.net.Socket()) {
			socket.bind(null);
			port_for_FTP = socket.getLocalPort();
		} catch (Exception e) {
		}

		JFileChooser filechooser = new JFileChooser(main_FTP_receive_folder);
		filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		filechooser.setDialogTitle("Select Save Folder for FTP");
		filechooser.setVisible(true);

		int selected = filechooser.showOpenDialog(chat_client_panel);
		if (selected == JFileChooser.APPROVE_OPTION) {
			File file = filechooser.getSelectedFile();
			FTP_receive_folder = file.getAbsolutePath();
		}

		System.out.println("Your FTP server port is " + port_for_FTP);
		System.out.println("Your Folder for receiving file via FTP server: " + FTP_receive_folder);

		if (FTP_receive_folder == null) {
			System.out.println("Please select Save Folder!");
			System.exit(0);
		}

		new FTPReceiveServer(port_for_FTP, FTP_receive_folder);

	}

	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			System.out.println("thread start");
			thread.start();
		}
	}

	public void stop() {
		if (thread != null) {
			thread = null;
		}
	}

	public void run() {
		String s;
		while (thread != null) {
			s = Client.getInstance().read();
			System.out.println(s);
			if (s == null) {
				clientClose();
			} else {
				if (s.length() > 0 && s.charAt(0) == CONTROL_CHAR) {
					String cs = String.valueOf(SEPARATE_CHAR);
					String command[] = s.substring(1).split(cs);

					for (int i = 0; i < command.length; i++) {
						System.out.println("command " + i + ": " + command[i]);
					}

					if (command[0].equals("sendFile")) {
						System.out.println("Identified sendFile command");

						try {
							java.net.InetAddress addr = java.net.InetAddress.getLocalHost();
							String ownHost = addr.getHostAddress();

							String mes = CONTROL_CHAR + "receiveOK" + SEPARATE_CHAR + command[1] + SEPARATE_CHAR
									+ ownHost + SEPARATE_CHAR + port_for_FTP + SEPARATE_CHAR + command[2];

							writeMes(mes);
						} catch (Exception e) {
							System.out.println("Exception: send receiveOK message");
						}
					} else if (command[0].equals("receiveOK")) {
						System.out.println("Identified receiveOK command");
						String fileName = command[4];
						String host = command[2];
						String portStr = command[3];
						int port = Integer.valueOf(portStr);
						FTPSendClient.fileSend(fileName, host, port);
					} else if (command[0].equals("requestSearchFile")) {
						System.out.println("Identified requestSearchFile command");
						String senderName = command[1];
						String fileName = command[2];
						String fileNameWithPath = checkAndGetFileInfo(fileName);
						if (fileNameWithPath != null) {
							sendMessageProcessToPerson(senderName, fileName + "was found & send an accept message");
							sendAcceptFileProcess(senderName, fileName);
						} else {
							String mes = fileName + " is nothing";
							sendMessageProcessToPerson(senderName, mes);
						}
					}

					else if (command[0].equals("acceptSearchFile")) {
						System.out.println("Identified acceptSearchFile command");
						String accepterName = command[1];
						String fileNameWithPath = command[2];

						// 依頼したファイル名と同一か確認するとともに，
						// 最初のacceptメッセージのみにcommitメッセージを返答する
						if (fileNameWithPath.equals(request_file_name)) {
							System.out.println("Get first accepter: " + accepterName);
							writeMes("commit to " + accepterName + ", FileName: " + fileNameWithPath);
							sendCommitFileProcess(accepterName, fileNameWithPath);
							request_file_name = null;
						}
					} else if (command[0].equals("commitSearchFile")) {
						System.out.println("Identified commitSearchFile command");
						String commiterName = command[1];
						String fileName = command[2];
						String fileNameWithPath = checkAndGetFileInfo(fileName);
						sendFileProcessUsingFTP(commiterName, fileNameWithPath);
					}
				} else {
					appendTextArea(s);
				}
			}
		}
	}

	// 接続の切断を行なう
	public void clientClose() {
		if (Client.getInstance() != null) {
			Client.getInstance().close();
			Client.setNullInstance();
			thread = null;
		}
	}

	// メッセージの送信を行う
	public void writeMes(String mes) {
		try {
			Client.getInstance().write(mes);
		} catch (Exception e) {
			System.out.println("Detected an exception while sending a message");
			e.printStackTrace(System.err);
		}
	}

	// 特定の相手のみにメッセージを送信するプロセス
	public void sendMessageProcessToPerson(String person, String message) {
		String mes = String.valueOf(CONTROL_CHAR) + "sendPerson" + String.valueOf(SEPARATE_CHAR) + person
				+ String.valueOf(SEPARATE_CHAR) + message;
		System.out.println("To server: " + mes);
		writeMes(mes);
	}

	// FTPファイル送信プロセス
	public void sendFileProcessUsingFTP(String person, String fileNameWithPath) {
		String mes = String.valueOf(CONTROL_CHAR) + "sendFile" + String.valueOf(SEPARATE_CHAR) + person
				+ String.valueOf(SEPARATE_CHAR) + fileNameWithPath;
		System.out.println(mes);
		writeMes(mes);
	}

	// SearchFile送信プロセス
	public void sendRequestFileProcess(String fileName) {
		String mes = String.valueOf(CONTROL_CHAR) + "requestSearchFile" + String.valueOf(SEPARATE_CHAR) + fileName;
		System.out.println(mes);
		writeMes(mes);
		request_file_name = fileName;
	}

	// AcceptSearchFile送信プロセス
	public void sendAcceptFileProcess(String person, String fileName) {
		String mes = String.valueOf(CONTROL_CHAR) + "acceptSearchFile" + String.valueOf(SEPARATE_CHAR) + person
				+ String.valueOf(SEPARATE_CHAR) + fileName;
		System.out.println(mes);
		writeMes(mes);
	}

	// CommitSearchFile送信プロセス
	public void sendCommitFileProcess(String person, String fileName) {
		String mes = String.valueOf(CONTROL_CHAR) + "commitSearchFile" + String.valueOf(SEPARATE_CHAR) + person
				+ String.valueOf(SEPARATE_CHAR) + fileName;
		System.out.println(mes);
		writeMes(mes);
	}

	// 指定されたファイルがFTPreceiveFolderのフォルダ内にあるかを確認し，
	// あるならばそのファイルのパスを含んだファイル情報を返答する．
	public String checkAndGetFileInfo(String fileName) {
		String ret = null;
		File folder = new File(FTP_receive_folder);
		String existFile[] = folder.list();
		for (int i = 0; i < existFile.length; i++) {
			// System.out.println(existFile[i]);
			if (fileName.equals(existFile[i])) {
				ret = FTP_receive_folder + "\\" + fileName;
				break;
			}
		}
		return ret;
	}

	public void appendTextArea(String mes) {
		free_area.append(mes + "\n");
		JScrollBar vBar = scrollpane.getVerticalScrollBar();
		int vBarMax = vBar.getMaximum();
		vBar.setValue(vBarMax);
	}

	// イベント処理
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == close_but) { // 回線切断を実行
			stop();
			clientClose();
			StateManager.next_panel = UpdatePanel(StateManager.login_panel);
		} else if (e.getSource() == quit_but) { // アプレットの終了
			stop();
			clientClose();
			System.exit(1);
		} else if (Client.getInstance() != null && e.getSource() == input_area) {
			// テキストフィールド内の文字列をサーバーへ送信する
			writeMes(input_area.getText());
			input_area.setText("");
		} else if (Client.getInstance() != null & e.getSource() == private_but) {
			System.out.println("Pushed private Button");
			String person = private_area.getText();
			String message = input_area.getText();
			if (person.equals("")) {
				String s = "*** A person is not chosen to send a message";
				System.out.println(s);
				appendTextArea(s);
				return;
			}
			System.out.println("Want to send " + message + " to " + person);
			sendMessageProcessToPerson(person, message);
		} else if (Client.getInstance() != null & e.getSource() == send_but) {
			System.out.println("Pushed send Button");
			String person = private_area.getText();
			if (person.equals("")) {
				String s = "*** A person is not chosen to send a file";
				System.out.println(s);
				appendTextArea(s);
				return;
			}
			FileDialog openDialog = new FileDialog(new JFrame(), "Choose a file for sending to the server");
			openDialog.setVisible(true);

			String fileName = openDialog.getFile();
			String dirName = openDialog.getDirectory();
			String fileNameWithPath = dirName + "/" + fileName;
			if (fileName == null) {
				String s = "*** Please choose a file!";
				System.out.println(s);
				appendTextArea(s);
				return;
			}
			openDialog = null;

			System.out.println("Want to send " + fileNameWithPath + " to " + person);
			sendFileProcessUsingFTP(person, fileNameWithPath);

		} else if (Client.getInstance() != null & e.getSource() == request_but) {
			System.out.println("Pushed request Button");
			String fileName = input_area.getText();
			if (fileName.equals("")) {
				String s = "*** Please write a file name in \"Send Field\"";
				System.out.println(s);
				appendTextArea(s);
				return;
			}

			System.out.println("Want to request (search) " + fileName + " file to all users");
			sendRequestFileProcess(fileName);
		}

	}

	@Override
	public JPanel UpdatePanel(JPanel next_panel) {
		System.out.println("update panel");
		return next_panel;
	}

	public static ChatClientGUI getInstance() {
		return chat_client_panel;
	}

}
