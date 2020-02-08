package canvas;

import complexNumbers.*;
import functions.Function2D;
import functions.VectorField2D;
import functions.VectorFieldFunction2D;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class Canvas2D extends JPanel {

	private int margin; // margin around coordinate system

	// Graphing options
	private int density; // how many values are drawn between x && y = [n...n+1]
	private int[] outputArea; // draw [0...1] x [2...3]
	private int coordinatelineDensity;
	private Point ZERO; // (0, 0) on the coordinate system
	private int dotwidth; // width of a calculated point
	private final double PRINTAREATOLERANCE = 0.1;

	private Font FONT = new Font("Ubuntu", Font.PLAIN, 30); // font used for the title

	// set what to draw
	private boolean paintFunctionPoints; // painting function points
	private boolean paintHorizontalLines; // painting horizontal lines from input grid
	private boolean paintVerticalLines; // painting verticalLines from input grid

	private Dimension paintableDimension; // square dimension where we can paint on
	private Point ONE; // location of the point (1, 1)

	private ArrayList<Function2D> functions2D; // saves the 2d functions to paint on this canvas
	private ArrayList<VectorFieldFunction2D> vectorFieldFunctions2D; // saves the 2d functions that are affected by a
																		// vector field to paint on this canvas
	private ArrayList<VectorField2D> vectorFields2D;
	private ArrayList<ArrayList<Complex>> functions; // outputPoints from f(g(x))
	private ArrayList<Color> functionColors; // colors of the input functions and transformed functions

	private ArrayList<ArrayList<Complex>> vectorFieldGridFunctions;
	private ArrayList<Color> vectorFieldGridFunctionColors;

	private boolean functionLines, functionPoints;
	private int functionLineWidth;

	private int vectorFieldGridLineWidth;
	private boolean vectorFieldGridLines;

	/* CONSTRUCTOR */

	/**
	 * Constructor, builds the JPanel and defines values used to draw the function
	 * 
	 * @param functions      ArrayList containing an ArrayList with calculated
	 *                       function points ready to plot
	 * @param functionColors ArrayList containing the colors for the different
	 *                       functions.
	 *                       {@code functions.size() == functionColors.size()}
	 */
	public Canvas2D(ArrayList<ArrayList<Complex>> functions, ArrayList<Color> functionColors,
			ArrayList<ArrayList<Complex>> vectorField, ArrayList<Color> vectorFieldColors) {

		setDefaultValues();

		// save given variables
		this.functions = functions;
		this.functionColors = functionColors;
		this.vectorFieldGridFunctions = vectorField;
		this.vectorFieldGridFunctionColors = vectorFieldColors;

		// set up this jpanel
		setupJPanel();

	}

	/**
	 * Constructor, builds the JPanel and defines values used to draw the function
	 * 
	 * @param parent parent class, this is where we get informations from
	 */
	public Canvas2D() {

		// initialize final variables
		setDefaultValues();

		// set up this jpanel
		setupJPanel();

	}

	/**
	 * adjusts the jpanel as we wish
	 */
	public void setupJPanel() {

		this.setOpaque(true);
		this.setBackground(Color.BLACK);

	}

	/* PAINTCOMPONENT */

	/**
	 * this function paints everything, the function, the coordinate system, the
	 * legends and the connecting lines
	 * 
	 * @param g needed to paint
	 */
	public void paintComponent(Graphics g) {

		// reset image, clearing the Graphics g variable
		super.paintComponent(g);

		// return if we have nothing to paint
		if (outputArea == null) {
			return;
		}

		g.setFont(FONT);

		// calculate square that we can paint on
		updateCoordinateSystem();

		// draw the coordinate System with its labels
		drawCoordinateSystem(g);

		// plot the function values f(z)
//		 drawFunctionVectorField(g); // Select which function to plot here

		draw2DVectorFields(g);

		// plot the transformed function values f(g(x))
//		drawFunctions(g);

		// draw the color legends
//		drawColorLegend(g);

		// draw 2d functions
		draw2DFunctions(g);

		// draw 2d functions in a vectorfield
//		draw2DVectorFieldFunctions(g);

	}

	/**
	 * saves the new size of the paintable Dimension in {@code paintableDimension}
	 */
	private void updateCoordinateSystem() {

		// use square as paintable Dimension
		int smallerLength = Math.min(this.getSize().width - margin, this.getSize().height - margin);
		paintableDimension = new Dimension(smallerLength, smallerLength);

		// set the new {@code ZERO} point of the coordinate system
		ZERO = new Point(this.getSize().width / 2, this.getSize().height / 2);

	}

	private void draw2DVectorFields(Graphics g) {

		for (int i = 0; i < vectorFields2D.size(); i++) {

//			System.out.println("drawing function " + i + " with " + functions.get(i).size() + " points");

			draw2DVectorField(g, vectorFields2D.get(i));

		}

	}

	private void draw2DVectorField(Graphics g, VectorField2D vectorField) {

		ArrayList<ArrayList<Complex>> vectorFieldPoints = vectorField.getPoints(0.05);
		Color vectorFieldColor = vectorField.getColor();

		if(!vectorField.isVisible()) {
			return;
		}
		// draw horizontal lines
		for (int i = 0; i < vectorFieldPoints.size(); i++) {

			draw2DFunction(g, vectorFieldPoints.get(i), vectorFieldColor);

		}

	}

	/**
	 * Draws the functions y = g(x)
	 * 
	 * @param g use graphics to draw on the jpanel
	 */
	private void draw2DFunctions(Graphics g) {

		// draw each inputfunction
		for (int i = 0; i < functions2D.size(); i++) {

//			System.out.println("drawing function " + i + " with " + functions.get(i).size() + " points");

			draw2DFunction(g, functions2D.get(i));

		}

	}

	private void draw2DFunction(Graphics g, ArrayList<Complex> functionPoints, Color functionColor) {

		Complex currentPoint;
		Point lastP, currentP = new Point(0, 0);
		double distanceToLastPoint, distanceToNextPoint;

		lastP = getPointAt(functionPoints.get(0).getRe(), functionPoints.get(0).getIm());
		distanceToLastPoint = Double.MAX_VALUE;

		g.setColor(functionColor);

		// draw each outputPoint
		for (int j = 0; j < functionPoints.size(); j++) {

			if ((currentPoint = functionPoints.get(j)) == null || !isInOutputRange(currentPoint)) {
				lastP = currentP;
				continue;
			}

//				System.out.println("LEINWAND2D: \t drawing point at " + currentPoint.getRe() + " + i(" + currentPoint.getIm() + ")");
			if (this.functionPoints) {
				drawPointAt(currentPoint.getRe(), currentPoint.getIm(), g, functionColor);
			}

			// draw connecting line
			if (functionLines) {
				currentP = getPointAt(currentPoint.getRe(), currentPoint.getIm());
				// g.drawLine(lastP.x, lastP.y, currentP.x, currentP.y);

				Graphics2D g2 = (Graphics2D) g;
				g2.setStroke(new BasicStroke(functionLineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

				distanceToNextPoint = Math.sqrt(Math.pow(lastP.x - currentP.x, 2) + Math.pow(lastP.y - currentP.y, 2));
				if (distanceToNextPoint > 20 * distanceToLastPoint) {
					lastP = currentP;
				}

				if (isInOutputRange(lastP)) {
					g2.draw(new Line2D.Float(lastP.x, lastP.y, currentP.x, currentP.y));
				}

				if (!lastP.equals(currentP)) {

					lastP = currentP;
					distanceToLastPoint = distanceToNextPoint;
				}
			}
		}

		// draw a bigger dot at the first and last point
		g.setColor(Color.white);
		g.fillOval(getPointAt(functionPoints.get(0).getRe(), functionPoints.get(0).getIm()).x - dotwidth,
				getPointAt(functionPoints.get(0).getRe(), functionPoints.get(0).getIm()).y - dotwidth, 2 * dotwidth,
				2 * dotwidth);
		g.fillOval(
				getPointAt(functionPoints.get(functionPoints.size() - 1).getRe(),
						functionPoints.get(functionPoints.size() - 1).getIm()).x - dotwidth,
				getPointAt(functionPoints.get(functionPoints.size() - 1).getRe(),
						functionPoints.get(functionPoints.size() - 1).getIm()).y - dotwidth,
				2 * dotwidth, 2 * dotwidth);

	}

	private void draw2DFunction(Graphics g, Function2D function) {

		List<Complex> functionPoints;
		Complex currentPoint;
		Point lastP, currentP = new Point(0, 0);
		Color currentColor;
		double distanceToLastPoint, distanceToNextPoint;

		functionPoints = function.getPoints(0.1);

		if (function.emptyRange() || !function.isVisible()) {
			return;
		}

		currentColor = function.getColor();
		lastP = getPointAt(functionPoints.get(0).getRe(), functionPoints.get(0).getIm());
		distanceToLastPoint = Double.MAX_VALUE;

		g.setColor(currentColor);

		// draw each outputPoint
		for (int j = 0; j < functionPoints.size(); j++) {

			if ((currentPoint = functionPoints.get(j)) == null || !isInOutputRange(currentPoint)) {
				lastP = currentP;
				continue;
			}

//				System.out.println("LEINWAND2D: \t drawing point at " + currentPoint.getRe() + " + i(" + currentPoint.getIm() + ")");
			if (this.functionPoints) {
				drawPointAt(currentPoint.getRe(), currentPoint.getIm(), g, currentColor);
			}

			// draw connecting line
			if (functionLines) {
				currentP = getPointAt(currentPoint.getRe(), currentPoint.getIm());
				// g.drawLine(lastP.x, lastP.y, currentP.x, currentP.y);

				Graphics2D g2 = (Graphics2D) g;
				g2.setStroke(new BasicStroke(functionLineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

				distanceToNextPoint = Math.sqrt(Math.pow(lastP.x - currentP.x, 2) + Math.pow(lastP.y - currentP.y, 2));
				if (distanceToNextPoint > 5 * distanceToLastPoint) {
					lastP = currentP;
				}
				g2.draw(new Line2D.Float(lastP.x, lastP.y, currentP.x, currentP.y));

				if (!lastP.equals(currentP)) {

					lastP = currentP;
					distanceToLastPoint = distanceToNextPoint;
				}
			}
		}

		// draw a bigger dot at the first and last point
		g.setColor(Color.white);
		g.fillOval(getPointAt(functionPoints.get(0).getRe(), functionPoints.get(0).getIm()).x - dotwidth,
				getPointAt(functionPoints.get(0).getRe(), functionPoints.get(0).getIm()).y - dotwidth, 2 * dotwidth,
				2 * dotwidth);
		g.fillOval(
				getPointAt(functionPoints.get(functionPoints.size() - 1).getRe(),
						functionPoints.get(functionPoints.size() - 1).getIm()).x - dotwidth,
				getPointAt(functionPoints.get(functionPoints.size() - 1).getRe(),
						functionPoints.get(functionPoints.size() - 1).getIm()).y - dotwidth,
				2 * dotwidth, 2 * dotwidth);

	}

	private boolean isInOutputRange(Complex point) {

		return point.getRe() >= outputArea[0] - PRINTAREATOLERANCE
				&& point.getRe() <= outputArea[1] + PRINTAREATOLERANCE
				&& point.getIm() >= outputArea[2] - PRINTAREATOLERANCE
				&& point.getIm() <= outputArea[3] + PRINTAREATOLERANCE;
	}

	private boolean isInOutputRange(Point point) {

		Point bottomLeftCorner = getPointAt(outputArea[0] - PRINTAREATOLERANCE, outputArea[2] - PRINTAREATOLERANCE);
		Point topRightCorner = getPointAt(outputArea[1] + PRINTAREATOLERANCE, outputArea[3] + PRINTAREATOLERANCE);

		return point.x >= bottomLeftCorner.x && point.x <= topRightCorner.x && point.y >= topRightCorner.y
				&& point.y <= bottomLeftCorner.y;
	}

	/**
	 * Draws the coordinate system of the complex number area with coordiante lines
	 * and labels
	 * 
	 * @param g using Graphics to draw on the JPanel
	 */
	private void drawCoordinateSystem(Graphics g) {

		int numberOfLines;
		int lineSpace; // space between two lines
		Point lineStart, lineEnd; // temporary start and end on-screen-position of the lines

		// draw vertical lines x = constant
		numberOfLines = (int) ((Math.abs(outputArea[0]) + Math.abs(outputArea[1])));
		lineSpace = paintableDimension.width / numberOfLines;
		ONE = new Point(lineSpace, lineSpace);
		for (int x = outputArea[0]; x <= outputArea[1]; x += coordinatelineDensity) {

//			System.out.println("x = " + x);
			// define line location
			lineStart = getPointAt(x, outputArea[2]);
			lineEnd = getPointAt(x, outputArea[3]);

			// draw line
			g.setColor(Color.GRAY);
			g.drawLine(lineStart.x, lineStart.y, lineEnd.x, lineEnd.y);

			// draw linelabel
			g.setColor(Color.WHITE);
			g.drawString(Integer.toString(x), ZERO.x + x * lineSpace + 5, ZERO.y - 5);
		}

		// draw horizontal lines y = constant

		lineSpace = paintableDimension.height / numberOfLines;
		numberOfLines = (int) ((Math.abs(outputArea[2]) + Math.abs(outputArea[3])) / coordinatelineDensity);
		for (int y = outputArea[2]; y <= outputArea[3]; y += coordinatelineDensity) {

			// define line locationoutputArea[0]
			lineStart = getPointAt(outputArea[0], y);
			lineEnd = getPointAt(outputArea[1], y);

			// draw line
			g.setColor(Color.GRAY);
			g.drawLine(lineStart.x, lineStart.y, lineEnd.x, lineEnd.y);

			// draw line label
			g.setColor(Color.WHITE);
			if (!vectorFieldGridLines || vectorFieldGridFunctions == null) {
				g.drawString(Integer.toString(y), ZERO.x + 5, lineStart.y - 5);
			} else {
				g.drawString(Integer.toString(y) + "i", ZERO.x + 5, lineStart.y - 5);
			}
		}

		// draw x = 0 and y = 0 lines again but thicker
		g.setColor(Color.white);
		g.fillRect(ZERO.x - 1, ZERO.y - paintableDimension.height / 2, 3, paintableDimension.height);
		g.fillRect(ZERO.x - paintableDimension.width / 2, ZERO.y - 1, paintableDimension.width, 3);

	}

	/**
	 * draws two legends for the colors, one for the colors in y direction and one
	 * for the colors in x direction with bounds of inputArea
	 * 
	 * @param g using Graphics to directly draw the legends on the JPanel
	 */
	private void drawColorLegend(Graphics g) {

		int size = paintableDimension.width / 10; // legends is a square
		Point location = new Point(paintableDimension.width - size,
				ZERO.y + paintableDimension.height / 2 - size - margin);
		Point location2 = new Point(2 * margin, ZERO.y + paintableDimension.height / 2 - size - margin);

		// draw colorlegends
		for (int x = 0; x <= size; x++) {
			g.setColor(getColor2(x, 0, size));
			g.drawLine(location.x + x, location.y, location.x + x, location.y + size);

			g.setColor(getColor(x, 0, size));
			g.drawLine(location2.x, location2.y + size - x, location2.x + size, location2.y + size - x);
		}

		// draw strings of inputArea bounds
		g.setColor(Color.WHITE);
		g.drawString("x=", location.x - 2 * FONT.getSize(), location.y - 5);
		g.drawString(Integer.toString((int) Math.round(vectorFieldGridFunctions.get(0).get(0).getRe())), location.x,
				location.y - 5);
		g.drawString(
				Integer.toString((int) Math.round(vectorFieldGridFunctions.get(vectorFieldGridFunctions.size() - 1)
						.get(vectorFieldGridFunctions.get(vectorFieldGridFunctions.size() - 1).size() - 1).getRe())),
				location.x + size - 5, location.y - 5);

		g.drawString("y=", location2.x - 2 * FONT.getSize(), location2.y - 5);
		g.drawString(Integer.toString((int) Math.round(vectorFieldGridFunctions.get(0).get(0).getIm())),
				location2.x - FONT.getSize(), location2.y + 10);
		g.drawString(
				Integer.toString((int) Math.round(vectorFieldGridFunctions.get(vectorFieldGridFunctions.size() - 1)
						.get(vectorFieldGridFunctions.get(vectorFieldGridFunctions.size() - 1).size() - 1).getIm())),
				location2.x - FONT.getSize(), location2.y + size);

	}

	/**
	 * draw a point at location x, y in the Coordinate system. x and y are NOT
	 * screen coordinates
	 * 
	 * @param x location x value according to coordinate system
	 * @param y location y value according to coordinate system
	 * @param g using Graphics to directly draw on the JPanel
	 */
	private void drawPointAt(double x, double y, Graphics g, Color color) {

		g.setColor(color);

		// invert y axis and draw point on screen
		g.fillRect((int) (x * ONE.x + ZERO.x - dotwidth / 2), (int) ((-1) * y * ONE.y + ZERO.y - dotwidth / 2),
				dotwidth, dotwidth);

	}

	/* GETTERS */

	/**
	 * returns a color according to the value in [min...max] so that we get that
	 * fancy color change colors reach from min = green to red to max = yellow
	 * 
	 * @param value where we are in [min...max]
	 * @param min   the minimum of the colorchange-area
	 * @param max   the maximum of the colorchange-area
	 * @return the color according to value in [min...max]
	 */
	private Color getColor(double value, double min, double max) {

		Color result;
		double change1 = (max + Math.abs(min)) / 2;
		// System.out.println("change at x = " + change1 + ", " + change2);

		double shiftedValue = value - min;

		// minimum, green to mid: red
		if (shiftedValue <= change1) {

//			System.out.println(((int) (shiftedValue * 255 / change1)) + " " + (255 - (int) (shiftedValue * 255 / change1)));
			result = new Color((int) (shiftedValue * 255 / change1), 255 - (int) (shiftedValue * 255 / change1), 0,
					255);

			// maximum, yellow to mid: red
		} else {

			result = new Color(255, (int) ((shiftedValue - change1) * 255 / change1), 0, 255);

		}

		return result;
	}

	/**
	 * returns a color according to the value in [min...max] so that we get that
	 * fancy color change colors reach from min = light blue to violet to max = pink
	 * 
	 * @param value where we are in [min...max]
	 * @param min   the minimum of the colorchange-area
	 * @param max   the maximum of the colorchange-area
	 * @return the color according to value in [min...max]
	 */
	private Color getColor2(double value, double min, double max) {

		Color result;
		double change1 = (max + Math.abs(min)) / 2;
		// System.out.println("change at x = " + change1 + ", " + change2);

		double shiftedValue = value - min;

		// minimum, light blue to mid: violet
		if (shiftedValue <= change1) {

//			System.out.println(((int) (shiftedValue * 255 / change1)) + " " + (255 - (int) (shiftedValue * 255 / change1)));
			result = new Color(100, 255 - (int) (shiftedValue * 255 / change1),
					255 - (int) (shiftedValue * 100 / change1), 255);

			// maximum, pink to mid: violet
		} else {

			result = new Color(100 + (int) ((shiftedValue - change1) * 155 / change1), 0, 155, 255);

		}

		return result;
	}

	/**
	 * returns the screen coordinates of a given point (x, y)
	 * 
	 * @param x location x value according to coordinate system
	 * @param y location y value according to coordinate system
	 * @return Point with on-screen-coordinates
	 */
	private Point getPointAt(double x, double y) {

		// invert y axis and return screenPosition of (x, y)
		return new Point((int) (x * ONE.x + ZERO.x), (int) ((-1) * y * ONE.y + ZERO.y));

	}

	/**
	 * @return returns paintable dimension of this jpanel
	 */
	public Dimension getPaintableDimension() {
		return paintableDimension;
	}

	/**
	 * @return returns the 2d functions arraylist
	 */
	public ArrayList<Function2D> get2DFunctions() {

		return functions2D;
	}
	
	public ArrayList<VectorField2D> get2DVectorFields() {
		
		return vectorFields2D;
	}

	/* SETTERS */

	public void setDefaultValues() {

		margin = 50;
		this.functions2D = new ArrayList<>();
		this.vectorFieldFunctions2D = new ArrayList<>();
		this.functions = new ArrayList<>();
		this.functionColors = new ArrayList<>();
		this.vectorFieldGridFunctions = new ArrayList<>();
		this.vectorFieldGridFunctionColors = new ArrayList<>();
		this.vectorFields2D = new ArrayList<>();

		outputArea = new int[] { -2, 2, -2, 2 };
		coordinatelineDensity = 1;
		dotwidth = 5;
		functionPoints = false;
		functionLines = true;
		functionLineWidth = 2;
		vectorFieldGridLineWidth = 2;
		vectorFieldGridLines = true;
		paintVerticalLines = true;
		paintHorizontalLines = true;
	}

	/**
	 * sets the vector field that has been calculated from a complex function
	 * 
	 * @param vectorField       ArrayList containing all gridlines as ArrayLists
	 *                          containing Complex numbers as the location of the
	 *                          output points
	 * @param vectorFieldColors ArrayList containing the colors for the vector field
	 *                          grid lines
	 */
	public void setVectorField(ArrayList<ArrayList<Complex>> vectorField, ArrayList<Color> vectorFieldColors) {

		vectorFieldGridFunctions = vectorField;
		vectorFieldGridFunctionColors = vectorFieldColors;

	}

	/**
	 * adds a function with its color to the 2D canvas
	 * 
	 * @param functionPoints calculated points of the function to add
	 * @param functionColor  color of the function to add
	 */
	public void addFunction(ArrayList<Complex> functionPoints, Color functionColor) {

		functions.add(functionPoints);
		functionColors.add(functionColor);

	}

	/**
	 * adds a 2d function to this canvas
	 * 
	 * @param function the function to be added
	 */
	public void addFunction2D(Function2D function) {

		functions2D.add(function);
	}

	/**
	 * @param function the function to be added
	 */
	public void addVectorField2D(VectorField2D vectorField) {

		vectorFields2D.add(vectorField);
	}

	/**
	 * @param outputArea adjust the output area size
	 */
	public void setOutputArea(int[] outputArea) {
		this.outputArea = outputArea;

	}

	/**
	 * @param value the density of the coordinate lines, for example 2 means we draw
	 *              1 out of 2 coordinate lines
	 */
	public void setCoordinatelineDensity(int value) {
		this.coordinatelineDensity = value;
	}

	/**
	 * @param value the diameter of a painted dot of the functions
	 */
	public void setDotwidth(int value) {
		this.dotwidth = value;
	}

	/**
	 * @param value true if we paint the horizontal lines of the vectorfield
	 */
	public void setHorizontalLines(boolean value) {
		this.paintHorizontalLines = value;
	}

	/**
	 * @param value true if we paint the vertical lines of the vectorfield
	 */
	public void setVerticalLines(boolean value) {
		this.paintVerticalLines = value;
	}

	/**
	 * @param value set true to draw lines between the individual dots of a function
	 */
	public void setFunctionLines(boolean value) {
		functionLines = value;
	}

}
