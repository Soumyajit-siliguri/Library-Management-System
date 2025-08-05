package com.cc.library;

import java.util.Objects;

public class Student {
	
	static int counter=0;
	private final int studentID;
	private String studentName;
	public Student(String studentName) {
		super();
		this.studentName = studentName;
		this.studentID=++counter;
	}
	@Override
	public int hashCode() {
		return Objects.hash(studentID);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Student other = (Student) obj;
		return studentID == other.studentID;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public int getStudentID() {
		return studentID;
	}
	@Override
	public String toString() {
		return "Student [studentID=" + studentID + ", studentName=" + studentName + "]";
	}
	
	
	

}
