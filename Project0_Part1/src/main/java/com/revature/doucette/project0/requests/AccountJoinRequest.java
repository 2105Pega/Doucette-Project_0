package com.revature.doucette.project0.requests;

import com.revature.doucette.project0.driver.Driver;

public class AccountJoinRequest implements Request {

	private Status status;
	private int accountID;
	private String sender;
	private String recipiant;

	public AccountJoinRequest(int accountID, String sender, String recipiant) {
		this.status = Status.Pending;
		this.accountID = accountID;
		this.sender = sender;
		this.recipiant = recipiant;
	}

	private void approve() {
		// TODO Auto-generated method stub
		if (status.equals(Status.Pending)) {
			Driver.users.get(recipiant).getMyAccountIds().add(accountID);
			status = Status.Approved;
		} else {
			// TODO log warning: resolved request re-evaluated
		}

	}

	private void deny() {
		// TODO Auto-generated method stub
		if (status.equals(Status.Pending)) {
			status = Status.Denied;
		} else {
			// TODO log warning: resolved request re-evaluated
		}
	}

	private void dissolve() {
		Driver.userRequests.remove(this); //I think i can just drop the reference here
	}

	@Override
	public void view() {
		switch (status) {
		case Approved:
			System.out.println("Your account " + accountID + " was joined by " + recipiant +  ".");
			break;
		case Pending:
			if (Driver.accounts.get(accountID) != null) { // make sure account still exists
				System.out.println("Your Friend " + sender + " has requested for you to join their account "+ accountID + ".");
			} else {
				dissolve();
			}
			break;
		case Denied:
			System.out.println("Your friend " + recipiant + " denied your request to join account "
					+ Driver.accounts.get(accountID) + ".");
			break;
		}
	}

	@Override
	public String toString() {
		return "AccountJoinRequest [status=" + status + ", accountID=" + accountID + ", sender=" + sender
				+ ", recipiant=" + recipiant + "]";
	}

	@Override
	public String getUser() {
		switch (status) {
		case Pending:
			return recipiant;
		default:
			return sender;
		}
	}

	@Override
	public void evaluate() {
		if (Driver.accounts.get(accountID) != null) { // make sure account still exists
			switch(status) {
			case Pending:
				while (status == Status.Pending) {
					view();
					System.out.println("Please Select an action:");
					System.out.println("	approve - accept the account join request.");
					System.out.println("	deny - deny the account join request.");
					System.out.println("	nothing - take no action and leave this request for later evaluation.");
					switch (Driver.console.nextLine().toLowerCase()) {
					case "a":
					case "approve":
						approve();
						break;
					case "d":
					case "deny":
						deny();
						break;
					case "n":
					case "nothing":
						return;
					default:
						System.out.println("Action not Recognised");
						break;
					}
				}
				break;
			default:
				view();
				System.out.println("Would you like to remove this request from your feed? (y/n)");
				String res = Driver.console.nextLine().toLowerCase();
				if(res.equals("y")||res.equals("yes")) {
					dissolve(); 
					System.out.println("Account Join Request Removed.");
				}
				break;
			}

		} else {
			dissolve();
		}

	}

}
