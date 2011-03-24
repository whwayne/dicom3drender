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

package edu.co.unal.bioing.jnukak3d.image3d;

import edu.co.unal.bioing.jnukak3d.nkDebug;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

/**
 *
 * @author Alexander Pinzon Fernandez
 */
public class nkBufferedImage3d extends nkImage3d{
    BufferedImage[] sliceImages;
    int depth, height, width;
    final Hashtable properties = new Hashtable();
    private static boolean DEBUG=nkDebug.DEBUG;
    private int defaultWidth=0;
    private int defaultCenter=0;
    
    public nkBufferedImage3d(BufferedImage[] sliceImages) {
        this.sliceImages = sliceImages;
        height = depth = width = 0;
        if(this.sliceImages != null){
            depth = this.sliceImages.length;
            if(depth >0){//TODO Watch out! width and height might not initialized!!!
                width = this.sliceImages[0].getWidth();
                height = this.sliceImages[0].getHeight();
                properties.put("datatype", nkData3DVolume.TYPE_INT);
             }
        }
        if(DEBUG)
            System.out.println("nkBufferedImage3d.nkBufferedImage3d depth:"+this.depth);
        if(DEBUG)
            System.out.println("nkBufferedImage3d.nkBufferedImage3d width:"+this.width);
        if(DEBUG)
            System.out.println("nkBufferedImage3d.nkBufferedImage3d height:"+this.height);
    }

    @Override
    public int getDepth(nkImage3dObserver observer) {
        return depth;
    }

    @Override
    public int getHeight(nkImage3dObserver observer) {
        return height;
    }

    @Override
    public Object getProperty(String name, nkImage3dObserver observer) {
        //throw new UnsupportedOperationException("Not supported yet.");
        return null;
    }

    @Override
    public int getWidth(nkImage3dObserver observer) {
        return width;
    }

    @Override
    public nkImage3dProducer getSource() {
        return null;
    }

    @Override
    public Object getProperty(String name) {
        return properties.get(name);
    }

    public void setProperty(String name, Object value) {
        properties.put(name, value);
    }

    public BufferedImage getSliceImage(int index) {
        if (sliceImages == null) return null;
        if(index >= sliceImages.length) return null;
        return sliceImages[index];
    }

    /**
     * @return the defaultWidth
     */
    public int getDefaultWidth() {
        return defaultWidth;
    }

    /**
     * @param defaultWidth the defaultWidth to set
     */
    public void setDefaultWidth(int defaultWidth) {
        this.defaultWidth = defaultWidth;
    }

    /**
     * @return the defaultCenter
     */
    public int getDefaultCenter() {
        return defaultCenter;
    }

    /**
     * @param defaultCenter the defaultCenter to set
     */
    public void setDefaultCenter(int defaultCenter) {
        this.defaultCenter = defaultCenter;
    }

    

}
