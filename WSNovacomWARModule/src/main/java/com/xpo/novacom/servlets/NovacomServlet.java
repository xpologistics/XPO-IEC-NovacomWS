package com.xpo.novacom.servlets;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.ndi.genericworker.client.GenericWorkerClientIEC;
import com.ndi.iec.business.GpsService;
import com.ndi.iec.dao.sql.impl.ParcUniqueDao;
import com.ndi.iec.pojo.VehiculePU;
import com.xpo.novacom.pojo.AssetData;
import com.xpo.novacom.pojo.Measure;
import com.xpo.novacom.pojo.NovacomData;
import com.xpo.novacom.utils.*;

@Component
public class NovacomServlet extends HttpServlet {
    private Logger log = LoggerFactory.getLogger("novacom");;
    private static final String FOURNISSEUR_NOVACOM_LIB = "NOVACOM";
    private static final String FOURNISSEUR_NOVACOM_LIB_COURT = "NOVA";

    @Autowired
    private ParcUniqueDao parcUniqueDao;

    @Autowired
    GpsService gpsService;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                config.getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        //System.out.println(httpServletRequest.getReader());

        try {
            String fluxRecu = IOUtils.toString(httpServletRequest.getReader());
            log.debug(fluxRecu);
            NovacomData novacomData = parse(new StringReader(fluxRecu), NovacomData.class);


            for ( AssetData assetData : novacomData.getAssetData()) {
            	log.debug(assetData.toString());
                this.createGPSMessage(assetData);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }

        httpServletResponse.getOutputStream().println("OK");
    }


    private void createGPSMessage(final AssetData assetData) throws InternalException {
        try {

            final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final Date date = formatter.parse(assetData.getDate().getDate() + " " + assetData.getDate().getTime());
            //final Date date = assetData.getDate().getTime();
            // Modification de la Qualite du GPS

            final StringTokenizer st = new StringTokenizer(assetData.getAsset().getValue(), "/");
            String immat = st.nextToken();
            if (immat != null) {
                VehiculePU vehicule = new VehiculePU();
                immat = immat.trim();
                //On recherche dans le cache
                String venumCache = ParcCache.getInstance().getVenum(Utils.toImmatRech(immat));
                if (venumCache != null) {

                    log.debug("Venum trouvé en cache");
                    vehicule.setImmat(immat);
                    vehicule.setVenum(venumCache);
                    vehicule.setVenser(venumCache);
                } else {
                    //Rien dans le cache, on cherche en BDD
                    log.debug("Venum non trouvé en cache, on cherche en base");
                    vehicule = parcUniqueDao.getVehicule(Utils.toImmatRech(immat));
                    log.debug(vehicule.getImmat() + vehicule.getVenum() + vehicule.getVenser());
                    //On met à jour le cache
                    ParcCache.getInstance().setVenum(immat, vehicule.getVenum());
                }


                if (vehicule == null) {
                    this.log.error("Venum nom trouvé pour immat : " + immat);
                    this.log.error("Flux d'entréé : " + assetData.toString());
                } else {


                    Float kilometre = (float)0;
                    double longitude = 0;
                    double latitude = 0;
                    int driver = -1;
                    int coDriver = -1;
                    final String fournisseur = FOURNISSEUR_NOVACOM_LIB;
                    final String idFournisseur = FOURNISSEUR_NOVACOM_LIB;
                    final String infos = "N/A";
                    int gpsQualite = 100;
                    final int evenement = 0;
                    final Double altitude = null;
                    final String etat_Tachygraphe = "U";

                    for (Measure measure : assetData.getMeasure()) {
                        if (measure.getField().equals("DISTANCE")) {
                            kilometre = Float.parseFloat(measure.getValue().replace(",","."));
                        }
                        if (measure.getField().equals("Position")) {
                            String value = measure.getValue();
                            StringTokenizer stLocation = new StringTokenizer(value, " ");
                            String tempLatitude = stLocation.nextToken();
                            latitude = Double.parseDouble(tempLatitude.substring(0, tempLatitude.length() - 1));
                            String tempLongitude = stLocation.nextToken();
                            longitude = Double.parseDouble(tempLongitude.substring(0, tempLongitude.length() - 1));
                        }

                    }

                    this.log.debug("Appel Generic Worker GPS");
                    GenericWorkerClientIEC.getInstance().publishGPSMessage(vehicule.getVenum(), date, longitude, latitude, fournisseur,
                            idFournisseur, infos, gpsQualite, driver, coDriver, kilometre, evenement,altitude, etat_Tachygraphe,
                            this.log);

                    this.log.debug("Fin appel Generic Worker GPS");

                    //Si ce n'est pas encore un equipement embarqué IEC on demande un ajout
                    log.debug("Vérification dans si immat dans IEC");
                    gpsService.verifImmatDansIEC(vehicule, FOURNISSEUR_NOVACOM_LIB_COURT, FOURNISSEUR_NOVACOM_LIB);
                }


            }




        } catch (final Exception e) {
            throw new InternalException("Cannot create GPS Message on Generic Worker", e);
        }
    }
    private <T> T parse(Reader url, Class<T> clazz) throws JAXBException {
        Unmarshaller unmarshaller = JAXBContext.newInstance(clazz).createUnmarshaller();
        return clazz.cast(unmarshaller.unmarshal(url));
    }


    public void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws IOException {
        httpServletResponse.getWriter().print("XPO Services Ready");
    }
}
