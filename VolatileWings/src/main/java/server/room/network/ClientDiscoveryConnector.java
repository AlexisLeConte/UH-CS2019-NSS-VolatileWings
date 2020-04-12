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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientDiscoveryConnector extends Thread {
    private final NetworkClientDiscoveryHandler handler;
    
    public ClientDiscoveryConnector(NetworkClientDiscoveryHandler handler) {
        this.handler = handler;
    }
    
    @Override
    public void run() {
        try (ServerSocket listener = new ServerSocket(RoomServerNetworkConfig.tcpClientDiscoveryPort)) {
            while(true) {
                Socket client = listener.accept();
                ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
                new ClientUpdateWorker(client, oos, ois, handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientUpdateWorker extends Thread {
    private static final int MAX_UPDATE_RETRIES = 3;
    
    private final Socket client;
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;
    private final NetworkClientDiscoveryHandler handler;
    
    public ClientUpdateWorker(
        Socket client,
        ObjectOutputStream oos,
        ObjectInputStream ois,
        NetworkClientDiscoveryHandler handler
    ) {
        this.client = client;
        this.oos = oos;
        this.ois = ois;
        this.handler = handler;
    }
    
    @Override
    public void run() {
        try {
            update(AssetRepository.AIRCRAFT);
            update(AssetRepository.TILEMAP);
            update(AssetRepository.TILESET);
            new ClientRegistrationWorker(client, oos, ois, handler).start();
        } catch (MaxAssetUpdateRetriesExceededException e) {
            e.printStackTrace();
            close();
        }
    }
    
    private void update(String filename) throws MaxAssetUpdateRetriesExceededException {
        AssetRepository repository = AssetRepository.getInstance();
        boolean success = false;
        int numberOfRetries = 0;
        do {
            try {
                AssetDescriptor local = repository.getDescriptor(filename);
                oos.writeObject(local);
                oos.flush();
                AssetDescriptor remote = (AssetDescriptor) ois.readObject();
                if (!local.equals(remote)) {
                    repository.copyAssetToStream(local, oos);
                    success = ois.readBoolean();
                } else {
                    success = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            numberOfRetries++;
        } while (!success && numberOfRetries <= MAX_UPDATE_RETRIES);
        if (!success) {
            throw new MaxAssetUpdateRetriesExceededException(filename);
        }
    }
    
    private void close() {
        try {
            client.close();
        } catch (IOException e) {}
    }
}

class ClientRegistrationWorker extends Thread {
    private final Socket client;
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;
    private final NetworkClientDiscoveryHandler handler;
    
    public ClientRegistrationWorker(
        Socket client,
        ObjectOutputStream oos,
        ObjectInputStream ois,
        NetworkClientDiscoveryHandler handler
    ) {
        this.client = client;
        this.oos = oos;
        this.ois = ois;
        this.handler = handler;
    }
    
    @Override
    public void run() {
        try {
            String username = (String) ois.readObject();
            byte id = handler.registerPlayer(username, client.getInetAddress());
            
            oos.writeObject(id);
            if (id != -1) {
                oos.writeObject(RoomServerNetworkConfig.udpClientUpdatePort);
                oos.writeObject(RoomServerNetworkConfig.multicastAddress);
                oos.writeObject(RoomServerNetworkConfig.udpClientMulticastPort);
                oos.writeObject(System.currentTimeMillis());
            }
            oos.flush();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }
    
    private void close() {
        try {
            client.close();
        } catch (IOException e) {}
    }
}
