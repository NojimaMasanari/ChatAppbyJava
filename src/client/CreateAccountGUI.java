package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class CreateAccountGUI extends JPanel implements ActionListener, State {

	String id;
	char[] pass;
	String name;
	String sex;
	int age;

	JButton createAccountBtn = new JButton("アカウント作成");
	JButton backLoginBtn = new JButton("ログイン画面へ");

	JTextField idtf = new JTextField();
	JPasswordField passpf = new JPasswordField();
	JTextField nametf = new JTextField();
	String[] sexdata = { "---", "男性", "女性" };
	JComboBox<String> sexcb = new JComboBox<String>(sexdata);
	String[] agedata = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
	JComboBox<String> agecb1 = new JComboBox<String>(agedata);
	JComboBox<String> agecb2 = new JComboBox<String>(agedata);

	JLabel id_label = new JLabel("ログインID : ");
	JLabel pass_label = new JLabel("パスワード : ");
	JLabel name_label = new JLabel("ユーザー名 : ");
	JLabel sex_label = new JLabel("性別 : ");
	JLabel age_label = new JLabel("年齢 : ");

	private static JFrame create_account_frame = new JFrame("create_account");
	private static CreateAccountGUI create_account_panel = new CreateAccountGUI();
	CreateAccount create_account_instance = new CreateAccount();

	public static void main(final String[] args) {
		create_account_panel.setBackground(Color.white);
		create_account_panel.setPreferredSize(new Dimension(300, 350));
		create_account_frame.add("Center", create_account_panel);
		create_account_frame.setTitle("CreateAccount");
		create_account_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		create_account_frame.setResizable(true);
		create_account_frame.pack();
		create_account_frame.setVisible(true);
	}

	private CreateAccountGUI() {
		setLayout(new BorderLayout());

		JPanel loginPanel = new JPanel(new GridBagLayout());
		Border inside = BorderFactory.createEmptyBorder(10, 7, 10, 12);
		Border outside = BorderFactory.createTitledBorder("アカウント作成画面");
		loginPanel.setBorder(BorderFactory.createCompoundBorder(outside, inside));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridheight = 1;

		gbc.gridx = 0;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridy = 0;
		loginPanel.add(id_label, gbc);
		gbc.gridy = 1;
		loginPanel.add(pass_label, gbc);
		gbc.gridy = 2;
		loginPanel.add(name_label, gbc);
		gbc.gridy = 3;
		loginPanel.add(sex_label, gbc);
		gbc.gridy = 4;
		loginPanel.add(age_label, gbc);
		gbc.insets = new Insets(60, 10, 10, 10);
		gbc.gridy = 5;
		loginPanel.add(backLoginBtn, gbc);
		backLoginBtn.addActionListener(this);

		gbc.gridx = 1;
		gbc.insets = new Insets(15, 10, 10, 10);
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridy = 0;
		loginPanel.add(idtf, gbc);
		gbc.gridy = 1;
		loginPanel.add(passpf, gbc);
		gbc.gridy = 2;
		loginPanel.add(nametf, gbc);
		gbc.gridy = 3;
		loginPanel.add(sexcb, gbc);
		gbc.gridy = 4;

		JPanel agePanel = new JPanel(new FlowLayout());
		agePanel.add(agecb1);
		agePanel.add(agecb2);

		loginPanel.add(agePanel, gbc);
		gbc.insets = new Insets(60, 10, 10, 10);
		gbc.gridy = 5;
		loginPanel.add(createAccountBtn, gbc);
		createAccountBtn.addActionListener(this);

		add("Center", loginPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == createAccountBtn) {
			JFrame dialogFrame = new JFrame();

			id = idtf.getText();
			pass = passpf.getPassword();
			name = nametf.getText();

			if ((String) sexcb.getSelectedItem() == "男性") {
				sex = "m";
			} else if ((String) sexcb.getSelectedItem() == "女性") {
				sex = "f";
			} else {
				sex = "n";
			}

			age = Integer.parseInt((String) agecb1.getSelectedItem()) * 10
					+ Integer.parseInt((String) agecb2.getSelectedItem());

			if (id.length() == 0) {
				JOptionPane.showMessageDialog(dialogFrame, "idを入力してください");
			} else if (pass.length == 0) {
				JOptionPane.showMessageDialog(dialogFrame, "パスワードを入力してください");
			} else if (name.length() == 0) {
				JOptionPane.showMessageDialog(dialogFrame, "名前を入力してください");
			} else if (create_account_instance.createAccount(id, pass, name, sex, age) == true) {
				JOptionPane.showMessageDialog(dialogFrame, "アカウントを作成しました");
				StateManager.next_panel = UpdatePanel(StateManager.login_panel);
			} else {
				JOptionPane.showMessageDialog(dialogFrame, "再入力してください");
			}
		} else if (e.getSource() == backLoginBtn) {
			StateManager.next_panel = UpdatePanel(StateManager.login_panel);
		}
	}

	@Override
	public JPanel UpdatePanel(JPanel panel_name) {
		System.out.println("update panel");
		return panel_name;
	}

	public static CreateAccountGUI getInstance() {
		return create_account_panel;
	}

}
