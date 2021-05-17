package com.revature.doucette.project0.data;

import java.io.Serializable;
import java.util.Vector;

import com.revature.doucette.project0.driver.Driver;
import com.revature.doucette.project0.requests.AccountApprovalRequest;
import com.revature.doucette.project0.requests.AccountJoinRequest;
import com.revature.doucette.project0.requests.FriendRequest;
import com.revature.doucette.project0.requests.Request;

public class User implements Serializable {
	private String username; // userid
	private String password;
	private boolean isAdmin;
	private Vector<Integer> myAccountIds = new Vector<Integer>();
	private Vector<String> myFriends = new Vector<String>();

	public User(String name, String password, boolean isAdmin) {
		Driver.logger.trace("In User(name,password,isAdmin)");
		this.username = name;
		this.password = password;
		this.isAdmin = isAdmin;
	}

	public void useApp() {
		Driver.logger.trace("In User.useApp");
		boolean using = true;
		while (using) {
			Driver.logger.debug("viewing user action menu");
			// phase 2 user actions
			System.out.println("Please select an action to perform:");
			System.out.println("	accounts - Manage accounts.");
			System.out.println("	friends - Manage friends");
			System.out.println("	requests - Manage requests");
			if (isAdmin) {
				System.out.println("	users - Veiw all users");
				// admin approval will be done in manage
			}
			System.out.println("	back - Return to login");
			System.out.println("	quit - Quit the application");
			String res = Driver.console.nextLine().toLowerCase();
			switch (res) {
			case "a":
			case "accounts":
				if (isAdmin) {
					System.out.println("Would you like to manage all accounts or only personal accounts? (a/p)");
					res = Driver.console.nextLine().toLowerCase();
					switch (res) {
					case "a":
					case "all":
						manageAllAccounts();
						break;
					default:
						manageMyAccounts();
						break;
					}
				} else {
					manageMyAccounts();
				}
				break;
			case "f":
			case "friends":
				manageFriends();
				break;
			case "r":
			case "requests":
				manageRequests();
				break;
			case "u":
			case "users":
				// this should only do something for admins
				if (isAdmin) {
					Driver.viewAllUsers();
				}
				break;
			case "b":
			case "back":
				using = false;
				break;
			case "q":
			case "quit":

				Driver.saveData();
				System.exit(0);
			default:
				System.out.println("Command not recognised.");
				break;
			}
			// user
			// create account
			// account CRUD/manage
			// ?? view other users (for joint accounts)
			// close application
			// admin/employee
			// user functionality
			// view all users
			// approve pending accounts
			// phase 3 action options
			// return to phase 2
		}
		/** System.out.println("User Authenticated"); / **/
	}

	private void manageAllAccounts() {
		Driver.logger.trace("In User.manageAllAccounts");
		boolean managing = true;
		while (managing) {
			System.out.println("Please Select an account to manage:");
			for (int id : Driver.accounts.keySet()) {
				System.out.println("	" + Driver.accounts.get(id));
			}
			System.out.println("	create - Create a new account");
			System.out.println("	back - Return to user menu");
			System.out.println("	quit - Quit the application");
			String res = Driver.console.nextLine().toLowerCase();
			switch (res) {
			case "c":
			case "create":
				System.out.println("Would you like to invite any friends to join your new account? (y/n)");
				res = Driver.console.nextLine().toLowerCase();
				if (res.equals("y") || res.equals("yes")) {
					createJointAccount();
				} else {
					createAccount();
				}
				break;
			case "b":
			case "back":
				managing = false;
				return;
			case "q":
			case "quit":
				Driver.saveData();
				System.exit(0);
			default:
				try {
					int id = Integer.parseInt(res);
					Driver.accounts.get(id).manageAsAdmin();
				} catch (NumberFormatException e) {
					System.out.println("Command not recognised.");
				}
				break;
			}
		}
	}

	private void manageMyAccounts() {
		Driver.logger.trace("In User.manageMyAccounts");
		boolean managing = true;
		while (managing) {
			System.out.println("Please Select an account to manage:");
			for (int id : myAccountIds) {
				System.out.println("	" + Driver.accounts.get(id));
			}
			System.out.println("	create - Create a new account");
			System.out.println("	back - Return to user menu");
			System.out.println("	quit - Quit the application");
			String res = Driver.console.nextLine().toLowerCase();
			switch (res) {
			case "c":
			case "create":
				System.out.println("Would you like to invite any friends to join your new account? (y/n)");
				res = Driver.console.nextLine().toLowerCase();
				if (res.equals("y") || res.equals("yes")) {
					createJointAccount();
				} else {
					createAccount();
				}
				break;
			case "b":
			case "back":
				managing = false;
				return;
			case "q":
			case "quit":
				Driver.saveData();
				System.exit(0);
			default:
				try {
					int id = Integer.parseInt(res);
					Driver.accounts.get(id).manage();
				} catch (NumberFormatException e) {
					System.out.println("Command not recognised.");
				}
				break;
			}
		}
	}

	private Account createAccount() {
		Driver.logger.trace("In User.createAccount");
		Account a = new Account(Driver.nextAvailableAccountID());
		Driver.accounts.put(a.getId(), a);
		// TODO: should probably let admins avoid submitting an approval request but
		// that would make this harder to demo
		AccountApprovalRequest aar = new AccountApprovalRequest(a.getId(), username);
		Driver.pendingAccounts.add(aar);
		myAccountIds.add(a.getId());
		return a;
	}

	private void createJointAccount() {
		Driver.logger.trace("In User.createJointAccount");
		Account newAccount = createAccount();
		if (myFriends.size() > 0) {
			Vector<String> addableFriends = new Vector<String>(myFriends);
			boolean addingFriends = true;
			while (addingFriends) {
				if (addableFriends.size() > 0) {
					for (String s : addableFriends) {
						User friend = Driver.users.get(s);
						if (friend.myFriends.contains(username)) {
							System.out.println("	" + friend);
						}
					}
					System.out.println("Please select a friend to add!");
					String res = Driver.console.nextLine().toLowerCase();
					String selectedFriend = null;
					for (String username : addableFriends) {
						if (res.equals(username)) {
							selectedFriend = username;
							break;
						}
					}
					if (selectedFriend != null) {
						AccountJoinRequest ajr = new AccountJoinRequest(newAccount.getId(), username, selectedFriend);
						Driver.userRequests.add(ajr);
					} else {
						System.out.println("Friend not recognised. No friend added to acconut");
					}
					System.out.println("Would you like to add another friend? (y/n)");
					res = Driver.console.nextLine().toLowerCase();
					if (res.equals("n") || res.equals("no")) {
						addingFriends = false;
					}
				} else {
					System.out.println("You have no more friends you can add");
					addingFriends = false;
				}
			}
		} else {
			System.out.println("Sorry but you have no friends to add to your new account.");
		}

	}

	private void manageFriends() {
		Driver.logger.trace("In User.manageFriends");
		boolean managing = true;
		while (managing) {
			System.out.println("Your Friends:");
			for (String s : myFriends) {
				System.out.println("	" + Driver.users.get(s));
			}
			System.out.println("	add - Send a friend request");
			System.out.println("	back - Return to user menu");
			System.out.println("	quit - Quit the application");
			String res = Driver.console.nextLine().toLowerCase();
			switch (res) {
			case "a":
			case "add":
				boolean adding = true;
				while (adding) {
					System.out.println("Please enter your friend's username: ");
					res = Driver.console.nextLine().toLowerCase();
					if (Driver.users.keySet().contains(res)) {
						if (myFriends.contains(res)) {
							System.out.println("User " + res + " is already your friend.");
						} else {
							createNewFriendRequest(res);
						}
						adding = false;
					} else {
						System.out.println("There is no user with username: " + res);
						System.out.println("Would you like to try again? (y/n)");
						if (!(res.equals("y") || res.equals("yes"))) {
							adding = false;
						}
					}
				}
				break;
			case "b":
			case "back":
				managing = false;
				break;
			case "q":
			case "quit":
				Driver.saveData();
				System.exit(0);
			default:
				if (myFriends.contains(res)) {
					User friend = Driver.users.get(res);
					System.out.println("Would you like to remove " + res + " as a friend? (y/n)");
					res = Driver.console.nextLine().toLowerCase();
					if (res.equals("y") || res.equals("yes")) {
						removeFriend(friend);
					}
				} else {
					System.out.println("Command not recognised.");
				}
				break;
			}
		}
	}

	private void removeFriend(User friend) {
		Driver.logger.trace("In User.removeFriend");
		myFriends.remove(friend.getUsername());
		friend.getMyFriends().remove(username);
	}

	private void createNewFriendRequest(String friendName) {
		Driver.logger.trace("In User.createNewFriendRequest");
		FriendRequest f = new FriendRequest(username, friendName);
		boolean frAlreadyExists = false;
		for (Request r : Driver.userRequests) {
			if (r.equals(f)) {
				frAlreadyExists = true;
			}
		}
		if (frAlreadyExists) {
			System.out.println("There is already a pending request to make you " + friendName + "'s friend.");
		} else {
			Driver.userRequests.add(f);
			System.out.println("You've successfuly sent a friend request to " + friendName + "!");
		}
	}

	private void manageRequests() {
		Driver.logger.trace("In User.manageRequests");
		if (isAdmin) {
			System.out.println("Would you like to manage account requests or personal reuqests? (a/p)");
			String res = Driver.console.nextLine().toLowerCase();
			if (res.equals("a") || res.equals("account")) {
				manageAccountRequests();
			} else {
				managePersonalRequests();
			}
		} else {
			managePersonalRequests();
		}
	}

	private void managePersonalRequests() {
		Driver.logger.trace("In User.managePersonalRequests");
		boolean managing = true;
		while (managing) {
			Vector<Request> myRequests = new Vector<Request>();
			for (Request r : Driver.userRequests) {
				if (r.getUser().equals(username)) {
					myRequests.add(r);
				}
			}
			System.out.println("Personal Requests:");
			for (int i = 0; i < myRequests.size(); i++) {
				System.out.println("	" + i + " - " + myRequests.get(i));
			}
			System.out.println("	back - back to preveious menu");
			System.out.println("	quit - quit app");
			System.out.println("Please select a request to evalueate");
			String res = Driver.console.nextLine().toLowerCase();
			switch (res) {
			case "b":
			case "back":
				managing = false;
				break;
			case "q":
			case "quit":
				Driver.saveData();
				System.exit(0);
			default:
				try {
					int i = Integer.parseInt(res);
					myRequests.get(i).evaluate();
				} catch (Exception e) {
					System.out.println("Command not Recognised");
				}
				break;
			}
		}
	}

	private void manageAccountRequests() {
		Driver.logger.trace("In User.manageAccountRequests");
		boolean managing = true;
		while (managing) {
			System.out.println("Account Requests:");
			for (int i = 0; i < Driver.pendingAccounts.size(); i++) {
				System.out.println("	" + i + " - " + Driver.pendingAccounts.get(i));
			}
			System.out.println("	back - back to preveious menu");
			System.out.println("	quit - quit app");
			System.out.println("Please select a request to evalueate");
			String res = Driver.console.nextLine().toLowerCase();
			switch (res) {
			case "b":
			case "back":
				managing = false;
				break;
			case "q":
			case "quit":
				Driver.saveData();
				System.exit(0);
			default:
				try {
					int i = Integer.parseInt(res);
					Driver.pendingAccounts.get(i).evaluate();
				} catch (NumberFormatException e) {
					System.out.println("Command not Recognised");
				}
				break;
			}
		}

	}

	public void view() {
		Driver.logger.trace("In User.view");
		System.out.println("	Username: " + username);
		System.out.println("	Password: " + password);
		System.out.println("	isAdmin: " + isAdmin);
		System.out.println("	Accounts: ");
		for (int id : myAccountIds) {
			System.out.println("		" + Driver.accounts.get(id));
		}
		System.out.println("	Friends: ");
		for (String s : myFriends) {
			System.out.println("		" + Driver.users.get(s));
		}
	}

	// Getters and setters
	public String getUsername() {
		return username;
	}

	public Vector<String> getMyFriends() {
		return myFriends;
	}

	/*
	 * * public void setMyFriends(Vector<String> myFriends) { // shouldn't change //
	 * this.myFriends = myFriends; // } // public void setName(String name) {
	 * //shouldn't change // this.username = name; // }/
	 **/
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isAdmin() {
		return isAdmin;
	}
	public void setAdmin(boolean admin) {
		this.isAdmin = admin;
	}

	public Vector<Integer> getMyAccountIds() {
		return myAccountIds;
	}
	/*
	 * * public void setMyAccountIds(int[] myAccountIds) { //reference shouldn't
	 * change // this.myAccountIds = myAccountIds; // }/
	 **/

	@Override
	public String toString() {
		return username + (isAdmin ? "(Admin)" : "") + " - Accounts: " + myAccountIds.size() + ", Friends: "
				+ myFriends.size();
	}

}
