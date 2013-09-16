package org.newdawn.asteroids.entity;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.newdawn.asteroids.model.ObjModel;
import org.newdawn.asteroids.particles.ParticleGroup;
import org.newdawn.spaceinvaders.lwjgl.Texture;

/**
 * The entity representing the player. This entity is responsible for
 * displaying a model, a particle system for the player's engine and
 * for creating shot entities based on player input.
 * 
 * @author Drew Murphy, Kevin Glass
 */
public class Player extends AbstractEntity {
	private ArrayList<Texture> textureList;
	/** The texture to be applied to the model*/
	private Texture texture;
	/** The model to be rendered */
	private ObjModel model;
	
	/** The x component of the forward vector */
	private float forwardX = 0;
	/** The y component of the forward vector */
	private float forwardY = 1;
	
	/** The time remaining until the next shot can be taken */
	private int shotTimeout;
	/** The time between shots */
	private int shotInterval = 300;
	
	/** The current weapon equipped */
	private int firingMode = 0;
	/** The state of the player's shield */
	private boolean shieldImmune = false;
	
	/** The texture of the shot */
	private Texture shotTexture;
	/** The engine flare */
	private ParticleGroup engine;
	
	private boolean mouseMode = true;
	private final int MOUSE_TOGGLE_INTERVAL = 300;
	private int mouseToggle = MOUSE_TOGGLE_INTERVAL;
	
	private boolean testMode = false;
	private final int TEST_TOGGLE_INTERVAL = 500;
	private int testToggle = TEST_TOGGLE_INTERVAL;
	
	/**
	 * Create a new Player entity
	 * 
	 * @param texture The texture to apply to the player's model
	 * @param model The model to display for the player
	 * @param shotTexture The texture to apply to the shot's created when
	 * the player fires
	 */
	public Player(ArrayList<Texture> textureList, ObjModel model, Texture shotTexture) {
		this.textureList = textureList;
		this.texture = textureList.get(0);
		this.model = model;
		this.shotTexture = shotTexture;
		
		engine = new ParticleGroup(100,200,0,0,10);
		
	}
	
	/**
	 * @see org.newdawn.asteroids.entity.Entity#update(org.newdawn.asteroids.entity.EntityManager, int)
	 */
	public void update(EntityManager manager, int delta) {
		if (Mouse.hasWheel()) {
			int dwheel = Mouse.getDWheel();
			if (dwheel != 0) {
				wheelWeaponMode(dwheel, manager);
			}
		
		}
		
		if (mouseMode) calculateRotation();
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A)) {
			rotationZ += (delta / 5.0f);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D)) {
			rotationZ -= (delta / 5.0f);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
			changeGuns(0, manager);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_2)) {
			changeGuns(1, manager);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_3)) {
			changeGuns(2, manager);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_4)) {
			changeGuns(3, manager);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_5)) {
			changeGuns(4, manager);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_6)) {
			changeGuns(5, manager);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S) || Mouse.isButtonDown(2)) {
			setVelocity(getVx()/1.003f, getVy()/1.003f);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_TAB)) {
			mouseToggle();
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)
				&& Keyboard.isKeyDown(Keyboard.KEY_T)) {
			toggleTestMode();
		}
		
		shotTimeout -= delta;
		mouseToggle -= delta;
		testToggle -= delta;
		
		if (shotTimeout <= 0) {
			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) || Mouse.isButtonDown(0)) {
				shoot(firingMode, manager);
				shotTimeout = shotInterval;
			}
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W) || Mouse.isButtonDown(1)) {
			velocityX += (forwardX * delta) / 50.0f;
			velocityY += (forwardY * delta) / 50.0f;
			
			float flameOffset = 2.5f;
			engine.addParticle(positionX-(forwardX*flameOffset), 
					   positionY-(forwardY*flameOffset), 
					   0.6f, 150);
		}
		
		forwardX = (float) Math.sin(Math.toRadians(rotationZ));
		forwardY = (float) -Math.cos(Math.toRadians(rotationZ));
		
		super.update(manager, delta);
		engine.update(delta);
	}
	
	/**
	 * The default gun effect the player has equipped
	 * @param manager The EntityManager corresponding to the player (i.e. InGameState)
	 */
	private void normal(EntityManager manager) {
		Shot shot = new Shot(shotTexture, 
							 getX() + forwardX, 
							 getY() + forwardY, 
							 forwardX * 30, 
							 forwardY * 30,
							 800, 0.65f, 1, 0, 1, true, 200, false);
		
		manager.addEntity(shot);
		manager.shotFired(0);
	}
	
	/**
	 * Produces a shotgun blast according to the player's location and rotation
	 * @param manager The EntityManager corresponding to the player (i.e. InGameState)
	 */
	private void shotgun(EntityManager manager) {
		if (manager.getAmmo(1) == 0 && !testMode) {
			changeGuns(0, manager);
			manager.outOfAmmo();
			return;
		}
		
		float newRotationR = rotationZ - 15;
		float newRotationL = rotationZ + 15;
		
		float forwardXL = (float) Math.sin(Math.toRadians(newRotationL));
		float forwardYL = (float) -Math.cos(Math.toRadians(newRotationL));
		
		float forwardXR = (float) Math.sin(Math.toRadians(newRotationR));
		float forwardYR = (float) -Math.cos(Math.toRadians(newRotationR));
		
		Shot leftshot = new Shot(shotTexture, 
							 getX() + forwardX, 
							 getY() + forwardY, 
							 forwardXL * 75, 
							 forwardYL * 75,
							 200, 0.9f, 0, 1, 0, true, 200, false);
		
		Shot rightshot = new Shot(shotTexture, 
				 getX() + forwardX, 
				 getY() + forwardY, 
				 forwardXR * 75, 
				 forwardYR * 75,
				 200, 0.9f, 0, 1, 0, true, 200, false);
		
		Shot centershot = new Shot(shotTexture, 
				 getX() + forwardX, 
				 getY() + forwardY, 
				 forwardX * 75, 
				 forwardY * 75,
				 200, 0.9f, 0, 1, 0, true, 200, false);

		
		manager.addEntity(leftshot);
		manager.addEntity(rightshot);
		manager.addEntity(centershot);
		if (!testMode) manager.updateAmmo(1, -1, false);
		manager.shotFired(1);
	}
	
	/**
	 * Produces a shield that protects the player from damage
	 * @param manager The EntityManager corresponding to the player (i.e. InGameState)
	 */
	private void shield(EntityManager manager) {
		if (manager.getAmmo(2) == 0 && !testMode) {
			changeGuns(0, manager);
			manager.outOfAmmo();
			return;
		}
		Shield shield = new Shield(shotTexture, this, 
							 getX(), getY(),
							 25000, 3.5f, 0, 1, 1);
		
		manager.addEntity(shield);
		if (!testMode) manager.updateAmmo(2, -1, false);
		manager.shotFired(2);
		changeGuns(0, manager);
	}
		
	/**
	 * Produces a fast-traveling laser that is not destroyed on contact
	 * @param manager The EntityManager corresponding to the player (i.e. InGameState)
	 */
	private void laser(EntityManager manager) {
		if (manager.getAmmo(3) == 0 && !testMode) {
			changeGuns(0, manager);
			manager.outOfAmmo();
			return;
		}
		Shot laser = new Shot(shotTexture, 
							 getX() + forwardX, 
							 getY() + forwardY, 
							 forwardX * 300, 
							 forwardY * 300,
							 150, 3f, 1, 0, 0, false, 350, false);
		
		manager.addEntity(laser);
		if (!testMode) manager.updateAmmo(3, -1, false);;
		manager.shotFired(3);
	}
	
	/**
	 * Produces multiple shots in cardinal directions
	 * @param manager The EntityManager corresponding to the player (i.e. InGameState)
	 */
	private void multi(EntityManager manager) {
		if (manager.getAmmo(4) == 0 && !testMode) {
			changeGuns(0, manager);
			manager.outOfAmmo();
			return;
		}
		
		float newRotationR = rotationZ - 90;
		float newRotationL = rotationZ + 90;
		float newRotationD = rotationZ + 180;
		
		float leftX = (float) Math.sin(Math.toRadians(newRotationL));
		float leftY = (float) -Math.cos(Math.toRadians(newRotationL));
		
		float rightX = (float) Math.sin(Math.toRadians(newRotationR));
		float rightY = (float) -Math.cos(Math.toRadians(newRotationR));
		
		float downX = (float) Math.sin(Math.toRadians(newRotationD));
		float downY = (float) -Math.cos(Math.toRadians(newRotationD));
		
		Shot leftshot = new Shot(shotTexture, 
				 getX() + forwardX, 
				 getY() + forwardY, 
				 leftX * 100, 
				 leftY * 100,
				 150, 1.5f, 0, 1, 0, true, 200, false);
		
		Shot rightshot = new Shot(shotTexture, 
				 getX() + forwardX, 
				 getY() + forwardY, 
				 rightX * 100, 
				 rightY * 100,
				 150, 1.5f, 0, 0, 1, true, 200, false);
		
		Shot upshot = new Shot(shotTexture, 
				 getX() + forwardX, 
				 getY() + forwardY, 
				 forwardX * 100, 
				 forwardY * 100,
				 150, 1.5f, 1, 0, 0, true, 200, false);
		
		Shot downshot = new Shot(shotTexture, 
				 getX() + forwardX, 
				 getY() + forwardY, 
				 downX * 100, 
				 downY * 100,
				 150, 1.5f, 1, 1, 0.82f, true, 200, false);

		
		manager.addEntity(leftshot);
		manager.addEntity(rightshot);
		manager.addEntity(upshot);
		manager.addEntity(downshot);
		if (!testMode) manager.updateAmmo(4, -1, false);
		manager.shotFired(4);
	}
	
	/**
	 * Fires a split-shot that branches on impact
	 * @param manager The EntityManager corresponding to the player (i.e. InGameState)
	 */
	private void shrapnel(EntityManager manager) {
		if (manager.getAmmo(5) == 0 && !testMode) {
			changeGuns(0, manager);
			manager.outOfAmmo();
			return;
		}
		Shot shrapnel = new Shot(shotTexture, 
							 getX() + forwardX, 
							 getY() + forwardY, 
							 forwardX * 100, 
							 forwardY * 100,
							 400, 1.5f, 0, 0, 1, true, 250, true);
		
		manager.addEntity(shrapnel);
		if (!testMode) manager.updateAmmo(5, -1, false);;
		manager.shotFired(5);
	}
	
	/**
	 * Calls the appropriate firing method according to the current weapon equipped
	 * @param mode The current weapon equipped (0= default, 1=shotgun, 2=shield, 3=laser)
	 * @param manager The EntityManager corresponding to the player (i.e. InGameState)
	 */
	private void shoot(int mode, EntityManager manager) {
		switch(mode) {
		case 0:
			normal(manager);
			break;
		case 1:
			shotgun(manager);
			break;
		case 2:
			shield(manager);
			break;
		case 3:
			laser(manager);
			break;
		case 4:
			multi(manager);
			break;
		case 5:
			shrapnel(manager);
			break;
		}
	}
	
	/**
	 * @see org.newdawn.asteroids.entity.Entity#render()
	 */
	public void render() {
		GL11.glEnable(GL11.GL_LIGHTING);
		
		GL11.glPushMatrix();

		GL11.glTranslatef(positionX,positionY,0);
		GL11.glRotatef(rotationZ,0,0,1);
		GL11.glRotatef(90,1,0,0);
		GL11.glScalef(0.01f,0.01f,0.01f);		
		texture.bind();
		model.render();
		
		GL11.glPopMatrix();
		
		renderEngine();
	}

	/**
	 * Render the particle effect thats used to represent our
	 * ship's engine.
	 */
	private void renderEngine() {
		GL11.glDisable(GL11.GL_LIGHTING);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		
		shotTexture.bind();
		engine.render();
		
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	/**
	 * @see org.newdawn.asteroids.Entity#getSize()
	 */
	public float getSize() {
		return 2;
	}
	
	/**
	 * Changes the current gun equipped
	 * @param mode The weapon to switch to (0=default, 1=shotgun, 2=shield, 3=laser)
	 * @param manager The EntityManager corresponding to the player (i.e. InGameState)
	 */
	public void changeGuns(int mode, EntityManager manager) {
		manager.setAmmoType(mode);
		firingMode = mode;
		shotTimeout = 0;
		this.setTexture(textureList.get(mode));
		switch (mode) {
		case 0:
			shotInterval = 300;
			break;
		case 1:
			shotInterval = 600;
			break;
		case 2:
			shotInterval = 10000;
			break;
		case 3:
			shotInterval = 3000;
			break;
		case 4:
			shotInterval = 300;
			break;
		case 5:
			shotInterval = 1500;
			break;
		}
	}

	/**
	 * @see org.newdawn.asteroids.entity.Entity#collide(org.newdawn.asteroids.entity.EntityManager, org.newdawn.asteroids.entity.Entity)
	 */
	public void collide(EntityManager manager, Entity other) {
		if (other instanceof Rock) {
			velocityX = (getX() - other.getX());
			velocityY = (getY() - other.getY());
			
			((Rock) other).split(manager, this);
			if (!shieldImmune && !testMode) manager.playerHit();
		}
		
		if (other instanceof Ammo){
			changeGuns(((Ammo) other).getGunMode(), manager);
		}
	}
	
	/**
	 * @return The x component of the current velocity
	 */
	public float getVx() {
		return velocityX;
	}
	
	/**
	 * @return The y component of the current velocity
	 */
	public float getVy() {
		return velocityY;
	}
	
	public void setVelocity(float vx, float vy) {
		velocityX = vx;
		velocityY = vy;
	}
	
	/**
	 * Sets the immunity value of the player's shield
	 * @param bool The immunity value to set to
	 */
	public void setImmunity(boolean bool) {
		shieldImmune = bool;
	}
	
	public void setTexture(Texture texture){
		this.texture = texture;
		this.render();
	}
	
	/**
	 * Calculates the rotation of the ship based on the current position of the mouse.
	 */
	public void calculateRotation() {
		float x = this.getX();
		float y = this.getY();
		
		float mouseX = ((float)Mouse.getX() - 400) / (400.0f/26.0f);
		float mouseY = ((float)Mouse.getY() - 300) / (400.0f/23.0f);
		
		float b = mouseX - x;
		float a = mouseY - y;
		
		rotationZ = (float)Math.toDegrees(Math.atan2(a, b)) + 90;
		
		forwardX = (float) Math.sin(Math.toRadians(rotationZ));
		forwardY = (float) -Math.cos(Math.toRadians(rotationZ));
	}
	
	private void mouseToggle() {
		if (mouseToggle <= 0) {
			if (mouseMode) mouseMode = false;
			else mouseMode = true;
			mouseToggle = MOUSE_TOGGLE_INTERVAL;
		}
	}
	
	private void toggleTestMode() {
		if (testToggle <= 0) {
			if (testMode) testMode = false;
			else testMode = true;
		}
		testToggle = TEST_TOGGLE_INTERVAL;
	}
	
	private void wheelWeaponMode(int delta, EntityManager manager) {
		if (delta > 0) {
			if (firingMode == 5) firingMode = 0;
			else firingMode++;
		}
		else {
			if (firingMode == 0) firingMode = 5;
			else firingMode--;
		}
		changeGuns(firingMode, manager);
	}
}
