package com.capgemini.addressbook;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddressBookService {

	private AddressBookDBService addressBookDBService;
	private List<Contact> contactList = new ArrayList<Contact>();

	public AddressBookService() {
		addressBookDBService = AddressBookDBService.getInstance();
	}

	public static void main(String[] args) {

		// Welcome Message
		System.out.println("Welcome to Address Book Program ");
	}

	public List<Contact> readData() throws DatabaseException {
		contactList = addressBookDBService.readDataDB();
		return contactList;
	}

	public void updateData(String firstName, String lastName, String phoneNumber) throws DatabaseException {
		contactList = addressBookDBService.readDataDB();
		int rowAffected = addressBookDBService.updateDataDB(firstName, lastName, phoneNumber);
		if (rowAffected != 0)
			(getContactByName(contactList, firstName, lastName)).setPhoneNumber(phoneNumber);
	}

	private Contact getContactByName(List<Contact> contactList, String firstName, String lastName)
			throws DatabaseException {
		Contact contact = contactList.stream().filter(contactObj -> (((contactObj.getFirstName()).equals(firstName))
				&& ((contactObj.getLastName()).equals(lastName)))).findAny().orElse(null);
		return contact;
	}

	public boolean checkContactsInsyncWithDatabase(String firstName, String lastName) throws DatabaseException {
		boolean result = false;
		contactList = addressBookDBService.readDataDB();
		Contact contactFromDb = addressBookDBService.getContactByNameFromDB(firstName, lastName).get(0);
		System.out.println(contactFromDb);
		result = getContactByName(contactList, firstName, lastName).equals(contactFromDb);
		System.out.println(getContactByName(contactList, firstName, lastName));
		return result;
	}
	
	public List<Contact> getContactsByDate(LocalDate startDate, LocalDate endDate) throws DatabaseException {
		System.out.println(startDate +" "+ endDate);
		return addressBookDBService.getContactsByDate(startDate, endDate);
	}
	
	public Map<String,Integer> getContactsCountByState() throws DatabaseException {
		return addressBookDBService.getContactsCountByStateDB();
	}

	public Map<String, Integer> getContactsCountByCity() throws DatabaseException {
		return addressBookDBService.getContactsCountByCityDB();
	}
}
