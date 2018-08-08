package tasu.myapplication;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;


public class DrawerActivity extends AppCompatActivity {

    CheckBox ch1 ,ch2, ch3, ch4, ch5, ch6, ch7;
    EditText eRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkbox_layout);


        ch1 = (CheckBox)findViewById(R.id.checkBoxDoctor);
        ch2 = (CheckBox)findViewById(R.id.checkBoxHospital);
        ch3 = (CheckBox)findViewById(R.id.checkBoxRestaurant);
        ch4 = (CheckBox)findViewById(R.id.checkBoxMuseum);
        ch5 = (CheckBox)findViewById(R.id.checkBoxStadium);
        ch6 = (CheckBox)findViewById(R.id.checkBoxLibrary);
        ch7 = (CheckBox)findViewById(R.id.checkBoxZoo);


        Button buttonOk = (Button)findViewById(R.id.btnOk);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();

                editor.putBoolean("c1", ch1.isChecked());
                editor.putBoolean("c2", ch2.isChecked());
                editor.putBoolean("c3", ch3.isChecked());
                editor.putBoolean("c4", ch4.isChecked());
                editor.putBoolean("c5", ch5.isChecked());
                editor.putBoolean("c6", ch6.isChecked());
                editor.putBoolean("c7", ch7.isChecked());

                eRange = (EditText)findViewById(R.id.editRange);
                String range = eRange.getText().toString();

                editor.putString("range", range);
                editor.commit();

                finish();

                //Intent intent1 = new Intent(v.getContext(), MapsActivity.class);
                //startActivity(intent1);

            }
        });
    }

}
