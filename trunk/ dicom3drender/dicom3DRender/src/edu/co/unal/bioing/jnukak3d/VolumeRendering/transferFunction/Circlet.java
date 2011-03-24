/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.co.unal.bioing.jnukak3d.VolumeRendering.transferFunction;

import java.awt.Color;

/**
 *
 * @author jleon
 */
public class Circlet {
    private int xCoordinate;
    private int yCoordinate;
    private boolean selected;
    private Color fillColor;

    public Circlet(int x,int y){
        this.xCoordinate=x;
        this.yCoordinate=y;
        this.fillColor=Color.BLACK;
    }

    /**
     * @return the xCoordinate
     */
    public int getxCoordinate() {
        return xCoordinate;
    }

    /**
     * @param xCoordinate the xCoordinate to set
     */
    public void setxCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    /**
     * @return the yCoordinate
     */
    public int getyCoordinate() {
        return yCoordinate;
    }

    /**
     * @param yCoordinate the yCoordinate to set
     */
    public void setyCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    /**
     * @return the isDragged
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param isDragged the isDragged to set
     */
    public void setSelected(boolean isSelected) {
        this.selected = isSelected;
    }

    /**
     * @return the fillColor
     */
    public Color getFillColor() {
        return fillColor;
    }

    /**
     * @param fillColor the fillColor to set
     */
    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }
}
