package com.vijay.mirrorapp.entities.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response{

	@SerializedName("data")
	@Expose
	private Data data;

	@SerializedName("message")
	@Expose
	private String message;

	@SerializedName("error_short_code")
	@Expose
	private String error_short_code;

    @SerializedName("errors")
    @Expose
    private Error errors;

    public Error getErrors() {
        return errors;
    }

    public void setErrors(Error errors) {
        this.errors = errors;
    }

    public void setData(Data data){
		this.data = data;
	}

	public Data getData(){
		return data;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setError_short_code(String error_short_code){
		this.error_short_code = error_short_code;
	}

	public String getError_short_code(){
		return error_short_code;
	}


	@Override
 	public String toString(){
		return 
			"Response{" + 
			"data = '" + data + '\'' + 
			",message = '" + message + '\'' + 
			"}";
		}
}