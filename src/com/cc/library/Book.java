package com.cc.library;

import java.util.Objects;

public class Book {
	static int counter=0;
	private final int bookID;
	private String bookName;
	private String bookAuthor;
	private int quantity;
	
	public Book(String bookName, String bookAuthor, int quantity) {
		super();
		this.bookName = bookName;
		this.bookAuthor = bookAuthor;
		this.quantity = quantity;
		this.bookID = ++counter;
	}
	
	public Book(int ID) {
		super();
		this.bookID = ID;
	}
	
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Book(String bookName) {
		this(bookName, "Author not provided", 0);
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getBookAuthor() {
		return bookAuthor;
	}

	public void setBookAuthor(String bookAuthor) {
		this.bookAuthor = bookAuthor;
	}

	public int getBookID() {
		return bookID;
	}

	@Override
	public String toString() {
		return "Book [bookID=" + bookID + ", bookName=" + bookName + ", bookAuthor=" + bookAuthor + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(bookID);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		return bookID == other.bookID;
	}
	
	
	
	
	
	
}
