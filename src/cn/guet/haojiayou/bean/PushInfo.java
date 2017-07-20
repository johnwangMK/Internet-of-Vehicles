package cn.guet.haojiayou.bean;

public class PushInfo {
	public String cID;
	public String userNo;
	public String content;
	public String pushTime;
	public String pushStatus;
	
	public String getcID() {
		return cID;
	}
	
	public void setcID(String cID) {
		this.cID = cID;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPushTime() {
		return pushTime;
	}

	public void setPushTime(String pushTime) {
		this.pushTime = pushTime;
	}

	public String getPushStatus() {
		return pushStatus;
	}

	public void setPushStatus(String pushStatus) {
		this.pushStatus = pushStatus;
	}

	public PushInfo() {
		// TODO Auto-generated constructor stub
	}

	public PushInfo(String cID, String userNo, String content, String pushTime,
			String pushStatus) {
		super();
		this.cID = cID;
		this.userNo = userNo;
		this.content = content;
		this.pushTime = pushTime;
		this.pushStatus = pushStatus;
	}

	@Override
	public String toString() {
		return "PushInfo [cID=" + cID + ", userNo=" + userNo + ", content="
				+ content + ", pushTime=" + pushTime + ", pushStatus="
				+ pushStatus + "]";
	}
	
	
}

