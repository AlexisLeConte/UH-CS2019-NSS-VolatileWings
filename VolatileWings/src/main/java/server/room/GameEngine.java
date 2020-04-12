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

package server.room;

import client.engine.Time;
import common.game.GameConfig;
import server.room.network.RoomServerNetworkConfig;
import server.room.network.NetworkGameOutput;
import server.room.network.NetworkGameInput;
import server.room.entity.Aircraft;
import server.room.entity.Bullet;
import server.room.engine.CollisionEngine;
import common.game.Team;
import server.room.network.packets.KeyboardStatePacket;
import server.room.network.packets.PlayerPacket;
import common.structures.QuadSearchGrid;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import server.room.engine.CollisionHandler;
import server.room.network.packets.AircraftPacket;
import server.room.network.packets.BulletPacket;
import server.room.network.packets.KillFeedPacket;
import server.room.network.packets.PlayerJoinedPacket;
import server.room.network.packets.PlayerLeftPacket;
import server.room.network.packets.ScoreboardPacket;
import common.structures.QuadSearchEntity;
import server.room.network.NetworkClientDiscoveryHandler;
import server.room.network.NetworkGameInputHandler;

public class GameEngine extends Thread implements NetworkClientDiscoveryHandler, NetworkGameInputHandler, CollisionHandler {
    private final Map<Byte, Player> players = new HashMap<>();
    private final LinkedList<Bullet> bullets = new LinkedList<>();
    private final QuadSearchGrid searchGrid;
    private final CollisionEngine collisionEngine;
    private final NetworkGameOutput networkOutput;
    private final NetworkGameInput networkInput;
    private byte maximumNumberOfPlayers;
    
    private final long MIN_SIMULATION_TIME = 33000000;
    
    public GameEngine() {
        this.maximumNumberOfPlayers = GameConfig.maxNumberOfPlayers;
        this.searchGrid = new QuadSearchGrid(GameConfig.gridSize, 4);
        this.collisionEngine = new CollisionEngine(this);
        
        // TODO: move to start method
        this.networkOutput = new NetworkGameOutput(RoomServerNetworkConfig.multicastAddress, RoomServerNetworkConfig.udpClientMulticastPort);
        this.networkInput = new NetworkGameInput(RoomServerNetworkConfig.udpClientUpdatePort, this);
    }
    
    @Override
    public void run() {
        // ready to listen to players input!
        networkInput.start();
        
        long lastSimulationTime = System.nanoTime();
        while (true) {
            // simulation duration for this step
            long duration = (System.nanoTime() - lastSimulationTime) / 1000000L;
            lastSimulationTime = System.nanoTime();
            
            // remove inactive players
            cleanRegisteredPlayers();
            
            // remove dead game objects
            cleanGameObjects();
            
            // move game objects
            synchronized (bullets) {
                for (Bullet bullet : bullets) {
                    searchGrid.remove(bullet);
                    bullet.step(duration);
                    bullet.consume(duration);
                    searchGrid.put(bullet);
                }
            }
            
            LinkedList<Bullet> newBullets = new LinkedList<>();
            synchronized (players) {
                for (Player player : players.values()) {
                    Aircraft aircraft = player.getAircraft();
                    searchGrid.remove(aircraft);
                    aircraft.step(duration);
                    searchGrid.put(aircraft);
                    networkOutput.push(new AircraftPacket(aircraft, Time.getTimestamp()));
                    
                    KeyboardStatePacket state = player.getKeyboardState();
                    if (state.isFirePressed()) {
                        long timestamp = Time.getTimestamp();
                        if (timestamp - player.getLastTimeFired() > GameConfig.fireDelay) {
                            player.setLastTimeFired(timestamp);
                            Bullet bullet = new Bullet(aircraft, 0/*timestamp - state.getTimestamp()*/); // Need a good RTT estimate here!
                            newBullets.add(bullet);
                            player.decreaseScore(GameConfig.bulletCost);
                            networkOutput.push(new BulletPacket(bullet));
                        }
                    }
                }
            }
            
            synchronized (bullets) {
                bullets.addAll(newBullets);
            }
            
            // detect collisions
            synchronized (players) {
                for (Player player : players.values()) {
                    Aircraft aircraft = player.getAircraft();
                    for (QuadSearchEntity entity : searchGrid.searchNeighbours(aircraft)) {
                        if (entity instanceof Bullet) {
                            collisionEngine.detectCollision((Bullet) entity, aircraft);
                        }
                    }
                }
            }
            
            synchronized (players) {
                networkOutput.push(new ScoreboardPacket(players));
            }
            
            // send updates to players
            networkOutput.send();
            
            if (System.nanoTime() - lastSimulationTime < MIN_SIMULATION_TIME) {
                try {
                    Thread.sleep((MIN_SIMULATION_TIME - System.nanoTime() + lastSimulationTime) / 1000000L);
                } catch (Exception e) {}
            }
        }
    }
    
    @Override
    public PlayerPacket onReceiveKeyboardState(KeyboardStatePacket state, InetAddress address) {
        synchronized (players) {
            Player sender = players.get(state.getPlayerId());
            if (sender == null || !sender.getAddress().equals(address)) {
                return null; // inactive player or cheater
            }
            if (sender.getKeyboardState().getTimestamp() < state.getTimestamp()) {
                sender.setCurrentKeyboardState(state);
            } else if (state.isFirePressed()) {
                sender.getKeyboardState().setFirePressed();
            } else {
                return null; // already processed
            }
            return new PlayerPacket(sender, state.getTimestamp());
        }
    }
    
    @Override
    public void onDamage(Player attacker, Player defender, int damage) {
        if (attacker.getTeam() == defender.getTeam() && attacker.getTeam() != Team.RENEGADE) {
            attacker.decreaseScore(damage);
        } else {
            attacker.increaseScore(GameConfig.scorePerDamagePoints * damage);
            // bonus points for damaging a renegade player
            if (defender.getTeam() == Team.RENEGADE) {
                attacker.increaseScore(GameConfig.renegadeDestroyerMultiplier * damage);
            }
            // bonus points for damaging any plane as a renegade
            if (attacker.getTeam() == Team.RENEGADE) {
                attacker.increaseScore(GameConfig.renegadePlayerMultiplier * damage);
            }
        }
    }

    @Override
    public void onDestroy(Player attacker, Player defender) {
        // TODO: implement team switching mechanics
        
        // update scoreboard and attacker team
        if (attacker.getTeam() == defender.getTeam() && attacker.getTeam() != Team.RENEGADE) {
            attacker.decreaseScore(GameConfig.friendlyFireMultiplier * GameConfig.scorePerAircraftDestroyed);
        } else {
            attacker.increaseScore(GameConfig.scorePerAircraftDestroyed);
            // bonus points for destroying a renegade player
            if (defender.getTeam() == Team.RENEGADE) {
                attacker.increaseScore(GameConfig.renegadeDestroyerMultiplier * GameConfig.scorePerAircraftDestroyed);
            }
            // bonus points for destroying any plane as a renegade
            if (attacker.getTeam() == Team.RENEGADE) {
                attacker.increaseScore(GameConfig.renegadePlayerMultiplier * GameConfig.scorePerAircraftDestroyed);
            }
        }
        respawn(defender.getAircraft());
        networkOutput.push(new KillFeedPacket(attacker.getId(), defender.getId()));
        
        // TODO: notify players changing teams
        
    }
    
    private void respawn(Aircraft aircraft) {
        float x = GameConfig.gridSize / 2;
        float y = GameConfig.gridSize / 2;
        float dst = GameConfig.gridSize / 3;
        float angle = (float) (Math.PI * Math.random());
        if (aircraft.getPlayer().getTeam() == Team.TEAM_CRIMSON) {
            angle += Math.PI;
        }
        aircraft.setSpeed(GameConfig.aircraftSpawnSpeed);
        aircraft.setHealth((byte) GameConfig.aircraftMaxHealth);
        searchGrid.remove(aircraft);
        aircraft.setX(x + (float) (dst * Math.cos(angle)));
        aircraft.setY(y + (float) (dst * Math.sin(angle)));
        aircraft.setT(angle + (float) Math.PI);
        searchGrid.put(aircraft);
    }
    
    @Override
    public byte registerPlayer(String username, InetAddress ip) {
        synchronized (players) {
            if (players.size() >= getMaximumNumberOfPlayers()) {
                return -1;
            }
            for (byte id = 0; id < this.getMaximumNumberOfPlayers(); ++id) {
                if (!players.containsKey(id)) {
                    Player player = new Player(ip, username, id, getNewPlayerTeamId());
                    respawn(player.getAircraft());
                    players.put(id, player);
                    networkOutput.push(new PlayerJoinedPacket(player));
                    return id;
                }
            }
        }
        return -1;
    }
    
    private byte getNewPlayerTeamId() {
        int countTeamSilver = 0;
        int countTeamCrimson = 0;
        synchronized (players) {
            for (Player player : players.values()) {
                if (player.getTeam() == Team.TEAM_SILVER) {
                    countTeamSilver++;
                }
                if (player.getTeam() == Team.TEAM_CRIMSON) {
                    countTeamCrimson++;
                }
            }
        }
        return (countTeamSilver <= countTeamCrimson) ? Team.TEAM_SILVER : Team.TEAM_CRIMSON;
    }
    
    private void cleanGameObjects() {
        synchronized (bullets) {
            LinkedList<Bullet> toRemove = new LinkedList<>();
            for (Bullet bullet : bullets) {
                if (bullet.getTtl() <= 0) {
                    toRemove.add(bullet);
                    searchGrid.remove(bullet);
                }
            }
            bullets.removeAll(toRemove);
        }
    }
    
    private void cleanRegisteredPlayers() {
        synchronized (players) {
            LinkedList<Player> toRemove = new LinkedList<>();
            for (Player player : players.values()) {
                long idleTime = Time.getTimestamp() - player.getKeyboardState().getTimestamp();
                if (idleTime > GameConfig.maxClientIdleTime) {
                    toRemove.add(player);
                    searchGrid.remove(player.getAircraft());
                }
            }
            for (Player player : toRemove) {
                players.remove(player.getId());
                networkOutput.push(new PlayerLeftPacket(player));
            }
        }
    }
    
    public synchronized void setMaximumNumberOfPlayers(byte limit) {
        maximumNumberOfPlayers = limit;
    }
    
    public synchronized int getMaximumNumberOfPlayers() {
        return maximumNumberOfPlayers;
    }
    
    public synchronized int getNumberOfPlayers() {
        return players.size();
    }
}
