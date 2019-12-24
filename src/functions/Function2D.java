package functions;

import java.awt.Color;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import complexNumbers.*;

public class Function2D {

	private String functionEquation;
	private char functionVariable;
	private ArrayList<Complex> functionPoints;
	private double[] range;
	private Color functionColor;
	
	private final double NOT_INITIALIZED = 12345;

	public Function2D(String functionEquation, char functionVariable, double[] range, Color functionColor) {

		this.functionEquation = functionEquation;
		this.functionVariable = functionVariable;
		this.functionColor = functionColor;
		this.range = range;

		this.functionPoints = new ArrayList<>();

		// create first entries for this function
		getOutputValue(this.range[0]);
		getOutputValue(this.range[1]);
	}

	// GETTERS //

	public Color getColor() {

		return functionColor;
	}

	public Complex getOutputValue(double inputValue) {

		// test if f(x) is already calculated
		for (Complex c : functionPoints) {
			switch (functionVariable) {
			case 'x':
				if (c.getRe() == inputValue) {
					return c;
				}
				break;
			case 'y':
				if (c.getIm() == inputValue) {
					return c;
				}
				break;
			default:
				System.err.println("invalid functionVariable: " + functionVariable);
			}
		}

		// calculate value
		Complex complexOutput = ComplexCalculator.calculate(new Complex(inputValue, 0), functionEquation);
		if(complexOutput == null) {
			addPointToFunctionPoints(inputValue, NOT_INITIALIZED);
			return null;
		}
		double outputValue = complexOutput.getRe();

		addPointToFunctionPoints(inputValue, outputValue);

		return createComplexNumber(inputValue, outputValue);
	}

	private void addPointToFunctionPoints(double inputValue, double outputValue) {

		// search index of the next smaller inputValue
		int index = functionPoints.size();
		for (int i = 0; i < functionPoints.size(); i++) {
			switch (functionVariable) {
			case 'x':
				if (functionPoints.get(i).getRe() > inputValue) {
					index = i;
				}
				break;
			case 'y':
				if (functionPoints.get(i).getIm() > inputValue) {
					index = i;
				}
				break;
			default:
				System.err.println("invalid functionVariable: " + functionVariable);
			}
		}

		// add output point to function points
		functionPoints.add(index, createComplexNumber(inputValue, outputValue));
	}

	private Complex createComplexNumber(double inputValue, double outputValue) {

		switch (functionVariable) {

		case 'x':
			return new Complex(inputValue, outputValue, true);

		case 'y':
			return new Complex(outputValue, inputValue, true);
		}

		System.err.println("invalid functionVariable: " + functionVariable);
		return null;
	}

	public ArrayList<Complex> getPoints(double maxDistance) {

		ArrayList<Complex> result = new ArrayList<>();
		Complex lastPoint, currentPoint = null;
		boolean rangeStarted = false, addedALastPoint = false;

		lastPoint = functionPoints.get(0);

		for (int i = 0; i < functionPoints.size() || (i <= functionPoints.size() && addedALastPoint); i++) {

//			System.out.println("functionInputPoints.size() = " + functionPoints.size());

			if (!addedALastPoint) {

				if (currentPoint != null) {
					lastPoint = currentPoint;
				}
			} else {

				addedALastPoint = false;
				i--;
			}

			currentPoint = functionPoints.get(i);

			// if its the first point in range, start using the points
			if (!rangeStarted && isPointInRange(currentPoint)) {

//				System.out.println("currentPoint " + currentPoint.toString() + " is in range");
				rangeStarted = true;
			} else {

				if (!rangeStarted) {

					continue;
				}
//				System.out.println("currentPoint " + currentPoint.toString() + " is in range");
			}

			// TODO: null pointer
			// if currentInputPoint is not in the function-input-range, break the loop
			if (!isPointInRange(currentPoint)) {

				continue;
			} else if (result.size() == 0) {

				result.add(currentPoint);
				continue;
			}

			if (currentPoint.subtract(result.get(result.size() - 1)).getRadius() > maxDistance) {

				if (result.get(result.size() - 1) == lastPoint) {

					// interpolate function points until we are below the maxDistance
					while (currentPoint.subtract(result.get(result.size() - 1)).getRadius() > maxDistance && !(currentPoint.getRe() == NOT_INITIALIZED || currentPoint.getIm() == NOT_INITIALIZED)) {

						addFunctionPoint(i - 1);
						currentPoint = functionPoints.get(i);
					}

					if(currentPoint.getRe() == NOT_INITIALIZED || currentPoint.getIm() == NOT_INITIALIZED) {
						i++;
						lastPoint = currentPoint;
						currentPoint = functionPoints.get(i);
					}

				} else {

					result.add(lastPoint);

					addedALastPoint = true;
				}
				// if currentPoint is the last point in range, add it to result and return
			} else if (isLastPointInRange(currentPoint)) {

				result.add(currentPoint);
				return result;
			}
		}

		return result;
	}

	private boolean isLastPointInRange(Complex point) {

		switch (functionVariable) {

		case 'x':
			return point.getRe() == range[1];

		case 'y':
			return point.getIm() == range[1];

		default:
			System.err.println("invalid functionVariable: " + functionVariable);
			return false;
		}
	}

	private boolean isPointInRange(Complex point) {

		switch (functionVariable) {

		case 'x':
			return point.getRe() >= range[0] && point.getRe() <= range[1];

		case 'y':
			return point.getIm() >= range[0] && point.getIm() <= range[1];

		default:
			System.err.println("invalid functionVariable: " + functionVariable);
			return false;
		}
	}

	public void addFunctionPoint(int previousIndex) {

		double inputValue = (getInputValue(previousIndex) + getInputValue(previousIndex + 1)) / 2;
		Complex output = ComplexCalculator.calculate(new Complex(inputValue, 0), functionEquation);

		double outputValue;
		if (output == null) {
			outputValue = NOT_INITIALIZED;
		} else {
			outputValue = output.getRe();
		}
		functionPoints.add(previousIndex + 1, createComplexNumber(inputValue, outputValue));
	}

	private double getInputValue(int index) {

		switch (functionVariable) {

		case 'x':
			return functionPoints.get(index).getRe();

		case 'y':
			return functionPoints.get(index).getIm();

		default:
			System.err.println("invalid functionVariable: " + functionVariable);
			return 0;
		}
	}

	public boolean emptyRange() {

		return range[0] == range[1];
	}
}
