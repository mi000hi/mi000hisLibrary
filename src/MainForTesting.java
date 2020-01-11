import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JFrame;

import canvas.*;
import canvasExtensions.Canvas2DFunctionModifier;
import canvasExtensions.Canvas2DInputField;
import complexNumbers.*;
import functions.Function2D;

public class MainForTesting {

	/**
	 * this main class is used to test the functions of this library
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		/*
		 * create canvas to paint functions on
		 */
		Canvas2D canvas2d = new Canvas2D();
		Canvas2DInputField inputFieldForCanvas = new Canvas2DInputField(canvas2d);
		Runnable functionModifier = new Canvas2DFunctionModifier(canvas2d);
		Thread functionModifierThread = new Thread(functionModifier);
		functionModifierThread.start();

		/*
		 * create the functions to be painted
		 */
//		Function2D function01 = new Function2D("0-sqrt(-z)", 'x', new double[] {-2, 0}, Color.yellow);
//		Function2D function02 = new Function2D("sqrt(z)", 'x', new double[] {0, 2}, Color.red);
		Function2D function03 = new Function2D("1/x", 'x', new double[] { -5, 5 }, Color.green);
//		Function2D function04 = new Function2D("sin(z)/cos(z)", 'x', new double[] {-4, 4}, Color.blue);
//		Function2D function05 = new Function2D("sin(z)/cos(z)", 'y', new double[] {-4, 4}, Color.orange);

		/*
		 * give the functions to the canvas
		 */
//		canvas2d.addFunction2D(function01);
//		canvas2d.addFunction2D(function02);
		canvas2d.addFunction2D(function03);
//		canvas2d.addFunction2D(function04);
//		canvas2d.addFunction2D(function05);

		/*
		 * create a jframe and add canvas to it
		 */
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(2, 2));

		frame.add(canvas2d);
		frame.add(inputFieldForCanvas);
		frame.add((Canvas2DFunctionModifier) functionModifier);

		canvas2d.setOutputArea(new int[] { -5, 5, -5, 5 });

		frame.setVisible(true);
		canvas2d.repaint();

		/*
		 * changing functions
		 */
		

		/*
		 * update canvas
		 */
		canvas2d.repaint();
		frame.repaint();

	}

}
