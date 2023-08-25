/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vmt.model;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author QUANG-PC
 */
public class SafeChildrenResponse
{
	@SerializedName("code")
	public String code;
	@SerializedName("desc")
	public String desc;
        

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getDesc()
	{
		return desc;
	}

	public void setDesc(String desc)
	{
		this.desc = desc;
	}

}
