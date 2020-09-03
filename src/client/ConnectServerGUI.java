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
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import static client.Constants.*;

public final class ConnectServerGUI extends JPanel implements ActionListener, State {

    int port = DEFAULT_PORT;
    String host = DEFAULT_HOST;

    JLabel user_label = new JLabel("ユーザー名　: ");
    JLabel port_label = new JLabel("ポート番号 : ");
    JLabel IP_label = new JLabel("IPアドレス : ");

    JTextField user_name = new JTextField("名無し");
    JButton connect_btn = new JButton("サーバー接続");
    JButton back_login_btn = new JButton("ログイン画面へ");

    JTextField port_tf = new JTextField(String.valueOf(port));
    JTextField IP_tf = new JTextField(host);

    private static ConnectServerGUI connect_server_panel = new ConnectServerGUI();
    private static JFrame connect_server_frame = new JFrame("ConnectFrame");

    public static void main(final String[] args) {
        connect_server_panel.setBackground(Color.white);
        connect_server_panel.setPreferredSize(new Dimension(300, 350));
        connect_server_frame.add("Center", connect_server_panel);
        connect_server_frame.setTitle("Login");
        connect_server_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        connect_server_frame.setResizable(true);
        connect_server_frame.pack();
        connect_server_frame.setVisible(true);
    }

    private ConnectServerGUI() {
        setLayout(new BorderLayout());

        JPanel loginPanel = new JPanel(new GridBagLayout());
        Border inside = BorderFactory.createEmptyBorder(10, 7, 10, 12);
        Border outside = BorderFactory.createTitledBorder("サーバー接続画面");
        loginPanel.setBorder(BorderFactory.createCompoundBorder(outside, inside));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridheight = 1;

        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 0;
        loginPanel.add(user_label, gbc);
        gbc.gridy = 1;
        loginPanel.add(port_label, gbc);
        gbc.gridy = 2;
        loginPanel.add(IP_label, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(15, 10, 10, 10);
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy = 0;
        loginPanel.add(user_name, gbc);
        gbc.gridy = 1;
        loginPanel.add(port_tf, gbc);
        gbc.gridy = 2;
        loginPanel.add(IP_tf, gbc);
        gbc.gridy = 3;
        loginPanel.add(connect_btn, gbc);
        connect_btn.addActionListener(this);

        gbc.insets = new Insets(60, 10, 10, 10);
        gbc.gridy = 4;
        loginPanel.add(back_login_btn, gbc);
        back_login_btn.addActionListener(this);

        add("Center", loginPanel);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {

        if (e.getSource() == connect_btn) {
            System.out.println("pushed connect btn");
            host = IP_tf.getText();
            port = Integer.parseInt(port_tf.getText());

            if (ConnectServer.clientOpen(host, port))
                StateManager.chat_client_panel.start();
                StateManager.chat_client_panel.launchSetFTPServer();
                StateManager.next_panel = UpdatePanel(StateManager.chat_client_panel);
                StateManager.chat_client_panel.writeMes(user_name.getText());

        } else if (e.getSource() == back_login_btn) {
            StateManager.next_panel = UpdatePanel(StateManager.login_panel);
        }
    }

    @Override
    public JPanel UpdatePanel(JPanel next_panel) {
        System.out.println("update panel");
        return next_panel;
    }

    public static ConnectServerGUI getInstance() {
        return connect_server_panel;
    }
}
