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

package edu.co.unal.bioing.jnukak3d;

import java.net.URL;
import javax.swing.ImageIcon;
import javax.vecmath.*;

/**
 * Common functions and procedures.
 * @author Alexander Pinzon Fernandez
*/
public class nkUtil {
    final static Runtime m_runtime = Runtime.getRuntime();

    /**
	 * Calculate euler angles from rotation matrix.
	 * @param mat 3x3 rotation matrix.
	*/
    static public Vector3d RotationMatrixToEulerAngles(Matrix3d mat){
        Vector3d res = new Vector3d();
        double angle_z, angle_x, angle_y = -Math.asin( mat.m02);        
        double C= Math.cos( angle_y );
        double trX, trY;
        if ( Math.abs( C ) > 0.005 ){
            trX      =  mat.m22 / C;           
            trY      = -mat.m12  / C;
            angle_x  = Math.atan2(trY, trX );
            trX      =  mat.m00 / C;           
            trY      = -mat.m01 / C;
            angle_z  = Math.atan2( trY, trX );
        }else{
            angle_x  = 0;                    
            trX      = mat.m11;              
            trY      = mat.m10;
            angle_z  = Math.atan2( trY, trX );
        }
        res.set(angle_x, angle_y, angle_z);
        return res;
    }

    /**
	 * Calculate 3d point from intersection of line generate by camera and mouse with 3D sphere.
	 * @param xin Mouse X position.
     * @param yin Mouse Y position.
     * @param w Width from window.
     * @param h Height from window.
	*/
    static public Vector3d MouseXYSpherePoint3D(double xin, double yin, int w, int h){
            double x = xin*2.0/w;
            double y = (h - yin)*2.0/h;

            x = x - 1.0;
            y = y - 1.0;

            double z2 = 1 - (x*x) - (y*y);
            double z = z2 > 0 ? Math.sqrt(z2) : 0;

            Vector3d p = new Vector3d(x, y, z);
            p.normalize();
            return p;
    }

    static public void printMemoryUsage(){
        long totalMem = (m_runtime.totalMemory())/(1024*1024);
        long freeMem = m_runtime.freeMemory()/(1024*1024);
        long usageMem = totalMem - freeMem;
        System.out.println("Memory-> Total: " + totalMem + " Mb |   Usage: " + usageMem + " Mb |   Free: " + freeMem + " Mb");
    }

    static public ImageIcon getImageIcon(String className, String keyIcon) {
        try{
            URL location=(URL)getResource(className, keyIcon);
            return new ImageIcon((location));
        }catch(NullPointerException npe){
            return new ImageIcon();
        }
    }

    static public Object getResource(String className, String key) {
        URL url = null;
        String name = key;

        if (name != null) {
            System.out.println("name was not null "+name);
            try {
                Class c = Class.forName(className);
                url = c.getResource(name);
            } catch (ClassNotFoundException cnfe) {
                System.err.println("Unable to find Main class");
                
            }
            System.out.println("Returning stuff"+url);
            return url;
        } else
            System.out.println("name was null");
            return null;

    }
}
