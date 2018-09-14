package ynca.nfs.Activities.clientActivities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ynca.nfs.Models.VehicleService;
import ynca.nfs.R;
import ynca.nfs.Models.Review;

public class FeedbackActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    static  String uidOfReviewedService;

    EditText comment;
    RatingBar rating;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_activity);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Korisnik")
            .child("VehicleService");

        uidOfReviewedService = getIntent().getStringExtra("uidOfReviewedService");

        comment = (EditText) findViewById(R.id.recenzijaEDIT);
        rating =  (RatingBar) findViewById(R.id.ratingBar);
        submit =  (Button) findViewById(R.id.recenzijaSUBMIT);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String  commentary = comment.getText().toString();
                float rate = rating.getRating();
                Review review = new Review(mUser.getEmail(), commentary, rate);
                mDatabaseReference.child(uidOfReviewedService).child("reviews")
                        .push().setValue(review);
                Toast.makeText(FeedbackActivity.this, "Succeed!", Toast.LENGTH_LONG);
                finish();
            }
        });

    }

}
