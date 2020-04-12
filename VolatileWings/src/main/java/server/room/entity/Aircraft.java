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

package server.room.entity;

import server.room.Player;
import common.game.AircraftFlightModel;
import common.structures.QuadSearchEntity;
import common.game.GameObject;

public class Aircraft implements GameObject, QuadSearchEntity {
    private final Player player;
    private byte health;
    private float x;
    private float y;
    private float t;
    private float speed;
    
    public static final float COLLISION_RADIUS = 0.5f;
    
    public Aircraft(Player player) {
        this.player = player;
    }
    
    public boolean takeDamage(double damage) {
        health -= damage;
        return health > 0;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public void step(long duration) {
        boolean rollLeft = player.getKeyboardState().isRollLeftPressed();
        boolean rollRight = player.getKeyboardState().isRollRightPressed();
        AircraftFlightModel.step(this, rollLeft, rollRight, duration);
    }
    
    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    public byte getHealth() {
        return health;
    }

    @Override
    public float getSpeed() {
        return speed;
    }
    
    @Override
    public float getT() {
        return t;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public void setT(float t) {
        this.t = t;
    }

    @Override
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    
    public void setHealth(byte health) {
        this.health = health;
    }
}
