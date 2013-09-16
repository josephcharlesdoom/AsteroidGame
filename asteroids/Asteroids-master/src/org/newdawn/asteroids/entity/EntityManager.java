package org.newdawn.asteroids.entity;

/**
 * The description of the class holding and maintaining the list of 
 * entities within the game. This interface forms the contract between
 * the entities being held in the game and their container. It provides
 * the entity logic a callback to the game code in a non-coupled manner.
 * 
 * @author Drew Murphy, Kevin Glass
 */
public interface EntityManager {
	/**
	 * Remove an entity form the game (i.e. remove a rock when its
	 * destroyed)
	 * 
	 * @param entity The entity to be removed
	 */
	public void removeEntity(Entity entity);
	
	/**
	 * Add an entity to the game (i.e. add a shot when the player fires)
	 * 
	 * @param entity The entity to be added.
	 */
	public void addEntity(Entity entity);
	
	/**
	 * Notification that a rock has been destroyed
	 * 
	 * @param size The size of the rock that was destroyed
	 */
	public void rockDestroyed(int size);
	
	/**
	 * Notification that the player was hit by a rock
	 */
	public void playerHit();
	
	/**
	 * Notification that the player fired a shot
	 */
	public void shotFired(int mode);
	
	/**
	 * Update the current amount of ammo the player has. If the player is picking up
	 * an ammo drop, pickup will be true.
	 * 
	 * @param mode The type of ammo being updated
	 * @param delta The amount to increase/decrease
	 * @param pickup True if ammo pickup, false if firing
	 */
	public void updateAmmo(int mode, int delta, boolean pickup);
	
	/**
	 * Returns amount of ammo for given weapon
	 * 
	 * @param mode 1=shotgun, 2=shield, 3=laser
	 */
	public int getAmmo(int mode);
	
	/**
	 * Updates the String value of the ammo type for the GUI
	 * 
	 * @param mode The type of ammo being used
	 */
	public void setAmmoType(int mode);
	
	/**
	 * Updates the shield duration
	 * 
	 * @param duration Updates the GUI values of the shield
	 */
	public void setShieldDuration(String duration);
	
	/**
	 * Notification that the player hit a rock with a shot
	 */
	public void successfulShot();
	
	/**
	 * Spawns a random ammo pickup
	 * 
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 */
	public void spawnAmmo(float x, float y);
	
	/**
	 * Plays the out of ammo sound.
	 */
	public void outOfAmmo();
	
	/**
	 * @return Returns the spawn status of the ammo lockout 
	 */
	public boolean readyToSpawn();
	
	/**
	 * Notification that the shield's duration is up
	 */
	public void shieldDown();
}
