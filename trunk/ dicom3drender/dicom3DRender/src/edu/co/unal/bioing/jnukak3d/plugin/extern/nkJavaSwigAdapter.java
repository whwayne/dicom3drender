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

import edu.co.unal.bioing.jnukak3d.plugin.nkDockManager;
import edu.co.unal.bioing.jnukak3d.ui.nkBinaryTreeLayout;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author Alexander Pinzon Fernandez
 */
public class nkJavaSwigAdapter extends nkDockManager{
    JFrame parent;
    Container content;
    nkBinaryTreeLayout root;

    private Component processLeaf(nkBinaryTreeLayout node){
        if(node == null) return null;
        if(node.getNodeType() == nkBinaryTreeLayout.NODE_LEAF){
            return (Component)node.getContent();
        }else if(node.getNodeType() == nkBinaryTreeLayout.NODE_HORIZONTAL){
            JPanel jp = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weighty = node.getWeight();
            gbc.weightx = 0.5;
            
            jp.add(processLeaf(node.getLeft()), gbc);
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weighty = 1.0 - node.getWeight();
            gbc.weightx = 0.5;
            jp.add(processLeaf(node.getRight()), gbc);
            return jp;
        }else if(node.getNodeType() == nkBinaryTreeLayout.NODE_VERTICAL){
            JPanel jp = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = node.getWeight();
            gbc.weighty = 0.5;

            jp.add(processLeaf(node.getLeft()), gbc);
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.weightx = 1.0 - node.getWeight();
            gbc.weighty = 0.5;
            jp.add(processLeaf(node.getRight()), gbc);
            return jp;
        }
        return null;
    }

    @Override
    public void config(Object windowParent, Object panelContent) {
        parent = (JFrame)windowParent;
        //parent.setLayout(new BorderLayout());
        content = (Container)panelContent;
        //jf.getContentPane().setLayout(new BorderLayout());
    }

    @Override
    public void pack() {
        
        content.setLayout(new BorderLayout());
        content.add(processLeaf(root), BorderLayout.CENTER);
        //parent.pack();
    }

    @Override
    public void readTree(nkBinaryTreeLayout root) {
        this.root = root;
    }




}
