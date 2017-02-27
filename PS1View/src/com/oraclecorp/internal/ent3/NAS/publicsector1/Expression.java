package com.oraclecorp.internal.ent3.NAS.publicsector1;

import oracle.adfmf.framework.api.AdfmfJavaUtilities;

public class Expression extends Number {
	Object value;
	
	/** get Expression Language value using #{expr} notation */
	public Expression(String elExpression) {
		value = (String) AdfmfJavaUtilities.getELValue(elExpression);
	}
	
	@Override
	public String toString() {
		return value.toString();
	}

	@Override
	public double doubleValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float floatValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int intValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long longValue() {
		// TODO Auto-generated method stub
		return 0;
	}

}
