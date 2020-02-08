package canvasExtensions;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SpringLayout;

import canvas.Canvas2D;
import functions.Function2D;

public class Canvas2DFunctionModifier extends JPanel implements Runnable, ActionListener, KeyListener {

	/*
	 * final variables
	 */
	private final ArrayList<Function2D> functions2D;
	private final Font LIST_FONT = new Font("Ubuntu", Font.PLAIN, 35);
	private final Point JPANEL_PADDING = new Point(10, 10);
	private final int COMPONENT_VERTICAL_DISTANCE = 10;
	private final int COMPONENT_HEIGHT = 50;
	private final int LABEL01_WIDTH, FIELD01_WIDTH, FIELD02_WIDTH, CHECKBOX_WIDTH, BUTTON_WIDTH;
	private final int[] COMPONENT_HORIZONTAL_POSITION;
	private final ArrayList<String> TYPES;

	/*
	 * other variables
	 */
	private SpringLayout layout = new SpringLayout();
	private Canvas2D canvas2d;
	private ArrayList<JComponent[]> listData;

	/*
	 * CONSTRUCTORS
	 */

	public Canvas2DFunctionModifier(Canvas2D canvas2d) {

		super();

		this.canvas2d = canvas2d;

		// FINAL VARIABLES
		LABEL01_WIDTH = 300;
		FIELD01_WIDTH = 200;
		FIELD02_WIDTH = 200;
		CHECKBOX_WIDTH = 40;
		BUTTON_WIDTH = 100;
		COMPONENT_HORIZONTAL_POSITION = new int[5];
		COMPONENT_HORIZONTAL_POSITION[0] = JPANEL_PADDING.x;
		COMPONENT_HORIZONTAL_POSITION[1] = COMPONENT_HORIZONTAL_POSITION[0] + CHECKBOX_WIDTH;
		COMPONENT_HORIZONTAL_POSITION[2] = COMPONENT_HORIZONTAL_POSITION[1] + LABEL01_WIDTH;
		COMPONENT_HORIZONTAL_POSITION[3] = COMPONENT_HORIZONTAL_POSITION[2] + FIELD01_WIDTH;
		COMPONENT_HORIZONTAL_POSITION[4] = COMPONENT_HORIZONTAL_POSITION[3] + FIELD02_WIDTH;

		TYPES = new ArrayList<>();
		TYPES.add(0, "function");
		TYPES.add(1, "range");
		TYPES.add(2, "color");

		// initialize variables
		functions2D = canvas2d.get2DFunctions();
		updateListData();

		// set gui on this panel
		showList();
	}

	/*
	 * MAIN METHOD
	 */

	/*
	 * OTHER METHODS
	 */

	/**
	 * removes all components from this jpanel and adds new ones with the data of
	 * the functions. this method does not update the actual data of the functions.
	 */
	public void showList() {

		// remove all components from this jpanel
		this.removeAll();
		this.setLayout(new SpringLayout());

		int componentY = JPANEL_PADDING.y, componentXIndex = 0;

		// add the new list-components
		for (JComponent[] j : listData) {
			for (JComponent l : j) {
				l.setFont(LIST_FONT);

				SpringLayout.Constraints cons = layout.getConstraints(l);
				cons.setX(Spring.constant(COMPONENT_HORIZONTAL_POSITION[componentXIndex]));
				cons.setY(Spring.constant(componentY));
				this.add(l, cons);

				componentXIndex++;
			}
			componentY += COMPONENT_VERTICAL_DISTANCE;
			componentY += COMPONENT_HEIGHT;
			componentXIndex = 0;
		}

		// repaint panel
		this.revalidate();
		this.repaint();
	}

	/**
	 * updates the function data arraylist, but does not update the values on the
	 * jpanel.
	 */
	private void updateListData() {

		// clear old data
		listData = new ArrayList<>();
		int dataIndex;
		Function2D f;

		// read new data
		for (int i = 0; i < functions2D.size(); i++) {
			JComponent[] data = new JComponent[COMPONENT_HORIZONTAL_POSITION.length];
			dataIndex = 0;
			f = functions2D.get(i);

			JCheckBox box = new JCheckBox();
			box.setName(Integer.toString(i));
			box.setSelected(f.isVisible());
			box.addActionListener(this);
			data[dataIndex++] = box;

			data[dataIndex++] = new JLabel(f.getFunctionEquation());

			double[] range = f.getRange();
			JTextField rangeField = new JTextField("[ " + range[0] + ", " + range[1] + " ]");
			rangeField.setName(Integer.toString(i) + "," + TYPES.indexOf("range"));
			rangeField.setFont(LIST_FONT);
			rangeField.setSize(FIELD01_WIDTH, COMPONENT_HEIGHT);
			rangeField.setPreferredSize(rangeField.getSize());
			rangeField.setEditable(true);
			rangeField.addKeyListener(this);
			data[dataIndex++] = rangeField;

			Color color = f.getColor();
			JTextField colorField = new JTextField(
					"[ " + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + " ]");
			colorField.setName(Integer.toString(i) + "," + TYPES.indexOf("color"));
			colorField.setFont(LIST_FONT);
			colorField.setSize(FIELD02_WIDTH, COMPONENT_HEIGHT);
			colorField.setPreferredSize(rangeField.getSize());
			colorField.setEditable(true);
			colorField.addKeyListener(this);
			data[dataIndex++] = colorField;

			JButton button = new JButton("delete");
			button.setFont(LIST_FONT);
			button.setSize(BUTTON_WIDTH, COMPONENT_HEIGHT);
			button.setPreferredSize(rangeField.getSize());
			button.setName(Integer.toString(i));
			button.addActionListener(this);
			data[dataIndex++] = button;

			listData.add(data);
		}
	}

	/**
	 * reads the input text field for the range of a function, and sets updates the
	 * range. this method also forces the canvas to repaint
	 * 
	 * @param functionIndex the index of the function, whose range has been changed
	 *                      / should be updated
	 * @param newRange      the new range for the function in a stringrepresentation
	 *                      "[...,...]"
	 */
	private void readAndChangeRange(int functionIndex, String newRange) {

		// get the functionrange
		double range[] = new double[2];
		String rangeSplit[] = newRange.replace('[', ' ').replace(']', ' ').split(",");
		try {
			range[0] = Double.parseDouble(rangeSplit[0]);
		} catch (NumberFormatException nfe) {
			range[0] = 0;
		}
		try {
			range[1] = Double.parseDouble(rangeSplit[1]);
		} catch (NumberFormatException nfe) {
			range[0] = 0;
		}
		if (range[0] > range[1]) {
			System.err.println("given range is in wrong order. swapping now. was: " + range[0] + " - " + range[1]);
			double tmp = range[0];
			range[0] = range[1];
			range[1] = tmp;
		}

		// change functionrange
		functions2D.get(functionIndex).setRange(range);

	}

	/**
	 * reads the textfield of the function {@code functionIndex} and changes the
	 * color of that given function to the new one. this forces the canvas to be
	 * repainted
	 * 
	 * @param functionIndex the index of the function whose color is to be changed
	 * @param newColor      the new color for the function as a stringrepresentation
	 *                      "[r, g, b]", where 0 <= r, g, b < 256
	 */
	private void readAndChangeColor(int functionIndex, String newColor) {

		int r = 0, g = 0, b = 0;
		String[] colors = newColor.replace('[', ' ').replace(']', ' ').replaceAll("\\s", "").split(",");

		try {
			try {
				r = Integer.parseInt(colors[0]);
			} catch (NumberFormatException nfe) {
				// not important, just let variables on 0
			}
			try {
				g = Integer.parseInt(colors[1]);
			} catch (NumberFormatException nfe) {
				// not important, just let variables on 0
			}
			try {
				b = Integer.parseInt(colors[2]);
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
		} catch (IndexOutOfBoundsException i) {
			// do nothing, values stay on 0
		}

		functions2D.get(functionIndex).setColor(new Color(r, g, b));
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
	public void run() {

		while (true) {
			if (functions2D.size() != listData.size()) {
				updateListData();
				System.out.println("updated listdata");
				showList();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() instanceof JCheckBox) {
			JCheckBox j = (JCheckBox) e.getSource();
			functions2D.get(Integer.parseInt(j.getName())).setVisible(j.isSelected());
		}

		if (e.getSource() instanceof JButton) {
			JButton b = (JButton) e.getSource();
			functions2D.remove(Integer.parseInt(b.getName()));

			updateListData();
			showList();
		}

		canvas2d.repaint();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

		if (isNumberKey(e.getKeyCode())) {
			JTextField tf = (JTextField) e.getSource();
			int functionID = Integer.parseInt(tf.getName().split(",")[0]);
			int type = Integer.parseInt(tf.getName().split(",", 2)[1]);

			if (type == TYPES.indexOf("range")) { // update range
				readAndChangeRange(functionID, ((JTextField) e.getSource()).getText());
			}

			if (type == TYPES.indexOf("color")) { // update color
				readAndChangeColor(functionID, ((JTextField) e.getSource()).getText());
			}

//		updateListData();
			canvas2d.repaint();
		}

	}

	private boolean isNumberKey(int keyCode) {

//		System.out.println(keyCode);
		return !(37 <= keyCode && keyCode <= 40);
	}
}
