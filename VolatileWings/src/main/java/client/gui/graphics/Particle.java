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

package client.gui.graphics;

import java.awt.Color;

public class Particle {
    private final float x;
    private final float y;
    private final Color c;
    private final long ttl;
    private long t;
    
    public Particle(float x, float y, float dispersion, long ttl, Color c) {
        this.x = x + (int) (dispersion * Math.random() - dispersion / 2);
        this.y = y + (int) (dispersion * Math.random() - dispersion / 2);
        this.ttl = ttl;
        this.c = c;
    }
    
    public void update(long duration) {
        t += duration;
    }
    
    public boolean isAlive() {
        return t < ttl;
    }
    
    public float getSize() {
        return 6.0f;
    }
    
    public Color getColor() {
        double alpha = 0.2 + 0.6 * ((ttl - t) / (double) ttl);
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (255 * alpha));
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
