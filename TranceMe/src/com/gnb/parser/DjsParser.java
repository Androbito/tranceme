package com.gnb.parser;

import java.util.ArrayList;

import android.util.Log;

import com.gnb.WS.WsResponseDjs;
import com.gnb.model.Auteur;
import com.gnb.model.Dj;
import com.google.gson.Gson;

public class DjsParser {
	Gson gson = new Gson();
	WsResponseDjs wsRespDjs;

	public DjsParser(String resultat) {
		// TODO Auto-generated constructor stub
		Log.i("DjsParser", "DjsParser");
		wsRespDjs = gson.fromJson(resultat, WsResponseDjs.class);
	}

	public boolean parse() {
		// TODO Auto-generated method stub
		if (wsRespDjs != null) {
			Log.i("parse is true", "DjsParser");
			return true;
		} else {
			Log.i("parse is false", "DjsParser");
			return false;
		}
	}

	public ArrayList<Dj> getObjects() {
		// TODO Auto-generated method stub
		ArrayList<Dj> Djs = new ArrayList<Dj>();
		for (Auteur auteur : wsRespDjs.auteurs) {
//			Log.i("Dj Name", "" + auteur.dj.name);
			Djs.add(auteur.dj);
		}
		return Djs;
	}
}
