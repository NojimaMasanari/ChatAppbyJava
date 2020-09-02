package client;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class FTPSendClient {

	public static void fileSend(String fileNameWithPath, String host, int port) {
		File file = null;
		int size = 0;
		try {
			System.out.println("FTPSendClient: File name ：" + fileNameWithPath);

			// ファイルを取り込む準備
			file = new File(fileNameWithPath);
			String FileNameOnly = file.getName();
			FileInputStream input = new FileInputStream(file);

			// ソケットを生成
			Socket socket = new Socket(host, port);

			// 出力用ストリームの準備
			BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream());

			// ファイル名の文字数を送信
			output.write(FileNameOnly.getBytes().length);

			// ファイル名を送信
			byte fileNameByte[] = FileNameOnly.getBytes();
			output.write(fileNameByte, 0, FileNameOnly.getBytes().length);

			// データをサーバーに送る準備
			byte[] buf = new byte[1024];
			int len = 0;

			// ファイルからのデータ読み込みとソケット通信によるデータ送信
			while ((len = input.read(buf)) != -1) {
				output.write(buf, 0, len);
				size += len;
			}

			System.out.println("FTPSendClient: Send: " + size + "bytes");

			output.close();
			input.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}