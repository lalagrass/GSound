package com.sample.gsound.synthesis;

import com.sample.gsound.StaticVariables;

public class JCRev extends Effect {

	Delay[] allpassDelays_ = new Delay[3];
	Delay[] combDelays_ = new Delay[4];
	OnePole[] combFilters_ = new OnePole[4];
	Delay outLeftDelay_ = new Delay(0, 4095);
	Delay outRightDelay_ = new Delay(0, 4095);
	double allpassCoefficient_;
	double[] combCoefficient_ = new double[4];

	public JCRev(double T60) {
		if (T60 > 0) {

			// lastFrame_.resize(1, 2, 0.0);

			// Delay lengths for 44100 Hz sample rate.
			int[] lengths = { 1116, 1356, 1422, 1617, 225, 341, 441, 211, 179 };
			double scaler = StaticVariables.SampleRate / 44100.0;

			int delay, i;
			if (scaler != 1.0) {
				for (i = 0; i < 9; i++) {
					delay = (int) (scaler * lengths[i]);
					if ((delay & 1) == 0)
						delay++;
					while (!this.isPrime(delay))
						delay += 2;
					lengths[i] = delay;
				}
			}

			for (i = 0; i < 3; i++) {
				allpassDelays_[i] = new Delay(0, 4095);
				allpassDelays_[i].setMaximumDelay(lengths[i + 4]);
				allpassDelays_[i].setDelay(lengths[i + 4]);
			}

			for (i = 0; i < 4; i++) {
				combDelays_[i] = new Delay(0, 4095);
				combDelays_[i].setMaximumDelay(lengths[i]);
				combDelays_[i].setDelay(lengths[i]);
				combFilters_[i] = new OnePole(0.9);
				combFilters_[i].setPole(0.2);
			}

			this.setT60(T60);
			outLeftDelay_.setMaximumDelay(lengths[7]);
			outLeftDelay_.setDelay(lengths[7]);
			outRightDelay_.setMaximumDelay(lengths[8]);
			outRightDelay_.setDelay(lengths[8]);
			allpassCoefficient_ = 0.7;
			effectMix_ = 0.3;
			this.clear();
		}
	}

	public void setT60(double T60) {
		if (T60 > 0.0) {
			for (int i = 0; i < 4; i++)
				combCoefficient_[i] = Math.pow(10.0,
						(-3.0 * combDelays_[i].getDelay() / (T60 * StaticVariables.SampleRate)));
		}
	}

	void clear() {
		allpassDelays_[0].clear();
		allpassDelays_[1].clear();
		allpassDelays_[2].clear();
		combDelays_[0].clear();
		combDelays_[1].clear();
		combDelays_[2].clear();
		combDelays_[3].clear();
		outRightDelay_.clear();
		outLeftDelay_.clear();
		lastFrame_[0] = 0;
		lastFrame_[1] = 0;
	}

	public double tick(double input) {
		int channel = 0;
		double temp, temp0, temp1, temp2, temp3, temp4, temp5, temp6;
		double filtout;

		temp = allpassDelays_[0].lastOut();
		temp0 = allpassCoefficient_ * temp;
		temp0 += input;
		allpassDelays_[0].tick(temp0);
		temp0 = -(allpassCoefficient_ * temp0) + temp;

		temp = allpassDelays_[1].lastOut();
		temp1 = allpassCoefficient_ * temp;
		temp1 += temp0;
		allpassDelays_[1].tick(temp1);
		temp1 = -(allpassCoefficient_ * temp1) + temp;

		temp = allpassDelays_[2].lastOut();
		temp2 = allpassCoefficient_ * temp;
		temp2 += temp1;
		allpassDelays_[2].tick(temp2);
		temp2 = -(allpassCoefficient_ * temp2) + temp;

		temp3 = temp2
				+ (combFilters_[0].tick(combCoefficient_[0]
						* combDelays_[0].lastOut()));
		temp4 = temp2
				+ (combFilters_[1].tick(combCoefficient_[1]
						* combDelays_[1].lastOut()));
		temp5 = temp2
				+ (combFilters_[2].tick(combCoefficient_[2]
						* combDelays_[2].lastOut()));
		temp6 = temp2
				+ (combFilters_[3].tick(combCoefficient_[3]
						* combDelays_[3].lastOut()));

		combDelays_[0].tick(temp3);
		combDelays_[1].tick(temp4);
		combDelays_[2].tick(temp5);
		combDelays_[3].tick(temp6);

		filtout = temp3 + temp4 + temp5 + temp6;

		lastFrame_[0] = effectMix_ * (outLeftDelay_.tick(filtout));
		lastFrame_[1] = effectMix_ * (outRightDelay_.tick(filtout));
		temp = (1.0 - effectMix_) * input;
		lastFrame_[0] += temp;
		lastFrame_[1] += temp;

		return 0.7 * lastFrame_[channel];
	}
}
