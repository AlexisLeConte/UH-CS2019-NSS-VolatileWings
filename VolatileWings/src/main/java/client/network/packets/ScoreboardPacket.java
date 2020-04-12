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

import common.game.PlayerScoreInfo;
import java.nio.ByteBuffer;
import java.util.LinkedList;

public class ScoreboardPacket extends NetworkPacket {
    private final LinkedList<PlayerScoreInfo> scores = new LinkedList<>();
    
    public ScoreboardPacket(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.get(); // discard packet type
        for (int i = 0; i < bytes.length / (MAX_USERNAME_LENGTH + 6); ++i) {
            byte[] usernameBytes = new byte[MAX_USERNAME_LENGTH];
            buffer.get(usernameBytes);
            int score = buffer.getInt();
            byte team = buffer.get();
            byte id = buffer.get();
            scores.add(new PlayerScoreInfo(bytesToUsername(usernameBytes), id, team, score));
        }
    }
    
    @Override
    public byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(1 + (MAX_USERNAME_LENGTH + 6) * scores.size());
        buffer.put(NetworkPacket.SCOREBOARD_PACKET);
        for (int i = 0; i < scores.size(); ++i) {
            buffer.put(usernameToBytes(scores.get(i).username));
            buffer.putInt(scores.get(i).score);
            buffer.put(scores.get(i).team);
            buffer.put(scores.get(i).id);
        }
        return buffer.array();
    }
    
    public LinkedList<PlayerScoreInfo> getScores() {
        return scores;
    }
}
