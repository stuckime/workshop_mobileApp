package ch.fhnw.parking.parkyourcar;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

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
    private ArrayAdapter<ParkingModel> arrayAdapter;
    private ListView listView;
    private GifImageView loadingScreen;
    private LinearLayout linearLayout;
    private DatabaseReference myRef;
    private MainActivity main;

    private ArrayList<ParkingModel> parkings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main = this;
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        listView = (ListView) findViewById(R.id.listview);
        linearLayout.setBackgroundColor(getResources().getColor(R.color.background));
        loadingScreen = (GifImageView) findViewById(R.id.loadingImage);
        loadingScreen.setGifImageResource(R.drawable.loading);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("parkings");
        addParkings();
    }

    private void addParkings(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                parkings = new ArrayList<>();

                for (DataSnapshot parking: dataSnapshot.getChildren()) {
                    ParkingModel parkingModel = new ParkingModel((String) parking.child("Name").getValue(), (String) parking.child("Status").getValue(), (String) parking.child("Place").getValue(), parking.getKey());
                    if(parkingModel.getName() != null) {
                        parkings.add(parkingModel);
                    }
                }
                arrayAdapter = new MyListAdapter(main, R.layout.list_item, parkings);
                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener(main);

                linearLayout.setBackgroundColor(getResources().getColor(R.color.white));
                loadingScreen.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent detailIntent = new Intent(this, ParkingDetailActivity.class);
        String value = arrayAdapter.getItem(position).getName();
        ParkingModel parking = null;
        for (ParkingModel p: parkings){
            if (value != null && p.getName().equals(value)){
                parking = p;
            }
        }
        if(parking != null) {
            detailIntent.putExtra(PARKING_NAME,value);
            detailIntent.putExtra(PARKING_STATUS, parking.getStatus());
            detailIntent.putExtra(PARKING_PLACE, parking.getPlace());
            detailIntent.putExtra(PARKING_DBID, parking.getDbId());
            startActivity(detailIntent);
        }

    }

    //tutorial
    private class MyListAdapter extends ArrayAdapter<ParkingModel> {

        private int layout;
        private ArrayList<ParkingModel> adapterParkings;
        private MyListAdapter(Context context, int resource, ArrayList<ParkingModel> objects) {
            super(context, resource, objects);
            adapterParkings = objects;
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layout, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.list_item_thumbnail);
            viewHolder.name = (TextView) convertView.findViewById(R.id.list_item_name);
            viewHolder.place = (TextView) convertView.findViewById(R.id.list_item_place);
            viewHolder.button = (ToggleButton) convertView.findViewById(R.id.list_item_btn);

            final ParkingModel parkingModel = adapterParkings.get(position);

            viewHolder.thumbnail.setImageResource(ParkingUtility.getIcon(parkingModel.getDbId(), parkingModel.getStatus()));
            viewHolder.name.setText(Html.fromHtml("<h2>"+parkingModel.getName()+"</h2>"));
            viewHolder.place.setText(parkingModel.getPlace());

            viewHolder.button.setTextOff(getString(R.string.toggleOff));
            viewHolder.button.setTextOn(getString(R.string.toggleOn));
            if (parkingModel.getStatus().equals("besetzt")){
                viewHolder.button.setChecked(true);
            }else{
                viewHolder.button.setChecked(false);
            }

            viewHolder.button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        myRef.child(parkingModel.getDbId()).child("Status").setValue("besetzt");
                    } else {
                        myRef.child(parkingModel.getDbId()).child("Status").setValue("frei");
                    }
                }
            });

            convertView.setTag(viewHolder);

            return convertView;
        }
    }

    //tutorial
    private class ViewHolder {
        ImageView thumbnail;
        TextView name;
        TextView place;
        ToggleButton button;
    }
}
