package com.sample.gsound.synthesis;

public class OnePole extends Filter {
	public OnePole(double thePole) {
		b_ = new double[1];
		a_ = new double[2];
		a_[0] = 1.0;
		outputs_ = new double[2];
		this.setPole(thePole);
	}

	public double tick(double input) {
		inputs_[0] = gain_ * input;
		lastFrame_ = b_[0] * inputs_[0] - a_[1] * outputs_[1];
		outputs_[1] = lastFrame_;

		return lastFrame_;
	}

	public void tick(double[] frames) {
		int channel = 0;
		int samples = 0;
		int hop = 1;
		for (int i = 0; i < frames.length; i++, samples += hop) {
			inputs_[0] = gain_ * frames[samples];
			frames[samples] = b_[0] * inputs_[0] - a_[1]
					* outputs_[1];
			outputs_[1] = frames[samples];
		}
		lastFrame_ = outputs_[1];
	}

	public void setPole(double thePole) {
		if (Math.abs(thePole) < 1.0) {

			// Normalize coefficients for peak unity gain.
			if (thePole > 0.0)
				b_[0] = (double) (1.0 - thePole);
			else
				b_[0] = (double) (1.0 + thePole);

			a_[1] = -thePole;
		}
	}
}