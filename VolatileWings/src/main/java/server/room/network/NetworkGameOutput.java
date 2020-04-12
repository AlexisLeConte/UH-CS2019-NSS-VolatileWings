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

import server.room.network.packets.NetworkPacket;
import common.network.ReedSolomon;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;

public class NetworkGameOutput {
    private final LinkedList<NetworkPacket> events = new LinkedList<>();
    private final InetAddress group;
    private final int port;
    
    public NetworkGameOutput(InetAddress group, int port) {
        this.group = group;
        this.port = port;
    }
    
    public void push(NetworkPacket packet) {
        synchronized(events) {
            events.add(packet);
        }
    }
    
    public void send() {
        try (DatagramSocket socket = new DatagramSocket()) {
            synchronized(events) {
                for (NetworkPacket event : events) {
                    // encode the update using Reed-Solomon codes
                    byte[] encoded = ReedSolomon.encode(event.getBytes());

                    // send the update to everyone (multicast)
                    socket.send(new DatagramPacket(encoded, encoded.length, group, port));
                }
                events.clear();
            }
        } catch (IOException e) {
            e.printStackTrace(); // TODO: Use Logger
        }
    }
}
