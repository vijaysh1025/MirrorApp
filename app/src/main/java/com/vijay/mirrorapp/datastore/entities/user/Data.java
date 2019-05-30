package com.vijay.mirrorapp.datastore.entities.user;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Data{

	@SerializedName("image")
	@Expose
	private Image image;

	@SerializedName("parent")
	@Expose
	private Object parent;

	@SerializedName("is_internal")
	@Expose
	private boolean isInternal;

	@SerializedName("injuries")
	@Expose
	private List<Object> injuries;

	@SerializedName("profile")
	@Expose
	private Profile profile;

	@SerializedName("is_parent")
	@Expose
	private boolean isParent;

	@SerializedName("uuid")
	@Expose
	private String uuid;

	@SerializedName("subscription_active")
	@Expose
	private Object subscriptionActive;

	@SerializedName("is_child")
	@Expose
	private boolean isChild;

	@SerializedName("children")
	@Expose
	private List<Object> children;

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("public_name")
	@Expose
	private String publicName;

	@SerializedName("email")
	@Expose
	private String email;

	@SerializedName("is_pending")
	@Expose
	private boolean isPending;

	@SerializedName("goals")
	@Expose
	private List<Object> goals;

	@SerializedName("user_token")
	@Expose
	private String user_token;

	@SerializedName("api_token")
	@Expose
	private String api_token;

	public String getApi_token() {
		return api_token;
	}

	public void setApi_token(String api_token) {
		this.api_token = api_token;
	}

	public String getUser_token() {
		return user_token;
	}

	public void setUser_token(String user_token) {
		this.user_token = user_token;
	}

	public void setImage(Image image){
		this.image = image;
	}

	public Image getImage(){
		return image;
	}

	public void setParent(Object parent){
		this.parent = parent;
	}

	public Object getParent(){
		return parent;
	}

	public void setIsInternal(boolean isInternal){
		this.isInternal = isInternal;
	}

	public boolean isIsInternal(){
		return isInternal;
	}

	public void setInjuries(List<Object> injuries){
		this.injuries = injuries;
	}

	public List<Object> getInjuries(){
		return injuries;
	}

	public void setProfile(Profile profile){
		this.profile = profile;
	}

	public Profile getProfile(){
		return profile;
	}

	public void setIsParent(boolean isParent){
		this.isParent = isParent;
	}

	public boolean isIsParent(){
		return isParent;
	}

	public void setUuid(String uuid){
		this.uuid = uuid;
	}

	public String getUuid(){
		return uuid;
	}

	public void setSubscriptionActive(Object subscriptionActive){
		this.subscriptionActive = subscriptionActive;
	}

	public Object getSubscriptionActive(){
		return subscriptionActive;
	}

	public void setIsChild(boolean isChild){
		this.isChild = isChild;
	}

	public boolean isIsChild(){
		return isChild;
	}

	public void setChildren(List<Object> children){
		this.children = children;
	}

	public List<Object> getChildren(){
		return children;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setPublicName(String publicName){
		this.publicName = publicName;
	}

	public String getPublicName(){
		return publicName;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}

	public void setIsPending(boolean isPending){
		this.isPending = isPending;
	}

	public boolean isIsPending(){
		return isPending;
	}

	public void setGoals(List<Object> goals){
		this.goals = goals;
	}

	public List<Object> getGoals(){
		return goals;
	}

	@Override
 	public String toString(){
		return 
			"Data{" + 
			"image = '" + image + '\'' + 
			",parent = '" + parent + '\'' + 
			",is_internal = '" + isInternal + '\'' + 
			",injuries = '" + injuries + '\'' + 
			",profile = '" + profile + '\'' + 
			",is_parent = '" + isParent + '\'' + 
			",uuid = '" + uuid + '\'' + 
			",subscription_active = '" + subscriptionActive + '\'' + 
			",is_child = '" + isChild + '\'' + 
			",children = '" + children + '\'' + 
			",name = '" + name + '\'' + 
			",public_name = '" + publicName + '\'' + 
			",email = '" + email + '\'' + 
			",is_pending = '" + isPending + '\'' + 
			",goals = '" + goals + '\'' + 
			"}";
		}
}