package br.com.studies.helpers;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.logging.Logger;

public class FuelHelper {

    private static final Logger LOGGER = Logger.getLogger("FuelHelper");

    private FuelHelper() {}

    public static void calculateFuel() {

        while (true) {
            LOGGER.info("Inicio do programa de calculo de combustivel.");

            String raceLength = getMaskedUserInput("Insira a duração da corrida (ex.: 20m): ", "##min", "00m");
            String lapTime = getMaskedUserInput("Insira o tempo de volta (ex.: 01:42.724): ", "##:##.###", "00:00.000");
            String fuelConsumptionPerLap = getMaskedUserInput("Insira o consumo por volta (ex.: 2,7): ", null, "0,0");

            LOGGER.info("Inicio da conversao para milisegundos.");
            int convertedLapTime = convertMinutesToMiliSeconds(lapTime);
            int convertedRaceLength = convertMinutesToMiliSeconds(raceLength) + convertedLapTime;
            BigDecimal convertedFuelConsuption = convertFuelConsuption(fuelConsumptionPerLap);
            LOGGER.info("Fim da conversao para milisegundos.");

            LOGGER.info("Inicio da verificacao de erros.");
            verifyErrors(convertedLapTime, "Tempo de volta inválido.");
            verifyErrors(convertedRaceLength, "Duração da corrida inválida.");
            verifyErrors(convertedFuelConsuption.intValue(), "Volume de combustível inválido.");
            LOGGER.info("Fim da verificacao de erros.");

            if (convertedLapTime != 0 && convertedRaceLength != 0 && convertedFuelConsuption.intValue() != 0) {
                LOGGER.info("Inicio do calculo de combustivel.");

                BigDecimal totalLaps = BigDecimal.valueOf(convertedRaceLength / convertedLapTime);

                BigDecimal minimumFuel = totalLaps.multiply(convertedFuelConsuption);
                BigDecimal recommendedFuel = minimumFuel.add(convertedFuelConsuption.multiply(new BigDecimal("1.2")));
                BigDecimal formationLapFuel = recommendedFuel.add(convertedFuelConsuption.multiply(new BigDecimal("1.5")));

                showMessage("Total de voltas aproximado: " + totalLaps.setScale(0, RoundingMode.UP) + " \n" +
                        "Combustível recomendado: " + recommendedFuel.setScale(1, RoundingMode.UP) + "L \n" +
                        "Combustível em caso de volta de formação completa: " + formationLapFuel.setScale(1, RoundingMode.UP) + "L      \n" +
                        "Combustível mínimo: " + minimumFuel.setScale(1, RoundingMode.UP) + "L");

                LOGGER.info("Fim do calculo de combustivel.");
            }

            int option = showConfirmation();

            if (option != JOptionPane.YES_OPTION) {
                LOGGER.info("Fim do programa de calculo de combustivel.");
                break;
            }

        }

    }

    private static int convertMinutesToMiliSeconds(String time) {
        if (!isTimeEmpty(time)) {
            if (time.contains(":") && time.contains(".")) {
                String[] times = time.split("[:\\.]");

                int min = Integer.parseInt(times[0]);
                int sec = Integer.parseInt(times[1]);
                int ms = Integer.parseInt(times[2]);

                return min * 60 * 1000 + sec * 1000 + ms;
            }

            if (time.contains("min")) {
                int min = Integer.parseInt(time.replace("min", ""));

                return min * 60 * 1000;
            }
        }
        return 0;
    }

    private static boolean isTimeEmpty(String time) {
        if (time == null) {
            return true;
        }

        if (time.contains(":") && time.contains(".")) {
            String replacedTime = time.replace(":", "").replace(".", "");

            return replacedTime.isEmpty() || replacedTime.isBlank();
        }

        if (time.contains("min")) {
            String replacedTime = time.replace("min", "");

            return replacedTime.isEmpty() || replacedTime.isBlank();
        }

        return time.isEmpty() || time.isBlank();
    }

    private static BigDecimal convertFuelConsuption(String fuel) {
        if (fuel != null) {
            String decimalSeparatorReplaced = fuel.replace(",", ".");

            try {
                return new BigDecimal(decimalSeparatorReplaced);
            } catch (Exception e) {
                return new BigDecimal(0);
            }

        }

        return new BigDecimal(0);
    }

    static String getMaskedUserInput(String message, String mask, String labelValue) {
        try {
            MaskFormatter maskFormatter;
            JFormattedTextField formattedTextField = new JFormattedTextField();
            if (mask != null) {
                maskFormatter = new MaskFormatter(mask);
                formattedTextField = new JFormattedTextField(maskFormatter);
            }
            formattedTextField.setValue(labelValue);

            int option = JOptionPane.showOptionDialog(
                    new JFrame(),
                    formattedTextField,
                    message,
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    null,
                    null
            );

            return (option == JOptionPane.OK_OPTION) ? formattedTextField.getText() : null;
        } catch (ParseException e) {
            LOGGER.warning("Erro ao converter valores");
            return null;
        }
    }

    private static void verifyErrors(int value, String message) {
        if (value == 0) {
            showError(message);
        }
    }

    static void showMessage(String message) {
        LOGGER.info(message);
        JOptionPane.showMessageDialog(null, message, "Cálculo realizado", JOptionPane.INFORMATION_MESSAGE);
    }

    static void showError(String message) {
        LOGGER.warning(message);
        JOptionPane.showMessageDialog(null, message, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    static int showConfirmation() {
        return JOptionPane.showConfirmDialog(null, "Deseja realizar outro cálculo?", "ACC cálculo de combustível", JOptionPane.YES_NO_OPTION);
    }

}
