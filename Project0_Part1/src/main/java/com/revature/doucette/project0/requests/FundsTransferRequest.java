package com.revature.doucette.project0.requests;

import java.util.Vector;

import com.revature.doucette.project0.data.Account;
import com.revature.doucette.project0.data.User;
import com.revature.doucette.project0.driver.Driver;
import com.revature.doucette.project0.requests.Request.Status;

public class FundsTransferRequest implements Request {

	private Status status;
	private String sender;
	private int senderAccountId;
	private int recipiantAccountId;
	private double transferAmmount;

	public FundsTransferRequest(String sender, int senderAccountId, int recipiantAccountId, double transferAmmount) {
		this.sender = sender;
		this.senderAccountId = senderAccountId;
		this.recipiantAccountId = recipiantAccountId;
		this.transferAmmount = transferAmmount;
	}

	private void approve() {
		if (status.equals(Status.Pending)) {
			Account aSend = Driver.accounts.get(senderAccountId);
			Account aRecieve = Driver.accounts.get(recipiantAccountId);
			if(aRecieve.canWithdraw(transferAmmount)&&aSend.canDeposit(transferAmmount)) { // probably only need the canWithdraw here
				aSend.addBalance(transferAmmount);
				aRecieve.addBalance(-transferAmmount);
			}else {
				System.out.println("Your account doen't have enough funds for the transfer.");
				System.out.println("Request will remain pending.");
				return;
			}
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
			System.out.println("Funds Transfer was successful!");
			break;
		case Pending:
			System.out.println("User " + sender + " has requested $" + transferAmmount + " be transfered from Account: "
					+ recipiantAccountId);
			break;
		case Denied:
			System.out.println("Funds transfer was unsuccesful.");
			break;
		}

	}

	@Override
	public void evaluate() {

		switch (status) {
		case Pending:
			if (Driver.accounts.get(senderAccountId) != null && Driver.accounts.get(recipiantAccountId) != null) { 
				while (status == Status.Pending) {
					view();
					System.out.println("Please Select an action:");
					System.out.println("	approve - accept the funds transfer.");
					System.out.println("	deny - deny the funds transfer.");
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
			} else {
				status = Status.Denied;
			}
			break;
		default:
			view();
			System.out.println("Would you like to remove this request from your feed? (y/n)");
			String res = Driver.console.nextLine().toLowerCase();
			if (res.equals("y") || res.equals("yes")) {
				Driver.userRequests.remove(this);
				System.out.println("Transfer Request Removed.");
			}
			break;
		}

	}

	@Override
	public String getUser() {
		switch(status) {
		case Pending:
			Vector<User> recipiants = Driver.getMembersOfAccount(recipiantAccountId);
			if(recipiants.contains(Driver.currentUser)) {
				return Driver.currentUser.getUsername();
			}else {
				return recipiants.get(0).getUsername();
			}
		default:
			return sender;
		}
	}

}