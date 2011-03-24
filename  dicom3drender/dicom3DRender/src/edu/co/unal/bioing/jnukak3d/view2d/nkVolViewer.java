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

package edu.co.unal.bioing.jnukak3d.view2d;

import edu.co.unal.bioing.jnukak3d.ui.event.nkMouseAdapter;
import edu.co.unal.bioing.jnukak3d.view3d.nk3DViewport;
import edu.co.unal.bioing.jnukak3d.image3d.nkData3DVolume;
import edu.co.unal.bioing.jnukak3d.plugin.nkDockManager;
import edu.co.unal.bioing.jnukak3d.plugin.nkKernel;
import edu.co.unal.bioing.jnukak3d.ui.nkBinaryTreeLayout;
import edu.co.unal.bioing.jnukak3d.view3d.nkI3DViewport;
import java.awt.*;
import java.awt.image.IndexColorModel;
import javax.swing.*;
import jiv.*;
import java.util.Vector;

/**
 * Class for view volume 3D, and implement all functions for manage image.
 * @author Alexander Pinzon Fernandez
*/
public class nkVolViewer extends JPanel{
    JFrame prv_parent;
    private Slice2DViewport	prv_vistaAxial;		//! View Axial.
    private Slice2DViewport	prv_vistaCoronal;	//! View Coronal.
    private Slice2DViewport	prv_vistaSagital;	//! View Sagital.
    private SliceImageProducer prv_ImageProducerAxial;
    private SliceImageProducer prv_ImageProducerCoronal;
    private SliceImageProducer prv_ImageProducerSagital;
    private nkI3DViewport prv_vista3D;		//! View 3D.
    private nkData3DVolume prv_image;
    private VolumeHeader prv_image_header;
    private Point3Dfloat		initial_world_cursor;
    private nkMouseAdapter popup_adapter= new nkMouseAdapter();
    private Vector event_listeners;

    /**
     * Class constructor
     * @param an_image_header Header information of image.
     * @param an_image Data of image.
     */


    public nkVolViewer(JFrame parent, VolumeHeader an_image_header,  nkData3DVolume an_image){
        prv_parent = parent;

        prv_image = an_image;
        prv_image_header = an_image_header;
        initial_world_cursor = new Point3Dfloat(0.0f, 0.0f,0.0f);

        if(an_image.getDataType() == nkData3DVolume.TYPE_BYTE){
            prv_ImageProducerAxial = new TransverseSliceImageProducer(getPrv_image(), 0, new ColorCoding().get8bitColormap(ColorCoding.GRAY,0,255));
            prv_ImageProducerCoronal = new CoronalSliceImageProducer(getPrv_image(), 0, new ColorCoding().get8bitColormap(ColorCoding.GRAY,0,255));
            prv_ImageProducerSagital = new SagittalSliceImageProducer(getPrv_image(), 0, new ColorCoding().get8bitColormap(ColorCoding.GRAY,0,255));
        }else{
            prv_ImageProducerAxial = new TransverseSliceImageProducer(getPrv_image(), 0, new ColorCoding().get8bitColormap(ColorCoding.GRAY,0,255),nkData3DVolume.TYPE_INT);
            prv_ImageProducerCoronal = new CoronalSliceImageProducer(getPrv_image(), 0, new ColorCoding().get8bitColormap(ColorCoding.GRAY,0,255),nkData3DVolume.TYPE_INT);
            prv_ImageProducerSagital = new SagittalSliceImageProducer(getPrv_image(), 0, new ColorCoding().get8bitColormap(ColorCoding.GRAY,0,255),nkData3DVolume.TYPE_INT);

        }

        prv_vistaAxial = new TransverseSlice2DViewport(prv_ImageProducerAxial, (PositionListener)prv_ImageProducerAxial, initial_world_cursor);
        prv_vistaCoronal = new CoronalSlice2DViewport(prv_ImageProducerCoronal, (PositionListener)prv_ImageProducerCoronal, initial_world_cursor);
        prv_vistaSagital = new  SagittalSlice2DViewport(prv_ImageProducerSagital, (PositionListener)prv_ImageProducerSagital, initial_world_cursor);
        
        prv_vista3D = new nk3DViewport(prv_image_header, getPrv_image(), prv_ImageProducerAxial,
                prv_ImageProducerCoronal, prv_ImageProducerSagital);

        prv_vistaAxial.addPositionListener(prv_ImageProducerAxial);
        prv_vistaAxial.addPositionListener(prv_ImageProducerCoronal);
        prv_vistaAxial.addPositionListener(prv_ImageProducerSagital);
        prv_vistaAxial.addPositionListener(prv_vista3D);
        prv_vistaCoronal.addPositionListener(prv_ImageProducerCoronal);
        prv_vistaCoronal.addPositionListener(prv_ImageProducerAxial);
        prv_vistaCoronal.addPositionListener(prv_ImageProducerSagital);
        prv_vistaCoronal.addPositionListener(prv_vista3D);
        prv_vistaSagital.addPositionListener(prv_ImageProducerSagital);
        prv_vistaSagital.addPositionListener(prv_ImageProducerAxial);
        prv_vistaSagital.addPositionListener(prv_ImageProducerCoronal);
        prv_vistaSagital.addPositionListener(prv_vista3D);
        
        addColormapListener(prv_ImageProducerAxial);
        addColormapListener(prv_ImageProducerCoronal);
        addColormapListener(prv_ImageProducerSagital);

        nkDockManager dm =  nkKernel.getInstance().getDockServer().getDefaultDockEngine().createDockManager();
        dm.config(parent, this);
        
        JPanel j3d = new JPanel(new BorderLayout());
        j3d.add((Container)prv_vista3D, BorderLayout.CENTER);

        nkBinaryTreeLayout nkTree;
        nkTree = new nkBinaryTreeLayout(nkBinaryTreeLayout.NODE_ROOT);
        nkTree.splitHorizontal(0.5);
        nkTree.getLeft().setData(j3d, "Viewer 3D", null);
        nkTree.getRight().splitVertical(0.33);
        nkTree.getRight().getLeft().setData(prv_vistaAxial, "Axial", null);
        nkTree.getRight().getRight().splitVertical(0.33);
        nkTree.getRight().getRight().getLeft().setData(prv_vistaCoronal, "Coronal", null);
        nkTree.getRight().getRight().getRight().setData(prv_vistaSagital, "Sagital", null);
        //dm.addDockinGrid(prv_vistaAxial, "Axial", 0, 1, 1,1, 0.5,0.5);
        //dm.addDockinGrid(prv_vistaCoronal, "Coronal", 1, 1, 1,1,0.5,0.5);
        //dm.addDockinGrid(j3d, "Viewer 3D", 0, 0, 3,1,0.5,0.5);
        //dm.addDockinGrid(prv_vistaSagital, "Sagital", 2, 1,1,1,0.5,0.5 );
        dm.readTree(nkTree);

        dm.pack();

        //setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );

        prv_vista3D.start();
        prv_vista3D.run();
    }
     
     public void changePallete(IndexColorModel icm){
         ColormapEvent e = new ColormapEvent(this, icm);
         _fireColormapEvent(e);
         prv_vista3D.changeColor();
     }

     final /*private*/ void _fireColormapEvent( final ColormapEvent e) {
	    // deliver the event to each of the listeners
	    if( null == event_listeners)
		// nobody listening...
		return;
	    for( int i= 0; i < event_listeners.size(); ++i)
		((ColormapListener)event_listeners.elementAt( i)).colormapChanged( e);
	}

	synchronized void addColormapListener( ColormapListener cl) {

	    if( null == event_listeners)
		event_listeners= new Vector();
	    event_listeners.addElement( cl);
	}

	synchronized void removeColormapListener( ColormapListener cl) {

	    if( null != event_listeners)
		event_listeners.removeElement( cl);
	}

    /**
     * @return the prv_image
     */
    public nkData3DVolume getPrv_image() {
        return prv_image;
    }

}
