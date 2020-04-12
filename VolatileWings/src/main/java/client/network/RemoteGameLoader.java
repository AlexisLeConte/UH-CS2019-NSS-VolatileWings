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

import client.engine.SimulationEngine;
import client.engine.Time;
import common.game.asset.AssetDescriptor;
import common.game.asset.AssetRepository;
import common.network.MaxAssetUpdateRetriesExceededException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class RemoteGameLoader {
    private static final int MAX_UPDATE_RETRIES = 3;
    private static final int SOCKET_TIMEOUT = 1000;
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;
    
    public static SimulationEngine loadGame(String username, RoomServerConfig config) {
        try (Socket socket = new Socket(config.address, config.tcpClientDiscoveryPort)) {
            socket.setSoTimeout(SOCKET_TIMEOUT);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            
            update(AssetRepository.AIRCRAFT);
            update(AssetRepository.TILEMAP);
            update(AssetRepository.TILESET);
            
            // send username
            oos.writeObject(username);
            oos.flush();
            
            // get a player id or -1
            byte id = (Byte) ois.readObject();
            if (id == -1) {
                return null;
            }
            
            // get connection details
            config.udpClientUpdatePort = (Integer) ois.readObject();
            config.multicastAddress = (InetAddress) ois.readObject();
            config.udpClientMulticastPort = (Integer) ois.readObject();
            
            // synchronize to server time
            Time.synchronize((Long) ois.readObject());
            return new SimulationEngine(id, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static void update(String filename) throws MaxAssetUpdateRetriesExceededException {
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
}
