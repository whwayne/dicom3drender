/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.co.unal.bioing.jnukak3d.VolumeRendering.sliceBasedVolumeRendering;


import edu.co.unal.bioing.jnukak3d.VolumeRendering.GLbase.BaseGlViewer;
import edu.co.unal.bioing.jnukak3d.VolumeRendering.util.GLutils;
import edu.co.unal.bioing.jnukak3d.image3d.nkData3DVolume;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


/**
 *
 * @author jleon
 */
public class SliceBasedVolumeRendering extends BaseGlViewer{

    //TODO clipping???

    //texture
    int numTextures=1;
    int[] textureIds = new int[numTextures];//save here ids of textures
    //private int imageAmount=150;
    private int textureWidth;//i
    private int textureHeight;//j
    private int textureDepth;//k
    private int textureChanels=4;
    private int baseTextureChanels=1;
    private byte[] originalChanels;
    private byte[] transferFunctionModifiedChanels;


    //clipping 
    private static final double FLT_EPSILON=Float.MIN_VALUE;
    private static final double FLT_MAX=Float.MAX_VALUE;
   
   // private int maxClipperZ=imageAmount;

    //volume
    int slicingfactor=1;
    boolean useMinimalSlicingFactor=false;

    //misc
    private boolean regenerateTexture=false;
    private boolean invalid;

    //tansfer maps
    private int[] alphaMap;
    private int[] redMap;
    private int[] greenMap;
    private int[] blueMap;



    public SliceBasedVolumeRendering(int width,int height, int _textureWidth,int _textureHeight,int _textureDepth, byte[] voxels){
        super(width,height,GLutils.get8BitRGBAHardwareAceleratedCapabilities());
        this.textureWidth=_textureWidth;
        this.textureHeight=_textureHeight;
        this.textureDepth=_textureDepth;
        originalChanels=new byte[textureWidth*textureHeight*textureChanels*textureDepth];
        transferFunctionModifiedChanels=new byte[originalChanels.length];
        this.originalChanels=voxels;
        this.invalid=true;//TODO must be change to allow rendering using this constuctor

    }

     public SliceBasedVolumeRendering(int width,int height,nkData3DVolume voxels){
        super(width,height,GLutils.get8BitRGBAHardwareAceleratedCapabilities());
        this.invalid=false;
        loadVoxels(voxels);
    }

    private void loadVoxels(nkData3DVolume voxels){
        this.textureWidth=voxels.getXSize();
        this.textureHeight=voxels.getYSize();
        this.textureDepth=voxels.getZSize();

        int[][][] pixels = (int[][][])voxels.getVoxels();

        int ix,iy,iz, ichanel;
        int acumz=0, acumy=0, pos=0, acumx;
      
        originalChanels=new byte[textureWidth*textureHeight*baseTextureChanels*textureDepth];
        transferFunctionModifiedChanels=new byte[textureWidth*textureHeight*textureDepth*4];// is ALWAYS RGBA

        for(iz = 0; iz<textureDepth; iz++){
            acumz = baseTextureChanels*textureWidth*textureHeight*iz;
            for(iy = 0; iy<textureHeight; iy++){
                acumy = baseTextureChanels*textureWidth*iy;
                for(ix = 0; ix<textureWidth; ix++){
                    acumx = baseTextureChanels *ix;
                    for(ichanel=0; ichanel<baseTextureChanels; ichanel++){
                        pos = acumz + acumy + acumx + ichanel;
                        originalChanels[pos] = (byte)(pixels[iz][iy][ix] >>> (8*ichanel));
                    }
                }
            }
        }

    }

    @Override
    protected void doExtraInitOperations(GL gl){
       setUpDefaultMaps();
       crateInitialTransferFunctionChanesl();
       create3DTexture(gl);

    }

    private void crateInitialTransferFunctionChanesl(){
        for(int i=0;i<transferFunctionModifiedChanels.length;i++)
            transferFunctionModifiedChanels[i]=originalChanels[i/4];

        for(int i=3;i<transferFunctionModifiedChanels.length;i=i+4)
             transferFunctionModifiedChanels[i]=(byte)254;
    }

    private void setUpDefaultMaps(){
        alphaMap=new int[256];
        redMap=new int[256];
        greenMap=new int[256];
        blueMap=new int[256];
        for(int i=0;i<alphaMap.length;i++){
            alphaMap[i]=i;
            redMap[i]=i;
            greenMap[i]=i;
            blueMap[i]=i;
        }

    }

    public void setSlicingFactor(int newSlicingfactor){
        this.slicingfactor=newSlicingfactor;
    }

    public void setNewAlphaMap(int[] newAlphaMap){
        this.alphaMap=newAlphaMap;
        this.regenerateTexture=true;
    }

     public void setNewRedMap(int[] newRedMap){
        this.redMap=newRedMap;
        this.regenerateTexture=true;
    }

    public void setNewGreenMap(int[] newGreenMap){
        this.greenMap=newGreenMap;
        this.regenerateTexture=true;
    }

    public void setNewBlueMap(int[] newBlueMap){
        this.blueMap=newBlueMap;
        this.regenerateTexture=true;
    }

    public void applyColorMaps(){
        int luminance;
        for(int i=3;i<transferFunctionModifiedChanels.length;i=i+4){
            luminance=(int)((originalChanels[i/4]) & 0x00FF);
            //System.out.println("Color Map Luminance: "+luminance);
            transferFunctionModifiedChanels[i-1]=(byte)blueMap[luminance];
            transferFunctionModifiedChanels[i-2]=(byte)greenMap[luminance];
            transferFunctionModifiedChanels[i-3]=(byte)redMap[luminance];
        }
    }

    @Override
    protected void doRotationStuff(){
        this.useMinimalSlicingFactor=true;
    }

    @Override
    protected void doNoRotationStuff() {
        this.useMinimalSlicingFactor=false;
    }

    public void applyAlphaMap(){
       int luminance;
        for(int i=3;i<transferFunctionModifiedChanels.length;i=i+4){
            luminance=(int)((originalChanels[i/4]) & 0x00FF);
            transferFunctionModifiedChanels[i]=(byte)alphaMap[luminance];
        }
    }
  /*  public void loadVoxels(){
        System.out.println("loading voxels");
        BufferedImage im = null;
        int[] rgbs=new int[512*512];
        String filename="";
        for(int i=0;i<textureDepth;i++){
            try{
               if(i<99){
                   //filename="../img/CaraBone/cara_00"+(i+1)+".jpg";
                   //filename="../img/CPQNA/cpqna_00"+(i+1)+".jpg";
                   filename="../img/cpqAsJpeg/cpqJPEG_00"+(i+1)+".jpg";
                   //filename="../img/adomenJpeg/SANCHEZ RIVERA LUIS_00"+(i+1)+".jpg";

               }else{
                   //filename="../img/CaraBone/cara_0"+(i+1)+".jpg";
                   //filename="../img/CPQNA/cpqna_0"+(i+1)+".jpg";
                    filename="../img/cpqAsJpeg/cpqJPEG_0"+(i+1)+".jpg";
                    //filename="../img/adomenJpeg/SANCHEZ RIVERA LUIS_00"+(i+1)+".jpg";
               }
                im = ImageIO.read(getClass().getResource(filename) );
                im.getRGB(0, 0, 512, 512, rgbs, 0, 512);

            }catch(Exception e){
                System.out.println("Error loading texture "+filename);
                //e.printStackTrace();
            }
    
            for(int e=0;e<rgbs.length;e++){
                originalChanels[(i*512*512*4)+4*e]= (byte)((rgbs[e] >>> 0));//red
                originalChanels[(i*512*512*4)+4*e+1]= (byte)((rgbs[e] >>> 8));//green
                originalChanels[(i*512*512*4)+4*e+2]=(byte)((rgbs[e] >>> 16));//blue
                originalChanels[(i*512*512*4)+4*e+3]=(byte)(rgbs[e] >>> 24);
            }
        }
        
        System.out.println("done loading voxels");
        for(int i=0;i<originalChanels.length;i++)
            transferFunctionModifiedChanels[i]=originalChanels[i];
        System.out.println("done copying voxels");
    }*/

    public void create3DTexture(GL gl){
        System.out.println("Cleaning up old texture");
        gl.glDeleteTextures(1, textureIds, 0);

        if(this.maxTextureSize<this.textureDepth){
           JOptionPane.showMessageDialog(null,
            "Selected dataset is too big, and cannot be rendered as a volume",
            "Jnukak Error",JOptionPane.ERROR_MESSAGE);
       }else{
            System.out.println("creating new texture");
            gl.glEnable(GL.GL_TEXTURE_3D);
            gl.glGenTextures(1,textureIds,0);
            gl.glBindTexture(GL.GL_TEXTURE_3D, textureIds[0]);

            set3DTextureParameters(gl);

            ByteBuffer dataAsByteBuffer=ByteBuffer.wrap(transferFunctionModifiedChanels);

            gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT,1);

            gl.glTexImage3D(GL.GL_TEXTURE_3D,
                            0, // Mipmap level.
                            GL.GL_RGBA,// GL.GL_RGBA, // Internal Texel Format,
                            textureWidth, textureHeight,textureDepth,
                            0, //Border
                            GL.GL_RGBA, // External format from image,
                            GL.GL_UNSIGNED_BYTE,
                            dataAsByteBuffer.rewind() // Imagedata as ByteBuffer
            );

            System.out.println("done creating texture");
       }
    }

    private void set3DTextureParameters( GL gl ){
        gl.glTexParameteri(GL.GL_TEXTURE_3D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_3D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_3D, GL.GL_TEXTURE_WRAP_R, GL.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL.GL_TEXTURE_3D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL.GL_TEXTURE_3D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
        gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
    }

    public void gl_clip(GL gl){
        double[] plane=new double[4];

        plane[0] = 1;  plane[1] =  0 ;  plane[2] =  0 ;  plane[3] = FLT_EPSILON ;
        gl.glEnable( GL.GL_CLIP_PLANE0 ) ;
        gl.glClipPlane( GL.GL_CLIP_PLANE0, plane,0 ) ;
        plane[0] = -1 ;  plane[1] =  0 ;  plane[2] =  0 ;  plane[3] = textureWidth - FLT_EPSILON ;
        gl.glEnable( GL.GL_CLIP_PLANE1 ) ;
        gl.glClipPlane( GL.GL_CLIP_PLANE1, plane,0 ) ;

        plane[0] =  0. ;  plane[1] = 1. ;  plane[2] =  0. ;  plane[3] = FLT_EPSILON;
        gl.glEnable( GL.GL_CLIP_PLANE2 ) ;
        gl.glClipPlane( GL.GL_CLIP_PLANE2, plane,0 ) ;
        plane[0] =  0. ;  plane[1] = -1. ;  plane[2] =  0. ;  plane[3] = textureHeight + FLT_EPSILON ;
        gl.glEnable( GL.GL_CLIP_PLANE3 ) ;
        gl.glClipPlane( GL.GL_CLIP_PLANE3, plane,0 ) ;

        plane[0] =  0. ;  plane[1] =  0. ;  plane[2] = 1 ;  plane[3] =FLT_EPSILON ;
        gl.glEnable( GL.GL_CLIP_PLANE4 ) ;
        gl.glClipPlane( GL.GL_CLIP_PLANE4, plane,0 ) ;
        plane[0] =  0. ;  plane[1] =  0. ;  plane[2] = -1. ;  plane[3] =  textureDepth + FLT_EPSILON ;
        gl.glEnable( GL.GL_CLIP_PLANE5 ) ;
        gl.glClipPlane( GL.GL_CLIP_PLANE5, plane,0 ) ;

    }


    public void gl_set(GL gl) {
        // push the relevant parts of the OpenGL state
        gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT   |
                        GL.GL_DEPTH_BUFFER_BIT   |
                        GL.GL_ENABLE_BIT         |
                        GL.GL_LIGHTING_BIT       |
                        GL.GL_POLYGON_BIT        |
                        GL.GL_TEXTURE_BIT);

        // openGL setup
        gl.glEnable (GL.GL_TEXTURE_3D);
        gl.glDisable(GL.GL_LIGHTING);
        gl.glDisable(GL.GL_CULL_FACE);
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);

        // enable alpha blending
        gl.glEnable(GL.GL_BLEND);
        gl.glDepthMask(false);


        gl.glBlendFunc(GL.GL_SRC_ALPHA,GL.GL_ONE_MINUS_SRC_ALPHA);
        //gl.glBlendFunc(GL.GL_CONSTANT_ALPHA,GL.GL_ONE);
    }

    @Override
    public void doPreBuildSceneOperations(GL gl){
        if(this.regenerateTexture){
            create3DTexture(gl);
            this.regenerateTexture=false;
        }
        gl_set (gl) ;
       gl_clip(gl) ;
    }

    @Override
    public void doPostBuildSceneOperations(GL gl){
        // unsets the openGL attributes and clipping planes
        gl_unclip(gl) ;
        gl_unset(gl) ;
    }

    @Override
    public void buildScene(GL gl) {
        int nslices=textureDepth;
        if(!this.useMinimalSlicingFactor)
            nslices*=slicingfactor;
        else
              nslices=nslices/4;

        // gets the direction of the observer
        double[]  gl_model=new double[16] ; // = { 1.0f,0.0f,0.0f,0.0f, 0.0f,0.0f,-1.0f,0.0f, 0.0f,-1.0f,0.0f,0.0f, 0.0f,0.0f,0.0f,1.0f } ;
        double[]  gl_proj=new double[16] ; // = { 1.0f,0.0f,0.0f,0.0f, 0.0f,1.0f,0.0f,0.0f, 0.0f,0.0f,1.0f,0.0f, 0.0f,0.0f,0.0f,1.0f } ;
        int[] gl_view =new int [4];
        gl.glGetDoublev (GL.GL_MODELVIEW_MATRIX , gl_model, 0 );
        gl.glGetDoublev (GL.GL_PROJECTION_MATRIX, gl_proj,  0 );
        gl.glGetIntegerv(GL.GL_VIEWPORT         , gl_view,  0 );

        //--------------------------------------------------//
        // gets the bounding box of the grid in the screen coordinates
        double xmin=FLT_MAX, xmax=-FLT_MAX, ymin=FLT_MAX, ymax=-FLT_MAX, zmin=FLT_MAX, zmax=-FLT_MAX;
        for( int i = 0; i < 8; ++i ){

        double bbx = ( (i&1) !=0 ) ? (double)textureWidth : (double)0.0 ;
        double bby = ( (i&2) !=0 ) ? (double)textureHeight : (double)0.0 ;
        double bbz = ( (i&4) !=0 ) ? (double)textureDepth : (double)0.0 ;

        double[] winPos=new double[3];
        glu.gluProject( bbx,bby,bbz, gl_model,0, gl_proj,0, gl_view,0, winPos,0 ) ;

        if( winPos[0] < xmin ) xmin = winPos[0];
        if( winPos[0] > xmax ) xmax = winPos[0];

        if( winPos[1] < ymin ) ymin = winPos[1];
        if( winPos[1] > ymax ) ymax = winPos[1];

        if( winPos[2] < zmin ) zmin = winPos[2];
        if( winPos[2] > zmax ) zmax = winPos[2];
        }//at the end the (x,y,z)(min,max) contain the limits of the vertex values of the cube on screen coordinates

        // world to tex coordinates
        double fx = 1.0 / textureWidth ;
        double fy = 1.0 / textureHeight;
        double fz = 1.0 / textureDepth ;

        //--------------------------------------------------//
        // draw each slice of the texture in the viewer coordinates
        float dz = (float)( (zmax-zmin) / nslices ) ;//slice "thicknes"
        float z  = (float)zmax - dz/2.0f ;

        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glBegin( GL.GL_QUADS );{
        for( int n =nslices-1 ; n >= 0 ; --n, z -= dz ){
            double[] point=new double[3];
            glu.gluUnProject( xmin,ymin,z, gl_model,0, gl_proj,0, gl_view,0, point,0 ) ;
            gl.glTexCoord3d( fx*point[0], fy*point[1], fz*point[2] );
            gl.glVertex3dv( point,0 ) ;

            glu.gluUnProject( xmax,ymin,z, gl_model,0, gl_proj,0, gl_view,0, point,0 ) ;
            gl.glTexCoord3d( fx*point[0], fy*point[1], fz*point[2] );
            gl.glVertex3dv( point,0 ) ;

            glu.gluUnProject( xmax,ymax,z, gl_model,0, gl_proj,0, gl_view,0, point,0 ) ;
            gl.glTexCoord3d( fx*point[0], fy*point[1], fz*point[2] );
            gl.glVertex3dv( point,0 ) ;

            glu.gluUnProject( xmin,ymax,z, gl_model,0, gl_proj,0, gl_view,0, point,0 ) ;
            gl.glTexCoord3d( fx*point[0], fy*point[1], fz*point[2] );
            gl.glVertex3dv( point,0 ) ;
        }
        }gl.glEnd() ; // GL_QUADS

    }

    public void gl_unclip(GL gl){
        // disable cube clip plane
        gl.glDisable( GL.GL_CLIP_PLANE0 ) ;
        gl.glDisable( GL.GL_CLIP_PLANE1 ) ;
        gl.glDisable( GL.GL_CLIP_PLANE2 ) ;
        gl.glDisable( GL.GL_CLIP_PLANE3 ) ;
        gl.glDisable( GL.GL_CLIP_PLANE4 ) ;
        gl.glDisable( GL.GL_CLIP_PLANE5 ) ;
    }

    public void gl_unset(GL gl){
        gl.glDisable (GL.GL_TEXTURE_3D);
        gl.glPopAttrib() ;
    }

    /**
     * @return the invalid
     */
    public boolean isInvalid() {
        return invalid;
    }

    /*public static void main (String args[]){
        SliceBasedVolumeRendering canvas=new SliceBasedVolumeRendering(800, 500);

        JFrame frame = new JFrame("Base Viewer");
        frame.getContentPane().add(canvas, BorderLayout.CENTER);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        canvas.requestFocus();
    }*/
}
