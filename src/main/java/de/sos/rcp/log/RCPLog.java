package de.sos.rcp.log;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RCPLog {
	
	private static RCPLog theInstance = new RCPLog();
	
	
	private RCPCoreLogFormatter mFormatter = null;
	
	private RCPLog(){
		mFormatter = new RCPCoreLogFormatter();
	}

	public static String getCallerClassName() {
		//using reflections should be quite fast, but unfortunately oracle removed (not only deprecated!) this method
		//return sun.reflect.Reflection.getCallerClass(callStackDepth).getName();
		return Thread.currentThread().getStackTrace()[3].getClassName();
	}

	public static int getIntendation() {
		return theInstance.mFormatter.getIntendation();
	}
	public static void setIntendation(final int i){
		theInstance.mFormatter.setIndentation(i);
	}
	public static void error(final int intendationDelta, final String message){
		if (theInstance != null) theInstance.mFormatter.addIndentation(intendationDelta);
		error(message);
	}
	public static void error(final String message, final int intendationDelta){
		error(message);
		if (theInstance != null) theInstance.mFormatter.addIndentation(intendationDelta);
	}
	public static void error(final String message)
	{
		try{
			final Logger l = Logger.getLogger(getCallerClassName());
			l.log(Level.SEVERE, message);
		}catch(final Exception e){ System.out.println("Failed to Log message: " + message); }
	}


	public static void warn(final int intendationDelta, final String message){
		if (theInstance != null) theInstance.mFormatter.addIndentation(intendationDelta);
		warn(message);
	}
	public static void warn(final String message, final int intendationDelta){
		warn(message);
		if (theInstance != null) theInstance.mFormatter.addIndentation(intendationDelta);
	}
	public static void warn(final String message)
	{
		try{
			final Logger l = Logger.getLogger(getCallerClassName());
			l.log(Level.WARNING, message);
		}catch(final Exception e){ System.out.println("Failed to Log message: " + message); }
	}

	public static void info(final int intendationDelta, final String message){
		if (theInstance != null) theInstance.mFormatter.addIndentation(intendationDelta);
		info(message);
	}
	public static void info(final String message, final int intendationDelta){
		info(message);
		if (theInstance != null) theInstance.mFormatter.addIndentation(intendationDelta);
	}
	public static void info(final String message)
	{
		try{
			final Logger l = Logger.getLogger(getCallerClassName());
			l.log(Level.INFO, message);
		}catch(final Exception e){System.out.println("Failed to Log message: " + message); }
	}

	public static void debug(final int intendationDelta, final String message){
		if (theInstance != null) theInstance.mFormatter.addIndentation(intendationDelta);
		debug(message);
	}
	public static void debug(final String message, final int intendationDelta){
		debug(message);
		if (theInstance != null) theInstance.mFormatter.addIndentation(intendationDelta);
	}
	public static void debug(final String message)
	{
		try{
			final Logger l = Logger.getLogger(getCallerClassName());
			l.log(Level.FINE, message);
		}catch(final Exception e){ System.out.println("Failed to Log message: " + message); }
	}


	public static void trace(final int intendationDelta, final String message){
		if (theInstance != null) theInstance.mFormatter.addIndentation(intendationDelta);
		trace(message);
	}
	public static void trace(final String message, final int intendationDelta){
		trace(message);
		if (theInstance != null) theInstance.mFormatter.addIndentation(intendationDelta);
	}
	public static void trace(final String message)
	{
		try{
			final Logger l = Logger.getLogger(getCallerClassName());
			l.log(Level.FINER, message);
		}catch(final Exception e){ System.out.println("Failed to Log message: " + message); }
	}
}
