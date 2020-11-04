package com.capgemini.addressbook;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		return executeSqlQuery(sqlQuery);
	}

	private List<Contact> executeSqlQuery(String sqlQuery) throws DatabaseException {
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

	public List<Contact> getContactsByDate(LocalDate startDate, LocalDate endDate) throws DatabaseException {
		String sqlQuery = String.format("SELECT * FROM contact WHERE startDate BETWEEN '%s' AND '%s';",
				Date.valueOf(startDate), Date.valueOf(endDate));
		return executeSqlQuery(sqlQuery);
	}

	public Map<String, Integer> getContactsCountByStateDB() throws DatabaseException {
		String sqlQuery = String.format("SELECT state, COUNT(first_name) AS count FROM contact GROUP BY state;");
		Map<String, Integer> stateToCount = new HashMap<>();
		try (Connection connection = DBConnection.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sqlQuery);
			while (result.next()) {
				String state = result.getString("state");
				Integer count = result.getInt("count");
				stateToCount.put(state, count);
			}
		} catch (SQLException e) {
			throw new DatabaseException("Unable to execute query!!", exceptionType.EXECUTE_QUERY);
		}
		return stateToCount;
	}

	public Map<String, Integer> getContactsCountByCityDB() throws DatabaseException {
		String sqlQuery = String.format("SELECT city, COUNT(first_name) AS count FROM contact GROUP BY city;");
		Map<String, Integer> cityToCount = new HashMap<>();
		try (Connection connection = DBConnection.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sqlQuery);
			while (result.next()) {
				String city = result.getString("city");
				Integer count = result.getInt("count");
				cityToCount.put(city, count);
			}
		} catch (SQLException e) {
			throw new DatabaseException("Unable to execute query!!", exceptionType.EXECUTE_QUERY);
		}
		return cityToCount;
	}

	public Contact addNewContactDB(Contact contact) throws DatabaseException {

		int contactId = -1;
		Connection connection = null;
		Contact contactData = null;
		try {
			connection = DBConnection.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try (Statement statement = connection.createStatement()) {
			String sql = String.format(
					"INSERT INTO contact (first_name,last_name,address,city,state,zip,phone_number,email_id,startDate) VALUES ('%s','%s','%s','%s','%s',%d,%s,'%s','%s');",
					contact.getFirstName(), contact.getLastName(), contact.getAddress(), contact.getCity(),
					contact.getState(), contact.getZip(), contact.getPhoneNumber(), contact.getEmail(),
					contact.getStartDate());
			int rowAffected = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					contactId = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

		try (Statement statement = connection.createStatement()) {
			String sql = String.format("INSERT INTO dictionary(contact_id,book_id,type_id)VALUES (%d,%d,%d);",
					contactId, contact.getAddressBookId(), contact.getAddressBookTypeId());
			int rowAffected = statement.executeUpdate(sql);
			if (rowAffected == 1) {
				contactData = new Contact(contactId, contact.getFirstName(), contact.getLastName(),
						contact.getAddress(), contact.getCity(), contact.getState(), contact.getZip(),
						contact.getPhoneNumber(), contact.getEmail());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return contactData;
	}
}
