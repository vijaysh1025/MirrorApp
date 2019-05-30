package com.vijay.mirrorapp.datastore.entities.user;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Profile{

	@SerializedName("birthdate")
	@Expose
	private Object birthdate;

	@SerializedName("workout_duration")
	@Expose
	private Object workoutDuration;

	@SerializedName("gender")
	@Expose
	private Object gender;

	@SerializedName("hr_max")
	@Expose
	private int hrMax;

	@SerializedName("hr_mins")
	@Expose
	private HrMins hrMins;

	@SerializedName("weight")
	@Expose
	private int weight;

	@SerializedName("hr_rest")
	@Expose
	private int hrRest;

	@SerializedName("display_name")
	private String displayName;

	@SerializedName("share_selfies")
	@Expose
	private boolean shareSelfies;

	@SerializedName("recovery_bpm")
	@Expose
	private int recoveryBpm;

	@SerializedName("variant_id")
	@Expose
	private Object variantId;

	@SerializedName("skill")
	@Expose
	private Object skill;

	@SerializedName("location")
	@Expose
	private Object location;

	@SerializedName("hr_zones")
	@Expose
	private HrZones hrZones;

	@SerializedName("workout_freq")
	@Expose
	private Object workoutFreq;

	@SerializedName("height")
	@Expose
	private int height;

	public void setBirthdate(Object birthdate){
		this.birthdate = birthdate;
	}

	public Object getBirthdate(){
		return birthdate;
	}

	public void setWorkoutDuration(Object workoutDuration){
		this.workoutDuration = workoutDuration;
	}

	public Object getWorkoutDuration(){
		return workoutDuration;
	}

	public void setGender(Object gender){
		this.gender = gender;
	}

	public Object getGender(){
		return gender;
	}

	public void setHrMax(int hrMax){
		this.hrMax = hrMax;
	}

	public int getHrMax(){
		return hrMax;
	}

	public void setHrMins(HrMins hrMins){
		this.hrMins = hrMins;
	}

	public HrMins getHrMins(){
		return hrMins;
	}

	public void setWeight(int weight){
		this.weight = weight;
	}

	public int getWeight(){
		return weight;
	}

	public void setHrRest(int hrRest){
		this.hrRest = hrRest;
	}

	public int getHrRest(){
		return hrRest;
	}

	public void setDisplayName(String displayName){
		this.displayName = displayName;
	}

	public String getDisplayName(){
		return displayName;
	}

	public void setShareSelfies(boolean shareSelfies){
		this.shareSelfies = shareSelfies;
	}

	public boolean isShareSelfies(){
		return shareSelfies;
	}

	public void setRecoveryBpm(int recoveryBpm){
		this.recoveryBpm = recoveryBpm;
	}

	public int getRecoveryBpm(){
		return recoveryBpm;
	}

	public void setVariantId(Object variantId){
		this.variantId = variantId;
	}

	public Object getVariantId(){
		return variantId;
	}

	public void setSkill(Object skill){
		this.skill = skill;
	}

	public Object getSkill(){
		return skill;
	}

	public void setLocation(Object location){
		this.location = location;
	}

	public Object getLocation(){
		return location;
	}

	public void setHrZones(HrZones hrZones){
		this.hrZones = hrZones;
	}

	public HrZones getHrZones(){
		return hrZones;
	}

	public void setWorkoutFreq(Object workoutFreq){
		this.workoutFreq = workoutFreq;
	}

	public Object getWorkoutFreq(){
		return workoutFreq;
	}

	public void setHeight(int height){
		this.height = height;
	}

	public int getHeight(){
		return height;
	}

	@Override
 	public String toString(){
		return 
			"Profile{" + 
			"birthdate = '" + birthdate + '\'' + 
			",workout_duration = '" + workoutDuration + '\'' + 
			",gender = '" + gender + '\'' + 
			",hr_max = '" + hrMax + '\'' + 
			",hr_mins = '" + hrMins + '\'' + 
			",weight = '" + weight + '\'' + 
			",hr_rest = '" + hrRest + '\'' + 
			",display_name = '" + displayName + '\'' + 
			",share_selfies = '" + shareSelfies + '\'' + 
			",recovery_bpm = '" + recoveryBpm + '\'' + 
			",variant_id = '" + variantId + '\'' + 
			",skill = '" + skill + '\'' + 
			",location = '" + location + '\'' + 
			",hr_zones = '" + hrZones + '\'' + 
			",workout_freq = '" + workoutFreq + '\'' + 
			",height = '" + height + '\'' + 
			"}";
		}
}