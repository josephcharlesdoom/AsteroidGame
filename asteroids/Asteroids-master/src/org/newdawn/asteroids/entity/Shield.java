package org.newdawn.asteroids.entity;

import org.lwjgl.opengl.GL11;
import org.newdawn.asteroids.particles.ParticleGroup;
import org.newdawn.spaceinvaders.lwjgl.Texture;

/**
 * The Shield class represents the shield object the player can equip. As it has different functionality
 * than the Shot class, it was necessary to implement a separate object for the shield.
 * @author Drew Murphy
 */
public class Shield extends AbstractEntity implements Entity {
	/** The texture to be applied to the shield */
	private Texture texture;
	/** The size of the shield */
	private float size = 4f;
	/** The duration of the shield */
	private int life = 0;
	/** The particle group making up the shield */
	private ParticleGroup particles;
	/** The Player object that the shield surrounds */
	private Player player;
	
	/**
	 * Create a new shot at a specified location and with a specified
	 * velocity.
	 * 
	 * @param texture The texture to apply to the particles building
	 * up shot.
	 */
	public Shield(Texture texture, Player player, float x, float y, int life,
		    float size, float r, float g, float b) {
		
		player.setImmunity(true);
		positionX = x;
		positionY = y;
		this.texture = texture;
		this.player = player;
		
		setVelocity(player.getVx(), player.getVy());
		
		this.life = life;
		this.size = size;
	
		particles = new ParticleGroup(250,250,r,g,b);
	}
	
	/**
	 * @see org.newdawn.asteroids.entity.Entity#update(org.newdawn.asteroids.entity.EntityManager, int)
	 */
	public void update(EntityManager manager, int delta) {
		super.update(manager, delta);
		
		life -= delta;
		manager.setShieldDuration(String.format("%.2f", ((float)life/(float)1000)));
		if (life < 0) {
			player.setImmunity(false);
			manager.removeEntity(this);
			manager.setShieldDuration("");
			manager.shieldDown();
		} else {
			setVelocity(player.getVx(), player.getVy());
			particles.addParticle(getX(), getY(), size, 200);
			particles.update(delta);
		}
	}
	
	/**
	 * @see org.newdawn.asteroids.entity.Entity#render()
	 */
	public void render() {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		
		texture.bind();
		particles.render();
		
		GL11.glDisable(GL11.GL_BLEND);
	}

	/**
	 * @see org.newdawn.asteroids.entity.Entity#getSize()
	 */
	public float getSize() {
		// the size of our shot
		return size;
	}

	/**
	 * @see org.newdawn.asteroids.entity.Entity#collide(org.newdawn.asteroids.entity.EntityManager, org.newdawn.asteroids.entity.Entity)
	 */
	public void collide(EntityManager manager, Entity other) {
		// if the shot hits a rock then we've scored! The rock
		// needs to split apart and then this shot has been used up 
		// so remove it.
		if (other instanceof Rock) {
			((Rock) other).split(manager, this);
			life = life - 1000;
		}
	}
	
	/**
	 * Sets the shield velocity, so that it stays around the ship
	 * @param vx The x component of the velocity
	 * @param vy The y component of the velocity
	 */
	public void setVelocity(float vx, float vy) {
		velocityX = vx;
		velocityY = vy;
	}
	
	/**
	 * @return Returns the remaining duration of the shield
	 */
	public float getDuration() {
		return life;
	}

}