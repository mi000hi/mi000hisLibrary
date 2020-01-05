package canvasExtensions;

import canvas.Canvas2D;
import functions.Function2D;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class Canvas2DInputField extends JPanel implements KeyListener {

	/*
	 * final variables
	 */
	private final Canvas2D canvas;
	private final Font TEXTFIELD_FONT = new Font("Ubuntu", Font.PLAIN, 40);

	/*
	 * other variables
	 */
	private JTextField functionInputField;
	private JTextField rangeInputField01, rangeInputField02;
	private JTextField colorInputField01, colorInputField02, colorInputField03;
	private JPanel colorPanel;
	private SpringLayout baseLayout;

	/*
	 * CONSTRUCTORS
	 */

	public Canvas2DInputField(Canvas2D canvas) {

		super();

		this.canvas = canvas;

		// initialize variables
		functionInputField = new JTextField("y = 5+sin( 3*x )");
		rangeInputField01 = new JTextField("-3");
		rangeInputField02 = new JTextField("5");
		colorInputField01 = new JTextField("0");
		colorInputField02 = new JTextField("123");
		colorInputField03 = new JTextField("255");

		functionInputField.setFont(TEXTFIELD_FONT);
		rangeInputField01.setFont(TEXTFIELD_FONT);
		rangeInputField02.setFont(TEXTFIELD_FONT);
		colorInputField01.setFont(TEXTFIELD_FONT);
		colorInputField02.setFont(TEXTFIELD_FONT);
		colorInputField03.setFont(TEXTFIELD_FONT);

		colorPanel = new JPanel();
		colorPanel.setOpaque(true);
		colorPanel.setBackground(readColor());

		createGui();
	}

	/*
	 * MAIN METHOD
	 */

	/*
	 * OTHER METHODS
	 */

	/**
	 * creates a new function from the input textfields and gives the new function
	 * to the canvas. the canvas will be repainted at the end.
	 */
	private void readAndAddFunction() {

		String[] functionInputString = functionInputField.getText().replaceAll("\\s", "").split("=", 2);
		char functionVariable;
		double[] range = new double[2];
		Color functionColor;

		// get the functionVariable
		if (functionInputString[0].length() != 1) {
			System.err.println("functionVariable can one character long: \\" + functionInputString[0] + "\\");
		}
		functionVariable = getOtherVariable(functionInputString[0].charAt(0));

		// get the functionrange
		range[0] = Double.parseDouble(rangeInputField01.getText());
		range[1] = Double.parseDouble(rangeInputField02.getText());
		if (range[0] > range[1]) {
			System.err.println("given range is in wrong order. swapping now. was: " + range[0] + " - " + range[1]);
			double tmp = range[0];
			range[0] = range[1];
			range[1] = tmp;
		}

		// get the functioncolor
		functionColor = readColor();

		System.out.println("adding new Function2D(" + functionInputString[1] + ", " + functionVariable + ", " + range
				+ ", " + functionColor.toString());
		canvas.addFunction2D(new Function2D(functionInputString[1], functionVariable, range, functionColor));

		// repaint the canvas with the new function
		canvas.repaint();
	}

	/**
	 * reads the color for the next function from the input textfields and returns
	 * it
	 * 
	 * @return the color for the next function. 0 for the colorparts, where the
	 *         inputstring does not match a number from 0-255.
	 */
	private Color readColor() {

		int r = 0, g = 0, b = 0;

		try {
			r = Integer.parseInt(colorInputField01.getText().replaceAll("\\s", ""));
		} catch (NumberFormatException nfe) {
			// not important, just let variables on 0
		}
		try {
			g = Integer.parseInt(colorInputField02.getText().replaceAll("\\s", ""));
		} catch (NumberFormatException nfe) {
			// not important, just let variables on 0
		}
		try {
			b = Integer.parseInt(colorInputField03.getText().replaceAll("\\s", ""));
		} catch (NumberFormatException nfe) {
			// not important, just let variables on 0
		}

		// test value ranges
		if (r < 0 || r > 255) {
			r = 0;
		}
		if (g < 0 || g > 255) {
			g = 0;
		}
		if (b < 0 || b > 255) {
			b = 0;
		}

		return new Color(r, g, b);
	}

	/**
	 * updates the background color of the preview jpanel by reading the values from
	 * the colorinputfields
	 */
	private void updatePreviewColor() {

		Color previewColor = readColor();

		colorInputField01.setBackground(previewColor);
		colorInputField02.setBackground(previewColor);
		colorInputField03.setBackground(previewColor);

		this.repaint();
	}

	private void createGui() {

		baseLayout = new SpringLayout();

		// set up color panel
		colorPanel = new JPanel();
		colorPanel.setOpaque(true);
		colorPanel.setBackground(new Color(0, 0, 0));
		colorPanel.setLayout(new GridLayout(1, 3));

		colorInputField01.addKeyListener(this);
		colorInputField02.addKeyListener(this);
		colorInputField03.addKeyListener(this);
		colorPanel.add(colorInputField01);
		colorPanel.add(colorInputField02);
		colorPanel.add(colorInputField03);

		this.setLayout(baseLayout);

		// create layout for this panel
		this.setLayout(new GridLayout(1, 4)); // TODO: make the springlayout

		// add components to this panel
		this.add(functionInputField);
		this.add(rangeInputField01);
		this.add(rangeInputField02);
		this.add(colorPanel);
	}

	/*
	 * GETTERS
	 */

	/**
	 * returns the opposite variable, x-->y, y-->x
	 * 
	 * @param variable x or y
	 * @return y or x depending on {@code variable}
	 */
	private char getOtherVariable(char variable) {

		switch (variable) {
		case 'x':
			return 'y';
		case 'y':
			return 'x';
		default:
			System.err.println("functionVariable can only be 'x' or 'y': " + variable);
			return 'x';
		}
	}

	/*
	 * SETTERS
	 */

	/*
	 * IMPLEMENTED METHODS
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {

//		System.out.println("\" + e.getKeyCode() + "\" pressed");
		switch (e.getKeyCode()) {
		case 10: // ENTER
			readAndAddFunction();
			break;
		default: // update colorpreview
			updatePreviewColor();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
