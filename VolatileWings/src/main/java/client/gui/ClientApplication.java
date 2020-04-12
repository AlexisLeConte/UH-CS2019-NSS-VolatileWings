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

package client.gui;

import client.ClientController;
import client.ClientControllerListener;
import client.gui.activity.Activity;
import client.gui.activity.GameActivity;
import client.gui.activity.LobbyActivity;
import client.gui.activity.LoginActivity;
import client.network.RoomServerConfig;
import java.awt.Dimension;
import java.util.LinkedList;
import javax.swing.JFrame;

public final class ClientApplication extends JFrame implements ClientControllerListener {
    public ClientApplication() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("Volatile Wings");
		this.setSize(new Dimension(800, 800));
		this.setMinimumSize(new Dimension(800, 800));
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.showActivity(new LoginActivity(this));
        ClientController.getInstance().addListener(this);
	}
    
    private void showActivity(Activity activity) {
        if (getContentPane() instanceof Activity) {
            ClientController.getInstance().removeListener((Activity) getContentPane());
        }
        ClientController.getInstance().addListener(activity);
        setContentPane(activity);
        activity.revalidate();
        activity.repaint();
    }

    @Override
    public void onLogin(String username) {
        showActivity(new LobbyActivity());
    }

    @Override
    public void onReceiveRoomServersConfigs(LinkedList<RoomServerConfig> configs) {}

    @Override
    public void onStartGame() {
        showActivity(new GameActivity(getWidth(), getHeight()));
    }

    @Override
    public void onStopGame() {
        showActivity(new LobbyActivity());
    }

    @Override
    public void onExit() {
        dispose();
    }
}
