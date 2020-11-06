package com.capgemini.addressbook;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AddressBookServiceThreadsTest {

	private AddressBookService addressBookService;
	private List<Contact> contactListDatabase;

	@Before
	public void init() {
		addressBookService = new AddressBookService();
	}
	
	@Test
	public void givenListOfContactssWhenAddedShouldMatchNoOfEntries() throws DatabaseException {
		List<Contact> contactList = new ArrayList<>();
		contactList.add(new Contact("Chanu", "Varma", "Begumpet", "Hyderabad", "Telangana", 533893, "9789784561",
				"chandu@gmail.com", LocalDate.now(), 101, 51));
		contactList.add(new Contact("Radha", "Gokul", "Thane", "Pune", "Maharashtra", 676945, "8912345674",
				"radha@gmail.com", LocalDate.now(), 101, 50));
		contactList.add(new Contact("Moksha", "Greeshma", "S.R.Nagar", "Chennai", "TamilNadu", 478945, "8796543298",
				"mokshagreeshma@gmail.com", LocalDate.now(), 101, 52));
		Instant start = Instant.now();
		addressBookService.addEmployeeListToEmployeeAndPayrollTable(contactList);
		Instant end = Instant.now();
		Instant startThread = Instant.now();
		addressBookService.addEmployeeListToEmployeeAndPayrollWithThreads(contactList);
		Instant endThread = Instant.now();
		contactListDatabase = addressBookService.readData();
		System.out.println("Duration without Threads : " + Duration.between(start, end));
		System.out.println("Duration with Threads : " + Duration.between(startThread, endThread));

		Assert.assertEquals(6, contactListDatabase.size());
	}

}
