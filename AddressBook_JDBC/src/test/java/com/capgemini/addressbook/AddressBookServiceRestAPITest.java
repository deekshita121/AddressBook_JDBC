package com.capgemini.addressbook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class AddressBookServiceRestAPITest {

	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}

	private Contact[] getContactList() {
		Response response = RestAssured.get("/contacts");
		System.out.println("Contact entries in JSON Server :\n" + response.asString());
		Contact[] arrayOfContacts = new Gson().fromJson(response.asString(), Contact[].class);
		return arrayOfContacts;
	}

	@Test
	public void UC1givenAddressBookDataWhenRetrievedShouldMatchNoofEntries() {
		AddressBookService addressBookService;
		Contact[] arrayOfContacts = getContactList();
		addressBookService = new AddressBookService(Arrays.asList(arrayOfContacts));
		long entries = addressBookService.countEntries();
		Assert.assertEquals(3, entries);
	}
	
	private Response addContactToJsonServer(Contact contact) {
		String contactJson = new Gson().toJson(contact);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(contactJson);
		return request.post("/contacts");
	}
	
	@Test
	public void UC2givenContactListWhenAddedShouldMatchResponseCode() {
		AddressBookService addressBookService;
		Contact[] arrayOfContacts = getContactList();
		addressBookService = new AddressBookService(Arrays.asList(arrayOfContacts));
		List<Contact> contactList = new ArrayList<>();
		contactList.add(new Contact(0, "Terisa", "Mark", "Kukatpally", "Hyderabad", "Telangana", 567894, "8976111277",
				"joey@gmail.com"));
		contactList.add(new Contact(0, "Phoebe", "Buffay", "K.T.Colony", "Bangalore", "Karnataka", 897654, "9955553277",
				"phoebe@gmail.com"));
		contactList.add(new Contact(0, "Monica", "Geller", "Thane", "Mumbai", "Maharashtra", 345123, "8985312356",
				"monica@gmail.com"));
		for (Contact contact : contactList) {
			Response response = addContactToJsonServer(contact);
			int statusCode = response.getStatusCode();
			Assert.assertEquals(201, statusCode);
			contact = new Gson().fromJson(response.asString(), Contact.class);
			addressBookService.addContact(contact);
		}
		long entries = addressBookService.countEntries();
		Assert.assertEquals(6, entries);
	}
	
}
