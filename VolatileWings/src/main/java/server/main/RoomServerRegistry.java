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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import server.main.network.RoomServerHealthcheckHandler;
import common.network.RoomServerHeartbeat;

public class RoomServerRegistry implements RoomServerHealthcheckHandler {
    private static final RoomServerRegistry SINGLETON_INSTANCE = new RoomServerRegistry();
    private final Map<Long, RoomServerHeartbeat> rooms = new HashMap<>();
    
    private RoomServerRegistry() {}
    
    public static RoomServerRegistry getInstance() {
        return SINGLETON_INSTANCE;
    }
    
    public LinkedList<RoomServerHeartbeat> getRoomServers() {
        synchronized (rooms) {
            return new LinkedList<>(rooms.values());
        }
    }

    @Override
    public void onRoomServerHeartbeat(RoomServerHeartbeat heartbeat, long id) {
        synchronized (rooms) {
            rooms.put(id, heartbeat);
        }
    }

    @Override
    public void onRoomServerDisconnection(long id) {
        synchronized (rooms) {
            rooms.remove(id);
        }
    }
}
