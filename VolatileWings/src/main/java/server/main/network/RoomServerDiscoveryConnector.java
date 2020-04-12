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

import common.network.MaxAssetUpdateRetriesExceededException;
import common.game.asset.AssetDescriptor;
import common.game.asset.AssetRepository;
import common.network.RoomServerHeartbeat;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class RoomServerDiscoveryConnector extends Thread {
    private final RoomServerHealthcheckHandler handler;
    private ServerSocket server;
    
    public RoomServerDiscoveryConnector(RoomServerHealthcheckHandler handler) {
        this.handler = handler;
    }
    
    @Override
    public void run() {
        try {
            server = new ServerSocket(MainServerNetworkConfig.tcpRoomServerDiscoveryPort);
            while (!isInterrupted()) {
                Socket socket = server.accept();
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                new RoomServerUpdateWorker(socket, oos, ois, handler).start();
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

class RoomServerUpdateWorker extends Thread {
    private static final int MAX_UPDATE_RETRIES = 3;
    
    private final Socket socket;
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;
    private final RoomServerHealthcheckHandler handler;
    
    public RoomServerUpdateWorker(
        Socket socket,
        ObjectOutputStream oos,
        ObjectInputStream ois,
        RoomServerHealthcheckHandler handler
    ) {
        this.socket = socket;
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
            new RoomServerHealthcheckWorker(socket, oos, ois, handler).start();
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
            socket.close();
        } catch (IOException e) {}
    }
}

class RoomServerHealthcheckWorker extends Thread {
    private static long workerCount;
    private final long workerId;
    private final Socket socket;
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;
    private final RoomServerHealthcheckHandler handler;
    
    public RoomServerHealthcheckWorker(
        Socket socket,
        ObjectOutputStream oos,
        ObjectInputStream ois,
        RoomServerHealthcheckHandler handler
    ) {
        this.workerId = workerCount++;
        this.socket = socket;
        this.oos = oos;
        this.ois = ois;
        this.handler = handler;
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                RoomServerHeartbeat heartbeat = (RoomServerHeartbeat) ois.readObject();
                handler.onRoomServerHeartbeat(heartbeat, workerId);
            }
        } catch (IOException | ClassNotFoundException e) {
            close();
        }
    }
    
    private void close() {
        handler.onRoomServerDisconnection(workerId);
        try {
            socket.close();
        } catch (IOException e) {}
    }
}
