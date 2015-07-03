package com.sample.gsound.synthesis;

import java.util.Random;

public class Noise {
	double lastFrame_;

	public Noise() {
		// Seed the random number generator
		this.setSeed(0);
	}

	public void setSeed(int seed) {
		/*
		 * if ( seed == 0 ) srand( (int) time( NULL ) ); else srand( seed );
		 */
	}

	public void tick(double[] frames) {
		int RAND_MAX = 0X7FFF;
		// int channel = 0;
		Random r = new Random();
		int index = 0;
		int hop = 1;
		for (int i = 0; i < frames.length / 2; i++, index += hop) {
            frames[index] = 2.0 * r.nextInt(RAND_MAX) / (RAND_MAX + 1.0) - 1.0;
            frames[frames.length - 1 - index] = frames[index];
            //frames[index] = 0.33 * Math.sin(2 * i * Math.PI / frames.length ) + 0.33 * Math.sin(4 * i * Math.PI / frames.length ) + 0.33 * Math.sin(8 * i * Math.PI / frames.length );
        }
		lastFrame_ = frames[index - hop];
	}
}
