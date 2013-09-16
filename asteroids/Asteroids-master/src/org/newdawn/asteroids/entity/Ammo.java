package org.newdawn.asteroids.entity;

import org.lwjgl.opengl.GL11;
import org.newdawn.asteroids.model.ObjModel;
import org.newdawn.spaceinvaders.lwjgl.Texture;

/**
 * The Ammo class represents an entity the player can pickup to gain ammo for the various
 * weapons. Called randomly when a rock is destroyed. When a player flies over an ammo drop,
 * they will gain ammunition for the weapon corresponding to that ammo type.
 * 
 * @author Drew Murphy
 *
 */
public class Ammo extends AbstractEntity {
	/**The texture to be applied to the model*/
	private Texture texture;
	/**The model to be rendered*/
	private ObjModel model;
	/**The firingMode of the ammo drop*/
	private int firingMode;
	/**The amount of ammo gained by picking up a drop*/
	private int ammoDelta;
	/**The size of the ammo drop*/
	private int size;
	/**The rotation speed of the ammo*/
	private float rotateSpeed = (float) (Math.random() * 0.5f) + 1;
	/**The length of time that the ammo stays active */
	private final int LIFE = 20000;
	/**The length of time remaining */
	private int lifeRemaining = LIFE;
	
	/**
	 * Create an ammo drop at a specified location with a random velocity. Done randomly when
	 * a rock is destroyed.
	 * 
	 * @param texture The texture to apply to the ammo drop
	 * @param firingMode The current firing mode
	 * @param model The model to be rendered for the drop
	 * @param x The x position of the ammo drop
	 * @param y The y position of the ammo drop
	 * @param size The size of the ammo drop 
	 */
	public Ammo(Texture texture, int firingMode, ObjModel model, float x, float y, int size) {
		
		this.texture = texture;
		this.model = model;
		this.firingMode = firingMode;
		
		velocityX = (float) (-4 + (Math.random() * 8));
		velocityY = (float) (-4 + (Math.random() * 8));
		positionX = x;
		positionY = y;

		this.size = size;
		
		switch(firingMode) {
		case 1:
			ammoDelta = 8;
			break;
		case 2:
			ammoDelta = 1;
			break;
		case 3:
			ammoDelta = 3;
			break;
		case 4:
			ammoDelta = 25;
			break;
		case 5:
			ammoDelta = 4;
			break;
		}
	}

	/**
	 * @see org.newdawn.asteroids.entity.Entity#update(org.newdawn.asteroids.entity.EntityManager, int)
	 */
	public void update(EntityManager manager, int delta) {
		// call the abstract entity's update method to cause the
		// rock to move based on its current settings
		lifeRemaining -= delta;
		super.update(manager, delta);
		
		// the rocks just spin round all the time, so adjust
		// the rotation of the rock based on the amount of time
		// that has passed
		rotationZ += (delta / 10.0f) * rotateSpeed;
		
		if (lifeRemaining <= 0) manager.removeEntity(this);
	}
	
	/**
	 * @see org.newdawn.asteroids.entity.Entity#render()
	 */
	public void render() {
		// enable lighting over the rock model
		GL11.glEnable(GL11.GL_LIGHTING);
				
		// store the original matrix setup so we can modify it
		// without worrying about effecting things outside of this 
		// class
		GL11.glPushMatrix();

		// position the model based on the players currently game
		// location
		GL11.glTranslatef(positionX,positionY,0);

		// rotate the rock round to its current Z axis rotate
		GL11.glRotatef(rotationZ,rotationZ,rotationZ,1);
		
		// scale the model based on the size of rock we're representing
		GL11.glScalef(0.015f, 0.015f, 0.015f);
		
		// bind the texture we want to apply to our rock and then
		// draw the model 
		texture.bind();
		model.render();
		
		// restore the model matrix so we leave this method
		// in the same state we entered
		GL11.glPopMatrix();
		
	}

	/**
	 * @see org.newdawn.asteroids.Entity#getSize()
	 */
	public float getSize() {
		return size;
	}

	/**
	 * @see org.newdawn.asteroids.entity.Entity#collide(org.newdawn.asteroids.entity.EntityManager, org.newdawn.asteroids.entity.Entity)
	 */
	public void collide(EntityManager manager, Entity other) {
		if (other instanceof Player) {
			manager.removeEntity(this);
			manager.updateAmmo(firingMode, ammoDelta, true);
		}
		
	}
	
	/**
	 * Accessor method for the current type of ammo
	 * @return returns the firingMode of the Ammo being picked up
	 */
	public int getGunMode() {
		return firingMode;
	}

}
