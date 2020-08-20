package com.example.gpdesign;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    Button signUp,login;
    EditText email,password;
    CheckBox remeberme;
    private FirebaseAuth firebaseAuth;

    private int attemps=0;
    private SharedPreferences mpref;
    private static final String Pref_Name="PrefsFile";

    @SuppressLint("LongLogTag")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mpref=getSharedPreferences(Pref_Name,MODE_PRIVATE);
        firebaseAuth=FirebaseAuth.getInstance();
        bindWidget();
        setupWidgetEventListener();
        getPreferencesData();



        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),FirstRegisteration.class);
                intent.putExtra("avtivitykey","frommain");
                intent.putExtra("secondactivitydata","frommain");
                startActivity(intent);
                finish();
            }
        });
    }
    private void getPreferencesData(){
        SharedPreferences sp=getSharedPreferences(Pref_Name,MODE_PRIVATE);

        if(sp.contains("pref_email")){
            String e=sp.getString("pref_email","not found");
            email.setText(e.toString());
        }
        if(sp.contains("pref_pass")){
            String p=sp.getString("pref_pass","not found");
            password.setText(p.toString());
        }
        if(sp.contains("pref_check")){
            boolean b=sp.getBoolean("pref_check",false);
            remeberme.setChecked(b);
        }
    }
    private void bindWidget(){
        signUp=findViewById(R.id.signUp);
        login=findViewById(R.id.login);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        remeberme=findViewById(R.id.remberme);
    }
    private void setupWidgetEventListener(){
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemps++;
                if(email.getText().toString().equals("")&&password.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Please Enter your Email and Password !!",Toast.LENGTH_LONG).show();
                }
                else if(email.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Please Enter your Email !!",Toast.LENGTH_LONG).show();
                }
                else if(password.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Please Enter your Password !!",Toast.LENGTH_LONG).show();
                }
                else{
                    firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        String useridd=firebaseAuth.getCurrentUser().getUid();
                                        if(remeberme.isChecked()){
                                            Boolean ischeck=remeberme.isChecked();
                                            SharedPreferences.Editor editor=mpref.edit();
                                            editor.putString("pref_email",email.getText().toString());
                                            editor.putString("pref_pass",password.getText().toString());
                                            editor.putBoolean("pref_check",ischeck);
                                            editor.apply();
                                        }
                                        else {
                                            mpref.edit().clear().apply();
                                        }
                                        hhh(useridd);
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }
    public void hhh(String Userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("USERS");
        reference.orderByChild("USERID").equalTo(Userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent intent=new Intent(getApplicationContext(),test.class);
                for(DataSnapshot datas: dataSnapshot.getChildren()){

                    String USErPlan=datas.child("plan").getValue().toString();
                    intent.putExtra("plan",USErPlan);

                    String USErWeight=datas.child("weight").getValue().toString();
                    intent.putExtra("weight",USErWeight);

                    String USErHeight=datas.child("height").getValue().toString();
                    intent.putExtra("height",USErHeight);

                    String USErDate=datas.child("date").getValue().toString();
                    intent.putExtra("date",USErDate);

                    String USErPref=datas.child("UserPref").getValue().toString();
                    intent.putExtra("UserPref",USErPref);

                    String USErCluster=datas.child("cluster").getValue().toString();
                    intent.putExtra("cluster",USErCluster);

                    String USErCal=datas.child("calories").getValue().toString();
                    intent.putExtra("calories",USErCal);

                    String USErDis=datas.child("diseases").getValue().toString();
                    intent.putExtra("diseases",USErDis);

                    String USErGen=datas.child("gender").getValue().toString();
                    intent.putExtra("gender",USErGen);

                    String USErActivity=datas.child("activity").getValue().toString();
                    intent.putExtra("Activity",USErActivity);

                    String USErage=datas.child("age").getValue().toString();
                    intent.putExtra("age",USErage);



                    startActivity(intent);
                    finish();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
