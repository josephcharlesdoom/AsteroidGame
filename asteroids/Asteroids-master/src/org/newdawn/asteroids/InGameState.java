package org.newdawn.asteroids;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.newdawn.asteroids.entity.Ammo;
import org.newdawn.asteroids.entity.Entity;
import org.newdawn.asteroids.entity.EntityManager;
import org.newdawn.asteroids.entity.Player;
import org.newdawn.asteroids.entity.Rock;
import org.newdawn.asteroids.gui.BitmapFont;
import org.newdawn.asteroids.model.ObjLoader;
import org.newdawn.asteroids.model.ObjModel;
import org.newdawn.asteroids.sound.Sound;
import org.newdawn.asteroids.sound.SoundLoader;
import org.newdawn.spaceinvaders.lwjgl.Texture;
import org.newdawn.spaceinvaders.lwjgl.TextureLoader;

/**
 * This state is responsible for rendering the game world and handling
 * the mechanics of game play.
 * 
 * @author Drew Murphy, Kevin Glass
 */
public class InGameState implements GameState, EntityManager {
	public static final String NAME = "ingame";

	/** The texture to be applied to the background */
	private Texture background;
	/** The texture to be applied to the shot */
	private Texture shotTexture;
	
	/** The texture to abe applied to the player's ship */
	private Texture shipTexture, shipTexture_2, shipTexture_3, shipTexture_4, shipTexture_5, shipTexture_6;
	/** The model of the player's ship to be rendered */
	private ObjModel shipModel;
	
	/** The texture to be applied to the rock entity */
	private Texture rockTexture;
	/** The model of the rock entity */
	private ObjModel rockModel;
	
	/** The texture of the shotgun shell ammo pickup */
	private Texture shellTexture;
	/** The model of the shotgun shell ammo pickup */
	private ObjModel shellModel;
	
	/** The texture of the laser ammo pickup */
	private Texture laserTexture;
	/** The model of the laser ammo pickup */
	private ObjModel laserModel;
	
	/** The texture of the shield_pickup ammo pickup */
	private Texture shieldTexture;
	/** The model of the shield_pickup ammo pickup */
	private ObjModel shieldModel;
	
	/** The texture of the multi-shot ammo pickup */
	private Texture multiTexture;
	/** The model of the multi-shot ammo pickup */
	private ObjModel multiModel;
	
	/** The texture of the multi-shot ammo pickup */
	private Texture shrapnelTexture;
	/** The model of the multi-shot ammo pickup */
	private ObjModel shrapnelModel;
	
	/** The font to be used in-game */
	private BitmapFont font;
	/** The ship the player controls in-game */
	private Player player;
	
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	private ArrayList <Entity>addList = new ArrayList<Entity>();
	private ArrayList <Entity>removeList = new ArrayList<Entity>();
	
	private ArrayList <Texture> shipTextures = new ArrayList<Texture>();

	/** The OpenGL material properties applied to everything in the game */
	private FloatBuffer material;
	
	private int score;
	private int life = 4;
	
	/** The number of shots the player has taken in the game */
	private int shotsTaken = 0;
	/** The number of shots that have collided with a rock */
	private int successfulShots = 0;
	
	private boolean gameOver;
	
	/** The score remaining until the player obtains an extra life */
	private int toNextLife = 25000;
	/** The constant that determines how often a player can obtain an extra life */
	private final int LIFE_THRESHOLD = 25000;
	
	private final int AMMO_LOCKOUT = 250;
	private int toAmmoSpawn = AMMO_LOCKOUT;
	
	/** The current String associated with the equipped ammo type. For GUI use. */
	private String ammoType = "NORMAL";
	/** The current String associated with the amount of ammo for the equipped type */
	private String ammoAmount = "";
	
	/** The amount of shotgun ammo the player has */
	private int shotgunAmmo = 0;
	/** The amount of shield_pickup ammo the player has */
	private int shieldAmmo = 0;
	/** The amount of laser ammo the player has */
	private int laserAmmo = 0;
	/** The amount of multi-shot ammo the player has */
	private int multiAmmo = 0;
	/** The amount of shrapnel ammo the player has */
	private int shrapnelAmmo = 0;
	
	/** The current String associated with the duration of the shield_pickup remaining */
	private String shieldDuration = "";
	
	private Sound gun;
	private Sound shotgun_pickup, shotgun;
	private Sound shield_pickup, shield_up, shield_down;
	private Sound laser_pickup, laser;
	private Sound multi_pickup, multi;
	private Sound shrapnel_pickup, shrapnel;
	
	private Sound split;
	private Sound level_up;
	private Sound extra_life;
	private Sound low_ammo;
	
	
	private Sound music;
	
	private int level;
	private int gameOverTimeout;
	
	/**
	 * Create a new game state
	 */
	public InGameState() {
	}

	/**
	 * @see org.newdawn.asteroids.GameState#getName()
	 */
	public String getName() {
		return NAME;
	}
	
	/**
	 * Define the light setup to view the scene
	 */
	private void defineLight() {
		FloatBuffer buffer;
		
		buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(1).put(1).put(1).put(1); 
		buffer.flip();
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, buffer);
		
		buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(1).put(1).put(1).put(1);
		buffer.flip();
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, buffer);
		
		// setup the ambient light 
		buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(0.8f).put(0.8f).put(0.8f).put(0.8f);
		buffer.flip();
		GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, buffer);
		GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE, GL11.GL_TRUE);
		
		// set up the position of the light
		buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(10).put(10).put(5).put(0);
		buffer.flip();
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, buffer);
		
		GL11.glEnable(GL11.GL_LIGHT0);
		
		material = BufferUtils.createFloatBuffer(4);
	}

	/**
	 * @see org.newdawn.asteroids.GameState#init(org.newdawn.asteroids.GameWindow)
	 */
	public void init(GameWindow window) throws IOException {
		defineLight();
		
		TextureLoader loader = new TextureLoader();
		background = loader.getTexture("res/bg.jpg");
		shotTexture = loader.getTexture("res/shot.png");
		
		shipTexture = loader.getTexture("res/spaceship.png");
		shipTexture_2 = loader.getTexture("res/spaceship_2.png");
		shipTexture_3 = loader.getTexture("res/spaceship_3.png");
		shipTexture_4 = loader.getTexture("res/spaceship_4.png");
		shipTexture_5 = loader.getTexture("res/spaceship_5.png");
		shipTexture_6 = loader.getTexture("res/spaceship_6.png");
		
		shipTextures.add(shipTexture);
		shipTextures.add(shipTexture_2);
		shipTextures.add(shipTexture_3);
		shipTextures.add(shipTexture_4);
		shipTextures.add(shipTexture_5);
		shipTextures.add(shipTexture_6);
		
		shipModel = ObjLoader.loadObj("res/spaceship.obj");
		
		rockTexture = loader.getTexture("res/rock.jpg");
		rockModel = ObjLoader.loadObj("res/rock.obj");
		
		shellTexture = loader.getTexture("res/shell.png");
		shellModel = ObjLoader.loadObj("res/shell.obj");
		
		shieldTexture = loader.getTexture("res/shield_pickup.png");
		shieldModel = ObjLoader.loadObj("res/shield_pickup.obj");
		
		laserTexture = loader.getTexture("res/laser.png");
		laserModel = ObjLoader.loadObj("res/laser.obj");
		
		multiTexture = loader.getTexture("res/multi.png");
		multiModel = ObjLoader.loadObj("res/multi.obj");
		
		shrapnelTexture = loader.getTexture("res/shrapnel.png");
		shrapnelModel = ObjLoader.loadObj("res/shrapnel.obj");
		
		Texture fontTexture = loader.getTexture("res/spaceage.png");
		font = new BitmapFont(fontTexture, 32, 32);
		
		gun= SoundLoader.get().getOgg("res/gun.ogg");
		
		shotgun_pickup = SoundLoader.get().getOgg("res/shell_pickup.ogg");
		shotgun= SoundLoader.get().getOgg("res/shotgun.ogg");
		
		shield_pickup = SoundLoader.get().getOgg("res/shield_pickup.ogg");
		shield_up = SoundLoader.get().getOgg("res/shield_up.ogg");
		shield_down = SoundLoader.get().getOgg("res/shield_down.ogg");
		
		laser_pickup = SoundLoader.get().getOgg("res/laser_pickup.ogg");
		laser = SoundLoader.get().getOgg("res/laser.ogg");
		
		multi_pickup = SoundLoader.get().getOgg("res/multi_pickup.ogg");
		multi = SoundLoader.get().getOgg("res/multi.ogg");
		
		shrapnel_pickup = SoundLoader.get().getOgg("res/shrapnel_pickup.ogg");
		shrapnel = SoundLoader.get().getOgg("res/shrapnel.ogg");
		
		split = SoundLoader.get().getOgg("res/bush.ogg");
		level_up = SoundLoader.get().getOgg("res/level_up.ogg");
		extra_life = SoundLoader.get().getOgg("res/1up.ogg");
		low_ammo = SoundLoader.get().getOgg("res/low_ammo.ogg");
		
		music = SoundLoader.get().getOgg("res/ingame.ogg");
	}

	/**
	 * @see org.newdawn.asteroids.GameState#render(org.newdawn.asteroids.GameWindow, int)
	 */
	public void render(GameWindow window, int delta) {
		// reset the view transformation matrix back to the empty
		// state. 
		GL11.glLoadIdentity();

		material.put(1).put(1).put(1).put(1); 
		material.flip();
		GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, material);
		GL11.glMaterial(GL11.GL_BACK, GL11.GL_DIFFUSE, material);
		
		// draw our background image
		GL11.glDisable(GL11.GL_LIGHTING);
		drawBackground(window);
		
		// position the view a way back from the models so we
		// can see them
		GL11.glTranslatef(0,0,-50);

		// loop through all entities in the game rendering them
		for (int i=0;i<entities.size();i++) {
			Entity entity = (Entity) entities.get(i);
			
			entity.render();
		}
		
		drawGUI(window);
	}

	/**
	 * Draw the overlay for score and lifes
	 * 
	 * @param window The window in which the GUI is displayed 
	 */
	private void drawGUI(GameWindow window) {
		window.enterOrtho();
		
		GL11.glColor3f(1,1,0);
		font.drawString(1, "SCORE:" + score, 5, 5);
		
		GL11.glColor3f(0, 1, 0.3f);
		font.drawString(1, ammoType + ammoAmount, 5, 40);
		
		if (!shieldDuration.equals("")) {
			font.drawString(1, "REMAINING:" + shieldDuration, 5, 75);
		}
		
		GL11.glColor3f(1,0,0);
		
		String lifeString = "LIVES:" + life;
		if (life >= 0) font.drawString(1, lifeString, 795 - (lifeString.length() * 27), 5);
		if (ammoAmount.equals("0")) font.drawString(1, "NO AMMO", 5, 565);

		if (gameOver) {
			font.drawString(1, "GAME OVER", 280, 286);
			if (shotsTaken == 0) {
				shotsTaken = 1;
				successfulShots = 1;
			}
			float accuracy = (float)successfulShots / (float) shotsTaken;
			accuracy = accuracy*100;
			String acc = String.format("%.2f", accuracy);
			font.drawString(1, "Accuracy:%" + acc, 200, 310);
		}
		GL11.glColor3f(1,1,1);
		
		String level_string = "Level" + (level - 4);
		font.drawString(1, level_string,795 - (level_string.length()*27), 565);

		
		window.leaveOrtho();
	}
	
	/**
	 * Draw the background image
	 * 
	 * @param window The window to display the background in 
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
		
		toAmmoSpawn -= delta;
		
		if (gameOver) {
			gameOverTimeout -= delta;
			if (gameOverTimeout < 0) {
				window.changeToState(MenuState.NAME);
			}
		}
		
		for (int i=0;i<entities.size();i++) {
			Entity entity = (Entity) entities.get(i);
			
			for (int j=i+1;j<entities.size();j++) {
				Entity other = (Entity) entities.get(j);
				
				if (entity.collides(other)) {
					entity.collide(this, other);
					other.collide(this, entity);
				}
			}
		}
		
		entities.removeAll(removeList);
		entities.addAll(addList);
		
		removeList.clear();
		addList.clear();
		
		// loop through all the entities in the game causing them
		// to update (i.e. move, shoot, etc)
		int rockCount = 0;
		
		for (int i=0;i<entities.size();i++) {
			Entity entity = (Entity) entities.get(i);
			
			entity.update(this, delta);
			
			if (entity instanceof Rock) {
				rockCount++;
			}
		}
		
		if (rockCount == 0) {
			level++;
			spawnRocks(level);
			level_up.play(1.0f, 1.0f, false);
		}
		
		if (toNextLife <= 0) {
			life++;
			toNextLife = LIFE_THRESHOLD;
			extra_life.play(1.0f, 1.0f, false);
		}
		
		
		
	}

	/**
	 * @see org.newdawn.asteroids.GameState#enter(org.newdawn.asteroids.GameWindow)
	 */
	public void enter(GameWindow window) {
		entities.clear();
		
		music.play(1.0f, 1.0f, true);
		
		player = new Player(shipTextures, shipModel, shotTexture);
		entities.add(player);
		
		life = 4;
		score = 0;
		level = 5;
		
		shotsTaken = 0;
		successfulShots = 0;
		
		shotgunAmmo = 0;
		shieldAmmo = 0;
		laserAmmo = 0;
		multiAmmo = 0;
		shrapnelAmmo = 0;
		
		gameOver = false;
		
		spawnRocks(level);
	}

	/**
	 * Spawn some asteroids into the game world
	 * 
	 * @param count The number of rocks to be spawned
	 */
	private void spawnRocks(int count) {
		// spawn some rocks
		int fails = 0;
		
		for (int i=0;i<count;i++) {
			float xp = (float) (-20 + (Math.random() * 40));
			float yp = (float) (-20 + (Math.random() * 40));
			
			Rock rock = new Rock(rockTexture, rockModel, xp, yp, 3);
			if (!rock.collides(player)) {
				entities.add(rock);
			} else {
				i--;
				fails++;
			}
			
			if (fails > 5) {
				return;
			}
		}
	}
	
	/**
	 * @see org.newdawn.asteroids.GameState#leave(org.newdawn.asteroids.GameWindow)
	 */
	public void leave(GameWindow window) {
	}

	/**
	 * @see org.newdawn.asteroids.entity.EntityManager#removeEntity(org.newdawn.asteroids.entity.Entity)
	 */
	public void removeEntity(Entity entity) {
		removeList.add(entity);
	}

	/**
	 * @see org.newdawn.asteroids.entity.EntityManager#addEntity(org.newdawn.asteroids.entity.Entity)
	 */
	public void addEntity(Entity entity) {
		addList.add(entity);
	}

	/**
	 * @see org.newdawn.asteroids.entity.EntityManager#rockDestroyed(int)
	 */
	public void rockDestroyed(int size) {
		split.play(1.0f,1.0f, false);
		score += (4 - size) * 100;
		toNextLife -= (4 - size) * 100;
	}

	/**
	 * @see org.newdawn.asteroids.entity.EntityManager#playerHit()
	 */
	public void playerHit() {
		life--;
		if (life < 0) {
			gameOver = true;
			gameOverTimeout = 3000;
			removeEntity(player);
		}
	}

	/**
	 * @see org.newdawn.asteroids.entity.EntityManager#shotFired()
	 */
	public void shotFired(int mode) {
		switch (mode) {
		case 0:
			gun.play(1.0f,0.5f, false);
			shotsTaken++;
			break;
		case 1:
			shotgun.play(1.0f,0.5f, false);
			shotsTaken = shotsTaken + 3;
			break;
		case 2:
			shield_up.play(1.0f, 1.0f, false);
			break;
		case 3:
			laser.play(1.0f, 1.0f, false);
			shotsTaken++;
			break;
		case 4:
			multi.play(1.0f, 1.0f, false);
			shotsTaken= shotsTaken + 4;
			break;
		case 5:
			shrapnel.play(1.0f, 1.0f, false);
			shotsTaken++;
			break;
		}
	}
	
	/**
	 * @see org.newdawn.asteroids.entity.EntityManager#updateAmmo()
	 */
	public void updateAmmo(int mode, int delta, boolean pickup) {
		switch(mode) {
		case 1:
			shotgunAmmo = shotgunAmmo + delta;
			if (pickup) shotgun_pickup.play(1.0f,0.5f, false);
			break;
		case 2:
			shieldAmmo = shieldAmmo + delta;
			if (pickup) shield_pickup.play(1.0f,0.5f, false);
			break;
		case 3:
			laserAmmo = laserAmmo + delta;
			if (pickup) laser_pickup.play(1.0f, 1.0f, false);
			break;
		case 4:
			multiAmmo = multiAmmo + delta;
			if (pickup) multi_pickup.play(1.0f, 1.0f, false);
			break;
		case 5:
			shrapnelAmmo = shrapnelAmmo + delta;
			if (pickup) shrapnel_pickup.play(1.0f, 1.0f, false);
			break;
		}
		setAmmoType(mode);
	}
	
	/**
	 * @see org.newdawn.asteroids.entity.EntityManager#getAmmo()
	 */
	public int getAmmo(int mode) {
		switch(mode) {
		case 1:
			return shotgunAmmo;
		case 2:
			return shieldAmmo;
		case 3:
			return laserAmmo;
		case 4:
			return multiAmmo;
		case 5:
			return shrapnelAmmo;
		default:
			return 0;
		}
	}
	
	/**
	 * @see org.newdawn.asteroids.entity.EntityManager#setAmmoType()
	 */
	public void setAmmoType(int mode) {
		switch(mode) {
		case 0:
			ammoType = "NORMAL";
			ammoAmount = "";
			break;
		case 1:
			ammoType = "SHOTGUN:";
			ammoAmount = shotgunAmmo + "";
			break;
		case 2:
			ammoType = "SHIELD:";
			ammoAmount = shieldAmmo + "";
			break;
		case 3:
			ammoType = "LASER:";
			ammoAmount = laserAmmo + "";
			break;
		case 4:
			ammoType = "MULTI-SHOT:";
			ammoAmount = multiAmmo + "";
			break;
		case 5:
			ammoType = "SHRAPNEL:";
			ammoAmount = shrapnelAmmo + "";
			break;
		}
	}
	
	/**
	 * @see org.newdawn.asteroids.entity.EntityManager#setShieldDuration()
	 */
	public void setShieldDuration(String duration) {
		shieldDuration = duration;
	}
	
	/**
	 * @see org.newdawn.asteroids.entity.EntityManager#successfulShot()
	 */
	public void successfulShot() {
		successfulShots++;
	}
	
	/**
	 * @see org.newdawn.asteroids.entity.EntityManager#spawnAmmo()
	 */
	public void spawnAmmo(float x, float y) {
		toAmmoSpawn = AMMO_LOCKOUT;
		Random r = new Random();
		float i = r.nextFloat();
		Ammo toSpawn;
		if (i > 0 && i < 0.4f) toSpawn = new Ammo(shellTexture, 1, shellModel, x, y, 1);
		else if (i >= 0.4f && i < 0.6f) toSpawn = new Ammo(shieldTexture, 2, shieldModel, x, y, 1);
		else if (i >= 0.6f && i < 0.75f) toSpawn = new Ammo(laserTexture, 3, laserModel, x, y, 1);
		else if (i >= 0.75f && i < 0.9f) toSpawn = new Ammo(multiTexture, 4, multiModel, x, y ,1);
		else toSpawn = new Ammo(shrapnelTexture, 5, shrapnelModel, x, y ,1);
		
		addEntity(toSpawn);
	}
	
	/**
	 * @see org.newdawn.asteroids.entity.EntityManager#outOfAmmo()
	 */
	public void outOfAmmo() {
		low_ammo.play(1.0f, 1.0f, false);
	}
	
	/**
	 * @see org.newdawn.asteroids.entity.EntityManager#readyToSpawn()
	 */
	public boolean readyToSpawn() {
		if (toAmmoSpawn <= 0) return true;
		else return false;
	}
	
	/**
	 * @see org.newdawn.asteroids.entity.EntityManager#shieldDown()
	 */
	public void shieldDown() {
		shield_down.play(1.0f, 1.0f, false);
	}
}
