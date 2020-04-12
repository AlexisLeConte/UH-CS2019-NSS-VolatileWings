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

package client;

import client.gui.ClientApplication;
import client.network.ClientNetworkConfig;
import common.game.asset.AssetRepository;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private static final Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        //if (args.length == 1 && args[0].equals("DEBUG")) {
            try {
                ClientNetworkConfig.mainServerAddress = InetAddress.getLocalHost();
                ClientNetworkConfig.tcpMainServerDiscoveryPort = 4200;
            } catch (UnknownHostException e) {}
        /*} else {
            try {
                System.out.print("Main Server Address: ");
                ClientNetworkConfig.mainServerAddress = InetAddress.getByName(scanner.next());
                System.out.print("Main Server Discovery Port: ");
                ClientNetworkConfig.tcpMainServerDiscoveryPort = scanner.nextInt();
            } catch (UnknownHostException ex) {}
        }*/
        AssetRepository.getInstance().setRepositoryLocation("src/main/resources/client/");
        new ClientApplication().setVisible(true);
    }
}
