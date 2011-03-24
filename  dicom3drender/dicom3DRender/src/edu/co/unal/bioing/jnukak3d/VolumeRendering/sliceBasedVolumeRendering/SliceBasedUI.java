/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.co.unal.bioing.jnukak3d.VolumeRendering.sliceBasedVolumeRendering;

import edu.co.unal.bioing.jnukak3d.VolumeRendering.ui.SlicingPanel;
import edu.co.unal.bioing.jnukak3d.VolumeRendering.ui.TransfeFunctionPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;



/**
 *
 * @author jleon
 */
public class SliceBasedUI implements ActionListener{

    private SliceBasedVolumeRendering GLcanvas;
    private TransfeFunctionPanel transferFunctionUI;
    private SlicingPanel slicingWindow;

    JFrame TransferFunctionFrame;
    JFrame GLCanvasFrame;
    JFrame SlicingFrame;

    public SliceBasedUI(SliceBasedVolumeRendering _canvas){
        JMenuBar menuBar=new JMenuBar();
        JMenu fileMenu=new JMenu("File");
        JMenu toolsMenu=new JMenu("Tools");

        JMenuItem slicingItem=new JMenuItem("Slicing Factor");
        slicingItem.addActionListener(this);
        slicingItem.setActionCommand("SlicingWindow");
        JMenuItem tranferFunctionItem=new JMenuItem("Show TransferFunction");
        tranferFunctionItem.addActionListener(this);
        tranferFunctionItem.setActionCommand("ShowTransferFunction");
        JMenuItem closeItem=new JMenuItem("Close");
        closeItem.addActionListener(this);
        closeItem.setActionCommand("Close");

        fileMenu.add(closeItem);
        toolsMenu.add(slicingItem);
        toolsMenu.add(tranferFunctionItem);
        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);

        GLcanvas=_canvas;
        transferFunctionUI=new TransfeFunctionPanel(this);

        GLCanvasFrame = new JFrame("Slice Based Vr");
        GLCanvasFrame.getContentPane().add(GLcanvas, BorderLayout.CENTER);
        GLCanvasFrame.setJMenuBar(menuBar);

        GLCanvasFrame.pack();
        GLCanvasFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        GLCanvasFrame.setVisible(true);
        GLcanvas.requestFocus();

        TransferFunctionFrame=new JFrame("Transfer Function");
        TransferFunctionFrame.getContentPane().add(transferFunctionUI, BorderLayout.CENTER);
        TransferFunctionFrame.pack();
        TransferFunctionFrame.setLocation((int)(GLCanvasFrame.getLocation().getX()), (int)(GLCanvasFrame.getLocation().getY())+GLCanvasFrame.getHeight());
        TransferFunctionFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        TransferFunctionFrame.setVisible(false);

        slicingWindow=new SlicingPanel(this);
        SlicingFrame=new JFrame("Slicing factor");
        SlicingFrame.getContentPane().add(slicingWindow);
        SlicingFrame.pack();
        SlicingFrame.setLocationRelativeTo(GLCanvasFrame);
        SlicingFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        SlicingFrame.setVisible(false);

    }

    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("NewTransferFunction")){
            int[] alphaMap=transferFunctionUI.getTranferFunctionCanvas().getAlphaMap();
            int[] redMap=transferFunctionUI.getTranferFunctionCanvas().getColorMap(1);
            int[] greenMap=transferFunctionUI.getTranferFunctionCanvas().getColorMap(2);
            int[] blueMap=transferFunctionUI.getTranferFunctionCanvas().getColorMap(3);

            GLcanvas.setNewAlphaMap(alphaMap);
            GLcanvas.applyAlphaMap();

            GLcanvas.setNewRedMap(redMap);
            GLcanvas.setNewGreenMap(greenMap);
            GLcanvas.setNewBlueMap(blueMap);
            GLcanvas.applyColorMaps();
        }else if(e.getActionCommand().equals("Close")){
            System.exit(0);
        }else if(e.getActionCommand().equals("ShowTransferFunction")){
            TransferFunctionFrame.setVisible(true);
        }else if(e.getActionCommand().equals("SlicingWindow"))
            SlicingFrame.setVisible(true);
        else if(e.getActionCommand().equals("ChangeSlicingFactor")){
            int slicingFactor=slicingWindow.getSlicingFactor();
            GLcanvas.setSlicingFactor(slicingFactor);
            SlicingFrame.setVisible(false);
        }else if(e.getActionCommand().equals("removeFunctionSegment")){
            transferFunctionUI.getTranferFunctionCanvas().removeCirlcets();
        }else if(e.getActionCommand().equals("addFunctionSegment")){
            transferFunctionUI.getTranferFunctionCanvas().addCirlcets();
        }else
            System.out.println("Unknow Action Command"+e.getActionCommand());
    }

    public final static void main(String[] args) {
      // new SliceBasedUI();
        
    }
}

