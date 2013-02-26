package com.gnb.parser;

import java.util.ArrayList;

import android.util.Log;

import com.gnb.WS.WsResponseHits;
import com.gnb.model.Hit;
import com.gnb.model.Track;
import com.google.gson.Gson;

public class HitsParser {
	Gson gson = new Gson();
	WsResponseHits wsRespHits;

	public HitsParser(String resultat) {
		// TODO Auto-generated constructor stub
		Log.i("DjsParser", "DjsParser");
		wsRespHits = gson.fromJson(resultat, WsResponseHits.class);
	}

	public boolean parse() {
		// TODO Auto-generated method stub
		if (wsRespHits != null) {
			Log.i("parse is true", "TracksParser");
			return true;
		} else {
			Log.i("parse is false", "TracksParser");
			return false;
		}
	}

	public ArrayList<Hit> getObjects() {
		// TODO Auto-generated method stub
		ArrayList<Hit> Hits = new ArrayList<Hit>();
		for (Track track : wsRespHits.tracks) {
			Hits.add(track.hit);
		}
		return Hits;
	}
}
