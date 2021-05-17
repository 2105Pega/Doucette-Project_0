package com.revature.doucette.project0.requests;

import com.revature.doucette.project0.driver.Driver;
import com.revature.doucette.project0.requests.Request.Status;

public class FriendRequest implements Request {

	private Status status;
	private String sender;
	private String recipiant;

	public FriendRequest(String sender, String recipiant) {
		this.status = Status.Pending;
		this.sender = sender;
		this.recipiant = recipiant;
	}

	private void approve() {
		if (status.equals(Status.Pending)) {
			Driver.users.get(recipiant).getMyFriends().add(sender);
			Driver.users.get(sender).getMyFriends().add(recipiant);
			status = Status.Approved;
		} else {
			// TODO log warning: resolved request re-evaluated
		}
	}

	private void deny() {
		if (status.equals(Status.Pending)) {
			status = Status.Denied;
		} else {
			// TODO log warning: resolved request re-evaluated
		}

	}

	@Override
	public void view() {
		switch (status) {
		case Approved:
			System.out.println(recipiant + " has agreed to become your friend!");
			break;
		case Pending:
			System.out.println("User " + sender + " has requested for you to become their friend! ");
			break;
		case Denied:
			System.out.println(recipiant + " denied your friend request.");
			break;
		}
	}

	@Override
	public String toString() {
		return "FriendRequest [status=" + status + ", sender=" + sender + ", recipiant=" + recipiant + "]";
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
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			FriendRequest fr = null;
			try {
				fr = (FriendRequest) o;
			} catch (Exception e) {
				return false;
			}
			if (fr == null) {
				return false;
			} else {
				if ((fr.sender.equals(sender) && fr.recipiant.equals(recipiant))
						|| (fr.recipiant.equals(sender) && fr.sender.equals(recipiant))) {
					return true;
				} else {
					return false;
				}
			}
		}

	}

	@Override
	public void evaluate() {
		switch (status) {
		case Pending:
			while (status == Status.Pending) {
				view();
				System.out.println("Please Select an action:");
				System.out.println("	approve - accept the friend request.");
				System.out.println("	deny - deny the friend request.");
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
			if (res.equals("y") || res.equals("yes")) {
				Driver.userRequests.remove(this);
				System.out.println("Friend Request Removed.");
			}
			break;
		}

	}
}
