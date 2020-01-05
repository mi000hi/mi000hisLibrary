package canvasExtensions;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Spring;
import javax.swing.SpringLayout;

import canvas.Canvas2D;
import functions.Function2D;

public class Canvas2DFunctionModifier extends JPanel implements Runnable, ActionListener {

	/*
	 * final variables
	 */
	private final ArrayList<Function2D> functions2D;
	private final Font LIST_FONT = new Font("Ubuntu", Font.PLAIN, 30);
	private final Point JPANEL_PADDING = new Point(10, 10);
	private final int COMPONENT_VERTICAL_DISTANCE = 10;
	private final int COMPONENT_HEIGHT = 50;
	private final int LABEL01_WIDTH, LABEL02_WIDTH, LABEL03_WIDTH, CHECKBOX_WIDTH;
	private final int[] COMPONENT_HORIZONTAL_POSITION;

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
		LABEL02_WIDTH = 200;
		LABEL03_WIDTH = 200;
		CHECKBOX_WIDTH = 40;
		COMPONENT_HORIZONTAL_POSITION = new int[4];
		COMPONENT_HORIZONTAL_POSITION[0] = JPANEL_PADDING.x;
		COMPONENT_HORIZONTAL_POSITION[1] = COMPONENT_HORIZONTAL_POSITION[0] + CHECKBOX_WIDTH;
		COMPONENT_HORIZONTAL_POSITION[2] = COMPONENT_HORIZONTAL_POSITION[1] + LABEL01_WIDTH;
		COMPONENT_HORIZONTAL_POSITION[3] = COMPONENT_HORIZONTAL_POSITION[2] + LABEL02_WIDTH;

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
				
				componentXIndex ++;
			}
			componentY += COMPONENT_VERTICAL_DISTANCE;
			componentY += COMPONENT_HEIGHT;
			componentXIndex = 0;
		}

		// repaint panel
		this.revalidate();
		this.repaint();
	}

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
			data[dataIndex++] = new JLabel("[ " + range[0] + ", " + range[1] + " ]");

			Color color = f.getColor();
			data[dataIndex++] = new JLabel("[ " + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + " ]");
			
			listData.add(data);
		}
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
		
		if(e.getSource() instanceof JCheckBox) {
			JCheckBox j = (JCheckBox) e.getSource();
			functions2D.get(Integer.parseInt(j.getName())).setVisible(j.isSelected());
			updateListData();
			canvas2d.repaint();
		}
	}

}
