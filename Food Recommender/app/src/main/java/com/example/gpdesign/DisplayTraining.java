package com.example.gpdesign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DisplayTraining extends AppCompatActivity {

    ImageView trainingimage;
    String TriningSecond="",TrainingCount="";
    List<Training> TrainingList;
    ImageView next,prev;
    int Current=0;
    List<Integer> list;
    String  Name="",URL="";
    TextView Trainname,traintime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_training);

        prev=findViewById(R.id.prev);
        next=findViewById(R.id.nex);
        trainingimage=findViewById(R.id.TrainingImage);
        Trainname=findViewById(R.id.TrainingName);
        traintime=findViewById(R.id.TrainingDuration);
        TrainingList=new ArrayList<>();


        String plan=getIntent().getStringExtra("plan");
        final String []names=getIntent().getStringArrayExtra("NameList");
        final String []Url=getIntent().getStringArrayExtra("UrlList");
        final String []Type=getIntent().getStringArrayExtra("Type");

        if (plan.equals("Loss Weight")){TriningSecond="60sec";TrainingCount="16";}
        else{TriningSecond="30sec";TrainingCount="8";}
        list=new ArrayList<>();
        Random rand = new Random();
        int n = rand.nextInt(15);
        list.add(n);
        while (list.size()!=5){
            n = rand.nextInt(15);
            if (!list.contains(n)){
                list.add(n);
            }
        }

        URL=Url[list.get(Current)];
        Name=names[list.get(Current)];
        Glide
        .with(this)
        .load(URL)
        .asGif()
        .error(R.drawable.qq)
        .into(trainingimage);

        Trainname.append(Name);
        if(Type[list.get(Current)].equals("second")){
            traintime.append(TriningSecond);
        }
        else{
            traintime.append("x"+TrainingCount);
        }

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Current!=0){
                    URL=Url[list.get(Current-1)];
                    Name=names[list.get(Current-1)];
                    Glide
                    .with(getApplicationContext())
                    .load(URL)
                    .asGif()
                    .error(R.drawable.qq)
                    .into(trainingimage);

                    Trainname.setText("Name: "+Name);
                    if(Type[list.get(Current-1)].equals("second")){
                        traintime.setText("Duration: "+TriningSecond);
                    }
                    else{
                        traintime.setText("Duration: x"+TrainingCount);
                    }
                    Current--;
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Current!=4){
                    URL=Url[list.get(Current+1)];
                    Name=names[list.get(Current+1)];
                    Glide
                    .with(getApplicationContext())
                    .load(URL)
                    .asGif()
                    .error(R.drawable.qq)
                    .into(trainingimage);

                    Trainname.setText("Name: "+Name);
                    if(Type[list.get(Current+1)].equals("second")){
                        traintime.setText("Duration: "+TriningSecond);
                    }
                    else{
                        traintime.setText("Duration: x"+TrainingCount);
                    }
                    Current++;
                }
            }
        });
        Log.v("lisss", String.valueOf(list));
    }


}