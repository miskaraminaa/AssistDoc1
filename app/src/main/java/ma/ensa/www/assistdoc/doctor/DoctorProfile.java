package ma.ensa.www.assistdoc.doctor;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import ma.ensa.www.assistdoc.R;

public class DoctorProfile extends AppCompatActivity {

    private EditText etPhone, etSpecialty, etUsername;
    private Button btnSaveProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_update_profile);

        // Initialisation des vues
        etPhone = findViewById(R.id.etPhone);
        etSpecialty = findViewById(R.id.etSpecialty);
        etUsername = findViewById(R.id.etUsername);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);


        // Charger le profil du docteur depuis Firestore
        loadDoctorProfile();

        // Action de sauvegarde
        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    // Méthode pour charger le profil du docteur depuis Firestore
    private void loadDoctorProfile() {
        // Récupérer l'ID du docteur (ici on suppose que l'ID est passé ou enregistré)
        String doctorId = "auzcbq0OqxZXiiagNh467nbRlJy2"; // Remplacer par l'ID réel du docteur

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("docteurs").document(doctorId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Récupérer les données depuis Firestore
                        String phone = documentSnapshot.getString("phone");
                        String specialty = documentSnapshot.getString("specialty");
                        String username = documentSnapshot.getString("username");

                        // Remplir les champs avec les données récupérées
                        etPhone.setText(phone);
                        etSpecialty.setText(specialty);
                        etUsername.setText(username);
                    } else {
                        Toast.makeText(DoctorProfile.this, "Profil non trouvé", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DoctorProfile.this, "Erreur de récupération des données", Toast.LENGTH_SHORT).show();
                });
    }

    // Méthode pour sauvegarder les modifications du profil
    private void saveProfile() {
        // Récupérer les données modifiées
        String updatedPhone = etPhone.getText().toString();
        String updatedSpecialty = etSpecialty.getText().toString();
        String updatedUsername = etUsername.getText().toString();

        // Récupérer l'ID du docteur
        String doctorId = "auzcbq0OqxZXiiagNh467nbRlJy2"; // Remplacer par l'ID réel du docteur

        // Enregistrer les nouvelles données dans Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("docteurs").document(doctorId)
                .update("phone", updatedPhone, "specialty", updatedSpecialty, "username", updatedUsername)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(DoctorProfile.this, "Profil mis à jour avec succès", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DoctorProfile.this, "Erreur lors de la mise à jour du profil", Toast.LENGTH_SHORT).show();
                });
    }
}
