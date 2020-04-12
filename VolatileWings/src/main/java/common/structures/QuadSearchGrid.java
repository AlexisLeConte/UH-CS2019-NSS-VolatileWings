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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class QuadSearchGrid {
    private final long gridSize;
    private final long quadSize;
    private final long numberOfQuads;
    private final Map<Long, ArrayList<QuadSearchEntity>> grid = new HashMap<>();
    
    public QuadSearchGrid(long gridSize, long quadSize) {
        this.gridSize = gridSize;
        this.quadSize = quadSize;
        this.numberOfQuads = (gridSize / quadSize) + (gridSize % quadSize != 0 ? 1 : 0);
    }
    
    public void put(QuadSearchEntity entity) {
        long key = getKey(entity);
        if (!grid.containsKey(key)) {
            grid.put(key, new ArrayList<>());
        }
        grid.get(key).add(entity);
    }
    
    public void remove(QuadSearchEntity entity) {
        long key = getKey(entity);
        if (grid.containsKey(key)) {
            grid.get(key).remove(entity);
        }
    }
    
    public LinkedList<QuadSearchEntity> searchNeighbours(QuadSearchEntity entity) {
        LinkedList<QuadSearchEntity> found = new LinkedList<>();
        long key = getKey(entity);
        for (long i = -1; i <= 1; ++i) {
            for (long j = -1; j <= 1; ++j) {
                long nkey = key + i + j * numberOfQuads;
                if (grid.containsKey(nkey)) {
                    found.addAll(grid.get(nkey));
                }
            }
        }
        found.remove(entity);
        return found;
    }
    
    private long getKey(QuadSearchEntity entity) {
        long keyX = (long) entity.getX() / quadSize;
        long keyY = ((long) entity.getY() / quadSize) * numberOfQuads;
        return keyX + keyY;
    }
}
