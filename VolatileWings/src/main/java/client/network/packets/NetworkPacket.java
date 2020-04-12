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

public abstract class NetworkPacket {
    public static final byte BULLET_PACKET = 0;
    public static final byte AIRCRAFT_PACKET = 1;
    public static final byte SCOREBOARD_PACKET = 2;
    public static final byte KILLFEED_PACKET = 3;
    public static final byte TEAM_SWITCH_PACKET = 4;
    public static final byte JOINED_PACKET = 5;
    public static final byte LEFT_PACKET = 6;
    
    protected static final byte MAX_USERNAME_LENGTH = 16;
    
    public abstract byte[] getBytes();
    
    protected final byte[] usernameToBytes(String username) {
        byte[] bytes = new byte[MAX_USERNAME_LENGTH];
        byte[] usernameBytes = username.getBytes();
        for (int i = 0; i < Math.min(username.length(), MAX_USERNAME_LENGTH); ++i) {
            bytes[i] = usernameBytes[i];
        }
        return bytes;
    }
    
    protected final String bytesToUsername(byte[] bytes) {
        return new String(bytes).trim();
    }
}
