package com.example.gpdesign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DisplayFoodCateg extends AppCompatActivity {

    ListView mylis;
    ArrayAdapter<String> adp;
    String categ;
    List<String[]> mylist;
    String[] data;
    Button btn;
    InputStream inputStream;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_food_categ);
        //LOADING The Food
        final HashMap<String, String[]> fooddata=new HashMap<String, String[]>();
        mylist=new ArrayList<>();
        inputStream=getResources().openRawResource(R.raw.fooddata2);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try{
            String csvLine;
            while((csvLine = reader.readLine()) !=null)
            {
                data=csvLine.split(",");
                mylist.add(data);
                String []temp = new String[6];
                temp[0]=data[1];
                temp[1]=data[2];
                temp[2]=data[3];
                temp[3]=data[4];
                temp[4]=data[5];
                temp[5]=data[6];
                fooddata.put(data[0].trim(),temp);

                try{
                }catch (Exception e)
                {
                    Log.e("problem",e.toString());
                }
            }

        }catch (IOException ex)
        {
            throw new  RuntimeException("Error"+ex);
        }
        //==========================
        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference().child("USERS");

        categ=getIntent().getStringExtra("categ");

        adp=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        mylis=findViewById(R.id.listitem);
        mylis.setAdapter(adp);
        btn=findViewById(R.id.pop);


        //take USer pref
        final ArrayList<String> arrayList=new ArrayList<String>();
        mylis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String p=((TextView)view).getText().toString();
                arrayList.add(p);
            }
        });

        //==================

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String UserPref = "";
                for (int i = 0; i < arrayList.size(); i++) {
                    if(i!=arrayList.size()-1) {
                        UserPref += arrayList.get(i) + ",";
                    }
                    else{
                        UserPref+=arrayList.get(i);
                    }
                }
                String userid = getIntent().getStringExtra("userID");
                DatabaseReference currentuser = databaseReference.child(userid);
                currentuser.child("UserPref").setValue(UserPref);
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
            });

    }

    @Override
    protected void onStart() {
        adp.clear();

        adp.notifyDataSetChanged();
        String q=categ;

        for (int i=0;i<mylist.size();i++){

            String []arr=mylist.get(i);
            if(arr[2].equals(q)){
                adp.add(arr[0]);
            }
        }
        super.onStart();
    }
}
