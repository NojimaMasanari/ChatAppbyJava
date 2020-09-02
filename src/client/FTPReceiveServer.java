package client;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FTPReceiveServer extends Thread {
	private ServerSocket serverSocket;
	// private Socket socket;
	private int port = -1;
	private String downloadFolder = null;

	public FTPReceiveServer(int p) {
		this(p, null);
	}

	public FTPReceiveServer(int p, String folder) {
		port = p;
		downloadFolder = folder;
		try {
			// サーバーソケットの作成
			serverSocket = new ServerSocket(port);
			start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			// クライアントからの接続待機
			while (true) {
				try {
					System.out.println("Use port : " + port);
					// クライアントからの接続待機
					Socket socket = serverSocket.accept();
					System.out.println(socket.getInetAddress() + " is connected");
					// 通信処理開始
					new FileReceiver(socket, downloadFolder);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

class FileReceiver extends Thread {
	private Socket socket = null;
	private String fileName = null;
	private String downloadFolder = null;

	public FileReceiver(Socket socket, String folder) {
		this.socket = socket;
		downloadFolder = folder;
		if (downloadFolder == null) {
			downloadFolder = "";
		} else {
			downloadFolder += "\\";
		}
		this.start();
	}

	public void run() {
		File file = null;
		int size = 0;
		try {
			// 入力ストリームの準備
			BufferedInputStream input = new BufferedInputStream(socket.getInputStream());

			// ファイル名の文字数を受け取る
			int fileNameSize = input.read();

			// ファイル名を受け取る
			byte fileNameByte[] = new byte[fileNameSize];
			input.read(fileNameByte);
			fileName = new String(fileNameByte);
			System.out.println("Write file name：" + fileName);

			// ファイル名からファイル作成の準備
			file = new File(downloadFolder + fileName);
			FileOutputStream output = new FileOutputStream(file);

			// データをサーバーから受け取る準備
			byte[] buf = new byte[1024];
			int len = 0;

			// ソケット通信によるデータの受け取りとファイルへの書き込み
			while ((len = input.read(buf)) != -1) {
				output.write(buf, 0, len);
				size += len;
			}

			System.out.println("Receive: " + size + "bytes");
			output.close();
			input.close();
			socket.close();
		} catch (Exception e) {
			try {
				socket.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}