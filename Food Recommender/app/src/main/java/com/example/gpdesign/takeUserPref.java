package com.example.gpdesign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class takeUserPref extends AppCompatActivity {
    RadioButton meat,che,fish,dia,fru,veg;
    String pre="dasd";
    Button next3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_user_pref);
        meat=findViewById(R.id.meat);
        che=findViewById(R.id.chicken);
        fish=findViewById(R.id.fish);
        dia=findViewById(R.id.dairy);
        fru=findViewById(R.id.fruit);
        veg=findViewById(R.id.vegetables);
        next3=findViewById(R.id.Next3);
        meat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pre="Meat";
            }
        });
        che.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pre="Chicken";
            }
        });
        fish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pre="Fish";
            }
        });
        dia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pre="Dairy";
            }
        });
        fru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pre="Fruit";
            }
        });
        veg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pre="vegetables";
            }
        });
        next3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),DisplayFoodCateg.class);
                intent.putExtra("categ",pre);
                intent.putExtra("userID",getIntent().getStringExtra("userID"));
                startActivity(intent);
            }
        });
    }
}
