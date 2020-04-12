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

package common.game.asset;

import java.io.Serializable;

public class AssetDescriptor implements Serializable {
    public final String filename;
    public int length;
    public byte[] checksum;
    
    public AssetDescriptor(String filename) {
        this.filename = filename;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof AssetDescriptor)) {
            return false;
        }
        AssetDescriptor descriptor = (AssetDescriptor) o;
        if (descriptor.length != length || !descriptor.filename.equals(filename)) {
            return false;
        }
        if (descriptor.checksum.length != checksum.length) {
            return false;
        }
        for (int i = 0; i < checksum.length; ++i) {
            if (descriptor.checksum[i] != checksum[i]) {
                return false;
            }
        }
        return true;
    }
}
