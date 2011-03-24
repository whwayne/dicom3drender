/*
  Copyright (C) 2009, Bioingenium Research Group
  http://www.bioingenium.unal.edu.co
  Author: Alexander Pinzon Fernandez

  JNukak3D is free software; you can redistribute it and/or modify it under
  the terms of the GNU General Public License as published by the Free
  Software Foundation; either version 2 of the License, or (at your
  option) any later version.

  JNukak3D is distributed in the hope that it will be useful, but WITHOUT
  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
  License for more details.

  You should have received a copy of the GNU General Public License
  along with JNukak3D; if not, write to the Free Software Foundation, Inc.,
  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA,
  or see http://www.gnu.org/copyleft/gpl.html
*/

package edu.co.unal.bioing.jnukak3d.view2d;

import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.util.Hashtable;

/**
 * Image consumer for image producer (jiv SliceProducer).
 * @author Alexander Pinzon Fernandez
*/
public class nkSliceImageConsumer implements ImageConsumer{
    private ColorModel color_model;
    private int width;
    private int height;
    private int hintflags;
    private byte[] pixels_byte;
    private int[] pixels_int;
    private int off;
    private int scansize;
    private int status;
    
    public nkSliceImageConsumer(){
    }

    public void imageComplete(int a_status) {
        status = a_status;
    }

    public void setColorModel(ColorModel a_model) {
        color_model = a_model;
    }

    public void setDimensions(int a_width, int a_height) {
        width = a_width;
        height = a_height;
    }

    public void setHints(int a_hintflags) {
        hintflags = hintflags | a_hintflags;
    }

    public void setPixels(int x, int y, int w, int h, ColorModel model, byte[] a_pixels, int a_off, int a_scansize) {
        pixels_byte = a_pixels;
    }

    public void setPixels(int x, int y, int w, int h, ColorModel model, int[] a_pixels, int a_off, int a_scansize) {
        pixels_int = a_pixels;
    }

    public void setProperties(Hashtable<?, ?> props) {
        //
    }

    public byte[] getPixels_byte() {
        return pixels_byte;
    }

    public int[] getPixels_int() {
        return pixels_int;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ColorModel getColorModel() {
        return color_model;
    }

    public int getStatus() {
        return status;
    }

}
