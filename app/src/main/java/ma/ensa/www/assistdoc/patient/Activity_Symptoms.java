package ma.ensa.www.assistdoc.patient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import ma.ensa.www.assistdoc.R;
import ma.ensa.www.assistdoc.ml.Model;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Activity_Symptoms extends AppCompatActivity {

    Button dis;
    MultiSelectSpinner symptomSpinner;
    EditText symptomsEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms);

        dis = findViewById(R.id.disease);
        symptomSpinner = findViewById(R.id.symptomSpinner);
        symptomsEditText = findViewById(R.id.symptomsEditText);

        // Charger la liste des symptômes depuis array.symptom_options
        List<String> symptomsList = getSymptomsList();

        // Set the list of symptoms to the MultiSelectSpinner
        symptomSpinner.setItems(symptomsList);

        // Set up the disease prediction button
        final String name = getIntent().getStringExtra("name");
        final String gender = getIntent().getStringExtra("gender");

        dis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve all selected symptoms from the MultiSelectSpinner
                List<Integer> selectedItems = symptomSpinner.getSelectedItems(); // Get selected symptoms' indexes

                // Create an array to store the symptoms
                String[] symptoms = new String[132]; // Assuming there are 132 symptoms

                // Update the symptoms array with the selected symptoms
                for (int i = 0; i < 132; i++) {
                    if (selectedItems.contains(i)) {
                        symptoms[i] = "Yes"; // If selected, mark as "Yes"
                    } else {
                        symptoms[i] = "No";  // If not selected, mark as "No"
                    }
                }
                Log.d("Symptoms", Arrays.toString(symptoms));

                // Convert the symptoms to a format usable by the model
                try {
                    Model model = Model.newInstance(getApplicationContext());

                    // Create a TensorBuffer with the symptom data
                    ByteBuffer byteBuffer = convertSymptomsToByteBuffer(symptoms);
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 132}, DataType.FLOAT32);
                    inputFeature0.loadBuffer(byteBuffer);

                    // Perform inference with the model
                    Model.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    // Release the model resources
                    model.close();

                    // Retrieve the model's output
                    float[] outputValues = outputFeature0.getFloatArray();

                    // Process the output to determine the most probable disease
                    String predictedDisease = getPredictedDisease(outputValues);

                    // Pass the results to the next activity
                    Intent dis_page = new Intent(Activity_Symptoms.this, ActivityDisease.class);
                    dis_page.putExtra("name", name);
                    dis_page.putExtra("gender", gender);
                    dis_page.putExtra("predictedDisease", predictedDisease);
                    startActivity(dis_page);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(Activity_Symptoms.this, "Error with the model.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to load symptoms from array.symptom_options
    private List<String> getSymptomsList() {
        List<String> symptomsList = new ArrayList<>();
        String[] symptoms = getResources().getStringArray(R.array.symptom_options); // Assuming symptoms are stored in strings.xml

        for (String symptom : symptoms) {
            symptomsList.add(symptom);
        }

        return symptomsList;
    }

    private ByteBuffer convertSymptomsToByteBuffer(String[] symptoms) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(132 * 4);  // Allocate memory for 132 features (based on your model)

        for (String symptom : symptoms) {
            byteBuffer.putFloat(mapSymptomToFeatureValue(symptom));  // Convert each symptom to a numerical value
        }

        return byteBuffer;
    }

    private float mapSymptomToFeatureValue(String symptom) {
        return symptom.equals("Yes") ? 1.0f : 0.0f;
    }

    private String getPredictedDisease(float[] outputValues) {
        int maxIndex = 0;
        for (int i = 1; i < outputValues.length; i++) {
            if (outputValues[i] > outputValues[maxIndex]) {
                maxIndex = i;
            }
        }

        String[] diseases = {
                "Fungal infection", "Allergy", "GERD", "Chronic cholestasis", "Drug Reaction",
                "Peptic ulcer disease", "AIDS", "Diabetes", "Gastroenteritis", "Bronchial Asthma",
                "Hypertension", "Migraine", "Cervical spondylosis", "Paralysis (brain hemorrhage)",
                "Jaundice", "Malaria", "Chicken pox", "Dengue", "Typhoid", "Hepatitis A",
                "Hepatitis B", "Hepatitis C", "Hepatitis D", "Hepatitis E", "Alcoholic hepatitis",
                "Tuberculosis", "Common Cold", "Pneumonia", "Dimorphic hemorrhoids (piles)", "Heart attack",
                "Varicose veins", "Hypothyroidism", "Hyperthyroidism", "Hypoglycemia", "Osteoarthritis",
                "Arthritis", "(vertigo) Paroxysmal Positional Vertigo", "Acne", "Urinary tract infection",
                "Psoriasis", "Impetigo"
        };

        return diseases[maxIndex];
    }
}
