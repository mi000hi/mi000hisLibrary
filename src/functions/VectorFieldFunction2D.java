package functions;

import java.awt.Color;
import java.util.ArrayList;

import complexNumbers.Complex;
import complexNumbers.ComplexCalculator;

public class VectorFieldFunction2D {

	/*
	 * final variables
	 */
	private final double NOT_INITIALIZED = 12345; // marks a double as not initialized, like a null reference
	private final String VECTORFIELD_EQUATION; // equation of this vectorfield, may be complex
	private final String INPUTFUNCTION_EQUATION; // equation of the function used as input in the vectorfield

	/*
	 * other variables
	 */
	private Function2D inputfunction; // function used as input in the vectorfield
	private ArrayList<Complex> functionInputPoints; // stores the complex input values for the
													// already calculated points
	private ArrayList<Complex> functionOutputPoints; // stores the complex output values for the already
														// calculated points
	private Color functionColor = null; // if this vectorfield shoud not have a dynamic color, this is the color
	private double[] inputfunctionRange; // range of the inputfunction in this vectorfield

	/*
	 * CONSTRUCTORS
	 */
	public VectorFieldFunction2D(String vectorfieldEquation, String inputfunctionEquation, char inputfunctionVariable,
			double[] inputfunctionRange, Color functionColor) {

		// initialize final variables
		VECTORFIELD_EQUATION = vectorfieldEquation.replaceAll("x", String.valueOf("z")).replaceAll("y",
				String.valueOf("z"));
		INPUTFUNCTION_EQUATION = inputfunctionEquation.replaceAll("x", String.valueOf(inputfunctionVariable))
				.replaceAll("y", String.valueOf(inputfunctionVariable))
				.replaceAll("z", String.valueOf(inputfunctionVariable));

		// initialize other variables
		inputfunction = new Function2D(inputfunctionEquation, inputfunctionVariable, inputfunctionRange, functionColor);
		this.functionColor = functionColor;

	}

	/*
	 * MAIN METHOD
	 */

	/*
	 * OTHER METHODS
	 */

	public boolean emptyInputFunctionRange() {
		return inputfunction.emptyRange();
	}

	public boolean isVisible() {
		return inputfunction.isVisible();
	}

	/*
	 * GETTERS
	 */

	public ArrayList<ArrayList<Complex>> getPoints(double maxDistance) {

		ArrayList<ArrayList<Complex>> result = new ArrayList<>();
		Complex lastInputPoint, currentInputPoint = null;
		Complex lastOutputPoint, currentOutputPoint = null;
		boolean addedALastPoint = false;

		result.add(new ArrayList<Complex>());
		result.add(new ArrayList<Complex>());

		functionInputPoints = inputfunction.getPoints(0.1);

		lastInputPoint = functionInputPoints.get(0);
		lastOutputPoint = ComplexCalculator.calculate(lastInputPoint, VECTORFIELD_EQUATION);

		for (int i = 0; i < functionInputPoints.size() || (i <= functionInputPoints.size() && addedALastPoint); i++) {

			if (!addedALastPoint) {
				if (!(currentInputPoint == null || currentOutputPoint == null
						|| currentInputPoint.getRe() == NOT_INITIALIZED || currentInputPoint.getIm() == NOT_INITIALIZED
						|| currentOutputPoint.getRe() == NOT_INITIALIZED
						|| currentOutputPoint.getIm() == NOT_INITIALIZED)) {
					lastInputPoint = currentInputPoint;
					lastOutputPoint = currentOutputPoint;
				}
			} else {
				addedALastPoint = false;
				i--;
			}

			currentInputPoint = functionInputPoints.get(i);
			currentOutputPoint = ComplexCalculator.calculate(currentInputPoint, VECTORFIELD_EQUATION);

			if (result.get(0).size() == 0) {
				result.get(0).add(currentInputPoint);
				result.get(1).add(currentOutputPoint);
				continue;
			}

			if (currentOutputPoint.distanceTo(result.get(1).get(result.get(1).size() - 1)) > maxDistance) {
				if (result.get(1).get(result.get(1).size() - 1) == lastOutputPoint) {

					// interpolating functionpoints until we are below the maxDistance
					while (currentOutputPoint.distanceTo(result.get(1).get(result.get(1).size() - 1)) > maxDistance
							&& !(currentInputPoint.getRe() == NOT_INITIALIZED
									|| currentInputPoint.getIm() == NOT_INITIALIZED
									|| currentOutputPoint.getRe() == NOT_INITIALIZED
									|| currentOutputPoint.getIm() == NOT_INITIALIZED || currentInputPoint == null
									|| currentOutputPoint == null)) {
						addFunctionInputPoint(i - 1);
						currentInputPoint = functionInputPoints.get(i);
						currentOutputPoint = ComplexCalculator.calculate(currentInputPoint, VECTORFIELD_EQUATION);
					}

					if (currentInputPoint == null || currentOutputPoint == null
							|| currentInputPoint.getRe() == NOT_INITIALIZED
							|| currentInputPoint.getIm() == NOT_INITIALIZED
							|| currentOutputPoint.getRe() == NOT_INITIALIZED
							|| currentOutputPoint.getIm() == NOT_INITIALIZED) {
						i++;
						if (i < functionInputPoints.size()) {
							lastInputPoint = currentInputPoint;
							lastOutputPoint = currentOutputPoint;
							currentInputPoint = functionInputPoints.get(i);
							currentOutputPoint = ComplexCalculator.calculate(currentInputPoint, VECTORFIELD_EQUATION);
						}
					}
				} else {
					result.get(0).add(lastInputPoint);
					result.get(1).add(lastOutputPoint);
					addedALastPoint = true;
				}
			}
		}

		return result;
	}

	private void addFunctionInputPoint(int previousIndex) {

		Complex inputValue = inputfunction.interpolateFunctionPoint(functionInputPoints.get(previousIndex),
				functionInputPoints.get(previousIndex + 1));

		Complex outputValue;
		if (!(inputValue.getRe() == NOT_INITIALIZED || inputValue.getIm() == NOT_INITIALIZED)) {
			outputValue = ComplexCalculator.calculate(inputValue, VECTORFIELD_EQUATION);
		} else {
			outputValue = null;
		}

		functionInputPoints.add(previousIndex + 1, outputValue);
	}

//	public ArrayList<ArrayList<Complex>> getPoints(double maxDistance) {
//
//		ArrayList<ArrayList<Complex>> result = new ArrayList<>();
//		functionInputPoints = inputfunction.getPoints(2);
//		functionOutputPoints = new ArrayList<>();
//		Complex currentInputPoint, lastInputPoint;
//		Complex currentOutputPoint, lastOutputPoint;
//		boolean pointsInterpolated = false;
//
//		// calculate first value
//		lastInputPoint = functionInputPoints.get(0);
//		lastOutputPoint = ComplexCalculator.calculate(lastInputPoint, VECTORFIELD_EQUATION);
//		functionOutputPoints.add(lastOutputPoint);
//
//		// calculate outputpoints for these inputpoints and interpolate if necessary
//		for (int inputpointIndex = 1; inputpointIndex < functionInputPoints.size(); inputpointIndex++) {
//
//			pointsInterpolated = false;
//
//			// calculate and add next value
//			currentInputPoint = functionInputPoints.get(inputpointIndex);
//			currentOutputPoint = ComplexCalculator.calculate(currentInputPoint, VECTORFIELD_EQUATION);
//			functionOutputPoints.add(inputpointIndex, currentOutputPoint);
//
//			// interpolate functioninputpoints
//			while (lastOutputPoint.distanceTo(currentOutputPoint) > maxDistance) {
//				currentInputPoint = inputfunction.interpolateFunctionPoint(lastInputPoint, currentInputPoint);
//				functionInputPoints.add(inputpointIndex, currentInputPoint);
//				currentOutputPoint = ComplexCalculator.calculate(currentInputPoint, VECTORFIELD_EQUATION);
////				functionOutputPoints.add(inputpointIndex, currentOutputPoint);
//				pointsInterpolated = true;
//			}
//
//			if (pointsInterpolated) {
//				inputpointIndex--;
//			}
//			lastInputPoint = functionInputPoints.get(inputpointIndex);
//			lastOutputPoint = functionOutputPoints.get(inputpointIndex);
//		}
//
//		// return the result
//		result.add(functionInputPoints);
//		result.add(functionOutputPoints);
//		return result;
//	}

	public Color getColor() {
		return functionColor;
	}

	/*
	 * SETTERS
	 */
}
