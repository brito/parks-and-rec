package com.oraclecorp.internal.ent3.NAS.publicsector1;

import java.util.Date;
import java.util.logging.Level;

import oracle.adfmf.framework.api.AdfmfJavaUtilities;
import oracle.adfmf.framework.exception.AdfException;
import oracle.adfmf.json.JSONException;
import oracle.adfmf.json.JSONObject;
import oracle.adfmf.util.Utility;

public class Console {
//	┬┌┐┌┌─┐┌─┐
//	││││├┤ │ │
//	┴┘└┘└  └─┘
	public static Date info(String message){ return log(Level.INFO, message); }
	public static Date info(JSONObject message){
		try {
			return log(Level.INFO, message.toString(2));
		} catch (JSONException e) {
			warn(e.getMessage());
			error(e);
			return new Date();
		}
	}
//	┬ ┬┌─┐┬─┐┌┐┌
//	│││├─┤├┬┘│││
//	└┴┘┴ ┴┴└─┘└┘
	public static Date warn(String message) { return log(Level.WARNING, message); }
//	┌─┐┬─┐┬─┐┌─┐┬─┐
//	├┤ ├┬┘├┬┘│ │├┬┘
//	└─┘┴└─┴└─└─┘┴└─
	public static Date error(Exception e) {
		e.printStackTrace();
		log(Level.SEVERE, e.getMessage());
		throw new AdfException(e.getMessage(), AdfException.ERROR);
	}
	

	private static final String APPLICATION_SCOPE_STATUS = "#{applicationScope.status}";
	private static Date log(Level level, String message) {		
		// 1. Application console output
		StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
		Utility.ApplicationLogger.logp(level, stackTraceElement.getClassName(),
				stackTraceElement.getMethodName(), message);

		// 2. UI
		AdfmfJavaUtilities.setELValue(APPLICATION_SCOPE_STATUS, message);
		
		// 3. Date
		return new Date();
	} 
}
