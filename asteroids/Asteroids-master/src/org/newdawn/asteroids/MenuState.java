package org.newdawn.asteroids;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.newdawn.asteroids.gui.BitmapFont;
import org.newdawn.asteroids.sound.Sound;
import org.newdawn.asteroids.sound.SoundLoader;
import org.newdawn.spaceinvaders.lwjgl.Texture;
import org.newdawn.spaceinvaders.lwjgl.TextureLoader;

/**
 * The menu state display options for the player to start the game
 * 
 * @author Drew Murphy, Kevin Glass
 */
public class MenuState implements GameState {
	public static final String NAME = "menu";
	private static final int START = 0;
	private static final int EXIT = 1;
	private Texture background;
	private BitmapFont font;
	
	private Sound menu;
	
	private String[] options = new String[] {"Start Game", "   Exit"};
	private int selected = 0;
	
	/**
	 * @see org.newdawn.asteroids.GameState#getName()
	 */
	public String getName() {
		return NAME;
	}

	/**
	 * @see org.newdawn.asteroids.GameState#init(org.newdawn.asteroids.GameWindow)
	 */
	public void init(GameWindow window) throws IOException {
		TextureLoader loader = new TextureLoader();
		background = loader.getTexture("res/bg.jpg");
		
		Texture fontTexture = loader.getTexture("res/spaceage.png");
		font = new BitmapFont(fontTexture, 32, 32);
		menu = SoundLoader.get().getOgg("res/menu.ogg");
	}

	/**
	 * @see org.newdawn.asteroids.GameState#render(org.newdawn.asteroids.GameWindow, int)
	 */
	public void render(GameWindow window, int delta) {
		GL11.glColor3f(0.2f,0.2f,0.3f);
		drawBackground(window);
		
		window.enterOrtho();

		GL11.glColor3f(1f,1f,1f);
		font.drawString(1, "ASTEROIDS", 280, 210);
		
		for (int i=0;i<options.length;i++) {
			GL11.glColor3f(0.5f,0.5f,0);
			if (selected == i) {
				GL11.glColor3f(1,1,0.3f);
			}
			font.drawString(0, options[i], 270, 280+(i*40));
		}
		
		window.leaveOrtho();
	}

	/**
	 * Draw a background to the window
	 * 
	 * @param window The window to which the background should be drawn
	 */
	private void drawBackground(GameWindow window) {
		window.enterOrtho();
		
		background.bind();
		
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0,0);
			GL11.glVertex2i(0,0);
			GL11.glTexCoord2f(0,1);
			GL11.glVertex2i(0,600);
			GL11.glTexCoord2f(1,1);
			GL11.glVertex2i(800,600);
			GL11.glTexCoord2f(1,0);
			GL11.glVertex2i(800,0);
		GL11.glEnd();
		
		window.leaveOrtho();
	}
	
	/**
	 * @see org.newdawn.asteroids.GameState#update(org.newdawn.asteroids.GameWindow, int)
	 */
	public void update(GameWindow window, int delta) {
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				if (Keyboard.getEventKey() == Keyboard.KEY_UP) {
					selected--;
					if (selected < 0) {
						selected = options.length - 1;
					}
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_DOWN) {
					selected++;
					if (selected >= options.length) {
						selected = 0;
					}
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_RETURN) {
					if (selected == START) {
						window.changeToState(InGameState.NAME);
					}
					if (selected == EXIT) {
						System.exit(0);
					}
				}
			}
		}
	}

	/**
	 * @see org.newdawn.asteroids.GameState#enter(org.newdawn.asteroids.GameWindow)
	 */
	public void enter(GameWindow window) {
		menu.play(1.0f, 1.0f, true);
	}

	/**
	 * @see org.newdawn.asteroids.GameState#leave(org.newdawn.asteroids.GameWindow)
	 */
	public void leave(GameWindow window) {
		menu.stop();
	}

}
