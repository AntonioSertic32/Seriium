package com.example.seriium.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.seriium.fragments.FourthFragment;
import com.example.seriium.fragments.ThirdFragment;
import com.example.seriium.fragments.FirstFragment;
import com.example.seriium.R;
import com.example.seriium.fragments.SecondFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    public FirebaseUser user;

    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        BottomNavigationView nav = findViewById(R.id.navigationView);
        nav.setOnNavigationItemSelectedListener(this);

        loadFragment(new FirstFragment());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch(item.getItemId()){
            case R.id.firstFragment:
                fragment = new FirstFragment();
                break;
            case R.id.secondFragment:
                fragment = new SecondFragment();
                break;
            case R.id.thirdFragment:
                fragment = new ThirdFragment();
                break;
            case R.id.fourthFragment:
                fragment = new FourthFragment();
                break;
        }
        return loadFragment(fragment);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    private boolean loadFragment(Fragment fragment){
        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,fragment)
                    .commit();
            return true;
        }
        return false;
    }
}