package com.example.musictest;


public class Utilities {
	
	/**
	 * Function to convert milliseconds time to
	 * Timer Format
	 * Hours:Minutes:Seconds
	 * */
	public String milliSecondsToTimer(long milliseconds){
		String finalTimerString = "";
		String secondsString = "";
		
		// Convert total duration into time
		   int hours = (int)( milliseconds / (1000*60*60));
		   int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
		   int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
		   // Add hours if there
		   if(hours > 0){
			   finalTimerString = hours + ":";
		   }
		   
		   // Prepending 0 to seconds if it is one digit
		   if(seconds < 10){ 
			   secondsString = "0" + seconds;
		   }else{
			   secondsString = "" + seconds;}
		   
		   finalTimerString = finalTimerString + minutes + ":" + secondsString;
		
		// return timer string
		return finalTimerString;
	}
	
	/**
	 * Function to get Progress percentage
	 * @param currentDuration
	 * @param totalDuration
	 * */
	public int getProgressPercentage(long currentDuration, long totalDuration){
		Double percentage = (double) 0;
		
		long currentSeconds = (int) (currentDuration / 1000);
		long totalSeconds = (int) (totalDuration / 1000);
		
		// calculating percentage
		percentage =(((double)currentSeconds)/totalSeconds)*100;
		
		// return percentage
		return percentage.intValue();
	}

	/**
	 * Function to change progress to timer
	 * @param progress - 
	 * @param totalDuration
	 * returns current duration in milliseconds
	 * */
	public int progressToTimer(int progress, int totalDuration) {
		int currentDuration = 0;
		totalDuration = (int) (totalDuration / 1000);
		currentDuration = (int) ((((double)progress) / 100) * totalDuration);
		
		// return current duration in milliseconds
		return currentDuration * 1000;
	}
	
	public String ParseName(String name) {
		char[] myName = name.toCharArray();
		for (Integer i=0; i<name.length(); i++) {
			if (i==0) {
				if (Character.isLowerCase(myName[i])) {
					myName[i] = Character.toUpperCase(myName[i]);
				}
			}
			else if (myName[i-1]==' ') {
				if (Character.isLowerCase(myName[i])) {
					myName[i] = Character.toUpperCase(myName[i]);
				}
			}
		}
		return String.valueOf(myName);
	}
	
	public String ParseTitle(String name) {
		String tmp = name;
		if (Character.isDigit(name.charAt(0)) && name.charAt(1)=='.') {
			if (name.charAt(2)==' ') {
				tmp = name.substring(3, name.length()-3);
			}
			else {
				tmp = name.substring(2, name.length()-2);
			}
		}
		return tmp;
	}
}
