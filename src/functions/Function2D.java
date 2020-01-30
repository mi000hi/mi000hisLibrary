package functions;

import java.awt.Color;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import complexNumbers.*;

public class Function2D {

	/*
	 * final variables
	 */
	private final double NOT_INITIALIZED = 12345; // marks a double as not initialized, like a null reference
	private final double DEFAULT_CALCULATION_STEP = 0.1; // default increment of the inputValue when calculating default
															// functionvalues in the given range
	private final String FUNCTION_EQUATION; // string-representation of the function
	private final char FUNCTION_VARIABLE; // dependency variable of the function, x or y (axis)
	private final String VECTORFIELD_EQUATION; // string-representation of the vectorfield affecting the function

	/*
	 * other variables
	 */
	private ArrayList<Complex> functionPoints; // points of the function as complex numbers, containing x and y
												// coordinates
	private ArrayList<Complex> vectorFieldOutputPoints; // points of the function affected by the vectorfield
	private double[] range; // range for the functionvariable, where this function is active (length == 2)
	private Color functionColor; // color of this function if it gets painted
	private boolean isVisible = true; // false if the function should not be painted

	/*
	 * CONSTRUCTORS
	 */

	/**
	 * Constructor of a real 2D function. functionpoints in the given range will be
	 * calculated with distance {@code DEFAULt_CALCULATION_STEP} while in this
	 * constructor. the rest of the points will be interpolated when the function
	 * {@code getPoints()} is called.
	 * 
	 * @param functionEquation string representation of the equation for this
	 *                         function
	 * @param functionVariable dependency variable of this function. x, y and z in
	 *                         {@code functionEquation} will be replaced with this
	 *                         value
	 * @param range            range of the dependency variable. only points in this
	 *                         range will be given back by calling the
	 *                         {@code getPoints()} method
	 * @param functionColor    color of this function, if you print it as a graph
	 */
	public Function2D(String functionEquation, char functionVariable, double[] range, Color functionColor) {

		this.FUNCTION_EQUATION = functionEquation.replaceAll("x", String.valueOf(functionVariable))
				.replaceAll("y", String.valueOf(functionVariable)).replaceAll("z", String.valueOf(functionVariable));
		this.FUNCTION_VARIABLE = functionVariable;
		this.functionColor = functionColor;
		this.functionPoints = new ArrayList<>();
		this.vectorFieldOutputPoints = new ArrayList<>();
		this.VECTORFIELD_EQUATION = "";

		// set range for this function
		setRange(range);
	}

	/**
	 * Constructor of a real 2D function. functionpoints in the given range will be
	 * calculated with distance {@code DEFAULt_CALCULATION_STEP} while in this
	 * constructor. the rest of the points will be interpolated when the function
	 * {@code getPoints()} is called. The function with this constructor will be
	 * affected by the specified vectorfield
	 * 
	 * @param functionEquation string representation of the equation for this
	 *                         function
	 * @param functionVariable dependency variable of this function. x, y and z in
	 *                         {@code functionEquation} will be replaced with this
	 *                         value
	 * @param range            range of the dependency variable. only points in this
	 *                         range will be given back by calling the
	 *                         {@code getPoints()} method
	 * @param functionColor    color of this function, if you print it as a graph
	 * @param vectorField      string representation of the vectorfield that affects
	 *                         this function
	 */
	public Function2D(String functionEquation, char functionVariable, double[] range, Color functionColor,
			String vectorFieldEquation) {

		this.FUNCTION_EQUATION = functionEquation.replaceAll("x", String.valueOf(functionVariable))
				.replaceAll("y", String.valueOf(functionVariable)).replaceAll("z", String.valueOf(functionVariable));
		this.FUNCTION_VARIABLE = functionVariable;
		this.functionColor = functionColor;
		this.functionPoints = new ArrayList<>();
		this.vectorFieldOutputPoints = new ArrayList<>();
		this.VECTORFIELD_EQUATION = vectorFieldEquation;

		// set range for this function
		setRange(range);
	}

	/*
	 * MAIN METHOD
	 */

	/*
	 * OTHER METHODS
	 */

	/**
	 * adds a given point with input and outputvalue to the {@code functionPoints}
	 * in the form of a complex number {@code x+iy}.
	 * 
	 * @param inputValue  inputValue that will lead the function to give us the
	 *                    outputValue
	 * @param outputValue already calculated {@code outputvalue = f(inputValue)}
	 */
	private void addPointToFunctionPoints(double inputValue, double outputValue) {

		// search index of the next smaller inputValue
		int index = getIndex(inputValue);

		// add output point to function points ordered by inputValues from small to big
		Complex inputPoint = createComplexNumber(inputValue, outputValue);
		functionPoints.add(index, inputPoint);
		if (VECTORFIELD_EQUATION != "") {
			vectorFieldOutputPoints.add(index, ComplexCalculator.calculate(inputPoint, VECTORFIELD_EQUATION));
		}
	}

	/**
	 * creates a complex number in the form {@code x+iy} representing the tupel
	 * {@code (inputValue / outputValue)} in a 2d coordinate system, where the
	 * {@code functionVariable} can be either the x or y component.
	 * 
	 * @param inputValue  inputValue that leads the function to give the outputValue
	 * @param outputValue the output of the function by giving it the inputValue
	 * @return a complex number representing input and outputvalue
	 */
	private Complex createComplexNumber(double inputValue, double outputValue) {

		switch (FUNCTION_VARIABLE) {
		case 'x':
			return new Complex(inputValue, outputValue, true);
		case 'y':
			return new Complex(outputValue, inputValue, true);
		}

		System.err.println("invalid functionVariable: " + FUNCTION_VARIABLE);
		return null;
	}

	/**
	 * tests if the given point is in the range, or on the border of the range, of
	 * this function.
	 * 
	 * @param point the point that is tested
	 * @return {@code true} if the given point is in the range or on its border
	 */
	private boolean isPointInRange(Complex point) {

		switch (FUNCTION_VARIABLE) {
		case 'x':
			return point.getRe() >= range[0] && point.getRe() <= range[1];
		case 'y':
			return point.getIm() >= range[0] && point.getIm() <= range[1];
		default:
			System.err.println("invalid functionVariable: " + FUNCTION_VARIABLE);
			return false;
		}
	}

	/**
	 * interpolates a functionpoint after the given index. the inputvalue for this
	 * function outputvalue is the mean of the point at the given index and the
	 * following point
	 * 
	 * @param previousIndex index of the point before the newly inserted
	 *                      functionpoint
	 */
	private void addFunctionPoint(int previousIndex) {

		double inputValue = (getInputValue(previousIndex) + getInputValue(previousIndex + 1)) / 2;
//		inputValue = Math.round(inputValue * Math.pow(10, NUMBER_OF_DECIMAL_INPUTDIGITS)) / Math.pow(10, NUMBER_OF_DECIMAL_INPUTDIGITS);
		Complex output = ComplexCalculator.calculate(new Complex(inputValue, 0), FUNCTION_EQUATION);

		double outputValue;
		if (output == null) {
			outputValue = NOT_INITIALIZED;
		} else {
			outputValue = output.getRe();
		}
		Complex inputPoint = createComplexNumber(inputValue, outputValue);
		functionPoints.add(previousIndex + 1, inputPoint);
		if (VECTORFIELD_EQUATION != "") {
			vectorFieldOutputPoints.add(previousIndex + 1,
					ComplexCalculator.calculate(inputPoint, VECTORFIELD_EQUATION));
		}
	}

	/**
	 * interpolates a functionpoint after the given functionvalue.
	 * 
	 * @param previousOutputPoint the function point before the one to be calculated
	 * @return the new interpolated functionpoint
	 */
	public Complex interpolateFunctionPoint(Complex previousFunctionValue, Complex followingFunctionValue) {

		int previousIndex = 0, followingIndex = 0;
		switch (FUNCTION_VARIABLE) {
		case 'x':
			previousIndex = getIndex(previousFunctionValue.getRe());
			followingIndex = getIndex(followingFunctionValue.getRe());
			break;
		case 'y':
			previousIndex = getIndex(previousFunctionValue.getIm());
			followingIndex = getIndex(followingFunctionValue.getIm());
			break;
		default:
			System.err.println("interpolating functionpoint, default case");
		}

		// retrun already calculated point
		if (previousIndex + 1 < followingIndex) {
			return functionPoints.get((int) ((previousIndex + followingIndex) / 2.0));
		}
		addFunctionPoint(previousIndex);
		return functionPoints.get(previousIndex + 1);
	}

	/**
	 * tests if the range-length of this function is 0
	 * 
	 * @return true if {@code range[0] == range[1]}, {@code false} else
	 */
	public boolean emptyRange() {

		return range[0] == range[1];
	}

	/*
	 * GETTERS
	 */

	/**
	 * @return the equation of this function as a string
	 */
	public String getFunctionEquation() {

		return FUNCTION_EQUATION;
	}

	/**
	 * @return the range of this function
	 */
	public double[] getRange() {

		return range;
	}

	/**
	 * returns the color of this function
	 * 
	 * @return color of this function
	 */
	public Color getColor() {

		return functionColor;
	}

	/**
	 * returns the output function value of this 2d function with the given
	 * {@code inputValue}. if the outputvalue is not yet known, it is calculated and
	 * added to the {@code functionPoints}.
	 * 
	 * @param inputValue value for the dependency variable {@code functionVariable}
	 * @return a complex point representing input and output values as
	 *         {@code x + iy}, where {@code x} or {@code y} is the
	 *         {@code inputValue} and the other one the {@code outputValue}
	 *         dependent of the {@code functionVariable}
	 */
	public Complex getOutputValue(double inputValue) {

		// TODO: should we round the inputValues to have some fixed max accuracy?
//		inputValue = Math.round(inputValue * Math.pow(10, NUMBER_OF_DECIMAL_INPUTDIGITS)) / Math.pow(10, NUMBER_OF_DECIMAL_INPUTDIGITS);

		// test if y = f(x) is already calculated
		for (Complex c : functionPoints) {
			switch (FUNCTION_VARIABLE) {
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
				System.err.println("invalid functionVariable: " + FUNCTION_VARIABLE);
			}
		}

		// value was not already calculated, calculate value now
		Complex complexOutput = ComplexCalculator.calculate(new Complex(inputValue, 0), FUNCTION_EQUATION);
		if (complexOutput == null) {
			addPointToFunctionPoints(inputValue, NOT_INITIALIZED);
			return null;
		}
		double outputValue = complexOutput.getRe();
		addPointToFunctionPoints(inputValue, outputValue);

		return createComplexNumber(inputValue, outputValue);
	}

	/**
	 * returns the inputpoints of this function to the vectorfield of this function
	 * 
	 * @param maxDistance the maximum distance between two calculated points
	 * @return a arraylist containing all the points of this function that we will
	 *         use as inputpoints of the vectorfield
	 */
	private ArrayList<Complex> getVectorFieldInputPoints(double maxDistance) {

		ArrayList<Complex> result = new ArrayList<>(); // saves all the points that we will return
		Complex lastPoint, currentPoint = null; // last watched point and currently watched point from the
												// functionPoints
		boolean rangeStarted = false; // true as soon as our currentFunction point is in the range of this function,
										// what means we need to start returning functionpoints
		boolean addedALastPoint = false; // if a lastpoint was added to the result arraylist, we cannot increment the i
											// in the for loop because we need to check the currentPoint first

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

			if (!isPointInRange(currentPoint)) {
				continue;
			} else if (result.size() == 0) {
				result.add(currentPoint);
				continue;
			}

			if (currentPoint.distanceTo(result.get(result.size() - 1)) > maxDistance) {
				if (result.get(result.size() - 1) == lastPoint) {

					// interpolate function points until we are below the maxDistance
					while (currentPoint.distanceTo(result.get(result.size() - 1)) > maxDistance
							&& !(currentPoint.getRe() == NOT_INITIALIZED || currentPoint.getIm() == NOT_INITIALIZED)) {
//						System.out.println("interpolating functionpoint");
						addFunctionPoint(i - 1);
						currentPoint = functionPoints.get(i);
					}

					if (currentPoint.getRe() == NOT_INITIALIZED || currentPoint.getIm() == NOT_INITIALIZED) {
						i++;
						if (i < functionPoints.size()) {
							lastPoint = currentPoint;
							currentPoint = functionPoints.get(i);
						}
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

	/**
	 * returns functionpoints in the range of this function, where 2 points next to
	 * each other have a maximum scalar distance of {@code maxDistance}. except if
	 * there are {@code null} points, what means we divided almost with 0.
	 * 
	 * @param maxDistance maximum scalar distance of two points in the 2D coordinate
	 *                    space
	 * @return many functionpoints in the functionrange {@code range} representing
	 *         this function
	 */
	public ArrayList<Complex> getPoints(double maxDistance) {

		ArrayList<Complex> result = getVectorFieldInputPoints(maxDistance);

		if (VECTORFIELD_EQUATION == "") {
			return result;
		}

		// if we have a vectorfield, means we get here, calculate the affected points
		// and return those

		System.out.println("calculating affected points");
		for (int i = 0; i < result.size(); i++) {
			result.set(i, ComplexCalculator.calculate(result.get(i), VECTORFIELD_EQUATION));
		}

		return result;
	}

	/**
	 * calculates the output points of this function when the vectorfield is
	 * applied. we need to specify the inputpoints in order to know which points we
	 * want to have because we can not interpolate from here. (at the moment)
	 * 
	 * @param inputPoints the points of this function, that we will apply the
	 *                    vectorfield on
	 * @return the arraylist containing the affected points of the vectorfield
	 */
	private ArrayList<Complex> getVectorFieldOutputPoints(ArrayList<Complex> inputPoints) {

		ArrayList<Complex> result = new ArrayList<>();

		if (VECTORFIELD_EQUATION == "") {
			result = (ArrayList<Complex>) inputPoints.clone();
			return result;
		}

		// if we have a vectorfield, means we get here, calculate the affected points
		// and return those
		for (int i = 0; i < inputPoints.size(); i++) {
			result.add(ComplexCalculator.calculate(inputPoints.get(i), VECTORFIELD_EQUATION));
		}

		return result;
	}

	/**
	 * returns both inputpoints and outputpoints when this function goes into the
	 * vectorfield
	 * 
	 * @param maxDistance maximum distance between two points
	 * @return
	 */
	public ArrayList<ArrayList<Complex>> getInputAndOutputPoints(double maxDistance) {

		ArrayList<ArrayList<Complex>> result = new ArrayList<>();

		result.add(getVectorFieldInputPoints(maxDistance));
		result.add(getVectorFieldOutputPoints(result.get(0)));

		return result;
	}

	/**
	 * returns the inputValue of the point at the given index
	 * 
	 * @param index index of the point, whose inputValue will be returned
	 * @return the inputValue of the point at the given index, or 0 if the point was
	 *         not found
	 */
	private double getInputValue(int index) {

		switch (FUNCTION_VARIABLE) {
		case 'x':
			return functionPoints.get(index).getRe();
		case 'y':
			return functionPoints.get(index).getIm();
		default:
			System.err.println("invalid functionVariable: " + FUNCTION_VARIABLE);
			return 0;
		}
	}

	/**
	 * returns the index of where the point is or should be if it has not yet been
	 * calculated. returns 0 if the {@code functionVariable} of this function is
	 * unknown
	 * 
	 * @param inputValue inputValue of the point, that we want the index from
	 * @return the index of where the point is or should be
	 */
	private int getIndex(double inputValue) {

		for (int i = 0; i < functionPoints.size(); i++) {
			switch (FUNCTION_VARIABLE) {
			case 'x':
				if (functionPoints.get(i).getRe() >= inputValue) {
					return i;
				}
				break;
			case 'y':
				if (functionPoints.get(i).getIm() >= inputValue) {
					return i;
				}
				break;
			default:
				System.err.println("invalid functionVariable: " + FUNCTION_VARIABLE);
				return 0;
			}
		}

		return functionPoints.size();
	}

	/**
	 * returns true if the given complex number as a point is the last point of the
	 * {@code functionPoints} that is in the range of this function.
	 * 
	 * @param point point that is tested
	 * @return {@code true} if the point is the last point from the
	 *         {@code functionPoints}, that is in the range, else returns
	 *         {@code false}
	 */
	private boolean isLastPointInRange(Complex point) {

		switch (FUNCTION_VARIABLE) {
		case 'x':
			return point.getRe() == range[1];
		case 'y':
			return point.getIm() == range[1];
		default:
			System.err.println("invalid functionVariable: " + FUNCTION_VARIABLE);
			return false;
		}
	}

	/**
	 * @return true if this function shall be visible
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/*
	 * SETTERS
	 */

	/**
	 * @param value {@code true} equals visible, {@code false} equals not visible
	 */
	public void setVisible(boolean value) {
		isVisible = value;
	}

	/**
	 * sets a new range for this function and calculates the functionpoints
	 * inbetween, if not already done, with an inputValue-step of
	 * {@code DEFAULT_CALCULATION_STEP}
	 * 
	 * @param newRange the new range for this function
	 */
	public void setRange(double[] newRange) {

		range = newRange;

		// create first entries for this function
		getOutputValue(this.range[0]);
		getOutputValue(this.range[1]);

		// calculate "default" functionpoints in that new range
		for (double inputValue = range[0]; inputValue <= range[1]; inputValue += DEFAULT_CALCULATION_STEP) {
			getOutputValue(inputValue);
		}
	}

	/**
	 * @param newColor the new color for this function
	 */
	public void setColor(Color newColor) {

		functionColor = newColor;

	}
}
