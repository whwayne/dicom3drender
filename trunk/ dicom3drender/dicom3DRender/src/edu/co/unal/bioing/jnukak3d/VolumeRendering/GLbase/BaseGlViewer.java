package edu.co.unal.bioing.jnukak3d.VolumeRendering.GLbase;

import com.sun.opengl.util.FPSAnimator;
import edu.co.unal.bioing.jnukak3d.VolumeRendering.util.GLutils;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.media.opengl.DebugGL;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jleon
 */
public class BaseGlViewer extends GLCanvas implements GLEventListener, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener{
     //opengl stuff
    protected GLU glu;
    protected FPSAnimator animator;
    protected int fps = 1;

    //misc stuff
    float[] background={0.9f,0.5f,0.2f,1.0f};
    protected int maxTextureSize;

    //mouse start positions
    private int mouseStartX;
    private int mouseStartY;

    //rotation stuff
    private int rotX;
    private int rotY;
    private int windowWidth;
    private int windowHeight;
    private int lastRotX=0;
    private int lastRotY=0;

    //camera stuff
    private int camX=200;
    private int camY=200;

    //"Zoom" stuff
    protected int cameraDistance=700;
    protected int zoomFactor=5;
    protected int movementFactor=4;

    //render stuff
    private boolean running;

     public BaseGlViewer(int width, int height, GLCapabilities capabilities) throws UnsatisfiedLinkError{
        super(capabilities);
        
        setSize(width, height);

        addGLEventListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);

        this.windowWidth=width;
        this.windowHeight=height;

        running=false;

    }

    public void startRendering(){
        this.running=true;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==37)
            camX=camX-1*movementFactor;
        else if(e.getKeyCode()==38)
            camY=camY+1*movementFactor;
        else if(e.getKeyCode()==39)
            camX=camX+1*movementFactor;
        else if(e.getKeyCode()==40)
            camY=camY-1*movementFactor;
        else
            ;
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseStartX=e.getX();
        mouseStartY=e.getY();

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //System.out.println("Moving camera");
        int x=e.getX();
        int y=e.getY();

        rotX+=(x-mouseStartX) * 180 / windowWidth;
        rotY+=(y-mouseStartY) * 180 / windowHeight;

        mouseStartX=x;
        mouseStartY=y;

        // TODO low res DIsplay here
        this.display();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height){
        GL gl = drawable.getGL();

        gl.glViewport(0, 0, width, height);

        this.windowWidth=width;
        this.windowHeight=height;
    }

    @Override
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
        throw new UnsupportedOperationException("Changing display is not supported.");
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseMoved(MouseEvent e) {
       
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
       this.cameraDistance+=zoomFactor*e.getWheelRotation();
    }

    @Override
    public void init(GLAutoDrawable drawable){
        drawable.setGL(new DebugGL(drawable.getGL()));
        GL gl = drawable.getGL();

        // Define "clear" color.
        gl.glClearColor(background[0],background[1],background[2],background[3]);

        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        gl.glHint(GL.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);

        glu = new GLU();

        // Start animator (which should be a field).
        animator= new FPSAnimator(this, fps);
        animator.start();

        int[] maxTexture=new int[1];
        gl.glGetIntegerv(GL.GL_MAX_3D_TEXTURE_SIZE, maxTexture, 0);
        this.maxTextureSize=maxTexture[0];
        //System.out.println("Max 3d texture Supported size"+this.maxTextureSize);

        doExtraInitOperations(gl);

    }

    protected void doExtraInitOperations(GL gl){

    }

    @Override
    public void display(GLAutoDrawable drawable){
        if (!animator.isAnimating()) {
            return;
        }

        GL gl = drawable.getGL();
        //clear the screen
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        setCamera(gl, glu, cameraDistance);


        gl.glRotated(rotX, 0, 1, 0);
        gl.glRotated(rotY, 1, 0, 0);

        if(lastRotX!=rotX || lastRotY!=rotY)
            doRotationStuff();
        else
            doNoRotationStuff();
        lastRotX=rotX;
        lastRotY=rotY;

        doPreBuildSceneOperations(gl);
        buildScene(gl);
        doPostBuildSceneOperations(gl);
        
     
    }

    protected void doRotationStuff(){
        
    }

    protected void doNoRotationStuff(){

    }

    public void doPreBuildSceneOperations(GL gl){
    }

    public void doPostBuildSceneOperations(GL gl){
    }

    //must be overriden
    public void buildScene(GL gl){
        gl.glColor3f(0.9f, 0.5f, 0.2f);
        gl.glBegin(GL.GL_TRIANGLES);
            gl.glColor3f(1.0f, 0.0f, 0.0f);
            gl.glVertex3f(-20, -20, 0);
            gl.glColor3f(0.0f, 1.0f, 0.0f);
            gl.glVertex3f(20, -20, 0);
            gl.glColor3f(0.0f, 0.0f, 1.0f);
            gl.glVertex3f(0, 20, 0);
        gl.glEnd();
    }

    private void setCamera(GL gl, GLU glu, float distance) {
        // Change to projection matrix.
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();

        // Perspective.
        float widthHeightRatio = (float) getWidth() / (float) getHeight();
        glu.gluPerspective(45, widthHeightRatio, 10, 1000);
        glu.gluLookAt(camX, camY, distance, camX, camY, 0, 0, 1, 0);

        // Change back to model view matrix.
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }


    
    public static void main (String args[]){
        BaseGlViewer canvas=new BaseGlViewer(800, 500,GLutils.get8BitRGBAHardwareAceleratedCapabilities());

        JFrame frame = new JFrame("Base Viewer");
        frame.getContentPane().add(canvas, BorderLayout.CENTER);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        canvas.requestFocus();
    }
}
