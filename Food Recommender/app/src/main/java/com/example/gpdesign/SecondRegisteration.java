package com.example.gpdesign;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class SecondRegisteration extends AppCompatActivity {
    Map<Integer,ArrayList<Integer>> clusters=new HashMap<Integer,ArrayList<Integer>>();
    ImageView back ;
    EditText weight,height;
    Spinner plan,activity,diseases;
    RadioButton anything,vegt;
    Button next2;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_registeration);

        int clusterID=0;
        for (int i=0;i<3;i++){ //plan loss=0 main=1 gain=2

            for (int k=0;k<2;k++){ //type any=0 veg=1
                ArrayList<Integer> values = new ArrayList<Integer>(Arrays.asList(i,k));
                clusters.put(clusterID,values);
                clusterID++;
            }

        }
        weight=findViewById(R.id.regWeight);
        height=findViewById(R.id.regHeight);
        plan=findViewById(R.id.plan);
        activity=findViewById(R.id.activity);
        anything=findViewById(R.id.any);
        diseases=findViewById(R.id.disease);
        anything.setChecked(true);
        vegt=findViewById(R.id.Vegetarian);
        next2=findViewById(R.id.Next2);
        back = findViewById(R.id.backToFirstReg) ;
        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference().child("USERS");


        if(getIntent().getStringExtra("myolddata").equals("data")){
            weight.setText(getIntent().getStringExtra("weight"));
            height.setText(getIntent().getStringExtra("height"));
            if(getIntent().getStringExtra("foodtype").equals("anything")){
                anything.setChecked(true);
            }
            else {
                vegt.setChecked(true);
            }
            ArrayAdapter<String> planadapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.plan));
            plan.setAdapter(planadapter);
            int planpos=planadapter.getPosition(getIntent().getStringExtra("plan"));
            plan.setSelection(planpos);


            ArrayAdapter<String> activityadapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.activity));
            activity.setAdapter(activityadapter);
            int activitypos=planadapter.getPosition(getIntent().getStringExtra("activity"));
            activity.setSelection(activitypos);

            ArrayAdapter<String> diseasesadapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.diseases));
            diseases.setAdapter(activityadapter);
            int diseasespos=planadapter.getPosition(getIntent().getStringExtra("diseases"));
            diseases.setSelection(diseasespos);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext() , FirstRegisteration.class);
                intent.putExtra("avtivitykey","fromsecondregactivity");
                intent.putExtra("regName",getIntent().getStringExtra("name"));
                intent.putExtra("regEmail",getIntent().getStringExtra("email"));
                intent.putExtra("regPassword",getIntent().getStringExtra("pass"));
                intent.putExtra("regAge",getIntent().getStringExtra("age"));
                intent.putExtra("gender",getIntent().getStringExtra("gender"));
                intent.putExtra("secondactivitydata","olddatatofirstactivity");
                intent.putExtra("regWeig",weight.getText().toString().trim());
                intent.putExtra("regHeig",height.getText().toString().trim());
                intent.putExtra("plan",plan.getSelectedItem().toString());
                intent.putExtra("activity",activity.getSelectedItem().toString());
                intent.putExtra("diseases",diseases.getSelectedItem().toString());
                String foodtype="";
                if(anything.isChecked()){
                    foodtype="anything";
                }
                else if(vegt.isChecked()){
                    foodtype="vegt";
                }
                intent.putExtra("foodtype",foodtype);
                startActivity(intent);
            }
        });




        next2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(weight.length()==0){
                    weight.setError("Please Enter Your Weight");
                }
                if(height.length()==0){
                    height.setError("Please Enter Your Height");
                }
                else {
                    String foodtype="";
                    if(anything.isChecked()){
                        foodtype="anything";
                    }
                    else if(vegt.isChecked()){
                        foodtype="vegt";
                    }
                    double cal=0;
                    int clus=0;
                    clus=findcluster(plan.getSelectedItem().toString(),foodtype);
                    cal=calcCalories(getIntent().getStringExtra("gender"),plan.getSelectedItem().toString(),activity.getSelectedItem().toString(),Integer.parseInt(weight.getText().toString()),Integer.parseInt(height.getText().toString()),Integer.parseInt(getIntent().getStringExtra("age")));
                    reg(cal,foodtype,clus);
                }
            }
        });
    }
    public double calcCalories(String gender,String plan,String activitylevel,int weight,int height,int age){

        double factor=-1;
        double cal=-1;

        if (activitylevel.equals("Lightly Active")){
            factor=1.375;
        }
        else if(activitylevel.equals("Moderately Active")){
            factor=1.55;
        }
        else if(activitylevel.equals("High Active")){
            factor=1.725;
        }
        if (gender.equals("male")){
            cal=((10*weight)+(6.25*height)-(5*age)+5)*factor;
        }
        else if(gender.equals("female")){
            cal=((10*weight)+(6.25*height)-(5*age)-161)*factor;
        }
        if (plan.equals("Loss Weight")){
            cal-=350;
        }
        else if(plan.equals("Gain Weight")){
            cal+=350;
        }
        return cal;
    }



    public int findcluster(String planvalue,String foodtypevalue){
        int min=-1;
        int planval=-1;
        int foodtypeval=-1;
        if(planvalue.equals("Loss Weight")){
            planval=0;
        }
        else if(planvalue.equals("Maintain")){
            planval=1;
        }
        else if(planvalue.equals("Gain Weight")){
            planval=2;
        }
        if(foodtypevalue.equals("anything")){
            foodtypeval=0;
        }
        else if(foodtypevalue.equals("vegt")){
            foodtypeval=1;
        }
        for(int i=0;i<clusters.size();i++){

            ArrayList<Integer> list=clusters.get(i);
            int plan= list.get(0);
            int type=list.get(1);
            int add= (int) Math.pow((planval-plan),2)+(int)Math.pow((foodtypeval-type),2);
            int res=(int)Math.sqrt(add);
            if (res==0){
                min=i;
                break;
            }
        }

        return min;
    }


    void reg(final double cal, final String foodtype,final int cluster){

        mAuth.createUserWithEmailAndPassword(getIntent().getStringExtra("email"),getIntent().getStringExtra("pass"))
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            String userid=mAuth.getCurrentUser().getUid();
                            DatabaseReference currentuser=databaseReference.child(userid);
                            currentuser.child("USERID").setValue(userid);
                            currentuser.child("NAme").setValue(getIntent().getStringExtra("name"));
                            currentuser.child("age").setValue(getIntent().getStringExtra("age"));
                            currentuser.child("gender").setValue(getIntent().getStringExtra("gender"));
                            currentuser.child("weight").setValue(weight.getText().toString().trim());
                            currentuser.child("height").setValue(height.getText().toString().trim());
                            currentuser.child("plan").setValue(plan.getSelectedItem().toString().trim());
                            currentuser.child("activity").setValue(activity.getSelectedItem().toString().trim());
                            currentuser.child("diseases").setValue(diseases.getSelectedItem().toString().trim());
                            currentuser.child("calories").setValue(String.valueOf(cal));
                            currentuser.child("foodtype").setValue(foodtype);
                            String pattern = "dd-MM-yyyy";
                            String dateInString =new SimpleDateFormat(pattern).format(new Date());
                            currentuser.child("date").setValue(dateInString);
                            String hh=String.valueOf(cluster);
                            currentuser.child("cluster").setValue(hh);
                            Intent intent=new Intent(getApplicationContext(),takeUserPref.class);
                            intent.putExtra("userID",userid);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
}
