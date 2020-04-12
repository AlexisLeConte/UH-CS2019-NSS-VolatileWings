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

package client.network.packets;

import java.nio.ByteBuffer;

public class PlayerJoinedPacket extends NetworkPacket {
    private final String username;
    private final byte team;
    
    public PlayerJoinedPacket(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.get(); // discard packet type
        byte[] usernameBytes = new byte[MAX_USERNAME_LENGTH];
        buffer.get(usernameBytes);
        username = bytesToUsername(usernameBytes);
        team = buffer.get();
    }
    
    @Override
    public byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(MAX_USERNAME_LENGTH + 2);
        buffer.put(NetworkPacket.JOINED_PACKET);
        buffer.put(usernameToBytes(username));
        buffer.put(team);
        return buffer.array();
    }

    public String getUsername() {
        return username;
    }

    public byte getTeam() {
        return team;
    }
}
