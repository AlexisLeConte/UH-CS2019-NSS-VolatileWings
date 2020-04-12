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

package client.gui.widget;

import client.gui.style.GuiStyles;
import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;

public final class TextField extends JFormattedTextField {
	public TextField() {
		this.setForeground(GuiStyles.LIGHT_FOREGROUND);
		this.setBackground(GuiStyles.LIGHT_BACKGROUND);
		this.setFont(GuiStyles.FONT_STANDARD);
		this.setBorder(BorderFactory.createLineBorder(GuiStyles.DARK_FOREGROUND));
	}
}
