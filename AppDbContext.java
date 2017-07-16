/**
 * Program Name: 			AppDbContext.java
 * Coder: 				Joseph Ryu for SEC01
 * Date: 				Jul 5, 2017
 * Purpose:				This class is used by the model objects. All queries are performed here.  
 */
import java.sql.*;
import java.util.*;
public class AppDbContext
{
	// Class wide scope variables
	// Connection objects
	public Connection		myConn;
	public Statement 		myStmt;
	public ResultSet 		myRslt;

	// Connection info
	private final String 	URL 		= "jdbc:mysql://localhost:3306/INFO5051_Books?useSSL=false";
	private final String 	USER 		= "root";
	private final String 	PASSWORD 	= "password";
	
	public 	SqlQueryBuilder sqlBuilder;		// Manages SQL strings
	private Table 			currentTable;	// Indicates current table(model)	
	
	public static enum Table {
		AUTHOR,
		BOOK,
		BORROWER,
		BOOK_AUTHOR,
		BOOK_LOAN,
		OTHER // used to check if data exists
	}
	
	public static enum Person {
		AUTHOR,
		BORROWER
	}
	
	public AppDbContext() throws SQLException
	{
		// Create the connection to the database
		myConn = DriverManager.getConnection(URL, USER, PASSWORD);
		myStmt = myConn.createStatement();	
		myRslt = null;
		currentTable = null;
		sqlBuilder = new SqlQueryBuilder();
	}	
	
	public void setTable(Table t) {
		this.currentTable = t;
	}
	
	public Table getTable() {
		return this.currentTable;
	}
	
	/*
	 * Method Name: fetch
	 * Purpose: 	Returns a list objects containing their specific objects (Author, Book or Borrower)
	 * 				from myRslt generated from executeQuery()
	 */
	// TODO: need to add the rest of the enum values
	public List<Object> fetch()
	{	
		List<Object> newList = new Vector<Object>();
		executeQuery(); // Creates rsultSet

		try {
			while(myRslt.next()) {
				switch(currentTable) {
				case AUTHOR:
					Author author = new Author(myRslt.getInt(1), myRslt.getString(2), myRslt.getString(3));
					newList.add(author);
					break;
				case BOOK:
					Book book = new Book(myRslt.getInt(1), myRslt.getString(2), myRslt.getString(3), myRslt.getString(4), myRslt.getString(5), myRslt.getBoolean(6));
					newList.add(book);
					break;
				case BORROWER:
					Borrower borrower = new Borrower(myRslt.getInt(1), myRslt.getString(2), myRslt.getString(3), myRslt.getString(4));
					newList.add(borrower);
					break;
				case BOOK_LOAN:	// Only occurs from borrowermodel. This for adding a new loan
					Vector<Object> row = new Vector<Object>();
					row.addElement(myRslt.getString(1) + " " + myRslt.getString(2)); // First and Last name
					row.addElement(myRslt.getString(3)); // Email
					row.addElement(myRslt.getString(4)); // Comment
					row.addElement(myRslt.getDate(5)); // DATE_OUT
					row.addElement(myRslt.getDate(6)); // Date_Returned
					row.addElement(myRslt.getString(7)); // ISBN
					newList.add(row);
					break;
				default:
					break;
				}
			}
		}
		catch(SQLException ex) {
			System.out.println("Exception caught in AppDbContext.fetch, message is " + ex.getMessage());
			ex.printStackTrace();
		}
		return newList;
	}
	
	/*
	 * Method Name: update
	 * Purpose :	This is used to execute updates using connections objects and sqlBuilder object
	 */
	public int update() {
		return executeUpdate();
	}
	
	/*
	 * Method Name: executeQuery
	 * Purpose :	Runs query through DB and populates this.myRslt 
	 */
	public void executeQuery() {
		try {
			this.myRslt = this.myStmt.executeQuery(sqlBuilder.getQuery());
		}
		catch(SQLException ex) {
			System.out.println("Exception caught in AppDbContext.executeQuery, message is " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	/*
	 * Method Name: executeUpdate
	 * Purpose :	Runs query through DB and returns the number of updated rows
	 */
	public int executeUpdate() {
		int updated = 0;
		try {
			updated = this.myStmt.executeUpdate(sqlBuilder.getQuery());
		}
		catch(SQLException ex) {
			System.out.println("Exception caught in AppDbContext.executeUpdate, message is " + ex.getMessage());
			ex.printStackTrace();
		}
		return updated;
	}
	
	/*
	 * Method Name: recordRetrieved
	 * Purpose :	Checks to see if any records exists with the current query
	 */
	public boolean recordRetrieved() {
		executeQuery();
		try {
			if(myRslt.next())
				return true;
		}
		catch (SQLException ex) {
			System.out.println("Exception caught in AppDbContext.recordRetrieved, message is " + ex.getMessage());
			ex.printStackTrace();
		}
		return false;
	}
	
	/*
	 * Method Name: convertToDBString
	 * Purpose :	Converts string with ' ' to be compatible with DB queries
	 */
	public String convertToDBString(String s) {
		return "'" + s + "'";
	}

	/*
	 * Method Name: getPersonId
	 * Purpose :	Fetches id for author or borrower from DB using their names
	 */
	public int getPersonId(String name, Person person)
	{
		//get first and last names
		String arr[] = name.split(" ", 2);
		String fName = arr[0];
		String lName = arr[1];

		sqlBuilder.select()
		.where("LAST_NAME = " + convertToDBString(lName))
		.where("FIRST_NAME = " + convertToDBString(fName));
		int id = -1;
		switch(person)
		{
			case AUTHOR: 		
				sqlBuilder.table("AUTHOR").buildQuery();
				Author a = (Author) fetch().get(0);
				id = a.getId();
				break;
			case BORROWER:	
				sqlBuilder.table("BORROWER").buildQuery();	
				Borrower b = (Borrower) fetch().get(0);
				id = b.getId();
				break;
		}
		return id;
	}
	
	/*
	 * Method Name: closeConnections
	 * Purpose :	Closes connection objects
	 */
	public void closeConnections() {
		try {
			if(myConn != null)
				myConn.close();
			if(myStmt != null)
				myStmt.close();
			if(myRslt != null)
				myRslt.close();
		}
		catch(SQLException ex) {
			System.out.println("Exception caught in AppDbContext.closeConnections, message is " + ex.getMessage());
			ex.printStackTrace();
		}
		System.out.println("Closing DB connections for " + this.currentTable);
	}
	
} //end class
