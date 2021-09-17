package com.xpo.novacom.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.websphere.management.AdminService;
import com.ibm.websphere.management.AdminServiceFactory;

/**
 * Environment management class Classe provenant du Framework NDI.
 *
 * @author fleroy
 */

public final class FWKNDIEnvironment {

	/** Runtime environement. */
	public final static Runtime RUNTIME = Runtime.getRuntime();

	/** Nom de la propriete pour le saut de ligne. */
	public final static String LINE_SEPARATOR_SYSTEM_PROPERTY_KEY = "line.separator";

	/** Valeur de la propriete pour le saut de ligne. */
	public final static String LINE_SEPARATOR_SYSTEM_PROPERTY_VALUE = getProperty(FWKNDIEnvironment.LINE_SEPARATOR_SYSTEM_PROPERTY_KEY, null);

	/** Nom de la propriete pour le separateur de path. */
	public final static String PATH_SEPARATOR_SYSTEM_PROPERTY_KEY = "separator";

	/** Valeur de la propriete pour le saut de ligne. */
	public final static String PATH_SEPARATOR_SYSTEM_PROPERTY_VALUE = getProperty(FWKNDIEnvironment.PATH_SEPARATOR_SYSTEM_PROPERTY_KEY, null);

	/** OS dependant shell commands. */
	private static List<String> soOSCommand = new ArrayList<>();

	/** Logger. */
	private final static Logger LOG = LoggerFactory.getLogger(FWKNDIEnvironment.class);

	/** Envrionment variables. */
	private static Properties soEnvVars = null;

	/**
	 * Constructeur par defaut.
	 */
	private FWKNDIEnvironment() {
	}

	/**
	 * Definition d'un constructeur de processus pour une commande.
	 *
	 * @param psCommandLine
	 *            commande a executer
	 * @return le handler sur la commande
	 */
	public static ProcessBuilder getCommandLine(final String psCommandLine) {
		final List<String> loCmd = new ArrayList<>(soOSCommand);
		loCmd.add(psCommandLine);
		return new ProcessBuilder(loCmd);
	}

	/**
	 * Liste des variables d'environnement.
	 *
	 * @return la liste des variables d'environnement
	 */
	public static Properties getVariables() {
		Properties loPres = null;

		if (soEnvVars == null) {
			loPres = new Properties();

			// recupration des variables systemes
			final Map<String, String> varMap = System.getenv();

			if (varMap != null) {
				for (final String key : varMap.keySet()) {
					loPres.setProperty(key, varMap.get(key));

				}
			}

			soEnvVars = loPres;

		} else {
			loPres = soEnvVars;
		}
		return loPres;
	}

	/**
	 * Recuperation d'une variable.
	 *
	 * @param psKey
	 *            nom de la variable
	 * @return la valeur de la variable d'environnement
	 * @throws IllegalStateException
	 *             if the variable is undefined
	 */
	public static String getVariable(final String psKey) throws IllegalStateException {
		final String lsRet = getVariable(psKey, null);
		System.out.println("Variable " + psKey + " = " + lsRet);
		if (lsRet == null) {
			LOG.error("getVariable('" + psKey + "') Variable undefined !");
			System.err.println("getVariable('" + psKey + "') Variable undefined !");
			throw new IllegalStateException("Variable or property '" + psKey + "' undefined ! ");
		}
		return lsRet;
	}

	/**
	 * Recuperation d'une variable depuis l'OS ou depuis WAS.
	 *
	 * @param psKey
	 *            nom de la variable
	 * @param psDefault
	 *            valeur par defaut
	 * @return la valeur de la variable d'environnement
	 */
	public static String getVariable(final String psKey, final String psDefault) {
		final Properties loProp = getVariables();
		String lsVal = null;
		if (loProp != null) {
			// L'OS a ete trouve dans la liste -> on cherche la var
			lsVal = loProp.getProperty(psKey);
		}
		if (lsVal == null) {
			// Si on n'a pas trouve l'OS ou que la cle n'a pas ete trouvee dans les var de l'OS
			// On essai de recuperer la variable sous WAS
			lsVal = expandVariable(psKey);
		}
		return (lsVal == null) ? psDefault : lsVal;
	}

	/**
	 * Recupere la valeur d'une propriete.
	 *
	 * @param psPropertyKey
	 *            Nom de la proprite
	 * @param psDefault
	 *            Valeur par dfaut de la proprit
	 * @return Valeur de la proprite
	 */
	public static String getProperty(final String psPropertyKey, final String psDefault) {
		return System.getProperty(psPropertyKey, psDefault);
	}

	/**
	 * Substitue dans une chaine les proprietes systems.
	 *
	 * @param psPath
	 *            chaine contenant les elements a susbstituer
	 * @return la chaine avec les valeurs subsitues
	 */
	public static String resolveSystemProperties(final String psPath) {
		int lnIdxS, lnIdxE;
		String lsRes, lsPropName, lsPropValue;

		lnIdxS = 0;
		lsRes = psPath;

		while ((lnIdxS = lsRes.indexOf("${", lnIdxS)) >= 0) {
			lnIdxE = lsRes.indexOf("}", lnIdxS);
			if (lnIdxE < 0) {
				lnIdxE = lnIdxS;
			} else {
				lsPropName = lsRes.substring(lnIdxS + 2, lnIdxE);
				lsPropValue = FWKNDIEnvironment.getVariable(lsPropName);
				if (lsPropName != null) {
					lsRes = lsRes.substring(0, lnIdxS) + lsPropValue + lsRes.substring(lnIdxE + 1);
				}
			}
		}
		return lsRes;
	}

	/**
	 * To get Was variable.
	 *
	 * @param psKey
	 *            Name for websphere variable
	 * @return value
	 */
	@SuppressWarnings("rawtypes")
	public static String expandVariable(final String psKey) {
		LOG.trace("Looking for variable " + psKey + " in websphere Variable");
		String lsResult = null;
		try {
			final AdminService loAs = AdminServiceFactory.getAdminService();
			final String lsServer = loAs.getProcessName();
			final Set lcResult = loAs.queryNames(new javax.management.ObjectName("*:*,type=AdminOperations,process=" + lsServer), null);
			lsResult = (String) loAs.invoke((ObjectName) lcResult.iterator().next(), "expandVariable", new Object[] { "${" + psKey + "}" },
					new String[] { "java.lang.String" });
		} catch (final Throwable loE) {
			LOG.error("Error in expandVariable(" + psKey + ") : " + loE);
		}
		LOG.trace("Result for Looking for variable " + psKey + " in websphere Variable : " + lsResult);
		return lsResult;
	}
}
