package com.sample.gsound.synthesis;

public class DelayA extends Filter {

	int inPoint_;
	int outPoint_;
	double delay_;
	double apInput_;
	double alpha_;
	double coeff_;
	
	double nextOutput_;
	boolean doNextOut_;

	public DelayA(double delay, int maxDelay) {
		if (delay >= 0.5 && delay <= maxDelay) {

			// Writing before reading allows delays from 0 to length-1.
			if (maxDelay + 1 > inputs_.length)
				inputs_ = new double[maxDelay + 1];
			inPoint_ = 0;
			this.setDelay(delay);
			apInput_ = 0.0;
			doNextOut_ = true;
		}
	}

	public void setMaximumDelay(long delay) {
		if (delay < inputs_.length)
			return;
		inputs_ = new double[(int) (delay + 1)];
	}

	public void setDelay(double delay) {
		int length = inputs_.length;

		if (delay < length && delay >= 0.5) {

			double outPointer = inPoint_ - delay + 1.0; // outPoint chases
														// inpoint
			delay_ = delay;

			while (outPointer < 0)
				outPointer += length; // modulo maximum length

			outPoint_ = (int) outPointer; // integer part
			if (outPoint_ >= length)
				outPoint_ = 0;
			alpha_ = 1.0 + outPoint_ - outPointer; // fractional part
            if ( alpha_ < 0.5 ) {
                outPoint_ += 1;
                if ( outPoint_ >= length )
                    outPoint_ -= length;
                alpha_ +=  1.0;
            }
			coeff_ = (1.0 - alpha_) / (1.0 + alpha_); // coefficient for allpass
		}
	}

	public double lastOut() {
		return lastFrame_;
	}

	public double tick(double input) {
        inPoint_++;
        if (inPoint_ >= inputs_.length)
            inPoint_ = 0;

		inputs_[inPoint_] = input * gain_;
		lastFrame_ = nextOut();
		doNextOut_ = true;
        outPoint_++;
        if (outPoint_ >= inputs_.length)
            outPoint_ = 0;
		apInput_ = inputs_[outPoint_];
		return lastFrame_;
	}

	public double nextOut() {
		if (doNextOut_) {
			// Do allpass interpolation delay.
			nextOutput_ = -coeff_ * lastFrame_;
			nextOutput_ += apInput_ + (coeff_ * inputs_[outPoint_]);
			doNextOut_ = false;
		}
		return nextOutput_;
	}
}
