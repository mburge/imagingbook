/*******************************************************************************
 This software is provided as a supplement to the authors' textbooks on digital
 image processing published by Springer-Verlag in various languages and editions.
 Permission to use and distribute this software is granted under the BSD 2-Clause 
 "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 Copyright (c) 2006-2013 Wilhelm Burger, Mark J. Burge. 
 All rights reserved. Visit http://www.imagingbook.com for additional details.
 ******************************************************************************/

package imagingbook.lib.image;


import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import imagingbook.lib.image.ImageAccessor;
import imagingbook.lib.image.Indexer;
import imagingbook.lib.image.OutOfBoundsStrategy;

public abstract class ImageAccessor {
	
	protected final int width;
	protected final int height;
	protected OutOfBoundsStrategy oobStrat;
	protected Indexer indexer;
	
	public static ImageAccessor create(ImageProcessor ip) {
		if (ip instanceof ByteProcessor)
			return create((ByteProcessor)ip);
		if (ip instanceof ShortProcessor)
			return create((ShortProcessor)ip);
		if (ip instanceof FloatProcessor)
			return create((FloatProcessor)ip);
		if (ip instanceof ColorProcessor)
			return create((ColorProcessor)ip);
		return null;
	}
	
	public static ImageAccessor.Byte create(ByteProcessor ip) {
		return new ImageAccessor.Byte(ip);
	}
	
	public static ImageAccessor.Short create(ShortProcessor ip) {
		return new ImageAccessor.Short(ip);
	}
	
	public static ImageAccessor.Float create(FloatProcessor ip) {
		return new ImageAccessor.Float(ip);
	}
	
	public static ImageAccessor.Rgb create(ColorProcessor ip) {
		return new ImageAccessor.Rgb(ip);
	}
	
	private ImageAccessor(ImageProcessor ip) {
		this.width = ip.getWidth();
		this.height = ip.getHeight();
		setOutOfBoundsMode(OutOfBoundsStrategy.NearestBorder);
	}
	
	public void setOutOfBoundsMode(OutOfBoundsStrategy mode) {
		this.oobStrat = mode;
		this.indexer = Indexer.create(width, height, mode);
	}
	
	// all ImageAccessor's can do this:
	public abstract float getp(int u, int v);
	public abstract void setp(int u, int v, float val);
	
	// ------------------------------------------------------------
	
	public static abstract class Gray extends ImageAccessor {
		private Gray(ImageProcessor ip) {
			super(ip);
		}
		
		public static ImageAccessor.Gray create(ImageProcessor ip) {
			if (ip instanceof ByteProcessor)
				return create((ByteProcessor)ip);
			if (ip instanceof ShortProcessor)
				return create((ShortProcessor)ip);
			if (ip instanceof FloatProcessor)
				return create((FloatProcessor)ip);
			throw new IllegalArgumentException("cannot create ImageAccessor.Gray for " + ip);
		}
	}
	
	public static abstract class Color extends ImageAccessor {
		private Color(ImageProcessor ip) {
			super(ip);
		}
		
		public static ImageAccessor.Color create(ImageProcessor ip) {
			if (ip instanceof ColorProcessor)
				return create((ColorProcessor)ip);
			throw new IllegalArgumentException("cannot create ImageAccessor.Color for " + ip);
		}
		
		public abstract void getp(int u, int v, int[] rgb);
		public abstract void setp(int u, int v, float[] rgb);
	}
	
	// ------------------------------------------------------------
	
	public static class Byte extends ImageAccessor.Gray {
		private byte[] pixels;
		private int pixelDefaultValue = 0;
		
		public Byte(ByteProcessor ip) {
			super(ip);
			this.pixels = (byte[]) ip.getPixels();
		}
		
		public float getp(int u, int v) {
			int i = indexer.getIndex(u, v);
			if (i < 0) 
				return pixelDefaultValue;
			else {
				return (0xff & pixels[i]);
			}
		}
		
		public void setp(int u, int v, float valf) {
			int val =  Math.round(valf);
			if (val < 0)
				val = 0;
			if (val > 255)
				val = 255;
			if (u >= 0 && u < width && v >= 0 && v < height) {
				pixels[width * v + u] = (byte) (0xFF & val);
			}
		}
	}
	
	// ------------------------------------------------------------
	
	public static class Short extends ImageAccessor.Gray {
		private short[] pixels;
		private int pixelDefaultValue = 0;
		
		public Short(ShortProcessor ip) {
			super(ip);
			this.pixels = (short[]) ip.getPixels();
		}
		
		public float getp(int u, int v) {
			int i = indexer.getIndex(u, v);
			if (i < 0) 
				return pixelDefaultValue;
			else
				return pixels[i];
		}
		
		public void setp(int u, int v, float valf) {	// problem?
			int val = Math.round(valf);
			if (val < 0) val = 0;
            if (val > 65535) val = 65535;
			if (u >= 0 && u < width && v >= 0 && v < height) {
				pixels[width * v + u] = (short) (0xFFFF & val);
			}
		}
	}
	
	// ------------------------------------------------------------
	
	public static class Float extends ImageAccessor.Gray {
		private float[] pixels;
		private float pixelDefaultValue = 0.0f;
		
		public Float(FloatProcessor ip) {
			super(ip);
			this.pixels = (float[]) ip.getPixels();
		}
		
		public float getp(int u, int v) {
			int i = indexer.getIndex(u, v);
			if (i < 0) 
				return pixelDefaultValue;
			else
				return pixels[i];
		}
		
		public void setp(int u, int v, float val) {
			if (u >= 0 && u < width && v >= 0 && v < height) {
				pixels[width * v + u] = val;
			}
		}
	}
	
	// ------------------------------------------------------------
	
	public static class Rgb extends ImageAccessor.Color {
		private int[] pixels;
		private int pixelDefaultValue = 0;
		
		public Rgb(ColorProcessor ip) {
			super(ip);
			this.pixels = (int[]) ip.getPixels();
		}
		
		public float getp(int u, int v) {
			int i = indexer.getIndex(u, v);
			if (i < 0) 
				return pixelDefaultValue;
			else
				return 0xffffff & pixels[i];
		}
		
		public void getp(int u, int v, int[] rgb) {
			int i = indexer.getIndex(u, v);
			int c = (i < 0) ? pixelDefaultValue : pixels[i];
			rgb[0] = (c&0xff0000)>>16;
			rgb[1] = (c&0xff00)>>8;
			rgb[2] = (c&0xff);
		}
		
		public void setp(int u, int v, float valf) {
			int val = (int) valf;
			if (u >= 0 && u < width && v >= 0 && v < height) {
				pixels[width * v + u] = 0xffffff & val;
			}
		}
		
		public void setp(int u, int v, float[] rgb) {	// 0..255 interval not checked!
			int r = Math.round(rgb[0]);
			int g = Math.round(rgb[1]);
			int b = Math.round(rgb[2]);
			if (u>=0 && u<width && v>=0 && v<height) {
				int val = ((r & 0xff)<<16) | ((g & 0xff)<<8) | b & 0xff;
				pixels[width * v + u] = val;
			}
		}
		
		public void setp(int u, int v, int[] rgb) {
			if (u >= 0 && u < width && v >= 0 && v < height) {
				int val = ((rgb[0] & 0xff) << 16) | ((rgb[1] & 0xff) << 8) | rgb[2] & 0xff;
				pixels[width * v + u] = val;
			}
		}
		
	}
	
	// ------------------------------------------------------------
	

}
