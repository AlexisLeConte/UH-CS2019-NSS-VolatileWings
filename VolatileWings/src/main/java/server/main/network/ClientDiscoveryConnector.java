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

package server.main.network;

import common.network.RoomServerHeartbeat;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import server.main.RoomServerRegistry;

public class ClientDiscoveryConnector extends Thread {
    private ServerSocket server;
    
    @Override
    public void run() {
        try {
            server = new ServerSocket(MainServerNetworkConfig.tcpClientDiscoveryPort);
            while (!isInterrupted()) {
                Socket socket = server.accept();
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                new ClientDiscoveryWorker(socket, oos).start();
            }
        } catch (IOException e) {}
    }
    
    public void close() {
        interrupt();
        try {
            server.close();
        } catch (IOException e) {}
    }
}

class ClientDiscoveryWorker extends Thread {
    private final Socket socket;
    private final ObjectOutputStream oos;
    private final long UPDATE_DELAY_MS = 1000;
    
    public ClientDiscoveryWorker(Socket socket, ObjectOutputStream oos) {
        this.socket = socket;
        this.oos = oos;
    }
    
    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                RoomServerRegistry registry = RoomServerRegistry.getInstance();
                LinkedList<RoomServerHeartbeat> rooms = registry.getRoomServers();
                oos.writeInt(rooms.size());
                for (RoomServerHeartbeat heartbeat : rooms) {
                    oos.writeObject(heartbeat);
                }
                oos.flush();
                Thread.currentThread().sleep(UPDATE_DELAY_MS);
            }
        } catch (IOException | InterruptedException e) {
            close();
        }
    }
    
    private void close() {
        interrupt();
        try {
            socket.close();
        } catch (IOException e) {}
    }
}
