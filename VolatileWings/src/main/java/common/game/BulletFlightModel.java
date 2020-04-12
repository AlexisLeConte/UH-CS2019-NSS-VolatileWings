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

public class BulletFlightModel {
    public static void step(GameObject bullet, long duration) {
        double nextX = bullet.getX() + duration * bullet.getSpeed() * Math.cos(bullet.getT());
        double nextY = bullet.getY() + duration * bullet.getSpeed() * Math.sin(bullet.getT());
        bullet.setX((float) nextX);
        bullet.setY((float) nextY);
    }
}
