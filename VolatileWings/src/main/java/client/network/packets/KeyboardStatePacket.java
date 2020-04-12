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

public class KeyboardStatePacket extends NetworkPacket {
    private final long timestamp;
    private final byte playerId;
    private byte keystate;
    
    public KeyboardStatePacket(long timestamp, byte playerId) {
        this.timestamp = timestamp;
        this.playerId = playerId;
        this.keystate = 0;
    }
    
    public KeyboardStatePacket(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        timestamp = buffer.getLong();
        playerId = buffer.get();
        keystate = buffer.get();
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.putLong(timestamp);
        buffer.put(playerId);
        buffer.put(keystate);
        return buffer.array();
    }
    
    public void setRollRightPressed() {
        keystate |= (1 << 1);
    }
    
    public void setRollLeftPressed() {
        keystate |= (1 << 2);
    }
    
    public void setFirePressed() {
        keystate |= 1;
    }
    
    public void setRollRightReleased() {
        keystate &= ~(1 << 1);
    }
    
    public void setRollLeftReleased() {
        keystate &= ~(1 << 2);
    }
    
    public void setFireReleased() {
        keystate &= ~1;
    }
    
    public boolean isRollRightPressed() {
        return ((keystate >> 1) & 1) == 1;
    }
    
    public boolean isRollLeftPressed() {
        return ((keystate >> 2) & 1) == 1;
    }
    
    public boolean isFirePressed() {
        return (keystate & 1) == 1;
    }
    
    public long getTimestamp() {
        return timestamp;
    }

    public byte getPlayerId() {
        return playerId;
    }
}
