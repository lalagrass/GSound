package com.sample.gsound;

public class GuitarCmd {
    public enum GuitarCmdType {
        Setting, Chord, Play, Wait, Press0, Press1, Press2, AutoPlay, AutoPlay2, AutoPlayStop
    };

    public GuitarCmdType CmdType;
    public int[] CmdTarget;

    public GuitarCmd() {

    }
}
