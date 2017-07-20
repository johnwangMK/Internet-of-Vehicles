package com.carUserLogin;

import android.content.Context;
import android.content.SharedPreferences;

public class CheckLoginState {
	
	private static SharedPreferences sharedPreferences;
	public static String username = "";
	private static String password = "";
	
	public static boolean check(Context context){
		sharedPreferences = context.getSharedPreferences("carUserLoginData",Context.MODE_PRIVATE);
		username = sharedPreferences.getString("userphoneNo", "");
		password = sharedPreferences.getString("upassword", "");
		if(sharedPreferences != null && username != "" && password != ""){
			return true;
		}else{
			return false;
		}
		
	}
	
}
