package com.example.rober.gascalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private View.OnFocusChangeListener onFocusChangeListener;
    private View.OnClickListener onClickListener;

    private EditText distanceEditText;
    private EditText carFuelEconomyEditText;
    private EditText fuelPriceEditText;

    private  TextView litersTextView;
    private  TextView litersLabelTextView;
    private  TextView priceTextView;
    private  TextView priceLabelTextView;

    private float liters;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set Listeners
        onFocusChangeListener = new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                calculateLiters();
                calculatePrice();
            }
        };

        onClickListener =  new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                calculateLiters();
                calculatePrice();
            }
        };

        // Set SharePreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Find elements
        distanceEditText = (EditText) findViewById(R.id.distanceEditText);
        carFuelEconomyEditText = (EditText)findViewById(R.id.carFuelEconomyEditText);
        fuelPriceEditText = (EditText)findViewById(R.id.fuelPriceEditText);

        litersTextView = (TextView)findViewById(R.id.litersTextView);
        litersLabelTextView = (TextView)findViewById(R.id.litersLabelTextView);
        priceTextView = (TextView)findViewById(R.id.priceTextView);
        priceLabelTextView = (TextView)findViewById(R.id.priceLabelTextView);

        // Setting events to EditTexts
        distanceEditText.setOnFocusChangeListener(onFocusChangeListener);
        carFuelEconomyEditText.setOnFocusChangeListener(onFocusChangeListener);
        fuelPriceEditText.setOnFocusChangeListener(onFocusChangeListener);
        litersLabelTextView.setOnClickListener(onClickListener);
        priceLabelTextView.setOnClickListener(onClickListener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.saveLastData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setLasData();
    }

    private void calculateLiters(){

        // List of EditText require to calculate
        List<EditText> requiredEditText = new ArrayList<>(Arrays.asList(
                                            distanceEditText,
                                            carFuelEconomyEditText));

        // Checks if can calculate
        if (!this.ValidateRequiredEdits(requiredEditText)){
            return;
        }

        // Get EditText values
        float distance = Float.parseFloat(distanceEditText.getText().toString());
        float performance = Float.parseFloat(carFuelEconomyEditText.getText().toString());

        // Calculate liters
        this.liters = distance / performance;

        // Round result to 2 decimals
        DecimalFormat df = new DecimalFormat("#.##");

        // Set value to litersTextView
        litersTextView.setText(df.format(this.liters));
    }

    private void calculatePrice(){

        // List of EditText require to calculate
        List<EditText> requiredEditText = new ArrayList<>();
        requiredEditText.add(fuelPriceEditText);

        // Checks if can calculate
        if (!this.ValidateRequiredEdits(requiredEditText)){
            return;
        }

        // Get EditText values
        float fuelPrice = Float.parseFloat(fuelPriceEditText.getText().toString());

        // Calculate price
        float price = this.liters * fuelPrice;

        // Round result to 2 decimals
        DecimalFormat df = new DecimalFormat("#.##");

        // Set value to litersTextView
        priceTextView.setText("$" + df.format(price));
    }

    private boolean ValidateRequiredEdits(List<EditText> requiredEditTexts){

        try {

            for (EditText item: requiredEditTexts) {
                Float.parseFloat(item.getText().toString());
            }

        }catch (NullPointerException nullPointerE){
            return false;
        }catch (NumberFormatException formatPointerE){
            return false;
        }

        return true;
    }

    private void saveLastData(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastData", getEditTextsSerialized());
        editor.apply();
    }

    private void setLasData(){
        // Get the preference value
        String lastData = sharedPreferences.getString("lastData", "");

        if (lastData.equals("")){
            return;
        }

        // List of EditText
        List<EditText> editTexts = new ArrayList<>(Arrays.asList(
                distanceEditText,
                carFuelEconomyEditText,
                fuelPriceEditText));

        for(int i = 0 ; i < editTexts.size(); i++){
            editTexts.get(i).setText(lastData.split(";")[i]);
        }
    }

    private String getEditTextsSerialized(){

        String data = "";

        // List of EditText
        List<EditText> editTexts = new ArrayList<>(Arrays.asList(
                distanceEditText,
                carFuelEconomyEditText,
                fuelPriceEditText));

        for (EditText item: editTexts) {
            data += item.getText().toString() + ";";
        }

        return data;
    }
}
