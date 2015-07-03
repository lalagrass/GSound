package com.sample.gsound;

import com.sample.gsound.synthesis.Cubic;
import com.sample.gsound.synthesis.Delay;
import com.sample.gsound.synthesis.Guitar2;
import com.sample.gsound.synthesis.JCRev;

public class GuitarType1 extends GuitarType0{

    // WvOut **wvout;
    Guitar2 guitar;
    // StringInfo voices[nStrings];
    JCRev reverb = new JCRev(1.0);
    // Messager messager;
    // Skini::Message message;
    double volume;
    double t60;
    int nWvOuts;
    int channels;
    int counter;
    boolean realtime;
    boolean settling;
    boolean haveMessage;
    int keysDown;

    double feedbackGain;
    double oldFeedbackGain;
    double distortionGain;
    double distortionMix;
    Delay feedbackDelay = new Delay(0, 4095);
    Cubic distortion = new Cubic();
    double feedbackSample;

    double _distortionGain = 1;
    double _distortionMix = 0.9;
    double _reverbMix = 0.5;
    double _a1 = 0.5;
    double _a2 = 0.5;
    double _a3 = 0.5;
    double _gain = 1;
    double _threshold = 0.6666667;

    public GuitarType1(double gain) {
        volume = 1.0;
        t60 = 0.75;
        channels = 1;
        counter = 0;
        realtime = false;
        settling = false;
        haveMessage = false;
        keysDown = 0;
        feedbackSample = 0.0;

        reverb.setT60(t60);
        reverb.setEffectMix(0.2);
        guitar = new Guitar2(6);
        distortion.setThreshold(2.0 / 3.0);
        distortion.setA1(1.0);
        distortion.setA2(0.0);
        distortion.setA3(-1.0 / 3.0);
        distortionMix = 0.9;
        distortionGain = 1.0;
        feedbackDelay.setMaximumDelay((long) (1.1 * StaticVariables.SampleRate));
        feedbackDelay.setDelay(20000);
        feedbackGain = 0.001;
        oldFeedbackGain = 0.001;
        guitar.SetStringGain(gain);
    }

    @Override
    public double tick() {
        double sample = feedbackDelay.tick(feedbackSample
                * feedbackGain);
        sample = guitar.tick(sample);
        double temp = distortionGain * sample;
        if (temp > 0.6666667)
            temp = 0.6666667;
        else if (temp < -0.6666667)
            temp = -0.6666667;
        else
            temp = distortion.tick(temp);
        sample = (distortionMix * temp)
                + ((1 - distortionMix) * sample);
        feedbackSample = sample;
        sample = volume * reverb.tick(sample);
        return sample;
    }

    @Override
    public void PalmMute(double a, int line, double f) {
        guitar.PalmMute(a, line, f);
    }

    @Override
    public void noteOn(int f, double a, int line) {
        guitar.noteOn(f, a, line);
    }

    @Override
    public void setFrequency(int f, int line) {
        guitar.setFrequency(f, line);
    }

    @Override
    public void noteOff(int line) {
        guitar.noteOff(line);
    }
}
