package com.example.todolistapp.Adds;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.R;

public class            TaskDetailsActivity extends AppCompatActivity {

    EditText TaskDe;
    RecyclerView recyclerView2;
    TextView adDSubTask,deDateTx,deDate,deTimeTx,deTime,deRemindTx,deTimeAt,deRepeatTx,deRepeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        _IdFind();
    }
    public void _IdFind() {
//        adDSubTask =findViewById(R.id.adDSubTask);
        deDateTx = findViewById(R.id.deDateTx);
        deDate = findViewById(R.id.deDate);
        deTimeTx = findViewById(R.id.deTimeTx);
        deTime = findViewById(R.id.deTime);
        deRemindTx = findViewById(R.id.deRemindTx);
        deTimeAt = findViewById(R.id.deTimeAt);
        deRepeatTx = findViewById(R.id.deRepeatTx);
        deRepeat = findViewById(R.id.deRepeat);
    }
    public void recycler_Fun() {

    }
}