package com.ngocketit.saovietcrm.model;

public class HelpListMenuItem extends ListMenuItem {
	public int helpContentResId;
	
	public HelpListMenuItem(int titleRestId, int iconResId, int helpContent) {
		super(titleRestId, iconResId, null);
		helpContentResId = helpContent;
	}
}
