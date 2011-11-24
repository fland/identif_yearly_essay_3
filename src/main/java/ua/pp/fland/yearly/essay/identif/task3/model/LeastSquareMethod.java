package ua.pp.fland.yearly.essay.identif.task3.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Maxim Bondarenko
 * @version 1.0 11/24/11
 */

public class LeastSquareMethod {

    private static final Logger log = LoggerFactory.getLogger(LeastSquareMethod.class);

    private final int startTemp;

    private final int environmentTemp;

    private final float wallThickness;

    private final float heatIrradiationCoeff;

    public LeastSquareMethod(int startTemp, int environmentTemp, float wallThickness, float heatIrradiationCoeff) {
        this.startTemp = startTemp;
        this.environmentTemp = environmentTemp;
        this.wallThickness = wallThickness;
        this.heatIrradiationCoeff = heatIrradiationCoeff;
    }

    public void calculate(){

    }
}
