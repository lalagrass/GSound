package com.sample.gsound.synthesis;

public class Delay extends Filter {

	long inPoint_;
	long outPoint_;
	long delay_;

	public Delay(long delay, long maxDelay) {
		// Writing before reading allows delays from 0 to length-1.
		// If we want to allow a delay of maxDelay, we need a
		// delay-line of length = maxDelay+1.
		if (delay <= maxDelay) {

			if ((maxDelay + 1) > inputs_.length)
				inputs_ = new double[(int) (maxDelay + 1)];

			inPoint_ = 0;
			this.setDelay(delay);
		}
	}

	public double lastOut() {
		return lastFrame_;
	};

	public double tick(double input) {
        inPoint_++;
        if (inPoint_ >= inputs_.length)
            inPoint_ = 0;
        outPoint_++;
        if (outPoint_ >= inputs_.length)
            outPoint_ = 0;
        inputs_[(int) inPoint_] = input * gain_;
		lastFrame_ = inputs_[(int) outPoint_];
		return lastFrame_;
	}

	public void setMaximumDelay(long delay) {
		if (delay < inputs_.length)
			return;
		inputs_ = new double[(int) (delay + 1)];
	}

	public void setDelay(long delay) {
		if (delay <= inputs_.length - 1) {
			// read chases write
			if (inPoint_ >= delay)
				outPoint_ = inPoint_ - delay;
			else
				outPoint_ = inputs_.length + inPoint_ - delay;
			delay_ = delay;
		}
	}

	public long getDelay() {
		return delay_;
	}
}
