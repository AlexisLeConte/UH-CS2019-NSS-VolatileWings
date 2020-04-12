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

package client.engine;

import client.domain.LocalPlayerState;
import client.domain.RemotePlayerState;
import client.domain.RemotePlayerStateSnapshot;
import client.entity.InterpolatedAircraft;
import client.entity.PredictedBullet;
import client.network.GameStateConnector;
import client.network.GameStateInputHandler;
import client.network.PlayerStateConnector;
import client.network.PlayerStateInputHandler;
import client.network.RoomServerConfig;
import client.network.packets.AircraftPacket;
import client.network.packets.BulletPacket;
import client.network.packets.KeyboardStatePacket;
import client.network.packets.KillFeedPacket;
import client.network.packets.PlayerJoinedPacket;
import client.network.packets.PlayerLeftPacket;
import client.network.packets.PlayerPacket;
import client.network.packets.ScoreboardPacket;
import common.game.AircraftFlightModel;
import common.game.BulletFlightModel;
import common.game.PlayerScoreInfo;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import common.game.GameConfig;

public class SimulationEngine extends Thread implements PlayerStateInputHandler, GameStateInputHandler, PlayerInputListener {
    private final RoomServerConfig config;
    private final byte playerId;
    
    private boolean rollRightPressed = false;
    private boolean rollLeftPressed = false;
    private boolean openFirePressed = false;
    
    private GameStateConnector gameStateConnector;
    private PlayerStateConnector playerStateConnector;
    
    private final Map<Byte, RemotePlayerState> players = new HashMap<>();
    private final LinkedList<PredictedBullet> bullets = new LinkedList<>();
    private final Deque<LocalPlayerState> predictions = new LinkedList<>();
    
    private final LocalPlayerState state = new LocalPlayerState();
    
    private final static long MIN_SIMULATION_TIME = 33000000; // 33 Simulations Per Second
    
    public SimulationEngine(byte playerId, RoomServerConfig config) {
        this.config = config;
        this.playerId = playerId;
    }
    
    @Override
    public void run() {
        gameStateConnector = new GameStateConnector(config, this);
        playerStateConnector = new PlayerStateConnector(config, this);
        gameStateConnector.start();
        
        long lastNanoTime = System.nanoTime();
        while (true) {
            // simulation duration for this step
            long duration = (System.nanoTime() - lastNanoTime) / 1000000L;
            lastNanoTime = System.nanoTime();
            long lastSimulationTime = Time.getTimestamp();
            
            // remove inactive players
            cleanRemotePlayers();
            
            // remove dead game objects
            cleanGameObjects();
            
            // send keyboard state and save current prediction for future corrections
            playerStateConnector.send(getCurrentKeyboardState(lastSimulationTime));
            synchronized (predictions) {
                predictions.addFirst(state.copy(lastSimulationTime));
            }
            
            // move game objects
            synchronized (bullets) {
                for (PredictedBullet bullet : bullets) {
                    BulletFlightModel.step(bullet, duration);
                    bullet.consume(duration);
                }
            }
            
            synchronized (state) {
                AircraftFlightModel.step(state, rollLeftPressed, rollRightPressed, duration);
            }
            
            // collisions ??
            
            // ensure there is at least MIN_SIMULATION_TIME ms between each simulation
            if (System.nanoTime() - lastNanoTime < MIN_SIMULATION_TIME) {
                try {
                    Thread.sleep((MIN_SIMULATION_TIME - System.nanoTime() + lastNanoTime) / 1000000L);
                } catch (Exception e) {}
            }
        }
    }
    
    public void kill() {
        
    }
    
    private synchronized KeyboardStatePacket getCurrentKeyboardState(long timestamp) {
        KeyboardStatePacket packet = new KeyboardStatePacket(timestamp, playerId);
        if (rollLeftPressed) {
            packet.setRollLeftPressed();
        }
        if (rollRightPressed) {
            packet.setRollRightPressed();
        }
        if (openFirePressed) {
            packet.setFirePressed();
        }
        return packet;
    }
    
    private void cleanGameObjects() {
        synchronized (bullets) {
            LinkedList<PredictedBullet> toRemove = new LinkedList<>();
            for (PredictedBullet bullet : bullets) {
                if (bullet.ttl <= 0) {
                    toRemove.add(bullet);
                }
            }
            bullets.removeAll(toRemove);
        }
    }
    
    private void cleanRemotePlayers() {
        synchronized (players) {
            LinkedList<RemotePlayerState> toRemove = new LinkedList<>();
            for (RemotePlayerState player : players.values()) {
                long idleTime = Time.getTimestamp() - player.lastTimeReceivedData;
                if (idleTime > GameConfig.maxClientIdleTime) {
                    toRemove.add(player);
                }
            }
            for (RemotePlayerState player : toRemove) {
                players.remove(player.id);
            }
        }
    }
    
    public LinkedList<RemotePlayerStateSnapshot> getRemotePlayersStateSnapshot(long timestamp) {
        LinkedList<RemotePlayerStateSnapshot> snapshot = new LinkedList<>();
        synchronized (players) {
            for (RemotePlayerState player : players.values()) {
                snapshot.add(player.get(System.nanoTime()));
            }
        }
        return snapshot;
    }
    
    public LinkedList<PredictedBullet> getBulletsSnapshot() {
        LinkedList<PredictedBullet> snapshot = new LinkedList<>();
        synchronized (bullets) {
            for (PredictedBullet bullet : bullets) {
                snapshot.add(new PredictedBullet(bullet));
            }
        }
        return snapshot;
    }
    
    public LocalPlayerState getLocalPlayerStateSnapshot() {
        synchronized (state) {
            return new LocalPlayerState(state);
        }
    }

    @Override
    public void onReceivePlayerState(PlayerPacket packet) {
        LocalPlayerState correction = new LocalPlayerState();
        synchronized (predictions) {
            while (!predictions.isEmpty() && predictions.peek().timestamp <= packet.getTimestamp()) {
                LocalPlayerState prediction = predictions.poll();
                if (prediction.timestamp == packet.getTimestamp()) {
                    correction.speed = packet.getSpeed() - prediction.speed;
                    correction.x = packet.getX() - prediction.x;
                    correction.y = packet.getY() - prediction.y;
                    correction.t = packet.getT() - prediction.t;
                    break;
                }
            }
        }
        synchronized (state) {
            state.team = packet.getTeam();
            state.health = packet.getHealth();
            state.speed += correction.speed;
            state.x += correction.x;
            state.y += correction.y;
            state.t += correction.t;
        }
    }

    @Override
    public void onReceiveAircraftPacket(AircraftPacket packet) {
        if (packet.getPlayerId() == playerId) {
            return; // discard player aircraft packets
        }
        synchronized (players) {
            long timestamp = Time.getTimestamp();
            byte id = packet.getPlayerId();
            if (players.containsKey(id)) {
                RemotePlayerState info = players.get(id);
                if (info.lastAircraftUpdateTimestamp >= packet.getTimestamp()) {
                    return; // discard packets received out of order
                }
                if (info.aircraft != null) {
                    info.aircraft.update(System.nanoTime(), packet);
                } else {
                    info.aircraft = new InterpolatedAircraft(System.nanoTime(), packet);
                }
                info.lastTimeReceivedData = timestamp;
                info.lastAircraftUpdateTimestamp = packet.getTimestamp();
            } else {
                RemotePlayerState info = new RemotePlayerState();
                info.aircraft = new InterpolatedAircraft(System.nanoTime(), packet);
                info.id = packet.getPlayerId();
                info.lastTimeReceivedData = timestamp;
                info.lastAircraftUpdateTimestamp = packet.getTimestamp();
                players.put(id, info);
            }
        }
    }

    @Override
    public void onReceiveBulletPacket(BulletPacket packet) {
        synchronized (bullets) {
            PredictedBullet bullet = new PredictedBullet();
            bullet.x = packet.getX();
            bullet.y = packet.getY();
            bullet.t = packet.getT();
            bullet.speed = GameConfig.bulletSpeed;
            bullet.ttl = GameConfig.bulletTimeToLive;
            bullets.add(bullet);
        }
    }

    @Override
    public void onReceiveScoreboardPacket(ScoreboardPacket packet) {
        synchronized (players) {
            long timestamp = Time.getTimestamp();
            for (PlayerScoreInfo score : packet.getScores()) {
                if (!players.containsKey(score.id)) {
                    players.put(score.id, new RemotePlayerState());
                }
                RemotePlayerState info = players.get(score.id);
                info.score = score.score;
                info.team = score.team;
                info.username = score.username;
                info.lastTimeReceivedData = timestamp;
            }
        }
    }

    @Override
    public void onReceiveKillFeedPacket(KillFeedPacket packet) {
        System.out.println(packet.getAttackerId() + " destroyed " + packet.getTargetId());
    }

    @Override
    public void onReceivePlayerJoinedPacket(PlayerJoinedPacket packet) {
        System.out.println(packet.getUsername() + " joined the game");
    }

    @Override
    public void onReceivePlayerLeftPacket(PlayerLeftPacket packet) {
        System.out.println(packet.getUsername() + " left the game");
    }

    @Override
    public synchronized void onTouchRollRight(boolean pressed) {
        rollRightPressed = pressed;
    }

    @Override
    public synchronized void onTouchRollLeft(boolean pressed) {
        rollLeftPressed = pressed;
    }

    @Override
    public synchronized void onTouchOpenFire(boolean pressed) {
        openFirePressed = pressed;
    }
}
