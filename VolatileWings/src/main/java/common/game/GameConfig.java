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

package common.game;

public class GameConfig {
    // grid parameters
    public static int gridSize = 64;
    
    // game room parameters
    public static byte maxNumberOfPlayers = 16;
    public static int clientKeystateUpdatePort;
    
    // scoring parameters
    public static int scorePerDamagePoints = 1;
    public static int scorePerAircraftDestroyed = 100;
    public static int friendlyFireMultiplier = -2;
    public static int renegadePlayerMultiplier = 3;
    public static int renegadeDestroyerMultiplier = 2;
    public static int frienldyRescuerMultiplier = 2;
    public static int bulletCost = 3;
    
    // flight model parameters
    public static int bulletTimeToLive = 2000;
    public static float bulletSpeed = 0.01f;
    public static float aircraftMaxSpeed = 0.003f;
    public static float aircraftMinSpeed = 0.001f;
    public static float aircraftSpawnSpeed = 0.002f;
    
    // damage model parameters
    public static byte damagePerHit = 5;
    public static byte aircraftMaxHealth = 100;
    
    public static long maxClientIdleTime = 5000;
    public static long fireDelay = 100;
}
