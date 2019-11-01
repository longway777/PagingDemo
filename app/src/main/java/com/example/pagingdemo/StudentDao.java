package com.example.pagingdemo;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface StudentDao {
    @Insert
    void insertStudents(Student... students);

    @Query("DELETE FROM student_table")
    void deleteAllStudents();

    @Query("SELECT * FROM student_table ORDER BY id")
    DataSource.Factory<Integer, Student> getAllStudents();
}
