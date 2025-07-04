package com.philcode.equals.EMP;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.philcode.equals.PWD.PWD_RegisterActivity2;
import com.philcode.equals.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Emp_PostJob extends AppCompatActivity {

    TextInputEditText postTitle, postDescription, yearsOfExperience;
    Button empBtnPost;
    CheckBox typeOfDisability1, typeOfDisability2, typeOfDisability3, typeOfDisabilityMore;
    Spinner setExpDate, spinnerCity;
    Button empBtnChoose;
    private RadioGroup rgWorkExperience, rgEducAttainment;
    private RadioButton rbEduc, rbWorkExperience, rbWithExperience, rbWithoutExperience;

    String typeOfDisability_1 = "";
    String typeOfDisability_2 = "";
    String typeOfDisability_3 = "";
    String typeOfDisability_More = "";
    private Spinner primary_skillsCategory;
    boolean[] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();
    String expDate1;
    String categorySkill;
    String jobSkill_1 = "";
    String jobSkill_2 = "";
    String jobSkill_3 = "";
    String jobSkill_4 = "";
    String jobSkill_5 = "";
    String jobSkill_6 = "";
    String jobSkill_7 = "";
    String jobSkill_8 = "";
    String jobSkill_9 = "";
    String jobSkill_10 = "";
    int numberOfPrimarySkills=0;
    String x;
    String Address;
    String city;
    String week1="1 week";
    String week2="2 weeks";
    String week3="3 weeks";
    String month1="1 month";
    String month2="2 months";
    String month3="3 month";
    String month4="4 months";
    String month5="5 month";
    String month6="6 months";
    String month7="7 month";
    String month8="8 months";
    String month9="9 month";
    String month10="10 months";
    String month11="11 month";
    String year1="1 year";
    String unli="Unlimited";
    int count = 10;
    int count2 = 0;
    int count3 = 0;
    int countw = 0;
    int years =0;
    private TextView textViewUserEmail, skillSelected;
    String[] pwdPrimarySkills = new String[10];


    String primarySkill1;
    String primarySkill2;
    String primarySkill3;
    String primarySkill4;
    String primarySkill5;
    String primarySkill6;
    String primarySkill7;
    String primarySkill8;
    String primarySkill9;
    String primarySkill10;
    String primarySkillOther;

    String job_1 = "";
    String job_2 = "";
    String job_3 = "";
    String job_4 = "";
    String job_5 = "";
    String job_6 = "";
    String job_7 = "";
    String job_8 = "";
    String job_9 = "";
    String job_10 = "";

    private String educAttainment = "";
    private String workExperience = "";

    String m1 = "";
    String m2 = "";
    String m3 = "";
    String m4 = "";
    String m5 = "";
    String m6 = "";
    String m7 = "";
    String m8 = "";
    String m9 = "";
    String m10 = "";
    String zz;


    Calendar cal = Calendar.getInstance();
    Calendar currentCal = Calendar.getInstance();
    Date currentDate = Calendar.getInstance().getTime();
    SimpleDateFormat df = new SimpleDateFormat("MMMM-dd-yyyy");
    String formattedDate = df.format(currentDate);
    String date = new SimpleDateFormat("MMMM-dd-yyyy", Locale.getDefault()).format(new Date());
    //String formattedDate = df.format(c);
    SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");


    String permission;
    CheckBox jobSkill1, jobSkill2, jobSkill3, jobSkill4, jobSkill5, jobSkill6, jobSkill7,
            jobSkill8, jobSkill9, jobSkill10, checkEducRequirement;

    // Folder path for Firebase Storage.
    String Storage_Path = "Job_Offers/";
    // Root Database Name for Firebase Database.
    String Database_Path = "Job_Offers";
    // Creating ImageView.
    private ImageView ImageView;
    // Creating URI.
    private Uri FilePathUri;

    // Image request code for onActivityResult() .
    int Image_Request_Code = 7;

    // Creating StorageReference and DatabaseReference object.
    StorageReference storageReference;
    DatabaseReference databaseReference;

    private final int PICK_IMAGE_REQUEST = 7;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        rgWorkExperience = findViewById(R.id.workexperience);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emp_postjob);
        FirebaseUser cbf = FirebaseAuth.getInstance().getCurrentUser();
        final String cu = cbf.getUid();
        // Assign FirebaseStorage instance to storageReference.
        storageReference = FirebaseStorage.getInstance().getReference();

        final DatabaseReference locRef = FirebaseDatabase.getInstance().getReference().child("Employers").child(cu);
        locRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Address = dataSnapshot.child("companyaddress").getValue(String.class);
                city = dataSnapshot.child("companycity").getValue(String.class);
     //           Toast.makeText(getApplicationContext(), cu+Address+city, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Assign FirebaseDatabase instance with root database name.
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        skillSelected = findViewById(R.id.selectedSkills);
        empBtnChoose = findViewById(R.id.empBtnChoose);
        empBtnPost = findViewById(R.id.empBtnPost);
        ImageView = findViewById(R.id.displayPostPic);
        rgEducAttainment = findViewById(R.id.rg_educ);
        rgWorkExperience = findViewById(R.id.workexperience);
        checkEducRequirement=(CheckBox) findViewById(R.id.educAttainmentRequirement);
        checkEducRequirement.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(Emp_PostJob.this);
        empBtnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creating intent.
                Intent intent = new Intent();

                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);
            }
        });
        final int selectExperience = rgWorkExperience.getCheckedRadioButtonId();

        yearsOfExperience = findViewById(R.id.yearsofExperience);
        rbWithExperience = findViewById(R.id.radio_11);
        rbWithoutExperience = findViewById(R.id.radio_10);
        rgWorkExperience = findViewById(R.id.workexperience);
        rbWorkExperience = findViewById(selectExperience);
        workExperience = rbWorkExperience.getText().toString();
        rgWorkExperience.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rbWithExperience.isChecked()) {
                    yearsOfExperience.setVisibility(View.GONE);

                }
                if (rbWithoutExperience.isChecked()) {
                    yearsOfExperience.setVisibility(View.GONE);
                }
            }
        });
        primary_skillsCategory = findViewById(R.id.skillSpinner);
        primary_skillsCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mUserItems.clear();

                final DatabaseReference primarySkillsRef = FirebaseDatabase.getInstance().getReference().child("Category");
                x = primary_skillsCategory.getSelectedItem().toString();
                if (x.equals("Click to select value")) {

                } else {
                    primarySkillsRef.orderByChild("skill").equalTo(x).addValueEventListener(new ValueEventListener() {
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                String parent = dataSnapshot1.getKey();
                           final DatabaseReference parentRef = FirebaseDatabase.getInstance().getReference().child("Category").child(parent);
                                parentRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        count=10;
                                        count2=0;
                                        if(dataSnapshot.child("skill1").getValue(String.class).equals("")){
                                            count--;
                                            m1="";
                                        }
                                        else{
                                            m1 = dataSnapshot.child("skill1").getValue(String.class);
                                            count2++;

                                        }
                                        if(dataSnapshot.child("skill2").getValue(String.class).equals("")){
                                            count--;
                                            m2="";
                                        }else{
                                            m2 = dataSnapshot.child("skill2").getValue(String.class);
                                            count2++;
                                        }
                                        if(dataSnapshot.child("skill3").getValue(String.class).equals("")){
                                            count--;
                                            m3="";
                                        }else{
                                            m3 = dataSnapshot.child("skill3").getValue(String.class);
                                            count2++;
                                        }
                                        if(dataSnapshot.child("skill4").getValue(String.class).equals("")){
                                            count--;
                                            m4="";
                                        }else{
                                            m4 = dataSnapshot.child("skill4").getValue(String.class);
                                            count2++;
                                        }
                                        if(dataSnapshot.child("skill5").getValue(String.class).equals("")){
                                            count--;
                                            m5="";
                                        }else{
                                            m5 = dataSnapshot.child("skill5").getValue(String.class);
                                            count2++;
                                        }
                                        if(dataSnapshot.child("skill6").getValue(String.class).equals("")){
                                            count--;
                                            m6="";
                                        }else{
                                            m6 = dataSnapshot.child("skill6").getValue(String.class);
                                            count2++;
                                        }
                                        if(dataSnapshot.child("skill7").getValue(String.class).equals("")){
                                            count--;
                                            m7="";
                                        }else{
                                            m7 = dataSnapshot.child("skill7").getValue(String.class);
                                            count2++;
                                        }
                                        if(dataSnapshot.child("skill8").getValue(String.class).equals("")){
                                            count--;
                                            m8="";

                                        }else{
                                            m8 = dataSnapshot.child("skill8").getValue(String.class);
                                            count2++;
                                        }
                                        if(dataSnapshot.child("skill9").getValue(String.class).equals("")){
                                            count--;
                                            m9="";

                                        }else{
                                            m9 = dataSnapshot.child("skill9").getValue(String.class);
                                            count2++;
                                        }
                                        if(dataSnapshot.child("skill10").getValue(String.class).equals("")){
                                            count--;
                                            m10="";

                                        }else{
                                            m10 = dataSnapshot.child("skill10").getValue(String.class);
                                            count2++;
                                        }
                                        String[] listSkills = new String[count];
                                        count3=0;
                                        if(m1.equals("")){
                                        }
                                        else{
                                            listSkills[count3]=m1;
                                            count3++;
                                        }
                                        if(m2.equals("")){
                                        }
                                        else{
                                            listSkills[count3]=m2;
                                            count3++;
                                        }
                                        if(m3.equals("")){
                                        }
                                        else{
                                            listSkills[count3]=m3;
                                            count3++;
                                        }
                                        if(m4.equals("")){
                                        }
                                        else{
                                            listSkills[count3]=m4;
                                            count3++;
                                        }
                                        if(m5.equals("")){
                                        }
                                        else{
                                            listSkills[count3]=m5;
                                            count3++;
                                        }
                                        if(m6.equals("")){
                                        }
                                        else{
                                            listSkills[count3]=m6;
                                            count3++;
                                        }
                                        if(m7.equals("")){
                                        }
                                        else{
                                            listSkills[count3]=m7;
                                            count3++;
                                        }
                                        if(m8.equals("")){
                                        }
                                        else{
                                            listSkills[count3]=m8;
                                            count3++;
                                        }
                                        if(m9.equals("")){
                                        }
                                        else{
                                            listSkills[count3]=m9;
                                            count3++;
                                        }
                                        if(m10.equals("")){
                                        }
                                        else{
                                            listSkills[count3]=m10;
                                            count3++;
                                        }
                                        checkedItems = new boolean[count3];
                                        callDialog(listSkills, checkedItems);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        postTitle = findViewById(R.id.postTitle);
        postDescription = findViewById(R.id.postDescription);
        // postLocation = findViewById(R.id.postLocation);
        typeOfDisability1 = (CheckBox) findViewById(R.id.typeOfDisability1);
        typeOfDisability2 = (CheckBox) findViewById(R.id.typeOfDisability2);
        typeOfDisability3 = (CheckBox) findViewById(R.id.typeOfDisability3);
        typeOfDisabilityMore = (CheckBox) findViewById(R.id.typeOfDisabilityOther);
        jobSkill1 = findViewById(R.id.typeOfSkills1);
        jobSkill2 = findViewById(R.id.typeOfSkills2);
        jobSkill3 = findViewById(R.id.typeOfSkills3);
        jobSkill4 = findViewById(R.id.typeOfSkills4);
        jobSkill5 = findViewById(R.id.typeOfSkills5);
        jobSkill6 = findViewById(R.id.typeOfSkills6);
        jobSkill7 = findViewById(R.id.typeOfSkills7);
        jobSkill8 = findViewById(R.id.typeOfSkills8);
        jobSkill9 = findViewById(R.id.typeOfSkills9);
        jobSkill10 = findViewById(R.id.typeOfSkills10);
        setExpDate = findViewById(R.id.spinnerExpDate);

        String[] spinnerExpDate = new String[]{"1 week","2 weeks", "3 weeks","1 month", "2 months",
                "3 months","4 months","5 months","6 months","7 months","8 months","9 months","10 months",
                "11 months","1 year", "Unlimited"};

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerExpDate);
        setExpDate.setAdapter(adapter2);
        final DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("Category/");
        categoryRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {

                final List<String> category = new ArrayList<String>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> data = (Map<String, Object>) snapshot.getValue();
                    category.add(data.get("skill").toString());
                }
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(Emp_PostJob.this, android.R.layout.simple_spinner_item, category) {
                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (position == 0) {
                            // Hide the second item from Spinner
                            tv.setVisibility(View.GONE);
                        } else {
                            tv.setVisibility(View.VISIBLE);
                        }
                        return view;
                    }
                };
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                primary_skillsCategory.setAdapter(categoryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        permission = "pending";
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        final String uid = currentFirebaseUser.getUid();
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Employers/"+uid);
        empBtnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootRef.addValueEventListener(new ValueEventListener() {
                    public void onDataChange(DataSnapshot snapshot) {
                        String companyName = snapshot.child("fullname").getValue().toString();
                        login();
                        uploadImage(companyName, uid);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Request_Code && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            FilePathUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                ImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void login() {
        if (typeOfDisability1.isChecked() && typeOfDisability2.isChecked() && typeOfDisability3.isChecked() && typeOfDisabilityMore.isChecked()) {
            typeOfDisability_1 = typeOfDisability1.getText().toString();
            typeOfDisability_2 = typeOfDisability2.getText().toString();
            typeOfDisability_3 = typeOfDisability3.getText().toString();
            typeOfDisability_More = typeOfDisability3.getText().toString();
        } else if (typeOfDisability1.isChecked() && typeOfDisability2.isChecked() && typeOfDisabilityMore.isChecked()) {
            typeOfDisability_1 = typeOfDisability1.getText().toString();
            typeOfDisability_2 = typeOfDisability2.getText().toString();
            typeOfDisability_More = typeOfDisabilityMore.getText().toString();
            typeOfDisability_3 = "";
        } else if (typeOfDisability1.isChecked() && typeOfDisability3.isChecked() && typeOfDisabilityMore.isChecked()) {
            typeOfDisability_1 = typeOfDisability1.getText().toString();
            typeOfDisability_2 = "";
            typeOfDisability_More = typeOfDisabilityMore.getText().toString();
            typeOfDisability_3 = typeOfDisability3.getText().toString();
        } else if (typeOfDisability2.isChecked() && typeOfDisability3.isChecked()&& typeOfDisabilityMore.isChecked()) {
            typeOfDisability_1 = "";
            typeOfDisability_2 = typeOfDisability2.getText().toString();
            typeOfDisability_More =typeOfDisabilityMore.getText().toString();
            typeOfDisability_3 = typeOfDisability3.getText().toString();
        } else if (typeOfDisability2.isChecked() && typeOfDisability3.isChecked()&& typeOfDisability1.isChecked()) {
            typeOfDisability_More = "";
            typeOfDisability_1 = typeOfDisability1.getText().toString();
            typeOfDisability_2 = typeOfDisability2.getText().toString();
            typeOfDisability_3 = typeOfDisability3.getText().toString();
        } else if (typeOfDisability1.isChecked() && typeOfDisability2.isChecked()) {
            typeOfDisability_1 = typeOfDisability1.getText().toString();
            typeOfDisability_2 = typeOfDisability2.getText().toString();
            typeOfDisability_More = "";
            typeOfDisability_3 = "";
        } else if (typeOfDisability1.isChecked() && typeOfDisability3.isChecked()) {
            typeOfDisability_1 = typeOfDisability1.getText().toString();
            typeOfDisability_2 = "";
            typeOfDisability_More = "";
            typeOfDisability_3 = typeOfDisability3.getText().toString();
        } else if (typeOfDisability1.isChecked()&& typeOfDisabilityMore.isChecked()) {
            typeOfDisability_1 = typeOfDisability1.getText().toString();
            typeOfDisability_2 = "";
            typeOfDisability_More = typeOfDisabilityMore.getText().toString();
            typeOfDisability_3 = "";


        } else if (typeOfDisabilityMore.isChecked() && typeOfDisability2.isChecked()) {
            typeOfDisability_1 = "";
            typeOfDisability_2 = typeOfDisability2.getText().toString();
            typeOfDisability_More = typeOfDisabilityMore.getText().toString();
            typeOfDisability_3 = "";
        } else if (typeOfDisabilityMore.isChecked() && typeOfDisability3.isChecked()) {
            typeOfDisability_More = typeOfDisabilityMore.getText().toString();
            typeOfDisability_3 =typeOfDisability3.getText().toString();
            typeOfDisability_1 = "";
            typeOfDisability_2 = "";


        } else if (typeOfDisability3.isChecked() && typeOfDisability2.isChecked()) {
            typeOfDisability_1 = "";
            typeOfDisability_2 = typeOfDisability2.getText().toString();
            typeOfDisability_More = "";
            typeOfDisability_3 =typeOfDisability3.getText().toString();


        } else if (typeOfDisability1.isChecked()) {
            typeOfDisability_1 = typeOfDisability1.getText().toString();
            typeOfDisability_2 = "";
            typeOfDisability_3 = "";
        } else if (typeOfDisability2.isChecked()) {
            typeOfDisability_2 = typeOfDisability2.getText().toString();
            typeOfDisability_1 = "";
            typeOfDisability_3 = "";
        } else if (typeOfDisabilityMore.isChecked()) {
            typeOfDisability_More = typeOfDisabilityMore.getText().toString();
            typeOfDisability_2 = "";
            typeOfDisability_1 = "";
            typeOfDisability_3 = "";
        } else if (typeOfDisability3.isChecked()) {
            typeOfDisability_3 = typeOfDisability3.getText().toString();
            typeOfDisability_1 = "";
            typeOfDisability_2 = "";

        }

        if (jobSkill1.isChecked()){
            jobSkill_1 = jobSkill1.getText().toString();
        }
        else{
            jobSkill_1 = "";
        } if (jobSkill2.isChecked()){
            jobSkill_2 = jobSkill2.getText().toString();
        }
        else{
            jobSkill_2 = "";
        } if (jobSkill3.isChecked()){
            jobSkill_3 = jobSkill3.getText().toString();
        }
        else{
            jobSkill_3 = "";
        } if (jobSkill4.isChecked()){
            jobSkill_4 = jobSkill4.getText().toString();
        }
        else{
            jobSkill_4 = "";
        } if (jobSkill5.isChecked()){
            jobSkill_5 = jobSkill5.getText().toString();
        }
        else{
            jobSkill_5 = "";
        } if (jobSkill6.isChecked()){
            jobSkill_6 = jobSkill6.getText().toString();
        }
        else{
            jobSkill_6 = "";
        } if (jobSkill7.isChecked()){
            jobSkill_7 = jobSkill7.getText().toString();
        }
        else{
            jobSkill_7 = "";
        } if (jobSkill8.isChecked()){
            jobSkill_8 = jobSkill8.getText().toString();
        }
        else{
            jobSkill_8 = "";
        } if (jobSkill9.isChecked()){
            jobSkill_9 = jobSkill9.getText().toString();
        }
        else{
            jobSkill_9 = "";
        } if (jobSkill10.isChecked()){
            jobSkill_10 = jobSkill10.getText().toString();
        }
        else{
            jobSkill_10 = "";
        }

    }
    public void callDialog(final String[] listSkillsZ, final boolean[] checkedItemsZ) {

        View view2 = LayoutInflater.from(Emp_PostJob.this).inflate(R.layout.other_skills,null);
        final EditText others = view2.findViewById(R.id.otherSkills);

        AlertDialog.Builder builder = new AlertDialog.Builder(Emp_PostJob.this);
        builder.setView(view2);
        builder.setTitle("Select Primary Skills");
        builder.setMultiChoiceItems(listSkillsZ, checkedItemsZ, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                if (isChecked) {
                    if (!mUserItems.contains(position)) {
                        mUserItems.add(position);
                    }
                } else if (mUserItems.contains(position)) {
                    mUserItems.remove(mUserItems.indexOf(position));
                }

            }
        });
        builder.setCancelable(false);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                primarySkillOther = others.getText().toString();
                String item = "";
                String item2 = "";
                for (int i = 0; i < mUserItems.size(); i++) {
                    item = listSkillsZ[mUserItems.get(i)];
                    pwdPrimarySkills[i] = item;
                    item2 = item2 + item + ", ";
                }
                primarySkill1 = pwdPrimarySkills[0];
                primarySkill2 = pwdPrimarySkills[1];
                primarySkill3 = pwdPrimarySkills[2];
                primarySkill4 = pwdPrimarySkills[3];
                primarySkill5 = pwdPrimarySkills[4];
                primarySkill6 = pwdPrimarySkills[5];
                primarySkill7 = pwdPrimarySkills[6];
                primarySkill8 = pwdPrimarySkills[7];
                primarySkill9 = pwdPrimarySkills[8];
                primarySkill10 = pwdPrimarySkills[9];
                if (primarySkillOther!="" && primarySkillOther!=null){
                    item2 = item2+" "+primarySkillOther;
                    skillSelected.setText(item2);
                }else{
                    skillSelected.setText(item2.substring(0, item2.length() - 2));
                }
            }

        });

        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Clear skills", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < checkedItemsZ.length; i++) {
                    checkedItemsZ[i] = false;
                    mUserItems.clear();
                    skillSelected.setText("");
                }
            }
        });
        AlertDialog mDialog = builder.create();
        mDialog.show();
    }

    private void uploadImage(final String companyName, final String uid) {
        if (checkEducRequirement.isChecked()){
            zz="true";
        }else{
            zz="false";
        }
        final Intent intent = new Intent(this, a_EmployeeContentMainActivity.class);
        if (FilePathUri != null) {

            //final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Posting...");
            progressDialog.show();

            final StorageReference ref = storageReference.child(Storage_Path+ System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));

            ref.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String profileImageUrl=task.getResult().toString();
                                    String tempPostTitle = postTitle.getText().toString().trim();
                                    String tempPostDescription = postDescription.getText().toString();
                                    String tempPostLocation = Address;
                                    String tempTypeOfDisability1 = typeOfDisability_1;
                                    String tempTypeOfDisability2 = typeOfDisability_2;
                                    String tempTypeOfDisability3 = typeOfDisability_3;
                                    String tempTypeOfDisabilityMore = typeOfDisability_More;
                                    // final String city = spinnerCity.getSelectedItem().toString().trim();
                                    String tempJobSkill1 = jobSkill_1;
                                    String tempJobSkill2 = jobSkill_2;
                                    String tempJobSkill3 = jobSkill_3;
                                    String tempJobSkill4 = jobSkill_4;
                                    String tempJobSkill5 = jobSkill_5;
                                    String tempJobSkill6 = jobSkill_6;
                                    String tempJobSkill7 = jobSkill_7;
                                    String tempJobSkill8 = jobSkill_8;
                                    String tempJobSkill9 = jobSkill_9;
                                    String tempJobSkill10 = jobSkill_10;

                                    rgEducAttainment = findViewById(R.id.rg_educ);
                                    final int selectedId = rgEducAttainment.getCheckedRadioButtonId();
                                    rbEduc = findViewById(selectedId);
                                    educAttainment = rbEduc.getText().toString();

                                    rgWorkExperience = findViewById(R.id.workexperience);
                                    final int selectExperience = rgWorkExperience.getCheckedRadioButtonId();
                                    rbWorkExperience = findViewById(selectExperience);
                                    workExperience = rbWorkExperience.getText().toString();

                                    final Spinner dropdown1 = findViewById(R.id.spinnerExpDate);

                                    String expDate = dropdown1.getSelectedItem().toString();

                                    if (expDate == week1) {
                                        cal.add(Calendar.WEEK_OF_YEAR, -1);
                                        cal.add(Calendar.WEEK_OF_YEAR, 2);
                                        expDate1 = format.format(cal.getTime());

                                    }else if (expDate == week2) {
                                        cal.add(Calendar.WEEK_OF_YEAR, -2);
                                        cal.add(Calendar.WEEK_OF_YEAR, 3);
                                        format.format(cal.getTime());
                                        expDate1 = format.format(cal.getTime());


                                    }else  if (expDate == week3) {
                                        cal.add(Calendar.WEEK_OF_YEAR, -3);
                                        cal.add(Calendar.WEEK_OF_YEAR, 4);
                                        format.format(cal.getTime());
                                        expDate1 = format.format(cal.getTime());

                                    }else  if (expDate == month1) {
                                        cal.add(Calendar.MONTH, 1);
                                        format.format(cal.getTime());
                                        expDate1 = format.format(cal.getTime());

                                    }else  if (expDate == month2) {
                                        cal.add(Calendar.MONTH, 2);
                                        format.format(cal.getTime());
                                        expDate1 = format.format(cal.getTime());

                                    } else if (expDate == month3) {
                                        cal.add(Calendar.MONTH, 3);
                                        format.format(cal.getTime());
                                        expDate1 = format.format(cal.getTime());

                                    } else if (expDate == month4) {
                                        cal.add(Calendar.MONTH, 4);
                                        format.format(cal.getTime());
                                        expDate1 = format.format(cal.getTime());

                                    } else if (expDate == month5) {
                                        cal.add(Calendar.MONTH, 5);
                                        format.format(cal.getTime());
                                        expDate1 = format.format(cal.getTime());

                                    } else if (expDate == month6) {
                                        cal.add(Calendar.MONTH, 6);
                                        format.format(cal.getTime());
                                        expDate1 = format.format(cal.getTime());

                                    } else if (expDate == month7) {
                                        cal.add(Calendar.MONTH, 7);
                                        format.format(cal.getTime());
                                        expDate1 = format.format(cal.getTime());

                                    } else if (expDate == month8) {
                                        cal.add(Calendar.MONTH, 8);
                                        format.format(cal.getTime());
                                        expDate1 = format.format(cal.getTime());

                                    } else if (expDate == month9) {
                                        cal.add(Calendar.MONTH, 9);
                                        format.format(cal.getTime());
                                        expDate1 = format.format(cal.getTime());

                                    } else if (expDate == month10) {
                                        cal.add(Calendar.MONTH, 10);
                                        format.format(cal.getTime());
                                        expDate1 = format.format(cal.getTime());

                                    } else if (expDate == month11) {
                                        cal.add(Calendar.MONTH, 11);
                                        format.format(cal.getTime());
                                        expDate1 = format.format(cal.getTime());

                                    }else  if (expDate == year1) {
                                        cal.add(Calendar.MONTH, 12);
                                        format.format(cal.getTime());
                                        expDate1 = format.format(cal.getTime());

                                    }else  if (expDate == unli) {
                                        expDate1 = "unlimited";

                                    }


                                    String tempPermission = permission;
                                    //  String expDate = setExpDate.getSelectedItem().toString();
                                    progressDialog.dismiss();
                                    for(int i=0;i<pwdPrimarySkills.length;i++){
                                        if(!(pwdPrimarySkills[i]==null)){
                                            countw++;
                                        }
                                    }
                                    numberOfPrimarySkills=countw;
                                    Date currentDate = Calendar.getInstance().getTime();
                                    SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy");
                                    String postDate = df.format(currentDate);
                                    categorySkill = primary_skillsCategory.getSelectedItem().toString();
                    //              @SuppressWarnings("VisibleForTests")
                                    Emp_PostJob_Information postJobInfo = new Emp_PostJob_Information(profileImageUrl, tempPostTitle,
                                            tempPostDescription,tempPostLocation, tempTypeOfDisability1, tempTypeOfDisability2,
                                            tempTypeOfDisability3,tempTypeOfDisabilityMore, tempJobSkill1, tempJobSkill2, tempJobSkill3
                                            ,tempJobSkill4, tempJobSkill5, tempJobSkill6, tempJobSkill7, tempJobSkill8, tempJobSkill9, tempJobSkill10,tempPermission,
                                            companyName, uid, expDate1, postDate, city, educAttainment, workExperience, categorySkill,
                                            primarySkill1, primarySkill2, primarySkill3, primarySkill4, primarySkill5, primarySkill6, primarySkill7,
                                            primarySkill8, primarySkill9, primarySkill10, primarySkillOther, numberOfPrimarySkills, zz);
                                    String ImageUploadId = databaseReference.push().getKey();
                                    databaseReference.child(ImageUploadId).setValue(postJobInfo);
                                    databaseReference.child(ImageUploadId).child("yearsOfExperience").setValue(years);
                                }
                            });
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Emp_PostJob.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                            progressDialog.setCancelable(false);
                        }
                    });
        }
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

}