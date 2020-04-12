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

public class BulletPacket extends NetworkPacket {
    private final byte playerId;
    private final float x;
    private final float y;
    private final float t;
    
    public BulletPacket(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.get(); // discard packet type
        playerId = buffer.get();
        x = buffer.getFloat();
        y = buffer.getFloat();
        t = buffer.getFloat();
    }
    
    @Override
    public byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(14);
        buffer.put(server.room.network.packets.NetworkPacket.BULLET_PACKET);
        buffer.put(playerId);
        buffer.putFloat(x);
        buffer.putFloat(y);
        buffer.putFloat(t);
        return buffer.array();
    }
    
    public byte getPlayerId() {
        return playerId;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getT() {
        return t;
    }
}
