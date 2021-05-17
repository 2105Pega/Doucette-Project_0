package com.revature.doucette.project0.requests;

import java.io.Serializable;

public interface Request extends Serializable{
 enum Status{
	 Pending,Approved,Denied
 }
 public void view();
 public void evaluate();
 public String getUser();
}
