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

import edu.co.unal.bioing.jnukak3d.ui.event.nkToolListener;
import edu.co.unal.bioing.jnukak3d.ui.event.nkToolEvent;
import edu.co.unal.bioing.jnukak3d.event.nkEvent;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.util.Vector;

/**
 *
 * @author Alexander Pinzon Fernandez
 */
public class nkToolBar extends JPanel implements nkToolListener{
    private JScrollPane prv_scroll_list;
    private String nameToolBar;
    private JPanel content;
    Vector listMenu;

    public nkToolBar(String name) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.nameToolBar = name;
        content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        prv_scroll_list = new JScrollPane(content);
        add(prv_scroll_list);
        JTextArea jta = new JTextArea();
        jta.setEnabled(false);
        add(jta);
    }

    public void addnkMenu(nkMenuTool nm){
        if( null == listMenu) listMenu = new Vector();
        if( null == nm || listMenu.contains( nm))
            return;
        listMenu.addElement(nm);
        content.add(nm);
        nm.addnkToolListener(this);
    }

    public String getNameToolBar() {
        return nameToolBar;
    }

    public void nkToolInvoke(nkToolEvent e) {
        if( null == listMenu) return;
        for( int i= 0; i < listMenu.size(); ++i){
            nkMenuTool nm1 = (nkMenuTool)listMenu.elementAt( i);
                    if(nm1.getNameMenu().compareTo(e.getName())!=0)
                        nm1.collapse(true);
        }
        paintAll(this.getGraphics());
    }

    public void nkEventInvoke(nkEvent e) {
        
    }




}
