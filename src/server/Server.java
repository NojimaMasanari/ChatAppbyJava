package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


import static server.Constants.*;

public class Server extends Thread {
	Channel channel[] = new Channel[MAX_CHANNELS];
	ServerSocket serversocket; // 接続受け付け用ServerSocket
	int port; // ポート番号

	public static void main(String args[]) {
		int p = PORT; // 指定されていなけれれば28000
		if (args.length > 0) {
			p = Integer.parseInt(args[0]);
		}
		new Server(p);
	}

	public Server(int port) {
		this.port = port;
		this.start();
	}

	// メインサーバーの切断を行なう
	public void serverClose() {
		try {
			System.out.println("Server#Close()");
			serversocket.close();
			serversocket = null;
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	// 全チャネルの切断を行なう
	public void clientClose() {
		System.out.println("Server#Close " + channel.length);
		for (int i = 0; i < channel.length; i++) {
			System.out.println("Server#Close(" + i + ")");
			channel[i].close();
			channel[i] = null;
		}
	}

	public void quit() {
		clientClose();
		serverClose();
		System.exit(1);
	}

	public void run() {
		int i;
		try {
			serversocket = new ServerSocket(port); // メインサーバー開放
			System.out.println("Server Start: the port number is " + port);
			while (true) { // 空いているチャネルを探す
				for (i = 0; i < MAX_CHANNELS; i++) {
					if (channel[i] == null) {
						break;
					}
				}
				if (i == MAX_CHANNELS) // 最大のクライアント数なら終了
					return;
				Socket socket = serversocket.accept(); // 接続待ち
				channel[i] = new Channel(socket, this);// 新チャネル作成
			}
		} catch (IOException e) {
			System.out.println("Server Err!");
			return;
		}
	}

	// 全チャネルへブロードキャスト
	synchronized void broadcast(String message) {
		for (int i = 0; i < MAX_CHANNELS; i++) {
			if (channel[i] != null && channel[i].socket != null) {
				channel[i].write(message);
			}
		}
	}

	// 特定の相手へ送信
	synchronized boolean singleSend(String person, String message) {
		boolean ret = false;
		System.out.println("To " + person + ": " + message);
		for (int i = 0; i < MAX_CHANNELS; i++) {
			if (channel[i] != null && channel[i].socket != null) {
				if (person.equals(channel[i].handle)) {
					channel[i].write(message);
					ret = true;
				}
			}
		}
		return ret;
	}

	// 特定の相手以外へのブロードキャスト
	synchronized void broadcastWithoutPerson(String person, String message) {
		for (int i = 0; i < MAX_CHANNELS; i++) {
			if (channel[i] != null && channel[i].socket != null) {
				if (!person.equals(channel[i].handle)) {
					channel[i].write(message);
				}
			}
		}
	}

}