package com.sample.gsound.synthesis;

import android.util.Log;

import com.sample.gsound.StaticVariables;


public class Filter {

	double gain_;
	int channelsIn_;
	double lastFrame_;
	double[] b_ = new double[0];
	double[] a_ = new double[0];
	double[] outputs_ = new double[1];
	double[] inputs_ = new double[1];

	public Filter() {
		gain_ = 1.0;
		channelsIn_ = 1;
	}

	public void setGain(double gain) {
		gain_ = gain;
	}

	public void clear() {
		int i;
		for (i = 0; i < inputs_.length; i++)
			inputs_[i] = 0;
		for (i = 0; i < outputs_.length; i++)
			outputs_[i] = 0;
		lastFrame_ = 0;
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
			Log.i(String.valueOf(a_.length), String.valueOf(b_.length));

			double phase = Math.atan2(imag, real);

			real = 0.0;
			imag = 0.0;
			for (int i = 0; i < a_.length; i++) {
				real += a_[i] * Math.cos(i * omegaT);
				imag -= a_[i] * Math.sin(i * omegaT);
			}
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
