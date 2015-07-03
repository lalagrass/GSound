package com.sample.gsound.synthesis;

public class DelayL extends Filter {

	int inPoint_;
	int outPoint_;
	double delay_;
	double alpha_;
	double omAlpha_;
	double nextOutput_;
	boolean doNextOut_;

	public DelayL(double delay, long maxDelay) {
		if (delay > 0 && delay <= maxDelay) {
			// Writing before reading allows delays from 0 to length-1.
			if (maxDelay + 1 > inputs_.length)
				inputs_ = new double[(int) (maxDelay + 1)];

			inPoint_ = 0;
			this.setDelay(delay);
			doNextOut_ = true;
		}
	}

	public void setMaximumDelay(long delay) {
		if (delay < inputs_.length)
			return;
		inputs_ = new double[(int) (delay + 1)];
	}

	public void setDelay(double delay) {
		if (delay >= 0 && delay < inputs_.length) {

			double outPointer = inPoint_ - delay; // read chases write
			delay_ = delay;

			while (outPointer < 0)
				outPointer += inputs_.length; // modulo maximum length

			outPoint_ = (int) outPointer; // integer part
			if (outPoint_ >= inputs_.length)
				outPoint_ = 0;
			alpha_ = outPointer - outPoint_; // fractional part
			omAlpha_ = 1.0 - alpha_;
		}
	}

	public double tick(double input) {
        inPoint_++;
        if (inPoint_ >= inputs_.length)
            inPoint_ = 0;
		inputs_[inPoint_]= input * gain_;
		lastFrame_= nextOut();
		doNextOut_ = true;

		// Increment output pointer modulo length.
		if (++outPoint_ >= inputs_.length)
			outPoint_ = 0;

		return lastFrame_;
	}

	public double nextOut() {
		if (doNextOut_) {
			// First 1/2 of interpolation
			nextOutput_ = inputs_[outPoint_] * omAlpha_;
			// Second 1/2 of interpolation
			if (outPoint_ + 1 < inputs_.length)
				nextOutput_ += inputs_[outPoint_ + 1] * alpha_;
			else
				nextOutput_ += inputs_[0] * alpha_;
			doNextOut_ = false;
		}
		return nextOutput_;
	}
}
