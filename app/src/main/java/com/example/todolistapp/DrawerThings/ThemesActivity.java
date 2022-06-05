package com.example.todolistapp.DrawerThings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todolistapp.HomePageActivity;
import com.example.todolistapp.R;

public class ThemesActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn1, btn2;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //SharedPreferences sharedPreferences = getSharedPreferences("", Context.MODE_PRIVATE);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int selectedThemes = sharedPreferences.getInt("SelectedTheme", 0);
        setThemesDynminc(selectedThemes);
        setTitle("Theme");

        setContentView(R.layout.activity_themes);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);

    }

    private void setThemesDynminc(int selectedThemes) {
        switch (selectedThemes) {
            case 1:
                ThemesActivity.this.setTheme(R.style.Theme1);
                break;
            case 2:
                ThemesActivity.this.setTheme(R.style.Theme2);
                break;
        }
    }

    @Override
    public void onClick(View v) {
         editor = sharedPreferences.edit();
        switch (v.getId()) {
            case R.id.btn1:
                editor.putInt("SelectedTheme" , 1);
                break;
            case R.id.btn2:
                editor.putInt("SelectedTheme" , 2);
                break;
        }
        editor.apply();
        startActivity(new Intent(this , HomePageActivity.class));
        finish();
    }
}