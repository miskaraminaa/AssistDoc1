package ma.ensa.www.assistdoc.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;  // Correct import
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import ma.ensa.www.assistdoc.Chat_Activity;
import ma.ensa.www.assistdoc.R;

public class Doctors extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ImageView chatIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors);

        drawerLayout = findViewById(R.id.draw_activity);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        chatIcon = findViewById(R.id.chat_icon);

        // Set up the toolbar
        setSupportActionBar(toolbar);

        chatIcon.setOnClickListener(view ->
                startActivity(new Intent(Doctors.this, Chat_Activity.class))
        );

        // Récupérer l'ID de l'utilisateur actuellement authentifié
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("Doctors", "Current User ID: " + currentUserId);

        // Handle navigation item clicks
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Close the drawer after an item is selected
                drawerLayout.closeDrawer(GravityCompat.START);

                switch (item.getItemId()) {
                    case R.id.nav_home:
                        // startActivity(new Intent(Doctors.this, HomeActivity.class));
                        break;
                    case R.id.profile:
                        // startActivity(new Intent(Doctors.this, ProfileActivity.class));
                        break;
                    case R.id.slots:
                        // startActivity(new Intent(Doctors.this, SlotsActivity.class));
                        break;
                    case R.id.appointment:
                        // startActivity(new Intent(Doctors.this, AppointmentActivity.class));
                        break;
                    case R.id.chats:
                        startActivity(new Intent(Doctors.this, Chat_Activity.class));
                        break;
                    case R.id.settingsApp:
                        // startActivity(new Intent(Doctors.this, SettingsActivity.class));
                        break;
                    case R.id.logout:
                        // Handle logout logic (e.g., clear session or token)
                        startActivity(new Intent(Doctors.this, SignIn_Doctor.class));
                        finish();
                        break;
                    case R.id.website:
                        // Handle website redirection
                        break;
                    case R.id.facebook:
                        // Handle Facebook redirection
                        break;
                    case R.id.twitter:
                        // Handle Twitter redirection
                        break;
                    case R.id.linkedin:
                        // Handle LinkedIn redirection
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
