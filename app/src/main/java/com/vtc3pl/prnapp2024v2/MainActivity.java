package com.vtc3pl.prnapp2024v2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText userNameEditText, passwordEditText;
    private Spinner spinnerDepo, spinnerYear;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        userNameEditText = findViewById(R.id.userName);
        passwordEditText = findViewById(R.id.userPassword);
        spinnerDepo = findViewById(R.id.spinnerDepo);
        spinnerYear = findViewById(R.id.spinnerYear);
        loginButton = findViewById(R.id.loginButton);

        ArrayAdapter<String> depoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Please Select Depo", "PNA", "NSK", "AKL", "AUR", "SHV", "KOP", "SGN", "NAG", "BRS", "JBL", "PDR", "ISL", "SOL", "SGL", "URL", "ANK", "ASL", "BEL", "BNH", "BRD", "HYD", "IND", "NAG", "JNPT", "TRI", "OZAR", "JLN", "STN", "NAN", "PBN", "AKJ", "BIJ", "KLG", "WGL", "LCK", "JAI", "PCV", "GZB", "BWD"});
        depoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set ArrayAdapter to spinnerDepo
        spinnerDepo.setAdapter(depoAdapter);

        // Set item selected listener for spinnerDepo
        spinnerDepo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle item selection
                String selectedDepo = (String) parent.getItemAtPosition(position);
                Toast.makeText(MainActivity.this, "Selected Depo: " + selectedDepo, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Please Select Year", "2122", "2223", "2324", "2425", "2526", "2728", "2829", "2930"});
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set ArrayAdapter to spinnerYear
        spinnerYear.setAdapter(yearAdapter);

        // Set item selected listener for spinnerYear
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle item selection
                String selectedYear = (String) parent.getItemAtPosition(position);
                Toast.makeText(MainActivity.this, "Selected year: " + selectedYear, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate input fields
                if (validateInputs()) {
                    // Perform login
                    performLogin();
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private boolean validateInputs() {
        String username = userNameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String depo = spinnerDepo.getSelectedItem().toString();
        String year = spinnerYear.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty() || depo.equals("Select an option") || year.equals("Select an option")) {
            Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void performLogin() {
        // Construct the URL for the login API endpoint
        String loginUrl = "https://vtc3pl.com/attloginprn.php";

        // Get inputs
        String username = userNameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String depo = spinnerDepo.getSelectedItem().toString();
        String year = spinnerYear.getSelectedItem().toString();

        // Prepare the request body
        RequestBody formBody = new FormBody.Builder().add("user_name", username).add("password", password).build();

        // Create the request
        Request request = new Request.Builder().url(loginUrl).post(formBody).build();

        // Create OkHttpClient instance
        OkHttpClient client = new OkHttpClient();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                Log.d("Response Data : ", responseData);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (responseData.equals("Success")) {
                            Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Login failed: " + responseData, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }
}