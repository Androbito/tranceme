package com.gnb.WS;

import java.util.List;

import com.gnb.model.Auteur;
import com.google.gson.annotations.SerializedName;

public class WsResponseDjs {
	@SerializedName("auteurs")
	public List<Auteur> auteurs;
}
