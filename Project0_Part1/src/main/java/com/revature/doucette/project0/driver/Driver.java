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
		logger.debug("Program started");
		logger.trace("In Driver.main");

		// phase 0 load data
		loadData();
		logger.debug("Data loaded");

		console = new Scanner(System.in);
		logger.debug("Scanner Booted");
		boolean running = true;
		while (running) {
			// Phase 1 user Login
			// choose login or register
			boolean authenticating = true;
			while (authenticating) {
				logger.debug("Authenticating");
				System.out.println("Would you like to login or register a new account? (login/register)");
				String res = console.nextLine().toLowerCase();
				switch (res) {
				case "l":
				case "log":
				case "login":
					// login
					authenticating = !login();
					logger.debug("Login attempt complete");
					break;
				case "r":
				case "reg":
				case "register":
					// register
					authenticating = !register();
					logger.debug("Register attempt complete");
					break;
				case "q":
				case "quit":
					logger.info("User Quit");
					authenticating=false;
					running=false;
					break; 
				}
			}
			logger.debug("Authentication complete");
			// phase 2 user actions
			if (currentUser != null) {
				logger.debug("User Authenticated");
				currentUser.useApp();
			}
		}
		logger.debug("Application closing");
		console.close();
		logger.debug("Scanner closed");

		// save data before exiting
		saveData();
		logger.debug("Data Saved");

	}

	static boolean login() {
		logger.trace("in Driver.login");
		boolean loggingOn = true;
		while (loggingOn) {
			logger.debug("Loging on");
			// gather credentials
			String[] credentials = gatherCredentials();
			logger.debug("Credentials gathered");
			// match credentials to user
			boolean userExists = false;
			for (String u : users.keySet()) {
				if (u.equals(credentials[0])) {
					if (users.get(u).getPassword().equals(credentials[1])) {
						System.out.println("Welcome " + u + "!");
						currentUser = users.get(u);
						loggingOn = false;
						logger.info("User: " + u + " Succcessfully logged on.");
					} else {
						System.out.println("The password is incorrect for user " + u + ".");
						logger.info("User: " + u + " Entered incorrect password");
					}
					userExists = true;
					break;
				}
			}
			if (!userExists) {
				logger.debug("Selected user '"+credentials[0]+"' Doesn't exist");
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
		logger.trace("in Driver.register");
		boolean registering = true;
		while (registering) {
			logger.debug("Registering");
			// gather credentials
			String[] credentials = gatherCredentials();
			logger.debug("Credentials gathered");
			
			// check credentials against existing users
			boolean userExists = false;
			for (String u : users.keySet()) {
				if (u.equals(credentials[0])) {
					userExists = true;
					break;
				}
			}
			if (userExists) {
				logger.info("User tried to register with existing account");
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
		logger.trace("In Driver.createUser");
		boolean admin = false;
		System.out.println("Would you like admin privelages? (y/n)");
		String res = console.nextLine().toLowerCase();
		if (res.equals("y") || res.equals("yes")) {
			admin = true;
		}
		currentUser = new User(uname, pword, admin);
		logger.info("New User: "+ currentUser + " created.");
		users.put(currentUser.getUsername(), currentUser);
	}

	static String[] gatherCredentials() {
		logger.trace("In Driver.gatherCredentials");
		System.out.println("Enter Your Username:");
		String r0 = console.nextLine();
		System.out.println("Enter Your Password:");
		String r1 = console.nextLine();
		String[] res = { r0, r1 };
		return res;
	}

	public static int nextAvailableAccountID() {
		logger.trace("In Driver.nextAvailableAccountID");
		if (largestAccountID == 0) { //only search if no current largest
			for (int i : accounts.keySet()) {
				if (i >= largestAccountID) {
					largestAccountID = i;
				}
			}
		}
		return ++largestAccountID;
	}

	public static void viewAllUsers() {
		logger.trace("In Driver.viewAllUsers");
		for (String s : users.keySet()) {
			System.out.println("	" + users.get(s));
		}
		System.out.println("Select a user for detailed view. Press enter to return to action menu.");
		logger.debug("Selecting User");
		String res = console.nextLine().toLowerCase();
		if (users.keySet().contains(res)) {
			users.get(res).view();
		}
	}

	public static Vector<User> getMembersOfAccount(Integer accountId){
		logger.trace("In Driver.getMembersOfAccount");
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
		logger.trace("In Driver.loadData");
		// load accounts
		load(ACCOUNT_FILE_PATH,Account.class,(account)->{
			accounts.put(account.getId(), account);
		});
		logger.info("Loaded Accounts: "+ accounts);
		// load users
		load(USER_FILE_PATH,User.class,(user)->{
			users.put(user.getUsername(), user);
		});
		logger.info("Loaded Users: "+ users);
		// load account requests
		load(AAR_FILE_PATH,AccountApprovalRequest.class,(req)->{
			pendingAccounts.add(req);
		});
		logger.info("Loaded Account Requests: "+ pendingAccounts);
		// load user requests
		load(USER_REQUESTS_FILE_PATH,Request.class,(req)->{
			userRequests.add(req);
		});
		logger.info("Loaded User Requests: "+ userRequests);
	}
	public static <T extends Serializable> void load(String file,Class<T> type,Consumer<T> storer) {
		logger.trace("In Driver.load");
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
			logger.warn("Didn't find " + file );
		} catch (IOException e) {
			logger.warn("IO Exception");
		} catch (ClassNotFoundException e) {
			logger.warn("Class " + type.toString() + " not found in file "+ file);
		}
		
	}

	public static void saveData() {
		logger.trace("In Driver.getMembersOfAccount");
		// save accounts
		save(ACCOUNT_FILE_PATH,accounts.keySet(),(id)->{
			return accounts.get(id);
			});
		logger.info("Saved Accounts: "+ accounts);
		// save users
		save(USER_FILE_PATH,users.keySet(),(id)->{
			return users.get(id);
			});
		logger.info("Saved Users: "+ users);
		// save account requests
		save(AAR_FILE_PATH,pendingAccounts,(i)->{
			return i;
			});
		logger.info("Saved Account Requests: "+ pendingAccounts);
		// save user requests
		save(USER_REQUESTS_FILE_PATH,userRequests,(i)->{
			return i;
		});
		logger.info("Saved User Requests: "+ userRequests);
	}
	public static <T,O extends Serializable> void save(String file, Collection<T> collection, Function<T,O> accessor) {
		logger.trace("In Driver.save");
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
			logger.warn("Didn't find " + file );
		} catch (IOException e) {
			logger.warn("IO Exception");
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
//	Done - Admins should be able to view 
//		Done - all accounts
//		Done - all account requests
//		Done - all account operations (withdraw/deposit/transfer)
//		Done - delete acconuts
//	Done - all data should be persisted in txt files
//	Done - Logging of transactions
//	Done - 100% JUnit Test coverage


// My Tasks
//	 Done - account approval pipeline
//	 Done - friend approval pipeline
//	 Done - account funds management
//   Done - manage specific accounts
//	 Done - flyweight request logic (though not for funds transfer requests since multiple transfers of the same ammount isn't unreasonable)
//	 Done - File i/o
//	 Done - Logging (could probably log more but i believe i meet the requirements)
//	 Done - JUnit tests

//---------------------------------------------------------------------------
// Topics to consider more next time
// 	TDD - currently most of my classes work more like services than beans making them herder to test
//	implementation - Request would likely be better implemented as an abstract class
//		- i think building the data/model layers first would help with better application of TDD
//		- having logging setup at the start would help avoid the hastle of going back through all the code to retroactively add logs)
//	multi-threading - app would probably have multiple user instances acting at the same time
//		- implimenting thread safety for updating user and account data
//		- requests should also have thread safe implimentations in order to prevent them being resolved multiple times

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
