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

import jiv.Point3Dint;
import jiv.SliceImageProducer;
/**
 *
 * @author Alexander Pinzon Fernandez
 */
public interface nkData3DVolume {
    public static final int TYPE_BYTE = 0;
    public static final int TYPE_INT = 1;

    public int getDataType();

    public void stopDownloads();

    public int getXSize();

    public int getYSize();

    public int getZSize();

    public float getXStep();

    public float getYStep();

    public float getZStep();

    public String getNickName();

    public Object getVoxel( int x, int y, int z);

    public Object getVoxel( Point3Dint voxel);

    public int getVoxelAsInt( int x, int y, int z);

    public int getVoxelAsInt( Point3Dint voxel);

    public byte getVoxelAsByte( int x, int y, int z);

    public byte getVoxelAsByte( Point3Dint voxel);

    public Object getTransverseSlice( final int z );

    public void getTransverseSlice( final int z, Object slice,
					  final SliceImageProducer consumer );
    public Object getSagittalSlice( final int x );

    public void getSagittalSlice( final int x, Object slice,
					final SliceImageProducer consumer);

    public Object getCoronalSlice( final int y );

    public void getCoronalSlice( final int y, Object slice,
				       final SliceImageProducer consumer);

    public float voxel2image( short voxel_value);

    public short image2voxel( float image_value);

    public void printTrueRange();

    public Object getVoxels();

}
