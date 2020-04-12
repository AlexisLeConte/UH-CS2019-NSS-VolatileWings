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

package client.entity;

import common.structures.InterpolatedFloat;
import client.network.packets.AircraftPacket;

public class InterpolatedAircraft {
    private final InterpolatedFloat x;
    private final InterpolatedFloat y;
    private final InterpolatedFloat t;
    private final InterpolatedFloat speed;
    private byte roll;
    private byte health;
    
    public InterpolatedAircraft(long timestamp, AircraftPacket packet) {
        x = new InterpolatedFloat(packet.getX(), timestamp);
        y = new InterpolatedFloat(packet.getY(), timestamp);
        t = new InterpolatedFloat(packet.getT(), timestamp);
        speed = new InterpolatedFloat(packet.getSpeed(), timestamp);
        roll = packet.getRoll();
        health = packet.getHealth();
    }
    
    public void update(long timestamp, AircraftPacket packet) {
        x.update(packet.getX(), timestamp);
        y.update(packet.getY(), timestamp);
        t.update(packet.getT(), timestamp);
        speed.update(packet.getSpeed(), timestamp);
        roll = packet.getRoll();
        health = packet.getHealth();
    }
    
    public AircraftSnapshot get(long timestamp) {
        AircraftSnapshot snapshot = new AircraftSnapshot();
        snapshot.x = x.get(timestamp);
        snapshot.y = y.get(timestamp);
        snapshot.t = t.get(timestamp);
        snapshot.speed = speed.get(timestamp);
        snapshot.roll = roll;
        snapshot.health = health;
        return snapshot;
    }
}
