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

package client.gui.style;

import java.awt.Color;
import java.awt.Font;

public final class GuiStyles {
	public static final Color LIGHT_BACKGROUND = new Color(100, 100, 100);
    public static final Color MEDIUM_BACKGROUND = new Color(80, 80, 80);
	public static final Color DARK_BACKGROUND = new Color(64, 64, 64);
	public static final Color LIGHT_FOREGROUND = new Color(230, 230, 230);
	public static final Color DARK_FOREGROUND = new Color(32, 32, 32);
    public static final Color CONFIRM = new Color(16, 100, 16);
	public static final Color CANCEL = new Color(128, 16, 16);
    
    public static final Font FONT_IMPORTANT = new Font("AR ESSENCE", Font.BOLD, 16);
	public static final Font FONT_STANDARD = new Font("AR ESSENCE", Font.PLAIN, 16);
}
