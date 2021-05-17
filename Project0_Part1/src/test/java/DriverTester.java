import java.util.Vector;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.revature.doucette.project0.data.Account;
import com.revature.doucette.project0.data.User;
import com.revature.doucette.project0.driver.Driver;

public class DriverTester {

	@Test
	public static void testNextAvailableAccountID() {
		Driver.accounts.put(6, new Account(6));
		Assertions.assertEquals(Driver.nextAvailableAccountID(), 7);
		Assertions.assertEquals(Driver.nextAvailableAccountID(), 8);
		Assertions.assertEquals(Driver.nextAvailableAccountID(), 9);
	}

	@Test
	public static void testGetMembersOfAccount() {
		String[] usernames = { "a", "b", "c", "d", "e" };
		for (String n : usernames) {
			User u = new User("a", "12345", false);
			for (int i = 1; i < 4; i++) {
				u.getMyAccountIds().add(i);
			}
			Driver.users.put(n, u);
		}
		String[] losernames = {"f","g","h","i","j"};
		for (String n : losernames) {
			User u = new User("a", "12345", false);
			for (int i = 3; i < 6; i++) {
				u.getMyAccountIds().add(i);
			}
			Driver.users.put(n, u);
		}
		Vector<User> out1 = Driver.getMembersOfAccount(1);
		for(int i=0; i<out1.size();i++) {
			Assertions.assertEquals(out1.get(i).getUsername(), usernames[i]);
		}
		Vector<User> out5 = Driver.getMembersOfAccount(5);
		for(int i=0; i<out5.size();i++) {
			Assertions.assertEquals(out5.get(i).getUsername(),losernames[i]);
		}
		Vector<User> out3 = Driver.getMembersOfAccount(3);
		for(int i=0; i<out5.size();i++) {
			if (i >4) {
				Assertions.assertEquals(out3.get(i).getUsername(),losernames[i-5]);
			}else {
				Assertions.assertEquals(out3.get(i).getUsername(), usernames[i]);
			}
		}
		Vector<User> out100 = Driver.getMembersOfAccount(100);
		Assertions.assertEquals(out100, new Vector<User>());
	}
	
	// should i test load and save data?
}
