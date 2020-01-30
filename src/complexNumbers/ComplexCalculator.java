package complexNumbers;

import java.util.ArrayList;

public class ComplexCalculator {

	/**
	 * calculates f(z) with the given z, this function is recursive! The
	 * functionstring can only contain a variable 'z'.
	 * 
	 * @param z        value of z in the given function, null if we divided by
	 *                 something too small
	 * @param function contains the function
	 * @param return   returns the resulting value = function(z)
	 */
	public static Complex calculate(Complex z, String function) {

//	System.out.println("to calculate: " + input);

		// test if a previous calculation returned null
		if (z == null) {
			return null;
		}

		function.replaceAll("\\s", ""); // remove spaces

		Complex result = new Complex(0, 0, true); // will be returned
		int openBrackets = 0; // counts the open brackets while we read through the string
		boolean bracketsRemoved; // true if theres was nothing to do but removing brackets at start and end

		do {

			bracketsRemoved = false;

			// for each character in function, look for + and -
			for (int i = 0; i < function.length(); i++) {
				// System.out.println(input.charAt(i) + " found");
				switch (function.charAt(i)) {

				case '(':
					openBrackets++;
					break;

				case ')':
					openBrackets--;
					break;

				case '+':
					if (openBrackets == 0) {

						Complex number = calculate(z, function.substring(0, i));

						if (number == null) {
							return null;
						}

						result = number.add(calculate(z, function.substring(i + 1, function.length())));
						return result;
					}
					break;

				case '-':
//					System.out.println("GUI: \t i = " + i + ", function: " + function);
					if (openBrackets == 0 && i != 0 && function.charAt(i - 1) != 'E') { // except case "-n"

						Complex number;

						// doesnt work here, because "-x+1" will be "-(x+1)"
						// case "-n"
//						if (i == 0) {
//							number = new Complex(0, 0, true);
//						} else {
						number = calculate(z, function.substring(0, i));
//						}

						if (number == null) {
							return null;
						}

						result = number.subtract(calculate(z, function.substring(i + 1, function.length())));
						return result;
					}
					break;

				}

			}

			// if there wasnt a + or -, for each character in function, look for * and /
			for (int i = function.length() - 1; i >= 0; i--) {
				switch (function.charAt(i)) {

				case '(':
					openBrackets++;
					break;

				case ')':
					openBrackets--;
					break;

				case '*':
					if (openBrackets == 0) {
						Complex number = calculate(z, function.substring(0, i));

						if (number == null) {
							return null;
						}

						result = calculate(z, function.substring(0, i))
								.multiply(calculate(z, function.substring(i + 1, function.length())));
						return result;
					}
					break;

				case '/':
					if (openBrackets == 0) {
						Complex numerator = calculate(z, function.substring(0, i));
						Complex denumerator = calculate(z, function.substring(i + 1, function.length()));

						if (numerator == null) {
							return null;
						}

						result = numerator.divide(denumerator);

						return result;
					}
					break;

				}
			}

			// if there wasnt a * or /, for each character in function, look for sin, cos
			// and exp
			for (int i = 0; i < function.length(); i++) {
				switch (function.charAt(i)) {

				case '(':
					openBrackets++;
					break;

				case ')':
					openBrackets--;
					break;

				case 's': // sin or sqrt
					if (openBrackets == 0) {

						// TODO: sqrt for complex functions
						if (function.charAt(i + 1) == 'q') { // this sqrt function will return sqrt(Re(z))
							result = new Complex(
									Math.sqrt(calculate(z, function.substring(i + 5, function.length() - 1)).getRe()),
									0, true);
						} else {
							Complex argument = calculate(z, function.substring(i + 4, function.length() - 1));
							if (argument == null) {
								return null;
							}
							result = argument.sin();
						}
						return result;
					}
					break;

				case 'c':
					if (openBrackets == 0) {
						Complex argument = calculate(z, function.substring(i + 4, function.length() - 1));
						if (argument == null) {
							return null;
						}
						result = argument.cos();
						return result;
					}
					break;

				case 'e':
					if (openBrackets == 0) {
						Complex argument = calculate(z, function.substring(i + 4, function.length() - 1));
						if (argument == null) {
							return null;
						}
						result = argument.exp();
						return result;
					}
					break;

				}
			}

			// if there was nothing to do, we need to remove brackets at start and end
			if (function.charAt(0) == '(' && function.charAt(function.length() - 1) == ')') {
//			System.out.println("removing brackets around " + input);
				function = function.substring(1, function.length() - 1);
				bracketsRemoved = true;
//			System.out.println("new input: " + input);
			}

		} while (bracketsRemoved);

		// if there also were no brackets to remove, we need to create a new complex
		// number
//	System.out.println("make a new Complex number from: " + input);

		// case "-n"
		if (function.charAt(0) == '-') {

			function = function.split("-")[1];

			switch (function) {

			case "x":
			case "y":
			case "z":
				return z.multiply(new Complex(-1, 0, true));

			case "i":
				return new Complex(0, -1, true);

			default:
//				System.out.println("CALCULATE: \t creating complex from " + function);
				return new Complex(-Double.parseDouble(function), 0, true);
			}

		} else {

			switch (function) {

			case "x":
			case "y":
			case "z":
				return z;

			case "i":
				return new Complex(0, 1, true);

			default:
				// System.out.println("CALCULATE: \t creating complex from " + function);
				return new Complex(Double.parseDouble(function), 0, true);
			}

		}

	}

	/**
	 * calculates a grid from the vector field from the complex function
	 * {@code functionName}. it calculates a grid with a density of
	 * {@code gridDensity} which means the space inbetween two horizontal or two
	 * vertica lines is {@code 1.0 / gridDensity}. each line is calculated
	 * seperately with an own density of {@code calculationDensity} so that the
	 * lines are rounder and more often cross in a 90 degree angle as it should be.
	 * this method returns {@code null} if {@code inputArea.length() != 4}.
	 * 
	 * @param functionName       string containing the name of the complex function,
	 *                           the vectorfield
	 * @param inputArea          double array defining the input area / location for
	 *                           the grid:
	 *                           {@code x from [inputArea[0] .. inputArea[1]]},
	 *                           {@code y from [inputArea[2] .. inputArea[3]]}
	 * @param calculationDensity the distance between two calculated points from any
	 *                           gridline {@code ≃ 1.0 / calculationDensity}
	 * @param gridDensity        determining the distance between two gridlines
	 *                           {@code ≃ 1.0 / gridDensity}
	 * @return
	 */
	public static ArrayList<ArrayList<Complex>> calculateVectorFieldGrid(String functionName, double[] inputArea,
			int calculationDensity, int gridDensity) {

		// inputArea Array must contain exactly 4 doubles
		if (inputArea.length != 4) {
			return null;
		}

		ArrayList<Complex> currentResult;
		ArrayList<ArrayList<Complex>> result = new ArrayList<>();

		// calculate horizontal input lines
		for (double deltaY = inputArea[2]; deltaY <= inputArea[3] + 1.0 / gridDensity; deltaY += 1.0 / gridDensity) {

			currentResult = new ArrayList<>();

			for (double x = inputArea[0]; x <= inputArea[1]; x += 1.0 / calculationDensity) {

				currentResult.add(calculate(new Complex(x, deltaY, true), functionName));

			}

			result.add(currentResult);

		}

		// calculate vertical input lines
		for (double deltaX = inputArea[0]; deltaX <= inputArea[1] + 1.0 / gridDensity; deltaX += 1.0 / gridDensity) {

			currentResult = new ArrayList<>();

			for (double y = inputArea[2]; y <= inputArea[3]; y += 1.0 / calculationDensity) {

				currentResult.add(calculate(new Complex(deltaX, y, true), functionName));

			}

			result.add(currentResult);

		}

		return result;

	}

	/**
	 * calculates the function y = f(x) in an intervall [functionStart..functionEnd]
	 * with a delta x of 1.0 / {@code calculationDensity}. the functionName can only
	 * contain the variable {@code z}
	 * 
	 * @param functionName       String containing the function, for example
	 *                           {@code "sin(z)"}
	 * @param functionStart      start of the definitionintervall of the function
	 * @param functionEnd        end of the definitionintervall of the function
	 * @param calculationDensity delta x step = {@code 1.0 / calculationDensity}
	 * @param result             returns the arraylist containing the outputpoints y
	 *                           = f(x)
	 */
	public static ArrayList<Complex> calculateFunction(String functionName, double functionStart, double functionEnd,
			int calculationDensity) {

		double y; // save value of y = f(x)
		ArrayList<Complex> result = new ArrayList<>();

		for (double x = functionStart; x <= functionEnd; x += 1.0 / calculationDensity) {

			y = calculate(new Complex(x, 0, true), functionName).getRe();
			result.add(new Complex(x, y, true));

		}

		return result;
	}

	/**
	 * calculates the transformed function {@code g(f(x))} from the input function
	 * {@code y = f(x)}. the resulting ArrayList has the same size as the given
	 * {@code functionToTransform}
	 * 
	 * @param functionName        String containing the function of the
	 *                            corresponding vectorfield, for example
	 *                            {@code "sin(z)"}
	 * @param functionToTransform input points of a function. those points will be
	 *                            transformed using the vectorfield
	 *                            {@code functionName}
	 * @return returns the arraylist containing the complex outputpoints c = g(f(x))
	 */
	public static ArrayList<Complex> calculateTransformedFunction(String functionName,
			ArrayList<Complex> functionToTransform) {

		double y; // save value of y = f(x)
		ArrayList<Complex> result = new ArrayList<>();

		for (Complex c : functionToTransform) {

			result.add(calculate(c, functionName));

		}

		return result;
	}

	/**
	 * calculates the transformed function {@code g(f(x))} from the input function
	 * {@code y = f(x)} in an intervall [functionStart..functionEnd] with a delta x
	 * of 1.0 / {@code calculationDensity}. the {@code functionName} and
	 * {@code functionToTransform} can only contain the variable {@code z}
	 * 
	 * @param functionName        String containing the function of the
	 *                            corresponding vectorfield, for example
	 *                            {@code "sin(z)"}
	 * @param functionToTransform String containing the input function, that will be
	 *                            transformed using the vectorfield, for example
	 *                            {@code "sin(z)"}
	 * @param functionStart       start of the definitionintervall of the function
	 * @param functionEnd         end of the definitionintervall of the function
	 * @param calculationDensity  delta x step = {@code 1.0 / calculationDensity}
	 * @return returns the arraylist containing the complex outputpoints c = g(f(x))
	 */
	public static ArrayList<Complex> calculateTransformedFunction(String functionName, String functionToTransform,
			double functionStart, double functionEnd, int calculationDensity) {

		double y; // save value of y = f(x)
		ArrayList<Complex> result = new ArrayList<>();

		for (double x = functionStart; x <= functionEnd; x += 1.0 / calculationDensity) {

			y = calculate(new Complex(x, 0, true), functionToTransform).getRe();
			result.add(calculate(new Complex(x, y, true), functionName));

		}

		return result;
	}

}
