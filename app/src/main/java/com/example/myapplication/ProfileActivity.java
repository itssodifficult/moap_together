package com.example.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity"; // 로그 태그 추가

    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private FirebaseAuth mFirebaseAuth;

    //private EditText metNickName, metAge;
    private EditText metNickName, metAge, metIntroduction;
    private ImageView mProfileImage;
    private Button mBtnSave, mBtnSelectPhoto;
    private Uri photoUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_READ_STORAGE = 2;
    private static final int REQUEST_READ_MEDIA_IMAGES = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("userAccount");
        mStorageRef = FirebaseStorage.getInstance().getReference("profilePics");

        metNickName = findViewById(R.id.et_nickname);
        metAge = findViewById(R.id.et_age);
        mProfileImage = findViewById(R.id.img_profile);
        mBtnSave = findViewById(R.id.btn_save);
        mBtnSelectPhoto = findViewById(R.id.btn_select_photo);
        metIntroduction = findViewById(R.id.et_introduction);


        if (currentUser != null) {
            loadUserProfile(currentUser);
            loadUserIntroduction(currentUser); // 사용자의 한 줄 소개 불러오기 추가
        }

        mBtnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPermissionContextPopup();
            }
        });

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile(currentUser);
                saveUserIntroduction(currentUser); // 사용자의 한 줄 소개 저장 추가
            }
        });
    }

    private void showPermissionContextPopup() {
        Log.d(TAG, "showPermissionContextPopup() 호출됨");
        new AlertDialog.Builder(this)
                .setTitle("권한이 필요합니다.")
                .setMessage("프로필 이미지를 바꾸기 위해서는 갤러리 접근 권한이 필요합니다.")
                .setPositiveButton("동의하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(ProfileActivity.this,
                                new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_READ_MEDIA_IMAGES);
                        openFileChooser();
                    }
                })
                .setNegativeButton("취소하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 취소하기 클릭 시 아무 작업 없음
                    }
                })
                .show();
    }
    /*
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                               @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == REQUEST_READ_MEDIA_IMAGES) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openFileChooser();
                } else {
                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
            }
        }
    */
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
            mProfileImage.setImageURI(photoUri);
        }
    }

    private void loadUserProfile(FirebaseUser currentUser) {
        mDatabaseRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserAccount userAccount = snapshot.getValue(UserAccount.class);
                    if (userAccount != null) {
                        metNickName.setText(userAccount.getNickname());
                        metAge.setText(userAccount.getAge());
                        if (userAccount.getProfileImageUrl() != null && !userAccount.getProfileImageUrl().isEmpty()) {
                            Glide.with(ProfileActivity.this).load(userAccount.getProfileImageUrl()).into(mProfileImage);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserProfile(FirebaseUser currentUser) {
        if (currentUser == null) return;

        String newNickname = metNickName.getText().toString().trim();
        String newAge = metAge.getText().toString().trim();

        if (photoUri != null) {
            uploadPhoto(currentUser, newNickname, newAge);
        } else {
            updateUserProfile(currentUser, newNickname, newAge, null);
        }
    }

    private void uploadPhoto(FirebaseUser currentUser, String newNickname, String newAge) {
        StorageReference fileReference = mStorageRef.child(currentUser.getUid() + ".jpg");

        fileReference.putFile(photoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                Log.d(TAG, "Photo URL: " + downloadUri.toString()); // 업로드된 사진의 URL 로그로 출력
                                updateUserProfile(currentUser, newNickname, newAge, downloadUri.toString());
                            } else {
                                Toast.makeText(ProfileActivity.this, "Failed to get photo URL", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ProfileActivity.this, "Photo upload failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUserProfile(FirebaseUser currentUser, String newNickname, String newAge, @Nullable String photoUrl) {
        DatabaseReference userRef = mDatabaseRef.child(currentUser.getUid());

        userRef.child("nickname").setValue(newNickname);
        userRef.child("age").setValue(newAge);
        if (photoUrl != null) {
            userRef.child("profileImageUrl").setValue(photoUrl);
        }

        Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
    }
    private void loadUserIntroduction(FirebaseUser currentUser) {
        mDatabaseRef.child(currentUser.getUid()).child("introduction").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String introduction = snapshot.getValue(String.class);
                if (introduction != null) {
                    metIntroduction.setText(introduction);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load user introduction", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserIntroduction(FirebaseUser currentUser) {
        if (currentUser == null) return;

        String newIntroduction = metIntroduction.getText().toString().trim();
        mDatabaseRef.child(currentUser.getUid()).child("introduction").setValue(newIntroduction);
    }
}
