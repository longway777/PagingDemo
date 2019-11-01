package com.example.pagingdemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    Button buttonPopulate, buttonClear;
    StudentDao studentDao;
    StudentsDatabase studentsDatabase;
    MyPagedAdapter pagedAdapter;
    LiveData<PagedList<Student>> allStudentsLivePaged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        pagedAdapter = new MyPagedAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(pagedAdapter);
        studentsDatabase = StudentsDatabase.getInstance(this);
        studentDao = studentsDatabase.getStudentDao();
        allStudentsLivePaged = new LivePagedListBuilder<>(studentDao.getAllStudents(), 2)
                .build();
        allStudentsLivePaged.observe(this, new Observer<PagedList<Student>>() {
            @Override
            public void onChanged(final PagedList<Student> students) {
                pagedAdapter.submitList(students);
                students.addWeakCallback(null, new PagedList.Callback() {
                    @Override
                    public void onChanged(int position, int count) {
                        Log.d("myLog", "onChanged: " + students);
                    }

                    @Override
                    public void onInserted(int position, int count) {

                    }

                    @Override
                    public void onRemoved(int position, int count) {

                    }
                });
            }
        });

        buttonPopulate = findViewById(R.id.buttonPopulate);
        buttonClear = findViewById(R.id.buttonClear);

        buttonPopulate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Student[] students = new Student[1000];
                for (int i = 0; i < 1000; i++) {
                    Student student = new Student();
                    student.setStudentNumber(i);
                    students[i] = student;
                }
                new InsertAsyncTask(studentDao).execute(students);
                new InsertAsyncTask(studentDao).execute();
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ClearAsyncTask(studentDao).execute();
            }
        });


    }

    static class InsertAsyncTask extends AsyncTask<Student, Void, Void> {
        StudentDao studentDao;

        public InsertAsyncTask(StudentDao studentDao) {
            this.studentDao = studentDao;
        }

        @Override
        protected Void doInBackground(Student... students) {
            studentDao.insertStudents(students);
            return null;
        }
    }

    static class ClearAsyncTask extends AsyncTask<Void, Void, Void> {
        StudentDao studentDao;

        public ClearAsyncTask(StudentDao studentDao) {
            this.studentDao = studentDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            studentDao.deleteAllStudents();
            return null;
        }
    }
}
