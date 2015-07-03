package com.sample.gsound.synthesis;

public class Effect {

	double[] lastFrame_ = new double[2];
	double effectMix_;

	public Effect() {
	}

	public boolean isPrime(int number) {

		if (number == 2)
			return true;
		if ((number & 1) != 0) {
			for (int i = 3; i < (int) Math.sqrt((double) number) + 1; i += 2)
				if ((number % i) == 0)
					return false;
			return true; // prime
		} else
			return false; // even
	}

	public void setEffectMix(double mix) {
		if (mix < 0.0) {
			effectMix_ = 0.0;
		} else if (mix > 1.0) {
			effectMix_ = 1.0;
		} else
			effectMix_ = mix;
	}
}
