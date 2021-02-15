package org.yeastrc.limelight.xml.comettpp.utils;

import org.yeastrc.limelight.xml.comettpp.constants.CometConstants;
import org.yeastrc.limelight.xml.comettpp.objects.TPPPSM;

import java.math.BigDecimal;

public class MassUtils {

    /**
     * Get the calculated m/z for a precursor for a psm that is (neutral mass + charge * hydrogen mass) / charge
     *
     * @param psm
     * @return
     */
    public static BigDecimal getObservedMoverZForPsm(TPPPSM psm) {

        final double charge = psm.getCharge();
        final double neutralMass = psm.getPrecursorNeutralMass().doubleValue();
        final double observedMoverZ = (CometConstants.COMET_MASS_HYDROGEN_MONO.doubleValue() * charge + neutralMass) / charge;

        return BigDecimal.valueOf(observedMoverZ);
    }

}
