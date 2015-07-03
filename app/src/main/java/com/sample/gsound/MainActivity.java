package com.sample.gsound;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

    private Button _b1;
    private Button _b2;
    private Button _b3;
    private Button _b4;
    private Button _b5;
    private Button _b6;

    private static volatile boolean _started = false;
    private GuitarManager _gm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        double gain = 0.9930;
        double amp = 0.66;
        _gm = new GuitarManager(new GuitarType1(gain), amp);
        OnUpdateChord(null);
        _b1 = (Button) findViewById(R.id.button1);
        _b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnSwipeGuitar(1);
            }
        });
        _b2 = (Button) findViewById(R.id.button2);
        _b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnSwipeGuitar(2);
            }
        });
        _b3 = (Button) findViewById(R.id.button3);
        _b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnSwipeGuitar(3);
            }
        });
        _b4 = (Button) findViewById(R.id.button4);
        _b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnSwipeGuitar(4);
            }
        });
        _b5 = (Button) findViewById(R.id.button5);
        _b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnSwipeGuitar(5);
            }
        });
        _b6 = (Button) findViewById(R.id.button6);
        _b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnSwipeGuitar(6);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        Stop();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Start();
    }

    public synchronized void Stop() {
        if (_started) {
            _started = false;
            _gm.Stop();
        }
    }

    public synchronized void Start() {
        if (!_started) {
            _started = true;
            _gm.Start();
        }
    }

    public void OnUpdateChord(int[] chord) {
        if (chord == null || chord.length < 6) {
            chord = new int[]{0, 0, 0, 0, 0, 0};
        }
        GuitarCmd cmd = new GuitarCmd();
        cmd.CmdTarget = chord;
        cmd.CmdType = GuitarCmd.GuitarCmdType.Chord;
        _gm.PushMessage(cmd);
    }

    public void OnSwipeGuitar(int type) {
        if (type >= 0 && type <= 8) {
            GuitarCmd cmd = new GuitarCmd();
            cmd.CmdType = GuitarCmd.GuitarCmdType.Play;
            cmd.CmdTarget = new int[]{type};
            _gm.PushMessage(cmd);
        }
    }
}
