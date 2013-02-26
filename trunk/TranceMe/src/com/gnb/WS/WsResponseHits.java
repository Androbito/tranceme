package com.gnb.WS;

import java.util.List;

import com.gnb.model.Track;
import com.google.gson.annotations.SerializedName;

public class WsResponseHits {
	@SerializedName("tracks")
	public List<Track> tracks;
}
