package com.capgemini.addressbook;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
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
	
	public void addNewContact(Contact contact) throws DatabaseException {
		Contact contactData = null;
		contactData = addressBookDBService.addNewContactDB(contact);
		System.out.println(contactData);
		if (contactData.getContactId() != -1)
			contactList.add(contactData);
	}

	public void addEmployeeListToEmployeeAndPayrollTable(List<Contact> contactList) throws DatabaseException {
		for(Contact contact : contactList)
			addNewContact(contact);
		
	}

	public void addEmployeeListToEmployeeAndPayrollWithThreads(List<Contact> contactList) {
		Map<Integer, Boolean> contactAditionStatus = new HashMap<>();
		contactList.forEach(contact -> {
			Runnable task = () -> {
				contactAditionStatus.put(contact.hashCode(), false);
				System.out.println("Contact being added : " + contact.getFirstName());
				try {
					addNewContact(contact);
				} catch (DatabaseException e) {
					e.printStackTrace();
				}
				contactAditionStatus.put(contact.hashCode(), true);
				System.out.println("Contact added : " + contact.getFirstName());
			};
			Thread thread = new Thread(task, contact.getFirstName());
			thread.start();
		});

		while (contactAditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
