package com.example.bloodsync;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.bloodsync.profile.ProfileActivity;

public class AwareActivity extends AppCompatActivity {

    private static final String USER_TYPE_PREF = "user_type";
    private static final String USER_PREFS = "user_prefs";
    private static final String TYPE_DONOR = "donor";
    private static final String TYPE_SEEKER = "seeker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aware);

        // Set up back button functionality
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> finish());

        // Set up home icon functionality
        LinearLayout homeTab = findViewById(R.id.homeTab);
        if (homeTab != null) {
            homeTab.setOnClickListener(v -> {
                navigateToHomeBasedOnUserType();
            });
        }

        LinearLayout profileTab = findViewById(R.id.profileTab);
        if (profileTab != null){
            profileTab.setOnClickListener(v -> {
                startActivity(new Intent(this, ProfileActivity.class));
            });
        }
    }

    private void navigateToHomeBasedOnUserType() {
        // First check SharedPreferences for cached user type
        SharedPreferences prefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        String cachedUserType = prefs.getString(USER_TYPE_PREF, null);

        if (cachedUserType != null) {
            // We have cached user type, use it for navigation
            navigateBasedOnUserType(cachedUserType);
        } else {
            // No cached data, check Firebase
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                // Get user type from Firebase
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(currentUser.getUid());
                
                userRef.child("userType").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String userType = dataSnapshot.getValue(String.class);
                            // Cache the user type for future use
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(USER_TYPE_PREF, userType);
                            editor.apply();
                            
                            navigateBasedOnUserType(userType);
                        } else {
                            // Default to MainActivityC if user type not found
                            startMainActivity();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Default to MainActivityC on error
                        startMainActivity();
                    }
                });
            } else {
                // User not logged in, go to MainActivityC
                startMainActivity();
            }
        }
    }

    private void navigateBasedOnUserType(String userType) {
        Intent intent;
        if (TYPE_DONOR.equalsIgnoreCase(userType)) {
            // Navigate to donor home
            intent = new Intent(AwareActivity.this, MainActivity.class);
        } else {
            // Default to MainActivityC
            intent = new Intent(AwareActivity.this, MainActivityC.class);
        }
        
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void startMainActivity() {
        Intent intent = new Intent(AwareActivity.this, MainActivityC.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}