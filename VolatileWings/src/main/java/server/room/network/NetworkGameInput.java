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

import server.room.network.packets.KeyboardStatePacket;
import server.room.network.packets.PlayerPacket;
import common.network.Hamming;
import common.network.ReedSolomon;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class NetworkGameInput extends Thread {
    private final NetworkGameInputHandler handler;
    private final int port;
    
    public NetworkGameInput(int port, NetworkGameInputHandler handler) {
        this.handler = handler;
        this.port = port;
    }
    
    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            while(true) {
                // receive a coded keyboard state
                byte[] buffer = new byte[20];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                
                // decode the keyboard state using Hamming codes
                byte[] decoded = Hamming.decode(packet.getData(), packet.getLength());
                if (decoded == null) {
                    continue; // unsuccessful decoding, discard packet
                }
                KeyboardStatePacket state = new KeyboardStatePacket(decoded);

                // notify the listener and get a feedback for the player
                PlayerPacket feedback = handler.onReceiveKeyboardState(state, packet.getAddress());
                if (feedback == null) {
                    continue; // feedback was already sent for this keyboard state
                }
                
                // encode the feedback using Reed-Solomon codes
                byte[] encoded = ReedSolomon.encode(feedback.getBytes());
                
                // send feedback to the player
                socket.send(new DatagramPacket(encoded, encoded.length, packet.getAddress(), packet.getPort()));
            }
        } catch (SocketException e) {
           e.printStackTrace(); // TODO: Use Logger
        } catch (IOException e) {
           e.printStackTrace(); // TODO: Use Logger
        }
    }
}
