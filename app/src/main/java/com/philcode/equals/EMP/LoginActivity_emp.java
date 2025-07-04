package com.philcode.equals.EMP;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.philcode.equals.LoadingActivityEMP;
import com.philcode.equals.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.philcode.equals.SelectionScreenActivity;

public class LoginActivity_emp extends AppCompatActivity implements View.OnClickListener {


    //defining views
    private Button buttonSignIn, buttonforgotpass;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignup;

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emp_login);

        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //if the objects getcurrentuser method is not null
        //means user is already logged in
        /*if(firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().isEmailVerified()){
            //close this activity
            //opening profile activity
            startActivity(new Intent(getApplicationContext(), ProfileActivity_emp.class));
        }*/

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSignIn = (Button) findViewById(R.id.buttonSignin);
        buttonforgotpass = (Button) findViewById(R.id.buttonForgotpasswor);
        textViewSignup  = (TextView) findViewById(R.id.textViewSignUp);

        progressDialog = new ProgressDialog(this);

        //attaching click listener
        buttonSignIn.setOnClickListener(this);
        buttonforgotpass.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);
    }

    //method for user login
    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();


        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Signing in, please wait...");
        progressDialog.show();

        //logging in the user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        //if the task is successfull
                        if(task.isSuccessful()){
                            if(firebaseAuth.getCurrentUser().isEmailVerified()){
                                finish();
                                startActivity(new Intent(getApplicationContext(), LoadingActivityEMP.class));
                            }else{
                                AlertDialog.Builder alert =  new AlertDialog.Builder(LoginActivity_emp.this);
                                alert.setMessage("Please check your email address.").setCancelable(false)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();

                                            }
                                        });
                                AlertDialog alertDialog = alert.create();
                                alertDialog.setTitle("Verify your Email");
                                alertDialog.show();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(LoginActivity_emp.this,"Incorrect Password", Toast.LENGTH_LONG).show();
                        } else if (e instanceof FirebaseAuthInvalidUserException) {
                            Toast.makeText(LoginActivity_emp.this,"Incorrect Email", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity_emp.this,"Invalid", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    //back
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            finish();
            startActivity(new Intent(getApplicationContext(), SelectionScreenActivity.class));
        }
        return super.onKeyDown(keyCode, event);
    }

    public static class CheckNetwork {


        private static final String TAG = CheckNetwork.class.getSimpleName();



        public static boolean isInternetAvailable(Context context)
        {
            NetworkInfo info = (NetworkInfo) ((ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

            if (info == null)
            {
                Log.d(TAG,"no internet connection");
                return false;
            }
            else
            {
                if(info.isConnected())
                {
                    Log.d(TAG," internet connection available...");
                    return true;
                }
                else
                {
                    Log.d(TAG," internet connection");
                    return true;
                }

            }
        }
    }


    @Override
    public void onClick(View view) {
        if(view == buttonSignIn){
            if(CheckNetwork.isInternetAvailable(LoginActivity_emp.this)) //returns true if internet available
            {
                userLogin();
            }
            else
            {
                AlertDialog.Builder alert =  new AlertDialog.Builder(LoginActivity_emp.this);
                alert.setMessage("Make sure that your Wi-Fi or mobile data is turned on, then try again.").setCancelable(false)
                        .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                            }
                        });
                AlertDialog alertDialog = alert.create();
                alertDialog.setTitle("No Internet Connection");
                alertDialog.show();
            }

        }

        if(view == textViewSignup){
            finish();
            startActivity(new Intent(this, RegisterActivity_emp.class));
        }

        if(view == buttonforgotpass){
            finish();
            startActivity(new Intent(this, ForgotpassActivity_emp.class));

        }
    }
}