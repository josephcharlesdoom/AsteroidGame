package org.newdawn.asteroids.sound;

/**
 * A sound that can be played through OpenAL
 * 
 * @author Drew Murphy, Kevin Glass
 */
public class Sound {
	/** The store from which this sound was loaded */
	private SoundLoader store;
	/** The buffer containing the sound */
	private int buffer;
	
	/**
	 * Create a new sound
	 * 
	 * @param store The sound store from which the sound was created
	 * @param buffer The buffer containing the sound data
	 */
	Sound(SoundLoader store, int buffer) {
		this.store = store;
		this.buffer = buffer;
	}
	
	/**
	 * Play this sound as a sound effect
	 * 
	 * @param pitch The pitch of the play back
	 * @param gain The gain of the play back
	 * @param loop The looping value of the playback
	 */
	public void play(float pitch, float gain, boolean loop) {
		store.playAsSound(buffer, pitch, gain, loop);
	}
	
	/**
	 * Stops all sounds being played
	 */
	public void stop(){
		store.stop();
	}
}
