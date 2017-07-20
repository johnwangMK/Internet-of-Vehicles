package com.wang.juhe;

public class StationInfo {
	private String resultcode;
	private int error_code;
	private String reason;
	private Result result;
	
	public StationInfo(String resultcode, int error_code, String reason,
			Result result) {
		super();
		this.resultcode = resultcode;
		this.error_code = error_code;
		this.reason = reason;
		this.result = result;
	}
	public String getResultcode() {
		return resultcode;
	}
	public void setResultcode(String resultcode) {
		this.resultcode = resultcode;
	}
	public int getError_code() {
		return error_code;
	}
	public void setError_code(int error_code) {
		this.error_code = error_code;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Result getResult() {
		return result;
	}
	public void setResult(Result result) {
		this.result = result;
	}
	
}
