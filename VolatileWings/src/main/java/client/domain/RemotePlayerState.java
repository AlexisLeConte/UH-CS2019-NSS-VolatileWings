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

package client.domain;

import client.entity.AircraftSnapshot;
import client.entity.InterpolatedAircraft;

public class RemotePlayerState {
    public long lastTimeReceivedData;
    public long lastAircraftUpdateTimestamp;
    public String username;
    public byte team;
    public byte id;
    public int score;
    public InterpolatedAircraft aircraft;
    public AircraftSnapshot aircraftSnapshot;
    
    public RemotePlayerStateSnapshot get(long timestamp) {
        RemotePlayerStateSnapshot snapshot = new RemotePlayerStateSnapshot();
        snapshot.username = this.username;
        snapshot.team = this.team;
        snapshot.score = this.score;
        if (aircraft != null) {
            snapshot.aircraftSnapshot = aircraft.get(timestamp);
        }
        return snapshot;
    }
}
