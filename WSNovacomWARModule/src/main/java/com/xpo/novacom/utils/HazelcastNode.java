package com.xpo.novacom.utils;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class HazelcastNode {

	// -------------------SECTION SINGLETON----------------------------

	private HazelcastInstance cache = null;

	// Pattern Singleton Thread Safe and Lazy
	private HazelcastNode() {
		new ClasspathXmlConfig("webnovacom-hazelcast.xml");
		this.cache = Hazelcast.newHazelcastInstance();

	}

	public static HazelcastNode getInstance() {
		return Holder.instance;
	}

	private static class Holder {
		static final HazelcastNode instance = new HazelcastNode();
	}

	// ----------------------------------------------------------------
	public HazelcastInstance getCache() {
		return this.cache;
	}

	public void setCache(final HazelcastInstance cache) {
		this.cache = cache;
	}

}
