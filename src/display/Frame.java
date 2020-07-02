package display;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import game.Config;
import game.State;

public class Frame extends JFrame {
	private static final long serialVersionUID = 1L;
	private Panel panel;
	
	public Frame(State state, Config config) {
		panel = new Panel(state, config);
		add(panel.getJPanel(), BorderLayout.CENTER);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Tictactoe");
		pack();
        setVisible(true);
        
        try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        panel.render();
	}
}
