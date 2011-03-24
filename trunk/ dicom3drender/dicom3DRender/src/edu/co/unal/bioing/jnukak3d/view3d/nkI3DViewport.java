/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.co.unal.bioing.jnukak3d.view3d;

import jiv.PositionEvent;
import jiv.PositionGenerator;
import jiv.PositionListener;

/**
 *
 * @author apinzonf
 */
public interface nkI3DViewport extends PositionGenerator, PositionListener {

    //{}
    void addPositionListener(PositionListener pl);

    void changeColor();

    // Return -1
    int getMaxSliceNumber();

    //return 1
    float getOrthoStep();

    void positionChanged(PositionEvent e);

    void removePositionListener(PositionListener pl);

    void run();

    /**
     * Procedure to initialize the 3d scene.
     */
    void start();

}
