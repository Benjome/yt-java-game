package com.zerulus.game.states;

import com.zerulus.game.GamePanel;
import com.zerulus.game.util.KeyHandler;
import com.zerulus.game.util.MouseHandler;
import com.zerulus.game.util.Vector2f;
import com.zerulus.game.util.AABB;
import com.zerulus.game.util.Camera;
import com.zerulus.game.graphics.Font;
import com.zerulus.game.graphics.Screen;
import com.zerulus.game.graphics.SpriteSheet;

import java.awt.Color;

public class GameStateManager {

    private GameState states[];

    public static Vector2f map;

    public static final int MENU = 0;
    public static final int PLAY = 1;
    public static final int PAUSE = 2;
    public static final int GAMEOVER = 3;
    public static final int EDIT = 4;

    public static Font font;
    public static SpriteSheet ui;
    public static Camera cam;

    public GameStateManager() {
        map = new Vector2f(GamePanel.width, GamePanel.height);
        Vector2f.setWorldVar(map.x, map.y);

        states = new GameState[5];

        font = new Font("font/font.png", 10, 10);
        SpriteSheet.currentFont = font;
        font.setDefaultColor(Color.decode("0xffffff"));

        ui = new SpriteSheet("ui/ui.png", 64, 64);

        cam = new Camera(new AABB(new Vector2f(0, 0), GamePanel.width + 64, GamePanel.height + 64));

        states[PLAY] = new PlayState(this, cam);
    }

    public boolean isStateActive(int state) {
        return states[state] != null;
    }

    public GameState getState(int state) {
        return states[state];
    }

    public void pop(int state) {
        states[state] = null;
    }

    public void add(int state) {
        if (states[state] != null)
            return;

        if (state == PLAY) {
            cam = new Camera(new AABB(new Vector2f(0, 0), GamePanel.width + 64, GamePanel.height + 64));
            states[PLAY] = new PlayState(this, cam);
        }
        else if (state == MENU) {
            states[MENU] = new MenuState(this);
        }
        else if (state == PAUSE) {
            states[PAUSE] = new PauseState(this);
        }
        else if (state == GAMEOVER) {
            states[GAMEOVER] = new GameOverState(this);
        }
        else if (state == EDIT) {
            if(states[PLAY] != null) {
                states[EDIT] = new EditState(this, cam);
            }
        }
    }

    public void addAndpop(int state) {
        addAndpop(state, 0);
    }

    public void addAndpop(int state, int remove) {
        pop(state);
        add(state);
    }

    public void update(double time) {
        for (int i = 0; i < states.length; i++) {
            if (states[i] != null) {
                states[i].update(time);
            }
        }
    }

    public void input(MouseHandler mouse, KeyHandler key) {
        
        for (int i = 0; i < states.length; i++) {
            if (states[i] != null) {
                states[i].input(mouse, key);
            }
        }        
    }

    public void render(Screen s) {
        for (int i = 0; i < states.length; i++) {
            if (states[i] != null) {
                states[i].render(s);
            }
        }
    }

}
