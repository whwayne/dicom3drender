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
import java.io.IOException;
import jiv.Point3Dint;
import jiv.SliceImageProducer;
import jiv.VolumeHeader;

/**
 *
 * @author Alexander Pinzon Fernadnez
 */
public class nkData3DImage implements nkData3DVolume{
    protected static final boolean DEBUG = nkDebug.DEBUG;
    /*private*/ byte[][][] 	voxels;     // (z,y,x)!
    /*private*/ int[][][] 	voxels_int;     // (z,y,x)!

    /*private*/ VolumeHeader.ResampleTable	resample_table;
    /*private*/ String 		nick_name;

    /** header associated with the data in volume_url */
    /*private*/ VolumeHeader	volume_header;
    /*private*/ VolumeHeader	common_sampling;


    public nkData3DImage( VolumeHeader common_sampling, int dataType){
        this.common_sampling= common_sampling;
        if(dataType == nkData3DVolume.TYPE_BYTE)
            voxels= new byte[ getZSize()][ getYSize()][ getXSize()];
        else
            voxels_int= new int[ getZSize()][ getYSize()][ getXSize()];
    }

    public Object getVoxels(){
        return voxels!=null ? voxels : voxels_int;
    }

    /** Note: it's not _required_ to declare SecurityException (since
     it's a subclass of RuntimeException), but we do it for clarity --
     this error is likely to happen when working with url-s...
    */
    public nkData3DImage( nkImage3d img3d,
			 VolumeHeader volume_header,
			 String nick_name)
	throws IOException, SecurityException{
        this( volume_header, ((Integer)(img3d.getProperty("datatype"))).intValue());

        this.volume_header= volume_header;
        this.common_sampling= volume_header;
	
	this.nick_name= nick_name;

	resample_table= volume_header.getResampleTable( common_sampling);

	/* initialize this volume's region to the dummy pattern
	   ("solid color", more exactly), but only within the extend
	   of this file -- leave black padding outside... */
        {
            int tam = img3d.getDepth(null);
            int w, h;
            w = img3d.getWidth(null);
            h = img3d.getHeight(null);
            for(int k=0; k<tam; k++){
                
                int[] rgbs = new int[w*h];
                ((nkBufferedImage3d)img3d).getSliceImage(k).getRGB(0, 0, w, h, rgbs, 0, w);
                for(int j=0; j< h; j++)
                if(voxels == null)
                    System.arraycopy(rgbs, j*w, voxels_int[k][j], 0, w);

            }

        }
	}

    /** Kills the background download threads. (note: currently, only
        the lengthy full-volume download thread is killed...)
    */
    final public void stopDownloads()
    {
        System.out.println("nkData3DImage Not implemented");
    }

    final public int getXSize() { return common_sampling.getSizeX(); }

    final public int getYSize() { return common_sampling.getSizeY(); }

    final public int getZSize() { return common_sampling.getSizeZ(); }

    final public float getXStep() { return volume_header.getStepX(); }

    final public float getYStep() { return volume_header.getStepY(); }

    final public float getZStep() { return volume_header.getStepZ(); }

    final public String getNickName() { return nick_name; }

    final public Object getVoxel( int x, int y, int z)
    {
        return voxels!=null? voxels[ z][ y][ x] :  voxels_int[ z][ y][ x];
    }

    final public Object getVoxel( Point3Dint voxel)
    {
        return getVoxel( voxel.x, voxel.y, voxel.z);
    }

    final public int getVoxelAsInt( int x, int y, int z)
    {
	// NB: this '&' needs to be done if you want to get 255
	// for the maximum valued voxel! otherwise you'll get
	// -1 because byte-s are signed in Java!
        return 0xFF & ((Byte)getVoxel( x, y, z)).byteValue();
    }

    final public int getVoxelAsInt( Point3Dint voxel)
    {
        return getVoxelAsInt( voxel.x, voxel.y, voxel.z);
    }

    public byte getVoxelAsByte(int x, int y, int z) {
        return ((Integer)getVoxel( x, y, z)).byteValue();
    }


    public byte getVoxelAsByte(Point3Dint voxel) {
        return getVoxelAsByte(voxel.x, voxel.y, voxel.z);
    }

    /** the result is arranged in "display order" (i.e. origin in _top_ left corner),
	or in other words "flipped" -- hence, it can be then directly fed to an
	image producer mechanism.
    */
    final public Object getTransverseSlice( final int z )
    {
        Object slice;
        if(voxels!=null)
            slice= new byte[ getYSize() * getXSize()];
        else
            slice= new int[ getYSize() * getXSize()];

        getTransverseSlice( z, slice, null);
        return slice;
    }

    /** this overloaded version is easier on the heap & garbage collector because
	it encourages reuse of an already allocated 'slice' array (which has
	to be large enough to hold the result, naturally).

	<em>Note:</em> param 'slice' should not be used outside the
	current thread!!
    */
    final public void getTransverseSlice( final int z, Object slice,
					  final SliceImageProducer consumer )
    {

        final int x_size= getXSize();
        final int y_size= getYSize();
        Object voxels;
        if(this.voxels != null)
            voxels= this.voxels;
        else
            voxels= this.voxels_int;

        // NB: this vertically flips the image...
        // (decreasing index loops are rumored to run faster in Java)
        if(this.voxels != null)
        for( int y= y_size - 1, offset= 0; y >= 0; --y, offset += x_size)
            System.arraycopy( ((byte[][][])voxels)[ z][ y], 0, (byte[])slice, offset, x_size);
        else
            for( int y= y_size - 1, offset= 0; y >= 0; --y, offset += x_size)
            System.arraycopy( ((int[][][])voxels)[ z][ y], 0, (int[])slice, offset, x_size);

    }

    final public Object getSagittalSlice( final int x )
    {
        Object slice;
        if(voxels != null)
            slice= new byte[ getZSize() * getYSize()];
        else
            slice= new int[ getZSize() * getYSize()];
        getSagittalSlice( x, slice, null);
        return slice;
    }

    final public void getSagittalSlice( final int x, Object slice,
					final SliceImageProducer consumer)
    {

        final int y_size= getYSize();
        final int z_size= getZSize();
        Object voxels;
        if(this.voxels != null)
            voxels= this.voxels;
        else
            voxels= this.voxels_int;
        // NB: this vertically flips the image...
        if(this.voxels != null)
            for( int z= z_size - 1, offset= 0; z >= 0; --z, offset += y_size)
            for( int y= y_size - 1; y >= 0; --y)
            ((byte[])slice)[ offset + y]= ((byte[][][])voxels)[ z][ y][ x];
        else
            for( int z= z_size - 1, offset= 0; z >= 0; --z, offset += y_size)
            for( int y= y_size - 1; y >= 0; --y)
            ((int[])slice)[ offset + y]= ((int[][][])voxels)[ z][ y][ x];
    }

    final public Object getCoronalSlice( final int y )
    {
        Object slice;
        if(voxels != null)
            slice= new byte[ getZSize() * getXSize()];
        else
            slice= new int[ getZSize() * getXSize()];
        getCoronalSlice( y, slice, null);
        return slice;
    }

    final public void getCoronalSlice( final int y, Object slice,
				       final SliceImageProducer consumer)
    {

        final int x_size= getXSize();
        final int z_size= getZSize();
        Object voxels;
        if(this.voxels != null)
            voxels= this.voxels;
        else
            voxels= this.voxels_int;
        // NB: this vertically flips the image...
        if(this.voxels != null)
        for( int z= z_size - 1, offset= 0; z >= 0; --z, offset += x_size)
            System.arraycopy( ((byte [][][])voxels)[ z][ y], 0, (byte [])slice, offset, x_size);
        else
            for( int z= z_size - 1, offset= 0; z >= 0; --z, offset += x_size)
            System.arraycopy( ((int [][][])voxels)[ z][ y], 0, (int [])slice, offset, x_size);
    }

    /**
     * @param voxel_value a 0..255 (byte) value
     * @return the image value (real value) corresponding to voxel_value
     */
    public final float voxel2image( short voxel_value) {
        return volume_header.voxel2image( voxel_value);
    }

    /**
     * @param image_value (aka real value)
     * @return the 0..255 voxel value corresponding to image_value
     */
    public final short image2voxel( float image_value) {
        return volume_header.image2voxel( image_value);
    }


    // debugging aid...
    public void printTrueRange()
    {
	int min= (voxels != null ? voxels[ 0][ 0][ 0]: voxels_int[ 0][ 0][ 0]);
	int max= (voxels != null ? voxels[ 0][ 0][ 0]: voxels_int[ 0][ 0][ 0]);
	for( int z= 0; z < getZSize(); ++z)
	    for( int y= 0; y < getYSize(); ++y)
		for( int x= 0; x < getXSize(); ++x) {
		    int vox= getVoxelAsInt( x, y, z);
		    // NB: if you want to assign 255 to a byte, you need to
		    // do assign it '(byte)255' (which does the right thing)
		    if( vox < min)
			min= vox;
		    else if( vox > max)
			max= vox;
		}
	if(DEBUG) System.out.println( "true range: " + min + " " + max);
    }

    /**
     * @return value of first element of <code>array</code>
     */
    final static /*private*/ int _first( final int[] array )
    {
        return array[ 0];
    }

    /**
     * @return value of last element of <code>array</code>
     */
    final static /*private*/ int _last( final int[] array )
    {
        return array[ array.length - 1 ];
    }

    public int getDataType() {
        return nkData3DVolume.TYPE_INT;
    }


}

