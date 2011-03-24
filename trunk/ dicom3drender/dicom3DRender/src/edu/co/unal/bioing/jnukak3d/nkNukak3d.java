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

import edu.co.unal.bioing.jnukak3d.ui.nkAbout;
import edu.co.unal.bioing.jnukak3d.view2d.nkVolViewer;
import com.pixelmed.dicom.AttributeList;
import edu.co.unal.bioing.jnukak3d.Dicom.io.nkDicomImport;
import edu.co.unal.bioing.jnukak3d.Dicom.io.nkDicomSort;
import edu.co.unal.bioing.jnukak3d.Dicom.nkDicomNodeTree;
import edu.co.unal.bioing.jnukak3d.event.nkEvent;
import edu.co.unal.bioing.jnukak3d.event.nkEventListener;
import edu.co.unal.bioing.jnukak3d.ui.nkMenuTool;
import edu.co.unal.bioing.jnukak3d.ui.nkTool;
import edu.co.unal.bioing.jnukak3d.ui.nkToolBar;
import edu.co.unal.bioing.jnukak3d.ui.event.nkToolEvent;
import edu.co.unal.bioing.jnukak3d.ui.event.nkToolListener;
import edu.co.unal.bioing.jnukak3d.Dicom.ui.nkWizardDicomImport;
import edu.co.unal.bioing.jnukak3d.VolumeRendering.sliceBasedVolumeRendering.SliceBasedUI;
import edu.co.unal.bioing.jnukak3d.VolumeRendering.sliceBasedVolumeRendering.SliceBasedVolumeRendering;
import edu.co.unal.bioing.jnukak3d.image3d.nkBufferedImage3dCreator;
import edu.co.unal.bioing.jnukak3d.image3d.nkData3DImage;
import edu.co.unal.bioing.jnukak3d.image3d.nkData3DVolume;
import edu.co.unal.bioing.jnukak3d.image3d.nkImage3d;
import edu.co.unal.bioing.jnukak3d.image3d.nkImage3dCreator;
import edu.co.unal.bioing.jnukak3d.plugin.extern.nkDockingFramesEngine;
import edu.co.unal.bioing.jnukak3d.plugin.extern.nkJavaSwigEngine;
import edu.co.unal.bioing.jnukak3d.plugin.extern.nkSanawareJavaDockingEngine;
import edu.co.unal.bioing.jnukak3d.plugin.nkDockManager;
import edu.co.unal.bioing.jnukak3d.plugin.nkKernel;
import edu.co.unal.bioing.jnukak3d.ui.nkBinaryTreeLayout;
import edu.co.unal.bioing.jnukak3d.ui.nkMenuItem;
import edu.co.unal.bioing.jnukak3d.ui.nkToolComboBox;
import edu.co.unal.bioing.jnukak3d.ui.nkToolSlider;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import jiv.ColorCoding;
import jiv.Data3DVolume;
import jiv.VolumeHeader;
import edu.co.unal.bioing.jnukak3d.pacsaccess.nkPacsBrowser;
import javax.swing.SwingWorker;

/**
 *
 * @author Alexander Pinzon Fernandez
 */
public class nkNukak3d extends JFrame implements nkToolListener, nkEventListener{
    protected static final boolean DEBUG= nkDebug.DEBUG;

    private final int imagebits=8;// esto deberia ser como una variable global, ya que debe coordinarse con la operacion de window width and center
    private final int MAX_WIDTH_SLIDER_POSITION=(int)Math.pow(2, imagebits);
    private final int MAX_CENTER_SLIDER_POSITION=(int)Math.pow(2, imagebits);
    //private final int MAX_CENTER_SLIDER_POSITION=(int)Math.pow(2, imagebits-1);

    private int windowCenterSet=0;
    private int windowWidthSet=0;
    private nkToolSlider widthSlider,centerSlider;

    private JTabbedPane bookVolViewers;
    private Vector<nkVolViewer> listnkVolViewer = new Vector<nkVolViewer>();
    private nkKernel m_kernel;

    private nkToolBar nkToolBarTools;
    private nkToolBar nkToolBarConfiguration;
    private nkToolBar nkToolBarWindowLevel;
    private nkBinaryTreeLayout nkTree;

    public enum EventIdentifier{
                ID_OPEN_FILE,                       /**< Open file. */
		ID_OPEN_FILE_DICOM,                 /**< Open stack of image Dicom. */
		ID_OPEN_FILE_MESH3D,                /**< Open object 3D. */
		ID_OPEN_FILE_VOL,                   /**< Open file vol. */
                ID_OPEN_FILE_RAW,                   /**< Open file raw. */
		ID_ABOUT,                           /**< Show Dialog About Nukak3d. */
		ID_ABOUT_MAC,                       /**< Show Dialog About Nukak3d (Only for Mac ) . */
		ID_TREE,                            /**< Event launch by nkToolBarMac. */
		ID_AREA,                            /**< Calc area . */
		ID_prBoundingBox,                   /**< Show/hide bounding box. */
		ID_BOXWIDGET,                       /**< Show/hide box widget. */
		ID_CAMERAPOS,                       /**< Show position of camera. */
		ID_CAMPLANES,                       /**< Update planes with position of camera. */
		ID_CLOSE,                           /**< Close application. */
		ID_CLOSE_ALL,                       /**< Close application. */
		ID_COLDET,                          /**< Collision detection. */
		ID_DICOMFIND,                       /**< Dicom C-FIND. */
		ID_DICOMSERVER,                     /**< Dicom listener SCP. */
		ID_ENDOCAMOBB,                      /**< Enable virtual endoscopy. */
		ID_ENDOCAM,                         /**< Enable Virtual endoscopy. */
		ID_FILPOLYDECIMATE,                 /**< To reduce numbers of polygon. */
		ID_FILPOLYDEFORM,                   /**< Deform Mesh. */
		ID_FILPOLYNORMALS,                  /**< Recalc normals of faces. */
		ID_FILPOLYSMOOTH,                   /**< Smooth mesh. */
		ID_FILPOLYTRIANGLE,                 /**< Triangle mesh. */
		ID_FILVOLGAUSSIAN,                  /**< Gaussian filter. */
		ID_FILVOLGRADIENT,                  /**< Gradient filter. */
		ID_FILVOLMEDIAN,                    /**< Median filter. */
		ID_FILVOLTHRESHOLD,                 /**< Threshold filter. */
		ID_SAVE_MESH3D,                     /**< Save object 3D. */
		ID_SAVE_VOL,                        /**< Save vol. */
		ID_JOYSTICK,                        /**< Input device to Joystick. */
		ID_LSLEVELSETSCOMPLETO,             /**< Level sets. */
		ID_MARCHING_CUBES,                  /**< Surface reconstruction with Marching Cubes. */
		ID_NAVENDOSCOPE,                    /**< Mode of navigation camera endoscopy. */
		ID_NAVFLIGHT,                       /**< Flight Camera. */
		ID_NAVJOYSTICK,                     /**< Joystick Camera. */
		ID_NAVRESET,                        /**< Reset camera position and orientation. */
		ID_NAVTRACKBALL,                    /**< Trackball Camera. */
		ID_NAVUNICAM,                       /**< Unicam Camera. */
		ID_LOOKUP_TABLE,                    /**< lookup table. */
                ID_INFORMATION_IMAGE,               /**< Image information. */
		ID_INFORMATION_POLYGON,             /**< Object 3D information. */
		ID_INFORMATION_VIDEO_CARD,          /**< VideoCard information. */
		ID_RESET_LOOKUP_TABLE,              /**< Reset window and level. */
		ID_EXIT,                            /**< Close application. */
		ID_SETLANGUAGE,                     /**< Change user language. */
		ID_SNAPSHOT3D,                      /**< Snapshot canvas 3D. */
		ID_SNAPSHOTAXIAL,                   /**< Snapshot axial view. */
		ID_SNAPSHOTCORONAL,                 /**< Snapshot coronal view. */
		ID_SNAPSHOTSAGITAL,                 /**< Snapshot sagital view. */
		ID_STEREO_ACTIVE,                   /**< Enable stereoscopic vision. */
		ID_STEREO_MORE_SEPARATION,          /**< Stereoscopic vision more separation. */
		ID_STEREO_LESS_SEPARATION,          /**< Stereoscopic vision less separation. */
		ID_STEREO_PASSIVE,                  /**< Active stereoscopy vision. */
		ID_VOLVIEWER_RENDERING_ESCALAR,     /**< Ortogonal planes view. */
		ID_VOLVIEWER_RENDERING_MRC_MIP,     /**< Ray Tracing MIP. */
		ID_VOLVIEWER_RENDERING_MRC_COMP,    /**< Ray Tracing COMPOSITE. */
		ID_VOLVIEWER_RENDERING_MRC_ISO,     /**< Ray Tracing ISOSURFACE. */
		ID_VOLVIEWER_RENDERING_TEXTURE,     /**< Texture mpping rendering. */
		ID_FPS,                             /**< Frames per second. */
		ID_LAST_LOOKUP_TABLE,               /**< Event's for lookup table. */
                ID_LOOKUP_TABLE_GREY,
                ID_LOOKUP_TABLE_HOTMETAL,
                ID_LOOKUP_TABLE_SPECTRAL,
                ID_LOOKUP_TABLE_RED,
                ID_LOOKUP_TABLE_GREEN,
                ID_LOOKUP_TABLE_BLUE,
                ID_LOOKUP_TABLE_MNI_LABELS,
                ID_WIDTH_SLIDER,
                ID_CENTER_SLIDER,
                ID_PRESETS_COMBO,
                ID_VOLUME_MENU_ITEM,
                ID_PACS_MENU_ITEM
    }

    
    public nkNukak3d(){
        m_kernel = nkKernel.getInstance();
        
        loadUIPlugins();
        setMenuBar();
        setToolBars();

        bookVolViewers = new JTabbedPane();

        buildLayOut();
        
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        setTitle("Jnukak3d");
       /* this.pack();
        this.setLocationRelativeTo(null);*/

    }

    public void buildLayOut(){
        nkTree = new nkBinaryTreeLayout(nkBinaryTreeLayout.NODE_ROOT);
        nkTree.splitVertical(0.60);
        nkTree.getLeft().splitHorizontal(0.3);
        nkTree.getLeft().getLeft().setData(nkToolBarTools, "Tools", null);
        nkTree.getLeft().getRight().splitHorizontal(0.5);
        nkTree.getLeft().getRight().getRight().setData(nkToolBarConfiguration, "Configuration", null);
        nkTree.getLeft().getRight().getLeft().setData(nkToolBarWindowLevel, "WindowLevel", null);
        nkTree.getRight().setData(bookVolViewers, "VolViewers", null);

        nkDockManager dockManager = m_kernel.getDockServer().getDefaultDockEngine().createDockManager();
        dockManager.config(this, this.getContentPane());
        dockManager.readTree(nkTree);
        dockManager.pack();
    }

    public void loadUIPlugins(){
        m_kernel.loadPlugin("DockingFrames", new nkDockingFramesEngine());
        m_kernel.loadPlugin("SanawareJavaDocking", new nkSanawareJavaDockingEngine());
        m_kernel.loadPlugin("javax.swing", new nkJavaSwigEngine());
        m_kernel.getDockServer().setDefaultEngine("SanawareJavaDocking");
    }

    public void setMenuBar(){
        JMenuBar  jMBMenu = new JMenuBar();
        JMenu jMFile = new JMenu("Menu");
        JMenuItem jMIExit = new JMenuItem("Exit");
        jMIExit.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        jMFile.add(jMIExit);
        jMBMenu.add(jMFile);
        JMenu jMMenuDicom = new JMenu("Dicom");
        JMenuItem dicomStudyItem=new JMenuItem("Open Local Dicom Study");
        dicomStudyItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openDicom();
            }
        });
        jMMenuDicom.add(dicomStudyItem);
        jMBMenu.add(jMMenuDicom);
        JMenu jMTools = new JMenu("Tools");

        nkMenuItem mIVolumeRendering=new nkMenuItem("Render Volume",EventIdentifier.ID_VOLUME_MENU_ITEM.ordinal());
        mIVolumeRendering.addnkToolListener(this);
        jMTools.add(mIVolumeRendering);

        nkMenuItem mIPacsAccess=new nkMenuItem("Retrive Dicom Study From Pacs", EventIdentifier.ID_PACS_MENU_ITEM.ordinal());
        mIPacsAccess.addnkToolListener(this);
        jMMenuDicom.add(mIPacsAccess);

        jMBMenu.add(jMTools);
        JMenu jMHelp = new JMenu("Help");
        JMenuItem jMIAbout = new JMenuItem("About JNukak3D");
        jMIAbout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                nkAbout nka = new nkAbout();
                nka.setVisible(true);
            }
        });
        jMHelp.add(jMIAbout);
        jMBMenu.add(jMHelp);
        setJMenuBar(jMBMenu);
    }

   
    public void setToolBars(){
        nkToolBarTools = new nkToolBar("Tools");
        nkMenuTool nkMenuVolume = new nkMenuTool("Open volume images");
        nkTool nkToolOpenVol = new nkTool("Volume", "Open a single file.", EventIdentifier.ID_OPEN_FILE_RAW.ordinal());

        nkToolOpenVol.addnkToolListener(this);

        nkTool nkToolOpenDicom = new nkTool("DICOM", "Open a Dicom Directory.", EventIdentifier.ID_OPEN_FILE_DICOM.ordinal());

        nkToolOpenDicom.addnkToolListener(this);

        nkMenuVolume.addnkTool(nkToolOpenVol);
        nkMenuVolume.addnkTool(nkToolOpenDicom);
        nkToolBarTools.addnkMenu(nkMenuVolume);

        nkToolBarConfiguration = new nkToolBar("Configuration");
        nkMenuTool nkMenuLookupTable = new nkMenuTool("Lookup Table");
        nkTool nkToolLTGrey  = new nkTool("Grey", "Grey Lookup Table.", EventIdentifier.ID_LOOKUP_TABLE_GREY.ordinal());
        nkTool nkToolLTHotmetal   = new nkTool("Hotmetal", "Hotmetal Lookup Table.", EventIdentifier.ID_LOOKUP_TABLE_HOTMETAL.ordinal());
        nkTool nkToolLTSpectral  = new nkTool("Spectral", "Spectral Lookup Table.", EventIdentifier.ID_LOOKUP_TABLE_SPECTRAL.ordinal());
        nkTool nkToolLTRed  = new nkTool("Red", "Red Lookup Table.", EventIdentifier.ID_LOOKUP_TABLE_RED.ordinal());
        nkTool nkToolLTGreen  = new nkTool("Green", "Green Lookup Table.", EventIdentifier.ID_LOOKUP_TABLE_GREEN.ordinal());
        nkTool nkToolLTBlue  = new nkTool("Blue", "Blue Lookup Table.", EventIdentifier.ID_LOOKUP_TABLE_BLUE.ordinal());
        nkTool nkToolLTMni_Labels  = new nkTool("MNI Labels", "MNI Labels Lookup Table.", EventIdentifier.ID_LOOKUP_TABLE_MNI_LABELS.ordinal());

        nkToolLTGrey.addnkToolListener(this);
        nkToolLTHotmetal.addnkToolListener(this);
        nkToolLTSpectral.addnkToolListener(this);
        nkToolLTRed.addnkToolListener(this);
        nkToolLTGreen.addnkToolListener(this);
        nkToolLTBlue.addnkToolListener(this);
        nkToolLTMni_Labels.addnkToolListener(this);

        nkMenuLookupTable.addnkTool(nkToolLTGrey);
        nkMenuLookupTable.addnkTool(nkToolLTHotmetal);
        nkMenuLookupTable.addnkTool(nkToolLTSpectral);
        nkMenuLookupTable.addnkTool(nkToolLTRed);
        nkMenuLookupTable.addnkTool(nkToolLTGreen);
        nkMenuLookupTable.addnkTool(nkToolLTBlue);
        nkMenuLookupTable.addnkTool(nkToolLTMni_Labels);

        nkToolBarConfiguration.addnkMenu(nkMenuLookupTable);

        //Window Level tool
        nkToolBarWindowLevel = new nkToolBar("WindowLevel");
        nkMenuTool nkWindowLevel = new nkMenuTool("Window Level");

        JPanel windowLevelPanel=new JPanel(new GridBagLayout());
        widthSlider=new nkToolSlider(JSlider.HORIZONTAL,1, MAX_WIDTH_SLIDER_POSITION, 1,EventIdentifier.ID_WIDTH_SLIDER.ordinal());
        centerSlider = new nkToolSlider(JSlider.HORIZONTAL,1, MAX_CENTER_SLIDER_POSITION, 1,EventIdentifier.ID_CENTER_SLIDER.ordinal());
        //centerSlider = new nkToolSlider(JSlider.HORIZONTAL,-1* MAX_CENTER_SLIDER_POSITION, MAX_CENTER_SLIDER_POSITION, 1,EventIdentifier.ID_CENTER_SLIDER.ordinal());

        centerSlider.setPreferredSize(new Dimension(210, 10));
        widthSlider.setPreferredSize(new Dimension(210, 10));
        JLabel presetLabel=new JLabel("Preset");
        JLabel labelWidth=new JLabel("W ");
        JLabel labelCenter=new JLabel("C ");
        nkToolComboBox presetCombo=new nkToolComboBox(nkToolComboBox.getPresetNames(),EventIdentifier.ID_PRESETS_COMBO.ordinal());
        presetCombo.setPreferredSize(new Dimension(100, 22));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=0;
        c.insets=new Insets(10, 0, 0, 0);
        windowLevelPanel.add(labelWidth, c);
        c.gridy=1;
        windowLevelPanel.add(labelCenter, c);
        c.gridx=1;
        windowLevelPanel.add(centerSlider,c);
        c.gridy=0;
        windowLevelPanel.add(widthSlider, c);
        c.gridx=0;
        c.gridy=2;
        c.insets.top=8;
        c.insets.bottom=10;
        windowLevelPanel.add(presetLabel, c);
        c.gridx=1;
        c.insets.top=10;

        windowLevelPanel.add(presetCombo, c);

        nkWindowLevel.add(windowLevelPanel);

        widthSlider.addnkToolListener(this);
        centerSlider.addnkToolListener(this);
        presetCombo.addnkToolListener(this);

        nkToolBarWindowLevel.addnkMenu(nkWindowLevel);
    }

    public void nkToolInvoke(nkToolEvent e) {
        if(DEBUG)
            System.out.println("nkNukak3d.nkToolInvoke(); Event: " + e);
        
        if(e.getId() == EventIdentifier.ID_OPEN_FILE_DICOM.ordinal()){
            openDicom();
            return;
        }else if(e.getId() == EventIdentifier.ID_OPEN_FILE_RAW.ordinal()){
            openRawFile();
            return;
        }else if(e.getId() == EventIdentifier.ID_PACS_MENU_ITEM.ordinal()){
            pacsAccessUtilInit();
            return;
        }
        if(  (listnkVolViewer.size()==0 || listnkVolViewer.get(0)==null) ){
            if( !e.getName().equals("SliderEvent") )
                JOptionPane.showMessageDialog(null, "A Dicom Study must be loaded First", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        nkVolViewer minkVol = listnkVolViewer.get(bookVolViewers.getSelectedIndex());
        if(e.getId() == EventIdentifier.ID_LOOKUP_TABLE_GREY.ordinal())
            minkVol.changePallete(new ColorCoding().get8bitColormap(ColorCoding.GREY,0,255));
        else if(e.getId() == EventIdentifier.ID_LOOKUP_TABLE_BLUE.ordinal())
            minkVol.changePallete(new ColorCoding().get8bitColormap(ColorCoding.BLUE,0,255));
        else if(e.getId() == EventIdentifier.ID_LOOKUP_TABLE_GREEN.ordinal())
            minkVol.changePallete(new ColorCoding().get8bitColormap(ColorCoding.GREEN,0,255));
        else if(e.getId() == EventIdentifier.ID_LOOKUP_TABLE_HOTMETAL.ordinal())
            minkVol.changePallete(new ColorCoding().get8bitColormap(ColorCoding.HOTMETAL,0,255));
        else if(e.getId() == EventIdentifier.ID_LOOKUP_TABLE_MNI_LABELS.ordinal())
            minkVol.changePallete(new ColorCoding().get8bitColormap(ColorCoding.MNI_LABELS,0,255));
        else if(e.getId() == EventIdentifier.ID_LOOKUP_TABLE_RED.ordinal())
            minkVol.changePallete(new ColorCoding().get8bitColormap(ColorCoding.RED,0,255));
        else if(e.getId() == EventIdentifier.ID_LOOKUP_TABLE_SPECTRAL.ordinal())
            minkVol.changePallete(new ColorCoding().get8bitColormap(ColorCoding.SPECTRAL,0,255));
        else if(e.getId() == EventIdentifier.ID_WIDTH_SLIDER.ordinal()){
              JSlider source = (JSlider)e.getSource();
              this.windowWidthSet=source.getValue();
              minkVol.changePallete(getNewPallete(this.windowWidthSet, this.windowCenterSet));
        }
        else if(e.getId() == EventIdentifier.ID_CENTER_SLIDER.ordinal()){
            JSlider source = (JSlider)e.getSource();
            this.windowCenterSet=source.getValue();
            minkVol.changePallete(getNewPallete(this.windowWidthSet, this.windowCenterSet));
        }
        else if(e.getId() == EventIdentifier.ID_PRESETS_COMBO.ordinal()){
            JComboBox cb = (JComboBox)e.getSource();
            String preset = (String)cb.getSelectedItem();
            for (nkToolComboBox.windowLevelpresets wlp : nkToolComboBox.windowLevelpresets.values()){
                if(wlp.name().equals(preset)){
                    if(DEBUG)
                        System.out.println(" Switching to "+wlp.width()+" "+wlp.center());
                   
                     minkVol.changePallete(getNewPallete(wlp.width(), wlp.center()));
                     setSliders( wlp.width() , wlp.center() );
                }
            }
        }else if(e.getId() == EventIdentifier.ID_VOLUME_MENU_ITEM.ordinal()){
            int canvasWidth=800;
            int canvasHeight=600;
            try{
                SliceBasedVolumeRendering vr=new SliceBasedVolumeRendering(canvasWidth, canvasHeight, minkVol.getPrv_image());
                new SliceBasedUI(vr);
            }catch(UnsatisfiedLinkError ule){
                JOptionPane.showMessageDialog(null, "Cannot start volume rendering tool, unable to find JOGL libraries", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void pacsAccessUtilInit(){
        nkPacsBrowser browser=new nkPacsBrowser();
        browser.addnkEventListener(this);
        browser.setVisible(true);
    }

    public IndexColorModel getNewPallete(int width, int center){
        byte[] gray = new byte[(int)Math.pow(2, imagebits)];
        double lowerBound=center-0.5-(width-1)/2;
        double  upperBound=center-0.5+(width-1)/2;
        if(DEBUG)
            System.out.println("BOUNDS "+lowerBound+" to "+upperBound);
        if(DEBUG)
            System.out.println("ARGS "+width+" to "+center);

        for(int i = 0; i < gray.length; i++){
            if(i<=lowerBound){gray[i]=(byte)0; }
            else if(i>upperBound){gray[i]=(byte)255; }
            else{
                double testVar=(double)(((i - (center - 0.5)) / (width-1) + 0.5) * (255 - 0)+ 0 );
                gray[i]= (byte)( testVar );
            }
        }
        return new IndexColorModel(imagebits,gray.length,gray,gray,gray);
    }

    public IndexColorModel getDefaultPallete(){
        int size=(int)Math.pow(2, imagebits);
        byte[] gray = new byte[size];

        for(int i = 0; i < gray.length; i++){
            double counterAsDouble=(double)(i);
            counterAsDouble/=size;
            counterAsDouble*=255;
            gray[i]=(byte)(counterAsDouble);

        }
        if(DEBUG)
            System.out.println("TEST defaut pallete Built");
        return new IndexColorModel(imagebits,gray.length,gray,gray,gray);
    }

    private void setSliders(int width,int center){
        this.widthSlider.setValue(width);
        this.centerSlider.setValue(center);
    }

    public void openRawFile(){
        JFileChooser jf = new JFileChooser();

        int returnVal = JFileChooser.APPROVE_OPTION;
        File file;
        if (jf.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            file = jf.getSelectedFile();
        }else{
            return;
        }

        try{
            
            File file2;
            JFileChooser jf2 = new JFileChooser();
            if (jf2.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                file2 = jf2.getSelectedFile();
            } else {
                return;
            }
            String choppedFilename;

        // extension without the dot
        String ext;

        // where the last dot is. There may be more than one.
        int dotPlace = file2.getName().lastIndexOf ( '.' );

        if ( dotPlace >= 0 )
        {
            // possibly empty
            choppedFilename = file2.getName().substring( 0, dotPlace );

            // possibly empty
            ext = file2.getName().substring( dotPlace + 1 );
        }else
        {
            // was no extension
            choppedFilename = file2.getName();
            ext = "";
        }
            VolumeHeader vh = new VolumeHeader(file.toURI().toURL());
            VolumeHeader vh2 = new VolumeHeader(0, 0, 0,
                    (int)vh.getStepX(), (int)vh.getStepY(), (int)vh.getStepZ(),
                    vh.getSizeX(), vh.getSizeY(), vh.getSizeZ(),
                    vh.toString() , vh.getImage_low(), vh.getImage_high());
            Data3DVolume d3d = new Data3DVolume(vh2, file2.toURI().toURL(), vh2, choppedFilename, 1);


            nkVolViewer nv = new nkVolViewer(this, vh, d3d);
            //RAW FILE
          //  nv.changePallete(getDefaultPallete());
            bookVolViewers.addTab("Name", nv);
            //minkVol.setVisible(true);
            //minkVol.setSize(640, 480);
            listnkVolViewer.add(nv);

        }catch(Exception e){

        }

    }
    
    public void openDicom(){
        nkWizardDicomImport nkWDI = new nkWizardDicomImport(this);
        nkWDI.addnkEventListener(this);
        nkWDI.setVisible(true);
    }



    private ArrayList<AttributeList> getAtributesForSelectedNode(nkDicomNodeTree ndt,nkDicomImport nki){
        String keyStudy, keySerie, keyAcquisition;
        ArrayList<AttributeList> attributeList;
        if(ndt == null){
            attributeList = nki.getDicomAcquisition(0,0,0);
            }else{
            if(ndt.getType()>= ndt.TYPE_STUDY){
                keyStudy = ndt.getStudyId();
                if(ndt.getType()>=ndt.TYPE_SERIE){
                    keySerie = ndt.getSerieId();
                    if(ndt.getType()>=ndt.TYPE_ACQUISITION){
                        keyAcquisition = ndt.getAcquisitionId();
                        attributeList = nki.getDicomAcquisition(keyStudy, keySerie, keyAcquisition);
                        System.out.println("INFO: Fully specified dicom load");
                    }else{
                        //TODO no acquisition specified always the firs one on te series?
                        attributeList = nki.getDicomAcquisition(keyStudy, keySerie, 0);
                        System.out.println("INFO: Dicom load without acquisition");
                    }
                }else{
                    //same as above?
                    attributeList = nki.getDicomAcquisition(keyStudy, 0, 0);
                    System.out.println("INFO: Dicom load without acquisition & series");
                }
            }else{
                //same as above?
                attributeList = nki.getDicomAcquisition(0, 0, 0);
                System.out.println("INFO: Noting Sprecified for the dicom load");
            }
        }
        return attributeList;
    }


    /**
     * this method loads the dicom study to be displayed by Jnukak3d
     * @param e
     */
    public void nkEventInvoke(nkEvent e) {
        System.out.println("Gauge Must Be Displayed Now");

        nkDicomNodeTree ndt = (nkDicomNodeTree)e.getAttribute("nkDicomNodeTreeSelected");
        nkDicomImport nki = (nkDicomImport)e.getAttribute("nkDicomImportSelected");
        ArrayList<AttributeList> attributeList;
        
        attributeList=getAtributesForSelectedNode(ndt,nki);

        nki=null;
        ndt=null;
             
        //TODO failure?
        if(attributeList == null) return;

        System.out.println("No deleay here right1?");
       
        AttributeList [] alList = new AttributeList [attributeList.size()];
       
        attributeList.toArray(alList);
        Arrays.sort(alList , new nkDicomSort());

    
        nkImage3dCreator nkIC = new nkBufferedImage3dCreator(alList);


 
        nkImage3d mi3d = nkIC.createImage3d();
        System.out.println("Deleay  IS here right1");
    
        try{
            mi3d.getWidth(null);
            mi3d.getHeight(null);
            mi3d.getDepth(null);
            VolumeHeader vh = new VolumeHeader(0,0,0,1,1,1,mi3d.getWidth(null), mi3d.getHeight(null), mi3d.getDepth(null),"xyz", 0f,1f);
            System.out.println("Deleay  IS here right2");

            if(mi3d==null)
                System.err.println("nkNukak3d mi3d is null");
            if(vh==null)
                System.err.println("nkNukak3d mi3d is null");

       
            nkData3DVolume n3d = new nkData3DImage(mi3d, vh, "name");
            System.out.println("Deleay  IS here right3");
                            
            nkVolViewer minkVol = new nkVolViewer(this, vh, n3d);
     
            //minkVol.changePallete(WindowCenterAndWidth.getIndexColorModelFromVOI(mi3d.getVoiLutTransformArgs()));

            //use a default window level value that should give "a not bad result" for most dicom images
            minkVol.changePallete(getNewPallete((int)Math.pow(2, imagebits)-1, (int)Math.pow(2, imagebits-1)));
            setSliders((int)Math.pow(2, imagebits)-1, (int)Math.pow(2, imagebits-1));
            this.windowCenterSet=(int)Math.pow(2, imagebits-1);
            this.windowWidthSet=(int)Math.pow(2, imagebits)-1;
            //This is ok aswell could be usefull
            //minkVol.changePallete(getDefaultPallete());
            
            bookVolViewers.addTab("Name", minkVol);
            //minkVol.setVisible(true);
            //minkVol.setSize(640, 480);
            listnkVolViewer.add(minkVol);
            
        }catch(Exception e4){
            System.out.println("nkNukak3d.nkEventInvoke()"+e4.toString());
            e4.printStackTrace();
        }
    }

   /* private class StudyLoader extends SwingWorker<Integer, Void> {
        @Override
        protected Void doInBackground() {
            long heads = 0;
            long total = 0;
            Random random = new Random();
            while (!isCancelled()) {
                total++;
                if (random.nextBoolean()) {
                    heads++;
                }
                publish(new FlipPair(heads, total));
            }
            return null;
        }

        @Override
        protected void process(List<FlipPair> pairs) {
            FlipPair pair = pairs.get(pairs.size() - 1);
            headsText.setText(String.format("%d", pair.heads));
            totalText.setText(String.format("%d", pair.total));
            devText.setText(String.format("%.10g",
                    ((double) pair.heads)/((double) pair.total) - 0.5));
        }
    }*/

        
}



