package com.sample.gsound.synthesis;

public class OneZero extends Filter {
	public OneZero(double theZero) {
		b_ = new double[2];
        inputs_ = new double[2];
        setZero(theZero);
	}

	public double tick(double input) {
		inputs_[0] = gain_ * input;
		lastFrame_ = b_[1] * inputs_[1] + b_[0] * inputs_[0];
        inputs_[1] = inputs_[0];
		return lastFrame_;
	}

	public void tick(double[] frames) {
		int channel = 0;
		int samples = 0;
		int hop = 1;
		for (int i = 0; i < frames.length; i++, samples += hop) {
			inputs_[0] = gain_ * frames[samples];
			frames[samples] = b_[1] * inputs_[1] + b_[0] * inputs_[0];
            inputs_[1] = inputs_[0];
		}
		lastFrame_ = frames[frames.length - 1];
	}

    public void setZero(double theZero) {
        if ( theZero > 0.0 )
            b_[0] = 1.0 / ( 1.0 + theZero);
        else
            b_[0] = 1.0 / ( 1.0 - theZero);
        b_[1] = -theZero * b_[0];
    }
}