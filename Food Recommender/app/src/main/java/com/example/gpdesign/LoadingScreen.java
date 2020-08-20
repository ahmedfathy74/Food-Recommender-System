package com.example.gpdesign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import com.felipecsl.gifimageview.library.GifImageView;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class LoadingScreen extends AppCompatActivity {
    private ProgressBar progressBar;
    private GifImageView gifImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        gifImageView=findViewById(R.id.gifimage);
        progressBar=findViewById(R.id.prog);
        progressBar.setVisibility(progressBar.VISIBLE);
        try {
            InputStream inputStream=getAssets().open("loading.gif");
            byte [] bytes= IOUtils.toByteArray(inputStream);
            gifImageView.setBytes(bytes);
            gifImageView.startAnimation();
        }
        catch (IOException ex){

        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LoadingScreen.this.startActivity(new Intent(LoadingScreen.this,MainActivity.class));
                LoadingScreen.this.finish();
            }
        },2500);
    }
}
