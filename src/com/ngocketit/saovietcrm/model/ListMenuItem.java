package com.ngocketit.saovietcrm.model;

public class ListMenuItem {
	public int titleResId;
	public int iconResId;
	public Class<?> intentClass;
	
	public ListMenuItem(int titleRestId, int iconResId, Class<?> intentClass) {
		this.titleResId = titleRestId;
		this.iconResId = iconResId;
		this.intentClass = intentClass;
	}
}
