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

import edu.co.unal.bioing.jnukak3d.ui.event.nkToolGenerator;
import edu.co.unal.bioing.jnukak3d.ui.event.nkToolListener;
import edu.co.unal.bioing.jnukak3d.ui.event.nkToolEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.SwingConstants;

/**
 *
 * @author Alexander Pinzon Fernandez
 */
public class nkMenuTool extends JPanel implements nkToolGenerator{
    private String prv_menu_name;
    private static int id_generator = -1;
    private int id;
    private JScrollPane prv_scroll_list;
    private boolean prv_scrool_visible;
    private JButton buttonTitle;

    private JList list;
    private DefaultListModel model;

    private Vector prv_nkToolEvent_listeners;

    public nkMenuTool(String name) {
        this(name, (Icon)null);
    }


    public nkMenuTool(String name,  Icon button_icon) {
        super(new BorderLayout());
        id_generator = id_generator + 1;
        prv_menu_name = name;
        id = id_generator;
        if(button_icon == null)
            buttonTitle = new JButton(prv_menu_name);
        else
            buttonTitle = new JButton(prv_menu_name, button_icon);
        buttonTitle.setHorizontalAlignment(SwingConstants.LEFT);
        buttonTitle.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    buttonTitleActionPerformed(evt);
                }
            }
        );
        add(buttonTitle, BorderLayout.NORTH);

        model = new DefaultListModel();

        list = new JList();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setModel(model);
        list.setCellRenderer(new Renderer());


        list.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {
                nkTool nkt = (nkTool)(list.getSelectedValue());
                            nkt.firenkToolEvent();
            }

            public void mousePressed(MouseEvent e) {}

            public void mouseReleased(MouseEvent e) {}

            public void mouseEntered(MouseEvent e) {}

            public void mouseExited(MouseEvent e) {}
        });

        prv_scroll_list = new JScrollPane(list);
        add(prv_scroll_list, BorderLayout.CENTER);
        prv_scrool_visible = true;
    }

    private void buttonTitleActionPerformed(java.awt.event.ActionEvent evt) {
        if(prv_scrool_visible){
            prv_scroll_list.setVisible(false);
            prv_scrool_visible = false;

        }else{
            prv_scroll_list.setVisible(true);
            prv_scrool_visible = true;
        }
        firenkMenuEvent();
    }

    public void collapse(boolean coll){
        if(coll){
            prv_scroll_list.setVisible(false);
            prv_scrool_visible = false;
        }else{
            prv_scroll_list.setVisible(true);
            prv_scrool_visible = true;
        }
    }
    
    public void addnkTool(nkTool nt){
        model.addElement(nt);
        list.setVisibleRowCount(model.size());
        //model.setSize(model.size());
    }


    private class Renderer extends DefaultListCellRenderer{
		@Override
		public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ){
			nkTool demo = (nkTool)value;
			super.getListCellRendererComponent( list, demo.getName(), index, isSelected, cellHasFocus );
			setIcon( demo.getIcon() );
                        setToolTipText(demo.getTextHelp());
			return this;
		}
	}

        final synchronized public void addnkToolListener (nkToolListener pl) {
        if( null == prv_nkToolEvent_listeners)
        prv_nkToolEvent_listeners = new Vector();
        if( null == pl || prv_nkToolEvent_listeners.contains( pl))
            return;
        prv_nkToolEvent_listeners.addElement( pl);
    }

    final synchronized public void removenkToolListener(nkToolListener pl) {
        if( null != prv_nkToolEvent_listeners && null != pl)
	    prv_nkToolEvent_listeners.removeElement( pl);
    }

    final synchronized public void firenkMenuEvent(){
        if( null == prv_nkToolEvent_listeners) return;
        nkToolEvent e = new nkToolEvent(this, id, prv_menu_name);
        for( int i= 0; i < prv_nkToolEvent_listeners.size(); ++i)
            ((nkToolListener)prv_nkToolEvent_listeners.elementAt( i)).nkToolInvoke(e);

    }

    public String getNameMenu() {
        return prv_menu_name;
    }



}
