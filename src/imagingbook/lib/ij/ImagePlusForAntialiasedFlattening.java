/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.lib.ij;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.Overlay;
import ij.plugin.frame.Recorder;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

/**
 * This class extends ImagePlus to allow flattening of overlays with antialiasing.
 * As of version 1.47o (26 April 2013) implemented in ImageJ and controllable
 * by ImagePlus.setAntialiasRendering(boolean). Thus not needed any longer.
 * @author WB
 *
 */

@Deprecated
public class ImagePlusForAntialiasedFlattening extends ImagePlus {
	
	private boolean antialiasRendering = true;
	
	public void setAntialiasRendering(boolean antialiasRendering) {
		this.antialiasRendering = antialiasRendering;
	}
	
	public ImagePlusForAntialiasedFlattening(String title, ImageProcessor ip) {
		super(title, ip);
	}

    /** Returns a "flattened" version of this image, in RGB format. */
	@Override
    public ImagePlus flatten() {
        ImagePlus imp2 = createImagePlus();
        String title = "Flat_"+getTitle();
        ImageCanvas ic2 = new ImageCanvas(imp2);
        //imp2.flatteningCanvas = ic2;
        
        imp2.setRoi(getRoi());
        if (getStackSize()>1) {
            imp2.setStack(getStack());
            imp2.setSlice(getCurrentSlice());
            if (isHyperStack()) {
                imp2.setDimensions(getNChannels(),getNSlices(),getNFrames());
                imp2.setPosition(getChannel(),getSlice(),getFrame());
                imp2.setOpenAsHyperStack(true);
            }
        }
        ImageCanvas ic = getCanvas();
        Overlay overlay2 = getOverlay();
        ic2.setOverlay(overlay2);
        if (ic!=null) {
            ic2.setShowAllROIs(ic.getShowAllROIs());
            //double mag = ic.getMagnification();
            //if (mag<1.0) ic2.setMagnification(mag);
        }
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        
        // start of Wilhelm's changes: ------------------------------------------------
        Graphics2D gg = (Graphics2D)g;
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
        		(antialiasRendering) ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        gg.drawImage(getImage(), 0, 0, null);
        ic2.paint(gg);
        // end of Wilhelm's changes ------------------------------------------------
        
        //imp2.flatteningCanvas = null;
        if (Recorder.record) Recorder.recordCall("imp = IJ.getImage().flatten();");
        return new ImagePlus(title, new ColorProcessor(bi));
    }

}
