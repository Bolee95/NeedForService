package ynca.nfs.Activities.clientActivities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.util.regex.Pattern;

import ynca.nfs.Activities.startActivities.LoginActivity;
import ynca.nfs.Activities.mainScreensActivities.mainScreenClientActivity;
import ynca.nfs.Models.Klijent;
import ynca.nfs.R;

public class Info_client extends Activity {
    private Button listCars;
    private EditText EmailClientET;
    private EditText PasswordClient;
    private EditText PhoneNum;
    private Button SaveBut;
    private EditText User;
    private ImageView mProfilePicture;
    private ProgressDialog mProgressDialog;

    //ne pipaj
    private static final int RC_PHOTO_PICKER =  2;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;

    private FirebaseAuth mAuth;

    private boolean isNameChanged;
    private boolean isEmailChanged;
    private boolean isPasswordChanged;
    private boolean isNumberChanged;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_client);


        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.Black));



        listCars = (Button) findViewById(R.id.ButtonListCars);
        EmailClientET = (EditText) findViewById(R.id.EmailClientET);
        PasswordClient = (EditText) findViewById(R.id.passwordET);
        PhoneNum = (EditText) findViewById(R.id.NumberETClient);
        SaveBut = (Button) findViewById(R.id.SaveButtonClient);
        SaveBut.setVisibility(View.INVISIBLE);
        SaveBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertBox();
            }
        });
        User = (EditText) findViewById(R.id.userName);
        mProfilePicture = (ImageView) findViewById(R.id.imageInfoClient);

        isEmailChanged = false;
        isNameChanged = false;
        isPasswordChanged = false;
        isNumberChanged = false;

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Korisnik").child("Klijent");

        mProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RC_PHOTO_PICKER);
            }
        });


        SharedPreferences shared = getSharedPreferences("SharedData",MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        Gson gson = new Gson();
        String json = shared.getString("TrenutniKlijent","");
        Klijent trenutniKlijent = gson.fromJson(json, Klijent.class);

        User.setText(trenutniKlijent.getIme() + " " + trenutniKlijent.getPrezime());
        EmailClientET.setText(trenutniKlijent.getEmail());
        PhoneNum.setText(trenutniKlijent.getBrojTelefona());

        listCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), ListaVozilaActivity.class));
            }
        });
        /*PhoneNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneNum.setText("");
            }
        });*/
        PhoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ChangeSaveButtonState();
                isNumberChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (PhoneNum.getText().equals(""))
                {
                    //Vrati na stare podatke
                    PhoneNum.setText("018/1234567");
                }
            }
        });

        /*PasswordClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasswordClient.setText("");
            }
        });*/
        PasswordClient.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ChangeSaveButtonState();
                isPasswordChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (PasswordClient.getText().equals(""))
                {
                    //Vrati na stare podatke
                    PasswordClient.setText("password");
                }

            }
        });
        /*EmailClientET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmailClientET.setText("");
            }
        });*/
        EmailClientET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ChangeSaveButtonState();
                isEmailChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(EmailClientET.getText().equals(""))
                {
                    //Vrati stare podatke
                    EmailClientET.setText("stari podaci");
                }
            }
        });

        User.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ChangeSaveButtonState();
                isNameChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //provera da li vec ima postojeca slika
        StorageReference photoRef = mStorageReference.child("photos").child(mAuth.getCurrentUser().getUid());
        photoRef.getDownloadUrl().addOnSuccessListener(this, new OnSuccessListener<Uri>() {
           @Override
           public void onSuccess(Uri uri) {
               if(uri != null) {
                   //showProgressDialog();
                   Glide.with(mProfilePicture.getContext())
                           .load(uri).into(mProfilePicture);
                   //hideProgressDialog();
               }
           }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(Info_client.this, mainScreenClientActivity.class));
    }

    public void ChangeSaveButtonState() {
        SaveBut.setVisibility(View.VISIBLE);
    }

    public void alertBox() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.Changes))
                //.setItems(mLikelyPlaceNames, listener)
                .setNegativeButton(getResources().getString(R.string.No), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(getResources().getString(R.string.Yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(isEmailChanged) {
                            mAuth.getCurrentUser().updateEmail(EmailClientET.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                try {
                                                    throw task.getException();
                                                } catch (FirebaseAuthRecentLoginRequiredException e) {
                                                    Toast.makeText(Info_client.this, R.string.zauzeti_serveri,
                                                            Toast.LENGTH_SHORT).show();
                                                } catch (Exception ee) {
                                                    //nista
                                                }
                                            } else if(task.isSuccessful()){
                                                mDatabaseReference.child(mAuth.getCurrentUser().getUid())
                                                        .child("email").setValue(EmailClientET.getText().toString());
                                                mAuth.signOut();
                                                finishAffinity();
                                                startActivity(new Intent(getBaseContext(), LoginActivity.class));
                                            }
                                        }
                                    });
                        }

                        if(isNumberChanged) {
                            mDatabaseReference.child(mAuth.getCurrentUser().getUid())
                                    .child("brojTelefona").setValue(PhoneNum.getText().toString());
                        }

                        if(isNameChanged) {
                            String[] imePrezime = User.getText().toString().split(Pattern.quote(" "));
                            String ime = imePrezime[0];
                            String prezime = imePrezime[imePrezime.length - 1];
                            mDatabaseReference.child(mAuth.getCurrentUser().getUid())
                                    .child("ime").setValue(ime);
                            mDatabaseReference.child(mAuth.getCurrentUser().getUid())
                                    .child("prezime").setValue(prezime);
                        }

                        if(isPasswordChanged) {
                            mAuth.getCurrentUser().updatePassword(PasswordClient.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                try {
                                                    throw task.getException();
                                                } catch (FirebaseAuthRecentLoginRequiredException e) {
                                                    Toast.makeText(Info_client.this, R.string.zauzeti_serveri,
                                                            Toast.LENGTH_SHORT).show();
                                                } catch (Exception ee) {
                                                    //nista
                                                }
                                            } else if(task.isSuccessful()){
                                                mAuth.signOut();
                                                finishAffinity();
                                                startActivity(new Intent(getBaseContext(), LoginActivity.class));
                                            }
                                        }
                                    });
                        }

                        Toast.makeText(Info_client.this, getResources().getString(R.string.ChangesSaved),
                                Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            StorageReference photoRef = mStorageReference.child("photos").child(mAuth.getCurrentUser().getUid());

            showProgressDialog();
            photoRef.putFile(selectedImageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests")
                    Uri s = taskSnapshot.getDownloadUrl();
                    Glide.with(mProfilePicture.getContext())
                            .load(s).into(mProfilePicture);
                    hideProgressDialog();
                }
            });
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}