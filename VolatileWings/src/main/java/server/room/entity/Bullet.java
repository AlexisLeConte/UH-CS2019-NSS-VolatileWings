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
import common.game.BulletFlightModel;
import common.game.GameConfig;
import common.game.GameObject;
import common.structures.QuadSearchEntity;

public class Bullet implements GameObject, QuadSearchEntity {
    private final Player player;
    private float x;
    private float y;
    private final float t;
    private float speed;
    private int ttl;
    
    public Bullet(Aircraft aircraft, long offset) {
        this.player = aircraft.getPlayer();
        this.x = (float) (aircraft.getX() + offset * Math.cos(aircraft.getT()));
        this.y = (float) (aircraft.getY() + offset * Math.sin(aircraft.getT()));
        this.t = aircraft.getT();
        this.speed = GameConfig.bulletSpeed;
        this.ttl = GameConfig.bulletTimeToLive;
    }
    
    public void step(long duration) {
        BulletFlightModel.step(this, duration);
    }
    
    public void consume(long duration) {
        ttl -= duration;
    }
    
    public Player getPlayer() {
        return player;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
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
    public float getSpeed() {
        return speed;
    }
    
    @Override
    public void setT(float t) {
        throw new UnsupportedOperationException("Cannot change final field Bullet::t");
    }

    @Override
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    
    public int getTtl() {
        return ttl;
    }
}
