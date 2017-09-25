/*
	This file is part of FreeJ2ME.

	FreeJ2ME is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	FreeJ2ME is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with FreeJ2ME.  If not, see http://www.gnu.org/licenses/
*/
package org.recompile.mobile;

import java.net.URL;
import java.util.Arrays;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.GameCanvas;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;

public class PlatformImage extends javax.microedition.lcdui.Image
{
	protected BufferedImage canvas;
	protected PlatformGraphics gc;

	public BufferedImage getCanvas()
	{
		return canvas;
	}

	public PlatformGraphics getGraphics()
	{
		return gc;
	}

	protected void createGraphics()
	{
		gc = new PlatformGraphics(this);
	}

	public PlatformImage(int Width, int Height)
	{
		// Create blank Image
		width = Width;
		height = Height;

		canvas = new BufferedImage(Width, Height, BufferedImage.TYPE_INT_ARGB);
		createGraphics();

		gc.setColor(0xFFFFFF);
		gc.fillRect(0, 0, width, height);

		platformImage = this;
	}

	public PlatformImage(String name)
	{
		// Create Image from resource name
		// System.out.println("Image From Resource Name");
		BufferedImage temp;

		InputStream stream = Mobile.getPlatform().loader.getMIDletResourceAsStream(name);

		if(stream==null)
		{
			System.out.println("Couldn't Load Image Stream (can't find "+name+")");
		}
		else
		{
			try
			{
				temp = ImageIO.read(stream);
				width = (int)temp.getWidth();
				height = (int)temp.getHeight();

				canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				createGraphics();

				gc.drawImage(temp, 0, 0);
			}
			catch (Exception e)
			{
				System.out.println("Couldn't Load Image Stream " + name);
				e.printStackTrace();
			}
		}
		platformImage = this;
	}

	public PlatformImage(InputStream stream)
	{
		// Create Image from InputStream
		// System.out.println("Image From Stream");
		BufferedImage temp;
		try
		{
			temp = ImageIO.read(stream);
			width = (int)temp.getWidth();
			height = (int)temp.getHeight();

			canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			createGraphics();

			gc.drawImage(temp, 0, 0);
		}
		catch(Exception e)
		{
			System.out.println("Couldn't Load Image Stream");
		}

		platformImage = this;
	}

	public PlatformImage(Image source)
	{
		// Create Image from Image
		width = source.platformImage.width;
		height = source.platformImage.height;

		canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		createGraphics();

		gc.drawImage(source.platformImage, 0, 0);

		platformImage = this;
	}

	public PlatformImage(byte[] imageData, int imageOffset, int imageLength)
	{
		// Create Image from Byte Array Range (Data is PNG, JPG, etc.)
		try
		{
			byte[] range = Arrays.copyOfRange(imageData, imageOffset, imageOffset+imageLength);
			InputStream stream = new ByteArrayInputStream(range);

			BufferedImage temp;

			temp = ImageIO.read(stream);
			width = (int)temp.getWidth();
			height = (int)temp.getHeight();

			canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			createGraphics();

			gc.drawImage(temp, 0, 0);
		}
		catch(Exception e)
		{
			System.out.println("Couldn't Load Image Data From Byte Array");

			//System.out.println(e.getMessage());
			//e.printStackTrace();
		}

		platformImage = this;
	}

	public PlatformImage(int[] rgb, int Width, int Height, boolean processAlpha)
	{
		// createRGBImage (Data is ARGB pixel data)
		width = Width;
		height = Height;

		if(width < 1) { width = 1; }
		if(height < 1) { height = 1; }

		canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		createGraphics();

		gc.drawRGB(rgb, 0, width, 0, 0, width, height, true);

		platformImage = this;
	}

	public PlatformImage(Image image, int x, int y, int Width, int Height, int transform)
	{
		// Create Image From Sub-Image, Transformed //
		BufferedImage sub = image.platformImage.canvas.getSubimage(x, y, Width, Height);

		canvas = transformImage(sub, transform);
		createGraphics();

		width = (int)canvas.getWidth();
		height = (int)canvas.getHeight();

		platformImage = this;
	}

	public void getRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height)
	{
		canvas.getRGB(x, y, width, height, rgbData, offset, scanlength);
	}

	public static BufferedImage transformImage(BufferedImage image, int transform)
	{
		int Width = (int)image.getWidth();
		int Height = (int)image.getHeight();
		int width = Width;
		int height = Height;

		AffineTransform af = new AffineTransform();

		switch (transform)
		{
			case Sprite.TRANS_MIRROR:
				af.scale(-1, 1);
				af.translate(-Width, 0);
				//System.out.println("Mirror");
			break;

			case Sprite.TRANS_MIRROR_ROT180:
				af.scale(-1, 1);
				af.translate(-Width, 0);
				af.rotate(Math.PI, Width/2, Height/2);
				//System.out.println("Mirror_180");
			break;

			case Sprite.TRANS_MIRROR_ROT270:
				af.scale(-1, 1);
				af.translate(-Width, 0);
				af.rotate(Math.PI+Math.PI/2, Width/2, Height/2);
				width = Height;
				height = Width;
				//System.out.println("Mirror_270");
			break;

			case Sprite.TRANS_MIRROR_ROT90:
				af.scale(-1, 1);
				af.translate(-Width, 0);
				width = Height;
				height = Width;
				af.rotate(Math.PI/2, Width/2, Height/2);
				//System.out.println("Mirror_90");
			break;

			case Sprite.TRANS_ROT180:
				af.rotate(Math.PI, Width/2, Height/2);
				//System.out.println("Rot_180");
			break;

			case Sprite.TRANS_ROT270:
				af.rotate(Math.PI+Math.PI/2, Width/2, Height/2);
				width = Height;
				height = Width;
				//System.out.println("Rot_270");
			break;

			case Sprite.TRANS_ROT90:
				af.rotate(Math.PI/2, Width/2, Height/2);
				width = Height;
				height = Width;
				//System.out.println("Rot_90");
			break;
		}
		BufferedImage transimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gc = transimage.createGraphics();
		gc.drawImage(image, af, null);

		return transimage;
	}
}