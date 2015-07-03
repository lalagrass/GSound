package com.sample.gsound;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GuitarManager {

    private Queue<GuitarCmd> _messageQueue;
    private BlockingQueue<short[]> _soundQueue;
    private Thread _threadGenerator;
    private Thread _threadConsumer;
    private GuitarType0 data;
    private double _amp;
    private static volatile boolean _isRunning = false;

    public GuitarManager(GuitarType0 d, double amp) {
        data = d;
        _messageQueue = new ConcurrentLinkedQueue<GuitarCmd>();
        _soundQueue = new LinkedBlockingQueue<short[]>(1);
        _amp = amp;
        if (_amp < 0.4)
            _amp = 0.4;
        if (_amp > 0.8)
            _amp = 0.8;
    }

    public void PushMessage(GuitarCmd cmd) {
        _messageQueue.offer(cmd);
    }

    public void Start() {
        _isRunning = true;
        if (_threadGenerator == null) {
            _threadGenerator = new Thread(_guitarGenerator);
            _threadGenerator.start();
        }
        if (_threadConsumer != null) {
            _threadConsumer.interrupt();
            _threadConsumer = null;
        }
        _threadConsumer = new Thread(_guitarConsumer);
        _threadConsumer.start();
    }

    public void Stop() {
        _isRunning = false;
        if (_threadConsumer != null) {
            _threadConsumer.interrupt();
            _threadConsumer = null;
        }
    }

    Runnable _guitarGenerator = new Runnable() {
        private int[] _remain = new int[6];

        private int[][] _frequency = {
                {330, 349, 370, 392, 415, 440, 466, 494, 523, 554, 587, 622,
                        659, 698, 740, 784, 831, 880, 932, 988, 1047},
                {247, 262, 277, 294, 311, 330, 349, 370, 392, 415, 440, 466,
                        494, 523, 554, 587, 622, 659, 698, 740, 784},
                {196, 208, 220, 233, 247, 262, 277, 294, 311, 330, 349, 370,
                        392, 415, 440, 466, 494, 523, 554, 587, 622},
                {147, 156, 165, 175, 185, 196, 208, 220, 233, 247, 262, 277,
                        294, 311, 330, 349, 370, 392, 415, 440, 466},
                {110, 117, 124, 131, 139, 147, 156, 165, 175, 185, 196, 208,
                        220, 233, 247, 262, 277, 294, 311, 330, 349},
                {82, 87, 93, 98, 104, 110, 117, 124, 131, 139, 147, 156, 165, 175, 185, 196, 208,
                        220, 233, 247, 262}};
        private int[] _chord = new int[7];
        private int[] _press = new int[]{-1, -1, -1, -1, -1, -1};

        private void NextChord(int[] chord, boolean needDisplay, int index) {
            if (chord != null && chord.length > 5) {
                for (int i = 0; i < 6; i++) {
                    _chord[i] = chord[i];
                }
            }
        }

        public void run() {
            short generatedSnd[];// = new byte[2 * numSamples];

            int _bufSize = AudioTrack.getMinBufferSize(StaticVariables.SampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);


            double sample;
            double temp;
            int _seg = 0;
            int sndIndex = 0;
            int dataarraysize = 5;
            short[][] dataArray = new short[dataarraysize][];
            for (int i = 0; i < dataarraysize; i++) {
                dataArray[i] = new short[_bufSize / 2];
            }

            while (!Thread.interrupted()) {
                generatedSnd = dataArray[sndIndex];
                sndIndex = (sndIndex + 1) % dataarraysize;
                for (int i = 0; i < generatedSnd.length; i++) {

                    if (_seg++ > 44) {
                        for (int l = 0; l < 6; l++) {
                            if (_remain[l] > 0) {
                                _remain[l]--;
                                if (_remain[l] == 0) {
                                    NoteOff(l);
                                }
                            }
                        }
                        _seg = 0;
                        ProcessMessage();
                    }
                    sample = data.tick();
                    generatedSnd[i] = (short) (Short.MAX_VALUE * sample);
                }
                try {
                    _soundQueue.put(generatedSnd);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void ProcessMessage() {
            int count = _messageQueue.size();
            if (count != 0)
                for (int j = 0; j < count; j++) {
                    GuitarCmd cmd = _messageQueue.poll();
                    ProcessMessage(cmd);
                }
        }

        private void ProcessMessage(GuitarCmd cmd) {
            if (cmd != null) {
                switch (cmd.CmdType) {
                    case Chord:
                        int[] chord = cmd.CmdTarget;
                        if (chord != null && chord.length > 0)
                            NextChord(chord, false, -1);
                        for (int i = 0; i < 6; i++) {
                            _press[i] = -1;
                        }
                        break;
                    case Press1: {
                        int[] ret = cmd.CmdTarget;
                        for (int i = 0; i < ret.length; i += 2) {
                            int line = ret[i];
                            int floor = ret[i + 1];
                            _press[line] = floor;
                        }
                    }
                    break;
                    case Press2: {
                        int[] ret = cmd.CmdTarget;
                        for (int i = 0; i < ret.length; i += 2) {
                            int line = ret[i];
                            int floor = ret[i + 1];
                            SetF(line, floor);
                        }
                    }
                    break;
                    case Play:
                        int[] params = cmd.CmdTarget;
                        if (params != null) {
                            for (int i = 0; i < params.length; i++)
                                PlayRight(params[i], false);
                        }
                        break;
                }
            }
        }

        private void PlayRight(int i, boolean needDisplay) {
            switch (i) {
                case 1:
                    NoteOn(0);
                    break;
                case 2:
                    NoteOn(1);
                    break;
                case 3:
                    NoteOn(2);
                    break;
                case 4:
                    NoteOn(3);
                    break;
                case 5:
                    NoteOn(4);
                    break;
                case 6:
                    NoteOn(5);
                    break;
            }
        }

        private void NoteOff(int i) {
            data.noteOff(i);
        }

        private void NoteOn(int i) {
            if (_press[i] > -1) {
                NoteOn(i, _press[i]);
                _press[i] = -1;
            } else {
                NoteOn(i, _chord[i]);
            }
            _remain[i] = 3600;
        }

        private void NoteOn(int i, int j) {
            if (j < 0)
                data.PalmMute(_amp, i, _frequency[i][0]);
            else
                data.noteOn(_frequency[i][j], _amp, i);
        }

        private void SetF(int line, int floor) {
            data.setFrequency(_frequency[line][floor], line);
        }
    };

    Runnable _guitarConsumer = new Runnable() {

        public void run() {
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            AudioTrack audioTrack;
            short generatedSnd[] = null;
            int _bufSize = AudioTrack.getMinBufferSize(StaticVariables.SampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, StaticVariables.SampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, _bufSize,
                    AudioTrack.MODE_STREAM);

            audioTrack.play();

            while (!Thread.interrupted() && _isRunning) {
                try {
                    generatedSnd = _soundQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (generatedSnd != null)
                    audioTrack.write(generatedSnd, 0, generatedSnd.length);
            }
            audioTrack.stop();
            audioTrack.release();
        }
    };
}
