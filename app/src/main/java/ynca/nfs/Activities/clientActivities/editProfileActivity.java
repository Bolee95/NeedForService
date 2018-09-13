package ynca.nfs.Activities.clientActivities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.util.ClientLibraryUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import ynca.nfs.Models.Client;
import ynca.nfs.Models.VehicleService;
import ynca.nfs.R;

public class editProfileActivity extends AppCompatActivity {


    private EditText updatedName;
    private EditText updatedSurname;
    private EditText updatedNumber;
    private EditText updatedEmail;
    private EditText updatedPassword;
    private Button submitButton;
    private Toolbar toolbar;

    private FirebaseDatabase mFirebaseStorage;
    private DatabaseReference mStorageReference;
    private ValueEventListener firebaseListener;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        updatedName = (EditText) findViewById(R.id.editName);
        updatedSurname = (EditText) findViewById(R.id.EditSurname);
        updatedEmail = (EditText) findViewById(R.id.EditEmail);
        updatedNumber = (EditText) findViewById(R.id.EditNumber);
        updatedPassword = (EditText) findViewById(R.id.EditPassword);
        submitButton = (Button) findViewById(R.id.ButtonEditSubmit);

        mFirebaseStorage = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //Toolbar podesavanja
        toolbar = (Toolbar) findViewById(R.id.editProfileToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_18dp);
        getSupportActionBar().setTitle("");


        mStorageReference = mFirebaseStorage.getReference().child("Korisnik").child("Client").child(user.getUid());

        firebaseListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Client temp = dataSnapshot.getValue(Client.class);

                updatedName.setText(temp.getFirstName());
                updatedEmail.setText(temp.getEmail());
                updatedSurname.setText(temp.getLastName());
                updatedNumber.setText(temp.getPhoneNumber());
                updatedPassword.setText("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mStorageReference.addValueEventListener(firebaseListener);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!updatedPassword.getText().equals(" "))
                {
                    //user.updatePassword(updatedPassword.getText().toString());
                }
                //user.updateEmail(updatedEmail.getText().toString());

                Map<String,Object> updatedData = new HashMap<>();
                updatedData.put("firstName",updatedName.getText().toString());
                updatedData.put("lastName",updatedSurname.getText().toString());
                updatedData.put("email",updatedEmail.getText().toString());
                updatedData.put("phoneNumber",updatedNumber.getText().toString());

                mStorageReference.updateChildren(updatedData);
                Toast.makeText(editProfileActivity.this, "User data edited successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.default_menu, menu);
        return true;
    }


}
