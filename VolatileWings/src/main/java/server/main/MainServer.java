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

package server.main;

import common.game.asset.AssetRepository;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import server.main.network.ClientDiscoveryConnector;
import server.main.network.MainServerNetworkConfig;
import server.main.network.RoomServerDiscoveryConnector;

public class MainServer {
    private static final Scanner scanner = new Scanner(System.in);
    private static RoomServerDiscoveryConnector roomConnector;
    private static ClientDiscoveryConnector clientConnector;
    
    public static void main(String[] args) {
        //if (args.length == 1 && args[0].equals("DEBUG")) {
            MainServerNetworkConfig.tcpClientDiscoveryPort = 4200;
            MainServerNetworkConfig.tcpRoomServerDiscoveryPort = 4201;
        /*} else {
            try {
                System.out.println("Server Address is: " + InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException ex) {}
            System.out.print("Room Server Discovery Port: ");
            MainServerNetworkConfig.tcpRoomServerDiscoveryPort = scanner.nextInt();
            System.out.print("Client Discovery Port: ");
            MainServerNetworkConfig.tcpClientDiscoveryPort = scanner.nextInt();
        }*/
        AssetRepository.getInstance().setRepositoryLocation("src/main/resources/main/");
        
        clientConnector = new ClientDiscoveryConnector();
        clientConnector.start();
        
        roomConnector = new RoomServerDiscoveryConnector(RoomServerRegistry.getInstance());
        roomConnector.start();
        
        String command;
        do {
            System.out.println("> ");
            command = scanner.nextLine();
        } while (!command.equals("exit"));
        
        clientConnector.close();
        roomConnector.close();
    }
}
