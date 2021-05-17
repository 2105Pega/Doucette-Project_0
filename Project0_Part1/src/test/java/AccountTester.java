import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.revature.doucette.project0.data.Account;

public class AccountTester {

	@Test
	public static void testQuickDeposit() {
		Account a = new Account(1);
		Assertions.assertEquals(a.getBalance(),0);
		a.quickDeposit(100);
		Assertions.assertEquals(a.getBalance(),100);
		a.quickDeposit(10000);
		Assertions.assertEquals(a.getBalance(),10100);
		a.quickDeposit(0);
		Assertions.assertEquals(a.getBalance(), 10100);
		a.quickDeposit(-100);
		Assertions.assertEquals(a.getBalance(), 10100);
	}
	@Test
	public static void testQuickWithdraw() {
		Account a = new Account(1,99999,true);
		Assertions.assertEquals(a.getBalance(), 99999);
		a.quickWithdraw(99);
		Assertions.assertEquals(a.getBalance(),99900);
		a.quickWithdraw(0);
		Assertions.assertEquals(a.getBalance(),99900);
		a.quickWithdraw(99999);
		Assertions.assertEquals(a.getBalance(), 99900);
		a.quickWithdraw(-100);
		Assertions.assertEquals(a.getBalance(), 99900);
		a.quickWithdraw(99900);
		Assertions.assertEquals(a.getBalance(), 0);
	}
	
	@Test
	public static void testCanDeposit() {
		Account a = new Account(1);
		Assertions.assertTrue(a.canDeposit(100));
		Assertions.assertTrue(a.canDeposit(99999999));
		Assertions.assertFalse(a.canDeposit(0));
		Assertions.assertFalse(a.canDeposit(-999));
	}
	
	@Test
	public static void testCanWithdraw() {
		Account a = new Account(1,99999,true);
		Assertions.assertTrue(a.canWithdraw(100));
		Assertions.assertTrue(a.canWithdraw(99999));
		Assertions.assertFalse(a.canWithdraw(999999));
		Assertions.assertFalse(a.canWithdraw(0));
		Assertions.assertFalse(a.canWithdraw(-100));
	}
	
	@Test
	public static void testGetID() {
		Account a = new Account(1);
		Assertions.assertEquals(a.getId(), 1);
		a=new Account(500);
		Assertions.assertEquals(a.getId(), 500);
	}
	
	@Test
	public static void testBalance() {
		Account a = new Account(1);
		Assertions.assertEquals(a.getBalance(),0);
		a.addBalance(100);
		Assertions.assertEquals(a.getBalance(), 100);
		a.addBalance(100);
		Assertions.assertEquals(a.getBalance(), 200);
		a.addBalance(-100);
		Assertions.assertEquals(a.getBalance(), 100);
	}
	
	@Test
	public static void testApproved() {
		Account a = new Account(1);
		Assertions.assertFalse(a.isApproved());
		a.setApproved(false);
		Assertions.assertFalse(a.isApproved());
		a.setApproved(true);
		Assertions.assertTrue(a.isApproved());
		a.setApproved(true);
		Assertions.assertTrue(a.isApproved());
		a.setApproved(false);
		Assertions.assertFalse(a.isApproved());
	}
}
