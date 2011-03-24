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

import edu.co.unal.bioing.jnukak3d.image3d.nkData3DVolume;
import edu.co.unal.bioing.jnukak3d.nkDebug;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.PixelGrabber;
import java.awt.image.ReplicateScaleFilter;
import java.util.Vector;
import jiv.Point3Dfloat;
import jiv.PositionEvent;
import jiv.PositionGenerator;
import jiv.PositionListener;
import jiv.SliceImageProducer;
import jiv.VolumeHeader;
import net.dzzd.access.IRender3D;
import net.dzzd.access.IURLTexture;
import net.dzzd.DzzD;
import net.dzzd.access.IFace3D;
import net.dzzd.access.IMappingUV;
import net.dzzd.access.IMaterial;
import net.dzzd.access.IMesh3D;
import net.dzzd.access.ITexture;
import net.dzzd.access.IVertex3D;
import net.dzzd.core.Face3D;
import net.dzzd.core.Point3D;
import net.dzzd.core.Vertex3D;
import net.dzzd.core.nkTexture;

/**
 * Class for view 3D ortogonal planes, of 3D dataset.
 * @author Alexander Pinzon Fernandez
 */
public final class nk3DViewport extends nkInteractorStyleTrackBall implements  nkI3DViewport/*, Runnable*/
{
    protected static final boolean DEBUG = nkDebug.DEBUG;
    final static private double MAX_TAM_CUBE = 6.0;
    final static private double DELTA_PLANES = 0.001;
    private double maxtam;
    private Point3Dfloat prv_cursorPosition;
    private volatile IRender3D prv_render;
	private volatile boolean prv_run=false;
    private SliceImageProducer prv_imageAxial;
    private SliceImageProducer prv_imageSagital;
    private SliceImageProducer prv_imageCoronal;
    private nkData3DVolume prv_image;
    private VolumeHeader prv_image_header;
    private Vector prv_event_listeners;
    private Point3D prv_sizePlanes;
    private Point3D prv_middlePlanes;


    /**
     * Class Constructor
     * @param an_image_header Header information of image.
     * @param an_image Data of image
     * @param an_ImageAxial Axial image producer.
     * @param an_ImageCoronal Coronal image producer.
     * @param an_ImageSagital Sagital image producer.
     */
    public nk3DViewport(VolumeHeader an_image_header,  nkData3DVolume an_image,
            SliceImageProducer an_ImageAxial, SliceImageProducer an_ImageCoronal, SliceImageProducer an_ImageSagital){
        super("MeshRoot");
        prv_imageAxial = an_ImageAxial;
        prv_imageCoronal = an_ImageCoronal;
        prv_imageSagital = an_ImageSagital;
        prv_image = an_image;
        prv_image_header = an_image_header;
        prv_cursorPosition = new Point3Dfloat(0.0f,0.0f,0.0f);
        maxtam = Math.max(
                (double)(prv_image_header.getSizeX()),
                (double)(prv_image_header.getSizeY()));
        maxtam = Math.max(maxtam, 
                (double)(prv_image_header.getSizeZ()));
        prv_sizePlanes = new Point3D(
                (double)(prv_image_header.getSizeX() * MAX_TAM_CUBE/maxtam),
                (double)(prv_image_header.getSizeY() * MAX_TAM_CUBE/maxtam),
                (double)(prv_image_header.getSizeZ() * MAX_TAM_CUBE/maxtam));

        prv_middlePlanes = new Point3D(
                ((double)(prv_image_header.getSizeX()))/2.0 +  (double)(prv_image_header.getStartX()),
                ((double)(prv_image_header.getSizeY()))/2.0 +  (double)(prv_image_header.getStartY()),
                ((double)(prv_image_header.getSizeZ()))/2.0 +  (double)(prv_image_header.getStartZ())
                );
    }

    /**
     * Procedure for initialize the 3d scene.
     */
    public synchronized void start(){

        this.prv_scene = DzzD.newScene3D();
        //SOFT software dzzd, JOGL, java OpeGL
	this.prv_render = DzzD.newRender3D(this.getClass(),"SOFT",null);
        //Add the Render3D canvas to the Applet Panel
	this.setLayout(null);
        this.prv_render.getCanvas().addMouseListener(this);
        this.prv_render.getCanvas().addMouseMotionListener(this);
        this.prv_render.getCanvas().addMouseWheelListener(this);
        
	this.add(this.prv_render.getCanvas());
	this.prv_render.setSize(Math.max( this.getSize().width, 400),
                Math.max(this.getSize().height,400),
                1);

        //Set Camera Aspect ratio to 1:1
	this.prv_scene.getCurrentCamera3D().setZoomY(((double)this.prv_render.getWidth())/((double)this.prv_render.getHeight()));
        this.prv_scene.getCurrentCamera3D().getPosition().setZ(-15.0);
        this.prv_scene.getCurrentCamera3D().getRotation().set(0.001, 0.001, 0.001);
	this.prv_render.setCamera3D(this.prv_scene.getCurrentCamera3D());
        this.prv_render.disableRender3DMode(DzzD.RM_TEXTURE_MIPMAP);
        this.prv_render.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        //Thread mainThread=new Thread(this);
        this.prv_run = false;
        //mainThread.start();
    }

    public void destroy(){
            this.prv_run=false;
    }


    /**
     * Procedure for add a plane in 3D scene.
     * @param a_size Weight and Height of plane.
     * @param name Name for this mesh. The final name is "Mesh" + name
     */
    private synchronized void createPlane(Point3D a_size, String name){
        IVertex3D[] vertices;
        vertices= new Vertex3D[8];
        if (a_size.x != 0.0){
            if(a_size.y == 0){
                (vertices[0]=DzzD.newVertex3D()).set(-a_size.x/2,-DELTA_PLANES,-a_size.z/2);	//bottom,near-left
                (vertices[1]=DzzD.newVertex3D()).set(-a_size.x/2,-DELTA_PLANES,a_size.z/2); 	//top,far-left
                (vertices[2]=DzzD.newVertex3D()).set(a_size.x/2,-DELTA_PLANES,a_size.z/2);	//top,far-right
                (vertices[3]=DzzD.newVertex3D()).set(a_size.x/2,-DELTA_PLANES,-a_size.z/2);	//bottom,near-left

                (vertices[4]=DzzD.newVertex3D()).set(a_size.x/2,DELTA_PLANES,-a_size.z/2);	//bottom,near-left
                (vertices[5]=DzzD.newVertex3D()).set(a_size.x/2,DELTA_PLANES,a_size.z/2); 	//top,far-left
                (vertices[6]=DzzD.newVertex3D()).set(-a_size.x/2,DELTA_PLANES,a_size.z/2);	//top,far-right
                (vertices[7]=DzzD.newVertex3D()).set(-a_size.x/2,DELTA_PLANES,-a_size.z/2);	//bottom,near-left
            }else{
                (vertices[0]=DzzD.newVertex3D()).set(-a_size.x/2,-a_size.y/2,-DELTA_PLANES);	//bottom,near-left
                (vertices[1]=DzzD.newVertex3D()).set(-a_size.x/2,a_size.y/2,-DELTA_PLANES); 	//top,far-left
                (vertices[2]=DzzD.newVertex3D()).set(a_size.x/2,a_size.y/2,-DELTA_PLANES);	//top,far-right
                (vertices[3]=DzzD.newVertex3D()).set(a_size.x/2,-a_size.y/2,-DELTA_PLANES);	//bottom,near-left

                (vertices[4]=DzzD.newVertex3D()).set(a_size.x/2,-a_size.y/2,DELTA_PLANES);	//bottom,near-left
                (vertices[5]=DzzD.newVertex3D()).set(a_size.x/2,a_size.y/2,DELTA_PLANES); 	//top,far-left
                (vertices[6]=DzzD.newVertex3D()).set(-a_size.x/2,a_size.y/2,DELTA_PLANES);	//top,far-right
                (vertices[7]=DzzD.newVertex3D()).set(-a_size.x/2,-a_size.y/2,DELTA_PLANES);	//bottom,near-left
               }
        }else{
            (vertices[0]=DzzD.newVertex3D()).set(-DELTA_PLANES,-a_size.y/2,-a_size.z/2);	//bottom,near-left
            (vertices[1]=DzzD.newVertex3D()).set(-DELTA_PLANES,-a_size.y/2,a_size.z/2); 	//top,far-left
            (vertices[2]=DzzD.newVertex3D()).set(-DELTA_PLANES,a_size.y/2,a_size.z/2);	//top,far-right
            (vertices[3]=DzzD.newVertex3D()).set(-DELTA_PLANES,a_size.y/2,-a_size.z/2);	//bottom,near-left

            (vertices[4]=DzzD.newVertex3D()).set(DELTA_PLANES,a_size.y/2,-a_size.z/2);	//bottom,near-left
            (vertices[5]=DzzD.newVertex3D()).set(DELTA_PLANES,a_size.y/2,a_size.z/2); 	//top,far-left
            (vertices[6]=DzzD.newVertex3D()).set(DELTA_PLANES,-a_size.y/2,a_size.z/2);	//top,far-right
            (vertices[7]=DzzD.newVertex3D()).set(DELTA_PLANES,-a_size.y/2,-a_size.z/2);	//bottom,near-left
        }

        IFace3D faces[]= new Face3D[4];
	faces[0]=DzzD.newFace3D(vertices[0],vertices[1],vertices[2]);
	faces[1]=DzzD.newFace3D(vertices[2],vertices[3],vertices[0]);
        faces[2]=DzzD.newFace3D(vertices[4],vertices[5],vertices[6]);
        faces[3]=DzzD.newFace3D(vertices[6],vertices[7],vertices[4]);



        IMesh3D meshPlane;
        meshPlane =DzzD.newMesh3D(vertices,faces);
		meshPlane.getPosition().set(0.0,0.0,0.0);
		meshPlane.setName("Mesh" + name);

        IMaterial materialPlane =DzzD.newMaterial();
		meshPlane.getFace3D(0).setMaterial(materialPlane);
		meshPlane.getFace3D(1).setMaterial(materialPlane);
        meshPlane.getFace3D(2).setMaterial(materialPlane);
        meshPlane.getFace3D(3).setMaterial(materialPlane);
		materialPlane.setDiffuseColor(0x000000);
		materialPlane.setSelfIlluminationLevel(50);

        /*IURLTexture texturePlane;
		texturePlane =DzzD.newURLTexture();
		texturePlane.setBaseURL("file:///c:/tmp/");
		texturePlane.setSourceFile("HELLO.JPG");
        texturePlane.setName("Texture" + name);*/

        IURLTexture texturePlane2;
        int w = 256,h = 256;
        if(name.compareToIgnoreCase("Axial") == 0){
            w = prv_image_header.getSizeX();
            h = prv_image_header.getSizeY();
            if(DEBUG) System.out.println("Axial, w=" + w + ", h=" + h);
        }else if(name.compareToIgnoreCase("Coronal") == 0){
            w = prv_image_header.getSizeX();
            h = prv_image_header.getSizeZ();
            if(DEBUG) System.out.println("Coronal, w=" + w + ", h=" + h);
        }else if(name.compareToIgnoreCase("Sagital") == 0){
            w = prv_image_header.getSizeY();
            h = prv_image_header.getSizeZ();
            if(DEBUG) System.out.println("Sagital, w=" + w + ", h=" + h);
        }

        if(DEBUG)
            System.out.println("nk 3dViewPort.createPlane() H:"+h+" W"+w);
        texturePlane2 =new nkTexture(w,h);
        //texturePlane2 =DzzD.newURLTexture();
        //texturePlane2.setBaseURL("file:///c:/tmp/");
		//texturePlane2.setSourceFile("axial.jpg");
        texturePlane2.setName("Texture" + name);

        materialPlane.setDiffuseTexture(texturePlane2);

		IMappingUV mappingUVPlane=DzzD.newMappingUV();
		mappingUVPlane.setVZoom(1);
		mappingUVPlane.setVOffset(0.0f);
		materialPlane.setMappingUV(mappingUVPlane);
		float mappingAxial[]={0,1,0,0,1,0, 1,0,1,1,0,1,
                              1,1,1,0,0,0, 0,0,0,1,1,1};
		//meshPlane.getFace3D(0).setMappingU(0, TOP_ALIGNMENT) setMappingUV( mappingAxial);
                //meshPlane.get
                setMappingUV(meshPlane.getFace3D(0), new float[]{0,1,0,0,1,0});
                setMappingUV(meshPlane.getFace3D(1), new float[]{1,0,1,1,0,1});
                setMappingUV(meshPlane.getFace3D(2), new float[]{1,1,1,0,0,0});
                setMappingUV(meshPlane.getFace3D(3), new float[]{0,0,0,1,1,1});

        this.prv_scene.addMesh3D(meshPlane);
		this.prv_scene.addMaterial(materialPlane);
		this.prv_scene.addTexture(texturePlane2);
    }

    private void setMappingUV(IFace3D faces, float values[]){
        faces.setMappingU(0, values[0]);
        faces.setMappingV(0, values[1]);
        faces.setMappingU(1, values[2]);
        faces.setMappingV(1, values[3]);
        faces.setMappingU(2, values[4]);
        faces.setMappingV(2, values[5]);
    }

    private void createScene3D()	{

        double AxialXWidth = prv_sizePlanes.x;
        double AxialYWidth = prv_sizePlanes.y;
        double CoronalXWidth = prv_sizePlanes.x;
        double CoronalZWidth = prv_sizePlanes.z;
        double SagitalYWidth = prv_sizePlanes.y;
        double SagitalZWidth = prv_sizePlanes.z;

        createPlane(new Point3D(AxialXWidth,AxialYWidth, 0.0), "Axial");
        createPlane(new Point3D(CoronalXWidth,0.0, CoronalZWidth), "Coronal");
        createPlane(new Point3D(0.0,SagitalYWidth, SagitalZWidth), "Sagital");
        createPlane(new Point3D(0.0,0.0001, 0.0001), "Root");

        prv_scene.getMesh3DByName("MeshRoot").addChild(prv_scene.getMesh3DByName("MeshCoronal"));
        prv_scene.getMesh3DByName("MeshRoot").addChild(prv_scene.getMesh3DByName("MeshSagital"));
        prv_scene.getMesh3DByName("MeshRoot").addChild(prv_scene.getMesh3DByName("MeshAxial"));
        

        
		this.prv_render.setAntialiasLevel(0);
		//Bugs can appear if the camera is perfectly parallale to a face
		this.prv_scene.getCurrentCamera3D().getRotation().setX(Math.PI*0.0001);
        this.prv_scene.setBackgroundColor(0xffffff);
        this.prv_run = false;
	}


    public void run() {
        if(DEBUG) System.out.println("nk3DViewport.run()");
        
        this.createScene3D();
        this.renderSingleFrame();
        //Thread.yield();
        //DzzD.sleep(1);

        changeTexture(prv_imageSagital, prv_scene.getTextureByName("TextureSagital"));
        changeTexture(prv_imageCoronal, prv_scene.getTextureByName("TextureCoronal"));
        changeTexture(prv_imageAxial, prv_scene.getTextureByName("TextureAxial"));

        this.renderSingleFrame();
        this.prv_run = true;
        

    }

     public void renderSingleFrame()	{
		//Set the scene to world space
		this.prv_scene.setScene3DObjectToWorld();

		//Set the scene to active camera space
		this.prv_scene.setScene3DObjectToCamera();

		//Tell the 3D render to compute & draw the frame
		this.prv_render.renderScene3D(this.prv_scene);
    }

    @Override
    public synchronized void paint(Graphics g) {
        if(this.prv_run){
            this.prv_render.setSize(this.getSize().width, this.getSize().height, 1);
            this.renderSingleFrame();
        }
        super.paint(g);
    }


    public void addPositionListener(PositionListener pl) {
        if( null == prv_event_listeners)
        prv_event_listeners = new Vector();
        if( null == pl || prv_event_listeners.contains( pl))
            return;
        prv_event_listeners.addElement( pl);
    }

    public void removePositionListener(PositionListener pl) {
        if( null != prv_event_listeners && null != pl)
	    prv_event_listeners.removeElement( pl);
    }

    public float getOrthoStep() {
        return Float.NaN;
    }

    public int getMaxSliceNumber() {
        return -1;
    }

    public void changeColor(){
        changeTexture(prv_imageCoronal, prv_scene.getTextureByName("TextureCoronal"));
        changeTexture(prv_imageAxial, prv_scene.getTextureByName("TextureAxial"));
        changeTexture(prv_imageSagital, prv_scene.getTextureByName("TextureSagital"));
        renderSingleFrame();
    }

    public synchronized void positionChanged(PositionEvent e) {
        if(e.getXYZ().equals(prv_cursorPosition)== false){
            if(this.prv_run){
                if(e.isYValid() && prv_cursorPosition.y != e.getXYZ().y){   //Coronal
                    changeTexture(prv_imageCoronal, prv_scene.getTextureByName("TextureCoronal"));
                    if(prv_scene.getMesh3DByName("MeshCoronal") != null){
                        prv_scene.getMesh3DByName("MeshCoronal").getPosition().setY( 
                                (Math.ceil(e.getXYZ().y) - prv_middlePlanes.y)
                                * MAX_TAM_CUBE/maxtam);
                    }
                }
                if( e.isZValid() && prv_cursorPosition.z != e.getXYZ().z){   //Axial
                    changeTexture(prv_imageAxial, prv_scene.getTextureByName("TextureAxial"));
                    if(prv_scene.getMesh3DByName("MeshAxial") != null){
                        prv_scene.getMesh3DByName("MeshAxial").getPosition().setZ( 
                                (Math.ceil(e.getXYZ().z) - prv_middlePlanes.z)
                                * MAX_TAM_CUBE/maxtam);
                    }
                }

                if(e.isXValid() && prv_cursorPosition.x != e.getXYZ().x){   //Sagital
                    changeTexture(prv_imageSagital, prv_scene.getTextureByName("TextureSagital"));
                    if(prv_scene.getMesh3DByName("MeshSagital") != null){
                        prv_scene.getMesh3DByName("MeshSagital").getPosition().setX( 
                                (Math.ceil(e.getXYZ().x) - prv_middlePlanes.x)
                                * MAX_TAM_CUBE/maxtam);

                    }
                }

                this.renderSingleFrame();
                prv_cursorPosition.copy(e.getXYZ());

            }

        }else{
            if (DEBUG) System.out.println("nk3DViewport: No change");
        }
        //prv_cursorPosition.copy(e.getXYZ());
    }

    private void changeTexture(SliceImageProducer sp, ITexture it){
        if(it == null) return;
        if(sp == null) return;
        Image resizedImage;
        int it_width = it.getPixelsWidth();
        int it_height = it.getPixelsHeight();
        ImageFilter replicate =
             new ReplicateScaleFilter(it_width, it_height);
          ImageProducer prod =new FilteredImageSource(sp, replicate);
          resizedImage = createImage(prod);
          PixelGrabber grabber =
            new PixelGrabber(resizedImage, 0, 0, it_width, it_height, false);
          try{
            grabber.grabPixels();
          }catch(InterruptedException ie){
              if (DEBUG) System.out.println("nk3DViewport.changeTexture:ERROR= " + ie);
          }
          Object data;
          if(grabber.getPixels() instanceof byte[])
              data = (byte[]) grabber.getPixels();
          else
              data = (int[]) grabber.getPixels();
          int[] pixels = it.getPixels();
          int i ;
          if(data != null && pixels != null){
              for(i = 0; i<it_width*it_height; i++){
                  if(data instanceof byte[])
                    pixels[i] = grabber.getColorModel().getRGB(((byte[])data)[i]) ;
                  else
                      pixels[i] = grabber.getColorModel().getRGB(((int[])data)[i]) ;

              }
          }

    }
    
    @Override
    public void render() {
        renderSingleFrame();
    }

}
