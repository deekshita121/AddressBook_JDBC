package com.capgemini.addressbook;

import java.util.List;

public class AddressBookService {

private AddressBookDBService addressBookDBService;
	
	public AddressBookService() {
		addressBookDBService = AddressBookDBService.getInstance();
	}

	public static void main(String[] args) {

		// Welcome Message
		System.out.println("Welcome to Address Book Program ");
	}

	public List<Contact> readData() throws DatabaseException {
		return addressBookDBService.readDataDB();
	}
}
