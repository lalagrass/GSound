package com.sample.gsound.synthesis;

import com.sample.gsound.StaticVariables;

public class Fir {
	double[] b_;
	double gain_;
	double[] inputs_;

	// double[] outputs_;

	public Fir() {
		b_ = new double[1];
		b_[0] = 1;
		gain_ = 1;
	}

	public double tick(double input) {
		inputs_[0] = gain_ * input;
		double lastFrame_ = b_[1] * inputs_[1] + b_[0] * inputs_[0];
		inputs_[1] = inputs_[0];
		return lastFrame_;
	}

	public void setCoefficients(double[] coefficients) {
		if (coefficients.length == 0) {
		} else {
			if (b_.length != coefficients.length) {
				b_ = coefficients;
				inputs_ = new double[b_.length];
			} else {
				for (int i = 0; i < b_.length; i++)
					b_[i] = coefficients[i];
			}
		}
	}

	public void setGain(double gain) {
		gain_ = gain;
	}

	public double phaseDelay(double frequency) {
		if (frequency > 0 && frequency <= 0.5 * StaticVariables.SampleRate) {

			double omegaT = 2 * Math.PI * frequency / StaticVariables.SampleRate;
			double real = 0.0, imag = 0.0;
			for (int i = 0; i < b_.length; i++) {
				real += b_[i] * Math.cos(i * omegaT);
				imag -= b_[i] * Math.sin(i * omegaT);
			}
			real *= gain_;
			imag *= gain_;
			// Log.i(String.valueOf(a_.length), String.valueOf(b_.length));

			double phase = Math.atan2(imag, real);

			real = 0.0;
			imag = 0.0;

			phase -= Math.atan2(imag, real);
			phase = -phase;
			while (phase < 0) {
				phase += 2 * Math.PI;
			}
			while (phase > Math.PI * 2) {
				phase -= 2 * Math.PI;
			}
			// phase = Math..fmod(-phase, 2 * Math.PI);
			// Log.i("phase", "phase");
			double d = phase / omegaT;
			return d;
		} else {
			// Log.i("phase", "err");
			return 0;
		}
	}
}
