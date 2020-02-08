package functions;

import java.awt.Color;
import java.util.ArrayList;

import complexNumbers.Complex;

public class VectorField2D {

	/*
	 * final variables
	 */
	private final String VECTORFIELD_EQUATION; // equation of this vectorfield, may be complex
	private final double GRIDLINE_DISTANCE; // the distance in the coordinatesystem of two adjecent gridlines
	private final double ROUND_FACTOR; // used to know how many digits to round
	/*
	 * other variables
	 */
	private ArrayList<Function2D> horizontalFunctions2D; // stores the horizontal functions that build this vectorfield
	private ArrayList<Function2D> verticalFunctions2D; // stores the vertical functions that build this vectorfield
	private ArrayList<ArrayList<Complex>> vectorFieldInputPointsHorizontal; // stores the complex input values for the
																			// already calculated
	// points
	private ArrayList<ArrayList<Complex>> vectorFieldInputPointsVertical;
	private ArrayList<ArrayList<Complex>> vectorFieldOutputPointsHorizontal; // stores the complex output values for the
																				// already calculated
	// points
	private ArrayList<ArrayList<Complex>> vectorFieldOutputPointsVertical;
	private Color vectorFieldColor = null; // if this vectorfield shoud not have a dynamic color, this is the color
	private double[] inputValueRange; // inputvalue range for this vectorfield

	private boolean isVisible = true;

	/*
	 * CONSTRUCTORS
	 */

	public VectorField2D(String vectorFieldEquation, double gridlineDistance, double[] inputValueRange, Color color) {

		// initialize final variables
		VECTORFIELD_EQUATION = vectorFieldEquation.replaceAll("x", String.valueOf("z")).replaceAll("y",
				String.valueOf("z"));
		GRIDLINE_DISTANCE = gridlineDistance;
		ROUND_FACTOR = 100;

		// initialize other variables
		horizontalFunctions2D = new ArrayList<>();
		verticalFunctions2D = new ArrayList<>();
		vectorFieldInputPointsHorizontal = new ArrayList<>();
		vectorFieldInputPointsVertical = new ArrayList<>();
		vectorFieldOutputPointsHorizontal = new ArrayList<>();
		vectorFieldOutputPointsVertical = new ArrayList<>();
		this.inputValueRange = new double[4];
		this.vectorFieldColor = color;

		setRange(inputValueRange);

	}

	/*
	 * MAIN METHOD
	 */

	/*
	 * OTHER METHODS
	 */

	private void adjust2DFunctions() {

		System.out.println("===== adjust2DFunctions() =====");

		// adjust vertical functions
		int functionIndex = 0;
		for (double realValue = inputValueRange[0]; realValue <= inputValueRange[1]; realValue += GRIDLINE_DISTANCE) {

			realValue = round(realValue);

//			System.out.println("realValue = " + realValue);

			while (functionIndex < verticalFunctions2D.size()
					&& verticalFunctions2D.get(functionIndex).getOutputValue(0).getRe() < realValue) {
				functionIndex++;
			}

//			if (functionIndex != verticalFunctions2D.size())
//				System.out.println(
//						"if: " + verticalFunctions2D.get(functionIndex).getOutputValue(0).getRe() + " == " + realValue);

			if (verticalFunctions2D.size() != functionIndex
					&& verticalFunctions2D.get(functionIndex).getOutputValue(0).getRe() == realValue) {
				verticalFunctions2D.get(functionIndex)
						.setRange(new double[] { inputValueRange[2], inputValueRange[3] });
			} else {
				// add this function at index functionIndex
				verticalFunctions2D.add(functionIndex,
						new Function2D(Double.toString(realValue), 'y',
								new double[] { inputValueRange[2], inputValueRange[3] }, vectorFieldColor,
								VECTORFIELD_EQUATION));

//				System.out.println("added a new function2D with realValue = " + realValue);
			}
		}

		// adjust horizontal functions
		functionIndex = 0;
		for (double imagValue = inputValueRange[2]; imagValue <= inputValueRange[3]; imagValue += GRIDLINE_DISTANCE) {

			imagValue = round(imagValue);

//			System.out.println("imagValue = " + imagValue);

			while (functionIndex < horizontalFunctions2D.size()
					&& horizontalFunctions2D.get(functionIndex).getOutputValue(0).getIm() < imagValue) {
				functionIndex++;
			}

			if (horizontalFunctions2D.size() != functionIndex
					&& horizontalFunctions2D.get(functionIndex).getOutputValue(0).getIm() == imagValue) {
				horizontalFunctions2D.get(functionIndex)
						.setRange(new double[] { inputValueRange[0], inputValueRange[1] });

//				System.out.println("setting new range for function with imagValue = " + imagValue);
			} else {
				// add this function at index functionIndex
				horizontalFunctions2D.add(functionIndex,
						new Function2D(Double.toString(imagValue), 'x',
								new double[] { inputValueRange[0], inputValueRange[1] }, vectorFieldColor,
								VECTORFIELD_EQUATION));

//				System.out.println("added a new function2D with imagValue = " + imagValue);
			}
		}
	}

	/*
	 * GETTERS
	 */

	public ArrayList<ArrayList<Complex>> getPoints(double maxDistance) {

		ArrayList<ArrayList<Complex>> result = new ArrayList<>();
		vectorFieldInputPointsVertical = new ArrayList<>();
		vectorFieldInputPointsHorizontal = new ArrayList<>();
		vectorFieldOutputPointsVertical = new ArrayList<>();
		vectorFieldInputPointsHorizontal = new ArrayList<>();

		ArrayList<ArrayList<Complex>> currentFunctionPoints = new ArrayList<>();

		// get points of the functions
		for (Function2D hf : horizontalFunctions2D) {

			if (isInInputRange(hf)) {
				currentFunctionPoints = hf.getInputAndOutputPoints(maxDistance);
				result.add(currentFunctionPoints.get(1));
			}
		}
//		System.out.println("added " + result.size() + " horizontal functions");

		for (Function2D vf : verticalFunctions2D) {

			if (isInInputRange(vf)) {
				currentFunctionPoints = vf.getInputAndOutputPoints(maxDistance);
				result.add(currentFunctionPoints.get(1));
			}
		}

		return result;
	}

	private boolean isInInputRange(Function2D function) {

		double[] functionRange = function.getRange();

		switch (function.getFunctionVariable()) {
		case 'x':
			return functionRange[0] == inputValueRange[0] && functionRange[1] == inputValueRange[1]
					&& function.getOutputValue(0).getIm() >= inputValueRange[2]
					&& function.getOutputValue(0).getIm() <= inputValueRange[3];
		case 'y':
			return functionRange[0] == inputValueRange[2] && functionRange[1] == inputValueRange[3]
					&& function.getOutputValue(0).getRe() >= inputValueRange[0]
					&& function.getOutputValue(0).getRe() <= inputValueRange[1];
		}

		System.err.println("did not enter a switch-case; VectorField2D.isInInputRange()");
		return false;
	}

	/*
	 * SETTERS
	 */

	public void setRange(double[] newRange) {

		if (newRange.length < 4) {
			System.err
					.println("new inputvaluerange for the 2d vectorfield has not enough values, did not change range: "
							+ newRange);
			return;
		}

		// correct range if necessary
		if (newRange[0] > newRange[1]) {
			double tmp = newRange[0];
			newRange[0] = newRange[1];
			newRange[1] = tmp;
		}
		if (newRange[2] > newRange[3]) {
			double tmp = newRange[2];
			newRange[2] = newRange[3];
			newRange[3] = tmp;
		}

		// find range so that point c = 0 + 0i is on the vectorfieldgrid
		double minRealValue = 0, maxRealValue = 0, minImagValue = 0, maxImagValue = 0;
		int sign = (int) Math.signum(newRange[0]);
		while ((minRealValue + sign * GRIDLINE_DISTANCE) * (-1) * sign > (newRange[0] - 0.001) * (-1) * sign) {
			minRealValue += sign * GRIDLINE_DISTANCE;
			minRealValue = round(minRealValue);
		}
		sign = (int) Math.signum(newRange[1]);
		while ((maxRealValue + sign * GRIDLINE_DISTANCE) * sign < (newRange[1] + 0.001) * sign) {
			maxRealValue += sign * GRIDLINE_DISTANCE;
			maxRealValue = round(maxRealValue);
		}
		sign = (int) Math.signum(newRange[2]);
		while ((minImagValue + sign * GRIDLINE_DISTANCE) * (-1) * sign > (newRange[2] - 0.001) * (-1) * sign) {
			minImagValue += sign * GRIDLINE_DISTANCE;
			minImagValue = round(minImagValue);
		}
		sign = (int) Math.signum(newRange[3]);
		while ((maxImagValue + sign * GRIDLINE_DISTANCE) * sign < (newRange[3] + 0.001) * sign) {
			maxImagValue += sign * GRIDLINE_DISTANCE;
			maxImagValue = round(maxImagValue);
		}

		inputValueRange[0] = minRealValue;
		inputValueRange[1] = maxRealValue;
		inputValueRange[2] = minImagValue;
		inputValueRange[3] = maxImagValue;

		System.out.println("newrange of the vectorfield: " + inputValueRange);

		// add the missing functions and change range of the grid functions
		adjust2DFunctions();

	}

	private double round(double value) {
		return Math.round(ROUND_FACTOR * value) / ROUND_FACTOR;
	}

	/**
	 * @param newColor the color for this vectorfield. set this to {@code null}, to
	 *                 have a dynamic color
	 */
	public void setColor(Color newColor) {
		vectorFieldColor = newColor;
	}

	public Color getColor() {
		return vectorFieldColor;
	}

	public boolean isVisible() {

		return isVisible;
	}

	public double[] getRange() {

		return inputValueRange;
	}

	public String getVectorFieldEquation() {

		return VECTORFIELD_EQUATION;
	}

	public void setVisible(boolean value) {

		isVisible = value;
	}

}
