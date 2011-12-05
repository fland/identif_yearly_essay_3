package ua.pp.fland.yearly.essay.identif.task3.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.pp.fland.yearly.essay.identif.task3.model.beans.DirectFlowData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Maxim Bondarenko
 * @version 1.0 12/4/11
 */

public class ExplicitFiniteDifferenceMethod extends FiniteDifferenceMethod {

    private static final Logger log = LoggerFactory.getLogger(ImplicitFiniteDifferenceMethod.class);

    private final double startTemp;

    private final double environmentTemp;

    private final double wallThickness;

    private final double heatIrradiationCoeff;

    private final double xStep;

    private final double timeStep;

    private final double endTime;

    private final double lambda;

    private final double a;

    private final long n;

    public ExplicitFiniteDifferenceMethod(double startTemp, double environmentTemp, double wallThickness,
                                          double heatIrradiationCoeff, double xStep, double a, double lambda,
                                          double timeStep, double endTime) {
        this.startTemp = startTemp;
        this.environmentTemp = environmentTemp;
        this.wallThickness = wallThickness;
        this.heatIrradiationCoeff = heatIrradiationCoeff;

        this.xStep = xStep;
        this.a = a;
        this.lambda = lambda;

        this.timeStep = timeStep;
        this.endTime = endTime;

        n = Math.round(wallThickness / xStep);
    }

    @Override
    public Map<Double, Map<BigDecimal, Double>> calculate() {
        log.debug("Calculating started...");

        Map<Double, Map<BigDecimal, Double>> calculatedTemp = new HashMap<Double, Map<BigDecimal, Double>>();
        Map<BigDecimal, Double> calculatedXTemp = new HashMap<BigDecimal, Double>(getStartTimeTemp(startTemp, wallThickness, xStep));
        double currTime = 0.0;
        calculatedTemp.put(currTime, calculatedXTemp);
        currTime = currTime + timeStep;

        for (; currTime <= endTime; currTime = currTime + timeStep) {
            calculatedXTemp = new HashMap<BigDecimal, Double>(getNextTimeTemp(calculatedXTemp));
            calculatedTemp.put(currTime, calculatedXTemp);
        }

        log.debug("Calculating finished.");

        return calculatedTemp;
    }

    private Map<BigDecimal, Double> getNextTimeTemp(Map<BigDecimal, Double> prevTimeTemp) {
        Map<BigDecimal, Double> calculatedXTemp = new HashMap<BigDecimal, Double>();

        double currX = 0.0 + xStep;
        long i;
        for (i = 1; i < n; i++) {
            BigDecimal currXBigDecimal = new BigDecimal(currX).setScale(X_STEP_SCALE, RoundingMode.HALF_UP);
            BigDecimal prevXBigDecimal = new BigDecimal(currX - xStep).setScale(X_STEP_SCALE, RoundingMode.HALF_UP);
            BigDecimal nextXBigDecimal = new BigDecimal(currX + xStep).setScale(X_STEP_SCALE, RoundingMode.HALF_UP);

            double temperature = prevTimeTemp.get(prevXBigDecimal) * this.a * timeStep / (xStep * xStep) +
                    prevTimeTemp.get(currXBigDecimal) * (1 - 2 * this.a * timeStep / (xStep * xStep)) +
                    prevTimeTemp.get(nextXBigDecimal) * timeStep *this.a / (xStep * xStep);

            calculatedXTemp.put(currXBigDecimal, temperature);
            currX = currX + xStep;
        }

        currX = 0.0;
        BigDecimal currXBigDecimal = new BigDecimal(currX).setScale(X_STEP_SCALE, RoundingMode.HALF_UP);
        BigDecimal nextXBigDecimal = new BigDecimal(currX + xStep).setScale(X_STEP_SCALE, RoundingMode.HALF_UP);
        double a = 1;
        double b = -1;
        double d = 0;
        double temperature = (d - a * calculatedXTemp.get(nextXBigDecimal)) / b;
        calculatedXTemp.put(currXBigDecimal, temperature);

        currX = wallThickness;
        currXBigDecimal = new BigDecimal(currX).setScale(X_STEP_SCALE, RoundingMode.HALF_UP);
        BigDecimal prevXBigDecimal = new BigDecimal(currX - xStep).setScale(X_STEP_SCALE, RoundingMode.HALF_UP);
        double c = -1;
        b = 1 + heatIrradiationCoeff * xStep / lambda;
        d = environmentTemp * heatIrradiationCoeff * xStep / lambda;
        temperature = (d - c * calculatedXTemp.get(prevXBigDecimal)) / b;
        calculatedXTemp.put(currXBigDecimal, temperature);

        return calculatedXTemp;
    }
}
