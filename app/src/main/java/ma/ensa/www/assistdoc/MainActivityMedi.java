package ma.ensa.www.assistdoc;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Calendar;
import java.util.List;

import ma.ensa.www.assistdoc.entities.Medicament;
import ma.ensa.www.assistdoc.model.MedicationReminderReceiver;

public class MainActivityMedi extends AppCompatActivity {
    private EditText editNom, editDosage, editFrequence, editHeurePris;
    private Button buttonAdd, buttonViewMeds;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_medi);

        // Initialiser les champs
        editNom = findViewById(R.id.editNom);
        editDosage = findViewById(R.id.editDosage);
        editFrequence = findViewById(R.id.editFrequence);
        editHeurePris = findViewById(R.id.editHeurePris);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonViewMeds = findViewById(R.id.buttonViewMeds);

        // Initialisation de Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Ajouter un médicament
        buttonAdd.setOnClickListener(view -> {
            String nom = editNom.getText().toString();
            String dosage = editDosage.getText().toString();
            String frequence = editFrequence.getText().toString();
            String heurePris = editHeurePris.getText().toString();

            if (nom.isEmpty() || dosage.isEmpty() || frequence.isEmpty() || heurePris.isEmpty()) {
                Toast.makeText(MainActivityMedi.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            Medicament medicament = new Medicament();
            medicament.setNom(nom);
            medicament.setDosage(dosage);
            medicament.setFrequence(frequence);
            medicament.setHeurePris(heurePris);

            // Vérifier si l'utilisateur est authentifié
            if (mAuth.getCurrentUser() != null) {
                String userId = mAuth.getCurrentUser().getUid();

                // Ajouter le médicament à Firestore sous la collection de l'utilisateur
                db.collection("Users")
                        .document(userId)
                        .collection("medications")
                        .add(medicament)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(MainActivityMedi.this, "Médicament ajouté à Firebase!", Toast.LENGTH_SHORT).show();
                            loadMedications(); // Optionnel: Charger les médicaments après ajout
                            scheduleMedicationReminders(medicament); // Planification des rappels
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(MainActivityMedi.this, "Erreur lors de l'ajout du médicament", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(MainActivityMedi.this, "Veuillez vous connecter pour ajouter des médicaments.", Toast.LENGTH_SHORT).show();
            }
        });

        // Voir les médicaments
        buttonViewMeds.setOnClickListener(view -> {
            Intent intent = new Intent(this, MedicationListActivity.class);
            startActivity(intent);
        });

        // Charger les médicaments au démarrage
        loadMedications();
    }

    private void loadMedications() {
        // Vérifier si l'utilisateur est authentifié
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            // Charger les médicaments depuis Firebase
            db.collection("Users")
                    .document(userId)
                    .collection("medications")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Medicament> medications = queryDocumentSnapshots.toObjects(Medicament.class);
                        // Mettez à jour l'adaptateur ou affichez les médicaments ici
                        Log.d("MainActivity", "Nombre de médicaments : " + medications.size());
                        for (Medicament medicament : medications) {
                            Log.d("MainActivity", "Médicament: " + medicament.getNom());
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("MainActivity", "Erreur lors du chargement des médicaments", e);
                    });
        } else {
            Log.e("MainActivity", "Utilisateur non authentifié.");
        }
    }

    private void scheduleMedicationReminders(Medicament medicament) {
        String[] times = medicament.getHeurePris().split(",");
        for (String time : times) {
            String formattedTime = time.trim();
            String[] parts = formattedTime.split(":");
            if (parts.length == 2) {
                try {
                    int hour = Integer.parseInt(parts[0]);
                    int minute = Integer.parseInt(parts[1]);

                    if (hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59) {
                        scheduleMedicationReminder(hour, minute, medicament.getNom());
                    }
                } catch (NumberFormatException e) {
                    Log.e("MainActivity", "Erreur de format pour l'heure: " + e.getMessage());
                }
            } else {
                Log.e("MainActivity", "Format d'heure invalide pour " + medicament.getNom() + ": " + time);
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleMedicationReminder(int hour, int minute, String medicamentName) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MedicationReminderReceiver.class);
        intent.putExtra("medicament_name", medicamentName);
        intent.putExtra("notification_type", "reminder");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                medicamentName.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Log.d("MainActivity", "Rappel programmé pour " + medicamentName + " à " + calendar.getTime());
    }
}