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

import client.network.packets.AircraftPacket;
import client.network.packets.BulletPacket;
import client.network.packets.KillFeedPacket;
import client.network.packets.NetworkPacket;
import client.network.packets.PlayerJoinedPacket;
import client.network.packets.PlayerLeftPacket;
import client.network.packets.ScoreboardPacket;
import common.network.ReedSolomon;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class GameStateConnector extends Thread {
    private final GameStateInputHandler handler;
    private final RoomServerConfig config;
    
    public GameStateConnector(RoomServerConfig config, GameStateInputHandler handler) {
        this.config = config;
        this.handler = handler;
    }
    
    @Override
    public void run() {
        try (MulticastSocket socket = new MulticastSocket(config.udpClientMulticastPort)) {
            socket.joinGroup(config.multicastAddress);
            while(true) {
                byte[] buffer = new byte[512];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                try {
                    byte[] decoded = ReedSolomon.decode(packet.getData(), packet.getLength());
                    switch(decoded[0]) {
                        case NetworkPacket.AIRCRAFT_PACKET:
                            handler.onReceiveAircraftPacket(new AircraftPacket(decoded));
                            break;
                        case NetworkPacket.BULLET_PACKET:
                            handler.onReceiveBulletPacket(new BulletPacket(decoded));
                            break;
                        case NetworkPacket.SCOREBOARD_PACKET:
                            handler.onReceiveScoreboardPacket(new ScoreboardPacket(decoded));
                            break;
                        case NetworkPacket.KILLFEED_PACKET:
                            handler.onReceiveKillFeedPacket(new KillFeedPacket(decoded));
                            break;
                        case NetworkPacket.JOINED_PACKET:
                            handler.onReceivePlayerJoinedPacket(new PlayerJoinedPacket(decoded));
                            break;
                        case NetworkPacket.LEFT_PACKET:
                            handler.onReceivePlayerLeftPacket(new PlayerLeftPacket(decoded));
                            break;
                        default:
                            System.out.println("Received unexpected packet code: " + decoded[0]);
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // corrupt packet detected
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // fatal network error
        }
    }
}
