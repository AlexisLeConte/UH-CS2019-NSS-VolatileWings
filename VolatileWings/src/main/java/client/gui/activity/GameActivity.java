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

package client.gui.activity;

import client.ClientController;
import client.domain.LocalPlayerState;
import client.domain.RemotePlayerStateSnapshot;
import client.engine.SimulationEngine;
import client.engine.Time;
import client.entity.PredictedBullet;
import client.gui.graphics.GraphicsStyles;
import client.gui.graphics.Particle;
import client.gui.graphics.ParticlesEmitter;
import client.network.RoomServerConfig;
import common.game.GameConfig;
import common.game.Team;
import common.game.asset.AssetRepository;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public final class GameActivity extends Activity {
    private final SimulationEngine engine;
    private final LinkedList<RemotePlayerStateSnapshot> players = new LinkedList<>();
    private final LinkedList<PredictedBullet> bullets = new LinkedList<>();
    private BufferedImage screen;
    private LocalPlayerState player;
    private boolean displayScoreboard = false;
    private final ParticlesEmitter emitter = new ParticlesEmitter();
    private long timestamp;
    
    private final static long MIN_SIMULATION_TIME = 16600000; // 60 FPS
    
    private static final String OPEN_FIRE_PRESSED = "OFP";
    private final Action openFirePressed = new AbstractAction(OPEN_FIRE_PRESSED) {
        @Override
        public void actionPerformed(ActionEvent e) {
            engine.onTouchOpenFire(true);
        }
    };
    
    private static final String OPEN_FIRE_RELEASED = "OFR";
    private final Action openFireReleased = new AbstractAction(OPEN_FIRE_RELEASED) {
        @Override
        public void actionPerformed(ActionEvent e) {
            engine.onTouchOpenFire(false);
        }
    };
    
    private static final String ROLL_LEFT_PRESSED = "RLP";
    private final Action rollLeftPressed = new AbstractAction(ROLL_LEFT_PRESSED) {
        @Override
        public void actionPerformed(ActionEvent e) {
            engine.onTouchRollLeft(true);
        }
    };
    
    private static final String ROLL_LEFT_RELEASED = "RLR";
    private final Action rollLeftReleased = new AbstractAction(ROLL_LEFT_RELEASED) {
        @Override
        public void actionPerformed(ActionEvent e) {
            engine.onTouchRollLeft(false);
        }
    };
    
    private static final String ROLL_RIGHT_PRESSED = "RRP";
    private final Action rollRightPressed = new AbstractAction(ROLL_RIGHT_PRESSED) {
        @Override
        public void actionPerformed(ActionEvent e) {
            engine.onTouchRollRight(true);
        }
    };
    
    private static final String ROLL_RIGHT_RELEASED = "RRR";
    private final Action rollRightReleased = new AbstractAction(ROLL_RIGHT_RELEASED) {
        @Override
        public void actionPerformed(ActionEvent e) {
            engine.onTouchRollRight(false);
        }
    };
    
    private static final String DISPLAY_SCOREBOARD_PRESSED = "DSP";
    private final Action displayScoreboardPressed = new AbstractAction(DISPLAY_SCOREBOARD_PRESSED) {
        @Override
        public void actionPerformed(ActionEvent e) {
            setDisplayScoreboard(true);
        }
    };
    
    private static final String DISPLAY_SCOREBOARD_RELEASED = "DSR";
    private final Action displayScoreboardReleased = new AbstractAction(DISPLAY_SCOREBOARD_RELEASED) {
        @Override
        public void actionPerformed(ActionEvent e) {
            setDisplayScoreboard(false);
        }
    };
    
    public synchronized boolean getDisplayeScoreboard() {
        return displayScoreboard;
    }
    
    public synchronized void setDisplayScoreboard(boolean displayed) {
        displayScoreboard = displayed;
    }
    
    public GameActivity(int width, int height) {
        engine = ClientController.getInstance().getSimulationEngine();
        screen = new BufferedImage(GameConfig.gridSize * 32, GameConfig.gridSize * 32, BufferedImage.TYPE_INT_ARGB);
        
        /*addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                int width = event.getComponent().getWidth();
                int height = event.getComponent().getHeight();
                screen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            }
        });*/
        
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), OPEN_FIRE_PRESSED);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true), OPEN_FIRE_RELEASED);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), ROLL_LEFT_PRESSED);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), ROLL_LEFT_RELEASED);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), ROLL_RIGHT_PRESSED);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), ROLL_RIGHT_RELEASED);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), DISPLAY_SCOREBOARD_PRESSED);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), DISPLAY_SCOREBOARD_RELEASED);
        
        getActionMap().put(OPEN_FIRE_PRESSED, openFirePressed);
        getActionMap().put(OPEN_FIRE_RELEASED, openFireReleased);
        getActionMap().put(ROLL_LEFT_PRESSED, rollLeftPressed);
        getActionMap().put(ROLL_LEFT_RELEASED, rollLeftReleased);
        getActionMap().put(ROLL_RIGHT_PRESSED, rollRightPressed);
        getActionMap().put(ROLL_RIGHT_RELEASED, rollRightReleased);
        getActionMap().put(DISPLAY_SCOREBOARD_PRESSED, displayScoreboardPressed);
        getActionMap().put(DISPLAY_SCOREBOARD_RELEASED, displayScoreboardReleased);
        
        new Thread() {
            @Override
            public void run() {
                long lastNanoTime = System.nanoTime();
                while (true) {
                    lastNanoTime = System.nanoTime();
                    synchronized (players) {
                        players.clear();
                        players.addAll(engine.getRemotePlayersStateSnapshot(Time.getTimestamp()));
                        players.sort((RemotePlayerStateSnapshot lhs, RemotePlayerStateSnapshot rhs) -> rhs.score - lhs.score);
                    }
                    synchronized (bullets) {
                        bullets.clear();
                        bullets.addAll(engine.getBulletsSnapshot());
                    }
                    player = engine.getLocalPlayerStateSnapshot();
                    repaint();
                    
                    if (System.nanoTime() - lastNanoTime < MIN_SIMULATION_TIME) {
                        try {
                            Thread.sleep((MIN_SIMULATION_TIME - System.nanoTime() + lastNanoTime) / 1000000L);
                        } catch (Exception e) {}
                    }
                }
            }
        }.start();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        long duration = System.nanoTime() - timestamp;
        timestamp = System.nanoTime();
        
        System.out.println("FPS: " + 1000000000L / duration);
        
        Graphics2D sg = screen.createGraphics();
        sg.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        sg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        // draw a grid
        sg.setColor(Color.WHITE);
        sg.fillRect(0, 0, GameConfig.gridSize * 32, GameConfig.gridSize * 32);
        sg.setColor(Color.BLACK);
        for (int i = 0; i < GameConfig.gridSize; ++i) {
            sg.drawLine(32 * i, 0, 32 * i, GameConfig.gridSize * 32);
            sg.drawLine(0, 32 * i, GameConfig.gridSize * 32, 32 * i);
        }
        
        emitter.update(duration / 1000000L);
        for (Particle p : emitter.getParticles()) {
            sg.setColor(p.getColor());
            sg.fillOval((int) (p.getX() - p.getSize()/2), (int) (p.getY() - p.getSize()/2), (int) p.getSize(), (int) p.getSize());
        }
        
        sg.setColor(Color.BLACK);
        synchronized (bullets) {
            for (PredictedBullet bullet : bullets) {
                /*sg.drawLine(
                    (int) (32 * bullet.x),
                    (int) (32 * bullet.y),
                    (int) (32 * bullet.x + 5 * Math.cos(bullet.t)),
                    (int) (32 * bullet.y + 5 * Math.sin(bullet.t))
                );*/
                sg.fillOval((int) (32 * bullet.x - 1.5), (int) (32 * bullet.y - 1.5), 3,3);
            }
        }
        
        sg.setColor(Color.BLACK);
        synchronized (players) {
            for (RemotePlayerStateSnapshot player : players) {
                if (player.aircraftSnapshot != null) {
                    drawAircraft(sg, player.aircraftSnapshot.x, player.aircraftSnapshot.y, player.aircraftSnapshot.t, getTeamColor(player.team));
                    if (player.aircraftSnapshot.health < GameConfig.aircraftMaxHealth * 0.8) {
                        int color = (int) (0.2 * 255 + 255 * player.aircraftSnapshot.health / (double) GameConfig.aircraftMaxHealth);
                        emitter.addParticle(new Particle(32 * player.aircraftSnapshot.x, 32 * player.aircraftSnapshot.y, 8, 1000, new Color(color, color, color)));
                    }
                }
            }
        }
        
        if (player != null) {
            drawAircraft(sg, player.x, player.y, player.t, getTeamColor(player.team));
            if (player.health < GameConfig.aircraftMaxHealth * 0.8) {
                int color = (int) (0.2 * 255 + 255 * player.health / (double) GameConfig.aircraftMaxHealth);
                emitter.addParticle(new Particle(32 * player.x, 32 * player.y, 8, 1000, new Color(color, color, color)));
            }
        } 
        
        sg.dispose();
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        /*
        AffineTransform cameraTransform = new AffineTransform();
        cameraTransform.concatenate(AffineTransform.getScaleInstance(1, -1));
        cameraTransform.concatenate(AffineTransform.getTranslateInstance(0, -screen.getHeight()));
        cameraTransform.concatenate(AffineTransform.getTranslateInstance(-32*player.getX() + getWidth()/2, -getHeight()/2 +(32*GameConfig.gridSize - 32*player.getY())));
        */
        AffineTransform cameraTransform = new AffineTransform();
        cameraTransform.concatenate(AffineTransform.getTranslateInstance(-32*player.getX() + getWidth()/2, getHeight()/2 +32*player.getY() - 32*GameConfig.gridSize));
        cameraTransform.concatenate(AffineTransform.getRotateInstance(player.t - Math.PI/2, 32*player.getX(), - 32*player.getY() + 32*GameConfig.gridSize));
        cameraTransform.concatenate(AffineTransform.getScaleInstance(1, -1));
        cameraTransform.concatenate(AffineTransform.getTranslateInstance(0, -screen.getHeight()));
        g2d.drawImage(screen, cameraTransform, this);
        
        if (getDisplayeScoreboard()) {
            synchronized (players) {
                for (int i = 0; i < players.size(); ++i) {
                    drawScoreBoardLine(g2d, players.get(i), i, players.size());
                }
            }
        }
    }
    
    public void drawAircraft(Graphics2D g, float x, float y, float t, Color c) {
        g.setColor(c);
        g.fillOval((int) (32*x) - 10, (int) (32*y) - 10, 20, 20);
        g.setColor(Color.BLACK);
        g.drawOval((int) (32*x) - 10, (int) (32*y) - 10, 20, 20);
        g.setColor(Color.BLACK);
        g.drawLine(
            (int) (32 * x),
            (int) (32 * y),
            (int) (32 * x + 10 * Math.cos(t)),
            (int) (32 * y + 10 * Math.sin(t))
        );
        
        /*
        BufferedImage texture = AssetRepository.getInstance().getAircraftTexture();
        AffineTransform transform = new AffineTransform();
        transform.concatenate(AffineTransform.getTranslateInstance(32*x - texture.getWidth()/2, 32*y - texture.getHeight()/2));
        transform.concatenate(AffineTransform.getRotateInstance(t + Math.PI / 2, texture.getWidth()/2, texture.getHeight()/2));
        g.drawImage(texture, transform, null);
        */
    }
    
    public Color getTeamColor(byte team) {
        if (team == Team.TEAM_CRIMSON) return GraphicsStyles.NOALPHA_CRIMSON;
        if (team == Team.TEAM_SILVER) return GraphicsStyles.NOALPHA_SILVER;
        return Color.WHITE;
    }
    
    public void drawScoreBoardLine(Graphics2D g, RemotePlayerStateSnapshot player, int i, int m) {
        g.setFont(GraphicsStyles.FONT_SCOREBOARD);
        FontMetrics metrics = g.getFontMetrics(GraphicsStyles.FONT_SCOREBOARD);
        Rectangle2D usernameBounds = metrics.getStringBounds(player.username, g);
        Rectangle2D scoreBounds = metrics.getStringBounds(String.valueOf(player.score), g);
        
        int lineWidth = getWidth() / 2;
        int lineHeight = (int) usernameBounds.getHeight() + 8;
        int lineX = getWidth() / 4;
        int lineY = getHeight() / 3 - ((m / 2) - i) * (lineHeight + 4);
        
        if (player.team == Team.TEAM_CRIMSON) {
            g.setColor(GraphicsStyles.ALPHA_CRIMSON);
        } else if (player.team == Team.TEAM_SILVER) {
            g.setColor(GraphicsStyles.ALPHA_SILVER);
        }
        g.fillRect(lineX, lineY, lineWidth + 2, lineHeight + 2);
        g.setColor(GraphicsStyles.DARK_BACKGROUND);
        g.fillRect(lineX, lineY + lineHeight, lineWidth + 2, 2);
        g.fillRect(lineX + lineWidth, lineY, 2, lineHeight);
        
        if (player.team == Team.TEAM_CRIMSON) {
            g.setColor(Color.WHITE);
        } else if (player.team == Team.TEAM_SILVER) {
            g.setColor(Color.WHITE);
        }
        g.drawString(player.username, lineX + 6, lineY + lineHeight - 8);
        int scoreX = lineX + lineWidth - 6 - (int) scoreBounds.getWidth();
        g.drawString(String.valueOf(player.score), scoreX, lineY + lineHeight - 8);
    }

    @Override
    public void onLogin(String username) {}

    @Override
    public void onReceiveRoomServersConfigs(LinkedList<RoomServerConfig> config) {}

    @Override
    public void onStartGame() {}

    @Override
    public void onStopGame() {}

    @Override
    public void onExit() {}
}
