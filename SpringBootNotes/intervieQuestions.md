# Creating Notes for Spring Boot Examples 

## How to connect Jpa with mysql and create one table using JPa

### Code for java files 
package com.rahul.Spring_Boot_Examples;

import com.rahul.Spring_Boot_Examples.Dao.StudentRepo;
import com.rahul.Spring_Boot_Examples.entity.Student;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

@SpringBootApplication
public class SpringBootExamplesApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(SpringBootExamplesApplication.class, args);

		 StudentRepo studentRepo=context.getBean(StudentRepo.class);

		 Student student1=new Student();
		 student1.setStudentName("Rahul");
		 student1.setGrade("A");
		 student1.setMarks(90);
		 student1.setSubject("Maths");
		 student1.setStudentrollNo(19);
		 student1.setResult("Pass");

		 studentRepo.save(student1);





	}

}


package com.rahul.Spring_Boot_Examples.entity;

import jakarta.persistence.*;

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

package com.rahul.Spring_Boot_Examples.Dao;

import com.rahul.Spring_Boot_Examples.entity.Student;
import org.springframework.data.repository.CrudRepository;

public interface StudentRepo extends CrudRepository<Student,Integer> {
}



### pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.5.6</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.rahul</groupId>
	<artifactId>Spring-Boot-Examples</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>Spring-Boot-Examples</name>
	<description>Practicing Examples</description>
	<url/>
	<licenses>
		<license/>
	</licenses>
	<developers>
		<developer/>
	</developers>
	<scm>
		<connection/>
		<developerConnection/>
		<tag/>
		<url/>
	</scm>
	<properties>
		<java.version>17</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>

		<!-- MySQL Connector -->
		<dependency>
			<groupId>com.mysql</groupId>
			<artifactId>mysql-connector-j</artifactId>
			<version>8.3.0</version>
			<scope>runtime</scope>
		</dependency>


	</dependencies>



	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>



### application.properties 
spring.application.name=Spring-Boot-Examples
server.port=2020

# For datasouce Configuration
# JDBC URL of the MySQL database
spring.datasource.url=jdbc:mysql://localhost:3306/test?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC

# Username for the database
spring.datasource.username=root

# Password for the database
spring.datasource.password=Rahul@3780

# MySQL JDBC driver
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Dialect tells Hibernate how to convert HQL/JPQL to SQL for MySQL
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect

# Schema generation strategy (create, update, create-drop, validate, none)
spring.jpa.hibernate.ddl-auto=update

# Show SQL queries in the console
spring.jpa.show-sql=true

# Format SQL logs for readability
spring.jpa.properties.hibernate.format_sql=true


Defining Repository Interfaces
To define a repository interface, you first need to define a domain class-specific repository interface. The interface must extend Repository and be typed to the domain class and an ID type. If you want to expose CRUD methods for that domain type, you may extend CrudRepository, or one of its variants instead of Repository




## Crud Operation using Data JPA

### how to save single and multiple object of student using JPA

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




##   Custome finder Methods or Derived Query Method

### Basically we create our own methods to retrive data from Db using Data JPA.
###     Where we not required to write whole implementation of method Implementation will done by JPA we need to follow ###  method name correctly so it's working nicely. 

### Ex. findByName custome method we need to write in interface and call where we required.

        find: Introducer
        By:Criteria
        name:Property 


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


     Resource for check what keywords are reserved:
     https://docs.spring.io/spring-data/jpa/reference/repositories/query-keywords-reference.html#appendix.query.method.predicate




##  JPA Queries and Native Queries 




