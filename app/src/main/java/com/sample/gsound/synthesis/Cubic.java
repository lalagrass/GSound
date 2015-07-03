package com.sample.gsound.synthesis;

public class Cubic {
    double lastFrame_;
    double a1_;
    double a2_;
    double a3_;
    double gain_;
    double threshold_;

    public Cubic() {
        a1_ = 0.5;
        a2_ = 0.5;
        a3_ = 0.5;
        gain_ = 1.0;
        threshold_ = 1.0;
    }

    public double tick(double input) {
        double inSquared = input * input;
        double inCubed = inSquared * input;

        lastFrame_ = gain_ * (a1_ * input + a2_ * inSquared + a3_ * inCubed);
        // Apply threshold if we are out of range.
        if (Math.abs(lastFrame_) > threshold_) {
            lastFrame_ = (lastFrame_ < 0 ? -threshold_ : threshold_);
        }
        return lastFrame_;
    }

    public void setThreshold(double threshold) {
        threshold_ = threshold;
    }

    public void setA1(double a1) {
        a1_ = a1;
    }

    // ! Set the a2 coefficient value.
    public void setA2(double a2) {
        a2_ = a2;
    }

    // ! Set the a3 coefficient value.
    public void setA3(double a3) {
        a3_ = a3;
    }

    // ! Set the gain value.
    void setGain(double gain) {
        gain_ = gain;
    }

}
