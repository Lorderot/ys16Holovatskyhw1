package ua.yandex.sumofseries.threads;

import java.util.function.DoubleUnaryOperator;

/**
 * @author Mykola Holovatsky
 */
public class SeriesSummator {
    private DoubleUnaryOperator function;
    private Double step = 0.0001;

    public SeriesSummator(DoubleUnaryOperator function) {
        this.function = function;
    }

    public Double sum(Double lowerBound, Double upperBound) {
        Double x = lowerBound;
        Double sum = 0.0;
        while (x + step < upperBound) {
            sum += (function.applyAsDouble(x + step)
                    + function.applyAsDouble(x)) / 2 * step;
            x += step;
        }
        sum += (function.applyAsDouble(upperBound)
                + function.applyAsDouble(x)) / 2 * (upperBound - x);
        return sum;
    }

    public Double getStep() {
        return step;
    }

    public void setStep(Double step) {
        if (step <= 0) {
            throw new IllegalArgumentException();
        }
        this.step = step;
    }
}
