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

package server.room;


import common.game.asset.AssetRepository;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import server.room.network.MainServerDiscoveryConnector;
import server.room.network.ClientDiscoveryConnector;
import server.room.network.RoomServerNetworkConfig;

public class RoomServer {
    private static MainServerDiscoveryConnector mainServerConnector;
    private static ClientDiscoveryConnector clientConnector;
    private static final Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        //if (args.length == 1 && args[0].equals("DEBUG")) {
            try {
                RoomServerNetworkConfig.multicastAddress = Inet4Address.getByName("233.0.0.0");
                RoomServerNetworkConfig.udpClientMulticastPort = 5000;
                RoomServerNetworkConfig.udpClientUpdatePort = 6000;
                RoomServerNetworkConfig.tcpClientDiscoveryPort = 7000;
                RoomServerNetworkConfig.mainServerAddress = InetAddress.getLocalHost();
                RoomServerNetworkConfig.tcpMainServerDiscoveryPort = 4201;
            } catch (UnknownHostException e) {}
        /*} else {
            try {
                System.out.print("Main Server Address: ");
                RoomServerNetworkConfig.mainServerAddress = Inet4Address.getByName(scanner.next());
                System.out.print("Main Server Port: ");
                RoomServerNetworkConfig.tcpMainServerDiscoveryPort = scanner.nextInt();
                System.out.print("Client Discovery Port: ");
                RoomServerNetworkConfig.tcpClientDiscoveryPort = scanner.nextInt();
                System.out.print("Client Update Port: ");
                RoomServerNetworkConfig.udpClientUpdatePort = scanner.nextInt();
                System.out.print("Client Multicast Address: ");
                RoomServerNetworkConfig.multicastAddress = Inet4Address.getByName(scanner.next());
                System.out.print("Client Multicast Port: ");
                RoomServerNetworkConfig.udpClientMulticastPort = scanner.nextInt();
            } catch (UnknownHostException e) {}
        }*/
        AssetRepository.getInstance().setRepositoryLocation("src/main/resources/room/");
        
        mainServerConnector = new MainServerDiscoveryConnector();
        if (!mainServerConnector.register()) {
            return;
        }
        
        GameEngine engine = new GameEngine();
        
        clientConnector = new ClientDiscoveryConnector(engine);
        clientConnector.start();
        
        engine.start();
        
        while(true) {
            System.out.print("> ");
            String[] command = scanner.nextLine().split(" ");
            if (command.length == 2 && command[0].equals("scale")) {
                byte scale = Byte.parseByte(command[1]);
                engine.setMaximumNumberOfPlayers(scale);
            }
        }
    }
}
