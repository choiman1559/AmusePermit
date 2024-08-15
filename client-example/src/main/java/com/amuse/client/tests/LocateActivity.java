package com.amuse.client.tests;

import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amuse.client.R;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.wrapper.locate.FusedLocationClient;
import com.amuse.permit.wrapper.locate.LocateModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Locale;

public class LocateActivity extends AppCompatActivity {

    TextView resultTextView;
    ArrayList<MaterialButton> apiButtons = new ArrayList<>();
    FusedLocationClient fusedLocationClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_locate);

        MaterialButton fetchBasicInfo = findViewById(R.id.fetchBasicInfo);
        resultTextView = findViewById(R.id.resultTextView);

        MaterialButton flushLocations = findViewById(R.id.flushLocations);
        MaterialButton getLastLocation = findViewById(R.id.getLastLocation);
        MaterialButton getCurrentLocation = findViewById(R.id.getCurrentLocation);
        MaterialButton getLocationAvailability = findViewById(R.id.getLocationAvailability);

        apiButtons.add(flushLocations);
        apiButtons.add(getLastLocation);
        apiButtons.add(getCurrentLocation);
        apiButtons.add(getLocationAvailability);
        setApiButtonsEnabled(false);

        flushLocations.setOnClickListener((v) -> postApiAction(fusedLocationClient.flushLocations()));
        getLastLocation.setOnClickListener((v) -> postApiAction(fusedLocationClient.getLastLocation()));
        getCurrentLocation.setOnClickListener((v) -> postApiAction(fusedLocationClient.getCurrentLocation(LocateModel.PRIORITY_HIGH_ACCURACY)));
        getLocationAvailability.setOnClickListener((v) -> postApiAction(fusedLocationClient.getLocationAvailability()));

        fetchBasicInfo.setOnClickListener((v) -> {
            fusedLocationClient = FusedLocationClient.getFusedLocationClient(this);
            setApiButtonsEnabled(true);
        });
    }

    void setApiButtonsEnabled(boolean enabled) {
        for(MaterialButton button : apiButtons) {
            button.setEnabled(enabled);
        }
    }

    void postApiAction(ResultTask<?> resultTask) {
        Toast.makeText(this, "Request Posted", Toast.LENGTH_SHORT).show();
        resultTask.setOnTaskCompleteListener(result -> {
            try {
                Object o = result.getResultData();
                if(o instanceof Location) {
                    Location location = (Location) o;
                    resultTextView.setText(String.format(Locale.getDefault(), "Latitude: %f, Longitude: %f", location.getLatitude(), location.getLongitude()));
                } else {
                    resultTextView.setText(new ObjectMapper().writeValueAsString(result));
                }

                if(result.hasException()) {
                    result.getException().printStackTrace();
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            if(result.isSuccess()) {
                Toast.makeText(this, "Request Succeed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Request Failed", Toast.LENGTH_SHORT).show();
            }
        }).invokeTask();
    }
}
