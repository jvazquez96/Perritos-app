package itesm.mx.perritos.pet;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import itesm.mx.perritos.R;

public class AddPetActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar tlToolbar;
    private Button btnPicture;
    private ImageView imgCover;
    private static final int RC_PHOTO_PICKER = 2;
    private Pet pet;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPetPhotosStorageReference;

    private EditText editName;
    private EditText editDescription;
    private EditText editGender;
    private EditText editAge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pets);
        tlToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tlToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Nueva mascota");
        btnPicture = (Button) findViewById(R.id.button_picture);
        btnPicture.setOnClickListener(this);
        imgCover = (ImageView) findViewById(R.id.image_pet);

        editName = (EditText) findViewById(R.id.edit_name);
        editDescription = (EditText) findViewById(R.id.edit_description);
        editGender = (EditText) findViewById(R.id.edit_gender);
        editAge = (EditText) findViewById(R.id.edit_age);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mPetPhotosStorageReference = mFirebaseStorage.getReference().child("pets_photos");
        pet = new Pet();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.confirm,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_confirm:
                pet.setName(editName.getText().toString());
                pet.setAge(Integer.valueOf(editAge.getText().toString()));
                pet.setDescription(editDescription.getText().toString());
                pet.setGender(editGender.getText().toString());
                Intent intent = new Intent();
                intent.putExtra("Pet",pet);
                setResult(RESULT_OK,intent);
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
        startActivityForResult(Intent.createChooser(intent,"Complete action using"),RC_PHOTO_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RC_PHOTO_PICKER) {
                Uri selectedImage = data.getData();
                StorageReference photoRef = mPetPhotosStorageReference.child(selectedImage.getLastPathSegment());

                photoRef.putFile(selectedImage).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUri = taskSnapshot.getDownloadUrl();
                        Glide.with(imgCover.getContext())
                                .load(downloadUri.toString())
                                .into(imgCover);
                        pet.setPhotoUrl(downloadUri.toString());
                    }
                });
            }
        }
    }
}
