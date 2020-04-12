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

import common.game.GameObject;

public class PredictedBullet implements GameObject {
    public float x;
    public float y;
    public float t;
    public float speed;
    public long ttl;
    
    public PredictedBullet() {}
    
    public PredictedBullet(PredictedBullet source) {
        this.x = source.x;
        this.y = source.y;
        this.t = source.t;
        this.speed = source.speed;
        this.ttl = source.ttl;
    }
    
    public void consume(long duration) {
        ttl -= duration;
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
    public float getSpeed() {
        return speed;
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
}
