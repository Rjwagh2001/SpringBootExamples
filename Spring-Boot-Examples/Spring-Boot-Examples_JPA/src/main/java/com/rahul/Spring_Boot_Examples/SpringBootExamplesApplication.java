package com.rahul.Spring_Boot_Examples;

import com.rahul.Spring_Boot_Examples.Dao.StudentRepo;
import com.rahul.Spring_Boot_Examples.entity.Student;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class SpringBootExamplesApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(SpringBootExamplesApplication.class, args);

		StudentRepo studentRepo=context.getBean(StudentRepo.class);

		//Craeting Multiple user  Student1 to student 4

		Student student1=new Student();
		student1.setStudentName("Rahul");
		student1.setGrade("A");
		student1.setMarks(90);
		student1.setSubject("Maths");
		student1.setStudentrollNo(19);
		student1.setResult("Pass");


		Student student2=new Student();
		student2.setStudentName("Ram");
		student2.setSubject("Maths");
		student2.setStudentrollNo(19);
		student2.setGrade("A");
		student2.setResult("Pass");
		student2.setMarks(90);


		Student student3 =new Student();
		student3.setStudentName("Ratan");
		student3.setSubject("Maths");
		student3.setStudentrollNo(19);
		student3.setGrade("A");
		student3.setResult("Pass");
		student3.setMarks(90);

		Student student4 =new Student();
		student4.setStudentName("Rakesh");
		student4.setSubject("Maths");
		student4.setStudentrollNo(19);
		student4.setGrade("A");
		student4.setResult("Pass");
		student4.setMarks(90);

		//Saving an single user
		studentRepo.save(student1);

		//Saving an multiple user
		Iterable<Student> allstudent=List.of(student1,student2,student3,student4);
		studentRepo.saveAll(allstudent);


		/* Update exisiting user by id

		//first find by id
		Optional<Student> optional=studentRepo.findById(1);
        Student student= optional.get();

		System.out.println("Before :" +student);

		//Modified using getter setter
		student.setStudentrollNo(18);

		//user is saved using save method
		studentRepo.save(student);
		System.out.println("After :" +student);


		 */

       /* Deleting an single and multiple and all user

		//deleting by single user by id
		//studentRepo.deleteById(1);

		Iterable<Student> allPrint=studentRepo.findAll();
		allPrint.forEach(Student->{
			System.out.println(Student);
		});


		//delete all using iterable ; first get all using iterable and then use for it ads delete
		studentRepo.deleteAll(allPrint);

        */


        /*
        // Cutome finder Methods or Dervived Methods using JPA

		//calling method to get response write in interface
		Iterable<Student> studentName=studentRepo.findByStudentName("Rahul");
		studentName.forEach(Student1->
		{
			System.out.println(Student1);
		});


		Iterable<Student> studentName1=studentRepo.findByStudentNameOrResult("Raj","");
		studentName1.forEach(Student1->
		{
			System.out.println(Student1);
		});


		Iterable<Student> studentName2=studentRepo.findByStudentNameAndResult("Rahul","Pass");
		studentName2.forEach(Student1->
		{
			System.out.println(Student1);
		});



         */



		//Native Query and JPA Query
		Iterable<Student> getAll=studentRepo.getAlluserbySQl();
		getAll.forEach(Student->{
			System.out.println(Student);
		});


		System.out.println("------------------------------------");


//		Iterable<Student> getAll1=studentRepo.getAlluser();
//		getAll1.forEach(Student->{
//			System.out.println(Student);
//		});









	}

}
