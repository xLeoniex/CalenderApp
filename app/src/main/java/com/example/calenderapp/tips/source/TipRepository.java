package com.example.calenderapp.tips.source;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.calenderapp.tips.model.TipModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.ArrayList;
import java.util.List;

public class TipRepository {
    private FirebaseDatabase database;
    private DatabaseReference reference;

    private StorageReference storageRef;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;
    private MutableLiveData<List<TipModel>> tipModelList = new MutableLiveData<>();

    private MutableLiveData<Uri> currentUrl = new MutableLiveData<>();
    public TipRepository() {
        database = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = database.getReference("users").child(currentUser.getUid()).child("Tips");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("Images").child("Gallery");

    }
    public MutableLiveData<List<TipModel>> getTipModelList()
    {
        List<TipModel> TipModelListInRepository = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TipModel currnetTipModel = new TipModel();
                TipModelListInRepository.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    currnetTipModel = dataSnapshot.getValue(TipModel.class);
                    TipModelListInRepository.add(currnetTipModel);

                }
                tipModelList.postValue(TipModelListInRepository);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return tipModelList;
    }
    public void AddTipToDatabase(TipModel tipModel)
    {
        if(tipModel != null)
        {
            DatabaseReference pushref = reference.push();
            String mKey = pushref.getKey();
            tipModel.setTipId(mKey);
            pushref.setValue(tipModel);
        }
    }

    public MutableLiveData<Uri> uploadTipImageToStorage(Uri uri)
    {
        if(uri != null)
        {
            storageRef.child(uri.getLastPathSegment()).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageRef.child(uri.getLastPathSegment()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri url) {
                            currentUrl.setValue(url);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("UploadFails",e.toString());
                }
            });
        }else {
            currentUrl.setValue(null);
        }

        return currentUrl;
    }

}
