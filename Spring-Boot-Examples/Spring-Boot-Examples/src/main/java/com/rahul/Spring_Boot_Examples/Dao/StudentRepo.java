package com.rahul.Spring_Boot_Examples.Dao;

import com.rahul.Spring_Boot_Examples.entity.Student;
import org.aspectj.apache.bcel.util.Repository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface StudentRepo extends CrudRepository<Student,Integer> {


    //write custome method to find student by name
    public List<Student> findByStudentName(String name);

    public List<Student> findByStudentNameOrResult(String name,String result);

    public List<Student> findByStudentNameAndResult(String name,String result);

    //Using an JPA
    @Query("select u from Student u")
    public List<Student> getAlluser();



    //using an Sql query (native query)
    @Query(value = "select * from student_test",nativeQuery = true)
    public List<Student> getAlluserbySQl();

}


