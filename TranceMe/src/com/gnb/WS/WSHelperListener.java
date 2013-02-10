package com.gnb.WS;

import java.util.List;

import com.gnb.model.Dj;

public interface WSHelperListener {

	void onDjsLoaded(List<Dj> djs);
	
	void onErrorLoadingDj();
}
