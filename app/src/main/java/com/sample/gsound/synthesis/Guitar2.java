package com.sample.gsound.synthesis;

import com.sample.gsound.StaticVariables;

public class Guitar2 {

	Twang[] strings_;
	int[] stringState_; // 0 = off, 1 = decaying, 2 = on
	int[] decayCounter_;
	int[] filePointer_;
	double[] pluckGains_;

	OnePole pickFilter_ = new OnePole(0.9);
	OnePole couplingFilter_ = new OnePole(0.9);
	double couplingGain_;
	double[] excitation_ = new double[1];
	double lastFrame_;
	double BASE_COUPLING_GAIN = 0.01;

    private double _gain = 0.993;

	public Guitar2(int nStrings) {
		String bodyfile = "";
		strings_ = new Twang[nStrings];
		for (int i = 0; i < strings_.length; i++) {
			strings_[i] = new Twang(50.0);
		}
		stringState_ = new int[nStrings];
		decayCounter_ = new int[nStrings];
		filePointer_ = new int[nStrings];
		pluckGains_ = new double[nStrings];

		setBodyFile(bodyfile);

		couplingGain_ = BASE_COUPLING_GAIN;
		couplingFilter_.setPole(0.9);
		pickFilter_.setPole(0.95);
	}

    public void SetStringGain(double gain) {
        _gain = gain;
    }

    public void PalmMute(double amplitude, int string, double f) {
        this.setFrequency(f, string);
        stringState_[string] = 2;
        filePointer_[string] = 0;
        strings_[string].setLoopGain(_gain * 9 / 10);
        pluckGains_[string] = amplitude * 1 / 3;
    }

	public void noteOn(double frequency, double amplitude, int string) {
		this.setFrequency(frequency, string);
		stringState_[string] = 2;
		filePointer_[string] = 0;
        strings_[string].setLoopGain(_gain);
		pluckGains_[string] = amplitude;
	}

	public void noteOff(int string) {
		strings_[string].setLoopGain(0.1);
		stringState_[string] = 1;
	}

	public void setFrequency(double frequency, int string) {
		strings_[string].setFrequency(frequency);
	}

	public void setBodyFile(String bodyfile) {
		boolean fileLoaded = false;
		if (bodyfile != "") {
			// try {
			// FileWvIn file( bodyfile );

			// Fill the StkFrames variable with the (possibly interpolated)
			// file data.
			// excitation_.resize( (unsigned long) ( 0.5 + ( file.getSize() *
			// Stk::sampleRate() / file.getFileRate() ) ) );
			// file.tick( excitation_ );
			// fileLoaded = true;
			// }
			// catch ( StkError &error ) {
			// oStream_ << "Guitar::setBodyFile: file error (" <<
			// error.getMessage() << ") ... using noise excitation.";
			// handleError( StkError::WARNING );
			// }
		}

		if (!fileLoaded) {
			int M = 200; // arbitrary value
			excitation_ = new double[M];
			Noise noise = new Noise();
			noise.tick(excitation_);
			// Smooth the start and end of the noise.
			int N = (int) (M * 0.2); // arbitrary value
			for (int n = 0; n < N; n++) {
				double weight = 0.5 * (1.0 - Math.cos(n * Math.PI / (N - 1)));
				excitation_[n] *= weight;
				excitation_[M - n - 1] *= weight;
			}
		}

		// Filter the excitation to simulate pick hardness
		pickFilter_.tick(excitation_);

		// Compute file mean and remove (to avoid DC bias).
		double mean = 0.0;
		for (int i = 0; i < excitation_.length; i++)
			mean += excitation_[i];
		mean /= excitation_.length;

		for (int i = 0; i < excitation_.length; i++)
			excitation_[i] -= mean;

		// Reset all the file pointers.
		for (int i = 0; i < strings_.length; i++)
			filePointer_[i] = 0;
	}

	public double tick(double input) {
		double temp, output = 0.0;
		lastFrame_ /= strings_.length;
		for (int i = 0; i < strings_.length; i++) {
			if (stringState_[i] != 0) {
				temp = input;
				// If pluckGain < 0.2, let string ring but don't pluck it.
				if (filePointer_[i] < excitation_.length
						&& pluckGains_[i] > 0.2)
					temp += pluckGains_[i] * excitation_[filePointer_[i]++];
				temp += couplingGain_ * couplingFilter_.tick(lastFrame_); // bridge
																			// coupling
				output += strings_[i].tick(temp);
				// Check if string energy has decayed sufficiently to turn it
				// off.
				if (stringState_[i] == 1) {
					if (Math.abs(strings_[i].lastOut()) < 0.001)
						decayCounter_[i]++;
					else
						decayCounter_[i] = 0;
					if (decayCounter_[i] > (int) (0.1 * StaticVariables.SampleRate)) {
						stringState_[i] = 0;
						decayCounter_[i] = 0;
					}
				}
			}
		}

		lastFrame_ = output;
		return lastFrame_;
	}
}
