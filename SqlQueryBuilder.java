/**
 * Program Name: 		SqlBuilder.java
 * Coder: 			Joseph Ryu
 * Date: 			Jul 14, 2017
 * Purpose:			Builds SQL string that can be used by a statement object
 */
import java.util.*;
public class SqlQueryBuilder {
	private String 			sqlQuery;
	private List<String> 		tables; 
	private List<String> 		columns;
	private List<String> 		wheres;
	private List<String> 		orders;
	private List<String> 		values; 	// This will be used for INSERT and UPDATE only
	private QueryType 		queryType; 	// Indicates which CRUD
	private boolean 		distinct;
	
	public static enum QueryType {
		SELECT,
		UPDATE,
		INSERT,
		DELETE
	}	
	
	public SqlQueryBuilder() {
		reinitialize();
	}
	
	/*
	 * Method Name: getQueryType
	 * Purpose: 	returns QueryType
	 */
	public QueryType getQueryType() {
		return queryType;
	}
	
	/*
	 * Method Name: getQuery
	 * Purpose: 	returns sqlQuery
	 */
	public String getQuery() {
		return sqlQuery;
	}
	
	/*
	 * Method Name: setQuery
	 * Purpose: 	sets sqlQuery
	 */
	public void setQuery(String query) {
		this.sqlQuery = query;
	}
	
	/*
	 * Method Name: distinct
	 * Purpose: 	flags distinct bool variable
	 */
	public SqlQueryBuilder distinct() {
		distinct = true;
		return this;
	}
	
	/*
	 * Method Name: select
	 * Purpose: 	Will begin a select SQL statement
	 */
	public SqlQueryBuilder select() {
		reinitialize();
		queryType = QueryType.SELECT;
		return this;
	}
	
	/*
	 * Method Name: update
	 * Purpose: 	Will begin an update SQL statement
	 */
	public SqlQueryBuilder update() {
		reinitialize();
		queryType = QueryType.UPDATE;
		return this;
	}
	
	/*
	 * Method Name: delete
	 * Purpose: 	Will begin a delete SQL statement
	 */
	public SqlQueryBuilder delete() {
		reinitialize();
		queryType = QueryType.DELETE;
		return this;
	}
	
	/*
	 * Method Name: insert
	 * Purpose: 	Will begin an insert SQL statement
	 */
	public SqlQueryBuilder insert() {
		reinitialize();
		queryType = QueryType.INSERT;
		return this;
	}
	
	/*
	 * Method Name: column
	 * Purpose: 	Accumulate column(s)
	 */
	public SqlQueryBuilder column(String column) {
		columns.add(column);
		return this;
	}
	
	/*
	 * Method Name: column
	 * Purpose: 	Accumulate column(s)
	 */
	public SqlQueryBuilder column(List<String> columns) {
		for(String c : columns)
			this.columns.add(c);
		return this;
	}
	
	/*
	 * Method Name: column
	 * Purpose: 	Accumulate column(s)
	 */
	public SqlQueryBuilder column(String[] columns) {
		for(String c : columns)
			this.columns.add(c);
		return this;
	}
	
	/*
	 * Method Name: table
	 * Purpose: 	Accumulate table(s)
	 */
	public SqlQueryBuilder table(String table) {
		tables.add(table);
		return this;
	}
	
	/*
	 * Method Name: where
	 * Purpose: 	Accumulate where(s)
	 */
	public SqlQueryBuilder where(String where) {
		this.wheres.add(where);
		return this;
	}
	
	/*
	 * Method Name: where
	 * Purpose: 	Accumulate where(s)
	 */
	public SqlQueryBuilder where(List<String> wheres) {
		for(String w : wheres)
			this.wheres.add(w);
		return this;
	}
	
	/*
	 * Method Name: order
	 * Purpose: 	Accumulate order(s)
	 */
	public SqlQueryBuilder order(String orderBy) {
		orders.add(orderBy);
		return this;
	}
	
	/*
	 * Method Name: order
	 * Purpose: 	Accumulate order(s)
	 */
	public SqlQueryBuilder order(List<String> orderBys) {
		for(String o : orderBys)
			this.orders.add(o);
		return this;
	}

	/*
	 * Method Name: value
	 * Purpose: 	Accumulate value(s)
	 */
	public SqlQueryBuilder value(String value) {
		values.add(value);
		return this;
	}
	
	/*
	 * Method Name: value
	 * Purpose: 	Accumulate value(s)
	 */
	public SqlQueryBuilder value(List<String> values) {
		for(String value : values)
			this.values.add(value);
		return this;
	}
	
	/*
	 * Method Name: value
	 * Purpose: 	Accumulate value(s)
	 */
	public SqlQueryBuilder value(String[] values) {
		for(String value : values)
			this.values.add(value);
		return this;
	}
	

	/*
	 * Method Name: appendQuery
	 * Purpose: 	Used to build queries using different containers to sqlQuery
	 */	
	private void appendQuery(String pattern, List<String> container) {
		for(int i = 0; i < container.size(); ++i) {
			if(i == container.size() -1)
				sqlQuery += container.get(i);
			else
				sqlQuery += container.get(i) + pattern + " ";	
		}
	}
	
	/*
	 * Method Name: appendWhere
	 * Purpose: 	Used to build append where conditions queries to sqlQuery
	 */	
	private void appendWhere() {
		sqlQuery += " WHERE ";
		appendQuery(" AND", wheres);
	}
	
	/*
	 * Method Name: buildQuery
	 * Purpose: 	This method builds SQL query based on the object's state
	 */	
	public String buildQuery() {
		switch(queryType) {
		case DELETE:
			sqlQuery = "DELETE FROM " + tables.get(0);
			appendWhere();
			break;
		case INSERT:
			sqlQuery = "INSERT INTO " + tables.get(0) + "(";
			appendQuery(",", columns);
			sqlQuery += ") VALUES(";
			appendQuery(",", values);
			sqlQuery += ")";
			if(!wheres.isEmpty())
				appendWhere();
			break;
		case SELECT:
			sqlQuery = "SELECT ";
			if(distinct)
				sqlQuery += "DISTINCT ";
			if(columns.isEmpty())	
				sqlQuery += "*";
			appendQuery(",", columns);
			sqlQuery += " FROM " + tables.get(0) + " ";
			
			if(!wheres.isEmpty())
				appendWhere();
			break;
		case UPDATE:
			sqlQuery += "UPDATE " +tables.get(0) + " SET ";
			appendQuery(" AND", values);
			appendWhere();
			break;		
		}
		System.out.println(sqlQuery);
		return sqlQuery;
	}
	
	/*
	 * Method Name: reinitialize
	 * Purpose: 	Reinitialize all data members within the object
	 */
	public void reinitialize() {
		sqlQuery 	= "";
		tables 		= new ArrayList<String>();
		columns 	= new ArrayList<String>();
		wheres 		= new ArrayList<String>();
		orders 		= new ArrayList<String>();
		values 		= new ArrayList<String>();
		distinct 	= false;
	}
} //end class
