package client;

import static client.Constants.*;

import java.awt.Dimension;

import javax.swing.JPanel;

public class StateManager extends Thread {
    static MainFrame main_frame = MainFrame.getInstance();
    static LoginGUI login_panel = LoginGUI.getInstance();
    static CreateAccountGUI create_account_panel = CreateAccountGUI.getInstance();
    static ChatClientGUI chat_client_panel = ChatClientGUI.getInstance();
    static ConnectServerGUI connect_server_panel = ConnectServerGUI.getInstance();

    public static JPanel now_panel;
    public static JPanel next_panel;

    public static void main(final String[] args) throws InterruptedException {
        new StateManager();
    }

    public StateManager() {
        main_frame.setVisible(true);
        now_panel = login_panel;
        next_panel = login_panel;
        now_panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        next_panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        main_frame.changePanel(login_panel);
        this.start();
    }

    public void run() {
        while (true) {
            if (now_panel != next_panel) {
                now_panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
                next_panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
                main_frame.changePanel(next_panel);
            }
            now_panel = next_panel;
            try {
                //ここで時間をとらないと画面が遷移しないことがある
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}