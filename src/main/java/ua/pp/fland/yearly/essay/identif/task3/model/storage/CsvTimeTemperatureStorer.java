package ua.pp.fland.yearly.essay.identif.task3.model.storage;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Maxim Bondarenko
 * @version 1.0 10/22/11
 */

public class CsvTimeTemperatureStorer implements TimeTemperatureStorer {
    private static final Logger log = LoggerFactory.getLogger(CsvTimeTemperatureStorer.class);

    private final CSVWriter writer;

    public CsvTimeTemperatureStorer(String outputFilePath) throws IOException {
        this(outputFilePath, '\t', '\"');
    }

    public CsvTimeTemperatureStorer(String outputFilePath, char separator, char quoteCharacter) throws IOException {
        writer = new CSVWriter(new FileWriter(outputFilePath), separator, quoteCharacter);
    }

    @Override
    public void store(Map<Double, Map<BigDecimal, Double>> calculatedTemp) throws IOException {
        log.debug("Forming data to store");
        Map<Double, List<Double>> formattedData = new HashMap<Double, List<Double>>(getFormattedData(calculatedTemp));
        log.debug("Formatted data got. Storing...");
        writer.writeAll(formWriteableList(formattedData,
                new ArrayList<BigDecimal>(calculatedTemp.get(calculatedTemp.keySet().iterator().next()).keySet())));

        writer.close();
    }

    private List<String[]> formWriteableList(Map<Double, List<Double>> formattedData, List<BigDecimal> xPoses) {
        List<Double> timeStamps = new ArrayList<Double>(formattedData.keySet());
        Collections.sort(timeStamps);
        List<String[]> res = new ArrayList<String[]>();

        Collections.sort(xPoses);
        String[] xPosesData = new String[1];
        xPosesData[0] = "Time, sec/Position";
        for (BigDecimal currPos : xPoses) {
            xPosesData = (String[]) ArrayUtils.add(xPosesData, currPos.toPlainString());
        }
        res.add(xPosesData);

        for (double timeStamp : timeStamps) {
            String[] data = new String[1];
            data[0] = String.valueOf(timeStamp);

            for (double value : formattedData.get(timeStamp)) {
                data = (String[]) ArrayUtils.add(data, String.valueOf(value));
            }

            res.add(data);
        }

        return res;
    }

    private Map<Double, List<Double>> getFormattedData(Map<Double, Map<BigDecimal, Double>> calculatedTemp) {
        Map<Double, List<Double>> formattedData = new HashMap<Double, List<Double>>();

        List<Double> timeStamps = new ArrayList<Double>(calculatedTemp.keySet());
        Collections.sort(timeStamps);
        Iterator<Double> timeStampsIterator = timeStamps.iterator();

        List<BigDecimal> xPosValues = new ArrayList<BigDecimal>(calculatedTemp.get(calculatedTemp.keySet().iterator().next()).keySet());
        Collections.sort(xPosValues);
        Iterator<BigDecimal> xPosValuesIterator = xPosValues.iterator();

        while (timeStampsIterator.hasNext()) {
            double currTimeStamp = timeStampsIterator.next();
            List<Double> values = new ArrayList<Double>();
            for (BigDecimal currXPos : xPosValues) {
                values.add(calculatedTemp.get(currTimeStamp).get(currXPos));
            }
            formattedData.put(currTimeStamp, values);
        }
        log.debug("Reached end of timeTemperature data");

        return formattedData;
    }
}
