package com.yaya.mobile.me.bean;

import java.io.Serializable;

public class UserBean implements Serializable {
	private static final long serialVersionUID = 1L;
	public static UserBean mUserBean;

	public static UserBean getUSerBean() {

		if (mUserBean == null) {
			mUserBean = new UserBean();
		}
		return mUserBean;

	}

	private String userName;
	private String password;
	private String avater;
	private String mobile;
	private int orderCount;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAvater() {
		return avater;
	}

	public void setAvater(String avater) {
		this.avater = avater;
	}

	public int getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}

	public static void setUserBean(UserBean userBean) {
		mUserBean = userBean;
	}

}
