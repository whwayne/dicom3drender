/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.co.unal.bioing.jnukak3d.view3d;

import edu.co.unal.bioing.jnukak3d.VolumeRendering.sliceBasedVolumeRendering.SliceBasedVolumeRendering;
import edu.co.unal.bioing.jnukak3d.image3d.nkData3DVolume;
import jiv.PositionEvent;
import jiv.PositionListener;
import jiv.VolumeHeader;

/**
 *
 * @author jleon
 */
public class nk3DVolumeViewport implements nkI3DViewport{
    private SliceBasedVolumeRendering render;

    public void addPositionListener(PositionListener pl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void changeColor() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getMaxSliceNumber() {
        return -1;
    }

    public float getOrthoStep() {
        return 1;
    }

    public void positionChanged(PositionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removePositionListener(PositionListener pl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public nk3DVolumeViewport(VolumeHeader an_image_header,  nkData3DVolume an_image){
        render=new SliceBasedVolumeRendering(800, 600,an_image.getXSize(),
                                             an_image.getYSize(),an_image.getZSize(),null);
    }

    public void run() {
        render.startRendering();
        render.requestFocus();
    }

    public void start() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    



}
