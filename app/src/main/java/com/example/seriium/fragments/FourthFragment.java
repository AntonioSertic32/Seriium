package com.example.seriium.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seriium.R;
import com.example.seriium.activities.Home;
import com.example.seriium.activities.SerieDetail;
import com.example.seriium.models.SerieSeason;
import com.example.seriium.models.User;
import com.example.seriium.models.UserSerie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;

import java.util.ArrayList;
import java.util.List;

public class FourthFragment extends Fragment {

    public FirebaseAuth mAuth;

    private ImageButton btnEditName;
    private ImageButton btnEditSurname;
    private Button btnChangeEmail;
    private Button btnVertificateEmail;
    private Button logout;
    private Button userResetPass;

    private String enteredName;
    private TextView userName;
    private String enteredSurname;
    private TextView userSurname;
    private TextView userEmail;

    public FourthFragment() {
    }

    public static FourthFragment newInstance(String param1, String param2) {
        FourthFragment fragment = new FourthFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fourth,container,false);

        mAuth = FirebaseAuth.getInstance();

        userName = view.findViewById(R.id.userName);
        userSurname = view.findViewById(R.id.userSurname);
        userEmail = view.findViewById(R.id.userEmail);
        userEmail.setText(mAuth.getCurrentUser().getEmail());

        LoadSeriesFirebase();

        logout = view.findViewById(R.id.userLogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), Home.class));
            }
        });

        btnEditName = view.findViewById(R.id.btnEditName);
        btnEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
                myAlertDialog.setTitle("Uredi ime");

                final EditText inputName = new EditText(getActivity());
                inputName.setInputType(InputType.TYPE_CLASS_TEXT);
                myAlertDialog.setView(inputName);
                myAlertDialog.setPositiveButton("Spremi", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        enteredName = inputName.getText().toString();

                        if(!enteredName.isEmpty()){
                            DatabaseReference serije = FirebaseDatabase.getInstance().getReference("korisnici/" + mAuth.getCurrentUser().getUid());
                            serije.child(String.valueOf("ime")).setValue(enteredName);
                            userName.setText(enteredName);
                        }

                        else{
                            Toast.makeText(getActivity(), "Polje mora biti popunjeno!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                myAlertDialog.setNegativeButton("Odustani", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    } });
                myAlertDialog.show();
            }
        });

        btnEditSurname = view.findViewById(R.id.btnEditSurname);
        btnEditSurname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
                myAlertDialog.setTitle("Uredi prezime");

                final EditText inputSurname = new EditText(getActivity());
                inputSurname.setInputType(InputType.TYPE_CLASS_TEXT);
                myAlertDialog.setView(inputSurname);
                myAlertDialog.setPositiveButton("Spremi", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        enteredSurname = inputSurname.getText().toString();

                        if(!enteredSurname.isEmpty()){
                            DatabaseReference serije = FirebaseDatabase.getInstance().getReference("korisnici/" + mAuth.getCurrentUser().getUid());
                            serije.child(String.valueOf("prezime")).setValue(enteredSurname);
                            userSurname.setText(enteredSurname);
                        }

                        else{
                            Toast.makeText(getActivity(), "Polje mora biti popunjeno!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                myAlertDialog.setNegativeButton("Odustani", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    } });
                myAlertDialog.show();
            }
        });

        btnChangeEmail = view.findViewById(R.id.btnChangeEmail);
        btnVertificateEmail = view.findViewById(R.id.btnVertificateEmail);
        userResetPass = view.findViewById(R.id.userResetPass);

        return view;
    }

    private void LoadSeriesFirebase(){
        FirebaseDatabase.getInstance().getReference("korisnici/" + mAuth.getCurrentUser().getUid() + "/" ).addListenerForSingleValueEvent(new ValueEventListener() {
            List<String> database = new ArrayList<>();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    database.add(snapshot.getValue().toString());
                }
                userName.setText(database.get(0));
                userSurname.setText(database.get(1));

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "" + databaseError, Toast.LENGTH_SHORT).show();
            }
        });
    }
}