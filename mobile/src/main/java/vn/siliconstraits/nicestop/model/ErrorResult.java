package vn.siliconstraits.nicestop.model;

import com.google.gson.annotations.SerializedName;

public class ErrorResult extends NetworkResult {

	@SerializedName("error")
	private String	error;

	@SerializedName("error_code")
	private String	error_code;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getError_code() {
		return error_code;
	}

	public void setError_code(String error_code) {
		this.error_code = error_code;
	}
}
