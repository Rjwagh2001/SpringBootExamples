package com.rahul.Spring_Boot_Examples.entity;

import jakarta.persistence.*;
import lombok.Data;

// for specifies class is an entity and is mapped to a database table.
@Data
@Entity
@Table(name = "Student_Test")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String studentName;
    private int studentrollNo;
    private int marks;
    private String subject;
    private String grade;
    private String result;


    @Override
    public String toString() {
        return "Sutudent{" +
                "id=" + id +
                ", studentName='" + studentName + '\'' +
                ", studentrollNo=" + studentrollNo +
                ", marks=" + marks +
                ", subject='" + subject + '\'' +
                ", grade='" + grade + '\'' +
                ", result=" + result +
                '}';
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getStudentrollNo() {
        return studentrollNo;
    }

    public void setStudentrollNo(int studentrollNo) {
        this.studentrollNo = studentrollNo;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String isResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }


}
