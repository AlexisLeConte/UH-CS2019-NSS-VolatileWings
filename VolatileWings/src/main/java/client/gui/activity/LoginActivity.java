/****************************************************************************************
 *  Volatile Wings - a multiplayer networked aircraft shooter game
 *  Copyright (C) 2019 Alexis Le Conte (lecontea@helsinki.fi) Student ID: 015148054
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ***************************************************************************************/

package client.gui.activity;

import client.ClientController;
import client.gui.widget.Button;
import client.gui.style.GuiStyles;
import client.gui.widget.Label;
import client.gui.widget.Panel;
import client.gui.widget.TextField;
import client.network.RoomServerConfig;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.JFrame;

public final class LoginActivity extends Activity {
	private final Button login = new Button("Login", GuiStyles.CONFIRM);
	private final Button exit = new Button("Exit", GuiStyles.CANCEL);
	private TextField username = new TextField();
	
	public LoginActivity(JFrame parent) {
		this.setLayout(new GridBagLayout());
        this.setBackground(GuiStyles.MEDIUM_BACKGROUND);
		
		Panel centerPanel = new Panel(new Dimension(240, 120));
		centerPanel.setBorder(BorderFactory.createLineBorder(GuiStyles.DARK_FOREGROUND));
		
		Panel loginPanel = new Panel(new Dimension(220, 100));
		loginPanel.setLayout(new GridLayout(3, 1, 0, 5));
		
		Panel buttonsPanel = new Panel();
		buttonsPanel.setLayout(new GridLayout(1, 2, 5, 0));
		buttonsPanel.add(login);
		buttonsPanel.add(exit);
		
		login.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
				if(isValidUsername(username.getText())) {
                    ClientController.getInstance().login(username.getText());
                }
			}
		});
		
		username.addKeyListener(new KeyListener() {
            @Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER && isValidUsername(username.getText())) {
                    ClientController.getInstance().login(username.getText());
                }
			}
            
            @Override
			public void keyTyped(KeyEvent e) {}
            
            @Override
			public void keyReleased(KeyEvent e) {}
		});
		
		exit.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                ClientController.getInstance().exit();
			}
		});
		
		loginPanel.add(new Label("Username"));
		loginPanel.add(username);
		loginPanel.add(buttonsPanel);
		
		centerPanel.add(loginPanel);
		this.add(centerPanel);
		username.requestFocusInWindow();
	}
	
	private boolean isValidUsername(String username) {
		if(username.isEmpty() || username.length() > 16) {
            return false;
        }
		for(char c : username.toCharArray()) {
			if(!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9'))) {
				return false;
            }
        }
		return true;
	}

    @Override
    public void onLogin(String username) {}

    @Override
    public void onReceiveRoomServersConfigs(LinkedList<RoomServerConfig> config) {}

    @Override
    public void onStartGame() {}

    @Override
    public void onStopGame() {}

    @Override
    public void onExit() {}
}
