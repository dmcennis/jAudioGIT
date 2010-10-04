/*
 * @(#)PlotDisplay.java	0.5	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package jAudioFeatureExtractor.GeneralTools;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import javax.swing.*;
import javax.swing.border.*;


/**
 * This class allows users to plot from 1 to 5 different data sets on either
 * the same graph or on vertically stacked separate plots. These data sets
 * may be of the same or different length.
 *
 * <p>The user may adjust the domain and range of the plots by adjusting the
 * values in the Minimum X, Maximum X, Minimum Y and Maximum Y text fields.
 * The user may also adjust the intervals at which the tics on the axes occur
 * by adjusting the values in the X Tic Interval and Y Tic Interval text fields.
 * Any changes made this way will be manifested when the Replot button is pressed.
 *
 * <p>This code was inspired by the Graph01 class of R. G. Baldwin.
 *
 * @author Cory McKay
 */
public class PlotDisplay
	extends JFrame
	implements ActionListener
{
	/* FIELDS ******************************************************************/
	
	
	static final long serialVersionUID = 1;
	
	// The range and domain of plot axes and their default values
	private	double		x_min;
	private	double		x_max;
	private	double		y_min;
	private	double		y_max;

	// The distance between tic marks on the axes and their default values.
	private	double		x_tic_interval;
	private	double		y_tic_interval;

	// Text fields for entering plotting parameters and their default values
	private	JTextField	x_min_tf;
	private	JTextField	x_max_tf;
	private	JTextField	y_min_tf;
	private	JTextField	y_max_tf;
	private	JTextField	x_tic_interval_tf;
	private	JTextField	y_tic_interval_tf;

	// The canvases where individual plots are held
	private	Canvas[]	canvases;

	// The data points that are to be graphed. The first indice specifies the
	// data set and the second indice specifies the x-coordinate indice.
	private double[][]	data_points;
	
	// The x-coordinates corresponding to the indices of the data points in the
	// data_points field. The indices both correspond to those of data_points.
	private double[][]	data_x_coordinates;


	/* CONSTRUCTOR *************************************************************/


	/**
	 * Set up the frame and graph the data.
	 *
	 * @param data_points_to_plot			The data points that are to be graphed. The 
	 *										first indice specifies the data set and the
	 *										second indice corresponds to the x-coordinate
	 *										indice. Each data set may be of a different length
	 *										if desired.
	 * @param data_x_coordinates_to_plot	The x-coordinates corresponding to the second indices
	 *										of the data points in the data_points parameter.
	 *										The indices both correspond to those of data_points.
	 *										This parameter may be null if the indices of
	 *										data_points themselves are to serve as x-coordinates.
	 * @param plot_on_one_graph				A value of true means that the data sets in
	 *										data_points will all be planted on one graph.
	 *										A value of false means that they will be plotted
	 *										on separate graphs that will be individually
	 *										created.
	 * @param plot_name						The title to give the set of graphs. A value of null
	 *										will result in a default value.
	 * @param quit_on_exit					If this is true, the JRE is closed when the exit box
	 *										is pressed. If it is false, this window is only
	 *										hidden and cleared.
	 * @throws Exception					Throws an informative exception if the given  
	 *										parameters are invalid.
	 */
	public PlotDisplay( double[][] data_points_to_plot,
	                    double[][] data_x_coordinates_to_plot,
	                    boolean plot_on_one_graph,
	                    String plot_name,
						boolean quit_on_exit )
		throws Exception
	{
		// Assign data_points_to_plot and data_x_coordinates_to_plot to fields
		data_points = data_points_to_plot;
		data_x_coordinates = data_x_coordinates_to_plot;

		// Check validity of input parameters
		if (data_points == null)
			throw new Exception("No data points were provided to plot.");
		if (data_points.length > 5)
			throw new Exception("More than five data sets specified.");
		for (int i = 0; i < data_points.length; i++)
			if (data_points[i] == null)
				throw new Exception("Data set " + i + " is empty.");
		if (data_x_coordinates != null)
		{
			if (data_points.length != data_x_coordinates.length)
				throw new Exception( "There are a different number of data sets\n" +
				                     "and data set labels." );
			for (int i = 0; i < data_points.length; i++)
				if (data_points[i].length != data_x_coordinates[i].length)
					throw new Exception( "Data set " + i + " has a different number\n" +
					                     "of data points and x-coodinate labels." );
		}

		// Construct data_x_coordinates if it was not provided
		if (data_x_coordinates == null)
		{
			data_x_coordinates = new double[data_points.length][];
			for (int i = 0; i < data_points.length; i++)
			{
				data_x_coordinates[i] = new double[data_points[i].length];
				for (int j = 0; j < data_points[i].length; j++)
					data_x_coordinates[i][j] = j;
			}
		}

		// Find the proper limits in which to plot within
		x_min = data_x_coordinates[0][0];
		x_max = data_x_coordinates[0][0];
		y_min = data_points[0][0];
		y_max = data_points[0][0];
		for (int i = 0; i < data_points.length; i++)
			for (int j = 0; j < data_points[i].length; j++)
			{
				if (data_points[i][j] < y_min)
					y_min = data_points[i][j]; 
				if (data_points[i][j] > y_max)
					y_max = data_points[i][j];
				if (data_x_coordinates[i][j] < x_min)
					x_min = data_x_coordinates[i][j];
				if (data_x_coordinates[i][j] > x_max)
					x_max = data_x_coordinates[i][j];
			}
		if (y_min < 0.0)
			y_min = y_min - Math.abs(y_min * 0.05);
		y_max = y_max + Math.abs(y_max * 0.05);

		// Set the tic intervals
		x_tic_interval = 10;
		y_tic_interval = 0.1;

		// Set the text fields
		x_min_tf = new JTextField("" + x_min, 7);
		x_max_tf = new JTextField("" + x_max, 7);
		y_min_tf = new JTextField("" + y_min, 7);
		y_max_tf = new JTextField("" + y_max, 7);
		x_tic_interval_tf = new JTextField("" + x_tic_interval, 7);
		y_tic_interval_tf = new JTextField("" + y_tic_interval, 7);
		
		//Set the colors
		Color blue = new Color((float)0.75,(float)0.85,(float)1.0);
		this.getContentPane().setBackground(blue);
		
		// Create the control panel and give it a border
		JPanel control_panel = new JPanel();
		control_panel.setLayout(new GridLayout(2, 4));
		control_panel.setBorder(new EtchedBorder());
		control_panel.setBackground(this.getContentPane().getBackground());

		// Set up button for replotting the graph
		JButton replot_button = new JButton("Replot");
		replot_button.addActionListener(this);

		// Populate each panel with a label and a text field
		JPanel pan0 = new JPanel();
		pan0.add(new JLabel("Minimum X"));
		pan0.add(x_min_tf);
		pan0.setBackground(this.getContentPane().getBackground());
		JPanel pan1 = new JPanel();
		pan1.add(new JLabel("Maximum X"));
		pan1.add(x_max_tf);
		pan1.setBackground(this.getContentPane().getBackground());
		JPanel pan2 = new JPanel();
		pan2.add(new JLabel("Minimum Y"));
		pan2.add(y_min_tf);
		pan2.setBackground(this.getContentPane().getBackground());
		JPanel pan3 = new JPanel();
		pan3.add(new JLabel("Maximum Y"));
		pan3.add(y_max_tf);
		pan3.setBackground(this.getContentPane().getBackground());
		JPanel pan4 = new JPanel();
		pan4.add(new JLabel("X Tic Interval"));
		pan4.add(x_tic_interval_tf);
		pan4.setBackground(this.getContentPane().getBackground());
		JPanel pan5 = new JPanel();
		pan5.add(new JLabel("Y Tic Interval"));
		pan5.add(y_tic_interval_tf);
		pan5.setBackground(this.getContentPane().getBackground());

		// Add the control parameters and replot button to the control panel
		control_panel.add(pan0);
		control_panel.add(pan1);
		control_panel.add(pan2);
		control_panel.add(pan3);
		control_panel.add(pan4);
		control_panel.add(pan5);
		control_panel.add(new JLabel(""));
		control_panel.add(replot_button);

		//Create a panel to contain the plots.
		JPanel canvas_panel = new JPanel();
		canvas_panel.setLayout(new GridLayout(0,1));

		// Create a custom Canvas object for each data set to be plotted and
		// add it to canvas_panel.
		if (!plot_on_one_graph)
		{
			canvases = new Canvas[data_points.length];
			for(int i = 0; i < data_points.length; i++)
			{
				canvases[i] = new PlotCanvas(i, false);
				if (i % 2 == 0)
					canvases[i].setBackground(Color.WHITE);
				else
					canvases[i].setBackground(Color.LIGHT_GRAY);
				canvas_panel.add(canvases[i]);
			}
		}
		else
		{
			canvases = new Canvas[1];
			canvases[0] = new PlotCanvas(0, true);
			canvases[0].setBackground(Color.WHITE);
			canvas_panel.add(canvases[0]);
		}

		// Add the panels to the overall frame
		getContentPane().add(control_panel, "South");
		getContentPane().add(canvas_panel, "Center");

		//Set action when close button pressed
		if (quit_on_exit)
			setDefaultCloseOperation(EXIT_ON_CLOSE);
		else
			addWindowListener(new WindowAdapter() {
				public void windowClosing (WindowEvent e)
				{
					end();
				}
			});

		// Finalize the display
		int	frame_width = 800;
		int frame_height = 500;
		setBounds(0, 0, frame_width, frame_height);
		if (plot_name == null)
			plot_name = new String("Data Plot");
		setTitle(plot_name);
		setVisible(true);

		// Force paint on startup
		for(int i = 0; i < canvases.length; i++)
			canvases[i].repaint();
	}


	/* PUBLIC METHODS **********************************************************/


	/**
	 * React to the Replot button by re-plotting the data sets given the values
	 * entered in the text field.
	 */
	public void actionPerformed(ActionEvent evt)
	{
		// Re-set the plotting parameters based on the values entered by the user
		try
		{
			x_min = Double.parseDouble(x_min_tf.getText());
			x_max = Double.parseDouble(x_max_tf.getText());
			y_min = Double.parseDouble(y_min_tf.getText());
			y_max = Double.parseDouble(y_max_tf.getText());
			x_tic_interval = Double.parseDouble(x_tic_interval_tf.getText());
			y_tic_interval = Double.parseDouble(y_tic_interval_tf.getText());
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(null, "Invalid text option: " + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
		}

		// Repaint the canases
		for(int i = 0; i < canvases.length; i++)
			canvases[i].repaint();
	}


	/* PRIVATE METHODS *********************************************************/


	/**
	 * Effectively erases and hides this window.
	 */
	private void end()
	{
		this.setVisible(false);
		data_points = null;
		data_x_coordinates = null;
		dispose();
		canvases = null;
		System.gc();
	}


	/* INTERNAL CLASS **********************************************************/

	
	/**
	 * Each object of this class represents a canvas that displays one or more
	 * data sets. Each object includes methods for graphing the given data and
	 * formatting the axes.
	 */
	private class PlotCanvas
		extends Canvas
	{
		
		static final long serialVersionUID = 1;

		// Unit conversion scaling
		private double x_scale;
		private double y_scale;

		// The data set being dealt with
		private int data_set_index;

		// The length of tics on the axes
		private double x_tic_length;
		private double y_tic_length;

		// Whether or not to plot multiple datasets on this canvas
		private boolean plot_on_one_graph;

		
		/**
		 * Store the given parameters.
		 *
		 * @param	data_set_index		The data set in the data_points field to
		 *								plot.
		 * @param	plot_on_one_graph	Whether or not to plot multiple datasets
		 *								on this canvas
		 */
		public PlotCanvas(int data_set_index, boolean plot_on_one_graph)
		{
			this.data_set_index = data_set_index;
			this.plot_on_one_graph = plot_on_one_graph;
		}


		/**
		 * Overides the paint method. Causes data points, axes and tics to be
		 * drawn or redrawn.
		 */
		public void paint(Graphics g)
		{
			// Calculate the scale factors in the x and y dimensions
			int width = canvases[data_set_index].getWidth();
			int height = canvases[data_set_index].getHeight();
			x_scale = width / (x_max - x_min);
			y_scale = height / (y_max - y_min);
			
			// Set the size of the tic lengths
			x_tic_length = (y_max - y_min) / 35.0;
			y_tic_length = (x_max - x_min) / 100.0;

			// Set the origin
			g.translate( (int) ((0 - x_min) * x_scale), (int) ((0 - y_min) * y_scale) );

			// Draw the axes
			drawAxes(g);

			// Draw in black
			g.setColor(Color.BLACK);

			// Plot all points on separate graphs
			if (! plot_on_one_graph)
			{
				int old_x = scaleX(data_x_coordinates[data_set_index][0]);
				int old_y = scaleY(data_points[data_set_index][0]);
				for (int i = 0; i < data_points[data_set_index].length; i++)
				{
					// Get the end points for the current line segment
					int new_x = scaleX(data_x_coordinates[data_set_index][i]);
					int new_y = scaleY(data_points[data_set_index][i]);
					
					// Draw a line connecting the start point and the end point
					g.drawLine(old_x, old_y, new_x, new_y);

					// Set the old end points to the new start points
					old_x = new_x;
					old_y = new_y;
				}
			}

			// Plot all points on the same graph
			else
			{
				for (int j = 0; j < data_points.length; j++)
				{
					int old_x = scaleX(data_x_coordinates[j][0]);
					int old_y = scaleY(data_points[j][0]);
					for (int i = 0; i < data_points[j].length; i++)
					{
						// Get the end points for the current line segment
						int new_x = scaleX(data_x_coordinates[j][i]);
						int new_y = scaleY(data_points[j][i]);
						
						// Draw a line connecting the start point and the end point
						g.drawLine(old_x, old_y, new_x, new_y);

						// Set the old end points to the new start points
						old_x = new_x;
						old_y = new_y;
					}
				}
			}
		}


		/**
		 * Draw the axes and their tic marks.
		 */
		private void drawAxes(Graphics g)
		{
			// Draw the axes in red
			g.setColor(Color.RED);

			// Prepare to label the right x-axis and the top y-axis. This involves 
			// getting the width of the string for the right end of the x-axis and the 
			// height of the string for the top of the y-axis.
			String x_max_string = "" + x_max;
			char[] x_max_chars = x_max_string.toCharArray();
			FontMetrics font_metrics = g.getFontMetrics();
			Rectangle2D r2d = font_metrics.getStringBounds(x_max_chars, 0, x_max_chars.length, g);
			int label_width = (int) r2d.getWidth();
			int label_height = (int) r2d.getHeight();

			// Draw the y-axis and label it
			if (x_min <= 0.0 && x_max >= 0.0)
			{
				g.drawLine(scaleX(0.0), scaleY(y_min), scaleX(0.0), scaleY(y_max));
				g.drawString("" + y_min, scaleX(y_tic_length / 2) + 2, scaleY(y_min));
				g.drawString("" + y_max, scaleX(y_tic_length / 2) + 2, scaleY(y_max) + label_height);
			}
			else if (x_min > 0.0)
			{
				g.drawLine(scaleX(x_min), scaleY(y_min), scaleX(x_min), scaleY(y_max));
				g.drawString("" + y_min, scaleX(x_min + (y_tic_length / 2)) + 2, scaleY(y_min));
				g.drawString("" + y_max, scaleX(x_min + (y_tic_length / 2)) + 2, scaleY(y_max) + label_height);
			}
			else if (x_max < 0.0)
			{
				g.drawLine(scaleX(x_max), scaleY(y_min), scaleX(x_max), scaleY(y_max));
				g.drawString("" + y_min, scaleX(x_max - (y_tic_length / 2)) - 20, scaleY(y_min));
				g.drawString("" + y_max, scaleX(x_max - (y_tic_length / 2)) - 20, scaleY(y_max) + label_height);
			}

			// Draw the x-axis and label it
			if (y_min <= 0.0 && y_max >= 0.0)
			{
				g.drawLine(scaleX(x_min), scaleY(0.0), scaleX(x_max), scaleY(0.0));
				g.drawString("" + x_min, scaleX(x_min), scaleY(x_tic_length / 2) - 2);
				g.drawString(x_max_string, scaleX(x_max) - label_width, scaleY(x_tic_length / 2) - 2);
			}
			else if (y_min > 0.0)
			{
				g.drawLine(scaleX(x_min), scaleY(y_min) - 1, scaleX(x_max), scaleY(y_min) - 1);
				g.drawString("" + x_min, scaleX(x_min), scaleY(y_min + (x_tic_length / 2)) - 2);
				g.drawString(x_max_string, scaleX(x_max) - label_width, scaleY(y_min + (x_tic_length / 2)) - 2);
			}
			else if (y_max < 0.0)
			{
				g.drawLine(scaleX(x_min), scaleY(y_min) - 1, scaleX(x_max), scaleY(y_min) - 1);
				g.drawString("" + x_min, scaleX(x_min), scaleY(y_min + (x_tic_length / 2)) - 2);
				g.drawString(x_max_string, scaleX(x_max) - label_width, scaleY(y_min + (x_tic_length / 2)) - 2);
			}

			// Draw the tic marks on the axes
			drawXTics(g);
			drawYTics(g);
		}


		/**
		 * Draw the tic marks on the x-axis.
		 */
		private void drawXTics(Graphics g)
		{
			// Find the top and bottom coordinates of tic marks
			int top_end;
			int bottom_end;
			if (y_min <= 0.0 && y_max >= 0.0)
			{
				top_end = scaleY(x_tic_length / 2);
				bottom_end = scaleY(-x_tic_length / 2);
			}
			else
			{
				top_end = scaleY(y_min + (x_tic_length / 2));
				bottom_end = scaleY(y_min);
			}

			// Draw the x-axis tic marks
			double x_doub = 0;
			int x = 0;
			while (x_doub < x_max) // positive x-axis
			{
				x = scaleX(x_doub);
				g.drawLine(x, top_end, x, bottom_end);
				x_doub += x_tic_interval;
			}
			x_doub = 0;
			while (x_doub > x_min) // negative x-axis
			{
				x = scaleX(x_doub);
				g.drawLine(x, top_end, x, bottom_end);
				x_doub -= x_tic_interval;
			}
		}


		/**
		 * Draw the tic marks on the y-axis.
		 */
		private void drawYTics(Graphics g)
		{
			// Find the top and bottom coordinates of tic marks
			int left_end = 0;
			int right_end = 0;
			if (x_min <= 0.0 && x_max >= 0.0)
			{
				left_end = scaleX(-y_tic_length / 2);
				right_end = scaleX(y_tic_length / 2);
			}
			else if (x_min >= 0.0)
			{
				left_end = scaleX(x_min);
				right_end = scaleX(x_min + (y_tic_length / 2));
			}
			else if (x_max <= 0.0)
			{
				left_end = scaleX(x_max);
				right_end = scaleX(x_max - (y_tic_length / 2));
			}		

			// Draw the y-axis tic marks
			double y_doub = 0;
			int y = 0;
			while (y_doub < y_max) // positive x-axis
			{
				y = scaleY(y_doub);
				g.drawLine(right_end, y, left_end, y);
				y_doub += y_tic_interval;
			}
			y_doub = 0;
			while (y_doub > y_min) // negative x-axis
			{
				y = scaleY(y_doub);
				g.drawLine(right_end, y, left_end, y);
				y_doub -= y_tic_interval;
			}
		}


		/**
		 * Scales the given double by the x_scale field and converts it
		 * to an integer.
		 */
		private int scaleX(double x)
		{
			return (int) (x * x_scale);
		}

  
		/**
  		 * Scales the given double by the y_scale field and converts it
		 * to an integer. Also causes the positive direction of the y-axis
		 * to be from bottom to top.
		 */
		private int scaleY(double y)
		{
			double y_doub = (y_max + y_min) - y;
			return (int) (y_doub * y_scale);
		}
	}
}