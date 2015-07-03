package com.sample.gsound.synthesis;

import com.sample.gsound.StaticVariables;

public class Twang {

	DelayA delayLine_ = new DelayA(0.5, 4095);
	DelayL combDelay_ = new DelayL(0, 4095);
	Fir loopFilter_ = new Fir();

	double lastOutput_;
	double frequency_;
	double loopGain_;
	double pluckPosition_;

	public Twang(double lowestFrequency) {
		if (lowestFrequency > 0.0) {

			this.setLowestFrequency(lowestFrequency);
			double[] coefficients = new double[2];
			for (int i = 0; i < coefficients.length; i++) {
				coefficients[i] = 0.5;
			}
			loopFilter_.setCoefficients(coefficients);

			loopGain_ = 0.995;
			pluckPosition_ = 0.4;
			this.setFrequency(220.0);
		}
	}

	public double lastOut() {
		return lastOutput_;
	}

	public void setLowestFrequency(double frequency) {
		long nDelays = (long) (StaticVariables.SampleRate / frequency);
		delayLine_.setMaximumDelay(nDelays + 1);
		combDelay_.setMaximumDelay(nDelays + 1);
	}

	public void setFrequency(double frequency) {
			frequency_ = frequency;
			// Delay = length - filter delay.
			double delay = (StaticVariables.SampleRate / frequency)
					- loopFilter_.phaseDelay(frequency);
			delayLine_.setDelay(delay);

			this.setLoopGain(loopGain_);

			// Set the pluck position, which puts zeroes at position * length.
			combDelay_.setDelay(0.5 * pluckPosition_ * delay);
	}

	public void setLoopGain(double loopGain) {
		if (loopGain < 0.0 || loopGain >= 1.0) {

		} else {

			loopGain_ = loopGain;
			double gain = loopGain_ + (frequency_ * 0.000005);
			if (gain >= 1.0)
				gain = 0.99999;
			loopFilter_.setGain(gain);
		}
	}

	public double tick(double input) {
		lastOutput_ = delayLine_.tick(input
				+ loopFilter_.tick(delayLine_.lastOut()));
		lastOutput_ -= combDelay_.tick(lastOutput_); // comb filtering on output
		lastOutput_ *= 0.5;

		return lastOutput_;
	}
}
