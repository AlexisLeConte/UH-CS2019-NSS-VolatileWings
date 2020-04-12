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
import java.awt.Font;

public class GraphicsStyles {
    public static final Color ALPHA_CRIMSON = new Color(128, 0, 0, 200);
    public static final Color ALPHA_SILVER = new Color(128, 128, 128, 200);
    public static final Color DARK_BACKGROUND = new Color(16, 16, 16, 128);
    
    public static final Color NOALPHA_CRIMSON = new Color(160, 0, 0);
    public static final Color NOALPHA_SILVER = new Color(180, 180, 180);
    
    public static final Font FONT_SCOREBOARD = new Font("Courier", Font.BOLD, 14);
    public static final Font FONT_NOTIFICATIONS = new Font("Arial", Font.PLAIN, 16);
}
