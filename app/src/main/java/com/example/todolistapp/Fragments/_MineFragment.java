package com.example.todolistapp.Fragments;


import static android.content.Context.MODE_PRIVATE;
import static com.example.todolistapp.Adds.AddNewTask.TAG;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.todolistapp.Model.ImageModel;
import com.example.todolistapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class _MineFragment extends Fragment {
    View view;
    SharedPreferences sharedPreferences;
    ImageView imageViewLogin;
    TextView UserName, TaskCompleted, TaskOpen, TaskNumber,TaskClose;
    private int requestCode = 1;
    private Uri imageUri;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Images");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();


    public _MineFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment__mine, container, false);
        _FindId();
        sharedPreferences = getActivity().getSharedPreferences("logged_user", MODE_PRIVATE);
        String user_email = sharedPreferences.getString("user_email", "");
        String user_fullName = sharedPreferences.getString("user_fullName", "");
        String user_phone = sharedPreferences.getString("user_phone", "");
        String user_id = sharedPreferences.getString("user_id", "");
        String image = sharedPreferences.getString("image","");
        if(image !=""){
            Uri myUri = Uri.parse(image);
            imageViewLogin.setImageURI(myUri);
        }
        UserName.setText(user_fullName);
        Log.d(TAG, "onCreateView: " + user_email);
        Log.d(TAG, "onCreateView: " + user_phone);
        Log.d(TAG, "onCreateView: " + user_fullName);
        Log.d(TAG, "onCreateView: " + user_id);
        _Listener();
        _fetchData();
        return view;
    }

    public void _FindId() {
        imageViewLogin = view.findViewById(R.id.imageViewLogin);
        UserName = view.findViewById(R.id.UserName);
        TaskCompleted = view.findViewById(R.id.TaskCompleted);
        TaskOpen = view.findViewById(R.id.TaskOpen);
        TaskNumber = view.findViewById(R.id.TaskNumber);
        TaskClose = view.findViewById(R.id.TaskClose);
        TaskNumber.setText("0");
        TaskCompleted.setText("0");
        TaskOpen.setText("0");
        TaskClose.setText("0");
    }
    public void  _fetchData(){
        String user_id = sharedPreferences.getString("user_id", "");
        final int[] numberTasks = {0};
        firestore.collection("task").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    int count = querySnapshot.size();
                    String tasksNumber= String.valueOf(count);
                    System.out.println("count"+count);


                    for (DocumentSnapshot document: querySnapshot.getDocuments()) {
                        if(document.getId() !=null){
                            Query query =firestore.collection("task").whereEqualTo("uid",user_id);
                            query.addSnapshotListener((value, error) -> {
                                for (DocumentChange documentChange :value.getDocumentChanges()){
                                    int countNumber = value.size();
                                    TaskNumber.setText(String.valueOf(countNumber));
                                    int status = Integer.parseInt(documentChange.getDocument().getData().get("status").toString());
                                    int statusNumber1=0;
                                    int statusNumber0=0;
                                    if (status==1){
                                        statusNumber1++;
                                    }
                                    TaskCompleted.setText(String.valueOf(statusNumber1));
                                    if(status==0){
                                        statusNumber0++;
                                    }
                                    TaskClose.setText(String.valueOf(statusNumber1));
                                    TaskOpen.setText(String.valueOf(statusNumber0));


                                    Log.d(TAG, "onEvent:status Number "+statusNumber1);
                                }
                            });
                        }else {
                            TaskNumber.setText("0");
                            TaskCompleted.setText("0");
                            TaskOpen.setText("0");
                            TaskClose.setText("0");
                        }
                    }
                }
            }
        });
    }

    public void _Listener() {
        imageViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageUri = data.getData();
        imageViewLogin.setImageURI(imageUri);
        imageViewLogin.setAlpha(1.0F);
        uploadPicture();

    }

    private void uploadPicture2() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        StorageReference file = storageReference.child(System.currentTimeMillis() + "." + getFile(imageUri));
        pd.show();
        file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageModel imageModel = new ImageModel(imageUri.toString());
                String sModel = reference.push().getKey();
                reference.child(sModel).setValue(imageModel);

                Toast.makeText(getContext(), "Image is Uploading", Toast.LENGTH_SHORT).show();
                pd.dismiss();


            }
        });


    }

    private void uploadPicture() {
        final ProgressDialog pd = new ProgressDialog(getContext());

        StorageReference file = storageReference.child(System.currentTimeMillis() + "." + getFile(imageUri));
        file.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            pd.show();
            file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Task<Uri> downloadUri = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    String generatedFilePath = downloadUri.toString(); /// The string(file link) that you need

                    ImageModel imageModel = new ImageModel(imageUri.toString());
                    String sModel = reference.push().getKey();
                    reference.child(sModel).setValue(imageModel);
                    Log.d(TAG, "onSuccess: " + sModel);
                    Toast.makeText(getContext(), "Image is Uploading", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onSuccess: "+imageUri.toString());
                    pd.dismiss();
                    sharedPreferences = getActivity().getSharedPreferences("logged_user", MODE_PRIVATE);
                    String user_id = sharedPreferences.getString("user_id", "");
                    String id = FirebaseAuth.getInstance().getUid();
                    Log.d(TAG, "onSuccess: "+FirebaseAuth.getInstance().getUid());
                    firestore.collection("users").document("fvg60GNIdDchIh04LIrTlquIUY92").update("image",imageUri.toString());
                    firestore.collection("users").addSnapshotListener((value, error) -> {
                        if (error != null) {
                            Log.d("TAG", "onEvent: error");
                        }
                        for (DocumentChange documentChange : value.getDocumentChanges()) {
                            String image = documentChange.getDocument().getData().get("image").toString();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("image", image);
                            editor.apply();

                        }
                    });
                }
            });
        }).addOnProgressListener(snapshot -> {
            double progressPercent = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
            pd.setMessage("Progress" + (int) progressPercent + "%");
        }).addOnFailureListener(e -> {
            pd.dismiss();
            Toast.makeText(getContext(), "Failed To Upload The Image", Toast.LENGTH_LONG).show();
        });
    }

    private String getFile(Uri imageUri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }
    
    
    
}