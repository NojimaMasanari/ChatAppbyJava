package client;

import javax.swing.JPanel;

public interface State {
    public abstract JPanel UpdatePanel(JPanel panel);
}