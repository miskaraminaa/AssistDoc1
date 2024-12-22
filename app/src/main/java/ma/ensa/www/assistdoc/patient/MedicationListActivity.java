package ma.ensa.www.assistdoc.patient;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ma.ensa.www.assistdoc.R;
import ma.ensa.www.assistdoc.adapter.MedicamentAdapter; // Correct import
import ma.ensa.www.assistdoc.entities.Medicament;

public class MedicationListActivity extends AppCompatActivity {

    private ListView listViewMedications;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_list);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("medications");

        listViewMedications = findViewById(R.id.listViewMedications);

        loadMedications();
    }

    private void loadMedications() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            dbRef.child(userId).get()
                    .addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            HashMap<String, Object> medicationsMap = (HashMap<String, Object>) dataSnapshot.getValue();
                            if (medicationsMap != null) {
                                List<Medicament> medications = new ArrayList<>();
                                for (Map.Entry<String, Object> entry : medicationsMap.entrySet()) {
                                    Medicament medicament = convertToMedicament(entry.getValue());
                                    medications.add(medicament);
                                }

                                if (!medications.isEmpty()) {
                                    MedicamentAdapter adapter = new MedicamentAdapter(MedicationListActivity.this, medications);
                                    listViewMedications.setAdapter(adapter);
                                } else {
                                    Toast.makeText(MedicationListActivity.this, "Aucun médicament trouvé", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("MedicationListActivity", "Erreur lors du chargement des médicaments", e);
                        Toast.makeText(MedicationListActivity.this, "Erreur lors du chargement des médicaments", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.e("MedicationListActivity", "Utilisateur non authentifié.");
            Toast.makeText(MedicationListActivity.this, "Veuillez vous connecter pour voir les médicaments.", Toast.LENGTH_SHORT).show();
        }
    }

    private Medicament convertToMedicament(Object value) {
        HashMap<String, Object> medicamentMap = (HashMap<String, Object>) value;

        String nom = (String) medicamentMap.get("nom");
        String dosage = (String) medicamentMap.get("dosage");
        String frequence = (String) medicamentMap.get("frequence");
        String heurePris = (String) medicamentMap.get("heurePris");

        return new Medicament(nom, dosage, frequence, heurePris);
    }
}
