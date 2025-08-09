package com.cc.library;

//Java Program to Illustrate Application CLass
//To Create The Menu For the Program

//Importing required classes
import java.util.Scanner;

//Class
public class Library {

 /**
  * Entry point for the library management system application.
  *
  * Launches a console-based menu that allows users to manage books and students, including adding, updating, searching, displaying, renting, and returning books, as well as registering and displaying students. The application continues to prompt for user input until the exit option is selected.
  *
  * @param args command-line arguments (not used)
  */
 public static void main(String[] args)
 {
     // Creating object of Scanner class
     // to take input from user
     Scanner input = new Scanner(System.in);

     // Displaying menu
     System.out.println(
         "********************Welcome to the GFG Library!********************");
     System.out.println(
         "                  Select From The Following Options:               ");
     System.out.println(
         "**********************************************************************");

     // Creating object of book class
     Books ob = new Books();
     // Creating object of students class
     Students obStudent = new Students();

     int choice;
     //int searchChoice;

     // Creating menu
     // using do-while loop
     do {

         dispMenu();
         choice = input.nextInt();

         // Switch case
         switch (choice) {
         
         case 0:
        	 System.out.println("Thank you for using the library system. Goodbye!");
        	 break;

             // Case
         case 1:
             ob.addBook();
             break;

             // Case
         case 2:
             ob.updateBook();
             break;

         // Case
         case 3:
        	 
        	 /*
             System.out.println(
                 " press 1 to Search with Book Serial No.");
             System.out.println(
                 " Press 2 to Search with Book's Author Name.");
             searchChoice = input.nextInt();

             // Nested switch
             switch (searchChoice) {

                 // Case
             case 1:
                 ob.searchBySno();
                 break;

                 // Case
             case 2:
                 ob.searchByAuthorName();
             }
             */
			 ob.searchBook();
             break;

             // Case
         case 4:
             ob.showAllBooks();
             break;

             // Case
         case 5:
             
             obStudent.registerStudent();
             break;

             // Case
         case 6:
             obStudent.showAllStudents();
             break;

             // Case
         case 7:
             //obStudent.checkOutBook(ob);
        	 ob.rentAbook();
             break;

             // Case
         case 8:
             //obStudent.checkInBook(ob);
        	 ob.returnAbook();
             break;

             // Default case that will execute for sure
             // if above cases does not match
         default:

             // Print statement
             System.out.println("ENTER BETWEEN 0 TO 8.");
         }

     }

     // Checking condition at last where we are
     // checking case entered value is not zero
     while (choice != 0);
 }

 /**
  * Displays the main menu options for the library management system and prompts the user for input.
  */
 private static void dispMenu() {
	// TODO Auto-generated method stub
	System.out.println(
		 "**********************************************************************");
	 System.out.println("1. Add Book");
	 System.out.println("2. Update Book");
	 System.out.println("3. Search Book");
	 System.out.println("4. Show All Books");
	 System.out.println("5. Register Student");
	 System.out.println("6. Show All Students");
	 System.out.println("7. Rent a Book");
	 System.out.println("8. Return a Book");
	 System.out.println("0. Exit");
	 System.out.print("Enter your choice: ");
	
 }
}
