package com.vtc3pl.prnapp2024v2;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity6 extends AppCompatActivity {

    private EditText editTextFromDateActivitySix, editTextToDateActivitySix, prnNumberEditText;
    private TextView textViewFromDateActivitySix, textViewToDateActivitySix, prnNumberTextView;
    private Calendar fromCalendar, toCalendar;
    private DatePickerDialog.OnDateSetListener fromDateSetListener, toDateSetListener;
    private Button searchButton;
    private String selectedRadioButton = "", username = "", depo = "", year = "";

    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main6);

        textViewFromDateActivitySix = findViewById(R.id.textViewFromDateActivitySix);
        editTextFromDateActivitySix = findViewById(R.id.editTextFromDateActivitySix);

        textViewToDateActivitySix = findViewById(R.id.textViewToDateActivitySix);
        editTextToDateActivitySix = findViewById(R.id.editTextToDateActivitySix);

        prnNumberTextView = findViewById(R.id.prnNumberTextView);
        prnNumberEditText = findViewById(R.id.prnNumberEditText);

        searchButton = findViewById(R.id.searchButton);

        tableLayout = findViewById(R.id.tableLayoutActivitySix);

        fromCalendar = Calendar.getInstance();
        toCalendar = Calendar.getInstance();

        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
            depo = intent.getStringExtra("depo");
            year = intent.getStringExtra("year");
        }

        fromDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                fromCalendar.set(Calendar.YEAR, year);
                fromCalendar.set(Calendar.MONTH, month);
                fromCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateFromDate();
            }
        };

        toDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                toCalendar.set(Calendar.YEAR, year);
                toCalendar.set(Calendar.MONTH, month);
                toCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateToDate();
            }
        };

        setupRadioButtons();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpClient client = new OkHttpClient();

                String url = "https://vtc3pl.com/arrival_prn_search_prn_app.php";
                String fromDate = editTextFromDateActivitySix.getText().toString().trim();
                String toDate = editTextToDateActivitySix.getText().toString().trim();
                String prnNumber = prnNumberEditText.getText().toString().trim();

                Log.e("usernme", username);
                Log.e("depo", depo);
                Log.e("year", year);
                Log.e("Selected ", selectedRadioButton);

                FormBody.Builder formBuilder = new FormBody.Builder();
                formBuilder.add("username", username);
                formBuilder.add("depo", depo);
                formBuilder.add("year", year);
                formBuilder.add("selectedRadioButton", selectedRadioButton);
                if (selectedRadioButton.equals("radioButton1")) {
                    formBuilder.add("fromDate", fromDate);
                    formBuilder.add("toDate", toDate);
                } else if (selectedRadioButton.equals("radioButton2")) {
                    formBuilder.add("prnNumber", prnNumber);
                }

                Request request = new Request.Builder().url(url).post(formBuilder.build()).build();

                // Make the request asynchronously
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e("onFailure", String.valueOf(e));
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            // Parse the response and display it in a table format
                            String responseData = response.body().string();
                            try {
                                JSONArray jsonArray = new JSONArray(responseData);
                                Log.e("response on success", String.valueOf(jsonArray));
                                // Process JSON array and display data in table format
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Process JSON array and display data in table format
                                        displayDataInTable(jsonArray);
                                    }
                                });
                            } catch (JSONException e) {
                                Log.e("onResponse Expetion : ", String.valueOf(e));
                                Log.d("Response : ", responseData);
                                e.printStackTrace();
                            }
                        } else {
                            // Handle unsuccessful response
                            Log.e("Error", "Unsuccessful response: " + response);
                        }
                    }
                });
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void showDatePickerDialogFromDate(View v) {
        new DatePickerDialog(this, fromDateSetListener, fromCalendar.get(Calendar.YEAR), fromCalendar.get(Calendar.MONTH), fromCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void showDatePickerDialogToDate(View v) {
        new DatePickerDialog(this, toDateSetListener, toCalendar.get(Calendar.YEAR), toCalendar.get(Calendar.MONTH), toCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateFromDate() {
        String dateFormat = "yyyy/MM/dd";
        editTextFromDateActivitySix.setText(android.text.format.DateFormat.format(dateFormat, fromCalendar));
    }

    private void updateToDate() {
        String dateFormat = "yyyy/MM/dd";
        editTextToDateActivitySix.setText(android.text.format.DateFormat.format(dateFormat, toCalendar));
    }

    private void setupRadioButtons() {
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        RadioButton radioButtonDate = findViewById(R.id.radioButtonDate);
        RadioButton radioButtonPRN = findViewById(R.id.radioButtonPRN);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == radioButtonDate.getId()) {
                showDateRangeViews();
                adjustSearchButtonPosition(editTextToDateActivitySix);
                selectedRadioButton = "radioButton1";
                Log.d("If RadioButton Value:", selectedRadioButton + " , checkId = " + checkedId);
            } else if (checkedId == radioButtonPRN.getId()) {
                showPRNViews();
                adjustSearchButtonPosition(prnNumberEditText);
                selectedRadioButton = "radioButton2";
                Log.d("else RadioButton Value:", selectedRadioButton + " , checkId = " + checkedId);
            }
        });
    }

    //https://vtc3pl.com/arrival_prn_search_prn_app.php
    private void showDateRangeViews() {
        textViewFromDateActivitySix.setVisibility(View.VISIBLE);
        editTextFromDateActivitySix.setVisibility(View.VISIBLE);
        textViewToDateActivitySix.setVisibility(View.VISIBLE);
        editTextToDateActivitySix.setVisibility(View.VISIBLE);

        prnNumberTextView.setVisibility(View.GONE);
        prnNumberEditText.setVisibility(View.GONE);
    }

    private void showPRNViews() {
        textViewFromDateActivitySix.setVisibility(View.GONE);
        editTextFromDateActivitySix.setVisibility(View.GONE);
        textViewToDateActivitySix.setVisibility(View.GONE);
        editTextToDateActivitySix.setVisibility(View.GONE);

        prnNumberTextView.setVisibility(View.VISIBLE);
        prnNumberEditText.setVisibility(View.VISIBLE);
    }

    private void adjustSearchButtonPosition(View anchorView) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) searchButton.getLayoutParams();
        params.topToBottom = anchorView.getId();
        searchButton.setLayoutParams(params);
        searchButton.setVisibility(View.VISIBLE);
    }

    private void displayDataInTable(JSONArray jsonArray) {
        // Clear existing table rows
        tableLayout.removeAllViews();

        // Create table headers
        TableRow headerRow = new TableRow(MainActivity6.this);
        headerRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        TextView srNoHeader = new TextView(MainActivity6.this);
        srNoHeader.setText("SrNo");
        headerRow.addView(srNoHeader);

        TextView prnNoHeader = new TextView(MainActivity6.this);
        prnNoHeader.setText("PRN No");
        headerRow.addView(prnNoHeader);

        TextView prnDateHeader = new TextView(MainActivity6.this);
        prnDateHeader.setText("PRN Date");
        headerRow.addView(prnDateHeader);

        TextView vehicleNoHeader = new TextView(MainActivity6.this);
        vehicleNoHeader.setText("Vehicle No");
        headerRow.addView(vehicleNoHeader);

        TextView locationHeader = new TextView(MainActivity6.this);
        locationHeader.setText("Location");
        headerRow.addView(locationHeader);

        TextView updateStockHeader = new TextView(MainActivity6.this);
        updateStockHeader.setText("Update Stock");
        headerRow.addView(updateStockHeader);

        tableLayout.addView(headerRow);

        // Iterate through JSON array and add rows to the table
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String prnId = jsonObject.getString("PRNId");
                String avDate = jsonObject.getString("AVDate");
                String vehicleNo = jsonObject.getString("VehicleNo");
                String depo = jsonObject.getString("Depo");

                TableRow row = new TableRow(MainActivity6.this);
                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                TextView srNo = new TextView(MainActivity6.this);
                srNo.setText(String.valueOf(i + 1));
                row.addView(srNo);

                TextView prnNo = new TextView(MainActivity6.this);
                prnNo.setText(prnId);
                row.addView(prnNo);

                TextView prnDate = new TextView(MainActivity6.this);
                prnDate.setText(avDate);
                row.addView(prnDate);

                TextView vehicle = new TextView(MainActivity6.this);
                vehicle.setText(vehicleNo);
                row.addView(vehicle);

                TextView location = new TextView(MainActivity6.this);
                location.setText(depo);
                row.addView(location);

                Button updateButton = new Button(MainActivity6.this);
                updateButton.setText("Update");
                // Add onClick listener for update button if needed

                row.addView(updateButton);

                tableLayout.addView(row);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("tableCreation Excep: ", String.valueOf(e));
            }
        }
    }


}