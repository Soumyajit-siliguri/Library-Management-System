package com.cc.library;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Students {
	
	List<Student> studentList = new ArrayList<Student>();
	static Scanner sc = new Scanner(System.in);
	
	public void registerStudent() {
		Student student = new Student(askString("Whats your name?"));
		this.studentList.add(student);
		System.out.println("Student registered successfully.");
	}
	
	public static String askString(String prompt){
		System.out.println(prompt);
		return sc.nextLine();
		
	}
	
	public Student searchStudent(int studentID) {
		for(Student student:studentList) {
			if(student.getStudentID() == studentID) {
				return student;
			}
		}
		System.out.println("No student found with ID: " + studentID);
		return null;
	}

	public void showAllStudents() {
		// TODO Auto-generated method stub
		if(studentList.isEmpty()) {
			System.out.println("No students registered yet.");
		} else {
			System.out.println("Registered Students:");
			for(Student student : studentList) {
				System.out.println(student);
			}
		}
		
	}
	
	

}
