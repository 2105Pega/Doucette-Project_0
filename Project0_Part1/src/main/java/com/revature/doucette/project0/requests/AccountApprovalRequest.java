package com.revature.doucette.project0.requests;

import com.revature.doucette.project0.data.User;
import com.revature.doucette.project0.driver.Driver;

public class AccountApprovalRequest implements Request {

	private Status status;
	private int accountId;
	public String applicantName;

	public AccountApprovalRequest(int accountId, String applicantName) {
		this.accountId = accountId;
		this.applicantName = applicantName;
		status = Status.Pending;

	}

	private void approve() {
		if (status.equals(Status.Pending)) {
			status = Status.Approved;
			Driver.pendingAccounts.remove(this);
			Driver.userRequests.add(this);
			Driver.accounts.get(accountId).setApproved(true);
		} else {
			// TODO log warning: resolved request re-evaluated
		}

	}

	private void deny() {
		if (status.equals(Status.Pending)) {
			status = Status.Denied;
			Driver.pendingAccounts.remove(this);
			Driver.userRequests.add(this);
			Driver.accounts.remove(accountId);
			for (User u : Driver.getMembersOfAccount(accountId)) {
				u.getMyAccountIds().remove((Integer) accountId);
			}
		} else {
			// TODO log warning: resolved request re-evaluated
		}
	}

	@Override
	public void view() {
		switch (status) {
		case Approved:
			System.out.println("Your account " + accountId + " was approved!");
			break;
		case Pending:
			System.out.println(Driver.accounts.get(accountId));
			break;
		case Denied:
			System.out.println("Your account " + accountId + " was denied and has been removed.");
			break;
		}

	}

	@Override
	public String toString() {
		return "AccountApprovalRequest [status=" + status + ", accountId=" + accountId + ", applicantName="
				+ applicantName + "]";
	}

	@Override
	public String getUser() {
		return applicantName;
	}


	@Override
	public void evaluate() {
		switch(status) {
		case Pending:
			while (status == Status.Pending) {
				view();
				System.out.println("Please select an action:");
				System.out.println("	approve - approve account");
				System.out.println("	deny - remove account");
				System.out.println("	nothing - leave request for future review");
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
				Driver.userRequests.remove(this); //I think i can just drop the reference here
				System.out.println("Account Approval Request Removed.");
			}
			break;
		}

		
	}
}
