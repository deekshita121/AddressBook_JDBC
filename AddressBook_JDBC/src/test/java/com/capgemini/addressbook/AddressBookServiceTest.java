package com.capgemini.addressbook;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AddressBookServiceTest {

	private AddressBookService addressBookService;
	private List<Contact> contactList;

	@Before
	public void init() {
		addressBookService = new AddressBookService();
	}

	@Test
	public void givenAddressBookDatabaseWhenRetrievedShouldContactsCount() {
		try {
			contactList = addressBookService.readData();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		Assert.assertEquals(5, contactList.size());
	}
}
