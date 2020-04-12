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
import client.gui.style.GuiStyles;
import client.gui.widget.Button;
import client.network.RoomServerConfig;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import javax.swing.SwingUtilities;

public final class LobbyActivity extends Activity {
    private final LinkedList<RoomServerConfig> configs = new LinkedList<>();
    
    public LobbyActivity() {
        setBackground(GuiStyles.MEDIUM_BACKGROUND);
    }
    
    private void updateRoomServersList() {
        removeAll();
        synchronized (configs) {
            for (int i = 0; i < configs.size(); ++i) {
                Button button = new Button("Room #" + (i+1));
                button.addActionListener(new RoomServerButtonListener(configs.get(i)));
                add(button);
            }
        }
        revalidate();
    }

    @Override
    public void onLogin(String username) {}

    @Override
    public void onReceiveRoomServersConfigs(LinkedList<RoomServerConfig> configs) {
        synchronized (this.configs) {
            this.configs.clear();
            this.configs.addAll(configs);
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateRoomServersList();
            }
        });
    }

    @Override
    public void onStartGame() {}

    @Override
    public void onStopGame() {}

    @Override
    public void onExit() {}
}

class RoomServerButtonListener implements ActionListener {
    private final RoomServerConfig config;
    
    public RoomServerButtonListener(RoomServerConfig config) {
        this.config = config;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ClientController.getInstance().connect(config);
    }    
}
