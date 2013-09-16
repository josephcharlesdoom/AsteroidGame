package org.newdawn.asteroids.entity;

/**
 * An abstract implementation of an in game entity. This provides
 * the code for the common functionality between the majority of 
 * entities including movement, rotating and collision detection.
 * 
 * Note that even thought the game entites are actually 3D models and
 * the whole game is run in 3D the entities are only allowed to move
 * on a 2D plane giving that classic 2D asteroids look and feel.
 * 
 * @author Drew Murphy, Kevin Glass
 */
public abstract class AbstractEntity implements Entity {
	/** The width of the play area - determined by trying things until it felt right */
	private static final int PLAY_AREA_WIDTH = 56;
	/** Half the width of the player area */
	private static final int HALF_WIDTH = PLAY_AREA_WIDTH / 2;
	/** The height of the play area - determined by trying things until it felt right */
	private static final int PLAY_AREA_HEIGHT = 46;
	/** Half the height of the player area */
	private static final int HALF_HEIGHT = PLAY_AREA_HEIGHT / 2;
	
	/** The current rotation on the Z axis of this entity */
	protected float rotationZ;
	
	/** The x position of this entity */
	protected float positionX = 0;
	/** The y position of this entity */
	protected float positionY = 0;
	
	/** The X component of the velocity of this entity */
	protected float velocityX = 0;
	/** The y component of the velocity of this entity */
	protected float velocityY = 0;
	
	/**
	 * @see org.newdawn.asteroids.entity.Entity#update(org.newdawn.asteroids.entity.EntityManager, int)
	 */
	public void update(EntityManager manager, int delta) {
		// update the position of this entity based on its current
		// velocity. 
		positionX += (velocityX * delta) / 1000.0f;
		positionY += (velocityY * delta) / 1000.0f;
		
		// if we move off either side of the player area, then come back on
		// the other side. In asteroids all entities have this behaviour
		if (positionX < -HALF_WIDTH) {
			positionX = HALF_WIDTH - 1;
		}
		if (positionX > HALF_WIDTH) {
			positionX = -(HALF_WIDTH - 1);
		}
		// same again but for top and bottom this time
		if (positionY < -HALF_HEIGHT) {
			positionY = HALF_HEIGHT - 1;
		}
		if (positionY > HALF_HEIGHT) {
			positionY = -(HALF_HEIGHT - 1);
		}
	}
	
	/**
	 * @see org.newdawn.asteroids.entity.Entity#getX()
	 */
	public float getX() {
		return positionX;
	}
	
	/**
	 * @see org.newdawn.asteroids.entity.Entity#getY()
	 */
	public float getY() {
		return positionY;
	}
	
	/**
	 * @see org.newdawn.asteroids.entity.Entity#collides(org.newdawn.asteroids.entity.Entity)
	 */
	public boolean collides(Entity other) {
		// We're going to use simple circle collision here since we're 
		// only worried about 2D collision.
		//
		// Normal math tells us that if the distance between the two
		// centres of the circles is less than the sum of their radius
		// then they collide. However, working out the distance between
		// the two would require a square root (Math.sqrt((dx*dx)+(dy*dy))
		// which could be quite slow.
		//
		// Instead we're going to square the sum of their radius and compare
		// that against the un-rooted value. This is equivilent but 
		// much faster
		
		// Get the size of the other entity and combine it with our
		// own, giving the range of collision. Square this so we can
		// compare it against the current distance.
		float otherSize = other.getSize();
		float range = (otherSize + getSize());
		range *= range;

		// Get the distance on X and Y between the two entities, then
		// find the squared distance between the two.
		float dx = getX() - other.getX();
		float dy = getY() - other.getY();
		float distance = (dx*dx)+(dy*dy);
		
		// if the squared distance is less than the squared range
		// then we've had a collision!
		return (distance <= range);
	}
}
