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

import edu.co.unal.bioing.jnukak3d.*;
import java.awt.event.MouseEvent;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import net.dzzd.access.ICamera3D;
import net.dzzd.access.IMesh3D;
import net.dzzd.access.IPoint3D;
import net.dzzd.access.IScene3D;
import net.dzzd.core.Point3D;

/**
 * Class for Implementing a virtual trackball, show in
 * <p>
 * http://blogs.msdn.com/danlehen/archive/2005/12/15/504548.aspx
 * @author Alexander Pinzon Fernandez
 */
public abstract class nkInteractorStyleTrackBall extends nkRenderWindowInteractor{
    /**
     * Variable that contain a 3D scene.
     */
	protected volatile IScene3D prv_scene;

    private Vector3d pointInitial;
    private Vector3d pointFinal;
    private Matrix3d rotMatrixInitial;
    private String nameMesh;

    /**
     * Class constructor.
     * @param nameMeshToTrackBall Name of mesh to rotate with this trackball.
     */
    public nkInteractorStyleTrackBall(String nameMeshToTrackBall) {
        super();
        nameMesh = nameMeshToTrackBall;
        pointInitial = new Vector3d();
        pointFinal =  new Vector3d();
        rotMatrixInitial = new Matrix3d();
        setRotMatrixInitial(0.0001, 0.0001, 0.0001);
    }

    /**
     * @return Name of mesh to rotate.
     */
    public String getNameMesh() {
        return nameMesh;
    }

    /**
     * Set name of mesh to rotate.
     */
    public void setNameMesh(String nameMesh) {
        this.nameMesh = nameMesh;
    }

    private void setRotMatrixInitial(double xRot, double yRot, double zRot){
        Matrix3d rotMeshY = new Matrix3d();
        Matrix3d rotMeshZ = new Matrix3d();
        //rotMatrixInitial =  new Matrix3d();
        rotMatrixInitial.rotX(xRot);
        rotMeshY.rotY(yRot);
        rotMatrixInitial.mul(rotMeshY);
        rotMeshZ.rotZ(zRot);
        rotMatrixInitial.mul(rotMeshZ);
    }

    private void configSetRotMatrixInitial(){
        if(prv_scene == null) return;
        IMesh3D root = prv_scene.getMesh3DByName(nameMesh);
        if(root == null) return;
        setRotMatrixInitial(root.getRotation().getX(), root.getRotation().getY(), root.getRotation().getZ());
    }

    /**
     * Implement action for mouse dragged event.
     */
    @Override
    protected void mouseDraggedAction(int buttonType) {
        switch (buttonType){
            case(MouseEvent.BUTTON1):
                pointFinal = nkUtil.MouseXYSpherePoint3D(getXFinal(), getYFinal(), this.getSize().width, this.getSize().height);
                trackBallMouse();
            break;
            case(MouseEvent.BUTTON2):
            break;
            case(MouseEvent.BUTTON3):
            break;
        }

    }

    /**
     * Implement action for mouse pressed event.
     */
    @Override
    protected void mousePressedAction(int buttonType) {
        switch (buttonType){
            case(MouseEvent.BUTTON1):
                pointInitial = nkUtil.MouseXYSpherePoint3D(getXInitial(), getYInitial(), this.getSize().width, this.getSize().height);
                pointFinal = nkUtil.MouseXYSpherePoint3D(getXInitial(), getYInitial(), this.getSize().width, this.getSize().height);
                configSetRotMatrixInitial();

            break;
            case(MouseEvent.BUTTON2):
            break;
            case(MouseEvent.BUTTON3):
            break;
        }
    }

    /**
     * Implement action for mouse released event.
     */
    @Override
    protected void mouseReleasedAction(int buttonType) {
        configSetRotMatrixInitial();
        switch(buttonType){
            case(MouseEvent.BUTTON1):
                //configSetRotMatrixInitial();
            break;
            case(MouseEvent.BUTTON2):
            break;
            case(MouseEvent.BUTTON3):
            break;
        }
    }

    /**
     * Implement action for wheel moved event.
     */
    @Override
    protected void wheelMovedAction(int rotation) {
        zoomCamera();
    }

    private void trackBallMouse(){
        if(pointInitial.equals(pointFinal)) return;
        IMesh3D root = prv_scene.getMesh3DByName(nameMesh);
        if(root == null) return;

        Vector3d axis = new Vector3d();
        axis.cross(pointInitial, pointFinal);
        double theta = pointInitial.angle(pointFinal);
        Quat4d delta = new Quat4d();
        delta.set(new AxisAngle4d(axis, theta));
        Quat4d q = new Quat4d();
        q.set(rotMatrixInitial);
        q.mul(delta);
        Matrix3d mrot = new Matrix3d();
        mrot.set(q);
        Vector3d vr = nkUtil.RotationMatrixToEulerAngles(mrot);
        //Vector3d vr = nkUtil.RotationMatrixToEulerAngles(rotMatrixInitial);

        //updateRotationPlanes(new Point3D(vr.x  + ((double)(getYFinal()-getYInitial()))/15, -vr.y, vr.z) );
        updateRotationPlanes(new Point3D(vr.x , -vr.y, vr.z) );
        render();
    }

    private void updateRotationPlanes(IPoint3D rotPlanes){
        IMesh3D root = prv_scene.getMesh3DByName(nameMesh);
        if(root == null) return;
        root.getRotation().set(rotPlanes.getX(), rotPlanes.getY(), rotPlanes.getZ());
    }

    private void zoomCamera(){
        ICamera3D camera3d = prv_scene.getCurrentCamera3D();
        if(camera3d == null) return;
        double cameraZ = camera3d.getPosition().getZ() + (((double)(getMouseWheelRotation()))/2.0);
        if(cameraZ>-0.5)cameraZ = -0.5;
        if(cameraZ<-50.0)cameraZ = -50.0;
		camera3d.getPosition().setZ(cameraZ);
        render();
    }


}
