/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.co.unal.bioing.jnukak3d.VolumeRendering.ui;

import edu.co.unal.bioing.jnukak3d.VolumeRendering.transferFunction.TransferFunctionCanvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/* ColorChooserDemo.java requires no other files. */
public class ColorChooser extends JPanel implements ChangeListener {

    //ColorChooserDemo
    protected JColorChooser colorChooser;
    protected JPanel previewPanel;
    protected Color startColor=Color.red;
    private Color selectedColor=Color.black;//default color
    JFrame containerFrame;

    public ColorChooser(TransferFunctionCanvas parentListener){
        super(new GridBagLayout());

        //frame to show the color chooser
        containerFrame=new JFrame();
        containerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Set up color chooser for setting text color
        colorChooser = new JColorChooser(startColor);
        AbstractColorChooserPanel[] panels=colorChooser.getChooserPanels();
        colorChooser.removeChooserPanel(panels[0]);
        colorChooser.removeChooserPanel(panels[2]);

        //preview color panel
        previewPanel=new JPanel();
        previewPanel.setPreferredSize(new Dimension(120, 25));
        previewPanel.setBackground(startColor);

        colorChooser.setPreviewPanel(previewPanel);
        colorChooser.getSelectionModel().addChangeListener(this);
        colorChooser.setBorder(BorderFactory.createTitledBorder(
                                             "Choose Circlet Color"));

        JButton okButton=new JButton("Select");
        okButton.addActionListener(parentListener);

        GridBagConstraints c=new GridBagConstraints();
        c.gridx=0;
        c.gridy=0;
        c.gridwidth=2;
        add(colorChooser, c);
        c.gridy=1;
        c.gridwidth=1;
        c.insets=new Insets(5, 10, 5, 0);
        c.anchor=GridBagConstraints.BASELINE_TRAILING;
        add(previewPanel,c);
        c.gridx=1;
        c.insets=new Insets(5, 10, 5, 10);
        add(okButton,c);
    }

    public void stateChanged(ChangeEvent e) {
        Color newColor = colorChooser.getColor();
        previewPanel.setBackground(newColor);
        this.selectedColor=newColor;
    }

    public Color getSelectedColor(){
        return this.selectedColor;
    }

    public void showUI() {
        //Create and set up the content pane.
        this.setOpaque(true); //content panes must be opaque
        containerFrame.setContentPane(this);

        //Display the window.
        containerFrame.pack();
        containerFrame.setVisible(true);
    }

    public void hideUI(){
        containerFrame.setVisible(false);
    }

}


