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

import client.network.packets.KeyboardStatePacket;
import client.network.packets.PlayerPacket;
import common.network.Hamming;
import common.network.ReedSolomon;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class PlayerStateConnector {
    private PlayerStateReader reader;
    private RoomServerConfig config;
    private DatagramSocket socket;
    
    public PlayerStateConnector(RoomServerConfig config, PlayerStateInputHandler handler) {
        this.config = config;
        try {
            socket = new DatagramSocket();
            reader = new PlayerStateReader(socket, handler);
            reader.start();
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
    }
    
    public void send(KeyboardStatePacket state) {
        // encode the keyboard state using Hamming codes
        byte[] encoded = Hamming.encode(state.getBytes());

        try {
            // send the state to the player using triple redundancy
            for (int i = 0; i < 3; ++i) {
                socket.send(new DatagramPacket(encoded, encoded.length, config.address, config.udpClientUpdatePort));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void close() {
        
    }
}

class PlayerStateReader extends Thread {
    private final PlayerStateInputHandler handler;
    private final DatagramSocket socket;
    
    public PlayerStateReader(DatagramSocket socket, PlayerStateInputHandler handler) {
        this.handler = handler;
        this.socket = socket;
    }
    
    @Override
    public void run() {
        while(true) {
            try {
                // receive a coded player state feedback
                byte[] buffer = new byte[64];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                
                // decode the player state using Reed-Solomon codes
                byte[] decoded = ReedSolomon.decode(packet.getData(), packet.getLength());
                if (decoded == null) {
                    continue; // unsuccessful decoding, discard packet
                }
                handler.onReceivePlayerState(new PlayerPacket(decoded));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
