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

import java.awt.image.IndexColorModel;

/**
 *
 * @author Alexander Pinzon Fernandez
 */
public abstract class nkImage3d {

    public abstract int getWidth(nkImage3dObserver observer); // X
    public abstract int getHeight(nkImage3dObserver observer); // Y
    public abstract int getDepth(nkImage3dObserver observer); // Z

    public abstract nkImage3dProducer getSource();
    public abstract Object getProperty(String name, nkImage3dObserver observer);
    public abstract Object getProperty(String name);
    public static final Object UndefinedProperty = new Object();
    public nkImage3d getScaledInstance(int width, int height, int depth, int hints) {

	/*nkImage3dFilter filter;
	if ((hints & (SCALE_SMOOTH | SCALE_AREA_AVERAGING)) != 0) {
	    filter = new AreaAveragingScaleFilter(width, height);
	} else {
	    filter = new ReplicateScaleFilter(width, height);
	}
	nkImage3dProducer prod;
	prod = new FilteredImageSource(getSource(), filter);
	return Toolkit.getDefaultToolkit().createImage(prod);*/
        return null;
    }

    public static final int SCALE_DEFAULT = 1;
    public static final int SCALE_FAST = 2;
    public static final int SCALE_SMOOTH = 4;
    public static final int SCALE_REPLICATE = 8;
    public static final int SCALE_AREA_AVERAGING = 16;

}
