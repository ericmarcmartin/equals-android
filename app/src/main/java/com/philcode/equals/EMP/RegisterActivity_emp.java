package com.philcode.equals.EMP;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.philcode.equals.PrivacyPolicyPDFViewer;
import com.philcode.equals.R;

import java.io.IOException;

public class RegisterActivity_emp extends AppCompatActivity implements View.OnClickListener {

    //firebase auth object
    private FirebaseAuth firebaseAuth;
    FirebaseStorage storage;
    StorageReference storageReference;
    private DatabaseReference mDatabase;
    private Spinner spinnerCity;

    // Folder path for Firebase Storage.
    String Storage_Path = "Employer_Reg_Form/";
    private TextView textViewUserEmail;
    private Button buttonLogout, buttonSave, btnUpload, buttonUploadEmpID;

    private EditText editCompanyName, editCompanyBackground, editContact, editEmail, editPassword,
            editFirstName, editLastName, editCompanyAddress, confirmPassword;
    private TextInputLayout editEmailError, editPasswordError, confirmPasswordError;
    private ImageView profilePicEMP, empValidID;
    private TextView emailAddressInUse;
    String password, stringConfirmPassword, emailCheck;
    int PICK_IMAGE_REQUEST = 7;
    private Uri filePath;
    //private Uri filePath2;
    private CheckBox checkPrivacy;

    boolean internetConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emp_register);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Employers");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        editEmailError = findViewById(R.id.textInputLayout3);
        editPasswordError = findViewById(R.id.textInputLayout4);
        confirmPasswordError = findViewById(R.id.textInputLayout5);

        buttonUploadEmpID = (Button) findViewById(R.id.btn_emp_ID_upload);
        buttonSave = (Button) findViewById(R.id.btnEditProfile);
        empValidID = findViewById(R.id.emp_ID);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
        buttonUploadEmpID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creating intent.
                Intent intent = new Intent();

                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), PICK_IMAGE_REQUEST);
            }
        });

        editEmail = findViewById(R.id.editEmail);
        editEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                /* When focus is lost check that the text field
                 * has valid values.
                 */
                if (!hasFocus) {
                    String email = editEmail.getText().toString();
                    if(!(email == null || email.equals(""))){
                        if(!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                            checkEmailExistsOrNot(email);
                        }else{
                            editEmailError.setError("Invalid email");
                        }
                    }
                }
            }
        });

        editPassword = findViewById(R.id.editPassword);
        editPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                /* When focus is lost check that the text field
                 * has valid values.
                 */
                if (!hasFocus) {
                    password = editPassword.getText().toString().trim();
                    if(password.length()==0){
                        editPasswordError.setError("Please enter a password");
                    }else{
                        editPasswordError.setError(null);
                    }if (password.length()<=5) {
                        editPasswordError.setError("Your password must contain at least 6 characters");
                    }else{
                        editPasswordError.setError(null);
                    }
                }
            }
        });


        confirmPassword = findViewById(R.id.confirmPassword);
        confirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                /* When focus is lost check that the text field
                 * has valid values.
                 */
                if (!hasFocus) {
                    stringConfirmPassword = confirmPassword.getText().toString().trim();
                    if(!(stringConfirmPassword.equals(password))){
                        confirmPasswordError.setError("Password doesn't match");
                    }else{
                        confirmPasswordError.setError(null);
                    }
                }
            }
        });

        editFirstName = findViewById(R.id.editEmployerFirstName);
        editFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                final String firstname = editFirstName.getText().toString().trim();
                if (!hasFocus) {
                    if (firstname.length() == 0) {
                        editFirstName.setError("Please enter your first name");
                    } else if (!firstname.matches("[a-zA-Z ]+")) {
                        editFirstName.setError("Please enter alphabetical letters only");
                    } else {
                        editFirstName.setError(null);
                    }
                }
            }
        });

        editLastName = findViewById(R.id.editEmployerLastName);
        editLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                final String lastname = editLastName.getText().toString().trim();
                if (!hasFocus) {
                    if (lastname.length() == 0) {
                        editLastName.setError("Please enter your last name");
                    } else if (!lastname.matches("[a-zA-Z ]+")) {
                        editLastName.setError("Please enter alphabetical letters only");
                    } else {
                        editLastName.setError(null);
                    }
                }
            }
        });
        editCompanyName = findViewById(R.id.editCompanyName);
        editCompanyName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    final String companyName = editCompanyName.getText().toString().trim();
                    if (companyName.length() == 0) {
                        editCompanyName.setError("Please enter the company name");
                    } else {
                        editCompanyName.setError(null);
                    }
                }
            }
        });
        emailAddressInUse = findViewById(R.id.emailAddressInUse);
        editContact = findViewById(R.id.editContact);
        editContact.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    final String contact = editContact.getText().toString().trim();
                    if (contact.length() == 0) {
                        editContact.setError("Please enter contact");
                    } else {
                        editContact.setError(null);
                    }
                }
            }
        });
        editCompanyBackground = findViewById(R.id.editCompanyBackground);
        editCompanyBackground.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    final String companyBackground = editCompanyBackground.getText().toString().trim();
                    if (companyBackground.length() == 0) {
                        editCompanyBackground.setError("Please enter company background");
                    } else {
                        editCompanyBackground.setError(null);
                    }
                }
            }
        });

        spinnerCity = findViewById(R.id.spinnerCity);
        editCompanyAddress = findViewById(R.id.editTextAddress);
        editCompanyAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    final String companyAddress = editCompanyAddress.getText().toString().trim();
                    if (companyAddress.length() == 0) {
                        editCompanyAddress.setError("Please enter address");
                    } else {
                        editCompanyAddress.setError(null);
                    }
                }
            }
        });

        //Privacy Policy
        checkPrivacy = findViewById(R.id.privacypolicy);
        String text = "I have read and agree to the Equals Privacy Policy";
        SpannableString ss = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(RegisterActivity_emp.this, PrivacyPolicyPDFViewer.class);
                startActivity(intent);
            }
        };

        ss.setSpan(clickableSpan,29,50, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        checkPrivacy.setText(ss);
        checkPrivacy.setMovementMethod(LinkMovementMethod.getInstance());




    }


    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity_emp.class));
        }
        return super.onKeyDown(keyCode, event);
    }


    public void checkEmailExistsOrNot(String emails){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            internetConnection = true;
        } else
            internetConnection = false;

        if (internetConnection == true) {
            final FirebaseAuth firebaseauth = FirebaseAuth.getInstance();
            firebaseauth.fetchSignInMethodsForEmail(emails).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                @Override
                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                    if (task.getResult().getSignInMethods().size() == 0){
                        editEmailError.setError(null);
                        emailCheck = "huhuz";
                    }else {
                        editEmailError.setError("Email address is already in use");
                        emailCheck = "hehez";
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }else{
            AlertDialog.Builder alert =  new AlertDialog.Builder(RegisterActivity_emp.this);
            alert.setMessage("Please check your internet connection and try again").setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(RegisterActivity_emp.this, RegisterActivity_emp.class));
                            Intent intent = new Intent();
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        }
                    });
            AlertDialog alertDialog = alert.create();
            alertDialog.setTitle("Network Connection");
            alertDialog.show();
        }

    }

    private void uploadImage() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            internetConnection = true;
        } else
            internetConnection = false;
        if (internetConnection == true) {
            final Intent intent = new Intent(this, EMP_FillUpInformation_ProfilePicture.class);
            if (filePath != null) {
                final String email = editEmail.getText().toString().trim();
                final String password = editPassword.getText().toString().trim();
                final String firstname = editFirstName.getText().toString().trim();
                final String lastname = editLastName.getText().toString().trim();
                final String fullname = editCompanyName.getText().toString().trim();
                final String typeStatus = "EMPPending";
                //  final String fullname = editCompanyName.getText().toString().trim();
                final String companybg = editCompanyBackground.getText().toString().trim();
                final String contact = editContact.getText().toString().trim();
                final String companyAddress = editCompanyAddress.getText().toString().trim();
                final String companyCity = spinnerCity.getSelectedItem().toString().trim();
                if (emailCheck == "hehez") {
                    Toast.makeText(getApplicationContext(), emailCheck, Toast.LENGTH_LONG);
                    editEmailError.setError("Email address is already in use");
                    editEmail.requestFocus();
                } else if (email.length() == 0) {
                    editEmail.setError("Please enter your email");
                    editEmail.requestFocus();
                } else if (password.length() == 0) {
                    //               editPassword.requestFocus();
                    editPassword.setError("Please enter a password");
                    editPassword.requestFocus();
                } else if (password.length() <= 5) {
                    //             editPassword.requestFocus();
                    editPassword.setError("Your password must contain at least 6 characters");
                    editPassword.requestFocus();
                } else if (!(stringConfirmPassword.equals(password))) {
                    confirmPasswordError.setError("Password doesn't match");
                    confirmPassword.requestFocus();
                } else if (firstname.length() == 0) {
                    //           editFirstName.requestFocus();
                    editFirstName.setError("Please enter your first name");
                    editFirstName.requestFocus();
                } else if (!firstname.matches("[a-zA-Z ]+")) {
                    //         editFirstName.requestFocus();
                    editFirstName.setError("Please enter alphabetical letters only");
                } else if (lastname.length() == 0) {
                    //       editLastName.requestFocus();
                    editLastName.setError("Please enter your last name");
                    editLastName.requestFocus();
                } else if (!lastname.matches("[a-zA-Z ]+")) {
                    //     editLastName.requestFocus();
                    editLastName.setError("Please enter alphabetical letters only");
                } else if (companyAddress.length() == 0) {
                    //    editTextAddress.requestFocus();
                    editCompanyAddress.setError("Please enter your address");
                    editCompanyAddress.requestFocus();
                } else if (contact.length() == 0) {
                    //  editContact.requestFocus();
                    editContact.setError("Please enter your contact number");
                    editContact.requestFocus();
                } else if (companybg.length() == 0) {
                    editCompanyBackground.setError("Please enter your company overview");
                    editCompanyBackground.requestFocus();
                } else if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity_emp.this, "Please enter your email", Toast.LENGTH_LONG).show();
                    return;
                } else if (TextUtils.isEmpty(companybg)) {
                    Toast.makeText(RegisterActivity_emp.this, "Please enter your company", Toast.LENGTH_LONG).show();
                    return;
                } else if (TextUtils.isEmpty(contact)) {
                    Toast.makeText(RegisterActivity_emp.this, "Please enter your contact", Toast.LENGTH_LONG).show();
                    return;
                } else if (TextUtils.isEmpty(firstname)) {
                    Toast.makeText(RegisterActivity_emp.this, "Please enter your first name", Toast.LENGTH_LONG).show();
                    return;
                } else if (TextUtils.isEmpty(lastname)) {
                    Toast.makeText(RegisterActivity_emp.this, "Please enter your last name", Toast.LENGTH_LONG).show();
                    return;

                }else if(!checkPrivacy.isChecked()) {
                    checkPrivacy.setError("Please check the checkbox first");
                    checkPrivacy.requestFocus();
                    Toast.makeText(RegisterActivity_emp.this, "Please confirm that you have read the Equals Privacy Policy", Toast.LENGTH_LONG).show();
                    return;
                } else {

                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    final StorageReference ref = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(filePath));
                    ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    progressDialog.dismiss();
                                    final String empValidID = task.getResult().toString();

                                    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity_emp.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            EmployeeInformation EmpInfo = new EmployeeInformation(email, password, typeStatus, firstname, lastname, fullname, companybg,
                                                                    contact, empValidID, companyAddress, companyCity);
                                                            FirebaseDatabase.getInstance().getReference("Employers").child(firebaseAuth.getCurrentUser().getUid()).setValue(EmpInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    Toast.makeText(getApplicationContext(), "Information saved", Toast.LENGTH_LONG).show();
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                        } else {
                                                            Toast.makeText(RegisterActivity_emp.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(RegisterActivity_emp.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                                // }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity_emp.this, "Failed:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Loading " + (int) progress + "%");
                            progressDialog.setCancelable(false);
                        }
                    });
                }
            }
        }else{
            AlertDialog.Builder alert =  new AlertDialog.Builder(RegisterActivity_emp.this);
            alert.setMessage("Please check your internet connection and try again").setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final Intent intent = new Intent(getApplicationContext(), RegisterActivity_emp.class);
                            startActivity(intent);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        }
                    });
            AlertDialog alertDialog = alert.create();
            alertDialog.setTitle("Network Connection");
            alertDialog.show();
        }
    }
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data !=null && data.getData() != null){
            filePath = data.getData();
            try{
                Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                empValidID.setImageBitmap(bitmap1);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onClick(View view) {
        //if logout is pressed
    }
}