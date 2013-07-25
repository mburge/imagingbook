package xC_Anhang;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.PlugIn;

public class Generic_Dialog_Example implements PlugIn {
	static String title = "Untitled";
	static int width = 512;
	static int height = 512;

	public void run(String arg) {
		
		GenericDialog gd = new GenericDialog("New Image");
		gd.addStringField("Title:", title);
		gd.addNumericField("Width:", width, 0);
		gd.addNumericField("Height:", height, 0);
		gd.showDialog();
		
		if (gd.wasCanceled())
			return;
		
		title = gd.getNextString();
		width = (int) gd.getNextNumber();
		height = (int) gd.getNextNumber();

		ImagePlus imp = NewImage.createByteImage(title, width, height, 1,
				NewImage.FILL_WHITE);
		imp.show();
	}
}
