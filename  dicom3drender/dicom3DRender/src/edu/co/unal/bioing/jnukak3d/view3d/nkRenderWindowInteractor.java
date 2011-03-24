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

package edu.co.unal.bioing.jnukak3d.view3d;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Mouse listener implementation.
 * @author Alexander Pinzon Fernandez
 */
public abstract class nkRenderWindowInteractor extends nkRenderWindow implements MouseListener, MouseMotionListener, MouseWheelListener {

    /**
     * This variable is True in the event of mouse button 1 is pressed
     */
    protected boolean mouseDraggedButton1;
    /**
     * This variable is True in the event of mouse button 1 is pressed
     */
    protected boolean mouseDraggedButton2;
    /**
     * This variable is True in the event of mouse button 1 is pressed
     */
    protected boolean mouseDraggedButton3;

    private int [] mousePositionInitial;
    private int [] mousePositionFinal;
    private int mouseWheelRotation;

    /**
     * Get mouse X position, by fisrt time before mouse button is dragged
     * @return x mouse position.
     */
    public int getXInitial(){  return mousePositionInitial[0];  }
    /**
     * Get mouse Y position, by fisrt time before mouse button is dragged
     * @return y mouse position.
     */
    public int getYInitial(){  return mousePositionInitial[1];  }
    /**
     * Get mouse X position, in the present time.
     * <p>
     * Valid X position once mouseDraggedButton1 is true.
     * @return x mouse position.
     */
    public int getXFinal(){  return mousePositionFinal[0];  }
    /**
     * Get mouse Y position, in the present time.
     * <p>
     * Valid Y position once mouseDraggedButton1 is true.
     * @return y mouse position.
     */
    public int getYFinal(){  return mousePositionFinal[1];  }
    /**
     * Get the number of turns of the wheel mouse.
     * @return Number of turns.
     */
    public int getMouseWheelRotation() {return mouseWheelRotation;    }
    
    /**
     * Class Constructor, Is necessary to invoke this function to initialize variables
     */
    public nkRenderWindowInteractor() {
        mousePositionInitial = new int[] {0,0};
        mousePositionFinal = new int[] {0,0};
        mouseWheelRotation = 0;
    }

    /**
     * Not used
     */
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * Capture mouse position.
     */
    public void mouseDragged(MouseEvent e) {
        mousePositionFinal[0] = e.getX();
        mousePositionFinal[1] = e.getY();
        if(mouseDraggedButton1) mouseDraggedAction(MouseEvent.BUTTON1);
        if(mouseDraggedButton2) mouseDraggedAction(MouseEvent.BUTTON2);
        if(mouseDraggedButton3) mouseDraggedAction(MouseEvent.BUTTON3);
    }

    /**
     * Abstract function invoke by mouseDragged function.
     * @param buttonType Anyone of this 3 values MouseEvent.BUTTON1, MouseEvent.BUTTON2, MouseEvent.BUTTON3
     */
    protected abstract void mouseDraggedAction(int buttonType);

    /**
     * Not used
     */
    public void mouseEntered(MouseEvent e) {    }

    /**
     * Not used
     */
    public void mouseExited(MouseEvent e) {    }

    /**
     * Not used
     */
    public void mouseMoved(MouseEvent e) {    }

    /**
     * Capture mouse position.
     */
    public void mousePressed(MouseEvent e) {
        mousePositionInitial[0] = e.getX();
        mousePositionInitial[1] = e.getY();
        mousePositionFinal[0] = e.getX();
        mousePositionFinal[1] = e.getY();
        switch (e.getButton()){
            case MouseEvent.BUTTON1:
                mouseDraggedButton1 = true;
                mousePressedAction(MouseEvent.BUTTON1);
            break;
            case MouseEvent.BUTTON2:
                mouseDraggedButton2 = true;
                mousePressedAction(MouseEvent.BUTTON2);
            break;
            case MouseEvent.BUTTON3:
                mouseDraggedButton3 = true;
                mousePressedAction(MouseEvent.BUTTON3);
            break;
        }
        
    }

    /**
     * Abstract function invoke by mousePressed function.
     * @param buttonType Anyone of this 3 values MouseEvent.BUTTON1, MouseEvent.BUTTON2, MouseEvent.BUTTON3
     */
    protected abstract void mousePressedAction(int buttonType);

    public void mouseReleased(MouseEvent e) {
        if(mouseDraggedButton1) mouseReleasedAction(MouseEvent.BUTTON1);
        if(mouseDraggedButton2) mouseReleasedAction(MouseEvent.BUTTON2);
        if(mouseDraggedButton3) mouseReleasedAction(MouseEvent.BUTTON3);
        mouseDraggedButton1 = false;
        mouseDraggedButton2 = false;
        mouseDraggedButton3 = false;
    }
    protected abstract void mouseReleasedAction(int buttonType);

    /**
     * Capture mouse wheel turns.
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
        mouseWheelRotation = e.getWheelRotation();
        wheelMovedAction(mouseWheelRotation);
    }
    /**
     * Abstract function invoke by mouseWheelMoved function.
     * @param rotation Number of turns of the wheel mouse.
     */
    protected abstract void wheelMovedAction(int rotation);
}
