package com.adt.authservice.util;

public enum WildCardEnum {
	LOGGEDIN_USER_ID;

	public static boolean isAvalableEnum(String s) {
		for (WildCardEnum uenam : values()) {
			if (uenam.name().equals(s))
				return true;
		}
		return false;
	}
}
