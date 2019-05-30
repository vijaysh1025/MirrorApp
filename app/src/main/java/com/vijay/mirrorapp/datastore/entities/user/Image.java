package com.vijay.mirrorapp.datastore.entities.user;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Image{

	@SerializedName("s3_url")
	@Expose
	private String s3Url;

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("cloudinary_id")
	@Expose
	private Object cloudinaryId;

	@SerializedName("url")
	@Expose
	private String url;

	@SerializedName("cloudinary_version")
	@Expose
	private Object cloudinaryVersion;

	public void setS3Url(String s3Url){
		this.s3Url = s3Url;
	}

	public String getS3Url(){
		return s3Url;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setCloudinaryId(Object cloudinaryId){
		this.cloudinaryId = cloudinaryId;
	}

	public Object getCloudinaryId(){
		return cloudinaryId;
	}

	public void setUrl(String url){
		this.url = url;
	}

	public String getUrl(){
		return url;
	}

	public void setCloudinaryVersion(Object cloudinaryVersion){
		this.cloudinaryVersion = cloudinaryVersion;
	}

	public Object getCloudinaryVersion(){
		return cloudinaryVersion;
	}

	@Override
 	public String toString(){
		return 
			"Image{" + 
			"s3_url = '" + s3Url + '\'' + 
			",name = '" + name + '\'' + 
			",cloudinary_id = '" + cloudinaryId + '\'' + 
			",url = '" + url + '\'' + 
			",cloudinary_version = '" + cloudinaryVersion + '\'' + 
			"}";
		}
}