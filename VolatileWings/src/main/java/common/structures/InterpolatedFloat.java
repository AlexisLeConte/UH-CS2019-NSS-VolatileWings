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

package common.structures;

public class InterpolatedFloat {
    private float a;
    private float b;
    private long ta;
    private long tb;
    private long delay;
    private long updateId;
    
    public InterpolatedFloat(float value, long timestamp) {
        a = value;
        b = value;
        ta = timestamp;
        tb = timestamp;
        delay = 0;
        updateId = 0;
    }
    
    public void update(float value, long timestamp) {
        a = b;
        ta = tb;
        b = value;
        tb = timestamp;
        updateId++;
        delay = tb - ta;
    }
    
    public float get(long timestamp) {
        if (delay == 0 || updateId < 2) {
            return a;
        }
        float factor = (float) (timestamp - delay - ta) / (float) delay;
        return (1.0f - factor) * a + factor * b;
    }
}
