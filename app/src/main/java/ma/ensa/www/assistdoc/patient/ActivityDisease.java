package ma.ensa.www.assistdoc.patient;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import ma.ensa.www.assistdoc.R;

public class ActivityDisease extends AppCompatActivity {

    TextView head;
    TextView diseaseInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease);

        // Initialize the TextViews
        head = findViewById(R.id.card_head);
        diseaseInfo = findViewById(R.id.disease_info);

        // Retrieve data passed by the Intent
        String name = getIntent().getStringExtra("name");
        String gender = getIntent().getStringExtra("gender");

        // Retrieve the predicted disease(s) from the previous activity
        String predictedDisease = getIntent().getStringExtra("predicted_disease");

        // Set the header (add null checks for name and gender)
        String headerText = (gender != null && name != null) ? gender + " " + name + ", you may have:" : "Unknown user, you may have:";
        head.setText(headerText);

        // Create a StringBuilder to display the disease information
        StringBuilder probableDiseases = new StringBuilder();

        // If the predicted disease exists, display it
        if (predictedDisease != null && !predictedDisease.isEmpty()) {
            probableDiseases.append(predictedDisease)
                    .append(" - ")
                    .append("A condition that matches the symptoms you've entered. ")
                    .append("For more details and recommended treatments, please see below.\n")
                    .append("Learn more: ")
                    .append("https://www.google.com/search?q=")
                    .append(predictedDisease.replaceAll(" ", "+"))
                    .append("\n\n");
        } else {
            // If no disease is predicted, show a default message
            probableDiseases.append("No specific disease could be predicted. Please consult a doctor for a more accurate diagnosis.\n");
        }

        // Display the disease(s) information in the TextView
        diseaseInfo.setText(probableDiseases.toString());

        // Enable clickable links in the TextView
        diseaseInfo.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
