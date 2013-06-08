package tk.sweetvvck.application;

import tk.sweetvvck.utils.Constant;
import android.app.Application;

public class ListenLecture extends Application {

	private String username = "";
	private boolean loginFlag = false;
	private boolean logoutFlag = false;
	private int skinFlag = Constant.BLUE;
	private boolean isFirstUse = false;
	private boolean isFromSet = false;

	public boolean isFromSet() {
		return isFromSet;
	}

	public void setFromSet(boolean isFromSet) {
		this.isFromSet = isFromSet;
	}

	public int getSkinFlag() {		
		return skinFlag;
	}

	public boolean isFirstUse() {
		return isFirstUse;
	}

	public void setFirstUse(boolean isFirstUse) {
		this.isFirstUse = isFirstUse;
	}

	public void setSkinFlag(int skinFlag) {
		this.skinFlag = skinFlag;
	}

	public boolean isLogoutFlag() {
		return logoutFlag;
	}

	public void setLogoutFlag(boolean logoutFlag) {
		this.logoutFlag = logoutFlag;
	}

	public boolean getLoginFlag() {

		return loginFlag;

	}

	public void setLoginFlag(boolean loginFlag) {

		this.loginFlag = loginFlag;

	}

	public String getUserName() {

		return username;

	}

	public void setUserName(String username) {

		this.username = username;

	}
}