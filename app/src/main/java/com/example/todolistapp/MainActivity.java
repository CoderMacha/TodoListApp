package com.example.todolistapp;



import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText editTextItem;
    private Button buttonAdd;
    private RecyclerView recyclerView;
    private TodoAdapter todoAdapter;
    private List<TodoItem> todoList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextItem = findViewById(R.id.editTextItem);
        buttonAdd = findViewById(R.id.buttonAdd);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        todoList = new ArrayList<>();
        todoAdapter = new TodoAdapter(this, todoList);
        recyclerView.setAdapter(todoAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("todos");

        buttonAdd.setOnClickListener(v -> {
            String title = editTextItem.getText().toString().trim();
            if (!TextUtils.isEmpty(title)) {
                String id = databaseReference.push().getKey();
                long timestamp = System.currentTimeMillis();
                TodoItem todo = new TodoItem(id, title, timestamp);
                if (id != null) {
                    databaseReference.child(id).setValue(todo).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            editTextItem.setText("");
                            Toast.makeText(MainActivity.this, "To-do item added", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to add item", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(MainActivity.this, "Please enter a to-do item", Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                todoList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    TodoItem todo = dataSnapshot.getValue(TodoItem.class);
                    if (todo != null) {
                        todoList.add(todo);
                    }
                }
                todoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}






