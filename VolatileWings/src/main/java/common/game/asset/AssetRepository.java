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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class AssetRepository {
    public static final String TILEMAP = "tilemap";
    public static final String TILESET = "tileset.png";
    public static final String AIRCRAFT = "aircraft.png";
    private static final AssetRepository SINGLETON_INSTANCE = new AssetRepository();
    private final Map<String, BufferedImage> textures = new HashMap<>();
    private String repository = "assets/";
    
    private AssetRepository() {}
    
    public static AssetRepository getInstance() {
        return SINGLETON_INSTANCE;
    }
    
    public void setRepositoryLocation(String repository) {
        this.repository = repository;
    }
    
    public AssetDescriptor getDescriptor(String filename) throws Exception {
        AssetDescriptor descriptor = new AssetDescriptor(filename);
        if (!assetFileExists(filename)) {
            descriptor.length = 0;
            descriptor.checksum = new byte[0];
            return descriptor;
        }
        try (FileInputStream fis = new FileInputStream(new File(repository + filename))) {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                digest.update(buffer, 0, length);
                descriptor.length += length;
            }
            descriptor.checksum = digest.digest();
        }
        return descriptor;
    }
    
    public void copyAssetToStream(AssetDescriptor asset, ObjectOutputStream oos) throws Exception {
        try (FileInputStream fis = new FileInputStream(new File(repository + asset.filename))) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                oos.write(buffer, 0, length);
                oos.flush();
            }
        }
    }
    
    public void copyStreamToAsset(ObjectInputStream ois, AssetDescriptor asset) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(new File(repository + asset.filename))) {
            byte[] buffer = new byte[1024];
            int length;
            int received = 0;
            while ((length = ois.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
                fos.flush();
                received += length;
                if (received >= asset.length) {
                    break;
                }
            }
        }
    }
    
    public byte[] getTileMap() {
        // TODO
        return null;
    }
    
    public BufferedImage getTileSetTexture() {
        return loadTexture(repository + TILESET);
    }
    
    public BufferedImage getAircraftTexture() {
        return loadTexture(repository + AIRCRAFT);
    }
    
    private BufferedImage loadTexture(String filename) {
        if (!textures.containsKey(filename)) {
            try {
                BufferedImage texture = ImageIO.read(new File(filename));
                textures.put(filename, texture);
            } catch (IOException e) {}
        }
        return textures.get(filename);
    }
    
    private boolean assetFileExists(String filename) {
        return new File(repository + filename).exists();
    }
}
