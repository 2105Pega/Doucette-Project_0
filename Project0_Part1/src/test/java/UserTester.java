import java.util.Vector;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.revature.doucette.project0.data.User;

public class UserTester {

	@Test
	public static void testCreateAccount() {
		// private (not sure i should test)
	}
	
	@Test
	public static void testRemoveFriend() {
		// private (not sure i should test)
	}
	
	@Test
	public static void testCreateNewFriendRequest() {
		// private (not sure i should test)
	}
	
	@Test
	public static void testGetUsername() {
		User u = new User("name", "pass",false);
		Assertions.assertEquals(u.getUsername(), "name");
		u = new User("billy","pass",false);
		Assertions.assertEquals(u.getUsername(), "billy");
	}
	
	@Test
	public static void testPassword() {
		User u = new User("name", "pass",false);
		Assertions.assertEquals(u.getPassword(), "pass");
		u.setPassword("billy");
		Assertions.assertEquals(u.getPassword(), "billy");
	}
	
	@Test
	public static void testAdmin() {
		User u = new User("name", "pass",false);
		Assertions.assertFalse(u.isAdmin());
		u.setAdmin(true);
		Assertions.assertTrue(u.isAdmin());
	}
	
	@Test
	public static void testGetMyFriends() {
		User u = new User("name", "pass",false);
		Vector<String> friends = u.getMyFriends();
		String[] names = {"a","b","c","d","e"};
		for(String name:names) {
			friends.add(name);
		}
		Vector<String> friends2 = u.getMyFriends();
		for(int i=0;i<friends2.size();i++) {
			Assertions.assertEquals(friends2.get(i), names[i]);
		}
	}
	
	@Test
	public static void testGetMyAccountIds() {
		User u = new User("name", "pass",false);
		Vector<Integer>accounts = u.getMyAccountIds();
		int[] ids = {1,2,3,4,5};
		for(int id:ids) {
			accounts.add(id);
		}
		Vector<Integer> accounts2 = u.getMyAccountIds();
		for(int i=0;i<accounts2.size();i++) {
			Assertions.assertEquals(accounts2.get(i), ids[i]);
		}
	}
	
}
