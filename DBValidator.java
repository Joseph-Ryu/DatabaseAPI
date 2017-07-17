/**
 * Program Name: 			DBValidator.java
 * Coder: 				Joseph Ryu
 * Date: 				Jul 12, 2017
 * Purpose:				Used to validate whether an insertion can occur with database with current data.
 * 					This class can be used in either frontend to validate input or backend as well.
 */
import javax.swing.JOptionPane;
import java.util.*;

public class DBValidator
{	
	private List<BaseDataColumn> tableColumns; // Represents the table that data will be validated against
	
	public static enum Type {
		STRING,
		INTEGER
	}
	
	public DBValidator() {
		tableColumns = new Vector<BaseDataColumn>();
	}
	
	/*
	 * Method Name: getColumns
	 * Purpose:	Retrieves tableColumns
	 */
	public List<BaseDataColumn> getColumns() {
		return tableColumns;
	}
	
	/*
	 * Method Name: addColumn
	 * Purpose:	Adds a column to the tableColumns vector
	 */
	public DBValidator addColumn(BaseDataColumn column) {
		tableColumns.add(column);
		return this;
	}
	
	/*
	 * Method Name: addColumn
	 * Purpose:	Adds multiple columns to the tableColumns vector
	 */
	public DBValidator addColumn(BaseDataColumn[] columns) {
		for(BaseDataColumn c : columns) 
			tableColumns.add(c);
		return this;
	}
	
	/*
	 * Method Name: validateData
	 * Purpose:	Validates data coming in against the tableColumns
	 */
	public boolean validateData(Vector<String> data) {	// This data should be in the correct order
		if(tableColumns.isEmpty())
			return errorMsg("You need to set the column(s)");
		
		if(tableColumns.size() != data.size())
			return errorMsg("Data passed in does not match the columns in our table");
		
		if(DBValidator.isDataEmpty(data))
			return errorMsg("One or more elements have empty string as data");
		
		// Check each data
		List<Boolean> validList = new Vector<Boolean>();
		for(int i = 0; i<tableColumns.size(); ++i) {
			validList.add(tableColumns.get(i).validate(data.get(i))); // checks each data
		}
		
		// If any false values found in list, return false
		for(boolean b : validList) 
			if(!b)
				return errorMsg("Data was not valid!");

		return true;
	}
	
	/*
	 * Method Name: errorMsg
	 * Purpose:	Shows error message and return false
	 */	
	private static boolean errorMsg(String msg) {
		JOptionPane.showMessageDialog(null, msg);
		return false;
	}
	
	/*
	 * Method Name: checkSingleQuotation
	 * Purpose:	Checks whether there is single quotation in the data. Returns true if there is one
	 */	
	public static boolean checkSingleQuotation(String data) {
		for(int i = 0; i < data.length(); ++i)
			if(data.charAt(i) == 39) { // single quotation is 39 ascii value
				JOptionPane.showMessageDialog(null, "No single quotations allowd here!!!");
				return true;
			}
		return false;
	}

	/*
	 * Method Name: isDataEmpty
	 * Purpose:	Checks whether data is empty or not. Returns true if there is null or empty strings
	 */
	public static boolean isDataEmpty(Vector<String> data)	{
		for(String d : data)
			if( d == null || d.isEmpty()) 
				return true;
		return false;
	}
	
	/*
	 * Method Name: isValidEmailAddress
	 * Purpose:	Checks whether data is in correct email format
	 */
	public static boolean isValidEmailAddress(String email) {
		String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
		java.util.regex.Matcher m = p.matcher(email);
		return m.matches();
	}
	
	/*
	 * Method Name: isNumeric
	 * Purpose:	Checks whether data only contains numbers
	 */
	public static boolean isNumeric(String str) {  
	  try {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe) {  
	    return false;  
	  }  
	  return true;  
	}
	
	/*
	 * Class Name: 	BaseDataColumn
	 * Purpose:	Base class for representing the tableColumns
	 */
	public static abstract class BaseDataColumn {
		private boolean exact; // this indicates whether the data has to be exactly the limit
		private Type 	type;
		private String 	colName;
		private int 	lengthLimit;
		
		public BaseDataColumn(Type type, String colName, int lengthLimit) {
			this.exact	 = false;
			this.type	 = type;
			this.colName 	 = colName;
			this.lengthLimit = lengthLimit;
		}
		
		public int getLengthLimit() {
			return lengthLimit;
		}
		
		public String getColName() {
			return this.colName;
		}
		
		public boolean validate(String data) {
			if(exact) {
				return data.length() == this.lengthLimit;
			}
			return data.length() <= this.lengthLimit;
		}
		
		public Type getType() {
			return type;
		}
		
		public void setExact(boolean b) {
			exact = b;
		}
		
		public boolean isExact() {
			return exact;
		}
	}
	
	/*
	 * Class Name: 	StringColumn
	 * Purpose:	Represents a string column with restrictions
	 */
	public static class StringColumn extends BaseDataColumn {
		private boolean isEmail;
		
		public StringColumn(String colName, int limitLength) {
			super(Type.STRING, colName, limitLength);
			isEmail = false;
		}
		
		public StringColumn(StringColumn sc) {
			super(Type.STRING, sc.getColName(), sc.getLengthLimit());
			isEmail = sc.isEmail;
		}
		
		@Override
		public boolean validate(String data) {
			if(isEmail) {
				if(!isValidEmailAddress(data))
					return errorMsg("Bad email format");
			}
			return super.validate(data) && !checkSingleQuotation(data);
		}
		public void setEmail(boolean b) {
			isEmail = b;
		}
	}
	
	/*
	 * Class Name: 	IntegerColumn
	 * Purpose:	Represents an integer column with restrictions
	 */
	public static class IntegerColumn extends BaseDataColumn {
		public IntegerColumn(String colName,int limitLength) {
			super(Type.INTEGER, colName,limitLength);
		}
		
		@Override
		public boolean validate(String data) {
			return super.validate(data) && isNumeric(data);
		}
	}		
} 

