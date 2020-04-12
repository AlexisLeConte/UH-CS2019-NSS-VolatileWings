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

package server.room.engine;

import server.room.entity.Aircraft;
import server.room.entity.Bullet;
import common.game.GameConfig;

public class CollisionEngine {
    private final CollisionHandler handler;
    
    public CollisionEngine(CollisionHandler handler) {
        this.handler = handler;
    }
    
    public void detectCollision(Bullet bullet, Aircraft defender) {
        if (bullet.getPlayer().getId() == defender.getPlayer().getId()) {
            return;
        }
        if (getDistance(bullet, defender) <= Aircraft.COLLISION_RADIUS) {
            byte damage = GameConfig.damagePerHit;
            if (defender.takeDamage(damage)) {
                handler.onDamage(bullet.getPlayer(), defender.getPlayer(), damage);
            } else {
                handler.onDestroy(bullet.getPlayer(), defender.getPlayer());
            }
            bullet.consume(bullet.getTtl());
        }
    }
    
    private double getDistance(Bullet bullet, Aircraft aircraft) {
        float bx = bullet.getX(), by = bullet.getY();
        float ax = aircraft.getX(), ay = aircraft.getY();
        return Math.sqrt((bx - ax) * (bx - ax) + (by - ay) * (by - ay));
    }
}
