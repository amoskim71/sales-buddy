package com.ngocketit.saovietcrm.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneralUtils {
	public static String md5(String in) {
	    MessageDigest digest;

	    try {
	        digest = MessageDigest.getInstance("MD5");
	        digest.reset();
	        digest.update(in.getBytes());
	        byte[] a = digest.digest();
	        int len = a.length;
	        StringBuilder sb = new StringBuilder(len << 1);
	        for (int i = 0; i < len; i++) {
	            sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
	            sb.append(Character.forDigit(a[i] & 0x0f, 16));
	        }
	        return sb.toString();
	    } catch (NoSuchAlgorithmException e) { 
	    	e.printStackTrace(); 
	    }
	    return null;
	}
	
	public static String sqlTimestampToDate(String timestamp) {
		String[] parts = timestamp.split(" ");
		if (parts.length == 2) {
			String[] subParts = parts[0].split("-");
			if (subParts.length == 3) {
				return subParts[2] + "-" + subParts[1] + "-" + subParts[0];
			}
		}
		
		return null;
	}

	public static String dateToSqlTimestamp(String dateStr) {
		String[] parts = dateStr.split("-");

		if (parts.length == 3) {
			String date = parts[0];
			String month = parts[1];
			String year = parts[2];
			
			if (date.length() < 2) date = "0" + date;
			if (month.length() < 2) month = "0" + month;
			
			return year + "-" + month + "-" + date + " 00:00:00";
		}
		
		return null;
	}
	
	public static String getCurrentTimestamp() {
        SimpleDateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dfmt.format(new Date());
	}
	
	public static String formatCurrency(double currency) {
		NumberFormat nf = NumberFormat.getCurrencyInstance();

		DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) nf).getDecimalFormatSymbols();
		decimalFormatSymbols.setCurrencySymbol("");
		//decimalFormatSymbols.setDecimalSeparator(',');
		//decimalFormatSymbols.setMonetaryDecimalSeparator(',');

		((DecimalFormat) nf).setDecimalFormatSymbols(decimalFormatSymbols);
		return nf.format(currency);
	}
}
