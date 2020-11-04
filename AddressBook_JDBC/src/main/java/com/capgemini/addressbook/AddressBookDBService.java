package com.capgemini.addressbook;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.capgemini.addressbook.DatabaseException.exceptionType;

public class AddressBookDBService {

	private static AddressBookDBService addressBookDBService;
	List<Contact> contactList = new ArrayList<Contact>();
	PreparedStatement preparedStatementByName;

	private AddressBookDBService() {

	}

	public static AddressBookDBService getInstance() {
		if (addressBookDBService == null)
			addressBookDBService = new AddressBookDBService();
		return addressBookDBService;
	}

	public List<Contact> readDataDB() throws DatabaseException {
		String sqlQuery = "SELECT * FROM contact;";
		return exceutesqlQuery(sqlQuery);
	}

	private List<Contact> exceutesqlQuery(String sqlQuery) throws DatabaseException {
		List<Contact> contactList = new ArrayList<>();
		try (Connection connection = DBConnection.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sqlQuery);
			contactList = getResultSet(result);
		} catch (SQLException e) {
			throw new DatabaseException("Unable to execute query!!", exceptionType.EXECUTE_QUERY);
		}
		return contactList;
	}

	private List<Contact> getResultSet(ResultSet result) throws DatabaseException {
		List<Contact> contactList = new ArrayList<>();
		try {
			while (result.next()) {
				contactList.add(new Contact(result.getInt("contact_id"), result.getString("first_name"),
						result.getString("last_name"), result.getString("address"), result.getString("city"),
						result.getString("state"), result.getInt("zip"), result.getString("phone_number"),
						result.getString("email_id")));
			}
		} catch (SQLException e) {
			throw new DatabaseException("Unable to execute query!!", exceptionType.EXECUTE_QUERY);
		}
		return contactList;
	}

	public int updateDataDB(String firstName, String lastName, String phoneNumber) throws DatabaseException {
		String sqlQuery = String.format(
				"UPDATE contact SET phone_number = %s WHERE first_name = '%s' AND last_name = '%s';", phoneNumber,
				firstName, lastName);
		try (Connection connection = DBConnection.getConnection()) {
			Statement statement = connection.createStatement();
			int rowAffected = statement.executeUpdate(sqlQuery);
			return rowAffected;
		} catch (SQLException e) {
			throw new DatabaseException("Unable to execute query!!", exceptionType.EXECUTE_QUERY);
		}
	}

	// To get contact data from database by name
	public List<Contact> getContactByNameFromDB(String firstName, String lastName) throws DatabaseException {
		List<Contact> contactListByName = null;
		if (preparedStatementByName == null)
			preparedStatemenToGetContactDataByName();
		ResultSet result = null;
		try {
			preparedStatementByName.setString(1, firstName);
			preparedStatementByName.setString(2, lastName);
			result = preparedStatementByName.executeQuery();
		} catch (SQLException e) {
			throw new DatabaseException("Unable to execute query!!", exceptionType.EXECUTE_QUERY);
		}
		contactListByName = getResultSet(result);
		return contactListByName;
	}

	private void preparedStatemenToGetContactDataByName() throws DatabaseException {
		String sql = "SELECT * FROM contact WHERE first_name = ? AND last_name = ?;";
		try {
			Connection connection = DBConnection.getConnection();
			preparedStatementByName = connection.prepareStatement(sql);
		} catch (SQLException e) {
			throw new DatabaseException("Unable to execute query!!", exceptionType.EXECUTE_QUERY);
		}
	}

}
