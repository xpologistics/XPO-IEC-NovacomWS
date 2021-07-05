package com.xpo.novacom.utils;

import com.ndi.iec.dbcomm.utils.DBUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;


public class ParcCache {

	private Logger log = LoggerFactory.getLogger("novacom");;
	private Map<String, String> mapVehicule = null;
	// -------------------SECTION SINGLETON----------------------------

	// Pattern Singleton Thread Safe and Lazy
	private ParcCache() {
		this.mapVehicule = HazelcastNode.getInstance().getCache().getMap("novacom-vehicule");
	}

	public static ParcCache getInstance() {
		return Holder.instance;
	}

	private static class Holder {
		static final ParcCache instance = new ParcCache();
	}

	// ----------------------------------------------------------------

	/**
	 * This method return the Parc Unique's venum for the vin passed in parameter Return null if this vin is unknowed in Parc Unique
	 *
	 * @param cgimat
	 * @return
	 * @throws InternalException
	 */
	public String getVenum(final String cgimat) throws InternalException {
		String result = null;
		// first, we call the cache
		log.debug("Call cache to retrieve venum | venum[" + cgimat + "]");
		result = this.mapVehicule.get(cgimat);

		log.debug("End Call cache to retrieve venum | venum[" + cgimat + "], : " + result);
		return result;
	}

	public synchronized void setVenum(String cgimat, String venum) {
			this.mapVehicule.put(cgimat, venum);
	}


}
