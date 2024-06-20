package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private EditText metId, metPass, metNickName, metAge, metIntroduction;
    private Button mBtnRegister, mBtnSelectPhoto;
    private Uri photoUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    private static final String USER_ACCOUNTS = "userAccount";

    private String userNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        metId = findViewById(R.id.et_id);
        metPass = findViewById(R.id.et_pass);
        metNickName = findViewById(R.id.et_nickname);
        metAge = findViewById(R.id.et_age);
        metIntroduction = findViewById(R.id.et_introduction); // Add this line
        mBtnRegister = findViewById(R.id.btn_register);
        mBtnSelectPhoto = findViewById(R.id.btn_select_photo);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        mBtnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            photoUri = data.getData();
        }
    }

    private void registerUser() {
        String strEmail = metId.getText().toString().trim();
        String strPwd = metPass.getText().toString().trim();
        userNickname = metNickName.getText().toString().trim();
        String strAge = metAge.getText().toString().trim();
        String strIntroduction = metIntroduction.getText().toString().trim(); // Add this line

        if (TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strPwd)) {
            Toast.makeText(RegisterActivity.this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        if (photoUri != null) {
                                            uploadPhoto(firebaseUser, strPwd, strAge, strIntroduction); // Update this line
                                        } else {
                                            saveUserProfile(firebaseUser, strPwd, strAge, strIntroduction, null); // Update this line
                                        }
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "인증 이메일 전송 실패: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "회원가입 실패: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void uploadPhoto(FirebaseUser firebaseUser, String password, String age, String introduction) { // Update this line
        StorageReference fileReference = mStorageRef.child("profilePics/" + firebaseUser.getUid() + ".jpg");

        fileReference.putFile(photoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("UPLOAD_PHOTO", "사진 업로드 성공");
                    fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                Log.d("UPLOAD_PHOTO", "사진 URL 가져오기 성공: " + downloadUri.toString());
                                saveUserProfile(firebaseUser, password, age, introduction, downloadUri.toString()); // Update this line
                            } else {
                                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                Toast.makeText(RegisterActivity.this, "사진 URL 가져오기 실패: " + errorMessage, Toast.LENGTH_LONG).show();
                                Log.e("UPLOAD_PHOTO", "사진 URL 가져오기 실패: " + errorMessage);
                            }
                        }
                    });
                } else {
                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                    Toast.makeText(RegisterActivity.this, "사진 업로드 실패: " + errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("UPLOAD_PHOTO", "사진 업로드 실패: " + errorMessage);
                }
            }
        });
    }

    private void saveUserProfile(FirebaseUser firebaseUser, String password, String age, String introduction, @Nullable String photoUrl) { // Update this line
        UserAccount account = new UserAccount();
        account.setIdToken(firebaseUser.getUid());
        account.setEmailId(firebaseUser.getEmail());
        account.setPassword(password);
        account.setNickname(userNickname);
        account.setAge(age);
        account.setIntroduction(introduction); // Add this line
        if (photoUrl != null) {
            account.setProfileImageUrl(photoUrl);
        }

        mDatabaseRef.child(USER_ACCOUNTS).child(firebaseUser.getUid()).setValue(account)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "회원가입에 성공했습니다. 인증 이메일을 확인해 주세요.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "회원가입 데이터 저장 실패: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}


/*package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private EditText metId, metPass, metNickName, metAge;
    private Button mBtnRegister, mBtnSelectPhoto;
    private Uri photoUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    private static final String USER_ACCOUNTS = "userAccount";

    private String userNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        metId = findViewById(R.id.et_id);
        metPass = findViewById(R.id.et_pass);
        metNickName = findViewById(R.id.et_nickname);
        metAge = findViewById(R.id.et_age);
        mBtnRegister = findViewById(R.id.btn_register);
        mBtnSelectPhoto = findViewById(R.id.btn_select_photo);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        mBtnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            photoUri = data.getData();
        }
    }

    private void registerUser() {
        String strEmail = metId.getText().toString().trim();
        String strPwd = metPass.getText().toString().trim();
        userNickname = metNickName.getText().toString().trim();
        String strAge = metAge.getText().toString().trim();

        if (TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strPwd)) {
            Toast.makeText(RegisterActivity.this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        if (photoUri != null) {
                                            uploadPhoto(firebaseUser, strPwd, strAge);
                                        } else {
                                            saveUserProfile(firebaseUser, strPwd, strAge, null);
                                        }
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "인증 이메일 전송 실패: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "회원가입 실패: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void uploadPhoto(FirebaseUser firebaseUser, String password, String age) {
        StorageReference fileReference = mStorageRef.child("profilePics/" + firebaseUser.getUid() + ".jpg");

        fileReference.putFile(photoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("UPLOAD_PHOTO", "사진 업로드 성공");
                    fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                Log.d("UPLOAD_PHOTO", "사진 URL 가져오기 성공: " + downloadUri.toString());
                                saveUserProfile(firebaseUser, password, age, downloadUri.toString());
                            } else {
                                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                Toast.makeText(RegisterActivity.this, "사진 URL 가져오기 실패: " + errorMessage, Toast.LENGTH_LONG).show();
                                Log.e("UPLOAD_PHOTO", "사진 URL 가져오기 실패: " + errorMessage);
                            }
                        }
                    });
                } else {
                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                    Toast.makeText(RegisterActivity.this, "사진 업로드 실패: " + errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("UPLOAD_PHOTO", "사진 업로드 실패: " + errorMessage);
                }
            }
        });
    }

    private void saveUserProfile(FirebaseUser firebaseUser, String password, String age, @Nullable String photoUrl) {
        UserAccount account = new UserAccount();
        account.setIdToken(firebaseUser.getUid());
        account.setEmailId(firebaseUser.getEmail());
        account.setPassword(password);
        account.setNickname(userNickname);
        account.setAge(age);
        if (photoUrl != null) {
            account.setPhotoUrl(photoUrl);
        }

        mDatabaseRef.child(USER_ACCOUNTS).child(firebaseUser.getUid()).setValue(account)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "회원가입에 성공했습니다. 인증 이메일을 확인해 주세요.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "회원가입 데이터 저장 실패: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
*/
