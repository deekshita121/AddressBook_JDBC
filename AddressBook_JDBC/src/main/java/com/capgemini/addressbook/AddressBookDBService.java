package com.capgemini.addressbook;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.capgemini.addressbook.DatabaseException.exceptionType;

public class AddressBookDBService {

	private static AddressBookDBService addressBookDBService;
	List<Contact> contactList = new ArrayList<Contact>();

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
						result.getString("state"), result.getInt("zip"), result.getLong("phone_number"),
						result.getString("email_id")));
			}
		} catch (SQLException e) {
			throw new DatabaseException("Unable to execute query!!", exceptionType.EXECUTE_QUERY);
		}
		return contactList;
	}
}
