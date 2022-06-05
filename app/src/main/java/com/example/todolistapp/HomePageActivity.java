package com.example.todolistapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.todolistapp.Adds.AddCategory;
import com.example.todolistapp.DrawerThings.FAQActivity;
import com.example.todolistapp.DrawerThings.FavoritesActivity;
import com.example.todolistapp.DrawerThings.ThemesActivity;
import com.example.todolistapp.Fragments._CalendarFragment;
import com.example.todolistapp.Fragments._MineFragment;
import com.example.todolistapp.Fragments._TaskFragment;
import com.example.todolistapp.Log.Log_in_upActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;


public class HomePageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    SharedPreferences sharedPreferences;
    BottomNavigationView bottom_navigation;
    NavigationView nav_view;
    FragmentManager fm;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    String item;
    private FirebaseFirestore firebaseFirestore;
    ArrayList<String> category_ArrayList;
    ArrayAdapter<String> arrayAdapter;
    ValueEventListener valueEventListener;
    private ListenerRegistration listenerRegistration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        _FindId();
        _Listener();
        GetData();
        DrawerSpinner();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.log_out,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }else {
            if(item.getItemId() == R.id.LogOut){
                confirmDialog();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmDialog() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Log Out ?");
        builder.setMessage("Are you sure yoe want to Log Out ?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Toast.makeText(HomePageActivity.this, "Delete", Toast.LENGTH_SHORT).show();
            sharedPreferences =getSharedPreferences("logged_user",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user_id", "");
            editor.putString("user_email", "");
            editor.putString("user_fullName", "");
            editor.putString("user_phone", "");
            editor.commit();
            Intent intent = new Intent(HomePageActivity.this, Log_in_upActivity.class);
            startActivity(intent);
            finish();

        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }


    public void _FindId() {
        fm = getSupportFragmentManager();
        firebaseFirestore = FirebaseFirestore.getInstance();
        category_ArrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, category_ArrayList);

        bottom_navigation = findViewById(R.id.bottom_navigation);
        drawerLayout = findViewById(R.id.drawer_id);
        nav_view = findViewById(R.id.nv);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout, R.string.nav_open, R.string.nav_close);
    }

    public void DrawerSpinner() {
        AddCategory addCategory = new AddCategory();
        Spinner spinner = (Spinner) nav_view.getMenu().findItem(R.id.category_mine).getActionView();
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view1, int position, long id) {
                // Toast.makeText(HomePageActivity.this, "postion "+position, Toast.LENGTH_SHORT).show();
                item = String.valueOf(parent.getItemAtPosition(position));
                if (item.equals("Create Category")) {
                    //  Toast.makeText(HomePageActivity.this, "postion "+item, Toast.LENGTH_SHORT).show();
                    addCategory.show(getSupportFragmentManager(), "");

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        // to make the Navigation drawer icon always appear on the action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        nav_view.setNavigationItemSelectedListener(this);
    }



    @SuppressLint("NonConstantResourceId")
    public void _Listener() {
        bottom_navigation.setSelectedItemId(R.id.page_task); // change to whichever id should be default
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.mvc, new _TaskFragment()).commit();
        setTitle("Tasks");
        bottom_navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.page_task:
                    _TaskFragment taskFragment = (_TaskFragment) getSupportFragmentManager().findFragmentById(R.id._task_fragment);

                    // Check if the tab fragment is available
                    if (taskFragment != null) {
                        taskFragment.updateListView();
                        // Call your method in the TabFragment
                    }
                    FragmentTransaction ft1 = fm.beginTransaction();
                    ft1.replace(R.id.mvc, new _TaskFragment()).commit();
                    setTitle("Tasks");

                    break;
                case R.id.page_Calender:
                    FragmentTransaction ftc = fm.beginTransaction();
                    ftc.replace(R.id.mvc, new _CalendarFragment()).commit();
                    setTitle("Calender");
                    break;

                case R.id.page_mine:
                    FragmentTransaction ftm = fm.beginTransaction();
                    ftm.replace(R.id.mvc, new _MineFragment()).commit();
                    setTitle("Mine");
                    break;
            }
            return true;
        });
    }

    public void GetData() {
        Query query = firebaseFirestore.collection("categories").orderBy("time", Query.Direction.DESCENDING);

        listenerRegistration = query.addSnapshotListener(this::onEvent);
    }
    @SuppressLint("NotifyDataSetChanged")
    private void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
        for (DocumentChange documentChange : Objects.requireNonNull(value).getDocumentChanges()) {
            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                String category = documentChange.getDocument().getString("category_name");
                category_ArrayList.add(category);
                arrayAdapter.notifyDataSetChanged();
            }
        }
        category_ArrayList.add("Create Category");
        listenerRegistration.remove();
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.StarTasks_mine:
                startActivity(new Intent(this, FavoritesActivity.class));
                setTitle("Favorites");
                break;
            case R.id.category_mine:
                Toast.makeText(this, "category entered", Toast.LENGTH_SHORT).show();

                break;
            case R.id.work_mine:
                break;

            case R.id.Birthday_mine:

                break;
            case R.id.Theme_mine:
                startActivity(new Intent(this, ThemesActivity.class));
                setTitle("Theme");
                break;
            case R.id.FAQ_mine:
                startActivity(new Intent(this, FAQActivity.class));
                setTitle("FAQ");
                break;
        }
        return true;
    }
}