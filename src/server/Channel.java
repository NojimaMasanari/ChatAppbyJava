package server;

/*各チャネル用のサーバプログラム*/
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Channel extends Thread {
	Server server; // チャットサーバ本体
	Socket socket = null; // ソケット
	BufferedReader input; // 入力用ストリーム
	OutputStreamWriter output; // 出力用ストリーム
	String handle; // クライアントのハンドル

	final char controlChar = (char) 05;
	final char separateChar = (char) 06;

	// 引数はチャネル番号、ソケット、Server.
	Channel(Socket s, Server cs) {
		server = cs;
		socket = s;
		start();
	}

	// クライアントへ文字列を出力する
	synchronized void write(String s) {
		try {
			output.write(s + "\r\n");
			output.flush();
		} catch (IOException e) {
			System.out.println("Write Err");
			close(); // エラーを起こしたら、接続を切断する
		}
	}

	/*
	 * チャネルのメインルーチン。 クライアントからの入力を受け付ける
	 */
	public void run() {
		String s;
		try {
			// ソケットから入出力ストリームを得る
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new OutputStreamWriter(socket.getOutputStream());
			write("# Welcome to Chat Service System"); // 歓迎の挨拶
			write("# Please input your name"); // ハンドル名登録
			handle = input.readLine();
			System.out.println("new user: " + handle);
			write("# Thank you for Mr./Ms." + handle + ". Please enjoy!");

			while (true) { // 入力待ちのループ
				s = input.readLine(); // 文字列入力を待つ
				if (s == null)
					close();
				else {
					if (s.length() > 0 && s.charAt(0) == controlChar) {
						System.out.println("Receive a control character");
						String cs = String.valueOf(separateChar);
						String command[] = s.substring(1).split(cs);
						for (int i = 0; i < command.length; i++) {
							System.out.println("command " + i + ": " + command[i]);
						}

						// 各制御コマンドごとの処理
						if (command[0].equals("sendPerson")) {
							System.out.println("Identified sendPerson command");
							String mes = handle + " : " + command[2] + " : only " + command[1];
							boolean success = server.singleSend(command[1], mes);
							if (success) {
								write(mes);
							} else {
								write(command[1] + " did not stay!");
							}
						}
						else if (command[0].equals("sendFile")) {
							System.out.println("Identified sendFile command");

							String mes = controlChar + "sendFile" + separateChar + handle + separateChar + command[2];
							boolean success = server.singleSend(command[1], mes);
							if (success) {
								write("To " + command[1] + ": fileInfo: " + command[2] + ": Send SendFile message");
							} else {
								write(command[1] + " did not stay!");
							}
						} else if (command[0].equals("receiveOK")) {
							System.out.println("Identified receiveOK command");

							String mes = controlChar + "receiveOK" + separateChar + handle + separateChar + command[2]
									+ separateChar + command[3] + separateChar + command[4];
							boolean success = server.singleSend(command[1], mes);
							if (success) {
								write("To " + command[1] + ": fileInfo: " + command[4] + ": Send receiveOK message");
							} else {
								write(command[1] + " did not stay!");
							}
						}
						else if (command[0].equals("requestSearchFile")) {
							System.out.println("Identified requestSearchFile command");

							String mes = controlChar + "requestSearchFile" + separateChar + handle + separateChar
									+ command[1];
							server.broadcastWithoutPerson(handle, mes);
						}
						else if (command[0].equals("acceptSearchFile")) {
							System.out.println("Identified acceptSearchFile command");

							String mes = controlChar + "acceptSearchFile" + separateChar + handle + separateChar
									+ command[2];
							server.singleSend(command[1], mes);
						}
						else if (command[0].equals("commitSearchFile")) {
							System.out.println("Identified commitSearchFile command");

							String mes = controlChar + "commitSearchFile" + separateChar + handle + separateChar
									+ command[2];
							server.singleSend(command[1], mes);
						}

					} else {
						server.broadcast(handle + " : " + s);
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Exception occurs in Channel: " + handle);
			close(); 
		}
	}

	// 接続を切断する
	public void close() {
		try {
			input.close(); // ストリームを閉じる
			output.close();
			socket.close(); // ソケットを閉じる
			socket = null;
			server.broadcast("# 回線切断 :" + handle);
			// stop();
		} catch (IOException e) {
			System.out.println("Close Err");
		}
	}
}
