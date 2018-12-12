package it.pgp.xzexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Compressor compressor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        compressor = new Compressor();
    }

    public void compress(View v) {
        try {
            compressor.createTarXz(new File("/storage/sdcard/files"),new File("/storage/sdcard/f2.tar.xz"));
            Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void extract(View v) {
        try {
            compressor.extractTarXz(new File("/storage/sdcard/f2.tar.xz"),new File("/storage/sdcard/extracted"));
            Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
