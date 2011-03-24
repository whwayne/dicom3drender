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

package edu.co.unal.bioing.jnukak3d.ui;

import javax.swing.Icon;

/**
 *
 * @author Alexander Pinzon Fernandez
 */
public class nkBinaryTreeLayout {
    public static final int NODE_ROOT  = 1;
    public static final int NODE_LEAF  = 2;
    public static final int NODE_VERTICAL  = 4;
    public static final int NODE_HORIZONTAL  = 8;
    int node_type;
    double weight;
    nkBinaryTreeLayout left;
    nkBinaryTreeLayout right;
    Object content;
    String title;
    Icon icon;

    public void setData(Object content,String title, Icon icon){
        this.content = content;
        this.title = title;
        this.icon =  icon;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public Object getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public Icon getIcon() {
        return icon;
    }

    public nkBinaryTreeLayout(int type) {
        this(type, 1.0);
    }

    public nkBinaryTreeLayout(int type, double weight) {
        node_type = type;
        this.weight = Math.min((Math.max(0.0, weight)), 1.0);
        left = null;
        right = null;
        content = null;
        icon = null;
    }

    public int getNodeType() {
        return node_type;
    }

    public double getWeight() {
        return weight;
    }

    public nkBinaryTreeLayout getLeft() {
        return left;
    }

    public nkBinaryTreeLayout getRight() {
        return right;
    }

    public boolean  split(int splitType, double a_weight){
        if(node_type >NODE_LEAF) return false;
        if(splitType != NODE_HORIZONTAL &&  splitType != NODE_VERTICAL) return false;
        node_type = splitType;
        weight = a_weight;
        left = new nkBinaryTreeLayout(NODE_LEAF);
        right = new nkBinaryTreeLayout(NODE_LEAF);

        return true;
    }

    public boolean  splitHorizontal(double a_weight){
        return split(NODE_HORIZONTAL, a_weight);
    }

    public boolean  splitVertical(double a_weight){
        return split(NODE_VERTICAL, a_weight);
    }

    @Override
    public String toString() {
        String cad = "";
        if(node_type == NODE_ROOT) cad = "R, ";
        else if(node_type == NODE_LEAF) cad = "L, ";
        else if(node_type == NODE_HORIZONTAL) cad = "H, ";
        else cad = "V, ";
        cad = cad + weight + "\n";
        if(left !=null){
            cad = cad + "left: " +  left.toString();
        }
        if(right !=null){
            cad = cad + "right:" + right.toString();
        }
        return cad;
    }
}
