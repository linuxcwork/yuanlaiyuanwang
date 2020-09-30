/*      						
 * Copyright 2010 Beijing Xinwei, Inc. All rights reserved.
 * 
 * History:
 * ------------------------------------------------------------------------------
 * Date    	|  Who  		|  What  
 * 2015��3��30��	| duanbokan 	| 	create the file                       
 */

package com.example.yang.util;

import android.graphics.drawable.Drawable;

/**
 * 
 * ���Ҫ����
 * 
 * <p>
 * ����ϸ����
 * </p>
 * 
 * @author duanbokan
 * 
 */

public class CountryModel
{
	// ��������
	public String countryName;
	
	// ���Ҵ���
	public String countryNumber;
	
	public String simpleCountryNumber;
	
	// ����������д
	public String countrySortKey;
	
	// ����ͼ��
	public Drawable contactPhoto;
	
	public CountryModel(String countryName, String countryNumber, String countrySortKey)
	{
		super();
		this.countryName = countryName;
		this.countryNumber = countryNumber;
		this.countrySortKey = countrySortKey;
		if (countryNumber != null)
		{
			this.simpleCountryNumber = countryNumber.replaceAll("\\-|\\s", "");
		}
	}
	
}
