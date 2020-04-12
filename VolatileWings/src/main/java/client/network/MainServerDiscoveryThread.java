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

package client.network;

import common.network.RoomServerHeartbeat;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.LinkedList;

public class MainServerDiscoveryThread extends Thread {
    private final MainServerDiscoveryHandler handler;
    private Socket socket;
    private ObjectInputStream ois;
    
    public MainServerDiscoveryThread(MainServerDiscoveryHandler handler) {
        this.handler = handler;
    }
    
    @Override
    public void run() {
        try {
            socket = new Socket(
                ClientNetworkConfig.mainServerAddress,
                ClientNetworkConfig.tcpMainServerDiscoveryPort
            );
            ois = new ObjectInputStream(socket.getInputStream());
            while (!isInterrupted()) {
                LinkedList<RoomServerConfig> rooms = new LinkedList<>();
                int numberOfRooms = (int) ois.readInt();
                for (int i = 0; i < numberOfRooms; ++i) {
                    RoomServerHeartbeat heartbeat = (RoomServerHeartbeat) ois.readObject();
                    RoomServerConfig config = new RoomServerConfig();
                    config.maxNumberOfPlayers = heartbeat.maxNumberOfPlayers;
                    config.numberOfPlayers = heartbeat.numberOfPlayers;
                    config.address = heartbeat.address;
                    config.tcpClientDiscoveryPort = heartbeat.tcpClientDiscoveryPort;
                    rooms.add(config);
                }
                handler.onReceiveRoomServersConfigs(rooms);
            }
        } catch (IOException | ClassNotFoundException e) {
            close();
        }
    }
    
    public void close() {
        interrupt();
        try {
            socket.close();
        } catch (IOException e) {}
    }
}
