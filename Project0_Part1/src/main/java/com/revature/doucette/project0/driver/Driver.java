package com.revature.doucette.project0.driver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revature.doucette.project0.data.Account;
import com.revature.doucette.project0.data.User;
import com.revature.doucette.project0.requests.AccountApprovalRequest;
import com.revature.doucette.project0.requests.Request;

public class Driver {
	// Constants
	public static final String USER_FILE_PATH = "data/users.txt";
	public static final String ACCOUNT_FILE_PATH = "data/accounts.txt";
	public static final String AAR_FILE_PATH = "data/accountApprovalRequests.txt";
	public static final String USER_REQUESTS_FILE_PATH = "data/userRequests.txt";
	// Services
	public static Scanner console;
	public static Logger logger = LogManager.getLogger(Driver.class);
	// Data
	public static HashMap<String, User> users = new HashMap<String, User>();
	public static HashMap<Integer, Account> accounts = new HashMap<Integer, Account>();
	public static Vector<AccountApprovalRequest> pendingAccounts = new Vector<AccountApprovalRequest>();
	public static Vector<Request> userRequests = new Vector<Request>();
	private static int largestAccountID = 0;
	public static User currentUser = null;

	public static void main(String[] args) {

		// phase 0 load data
		loadData();

		console = new Scanner(System.in);
		boolean running = true;
		while (running) {
			// Phase 1 user Login
			// choose login or register
			boolean authenticating = true;
			while (authenticating) {
				System.out.println("Would you like to login or register a new account? (login/register)");
				String res = console.nextLine().toLowerCase();
				switch (res) {
				case "l":
				case "log":
				case "login":
					// login
					authenticating = !login();
					break;
				case "r":
				case "reg":
				case "register":
					// register
					authenticating = !register();
					break;
				case "q":
				case "quit":
					return; // quit app. No need to save unmodified data
				}
			}
			// phase 2 user actions
			if (currentUser != null) {
				currentUser.useApp();
			} else {
				System.out.println("Authenticated without current user!! (BIG PROBLEM)");
				return;
			}
		}
		console.close();

		// save data before exiting
		saveData();

	}

	static boolean login() {
		boolean loggingOn = true;
		while (loggingOn) {
			// gather credentials
			String[] credentials = gatherCredentials();

			// match credentials to user
			boolean userExists = false;
			for (String u : users.keySet()) {
				if (u.equals(credentials[0])) {
					if (users.get(u).getPassword().equals(credentials[1])) {
						System.out.println("Welcome " + u + "!");
						currentUser = users.get(u);
						loggingOn = false;
					} else {
						System.out.println("The password is incorrect for user " + u + ".");
					}
					userExists = true;
					break;
				}
			}
			if (!userExists) {
				System.out.println("There is no user with username: " + credentials[0]);
				System.out.println(
						"Would you like to use the current credentials to register a new account instead? (y/n)");
				String res = console.nextLine().toLowerCase();
				if (res.equals("y") || res.equals("yes")) {
					createUser(credentials[0], credentials[1]);
					break;
				}
			}
			if (currentUser == null) {
				System.out.println("Would you like to make another login attempt? (y/n)");
				String res = console.nextLine().toLowerCase();
				if (res.equals("y") || res.equals("yes")) {
					continue;
				}
				return false;
			}
			/* * System.out.println(credentials[0] + "\n" + credentials[1]);/* */
		}
		return true;
	}

	static boolean register() {
		boolean registering = true;
		while (registering) {
			// gather credentials
			String[] credentials = gatherCredentials();

			// check credentials against existing users
			boolean userExists = false;
			for (String u : users.keySet()) {
				if (u.equals(credentials[0])) {
					userExists = true;
					break;
				}
			}
			if (userExists) {
				System.out.println("User " + credentials[0] + " Already Exists!");
				System.out.println("Would you like to try again? (y/n)");
				String res = console.nextLine().toLowerCase();
				if (res.equals("y") || res.equals("yes")) {
					continue;
				}
				return false;
			} else {
				// create user
				createUser(credentials[0], credentials[1]);
				registering = false;
			}
			/* * System.out.println(credentials[0] + "\n" + credentials[1]);/* */
		}
		return true;
	}

	static void createUser(String uname, String pword) {
		boolean admin = false;
		System.out.println("Would you like admin privelages? (y/n)");
		String res = console.nextLine().toLowerCase();
		if (res.equals("y") || res.equals("yes")) {
			admin = true;
		}
		currentUser = new User(uname, pword, admin);
		users.put(currentUser.getUsername(), currentUser);
	}

	static String[] gatherCredentials() {
		System.out.println("Enter Your Username:");
		String r0 = console.nextLine();
		System.out.println("Enter Your Password:");
		String r1 = console.nextLine();
		String[] res = { r0, r1 };
		return res;
	}

	public static int nextAvailableAccountID() {
		if (largestAccountID == 0) {
			for (int i : accounts.keySet()) {
				if (i >= largestAccountID) {
					largestAccountID = i;
				}
			}
		}
		return ++largestAccountID;
	}

	public static void viewAllUsers() {
		for (String s : users.keySet()) {
			System.out.println("	" + users.get(s));
		}
		System.out.println("Select a user for detailed view. Press enter to return to action menu.");
		String res = console.nextLine().toLowerCase();
		if (users.keySet().contains(res)) {
			users.get(res).view();
		}
	}

	public static Vector<User> getMembersOfAccount(Integer accountId){
		Vector<User> out = new Vector<User>();
		for(String name: users.keySet()) {
			User u = users.get(name);
			if(u.getMyAccountIds().contains(accountId)) {
				out.add(u);
			}
		}
		return out;
	}
	public static void loadData() {
		// load accounts
		load(ACCOUNT_FILE_PATH,Account.class,(account)->{
			accounts.put(account.getId(), account);
		});
		// load users
		load(USER_FILE_PATH,User.class,(user)->{
			users.put(user.getUsername(), user);
		});
		// load account requests
		load(AAR_FILE_PATH,AccountApprovalRequest.class,(req)->{
			pendingAccounts.add(req);
		});
		// load user requests
		load(USER_REQUESTS_FILE_PATH,Request.class,(req)->{
			userRequests.add(req);
		});
		/**/System.out.println("Data Loaded");/**/
	}
	public static <T extends Serializable> void load(String file,Class<T> type,Consumer<T> storer) {
		FileInputStream fis;
		ObjectInputStream ois;
		try {
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			T o = (T) ois.readObject(); 
			while(o!=null) {
				storer.accept(o);
				o = (T) ois.readObject(); 
			}
			ois.close();
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
				//e.printStackTrace();
			
			/**/System.out.println("Didn't find " + file );/**/
		} catch (IOException e) {
			// TODO Auto-generated catch block
				//e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			
			/**/System.out.println("Class " + type.toString() + " not found");/**/
		}
		
	}

	public static void saveData() {
		// save accounts
		save(ACCOUNT_FILE_PATH,accounts.keySet(),(id)->{
			return accounts.get(id);
			});
		// save users
		save(USER_FILE_PATH,users.keySet(),(id)->{
			return users.get(id);
			});
		// save account requests
		save(AAR_FILE_PATH,pendingAccounts,(i)->{
			return i;
			});
		// save user requests
		save(USER_REQUESTS_FILE_PATH,userRequests,(i)->{
			return i;
		});
		/**/System.out.println("Data Saved");/**/
	}
	public static <T,O extends Serializable> void save(String file, Collection<T> collection, Function<T,O> accessor) {
		FileOutputStream fos;
		ObjectOutputStream oos;
		try {
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			for (T id : collection) {
				oos.writeObject(accessor.apply(id));
			}
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
				//e.printStackTrace();
			
			/**/System.out.println("Didn't find " + file );/**/
		} catch (IOException e) {
			// TODO Auto-generated catch block
				//e.printStackTrace();
			
			/**//**/
		}
	}
}

//----------------------------------------------------------------------------
// TODO list
// Requirements
//	Done - Maven Project with Java 1.8
//	Done - Interaction with console through Scanner
//	Done - Custoers should be able to register with a username and password
//  Done - Customers should be able to apply for accounts
//	Done - Accounts can have multiple users
//	Done - Users can...With their accounts
//		Done - Withdraw
//		Done - Deposit
//		Done - Transfer funds
//	Done - Basic validation of inputs
//		Done - no negative values
//		Done - no withdrawing more money than is in the account
//	Done - Admins should be able to view 
//		Done - all customers
//		Done - their accounts and ascociated balances
//		Done - customer personal information
//	Done - Admins should be able to manage applications for accounts
//	  - Admins should be able to view 
//		Done - all accounts
//		Done - all account requests
//		Done - all account operations (withdraw/deposit/transfer)
//		  - delete acconuts
//	Done - all data should be persisted in txt files
//	  - 100% JUnit Test coverage
//	  - Logging of transactions
// Tasks
//	 Done - account approval pipeline
//	 Done - friend approval pipeline
//	  - account funds management
//    - manage specific accounts
//	  - flyweight request logic
//	Done - File i/o
//	  - Logging
//	  - JUnit tests

//---------------------------------------------------------------------------
// Topics to consider more next time
// 	TDD - currently most of my classes work more like services than beans making them herder to test
//	


//-----------------------------------------------------------------------------
// data
//	 user data
//		 username
//		 password
//		 ?? other info
//		 privileges
//		 associated accounts
//	 account data
//		 approved
//		 balance
//	 requests
//		 friend
//			 user1/sender
//			 user2/recipiant
//			 status
//		 account approval
//			 ?? user/sender
//			 account id
//			 status
//		 join account
//			 user/recipiant
//			 account id
//			 ?? sender
//			 status

// things to test
// 	 users
//		create
//		view
// 	 accounts
//		create
//		view
//		transfer funds
// 	 requests
//		create
//		view
//		approve/deny

// things to study
//	 File class
//	 input/output stream classes
//		 for files
//		 for objects
//	 Scanner class
//	 Junit stuff
