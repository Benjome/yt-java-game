package com.zerulus.game.tiles;

import com.zerulus.game.graphics.Screen;
import com.zerulus.game.graphics.SpriteSheet;
import com.zerulus.game.util.Camera;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import com.zerulus.game.tiles.blocks.NormBlock;

import java.io.File;

public class TileManager {

    public static ArrayList<TileMap> tm;
    public Camera cam;

    public TileManager() {
        tm = new ArrayList<TileMap>();
    }

    public TileManager(String path, Camera cam) {
        tm = new ArrayList<TileMap>();
        addTileMap(path, 64, 64, cam);
    }

    public TileManager(String path, int blockWidth, int blockHeight, Camera cam) {
        tm = new ArrayList<TileMap>();
        addTileMap(path, blockWidth, blockHeight, cam);
    }

    private void addTileMap(String path, int blockWidth, int blockHeight, Camera cam) {
        this.cam = cam;
        String imagePath;

        int width = 0;
        int height = 0;
        int tileWidth;
        int tileHeight;
        int tileColumns;
        int layers = 0;
        SpriteSheet sprite;

        String[] data = new String[10];

        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document doc = builder.parse(new File(getClass().getClassLoader().getResource(path).toURI()));
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("tileset");
            Node node = list.item(0);
            Element eElement = (Element) node;

            imagePath = eElement.getAttribute("name");
            tileWidth = Integer.parseInt(eElement.getAttribute("tilewidth"));
            tileHeight = Integer.parseInt(eElement.getAttribute("tileheight"));
            tileColumns =  Integer.parseInt(eElement.getAttribute("columns"));
            
            sprite = new SpriteSheet("tile/" + imagePath + ".png", tileWidth, tileHeight);

            if(tileWidth != blockWidth || tileHeight != blockHeight) {
                sprite.resize(blockWidth * (sprite.getSpriteSheet().w / tileWidth), blockHeight * (sprite.getSpriteSheet().h / tileHeight));
                sprite.setSize(blockWidth, blockHeight);
            }

            list = doc.getElementsByTagName("layer");
            layers = list.getLength();

            for(int i = 0; i < layers; i++) {
                node = list.item(i);
                eElement = (Element) node;
                if(i <= 0) {
                    width = Integer.parseInt(eElement.getAttribute("width"));
                    height = Integer.parseInt(eElement.getAttribute("height"));
                }

                data[i] = eElement.getElementsByTagName("data").item(0).getTextContent();

                if(i >= 1) {
                    tm.add(new TileMapNorm(data[i], sprite, width, height, blockWidth, blockHeight, tileColumns));
                } else {
                    tm.add(new TileMapObj(data[i], sprite, width, height, blockWidth, blockHeight, tileColumns));
                }
            }

            cam.setLimit(width * blockWidth, height * blockHeight);
        } catch(Exception e) {
            System.out.println("ERROR - TILEMANAGER: can not read tilemap:");
            e.printStackTrace();
            System.exit(0);
        }
    }

    public NormBlock getNormalTile(int id) {
        NormBlock block = (NormBlock) tm.get(1).getBlocks()[id];
        if(block != null) {
            return block;
        }

        return null;
    }

    public void render(Screen s) {
        if(cam == null)
            return;

        for(int i = 0; i < tm.size(); i++) {
            tm.get(i).render(s, cam.getBounds());
        }
    }
}
