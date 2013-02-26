package com.gnb.WS;

import java.util.List;

import com.gnb.model.Dj;
import com.gnb.model.Hit;

public interface WSHelperListener {

	void onDjsLoaded(List<Dj> djs);

	void onErrorLoadingDj();

	void onHitsLoaded(List<Hit> hits);

	void onErrorLoadingHit();
}
