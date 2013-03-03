package com.gnb.model;

import com.google.gson.annotations.SerializedName;

public class Hit {
	@SerializedName("AUTEUR_ID")
	public int idAut;
	@SerializedName("TRACK_ID")
	public int id;
	@SerializedName("TRACK_TITLE")
	public String title;
	@SerializedName("TRACK_DURATION")
	public String duration;
	@SerializedName("TRACK_URL")
	public String url;
	@SerializedName("TRACK_INFO")
	public String info;
	@SerializedName("TRACK_IMG_URL")
	public String img;
	public Hit(int idAut, int id, String title, String duration, String url,
			String info, String img) {
		super();
		this.idAut = idAut;
		this.id = id;
		this.title = title;
		this.duration = duration;
		this.url = url;
		this.info = info;
		this.img = img;
	}
	public int getIdAut() {
		return idAut;
	}
	public void setIdAut(int idAut) {
		this.idAut = idAut;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
}
