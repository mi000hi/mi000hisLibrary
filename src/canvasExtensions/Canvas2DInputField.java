package canvasExtensions;

import canvas.Canvas2D;
import functions.Function2D;
import functions.VectorField2D;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class Canvas2DInputField extends JPanel implements KeyListener, ActionListener {

	/*
	 * final variables
	 */
	private final Canvas2D canvas;
	private final Font TEXTFIELD_FONT = new Font("Ubuntu", Font.PLAIN, 40);
	private final Font TITLE_FONT = new Font("Ubuntu", Font.BOLD, 60);

	/*
	 * other variables
	 */
	private JTextField functionInputField, vectorFieldInputField;
	private JTextField functionRangeInputField01, functionRangeInputField02;
	private JTextField vectorFieldRangeInputField01, vectorFieldRangeInputField02, vectorFieldRangeInputField03, vectorFieldRangeInputField04;
	private JTextField functionColorInputField01, functionColorInputField02, functionColorInputField03;
	private JTextField vectorFieldColorInputField01, vectorFieldColorInputField02, vectorFieldColorInputField03;
	private JPanel functionColorPanel, vectorFieldColorPanel;
	private SpringLayout baseLayout;

	private JPanel titlePanel;
	private JPanel functionInputPanel;
	private JPanel vectorFieldInputPanel;
	private JLabel titleLabel;

	private JComboBox inputList;

	/*
	 * CONSTRUCTORS
	 */

	public Canvas2DInputField(Canvas2D canvas) {

		super();

		this.canvas = canvas;

		// initialize variables
		functionInputField = new JTextField("y = 5+sin( 3*x )");
		vectorFieldInputField = new JTextField("z");
		functionRangeInputField01 = new JTextField("-3");
		functionRangeInputField02 = new JTextField("5");
		vectorFieldRangeInputField01 = new JTextField("-3");
		vectorFieldRangeInputField02 = new JTextField("5");
		vectorFieldRangeInputField03 = new JTextField("-3");
		vectorFieldRangeInputField04 = new JTextField("5");
		functionColorInputField01 = new JTextField("0");
		functionColorInputField02 = new JTextField("123");
		functionColorInputField03 = new JTextField("255");
		vectorFieldColorInputField01 = new JTextField("0");
		vectorFieldColorInputField02 = new JTextField("123");
		vectorFieldColorInputField03 = new JTextField("255");

		functionInputField.setFont(TEXTFIELD_FONT);
		vectorFieldInputField.setFont(TEXTFIELD_FONT);
		functionRangeInputField01.setFont(TEXTFIELD_FONT);
		functionRangeInputField02.setFont(TEXTFIELD_FONT);
		vectorFieldRangeInputField01.setFont(TEXTFIELD_FONT);
		vectorFieldRangeInputField02.setFont(TEXTFIELD_FONT);
		vectorFieldRangeInputField03.setFont(TEXTFIELD_FONT);
		vectorFieldRangeInputField04.setFont(TEXTFIELD_FONT);
		functionColorInputField01.setFont(TEXTFIELD_FONT);
		functionColorInputField02.setFont(TEXTFIELD_FONT);
		functionColorInputField03.setFont(TEXTFIELD_FONT);
		vectorFieldColorInputField01.setFont(TEXTFIELD_FONT);
		vectorFieldColorInputField02.setFont(TEXTFIELD_FONT);
		vectorFieldColorInputField03.setFont(TEXTFIELD_FONT);

		functionColorPanel = new JPanel();
		functionColorPanel.setOpaque(true);
		functionColorPanel.setBackground(readColor());
		vectorFieldColorPanel = new JPanel();

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
		range[0] = Double.parseDouble(functionRangeInputField01.getText());
		range[1] = Double.parseDouble(functionRangeInputField02.getText());
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

		if(inputList == null || inputList.getSelectedIndex() == 0) {
		try {
			r = Integer.parseInt(functionColorInputField01.getText().replaceAll("\\s", ""));
		} catch (NumberFormatException nfe) {
			// not important, just let variables on 0
		}
		try {
			g = Integer.parseInt(functionColorInputField02.getText().replaceAll("\\s", ""));
		} catch (NumberFormatException nfe) {
			// not important, just let variables on 0
		}
		try {
			b = Integer.parseInt(functionColorInputField03.getText().replaceAll("\\s", ""));
		} catch (NumberFormatException nfe) {
			// not important, just let variables on 0
		}
		} else {
			
			try {
				r = Integer.parseInt(vectorFieldColorInputField01.getText().replaceAll("\\s", ""));
			} catch (NumberFormatException nfe) {
				// not important, just let variables on 0
			}
			try {
				g = Integer.parseInt(vectorFieldColorInputField02.getText().replaceAll("\\s", ""));
			} catch (NumberFormatException nfe) {
				// not important, just let variables on 0
			}
			try {
				b = Integer.parseInt(vectorFieldColorInputField03.getText().replaceAll("\\s", ""));
			} catch (NumberFormatException nfe) {
				// not important, just let variables on 0
			}
			
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

		if(inputList.getSelectedIndex() == 0) {
		functionColorInputField01.setBackground(previewColor);
		functionColorInputField02.setBackground(previewColor);
		functionColorInputField03.setBackground(previewColor);
		} else {
			vectorFieldColorInputField01.setBackground(previewColor);
			vectorFieldColorInputField02.setBackground(previewColor);
			vectorFieldColorInputField03.setBackground(previewColor);
		}

		this.repaint();
	}

	private void createGui() {

		baseLayout = new SpringLayout();

		// titlepanel
		titlePanel = new JPanel();
		titlePanel.setLayout(new GridLayout(1, 2));

		titleLabel = new JLabel("add a new ");
		titleLabel.setFont(TITLE_FONT);

		inputList = new JComboBox(new String[] { "Function2D", "VectorField2D" });
		inputList.setSelectedIndex(0);
		inputList.setFont(TITLE_FONT);
		inputList.addActionListener(this);

		titlePanel.add(titleLabel);
		titlePanel.add(inputList);

		// functioninputpanel
		functionInputPanel = new JPanel();
		functionInputPanel.setLayout(new GridLayout(1, 4));

		functionColorPanel = new JPanel();
		functionColorPanel.setOpaque(true);
		functionColorPanel.setBackground(new Color(0, 0, 0));
		functionColorPanel.setLayout(new GridLayout(1, 3));

		functionColorInputField01.addKeyListener(this);
		functionColorInputField02.addKeyListener(this);
		functionColorInputField03.addKeyListener(this);
		functionColorPanel.add(functionColorInputField01);
		functionColorPanel.add(functionColorInputField02);
		functionColorPanel.add(functionColorInputField03);

		functionInputPanel.add(functionInputField);
		functionInputPanel.add(functionRangeInputField01);
		functionInputPanel.add(functionRangeInputField02);
		functionInputPanel.add(functionColorPanel);

		// vectorfieldinputpanel
		vectorFieldInputPanel = new JPanel();
		vectorFieldInputPanel.setLayout(new GridLayout(1, 6));

		vectorFieldColorPanel = new JPanel();
		vectorFieldColorPanel.setOpaque(true);
		vectorFieldColorPanel.setBackground(new Color(0, 0, 0));
		vectorFieldColorPanel.setLayout(new GridLayout(1, 3));

		vectorFieldColorInputField01.addKeyListener(this);
		vectorFieldColorInputField02.addKeyListener(this);
		vectorFieldColorInputField03.addKeyListener(this);
		vectorFieldColorPanel.add(vectorFieldColorInputField01);
		vectorFieldColorPanel.add(vectorFieldColorInputField02);
		vectorFieldColorPanel.add(vectorFieldColorInputField03);

		vectorFieldInputPanel.add(vectorFieldInputField);
		vectorFieldInputPanel.add(vectorFieldRangeInputField01);
		vectorFieldInputPanel.add(vectorFieldRangeInputField02);
		vectorFieldInputPanel.add(vectorFieldRangeInputField03);
		vectorFieldInputPanel.add(vectorFieldRangeInputField04);
		vectorFieldInputPanel.add(vectorFieldColorPanel);

		this.setLayout(new GridLayout(2, 1));
		this.add(titlePanel);
		this.add(functionInputPanel);

		// TODO: create all inputpanels for functions / vectorfields and add only the
		// one that is requested...
	}

	private void updateGui() {

		this.removeAll();

//		this.setLayout(new GridLayout(2, 1));
		this.add(titlePanel);

//		System.out.println("selected index = " + inputList.getSelectedIndex());
		if (inputList.getSelectedIndex() == 0) {
			this.add(functionInputPanel);
		} else {
			this.add(vectorFieldInputPanel);
		}

		this.revalidate();
		this.repaint();

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
		
		

	}

	@Override
	public void keyPressed(KeyEvent e) {

//		System.out.println("\" + e.getKeyCode() + "\" pressed");
		switch (e.getKeyCode()) {
		case 10: // ENTER
			if(inputList.getSelectedIndex() == 0) {
				readAndAddFunction();
			} else {
				readAndAddVectorField();
			}
			break;
		default: // update colorpreview
//			updatePreviewColor();
		}
	}

	private void readAndAddVectorField() {
		
		String vectorFieldInputString = vectorFieldInputField.getText().replaceAll("\\s", "").replaceAll("x", "z").replaceAll("y", "z");
		double[] range = new double[4];
		Color vectorFieldColor;
		
		// gridlinedistance
		double gridLineDistance = 1;

		// get the functionrange
		range[0] = Double.parseDouble(vectorFieldRangeInputField01.getText());
		range[1] = Double.parseDouble(vectorFieldRangeInputField02.getText());
		range[2] = Double.parseDouble(vectorFieldRangeInputField03.getText());
		range[3] = Double.parseDouble(vectorFieldRangeInputField04.getText());
 		if (range[0] > range[1]) {
			System.err.println("given range is in wrong order. swapping now. was: " + range[0] + " - " + range[1]);
			double tmp = range[0];
			range[0] = range[1];
			range[1] = tmp;
		}
 		if (range[2] > range[3]) {
			System.err.println("given range is in wrong order. swapping now. was: " + range[2] + " - " + range[3]);
			double tmp = range[2];
			range[2] = range[3];
			range[3] = tmp;
		}

		// get the functioncolor
		vectorFieldColor = readColor();

		System.out.println("adding new VectorField2D(" + vectorFieldInputString + ", " + gridLineDistance + ", " + range
				+ ", " + vectorFieldColor.toString());
		canvas.addVectorField2D(new VectorField2D(vectorFieldInputString, gridLineDistance, range, vectorFieldColor));

		// repaint the canvas with the new function
		canvas.repaint();
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		updatePreviewColor();

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		System.out.println("\"" + e.getActionCommand() + "\" action performed");
		switch (e.getActionCommand()) {

		case "comboBoxChanged":
			updateGui();
			break;

		}

	}

}
