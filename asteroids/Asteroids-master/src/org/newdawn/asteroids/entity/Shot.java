package org.newdawn.asteroids.entity;

import org.lwjgl.opengl.GL11;
import org.newdawn.asteroids.particles.ParticleGroup;
import org.newdawn.spaceinvaders.lwjgl.Texture;

/**
 * An entity representing a single shot fired by the player
 * 
 * @author Drew Murphy, Kevin Glass
 */
public class Shot extends AbstractEntity implements Entity {
	/** The Texture to be applied to the Shot */
	private Texture texture;
	/** The size of the shot */
	private float size = 0.0f;
	/** The amount of time the shot exists (in ms) */
	private int life = 0;
	/** The amount of time each particle exists before it fades */
	/** The particle group making up the shot */
	private ParticleGroup particles;
	/** determines if the shot is destroyed upon collision with a rock */
	private boolean destroyable;
	/** determines if the shot branches */
	private boolean split;
	
	/**
	 * Create a new shot at a specified location and with a specified
	 * velocity.
	 * 
	 * @param texture The texture to apply to the particles building
	 * up shot.
	 * @param x The initial x position of the shot
	 * @param y The initial y position of the shot
	 * @param vx The x component of the initial velocity of the shot
	 * @param vy The y component of the initial velocity of the shot
	 * @param life The time the shot is active
	 * @param size The size of the shot
	 * @param r The red value
	 * @param g The green value
	 * @param b The blue value
	 * @param destroyable Determines if the shot is destroyed upon collision
	 * @param particleLife The length of time each particle is active
	 */
	public Shot(Texture texture, float x, float y, float vx, float vy, int life,
			    float size, float r, float g, float b,
			    boolean destroyable, int particleLife, boolean split) {
		positionX = x;
		positionY = y;
		velocityX = vx;
		velocityY = vy;
		this.texture = texture;
		
		this.life = life;
		this.size = size;
		this.destroyable = destroyable;
		this.split = split;
	
		particles = new ParticleGroup(100,particleLife,r,g,b);
	}
	
	/**
	 * @see org.newdawn.asteroids.entity.Entity#update(org.newdawn.asteroids.entity.EntityManager, int)
	 */
	public void update(EntityManager manager, int delta) {
		super.update(manager, delta);
		life -= delta;
		if (life < 0) {
			manager.removeEntity(this);
		} else {
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
		return size;
	}

	/**
	 * @see org.newdawn.asteroids.entity.Entity#collide(org.newdawn.asteroids.entity.EntityManager, org.newdawn.asteroids.entity.Entity)
	 */
	public void collide(EntityManager manager, Entity other) {
		if (other instanceof Rock) {
			manager.successfulShot();
			((Rock) other).split(manager, this);
			if (split && size > 0.5f) this.split(manager);
			if (destroyable) manager.removeEntity(this);
		}
	}
	
	public void split(EntityManager manager) {
		
		float rotation = (float)Math.toDegrees(Math.atan2(this.velocityY,this.velocityX)) + 90;
		
		float rotationL = rotation - 15;
		float rotationR = rotation + 15;
		
		float vxl = (float) Math.sin(Math.toRadians(rotationL));
		float vyl = (float) -Math.cos(Math.toRadians(rotationL));
		
		float vxr = (float) Math.sin(Math.toRadians(rotationR));
		float vyr = (float) -Math.cos(Math.toRadians(rotationR));
		
		Shot s1= new Shot(texture, 
				 getX(), 
				 getY(), 
				 vxl * 100, 
				 vyl * 100,
				 life, (size/1.5f),  0, 0, 1, true, 350, true);
		
		Shot s2= new Shot(texture, 
				 getX(), 
				 getY(), 
				 vxr * 100, 
				 vyr * 100,
				 life, (size/1.5f),  0, 0, 1, true, 350, true);
		
		manager.addEntity(s1);
		manager.addEntity(s2);
	}

}
