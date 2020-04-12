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

import client.engine.Time;
import server.room.network.packets.KeyboardStatePacket;
import java.net.InetAddress;
import server.room.entity.Aircraft;

public class Player {
    private final byte id;
    private final InetAddress address;
    private final String username;
    private final Aircraft aircraft;
    private int score;
    private byte team;
    private byte teamKillsCount;
    private byte lastKillTeam;
    private KeyboardStatePacket state;
    private long lastTimeFired;
    
    public Player(InetAddress address, String username, byte id, byte team) {
        this.address = address;
        this.username = username;
        this.id = id;
        this.team = team;
        this.score = 0;
        this.aircraft = new Aircraft(this);
        this.state = new KeyboardStatePacket(Time.getTimestamp(), id);
    }
    
    public void setCurrentKeyboardState(KeyboardStatePacket state) {
        this.state = state;
    }
    
    public KeyboardStatePacket getKeyboardState() {
        return state;
    }
    
    public Aircraft getAircraft() {
        return aircraft;
    }
    
    public byte getTeam() {
        return team;
    }
    
    public byte getLastKillTeam() {
        return lastKillTeam;
    }
    
    public void setLastKillTeam(byte team) {
        lastKillTeam = team;
    }
    
    public void increaseScore(int amount) {
        score += amount;
    }
    
    public void decreaseScore(int amount) {
        score -= amount;
        if (score < 0) {
            score = 0;
        }
    }

    public String getUsername() {
        return username;
    }

    public byte getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public byte getTeamKillsCount() {
        return teamKillsCount;
    }

    public KeyboardStatePacket getState() {
        return state;
    }
    
    public InetAddress getAddress() {
        return address;
    }
    
    public void setTeam(byte team) {
        this.team = team;
    }

    public long getLastTimeFired() {
        return lastTimeFired;
    }

    public void setLastTimeFired(long lastTimeFired) {
        this.lastTimeFired = lastTimeFired;
    }
}
