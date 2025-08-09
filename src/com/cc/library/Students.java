package com.cc.library;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Students {
	
	List<Student> studentList = new ArrayList<Student>();
	static Scanner sc = new Scanner(System.in);
	
	/**
	 * Registers a new student by prompting the user for a name and adding the student to the list.
	 */
	public void registerStudent() {
		Student student = new Student(askString("Whats your name?"));
		this.studentList.add(student);
		System.out.println("Student registered successfully.");
	}
	
	/**
	 * Prompts the user with the specified message and returns the input as a string.
	 *
	 * @param prompt the message to display to the user
	 * @return the user's input as a string
	 */
	public static String askString(String prompt){
		System.out.println(prompt);
		return sc.nextLine();
		
	}
	
	/**
	 * Searches for a student in the list by their student ID.
	 *
	 * @param studentID the ID of the student to search for
	 * @return the Student object with the matching ID, or null if no such student exists
	 */
	public Student searchStudent(int studentID) {
		for(Student student:studentList) {
			if(student.getStudentID() == studentID) {
				return student;
			}
		}
		System.out.println("No student found with ID: " + studentID);
		return null;
	}

	/**
	 * Displays all registered students in the system.
	 *
	 * Prints a message if no students are registered; otherwise, lists each student's details.
	 */
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
