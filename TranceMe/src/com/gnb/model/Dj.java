package com.gnb.model;

import com.google.gson.annotations.SerializedName;

public class Dj {
	@SerializedName("AUTEUR_ID")
	public int id;
	@SerializedName("AUTEUR_NAME")
	public String name;
	@SerializedName("AUTEUR_DESCRIPTION")
	public String description;
	public Dj(int id, String name, String description) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
