package rikka.materialpreference.util;

import android.widget.EdgeEffect;

class EdgeEffectCompatLollipop {
	public static boolean onPull(Object edgeEffect, float deltaDistance, float displacement) {
		((EdgeEffect) edgeEffect).onPull(deltaDistance, displacement);
		return true;
	}
}