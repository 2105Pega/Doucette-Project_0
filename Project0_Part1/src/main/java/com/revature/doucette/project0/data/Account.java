package com.revature.doucette.project0.data;

import java.io.Serializable;

import com.revature.doucette.project0.driver.Driver;
import com.revature.doucette.project0.requests.FundsTransferRequest;

public class Account implements Serializable {
	// member variables
	private int id;
	private double balance;
	private boolean approved;

	// Constructor
//	public Account() {
//		this.balance = 0;
//		this.approved = false;
//		this.id = -1; // invalid
//	}
	public Account(int id) {
		this.id = id;
		this.balance = 0;
		this.approved = false;
	}

	public Account(int id, double balance, boolean approved) {
		this.balance = balance;
		this.id = id;
		this.approved = approved;
	}

	// Methods
	public void manage() {
		if (approved) {
			manageAsAdmin();
		} else {
			System.out.println("You cant manage this account until it is approved.");
		}
	}

	public void manageAsAdmin() {
		boolean managing = true;
		while (managing) {
			// Read
			view();
			// Update
			// Destroy
			System.out.println("Please select an action.");
			System.out.println("	add - add funds to this account.");
			System.out.println("	remove - remove funds from this account.");
			System.out.println("	transfer - transfer funds to/from another account.");
			System.out.println("	delete - delete this account.");
			System.out.println("	back - back to previous menu");
			System.out.println("	quit - exit app");
			String res = Driver.console.nextLine().toLowerCase();
			switch (res) {
			case "a":
			case "add":
				deposit();
				break;
			case "r":
			case "remove":
				withdraw();
				break;
			case "t":
			case "transfer":
				transfer();
				break;
			case "d":
			case "delete":
				delete();
				break;
			case "b":
			case "back":
				return;
			case "q":
			case "quit":
				Driver.saveData();
				System.exit(0);
				return;
			default:
				System.out.println("Command not recognised");
				break;
			}
		}
	}

	private void deposit() {
		System.out.println("Please enter the ammount you'd like to deposit: ");
		try {
			double res = Double.parseDouble(Driver.console.nextLine());
			quickDeposit(res);
		} catch (NumberFormatException e) {
			System.out.println("Unrecognised deposit ammount.");
		}
	}

	private void quickDeposit(double ammount) {
		if (canDeposit(ammount)) {
			this.balance += ammount;
		} else {
			System.out.println("Deposit ammount invalid.");
		}
	}

	public boolean canDeposit(double ammount) {
		if (ammount > 0) {
			return true; // success
		} else {
			return false;
		}
	}

	private void withdraw() {
		System.out.println("Please enter the ammount you'd like to withdraw: ");
		try {
			double res = Double.parseDouble(Driver.console.nextLine());
			quickWithdraw(res);
		} catch (NumberFormatException e) {
			System.out.println("Unrecognised withdrawl ammount.");
		}
	}

	private void quickWithdraw(double ammount) {
		if (canWithdraw(ammount)) {
			this.balance -= ammount;
		} else {
			System.out.println("Withdrawl ammount invalid.");
		}
	}

	public boolean canWithdraw(double ammount) {
		if (ammount > 0 && ammount <= this.balance) {
			return true;
		} else {
			return false;
		}
	}

	private void transfer() {
		boolean transfering = true;
		while (transfering) {
			System.out.println("Please identify an account to transfer with.");
			Account otherAccount = null;
			int otherAccountId;
			try {
				otherAccountId = Integer.parseInt(Driver.console.nextLine());
				otherAccount = Driver.accounts.get(otherAccountId);
			} catch (NumberFormatException e) {
				System.out.println("invalid account id");
				continue;
			}
			if (otherAccount == null) {
				System.out.println("Account " + otherAccountId + " does not exist.");
				continue;
			} else {
				transfer(otherAccount);
				transfering = false;
			}
		}
	}

	private void transfer(Account account) {
		boolean transfering = true;
		while (transfering) {
			System.out.println("Are you sending or recieving funds? (send/recieve/cancel)");
			String res = Driver.console.nextLine().toLowerCase();
			switch (res) {
			case "s":
			case "send":
				System.out.println("Please enter an ammount to send: ");
				try {
					double ammount = Double.parseDouble(Driver.console.nextLine());
					if(canWithdraw(ammount)&&account.canDeposit(ammount)) {
						this.balance -= ammount;
						account.addBalance(ammount);
						System.out.println("Successful trnasfer of $" + ammount + " from your account to account " + account.getId());
					}else {
						System.out.println("Transfer ammount invalid.");
						continue;
					}
				} catch (NumberFormatException e) {
					System.out.println("Unrecognised ammount");
					continue;
				}
				transfering=false;
				break;
			case "r":
			case "recieve":
				System.out.println("Please enter an ammount to take: ");
				try {
					double ammount = Double.parseDouble(Driver.console.nextLine());
					if(canWithdraw(ammount)&&account.canDeposit(ammount)) {
						FundsTransferRequest ftr = new FundsTransferRequest(Driver.currentUser.getUsername(),id,account.getId(),ammount);
						Driver.userRequests.add(ftr);
						System.out.println("Request sent to transfer funds to your account!");
						System.out.println("The transaction will be completed if and when one of the account holders accepts your request.");
					}else {
						System.out.println("Transfer ammount invalid.");
						continue;
					}
				} catch (NumberFormatException e) {
					System.out.println("Unrecognised ammount");
					continue;
				}
			default:
				transfering=false;
				break;
			}
		}
	}

	private void delete() {
		System.out.println("Are you sure that you would like to delete this account? (y/n)");
		String res = Driver.console.nextLine().toLowerCase();
		if(res.equals("y")||res.equals("yes")) {
			quickWithdraw(balance);// user should get their money out
			Driver.accounts.remove(id);// drop account reference
		}
	}

	//// getters and setters
	public int getId() {
		return id;
	}

//	public void setId(int id) { //id shouldn't change
//		this.id = id;
//	}
	public double getBalance() {
		return balance;
	}

	public void addBalance(double ammount) {
		this.balance += ammount;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public void view() {
		System.out.println("Account Info");
		System.out.println("	Id: " + id);
		System.out.println("	Balance: " + balance);
		System.out.println("	Approval: " + approved);
	}

	//// 'object' method overrides
	@Override
	public String toString() {
		return id + " - Balance: " + balance + " - " + (approved ? "Approved" : "Pending Approval");
	}

}
