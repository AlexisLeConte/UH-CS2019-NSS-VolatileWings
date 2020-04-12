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

package client;

import client.engine.SimulationEngine;
import client.network.MainServerDiscoveryThread;
import client.network.RoomServerConfig;
import java.util.LinkedList;
import client.network.MainServerDiscoveryHandler;
import client.network.RemoteGameLoader;

public class ClientController implements MainServerDiscoveryHandler {
    private static final ClientController INSTANCE = new ClientController();
    private final LinkedList<ClientControllerListener> listeners = new LinkedList<>();
    private MainServerDiscoveryThread mainServerDiscoveryThread;
    private String username;
    private SimulationEngine engine;
    
    public static ClientController getInstance() {
        return INSTANCE;
    }
    
    private ClientController() {
        
    }
    
    public void addListener(ClientControllerListener listener) {
        new Thread() {
            @Override
            public void run() {
                synchronized(listeners) {
                    listeners.add(listener);
                }
            }
        }.start();
    }
    
    public void removeListener(ClientControllerListener listener) {
        new Thread() {
            @Override
            public void run() {
                synchronized(listeners) {
                    listeners.remove(listener);
                }
            }
        }.start();
    }
    
    public void login(String username) {
        this.username = username;
        synchronized(listeners) {
            for (ClientControllerListener listener : listeners) {
                listener.onLogin(username);
            }
        }
        if (mainServerDiscoveryThread != null) {
            mainServerDiscoveryThread.close();
        }
        mainServerDiscoveryThread = new MainServerDiscoveryThread(this);
        mainServerDiscoveryThread.start();
    }
    
    public void connect(RoomServerConfig config) {
        if (mainServerDiscoveryThread != null) {
            mainServerDiscoveryThread.close();
            mainServerDiscoveryThread = null;
        }
        engine = RemoteGameLoader.loadGame(username, config);
        if (engine != null) {
            engine.start();
            synchronized(listeners) {
                for (ClientControllerListener listener : listeners) {
                    listener.onStartGame();
                }
            }
        }
    }
    
    public void disconnect() {
        if (engine != null) {
            engine.kill();
        }
        login(username);
    }
    
    public void exit() {
        synchronized(listeners) {
            for (ClientControllerListener listener : listeners) {
                listener.onExit();
            }
        }
    }
    
    public SimulationEngine getSimulationEngine() {
        return engine;
    }

    @Override
    public void onReceiveRoomServersConfigs(LinkedList<RoomServerConfig> configs) {
        synchronized(listeners) {
            for (ClientControllerListener listener : listeners) {
                listener.onReceiveRoomServersConfigs(configs);
            }
        }
    }
}
