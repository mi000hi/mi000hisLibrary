package canvasExtensions;

import canvas.Canvas2D;
import functions.Function2D;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class Canvas2DInputField extends JPanel implements KeyListener {

	/*
	 * final variables
	 */
	private final Canvas2D canvas;

	/*
	 * other variables
	 */
	private JTextField functionInputField;
	private JTextField rangeInputField01, rangeInputField02;
	private JTextField colorInputField01, colorInputField02, colorInputField03;
	private JPanel colorPanel;

	/*
	 * CONSTRUCTORS
	 */

	public Canvas2DInputField(Canvas2D canvas) {

		super();

		this.canvas = canvas;

		// initialize variables
		functionInputField = new JTextField("ex. y = 5+sin( 3x )");
		rangeInputField01 = new JTextField("ex. -3");
		rangeInputField02 = new JTextField("ex. 5");
		colorInputField01 = new JTextField("ex. 0");
		colorInputField02 = new JTextField("ex. 123");
		colorInputField03 = new JTextField("ex. 255");

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
		functionVariable = functionInputString[0].charAt(0);
		if (functionVariable != 'y' && functionVariable != 'x') {
			System.err.println("functionVariable can only be 'x' or 'y': " + functionVariable);
		}

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
			r = Integer.parseInt(colorInputField01.getText());
			g = Integer.parseInt(colorInputField02.getText());
			b = Integer.parseInt(colorInputField03.getText());
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

		colorPanel.setBackground(previewColor);
	}
	
	private void createGui() {
		
	}

	/*
	 * GETTERS
	 */

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
