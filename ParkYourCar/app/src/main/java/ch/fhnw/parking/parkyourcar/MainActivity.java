package ch.fhnw.parking.parkyourcar;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final String PARKING_NAME = "name";
    public static final String PARKING_STATUS = "status";
    public static final String PARKING_PLACE = "place";
    public static final String PARKING_DBID = "dbid";
    //private ArrayAdapter<String> arrayAdapter;
    private MyListAdapter arrayAdapter;
    private ListView listView;
    private DatabaseReference myRef;
    private MainActivity main;

    private ArrayList<ParkingModel> parkings;

    //tutorial
    private ArrayList<String> data = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main = this;
        listView = (ListView) findViewById(R.id.listview);

        //tutorial
        /*
        generateContext();
        listView.setAdapter(new MyListAdapter(this, R.layout.list_item, data));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "List item was clicked at " + position, Toast.LENGTH_SHORT).show();
            }
        });
        */




        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("parkings");
        addParkings();
    }

    private void generateContext() {
        for(int i = 0; i < 10; i++ ){
            data.add("This is Row number "+i);
        }
    }

    private void addParkings(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               // String value = dataSnapshot.getValue(String.class);
                parkings = new ArrayList<ParkingModel>();
                for (DataSnapshot parking: dataSnapshot.getChildren()){
                    parkings.add(new ParkingModel((String)parking.child("Name").getValue(), (String)parking.child("Status").getValue(),(String)parking.child("Place").getValue(), (String)parking.getKey()));

                    //arrayAdapter.add((String)parking.child("Name").getValue());
                }
                arrayAdapter = new MyListAdapter(main, R.layout.list_item, parkings);
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
        String value = arrayAdapter.getItem(position).toString();
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

    //tutorial
    private class MyListAdapter extends ArrayAdapter<ParkingModel> {

        private int layout;
        private MyListAdapter(Context context, int resource, List<ParkingModel> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder mainViewHolder = null;
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.list_item_thumbnail);
                viewHolder.title = (TextView) convertView.findViewById(R.id.list_item_text);
                viewHolder.button = (Button) convertView.findViewById(R.id.list_item_btn);
                //viewHolder.title.setText("List "+position);
                viewHolder.button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "Button was clicked for list item "+position, Toast.LENGTH_SHORT).show();
                    }
                });
                convertView.setTag(viewHolder);

            } else {
                mainViewHolder = (ViewHolder) convertView.getTag();
                //mainViewHolder.title.setText(getItem(position));

            }
            return convertView;
        }
    }

    //tutorial
    public class ViewHolder {
        ImageView thumbnail;
        TextView title;
        Button button;
    }
}
