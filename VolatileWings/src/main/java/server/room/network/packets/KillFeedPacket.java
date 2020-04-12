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

package server.room.network.packets;

public class KillFeedPacket extends NetworkPacket {
    private final byte attackerId;
    private final byte targetId;
    
    public KillFeedPacket(byte attackerId, byte targetId) {
        this.attackerId = attackerId;
        this.targetId = targetId;
    }
    
    public KillFeedPacket(byte[] bytes) {
        attackerId = bytes[1];
        targetId = bytes[2];
    }

    @Override
    public byte[] getBytes() {
        return new byte[]{KILLFEED_PACKET, attackerId, targetId};
    }

    public byte getAttackerId() {
        return attackerId;
    }

    public byte getTargetId() {
        return targetId;
    }
}
