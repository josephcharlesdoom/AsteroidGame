package org.newdawn.asteroids.particles;

import org.lwjgl.opengl.GL11;

/**
 * A very simple collection of particles that simply fade out. 
 * This isn't really much of an engine (hence the name group) since
 * it doesn't do much for the particle. It does however manage them.
 * 
 * @author Kevin Glass
 */
public class ParticleGroup {
	/** The positions of the particles being rendered */
	private float[][] pos;
	/** The life left in each particle being rendered */
	private int[] life;
	/** The initial size of each particle */
	private float[] initialSize;
	/** The current size of each particle */
	private float[] size;
	/** The alpha value of each particle */
	private float[] alpha;
	/** The index of the next particle to be used */
	private int next;
	/** The time particles should take to fade out */
	private int fadeOut;
	/** The red component of the colour of each particle */
	private float r;
	/** The green component of the colour of each particle */
	private float g;
	/** The blue component of the colour of each particle */
	private float b;
	
	/**
	 * Create a new group of particles 
	 * 
	 * @param count The number of particles to be rendered
	 * @param fadeOut The amount of time it takes for particles to fade
	 * @param r The red component of the colour of each particle
	 * @param g The green component of the colour of each particle
	 * @param b The blue component of the colour of each particle
	 */
	public ParticleGroup(int count, int fadeOut, float r, float g, float b) {
		pos = new float[count][2];
		life = new int[count];
		size = new float[count];
		initialSize = new float[count];
		alpha = new float[count];
		
		this.fadeOut = fadeOut;
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	/**
	 * Render the particles in the group
	 */
	public void render() {
		GL11.glBegin(GL11.GL_QUADS);
		for (int i=0;i<life.length;i++) {
			if (life[i] > 0) {
				float scalar = alpha[i] / 3;
				GL11.glColor4f(
					       (r * (1 - scalar)) + scalar,
					       (g * (1 - scalar)) + scalar,
						   (b * (1 - scalar)) + scalar,
						       alpha[i]);
				
				GL11.glTexCoord2f(0,0);
				GL11.glVertex3f(pos[i][0]-size[i],pos[i][1]-size[i],-0.3f);
				GL11.glTexCoord2f(1,0);
				GL11.glVertex3f(pos[i][0]+size[i],pos[i][1]-size[i],-0.3f);
				GL11.glTexCoord2f(1,1);
				GL11.glVertex3f(pos[i][0]+size[i],pos[i][1]+size[i],-0.3f);
				GL11.glTexCoord2f(0,1);
				GL11.glVertex3f(pos[i][0]-size[i],pos[i][1]+size[i],-0.3f);
			}
		}
		GL11.glEnd();
		
		GL11.glColor4f(1,1,1,1);
	}
	
	/**
	 * Update the particles state, i.e. fade them out over time
	 * 
	 * @param delta The amount of time thats passed since last 
	 * update
	 */
	public void update(int delta) {
		// cycle through every particle, aging it. It the particle
		// is still alive this frame then fade it and shrink it
		for (int i=0;i<life.length;i++) {
			if (life[i] >= 0) {
				life[i] -= delta;
				if (life[i] < fadeOut) {
					alpha[i] = life[i] / ((float) fadeOut);
					size[i] = (life[i] / ((float) fadeOut)) * initialSize[i];
				}
			}
		}
	}
	
	/**
	 * Add a new particle to the engine
	 * 
	 * @param x The x coordinate of the particle
	 * @param y The y coordinate of the particle
	 * @param size The initial size of the particle
	 * @param life The time the particle will last for (in milliseconds)
	 */
	public void addParticle(float x, float y, float size, int life) {
		pos[next][0] = x;
		pos[next][1] = y;
		this.size[next] = size;
		this.initialSize[next] = size;
		this.life[next] = life;
		alpha[next] = 1;
		
		next++;
		if (next >= this.life.length) {
			next = 0;
		}
	}
}
