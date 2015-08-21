package randomj;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;

import imagescience.image.FloatImage;
import imagescience.image.Image;
import imagescience.random.Randomizer;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class RJ_Uniform implements PlugIn, WindowListener {
	
	private static String min = "0.0";
	private static String max = "1.0";
	
	private static final String[] inserts = { "Additive", "Multiplicative" };
	private static int insert = 0;
	
	private static Point position = new Point(-1,-1);
	
	public void run(String arg) {
		
		if (!RJ.check()) return;
		final ImagePlus input = RJ.imageplus();
		if (input == null) return;
		
		RJ.log(RJ.name()+" "+RJ.version()+": Uniform");
		
		RJ.options();
		
		GenericDialog gd = new GenericDialog(RJ.name()+": Uniform");
		gd.addStringField("Min:",min);
		gd.addStringField("Max:",max);
		gd.addPanel(new Panel(),GridBagConstraints.EAST,new Insets(0,0,0,0));
		gd.addChoice("Insertion:",inserts,inserts[insert]);
		
		if (position.x >= 0 && position.y >= 0) {
			gd.centerDialog(false);
			gd.setLocation(position);
		} else gd.centerDialog(true);
		gd.addWindowListener(this);
		gd.showDialog();
		
		if (gd.wasCanceled()) return;
		
		min = gd.getNextString();
		max = gd.getNextString();
		insert = gd.getNextChoiceIndex();
		
		(new RJUniform()).run(input,min,max,insert);
	}
	
	public void windowActivated(final WindowEvent e) { }
	
	public void windowClosed(final WindowEvent e) {
		
		position.x = e.getWindow().getX();
		position.y = e.getWindow().getY();
	}
	
	public void windowClosing(final WindowEvent e) { }
	
	public void windowDeactivated(final WindowEvent e) { }
	
	public void windowDeiconified(final WindowEvent e) { }
	
	public void windowIconified(final WindowEvent e) { }
	
	public void windowOpened(final WindowEvent e) { }
	
}

class RJUniform { // To avoid exceptions when ImageScience is not installed
	
	void run(final ImagePlus input, final String min, final String max, final int insert) {
		
		try {
			double minValue, maxValue;
			try { minValue = Double.parseDouble(min); }
			catch (Exception e) { throw new IllegalArgumentException("Invalid minimum value"); }
			try { maxValue = Double.parseDouble(max); }
			catch (Exception e) { throw new IllegalArgumentException("Invalid maximum value"); }
			int insertValue = Randomizer.ADDITIVE;
			switch (insert) {
				case 0: insertValue = Randomizer.ADDITIVE; break;
				case 1: insertValue = Randomizer.MULTIPLICATIVE; break;
			}
			
			final Image image = Image.wrap(input);
			final Randomizer randomizer = new Randomizer();
			randomizer.messenger.log(RJ_Options.log);
			randomizer.progressor.display(RJ_Options.pgs);
			final Image output = RJ_Options.floatout ? 
				randomizer.uniform(new FloatImage(image),minValue,maxValue,insertValue,false) :
				randomizer.uniform(image,minValue,maxValue,insertValue,true);
			RJ.show(output,input);
			
		} catch (OutOfMemoryError e) {
			RJ.error("Not enough memory for this operation");
			
		} catch (IllegalArgumentException e) {
			RJ.error(e.getMessage());
			
		} catch (Throwable e) {
			RJ.error("An unidentified error occurred while running the plugin");
			
		}
	}
	
}
