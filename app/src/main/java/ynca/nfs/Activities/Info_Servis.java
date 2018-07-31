package ynca.nfs.Activities;

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
import android.support.v7.app.AppCompatActivity;
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

import ynca.nfs.Activities.StartActivities.LoginActivity;
import ynca.nfs.R;
import ynca.nfs.Servis;

public class Info_Servis extends AppCompatActivity {

    private EditText mNameET;
    private EditText mOwnerNameET;
    private EditText mAddressET;
    private EditText mNumberET;
    private EditText mEmailET;
    private EditText mPasswordET;

    private ImageView mPhotoService;
    private Button mSaveButton;
    private ProgressDialog mProgressDialog;

    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;

    private boolean isNameChanged;
    private boolean isOwnerNameChanged;
    private boolean isAddressChanged;
    private boolean isNumberChanged;
    private boolean isEmailChanged;
    private boolean isPasswordChanged;

    private static final int RC_PHOTO_PICKER =  2;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info__servis);


        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.Black));


        mNameET = (EditText) findViewById(R.id.nameService);
        mOwnerNameET = (EditText) findViewById(R.id.ownerNameET);
        mAddressET = (EditText) findViewById(R.id.addressET);
        mNumberET = (EditText) findViewById(R.id.NumberETService);
        mEmailET = (EditText) findViewById(R.id.EmailServiceET);
        mPasswordET = (EditText) findViewById(R.id.passwordET);

        mPhotoService = (ImageView) findViewById(R.id.imageInfoService);
        mSaveButton = (Button) findViewById(R.id.SaveButtonService);

        mSaveButton.setVisibility(View.INVISIBLE);

        isNameChanged = false;
        isOwnerNameChanged = false;
        isAddressChanged = false;
        isNumberChanged = false;
        isEmailChanged = false;
        isPasswordChanged = false;

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Korisnik").child("Servis");
        mAuth = FirebaseAuth.getInstance();

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertBox();
            }
        });

        mPhotoService.setOnClickListener(new View.OnClickListener() {
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
        String json = shared.getString("TrenutniServis","");
        Servis trenutniServis = gson.fromJson(json, Servis.class);

        mNameET.setText(trenutniServis.getNaziv());
        mOwnerNameET.setText(trenutniServis.getImeVlasnika());
        mAddressET.setText(trenutniServis.getAdresa());
        mNumberET.setText(trenutniServis.getTelefon());
        mEmailET.setText(trenutniServis.getEmail());

        //proverava da li vec postoji slika servisa
        StorageReference photoRef = mStorageReference.child("photos").child(mAuth.getCurrentUser().getUid());
        photoRef.getDownloadUrl().addOnSuccessListener(this, new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(uri != null) {
                    //showProgressDialog();
                    Glide.with(mPhotoService.getContext())
                            .load(uri).into(mPhotoService);
                    //hideProgressDialog();
                }
            }
        });

        mNameET.addTextChangedListener(new TextWatcher() {
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

        mOwnerNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ChangeSaveButtonState();
                isOwnerNameChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mAddressET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ChangeSaveButtonState();
                isAddressChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mNumberET.addTextChangedListener(new TextWatcher() {
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

            }
        });

        mEmailET.addTextChangedListener(new TextWatcher() {
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

            }
        });

        mPasswordET.addTextChangedListener(new TextWatcher() {
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

            }
        });
    }

    public void alertBox(){
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
                            mAuth.getCurrentUser().updateEmail(mEmailET.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                try {
                                                    throw task.getException();
                                                } catch (FirebaseAuthRecentLoginRequiredException e) {
                                                    Toast.makeText(Info_Servis.this, R.string.zauzeti_serveri,
                                                            Toast.LENGTH_SHORT).show();
                                                } catch (Exception ee) {
                                                    //nista
                                                }
                                            } else if(task.isSuccessful()){
                                                mDatabaseReference.child(mAuth.getCurrentUser().getUid())
                                                        .child("email").setValue(mEmailET.getText().toString());
                                                mAuth.signOut();
                                                finishAffinity();
                                                startActivity(new Intent(getBaseContext(), LoginActivity.class));
                                            }
                                        }
                                    });
                        }

                        if(isNumberChanged) {
                            mDatabaseReference.child(mAuth.getCurrentUser().getUid())
                                    .child("brojTelefona").setValue(mNumberET.getText().toString());
                        }

                        if(isNameChanged) {
                            mDatabaseReference.child(mAuth.getCurrentUser().getUid())
                                    .child("naziv").setValue(mNameET.getText().toString());
                        }

                        if(isPasswordChanged) {
                            mAuth.getCurrentUser().updatePassword(mPasswordET.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                try {
                                                    throw task.getException();
                                                } catch (FirebaseAuthRecentLoginRequiredException e) {
                                                    Toast.makeText(Info_Servis.this, R.string.zauzeti_serveri,
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

                        if(isAddressChanged){
                            mDatabaseReference.child(mAuth.getCurrentUser().getUid())
                                    .child("adresa").setValue(mAddressET.getText().toString());
                        }

                        if(isOwnerNameChanged){
                            mDatabaseReference.child(mAuth.getCurrentUser().getUid())
                                    .child("imeVlasnika").setValue(mOwnerNameET.getText().toString());
                        }

                        Toast.makeText(Info_Servis.this, R.string.ChangesSaved,
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
                    Glide.with(mPhotoService.getContext())
                            .load(s).into(mPhotoService);
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

    public void ChangeSaveButtonState() {
        mSaveButton.setVisibility(View.VISIBLE);
    }
}
