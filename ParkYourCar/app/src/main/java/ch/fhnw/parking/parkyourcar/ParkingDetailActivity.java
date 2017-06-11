package ch.fhnw.parking.parkyourcar;

import android.content.Intent;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ParkingDetailActivity extends AppCompatActivity {

    TextView viewName;
    TextView viewStatus;
    TextView viewPos;
    ToggleButton toggle;
    ImageView viewIcon;

    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_detail);

        viewName = (TextView) findViewById(R.id.textName);
        viewStatus = (TextView) findViewById(R.id.textOccupation);
        viewPos = (TextView) findViewById(R.id.textPlace);
        viewIcon = (ImageView) findViewById(R.id.imageParking);

        Intent intent = getIntent();
        String name = intent.getStringExtra(MainActivity.PARKING_NAME);
        final String status = intent.getStringExtra(MainActivity.PARKING_STATUS);
        String place = intent.getStringExtra(MainActivity.PARKING_PLACE);
        final String dbId = intent.getStringExtra(MainActivity.PARKING_DBID);
        viewName.setText(Html.fromHtml("<h2>"+name+"</h2>"));
        viewStatus.setText(status);
        viewPos.setText(place);
        viewIcon.setImageResource(ParkingUtility.getIcon(dbId, status));


        toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setTextOff(getString(R.string.toggleOff));
        toggle.setTextOn(getString(R.string.toggleOn));
        if (status.equals("besetzt")){
            toggle.setChecked(true);
        }else{
            toggle.setChecked(false);
        }

       FirebaseDatabase database = FirebaseDatabase.getInstance();
       myRef = database.getReference("parkings");
       toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if (isChecked) {
                   myRef.child(dbId).child("Status").setValue("besetzt");
                   viewStatus.setText("besetzt");
                   viewIcon.setImageResource(ParkingUtility.getIcon(dbId, "besetzt"));
               } else {
                   myRef.child(dbId).child("Status").setValue("frei");
                   viewStatus.setText("frei");
                   viewIcon.setImageResource(ParkingUtility.getIcon(dbId, "frei"));
               }

           }
       });


    }
}
