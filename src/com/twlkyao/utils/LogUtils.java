package com.twlkyao.utils;

/** 
 * @author wangli
 * @editor Shiyao Qi
 * @date 2014.2.19
 * @email qishiyao2008@126.com
 */

public final class LogUtils {
	
	private boolean logEnabled; // Indicate whether to enable to log.
	
	private String tag; // The Log tag.
	
	/**
	 * Constructor
	 * @param LogEnabled
	 * @param Tag
	 */
	public LogUtils(boolean logEnabled, String tag){
		this.logEnabled = logEnabled;
		this.tag = tag;
	}
	
	private final String TAG_CONTENT_PRINT = "%s:%s.%s:%d"; // The format of log.
	
	private static StackTraceElement getCurrentStackTraceElement() {
		return Thread.currentThread().getStackTrace()[4];
	}
	
	/**
	 * Print log.
	 */
	public void trace() {
		if (logEnabled) {
			android.util.Log.d(tag,
					getContent(getCurrentStackTraceElement()));
		}
	}
	
	/**
	 * Get log.
	 * @param trace
	 * @return
	 */
	private String getContent(StackTraceElement trace) {
		return String.format(TAG_CONTENT_PRINT, tag,
				trace.getClassName(), trace.getMethodName(),
				trace.getLineNumber());
	}
	
	/**
	 * Print default log.
	 */
	public void traceStack() {
		if (logEnabled) {
			traceStack(tag, android.util.Log.ERROR);
		}
	}
	
	/**
	 * Print the stack call info.
	 * @param tag
	 * @param priority
	 */
	public void traceStack(String tag, int priority) {

		if (logEnabled) {
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			android.util.Log.println(priority, tag, stackTrace[4].toString());
			StringBuilder str = new StringBuilder();
			String prevClass = null;
			for (int i = 5; i < stackTrace.length; i++) {
				String className = stackTrace[i].getFileName();
				int idx = className.indexOf(".java");
				if (idx >= 0) {
					className = className.substring(0, idx);
				}
				if (prevClass == null || !prevClass.equals(className)) {
					str.append(className.substring(0, idx));  
				}
				prevClass = className;
				str.append(".").append(stackTrace[i].getMethodName())
				.append(":").append(stackTrace[i].getLineNumber())
				.append("->");
			}
			android.util.Log.println(priority, tag, str.toString());
		}
	}

	/**
	 * Set tag and message.
	 * @param tag
	 * @param msg
	 */
	public void d(String tag, String msg) {
		if (logEnabled) {
			android.util.Log.d(tag, getContent(getCurrentStackTraceElement()) + ">" + msg);
		}
	}
	/**
	 * Use default tag and set message.
	 * @param msg
	 */
	public void d(String msg) {
		if (logEnabled) {
			android.util.Log.d(tag, getContent(getCurrentStackTraceElement()) + ">" + msg);
		}
	}

	/**
	 * Set tag and message.
	 * @param tag
	 * @param msg
	 */
	public void i(String tag,String msg){
		if (logEnabled) {
			android.util.Log.i(tag, getContent(getCurrentStackTraceElement()) + ">" + msg);
		}
	}
	
	/**
	 * Use default tag and set message.
	 * @param tag
	 */
	public void i(String msg) {
		if(logEnabled) {
			android.util.Log.i(tag, getContent(getCurrentStackTraceElement()) + ">" + msg);
		}
	}
	
	/**
	 * Set tag and message.
	 * @param tag
	 * @param msg
	 */
	public void v(String tag,String msg){
		if (logEnabled) {
			android.util.Log.v(tag, getContent(getCurrentStackTraceElement()) + ">" + msg);
		}
	}
	
	/**
	 * Use default tag and set message.
	 * @param tag
	 */
	public void v(String msg) {
		if(logEnabled) {
			android.util.Log.v(tag, getContent(getCurrentStackTraceElement()) + ">" + msg);
		}
	}
	
	/**
	 * Set tag and message.
	 * @param tag
	 * @param msg
	 */
	public void e(String tag,String msg){
		if (logEnabled) {
			android.util.Log.e(tag, getContent(getCurrentStackTraceElement()) + ">" + msg);
		}
	}
	
	/**
	 * Use default tag and set message.
	 * @param tag
	 */
	public void e(String msg) {
		if(logEnabled) {
			android.util.Log.e(tag, getContent(getCurrentStackTraceElement()) + ">" + msg);
		}
	}
	
	/**
	 * Set tag and message.
	 * @param tag
	 * @param msg
	 */
	public void w(String tag,String msg){
		if (logEnabled) {
			android.util.Log.w(tag, getContent(getCurrentStackTraceElement()) + ">" + msg);
		}
	}
	
	/**
	 * Use default tag and set message.
	 * @param tag
	 */
	public void w(String msg) {
		if(logEnabled) {
			android.util.Log.w(tag, getContent(getCurrentStackTraceElement()) + ">" + msg);
		}
	}
}