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

package common.game;

public class AircraftFlightModel {
    public static void step(GameObject aircraft, boolean rollLeft, boolean rollRight, long duration) {
        if (rollLeft) {
            aircraft.setT(aircraft.getT() + (float) duration * 1.0f * aircraft.getSpeed());
        }
        if (rollRight) {
            aircraft.setT(aircraft.getT() - (float) duration * 1.0f * aircraft.getSpeed());
        }
        double nextX = aircraft.getX() + (double) duration * aircraft.getSpeed() * Math.cos(aircraft.getT());
        double nextY = aircraft.getY() + (double) duration * aircraft.getSpeed() * Math.sin(aircraft.getT());
        if (nextX < 0) {
            aircraft.setT(0.0f);
        } else if (nextX > GameConfig.gridSize) {
            aircraft.setT((float) Math.PI);
        }
        if (nextY < 0) {
            aircraft.setT((float) Math.PI / 2.0f);
        } else if (nextY > GameConfig.gridSize) {
            aircraft.setT(3.0f * (float) Math.PI / 2.0f);
        }
        aircraft.setX((float) nextX);
        aircraft.setY((float) nextY);
    }
}
