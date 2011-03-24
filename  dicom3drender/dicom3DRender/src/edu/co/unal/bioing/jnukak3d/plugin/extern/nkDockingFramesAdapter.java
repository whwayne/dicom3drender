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

package edu.co.unal.bioing.jnukak3d.plugin.extern;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.CGridArea;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.intern.CDockable;
import edu.co.unal.bioing.jnukak3d.plugin.nkDockManager;
import edu.co.unal.bioing.jnukak3d.ui.nkBinaryTreeLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import javax.swing.JFrame;

/**
 * @author Alexander Pinzon Fernandez
 */
public class nkDockingFramesAdapter extends nkDockManager{
    CControl control;
    CGrid grid;
    int rows_grid;
    int cols_grid;
    nkBinaryTreeLayout root;
    Container content;

    public nkDockingFramesAdapter() {
    }

    @Override
    public void config(Object windowParent, Object panelContent) {
        JFrame jf = (JFrame)windowParent;
        control = new CControl( jf );
        content = (Container)panelContent;
        content.setLayout(new BorderLayout());
        content.add( control.getContentArea(), BorderLayout.CENTER );
        //jf.getContentPane().setLayout(new BorderLayout());
        //jf.getContentPane().add( control.getContentArea(), BorderLayout.CENTER );
        grid = new CGrid( control );
    }

    private  CDockable processLeaf(nkBinaryTreeLayout node){
        if(node == null) return null;
        if(node.getNodeType() == nkBinaryTreeLayout.NODE_LEAF){
            return createDockable(node.getTitle(), (Component)node.getContent());
        }else if(node.getNodeType() == nkBinaryTreeLayout.NODE_VERTICAL){
            CGridArea cga = new CGridArea(control, "id:"+ node.hashCode());
            CGrid cg = new CGrid(control);
            double weight = node.getWeight();
            cg.add(0, 0, weight, 1, processLeaf(node.getLeft()));
            cg.add(weight, 0, (1 - weight), 1, processLeaf(node.getRight()));
            cga.deploy(cg);
            return cga;
                        
        }else if(node.getNodeType() == nkBinaryTreeLayout.NODE_HORIZONTAL){
            CGridArea cga = new CGridArea(control, "id:"+ node.hashCode());
            CGrid cg = new CGrid(control);
            double weight = node.getWeight();
            cg.add(0, 0, 1, weight, processLeaf(node.getLeft()));
            cg.add(0, weight, 1, (1 - weight), processLeaf(node.getRight()));
            cga.deploy(cg);
            return cga;
        }
        return null;
    }


    private SingleCDockable createDockable( String title, Component panel) {
         panel.setBackground(Color.BLACK);
         DefaultSingleCDockable dockable = new DefaultSingleCDockable( title, title, panel );
         dockable.setCloseable( false );
         dockable.setExternalizable(true);
         return dockable;
     }

    @Override
    public void pack() {
        if(grid!= null && control != null && root != null){
            grid.add(0, 0, 1, 1, processLeaf(root));
            control.getContentArea().deploy(grid);
        }
    }

    @Override
    public void readTree(nkBinaryTreeLayout root) {
        this.root = root;
    }

    

}
