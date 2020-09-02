package client;

import javax.swing.JFrame;
import javax.swing.JPanel;

//import static client.Constants.WIDTH;
//import static client.Constants.HEIGHT;

public class MainFrame extends JFrame {
	private static MainFrame main_frame = new MainFrame(WIDTH, HEIGHT);

	private MainFrame(int width, int height) {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(width, height);
		setLocationRelativeTo(null);
		setResizable(true);
	}

	public void changePanel(JPanel panel) {
		getContentPane().removeAll();
		super.add(panel);
		this.pack();
		this.setVisible(true);
	}

	public static MainFrame getInstance() {
		return main_frame;
	}
}