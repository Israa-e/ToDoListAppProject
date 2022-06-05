package com.example.todolistapp.Adds;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.todolistapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddCategory extends AppCompatDialogFragment {

    private FirebaseFirestore firebaseFirestore;
    EditText categoryName;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        firebaseFirestore = FirebaseFirestore.getInstance();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.add_category, null);
        categoryName = view.findViewById(R.id.ed_category);

        builder.setView(view).setTitle("Test Dialog ")

                .setNegativeButton(" Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Map<String, Object> taskMap = new HashMap<>();
                        long timeStamp = System.currentTimeMillis();
                        String name = categoryName.getText().toString();
                        taskMap.put("category_name", name);
                        taskMap.put("time", timeStamp);

                        firebaseFirestore.collection("categories").add(taskMap).addOnCompleteListener(add_category -> {
                            if (add_category.isSuccessful()) {
                                Log.d("Successful Category Created", "onClick: ");
                            } else {
                                Log.d("Failed Category Created", "onClick: ");                            }
                        }).addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());

                    }
                });
        return builder.create();
    }

    //    ed_category = view.findViewById(R.id.ed_category);
//    btn_save = view.findViewById(R.id.btn_save);
//
//
//        if (ed_category.getText().toString().isEmpty()) {
//        btn_save.setEnabled(false);
//        btn_save.setBackgroundColor(Color.GRAY);
//    }
//    firebaseFirestore = FirebaseFirestore.getInstance();
//    boolean isUpdate = false;
//    final Bundle bundle = getArguments();
//        if (bundle != null) {
//        isUpdate = true;
//        String categoryText = bundle.getString("category_name");
//        id = bundle.getString("id");
//        ed_category.setText(categoryText);
//Map<String, Object> taskMap = new HashMap<>();
//    long timeStamp = System.currentTimeMillis();
//    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//    String uid = user.getUid();
//                taskMap.put("id", uid);
//                taskMap.put("category_name", categoryName);
//                taskMap.put("time", timeStamp);
//
//                firebaseFirestore.collection("categories").add(taskMap).addOnCompleteListener(add_category -> {
//        if (add_category.isSuccessful()) {
//            Toast.makeText(context, "Category Saved", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(context, Objects.requireNonNull(add_category.getException()).getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }).addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
//
//}
//    dismiss();
//});


}