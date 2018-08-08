package tasu.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;



public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        String placeName = getIntent().getExtras().getString("name");
        String placeVicinity = getIntent().getExtras().getString("vicinity");
        final Double placeLatitude = getIntent().getExtras().getDouble("latitude");
        final Double placeLongitude = getIntent().getExtras().getDouble("longitude");

        TextView tvName = (TextView)findViewById(R.id.textViewName);
        TextView tvVicinity = (TextView)findViewById(R.id.textViewVicinity);
        TextView tvPosition = (TextView)findViewById(R.id.textViewPosition);
        TextView tvStreet = (TextView)findViewById(R.id.textViewStreet);

        tvName.setText(placeName);
        tvVicinity.setText(placeVicinity);
        tvPosition.setText("Latitude: " + placeLatitude + "\nLongitude: " + placeLongitude);

        tvStreet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent street = new Intent(getApplicationContext(), StreetActivity.class);
                street.putExtra("latitude", placeLatitude);
                street.putExtra("longitude", placeLongitude);
                startActivity(street);
            }
        });

    }

}
