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

package server.room.network;

import common.game.asset.AssetDescriptor;
import common.game.asset.AssetRepository;
import common.network.MaxAssetUpdateRetriesExceededException;
import common.network.RoomServerHeartbeat;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class MainServerDiscoveryConnector {
    private final int SOCKET_TIMEOUT = 1000;
    private Socket server;
    
    public boolean register() {
        try {
            server = new Socket(
                RoomServerNetworkConfig.mainServerAddress,
                RoomServerNetworkConfig.tcpMainServerDiscoveryPort
            );
            server.setSoTimeout(SOCKET_TIMEOUT);
            ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(server.getInputStream());
            new RoomServerUpdateWorker(server, oos, ois).start();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    
    public void close() {
        try {
            server.close();
        } catch (IOException e) {}
    }
}

class RoomServerUpdateWorker extends Thread {
    private static final int MAX_UPDATE_RETRIES = 3;
    
    private final Socket server;
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;
    
    public RoomServerUpdateWorker(
        Socket server,
        ObjectOutputStream oos,
        ObjectInputStream ois
    ) {
        this.server = server;
        this.oos = oos;
        this.ois = ois;
    }
    
    @Override
    public void run() {
        try {
            update(AssetRepository.AIRCRAFT);
            update(AssetRepository.TILEMAP);
            update(AssetRepository.TILESET);
            new RoomServerHealthcheckWorker(server, oos).start();
        } catch (MaxAssetUpdateRetriesExceededException e) {
            close();
        }
    }
    
    private void update(String filename) throws MaxAssetUpdateRetriesExceededException {
        AssetRepository repository = AssetRepository.getInstance();
        int numberOfRetries = 0;
        do {
            try {
                AssetDescriptor remote = (AssetDescriptor) ois.readObject();
                AssetDescriptor local = repository.getDescriptor(filename);
                oos.writeObject(local);
                oos.flush();
                if (!local.equals(remote)) {
                    repository.copyStreamToAsset(ois, remote);
                    local = repository.getDescriptor(remote.filename);
                    if (local.equals(remote)) {
                        oos.writeBoolean(Boolean.TRUE);
                        oos.flush();
                        break;
                    } else {
                        oos.writeBoolean(Boolean.FALSE);
                        oos.flush();
                    }
                } else {
                    break;
                }
            } catch (Exception e) {}
            numberOfRetries++;
        } while (numberOfRetries <= MAX_UPDATE_RETRIES);
        if (numberOfRetries > MAX_UPDATE_RETRIES) {
            throw new MaxAssetUpdateRetriesExceededException(filename);
        }
    }
    
    private void close() {
        try {
            server.close();
        } catch (IOException e) {}
    }
}

class RoomServerHealthcheckWorker extends Thread {
    private final Socket socket;
    private final ObjectOutputStream oos;
    private final long UPDATE_DELAY = 1000;
    
    public RoomServerHealthcheckWorker(Socket socket, ObjectOutputStream oos) {
        this.socket = socket;
        this.oos = oos;
    }
    
    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                RoomServerHeartbeat heartbeat = new RoomServerHeartbeat();
                
                // TODO: retrieve game specific data
                heartbeat.maxNumberOfPlayers = 16;
                heartbeat.numberOfPlayers = 0;
                heartbeat.address = InetAddress.getLocalHost();
                heartbeat.tcpClientDiscoveryPort = RoomServerNetworkConfig.tcpClientDiscoveryPort;
                oos.writeObject(heartbeat);
                oos.flush();
                Thread.currentThread().sleep(UPDATE_DELAY);
            }
        } catch (IOException | InterruptedException e) {
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
