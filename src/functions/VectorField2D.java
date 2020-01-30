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

		// adjust vertical functions
		int functionIndex = 0;
		for (double realValue = inputValueRange[0]; realValue <= inputValueRange[1]; realValue += GRIDLINE_DISTANCE) {

			while (functionIndex < verticalFunctions2D.size()
					&& verticalFunctions2D.get(functionIndex).getOutputValue(0).getRe() < realValue) {
				functionIndex++;
			}
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
			}
		}

		// adjust horizontal functions
		functionIndex = 0;
		for (double imagValue = inputValueRange[2]; imagValue <= inputValueRange[3]; imagValue += GRIDLINE_DISTANCE) {

			while (functionIndex < horizontalFunctions2D.size()
					&& horizontalFunctions2D.get(functionIndex).getOutputValue(0).getRe() < imagValue) {
				functionIndex++;
			}
			if (horizontalFunctions2D.size() != functionIndex
					&& horizontalFunctions2D.get(functionIndex).getOutputValue(0).getRe() == imagValue) {
				horizontalFunctions2D.get(functionIndex)
						.setRange(new double[] { inputValueRange[0], inputValueRange[1] });
			} else {
				// add this function at index functionIndex
				horizontalFunctions2D.add(functionIndex,
						new Function2D(Double.toString(imagValue), 'x',
								new double[] { inputValueRange[0], inputValueRange[1] }, vectorFieldColor,
								VECTORFIELD_EQUATION));
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
			currentFunctionPoints = hf.getInputAndOutputPoints(maxDistance);
			result.add(currentFunctionPoints.get(1));
		}

		for (Function2D vf : verticalFunctions2D) {
			currentFunctionPoints = vf.getInputAndOutputPoints(maxDistance);
			result.add(currentFunctionPoints.get(1));
		}

		return result;
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
		}
		sign = (int) Math.signum(newRange[1]);
		while ((maxRealValue + sign * GRIDLINE_DISTANCE) * sign < (newRange[1] + 0.001) * sign) {
			maxRealValue += sign * GRIDLINE_DISTANCE;
		}
		sign = (int) Math.signum(newRange[2]);
		while ((minImagValue + sign * GRIDLINE_DISTANCE) * (-1) * sign > (newRange[2] - 0.001) * (-1) * sign) {
			minImagValue += sign * GRIDLINE_DISTANCE;
		}
		sign = (int) Math.signum(newRange[3]);
		while ((maxImagValue + sign * GRIDLINE_DISTANCE) * sign < (newRange[3] + 0.001) * sign) {
			maxImagValue += sign * GRIDLINE_DISTANCE;
		}

		inputValueRange[0] = minRealValue;
		inputValueRange[1] = maxRealValue;
		inputValueRange[2] = minImagValue;
		inputValueRange[3] = maxImagValue;

		System.out.println("newrange of the vectorfield: " + inputValueRange);

		// add the missing functions and change range of the grid functions
		adjust2DFunctions();

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
