package com.example.momentum.habitEvents;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;


import com.example.momentum.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
/**
 * an activity shows list of habit events
 * click items on list to view the individual habit event details
 * click delete button and click the habit event that user wants to remove
 * @author Han Yan
 */
public class Habit_Events extends AppCompatActivity {
    ListView listView;
    Button delete_habitEvent_btn;
    private String uid;
    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_events);

        listView = findViewById(R.id.habit_event_listView);

        //create a list for habit events
        ArrayList<String> list = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_item, list);
        listView.setAdapter(adapter);

        //create reference of database and retrieve habit events collection from db
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        CollectionReference reference = db.collection("Users").document(uid).collection("Events");
        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                list.clear();

                for  (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    if (doc.exists()){
                        list.add(doc.getId());
                    }
                }
                adapter.notifyDataSetChanged();

            }
        });


        //click item on the list and switch to individual view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                startActivity(new Intent(Habit_Events.this, Indiv_habitEvent_view.class));
            }

        });

        //delete a habit event
        delete_habitEvent_btn =findViewById(R.id.delete_he_btn);
        delete_habitEvent_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete_habitEvent_btn.setText("Finish");
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        String itemValue = (String)listView.getItemAtPosition(position);
                        adapter.remove(itemValue);
                        adapter.notifyDataSetChanged();
                    }
                });
                delete_habitEvent_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        delete_habitEvent_btn.setText("Delete");
                        delete_habitEvent_btn.setEnabled(false);
                    }
                });
            }
        });




    }




}