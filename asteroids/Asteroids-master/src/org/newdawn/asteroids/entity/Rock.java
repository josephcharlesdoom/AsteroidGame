package org.newdawn.asteroids.entity;

import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.newdawn.asteroids.model.ObjModel;
import org.newdawn.spaceinvaders.lwjgl.Texture;

/**
 * The entity representing a rock floating round in the game play
 * area. Rocks move around until they hit something. They then either
 * change direction (if they hit another rock), split into smaller
 * rocks (if they're big enough) or disappear (if they're small) 
 * 
 * @author Drew Murphy, Kevin Glass
 */
public class Rock extends AbstractEntity {
	private Texture texture;
	private ObjModel model;
	private int size;
	private float rotateSpeed = (float) (Math.random() * 0.5f) + 1;
	private final float AMMO_SPAWN_CHANCE = 0.10f;
	
	/**
	 * Create a rock at a specifed location with a random velocity. This
	 * should be used when spawning rocks at the begining of the game.
	 * 
	 * @param texture The texture to apply to the rock
	 * @param model The model to be rendered for the rock
	 * @param x The initial x position of the rock
	 * @param y The initial y position of the rock 
	 * @param size The size of the rock (3 - big rock, 2 - medium , 1 - small)
	 */
	public Rock(Texture texture, ObjModel model, float x, float y, int size) {
		// we're simply going to call the other constructor with some
		// random values for the velocity
		this(texture, model, x, y, size, 
				(float) (-4 + (Math.random() * 8)), 
				(float) (-4 + (Math.random() * 8)));
	}
	
	/**
	 * Create a rock at a specified location with a given velocity. This
	 * should be used when rocks as an effect of splitting another
	 * rock.
	 * 
	 * @param texture The texture to apply to the rock model
	 * @param model The model to be rendered for this rock
	 * @param x The initial x position of this rock
	 * @param y The initial y position of the rock 
	 * @param size The size of the rock (3 - big rock, 2 - medium , 1 - small)
	 * @param vx The x component of the initial velocity
	 * @param vy The y component of the initial velocity
	 */
	public Rock(Texture texture, ObjModel model, float x, float y, int size, float vx, float vy) {
		this.texture = texture;
		this.model = model;
		
		velocityX = vx;
		velocityY = vy;
		positionX = x;
		positionY = y;

		this.size = size;
	}

	/**
	 * @see org.newdawn.asteroids.entity.Entity#update(org.newdawn.asteroids.entity.EntityManager, int)
	 */
	public void update(EntityManager manager, int delta) {
		super.update(manager, delta);
		rotationZ += (delta / 10.0f) * rotateSpeed;
	}

	/**
	 * @see org.newdawn.asteroids.entity.Entity#render()
	 */
	public void render() {
		GL11.glEnable(GL11.GL_LIGHTING);
		
		GL11.glPushMatrix();

		GL11.glTranslatef(positionX,positionY,0);
		GL11.glRotatef(rotationZ,0,0,1);
		GL11.glScalef(size, size, size);
		
		texture.bind();
		model.render();
		
		GL11.glPopMatrix();
	}

	/**
	 * @see org.newdawn.asteroids.Entity#getSize()
	 */
	public float getSize() {
		return size * 0.5f;
	}

	/**
	 * Cause this rock to split apart into two smaller rocks
	 * or to disappear if its too small to spliy
	 * 
	 * @param manager The callback to the class managing the list
	 * of entities in the game
	 * @param reason The entity which was the reason for this split to
	 * occur (either the player's ship or a shot hitting the rock)
	 */
	void split(EntityManager manager, Entity reason) {
		manager.removeEntity(this);
		manager.rockDestroyed(size);
		
		if (size > 1) {
			float dx = getX() - reason.getX();
			float dy = getY() - reason.getY();
			
			dx *= (size * 0.2f);
			dy *= (size * 0.2f);
			
			float speed = 2;
			
			Rock rock1 = new Rock(texture, model, 
					getX() + dy, getY() - dx, size - 1, dy * speed, -dx * speed);
			Rock rock2 = new Rock(texture, model,
					getX() - dy, getY() + dx, size - 1, -dy * speed, dx * speed);
			
			manager.addEntity(rock1);
			manager.addEntity(rock2);
				
		}
		else {
			Random r = new Random();
			float f = r.nextFloat();
			if (f < AMMO_SPAWN_CHANCE && manager.readyToSpawn()) manager.spawnAmmo(getX(), getY());
		}
	}
	
	/**
	 * @see org.newdawn.asteroids.entity.Entity#collide(org.newdawn.asteroids.entity.EntityManager, org.newdawn.asteroids.entity.Entity)
	 */
	public void collide(EntityManager manager, Entity other) {
		// if anything collides with a rock its direction must change 
		// (to prevent rocks intersecting with each other). For effect
		// we'll also change the direction of rotation
		velocityX = (getX() - other.getX());
		velocityY = (getY() - other.getY());
		
		rotateSpeed = -rotateSpeed;
	}
}
