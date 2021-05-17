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
		Driver.logger.trace("In Account(id)");
		this.id = id;
		this.balance = 0;
		this.approved = false;
	}

	public Account(int id, double balance, boolean approved) {
		Driver.logger.trace("In Account(id,balance,approved)");
		this.balance = balance;
		this.id = id;
		this.approved = approved;
	}

	// Methods
	public void manage() {
		Driver.logger.trace("In Account.manage");
		if (approved) {
			manageAsAdmin();
		} else {
			System.out.println("You cant manage this account until it is approved.");
		}
	}

	public void manageAsAdmin() {
		Driver.logger.trace("In Account.manageAsAdmin");
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
		Driver.logger.trace("In Account.deposit");
		System.out.println("Please enter the ammount you'd like to deposit: ");
		try {
			double res = Double.parseDouble(Driver.console.nextLine());
			quickDeposit(res);
		} catch (NumberFormatException e) {
			System.out.println("Unrecognised deposit ammount.");
		}
	}

	public void quickDeposit(double ammount) {
		Driver.logger.trace("In Account.quickDeposit");
		if (canDeposit(ammount)) {
			this.balance += ammount;
			Driver.logger.info("Deposited "+ammount+" in account "+ id);
		} else {
			System.out.println("Deposit ammount invalid.");
			Driver.logger.debug("Deposit failed, Invalid ammount");
		}
	}

	public boolean canDeposit(double ammount) {
		Driver.logger.trace("In Account.canDeposit");
		if (ammount > 0) {
			return true; // success
		} else {
			return false;
		}
	}

	private void withdraw() {
		Driver.logger.trace("In Account.withdraw");
		System.out.println("Please enter the ammount you'd like to withdraw: ");
		try {
			double res = Double.parseDouble(Driver.console.nextLine());
			quickWithdraw(res);
		} catch (NumberFormatException e) {
			System.out.println("Unrecognised withdrawl ammount.");
		}
	}

	public void quickWithdraw(double ammount) {
		Driver.logger.trace("In Account.quickWithdraw");
		if (canWithdraw(ammount)) {
			this.balance -= ammount;
			Driver.logger.info("Withdrew "+ammount+" from account " + id);
		} else {
			System.out.println("Withdrawl ammount invalid.");
			Driver.logger.debug("Withdrawl failed, Invalid ammount");
		}
	}

	public boolean canWithdraw(double ammount) {
		Driver.logger.trace("In Account.canWithdraw");
		if (ammount > 0 && ammount <= this.balance) {
			return true;
		} else {
			return false;
		}
	}

	private void transfer() {
		Driver.logger.trace("In Account.transfer");
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
				Driver.logger.warn("account id not a number");
				continue;
			}
			if (otherAccount == null) {
				System.out.println("Account " + otherAccountId + " does not exist.");
				Driver.logger.info("User attempted to access account " + otherAccountId + ". It does not exist.");
				continue;
			} else {
				transfer(otherAccount);
				transfering = false;
			}
		}
	}

	private void transfer(Account account) {
		Driver.logger.trace("In Account.transfer(ammount)");
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
						Driver.logger.info("Successful trnasfer of $" + ammount + " from account "+id+" to account " + account.getId());
					}else {
						System.out.println("Transfer ammount invalid.");
						Driver.logger.debug("Transaction failed, Invalid ammount");
						continue;
					}
				} catch (NumberFormatException e) {
					System.out.println("Unrecognised ammount");
					Driver.logger.warn("transfer ammount not a number");
					continue;
				}
				transfering=false;
				break;
			case "r":
			case "recieve":
				System.out.println("Please enter an ammount to take: ");
				try {
					double ammount = Double.parseDouble(Driver.console.nextLine());
					if(account.canWithdraw(ammount)&&canDeposit(ammount)) {
						FundsTransferRequest ftr = new FundsTransferRequest(Driver.currentUser.getUsername(),id,account.getId(),ammount);
						Driver.userRequests.add(ftr);
						System.out.println("Request sent to transfer funds to your account!");
						System.out.println("The transaction will be completed if and when one of the account holders accepts your request.");
					}else {
						System.out.println("Transfer ammount invalid.");
						Driver.logger.debug("Transaction failed, Invalid ammount");
						continue;
					}
				} catch (NumberFormatException e) {
					System.out.println("Unrecognised ammount");
					Driver.logger.warn("transfer ammount not a number");
					continue;
				}
			default:
				transfering=false;
				break;
			}
		}
	}

	private void delete() {
		Driver.logger.trace("In Account.delete");
		System.out.println("Are you sure that you would like to delete this account? (y/n)");
		String res = Driver.console.nextLine().toLowerCase();
		if(res.equals("y")||res.equals("yes")) {
			quickWithdraw(balance);// user should get their money out
			Driver.accounts.remove(id);// drop account reference
			for (User u : Driver.getMembersOfAccount(id)) {
				u.getMyAccountIds().remove(id);
			}
			Driver.logger.info("Account: " + this + " deleted.");
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
		Driver.logger.trace("In Account.view");
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
