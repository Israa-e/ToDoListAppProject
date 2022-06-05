package com.example.todolistapp.Fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.Adapter.ToDoAdapter;
import com.example.todolistapp.Adds.AddNewTask;
import com.example.todolistapp.HomePageActivity;
import com.example.todolistapp.Model.ToDoModel;
import com.example.todolistapp.Adds.OnDialogCloseListener;
import com.example.todolistapp.R;
import com.example.todolistapp.Adds.TouchHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class _TaskFragment extends Fragment implements OnDialogCloseListener {
    View view;
    public static final String TAGS = "AddNewTask";
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private FirebaseFirestore firebaseFirestore;
    private ToDoAdapter toDoAdapter;
    Button mBtnSave;
    EditText task;
    TextView no_task;
    ImageView empty_imageView_task;
    private ImageButton star_btn;
    private ToDoModel toDoModel;
    private List<ToDoModel> toDoModels;
    private ListenerRegistration listenerRegistration;
    FirebaseAuth firebaseAuth;
    SharedPreferences sharedPreferences;


    public _TaskFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment___task, container, false);

        _IdFind();
        recycler_Fun();
        ShowData();
        floatingActionButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.rotation));
        return view;
    }


    public void _IdFind() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerView_cat);
        task = view.findViewById(R.id.task_ed);
        mBtnSave = view.findViewById(R.id.save);
        no_task = view.findViewById(R.id.no_task);
        empty_imageView_task = view.findViewById(R.id.empty_imageView_task);
        floatingActionButton = view.findViewById(R.id.floatingActionButton_cat);
        star_btn = view.findViewById(R.id.starBtn);
    }

    public void recycler_Fun() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AddNewTask addNewTask = (new AddNewTask()).newInstance();
        floatingActionButton.setOnClickListener(view -> {
            addNewTask.show(getParentFragmentManager(), AddNewTask.TAG);
//            new Handler().postDelayed(() ->{
//                addNewTask.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
//                        toDoModels.clear();
//                    }
//                });
//            }, 1000);

        });
        toDoModels = new ArrayList<>();
        toDoAdapter = new ToDoAdapter((HomePageActivity) getContext(), getActivity(), toDoModels);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(toDoAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(toDoAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager lManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstElementPosition = lManager.findFirstVisibleItemPosition();
                Log.d("TAG", "onScrolled: " + firstElementPosition);

                if (firstElementPosition < 0) {
                    empty_imageView_task.setVisibility(View.VISIBLE);
                    no_task.setVisibility(View.VISIBLE);
                } else {
                    empty_imageView_task.setVisibility(View.GONE);
                    no_task.setVisibility(View.GONE);
                }
            }

        });
    }

    class updateRec extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            toDoModels.clear();

        }
    }

    public void updateAdapter(List<ToDoModel> mDataList) {
        this.toDoModels = mDataList;
        toDoAdapter.notifyDataSetChanged();
    }

    public void updateListView() {
        toDoAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void ShowData() {
        Query query = firebaseFirestore.collection("task").orderBy("time", Query.Direction.DESCENDING);

        listenerRegistration = query.addSnapshotListener((value, error) -> onEvent(value, error));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onDialogCloseListener(DialogInterface dialogInterface) {
        System.out.println("this is y ");

        toDoModels.clear();
        ShowData();
        toDoAdapter.notifyDataSetChanged();
    }

    int x = 0;

    @SuppressLint("NotifyDataSetChanged")
    private void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
        for (DocumentChange documentChange : Objects.requireNonNull(value).getDocumentChanges()) {
            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                String id = documentChange.getDocument().getId();
                ToDoModel Model = documentChange.getDocument().toObject(ToDoModel.class).withId(id);
                toDoModels.add(Model);
                toDoAdapter.notifyDataSetChanged();
                x++;
            }
        }
        listenerRegistration.remove();
        System.out.println("this is x " + x);
    }
}