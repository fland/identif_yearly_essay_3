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
 * @version 1.0 11/24/11
 */

public class ImplicitFiniteDifferenceMethod {

    private static final Logger log = LoggerFactory.getLogger(ImplicitFiniteDifferenceMethod.class);

    private final double startTemp;

    private final double environmentTemp;

    private final double wallThickness;

    private final double heatIrradiationCoeff;

    private final double xStep;

    private final double timeStep;

    private final double endTime;

    private final static int X_STEP_SCALE = 4;

    private final static double lambda = 1;

    private final double Bi;

    private final long n;

    public ImplicitFiniteDifferenceMethod(double startTemp, double environmentTemp, double wallThickness,
                                          double heatIrradiationCoeff, double xStep) {
        this.startTemp = startTemp;
        this.environmentTemp = environmentTemp;
        this.wallThickness = wallThickness;
        this.heatIrradiationCoeff = heatIrradiationCoeff;

        this.xStep = xStep;

        timeStep = 0.005;
        endTime = 0.5;

        Bi = wallThickness * heatIrradiationCoeff / lambda;
        n = Math.round(wallThickness / xStep);
    }

    public Map<Double, Map<BigDecimal, Double>> calculate() {
        Map<Double, Map<BigDecimal, Double>> calculatedTemp = new HashMap<Double, Map<BigDecimal, Double>>();
        Map<BigDecimal, Double> calculatedXTemp = new HashMap<BigDecimal, Double>(getStartTimeTemp(startTemp));
        double currTime = 0.0;
        calculatedTemp.put(currTime, calculatedXTemp);
        currTime = currTime + timeStep;

        final double s = (xStep * xStep) / timeStep;

        for (; currTime < endTime; currTime = currTime + timeStep) {
            calculatedXTemp = new HashMap<BigDecimal, Double>(getNextTimeTemp(calculatedXTemp, s));
            calculatedTemp.put(currTime, calculatedXTemp);
        }

        return calculatedTemp;
    }

    private Map<BigDecimal, Double> getNextTimeTemp(Map<BigDecimal, Double> prevTimeTemp, final double s) {
        Map<BigDecimal, Double> calculatedXTemp = new HashMap<BigDecimal, Double>();

        Map<Long, DirectFlowData> directFlowDataMap = new HashMap<Long, DirectFlowData>();
        double a = -1;
        double b = 1;
        double d = 0;
        double c = 1;
        directFlowDataMap.put(0L, new DirectFlowData(c, b, d, -a / b, -d / b));

        double currX = 0.0 + xStep;
        long i;
        for (i = 1; i < n; i++) {
            BigDecimal currXBigDecimal = new BigDecimal(currX).setScale(X_STEP_SCALE, RoundingMode.HALF_UP);
            a = 1;
            b = -(2 + s);
            d = s * prevTimeTemp.get(currXBigDecimal);
            double prevF = directFlowDataMap.get(i - 1).getF();
            double prevG = directFlowDataMap.get(i - 1).getG();

            double f = -a / (c * prevF + b);
            double g = -(d + c * prevG) / (c * prevF + b);
            directFlowDataMap.put(i, new DirectFlowData(c, b, d, f, g));
            currX = currX + xStep;
        }
//        i = i + 1;
        b = 1;
        d = 0;
        directFlowDataMap.put(i, new DirectFlowData(c, b, d, 1, 1));

        currX = wallThickness;
        BigDecimal currXBigDecimal = new BigDecimal(currX).setScale(X_STEP_SCALE, RoundingMode.HALF_UP);
        c = directFlowDataMap.get(i).getC();
        b = directFlowDataMap.get(i).getB();
        d = directFlowDataMap.get(i).getD();
        double g = directFlowDataMap.get(i - 1).getG();
        double f = directFlowDataMap.get(i - 1).getF();
        double prevTemp = -(c * g + d) / (b + c * f);
        calculatedXTemp.put(currXBigDecimal, prevTemp);
        currX = currX - xStep;
        for (; i > 0; i--) {
            currXBigDecimal = new BigDecimal(currX).setScale(X_STEP_SCALE, RoundingMode.HALF_UP);
            f = directFlowDataMap.get(i).getF();
            g = directFlowDataMap.get(i).getG();
            double currTemp = f * prevTemp + g;
            calculatedXTemp.put(currXBigDecimal, currTemp);
            prevTemp = currTemp;
            currX = currX - xStep;
        }

        return calculatedXTemp;
    }

    private Map<BigDecimal, Double> getStartTimeTemp(double startTemp) {
        Map<BigDecimal, Double> startXTemp = new HashMap<BigDecimal, Double>();

        double currXPos = 0;
        for (; currXPos <= wallThickness; currXPos = currXPos + xStep) {
            BigDecimal bigDecimalXPos = new BigDecimal(currXPos).setScale(X_STEP_SCALE, RoundingMode.HALF_UP);
            startXTemp.put(bigDecimalXPos, startTemp);
        }

        return startXTemp;
    }
}
