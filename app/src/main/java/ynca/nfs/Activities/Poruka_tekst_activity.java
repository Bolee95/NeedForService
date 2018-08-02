package ynca.nfs.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import ynca.nfs.Activities.clientActivities.Message_activity;
import ynca.nfs.Activities.mainScreensActivities.mainScreenClientActivity;
import ynca.nfs.Models.Klijent;
import ynca.nfs.Models.Poruka;
import ynca.nfs.R;

/**
 * Created by Nikola on 5/30/2017.
 */

public class Poruka_tekst_activity extends AppCompatActivity {
    Intent i;
    Poruka poruka;
    TextView posiljaocTV;
    TextView naslovTV;
    TextView tekstTV;
    TextView replyTV;



    static Klijent trenutniKlijent;
    static int BROJ_PORUKA;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sadrzaj_poruke);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.Black));

        Intent i = getIntent();
        boolean bilaProcitana = i.getBooleanExtra("porukaBilaProcitana", true);
        if (bilaProcitana)
            mainScreenClientActivity.dekrementirajBrojNeprocitanihPoruka();
        final Poruka poruka = (Poruka) i.getSerializableExtra("PorukaZaCitanje");
        BROJ_PORUKA = (int) i.getIntExtra("BROJ_PORUKA",-1);
        posiljaocTV = (TextView) findViewById(R.id.PosiljaocPorukeTV);
        naslovTV = (TextView) findViewById(R.id.NaslovPorukeTV);
        tekstTV = (TextView) findViewById(R.id.TekstPorukeTV);
        replyTV = (TextView) findViewById(R.id.SadrzajPorukeReplyTV);

        posiljaocTV.setText(poruka.getPosiljalac());
        naslovTV.setText(poruka.getNaslov());
        tekstTV.setText(poruka.getTekst());


        replyTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), Message_activity.class);
                i.putExtra("MSG_DST", poruka);
                i.putExtra("isReply", true);
                i.putExtra("BROJ_PORUKA", BROJ_PORUKA);
                startActivity(i);
                finish();
            }
        });






    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();

    }

    //    public void vratiKlijenta(FirebaseUser user, DatabaseReference database) {
//        String email = user.getEmail();
//        database.child("Korisnik").child("Klijent").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Klijent klijent = dataSnapshot.getValue(Klijent.class);
//                String ovdeNekiStringKojiCesVanDaDeklarises = klijent.getIme() + " " + klijent.getPrezime();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

//    public void imeServisa(FirebaseUser user, DatabaseReference database){
//        String email = user.getEmail();
//        database.child("Korisnik").child("Service").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Service servis = dataSnapshot.getValue(Service.class);
//                String ovdeNekiStringKojiCesVanDaDeklarises = servis.getNaziv();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
}
