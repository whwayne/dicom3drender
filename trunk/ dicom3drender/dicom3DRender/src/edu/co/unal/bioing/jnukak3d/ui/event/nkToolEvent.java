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

package edu.co.unal.bioing.jnukak3d.ui.event;

import edu.co.unal.bioing.jnukak3d.event.nkEvent;

/**
 *
 * @author Alexander Pinzon Fernandez
 */
public final class nkToolEvent extends nkEvent {
    int id;
    String name;
    public nkToolEvent(Object a_source, int a_id, String a_name) {
        super(a_source);
        id = a_id;
        name = a_name;
    }

    final public int getId() {
        return id;
    }

    final public String getName() {
        return name;
    }

    @Override
    final public String toString() {
        return "nkToolEvent [name: " + name + ", id: " + id + "] source = " + source;
    }

}
