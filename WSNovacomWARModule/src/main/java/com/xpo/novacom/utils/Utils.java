package com.xpo.novacom.utils;

public class Utils {
    /**
     * To immat rech.
     *
     * @param immat
     *            the immat
     * @return une immatriculation sans caracteres speciaux
     */
    public static String toImmatRech(final String immat) {
        if (immat == null) {
            return null;
        }
        String immatRech = immat.toUpperCase();
        immatRech = com.ndi.tools.StringUtils.replaceOccurenceString(immatRech, " ", "");
        immatRech = com.ndi.tools.StringUtils.replaceOccurenceString(immatRech, ".", "");
        immatRech = com.ndi.tools.StringUtils.replaceOccurenceString(immatRech, "-", "");
        immatRech = com.ndi.tools.StringUtils.replaceOccurenceString(immatRech, "/", "");
        immatRech = com.ndi.tools.StringUtils.replaceOccurenceString(immatRech, "\\", "");
        return immatRech;
    }
}
