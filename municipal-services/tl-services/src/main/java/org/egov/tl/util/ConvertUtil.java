package org.egov.tl.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ConvertUtil {
	
	public static String getCurrentDate(String timeZoneName,Long timestamp) {
		
		Date today = timestamp==null?new Date():new Date(timestamp);
	  //displaying this date on IST timezone
//      DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm:SS z");
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        df.setTimeZone(TimeZone.getTimeZone(timeZoneName));
        String IST = df.format(today);
        return IST;
	}

	public static boolean isNumeric(String strNum) {
		strNum=strNum.replaceAll("\\s+", "");
		if (strNum == null) {
			return false;
		}
		try {
			double d = Double.parseDouble(strNum);
		} catch (Exception nfe) {
			return false;
		}
		return true;
	}

	public static String numberToComa(String num) {
		if(num==null||num.equals("0")) {
			return "0";
		}
		num=num.replaceAll("\\s+", "");
		String currencyFormate="Rs ";
		try {
//		if (isNumeric(num)&&num.contains(".")&&num.split("\\.")[0].length()<4) {
//			String uno="";
//			String no=num.split("\\.")[0];
//			uno=no.charAt(0)+","+no.charAt(1)+no.charAt(2);
//			currencyFormate+=uno;
//		}else if(isNumeric(num)&&!num.contains(".")&&num.length()<4) {
//			String uno="";
//			String no=num;
//			uno=no.charAt(0)+","+no.charAt(1)+no.charAt(2);
//			currencyFormate+= uno+".00";
//	    }else 
	   if(isNumeric(num)) {	
//			Format format = com.ibm.icu.text.NumberFormat.getCurrencyInstance(new Locale("en", "in"));
			String dd=num;//format.format(new BigDecimal(num)).replace("₹", "");
//			int index=dd.lastIndexOf(",");
//			String fs=dd.substring(0,index);
//			String[] ls=dd.substring(index+1).split("\\.");
//			String no=ls[0];
//			no = no.charAt(0)+","+no.charAt(1)+no.charAt(2)+"."+ls[1];
//			fs=fs+","+no;
//			System.out.println(fs+" , "+ls.length+","+dd.substring(index+1).split("\\.")[0]);	
			currencyFormate+= dd;
		}else {
			currencyFormate="Wrong Input";	
		}
		}catch (Exception e) {
			currencyFormate="Wrong Input";	
		}
		
		return currencyFormate;
		
	}

	public static String numberToWords(String num) {
		if(num==null) {
			return "Wrong Input";
		}else if(num.equals("0")){
			return "zero";	
		}
		
		num=num.replaceAll("\\s+", "");
	 try {
	  if(isNumeric(num)) {
		BigDecimal bd = new BigDecimal(num);
		long number = bd.longValue();
		long no = bd.longValue();
		int decimal = (int) (bd.remainder(BigDecimal.ONE).doubleValue() * 100);
		int digits_length = String.valueOf(no).length();
		int i = 0;
		ArrayList<String> str = new ArrayList<>();
		HashMap<Integer, String> words = new HashMap<>();
		words.put(0, "");
		words.put(1, "One");
		words.put(2, "Two");
		words.put(3, "Three");
		words.put(4, "Four");
		words.put(5, "Five");
		words.put(6, "Six");
		words.put(7, "Seven");
		words.put(8, "Eight");
		words.put(9, "Nine");
		words.put(10, "Ten");
		words.put(11, "Eleven");
		words.put(12, "Twelve");
		words.put(13, "Thirteen");
		words.put(14, "Fourteen");
		words.put(15, "Fifteen");
		words.put(16, "Sixteen");
		words.put(17, "Seventeen");
		words.put(18, "Eighteen");
		words.put(19, "Nineteen");
		words.put(20, "Twenty");
		words.put(30, "Thirty");
		words.put(40, "Forty");
		words.put(50, "Fifty");
		words.put(60, "Sixty");
		words.put(70, "Seventy");
		words.put(80, "Eighty");
		words.put(90, "Ninety");
		String digits[] = { "", "Hundred", "Thousand", "Lakh", "Crore" };
		while (i < digits_length) {
			int divider = (i == 2) ? 10 : 100;
			number = no % divider;
			no = no / divider;
			i += divider == 10 ? 1 : 2;
			if (number > 0) {
				int counter = str.size();
				String plural = (counter > 0 && number > 9) ? "s" : "";
				String tmp = (number < 21) ? words.get(Integer.valueOf((int) number)) + " " + digits[counter] + plural
						: words.get(Integer.valueOf((int) Math.floor(number / 10) * 10)) + " "
								+ words.get(Integer.valueOf((int) (number % 10))) + " " + digits[counter] + plural;
				str.add(tmp);
			} else {
				str.add("");
			}
		}

		Collections.reverse(str);
		String Rupees = String.join(" ", str).trim();

		String paise = (decimal) > 0
				? " And Paise " + words.get(Integer.valueOf((int) (decimal - decimal % 10))) + " "
						+ words.get(Integer.valueOf((int) (decimal % 10)))
				: "";
		return "" + Rupees + paise + " only";
	  }else {
		  return "Wrong Input";
	  }
	 }catch (Exception e) {
		 return "Wrong Input";
	}
	}
	
	
    public static String getCurrentFullDate(String timeZoneName,Long timestamp) {
		
		Date today = timestamp==null?new Date():new Date(timestamp);
	  //displaying this date on IST timezone
//      DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm:SS z");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
        df.setTimeZone(TimeZone.getTimeZone(timeZoneName));
        String IST = df.format(today);
        return IST;
	}
    
    public static String splitAllApplicationNumber(String applicationNumber){
		StringJoiner sj=new StringJoiner(",");
		String applicationNumbersList[]=applicationNumber.split(",");
		for(String apl:applicationNumbersList) {
			sj.add("'"+apl+"'");
		}
		return sj.toString();
	}

	
//	public static void main(String[] args) {
//		System.out.println(ConvertUtil.numberToWords(String.valueOf(6094912.0)));
//	}
}
