/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.co.unal.bioing.jnukak3d.VolumeRendering.ui;

import edu.co.unal.bioing.jnukak3d.VolumeRendering.sliceBasedVolumeRendering.SliceBasedUI;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;


/**
 *
 * @author jleon
 */
public class SlicingPanel extends JPanel{
    private JSpinner slicingSpiner;

    public SlicingPanel(SliceBasedUI parentListener){
        super();
        this.setLayout(new GridBagLayout());
        
        JLabel slicingLabel=new JLabel("Slicing Factor");

        SpinnerModel model = new SpinnerNumberModel(1,1,40,1);
        slicingSpiner=new JSpinner(model);

        JButton okButton=new JButton("Ok");
        okButton.addActionListener(parentListener);
        okButton.setActionCommand("ChangeSlicingFactor");

        GridBagConstraints c=new GridBagConstraints();
        c.gridx=0;c.gridy=0;
        c.insets=new Insets(5, 5, 5, 5);
        this.add(slicingLabel,c);

        c.gridx=1;c.gridy=0;
        this.add(slicingSpiner,c);

        c.gridx=1;c.gridy=1;
        this.add(okButton,c);
    }

    public int getSlicingFactor(){
        return (Integer)slicingSpiner.getValue();
    }

}
