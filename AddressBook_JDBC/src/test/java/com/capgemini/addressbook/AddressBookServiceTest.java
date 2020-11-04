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
	public void givenAddressBookDatabaseWhenRetrievedShouldContactsCount() throws DatabaseException {
		contactList = addressBookService.readData();
		Assert.assertEquals(5, contactList.size());
	}

	@Test
	public void givenUpdatedPhoneNumberWhenUpdatedShouldSyncWithDatabase() throws DatabaseException {
		boolean result = false;
		addressBookService.updateData("Divya", "Prakash", "9876543210");
		result = addressBookService.checkContactsInsyncWithDatabase("Divya", "Prakash");
		Assert.assertTrue(result);
	}
}
