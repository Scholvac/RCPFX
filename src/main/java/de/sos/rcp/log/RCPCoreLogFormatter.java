package de.sos.rcp.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class RCPCoreLogFormatter extends Formatter {

	private static final String QUALIFIER = "de.sos.rcp.log";
	private static final String NODE = "RCPLogFormater";

	private boolean mShowLevel = true;
	private boolean mShowThread = false;
	private boolean mShowSourceFile = false;
	private boolean mShowSourceMethod = false;
	private boolean mShowTimestamp = true;
	private boolean mShowLoggerName = true;
	private String mTimeStampFormat = "HH:mm:ss:SSS/dd-MM-yyyy";
	private final SimpleDateFormat mTimestampFormatter;
	private int mIndentation = 0;

	public RCPCoreLogFormatter()
	{
//		mShowLevel = PreferencesTool.getBoolean(QUALIFIER, NODE, "showLevel", true);
//		mShowLoggerName = PreferencesTool.getBoolean(QUALIFIER, NODE, "showLoggerName", mShowLoggerName);
//		mShowThread = PreferencesTool.getBoolean(QUALIFIER, NODE, "showThread", false);
//		mShowSourceFile = PreferencesTool.getBoolean(QUALIFIER, NODE, "showSourceFile", false);
//		mShowSourceMethod = PreferencesTool.getBoolean(QUALIFIER, NODE, "showSourceMethod", false);
//		mShowTimestamp = PreferencesTool.getBoolean(QUALIFIER, NODE, "showTimestamp", true);
//		mTimeStampFormat = PreferencesTool.getString(QUALIFIER, NODE, "timestampFormat", mTimeStampFormat);
		mTimestampFormatter = new SimpleDateFormat(mTimeStampFormat);
	}

	@Override
	public String format(final LogRecord record) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mIndentation; i++)
			sb.append("\t");
		if (mShowLoggerName){
			sb.append(record.getLoggerName());
			sb.append(" : ");
		}
		if (mShowTimestamp){
			sb.append(mTimestampFormatter.format(new Date(record.getMillis())));
			sb.append("- ");
		}
		if (mShowThread){
			sb.append("Thread: ");
			sb.append(record.getThreadID());
			sb.append(" - ");
		}
		if (mShowLevel){
			sb.append("[");
			sb.append(record.getLevel().toString());
			sb.append("] ");
		}
		sb.append(record.getMessage());
		if (mShowSourceFile){
			sb.append(" at: ");
			sb.append(record.getSourceClassName());
			sb.append(" ");
		}
		if (mShowSourceMethod){
			if (mShowSourceFile == false)
				sb.append(" at: " );
			sb.append(record.getSourceMethodName());
			sb.append(" ");
		}

		return sb.toString();
	}

	public void addIndentation(final int delta) {
		mIndentation += delta;
	}

	public int getIntendation() {
		return mIndentation;
	}
	public void setIndentation(final int i){
		mIndentation = i;
	}

}
