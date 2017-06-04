package ch.fhnw.parking.parkyourcar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final String PARKING_NAME = "name";
    public static final String PARKING_STATUS = "status";
    public static final String PARKING_PLACE = "place";
    public static final String PARKING_DBID = "dbid";
    private ArrayAdapter<String> arrayAdapter;
    private ListView listView;
    private DatabaseReference myRef;
    private MainActivity main;

    private ArrayList<ParkingModel> parkings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main = this;
        listView = (ListView) findViewById(R.id.listview);


        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("parkings");
        addParkings();
    }

    private void addParkings(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               // String value = dataSnapshot.getValue(String.class);
                parkings = new ArrayList<ParkingModel>();
                arrayAdapter = new ArrayAdapter<String>(main, android.R.layout.simple_list_item_1);

                for (DataSnapshot parking: dataSnapshot.getChildren()){
                    parkings.add(new ParkingModel((String)parking.child("Name").getValue(), (String)parking.child("Status").getValue(),(String)parking.child("Place").getValue(), (String)parking.getKey()));

                    arrayAdapter.add((String)parking.child("Name").getValue());
                }

                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener(main);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        String[] parkings = new String[6];


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent detailIntent = new Intent(this, ParkingDetailActivity.class);
        String value = arrayAdapter.getItem(position);
        for (ParkingModel parking: parkings){
            if (parking.getName().equals(value)){
                detailIntent.putExtra(PARKING_NAME,value);
                detailIntent.putExtra(PARKING_STATUS, parking.getStatus());
                detailIntent.putExtra(PARKING_PLACE, parking.getPlace());
                detailIntent.putExtra(PARKING_DBID, parking.getDbId());

            }
        }
        startActivity(detailIntent);
    }
}
