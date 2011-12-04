package ua.pp.fland.yearly.essay.identif.task3.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Maxim Bondarenko
 * @version 1.0 12/4/11
 */

public abstract class FiniteDifferenceMethod {

    private final static double DOUBLE_DELTA = 0.00001;

    protected final static int X_STEP_SCALE = 4;

    /**
     * Calculates temperature of wall in time and x position
     * @return map of pairs [time] = [map of pairs [x position] = [temperature] ]
     */
    public abstract Map<Double, Map<BigDecimal, Double>> calculate();

    protected Map<BigDecimal, Double> getStartTimeTemp(double startTemp, double wallThickness,
                                                       double xStep) {
        Map<BigDecimal, Double> startXTemp = new HashMap<BigDecimal, Double>();

        double currXPos = 0;
        for (; currXPos <= wallThickness + DOUBLE_DELTA; currXPos = currXPos + xStep) {
            BigDecimal bigDecimalXPos = new BigDecimal(currXPos).setScale(X_STEP_SCALE, RoundingMode.HALF_UP);
            startXTemp.put(bigDecimalXPos, startTemp);
        }

        return startXTemp;
    }
}
