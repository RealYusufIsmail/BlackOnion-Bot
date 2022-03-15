package com.github.black0nion.blackonionbot.blackobjects;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class BlackArrayList<V> extends ArrayList<V> {
	public BlackArrayList<V> addAndGetSelf(final V value) {
		super.add(value);
		return this;
	}
}