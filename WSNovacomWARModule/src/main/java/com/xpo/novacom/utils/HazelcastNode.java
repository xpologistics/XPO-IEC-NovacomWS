package com.xpo.novacom.utils;

import java.io.FileNotFoundException;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.FileSystemXmlConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class HazelcastNode {

	// -------------------SECTION SINGLETON----------------------------

	private HazelcastInstance cache = null;

	// Pattern Singleton Thread Safe and Lazy
	private HazelcastNode() {
		//new ClasspathXmlConfig(ConfigurationUtils.HAZELCAST_CONF_URL_FILE);
		try {
			new FileSystemXmlConfig(ConfigurationUtils.HAZELCAST_CONF_URL_FILE);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
