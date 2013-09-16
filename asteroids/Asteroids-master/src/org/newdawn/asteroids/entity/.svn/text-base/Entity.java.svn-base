package org.newdawn.asteroids.entity;

/**
 * The interface describing the entities that appear in the game. This 
 * defines the contract between the elements building up the game (player,
 * rocks, shots) and the class containing them (i.e. the game)
 * 
 * @author Kevin Glass
 */
public interface Entity {
	/**
	 * Update the entitiy, cause it to do whatever it is that it 
	 * does. (rocks - move, player - check controls, etc.)
	 * 
	 * @param manager The manager responsible for maintaining the
	 * list of entities in the game. This gives us a hook back to be able
	 * to manipulate the game based on events that occurs in the during
	 * the update (i.e. if a shot is fired, play a sound effect)
	 * @param delta The amount of time in milliseconds that has passed
	 * since the last update, i.e. the amount of time the entity should
	 * be progressed.
	 */
	public void update(EntityManager manager, int delta);
	
	/**
	 * Render this entity. This causes the entity implementation to
	 * execute calls on OpenGL to produce the geometry for its visual
	 * represenation. 
	 * 
	 * This will be called every frame to render the scene.
	 */
	public void render();
	
	/**
	 * Get the size of this entity (the radius of its collision circle). 
	 * This is used by other entities when calculation collisions.
	 * 
	 * @return The size of this entity
	 */
	public float getSize();
	
	/**
	 * Get the current X coordinate of this entities position.
	 * 
	 * @return The current X coordinate of this entities position
	 */
	public float getX();

	/**
	 * Get the current Y coordinate of this entities position.
	 * 
	 * @return The current Y coordinate of this entities position
	 */
	public float getY();
	
	/**
	 * Notify this entity that it has collided with another entity. This
	 * is called once collision resolution has occured.
	 * 
	 * @see collides(Entity)
	 * @param manager A hook back to the class responsible for maintaining
	 * the list of entities within the game. This allows collision effects
	 * to change the entities in game (i.e. rock splitting into smaller 
	 * rocks)
	 * @param other The entity that this entity has collided with
	 */
	public void collide(EntityManager manager, Entity other);
	
	/**
	 * Check if this entity collides with another entity, i.e. do their
	 * collision circles intersect.
	 * 
	 * @see getSize()
	 * @param other The entity to check collision against
	 * @return True if collision is detected.
	 */
	public boolean collides(Entity other);
}
