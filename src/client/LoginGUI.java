package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;

public final class LoginGUI extends JPanel implements ActionListener, State {

	String id;
	char[] pass;

	JButton login_btn = new JButton("ログイン");
	JButton create_account_btn = new JButton("アカウント作成");

	JTextField idtf = new JTextField();
	JPasswordField passpf = new JPasswordField();
	JLabel id_label = new JLabel("ログインID : ");
	JLabel pass_label = new JLabel("パスワード : ");
	JLabel create_account_label = new JLabel("アカウント作成 : ");

	private static LoginGUI login_panel = new LoginGUI();
	private static JFrame login_frame = new JFrame("LoginFrame");
	Login login_instance = new Login();

	public static void main(final String[] args) {
		login_panel.setBackground(Color.white);
		login_panel.setPreferredSize(new Dimension(300, 350));
		login_frame.add("Center", login_panel);
		login_frame.setTitle("Login");
		login_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		login_frame.setResizable(true);
		login_frame.pack();
		login_frame.setVisible(true);
	}

	private LoginGUI() {
		setLayout(new BorderLayout());

		JPanel loginPanel = new JPanel(new GridBagLayout());
		Border inside = BorderFactory.createEmptyBorder(10, 7, 10, 12);
		Border outside = BorderFactory.createTitledBorder("ログイン画面");
		loginPanel.setBorder(BorderFactory.createCompoundBorder(outside, inside));

		GridBagConstraints gb_constraints = new GridBagConstraints();
		gb_constraints.gridheight = 1;

		gb_constraints.gridx = 0;
		gb_constraints.insets = new Insets(10, 10, 10, 10);
		gb_constraints.anchor = GridBagConstraints.WEST;
		gb_constraints.gridy = 0;
		loginPanel.add(id_label, gb_constraints);
		gb_constraints.gridy = 1;
		loginPanel.add(pass_label, gb_constraints);
		gb_constraints.insets = new Insets(60, 10, 10, 10);
		gb_constraints.gridy = 3;
		loginPanel.add(create_account_label, gb_constraints);

		gb_constraints.gridx = 1;
		gb_constraints.insets = new Insets(15, 10, 10, 10);
		gb_constraints.weightx = 1;
		gb_constraints.fill = GridBagConstraints.HORIZONTAL;

		gb_constraints.gridy = 0;
		loginPanel.add(idtf, gb_constraints);
		gb_constraints.gridy = 1;
		loginPanel.add(passpf, gb_constraints);
		
		gb_constraints.gridy = 2;
		loginPanel.add(login_btn, gb_constraints);
		login_btn.addActionListener(this);

		gb_constraints.insets = new Insets(60, 10, 10, 10);
		gb_constraints.gridy = 3;
		loginPanel.add(create_account_btn, gb_constraints);
		create_account_btn.addActionListener(this);

		add("Center", loginPanel);
	}

	private void caution() {
		final JFrame cautionFrame = new JFrame();
		JOptionPane.showMessageDialog(cautionFrame, "ログインできませんでした");
	}

	@Override
	public void actionPerformed(final ActionEvent e) {

		if (e.getSource() == login_btn) {
			id = idtf.getText();
			pass = passpf.getPassword();
			if (login_instance.doLogin(id, pass) == true) {
				System.out.println("login success");
				StateManager.next_panel = UpdatePanel(StateManager.connect_server_panel);

			} else {
				System.out.println("login unsuccess");
				caution();
			}
		} else if (e.getSource() == create_account_btn) {
			StateManager.next_panel = UpdatePanel(StateManager.create_account_panel);
		}
	}

	@Override
	public JPanel UpdatePanel(JPanel next_panel) {
		System.out.println("update panel");
		return next_panel;
	}

	public static LoginGUI getInstance() {
		return login_panel;
	}
}