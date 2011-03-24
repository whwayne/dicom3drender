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
package edu.co.unal.bioing.jnukak3d.Dicom.ui;

import edu.co.unal.bioing.jnukak3d.nkDebug;
import java.util.List;
import java.awt.GridLayout;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import jwf.WizardPanel;

/** 
 * @author Alexander Pinzon Fernandez
 */
public class nkWizardPage2 extends WizardPanel {

    JFileChooser chooser;
    File directory;
    JTextField directoryText;


    /** A default constructor. */
    public nkWizardPage2() {
        setLayout(new GridLayout(0, 1));
        setBorder(new TitledBorder("Directory Choosen"));
        add(new JLabel());
        add(new JLabel());
        add(new JLabel());
        add(new JLabel("Choosen Dicom Directory"));
        directoryText = new JTextField("");
        directoryText.setEditable(false);
        add(directoryText);
        add(new JLabel());
        add(new JLabel());
        add(new JLabel());

        directory = null;
    }

    /** Called when the panel is set. */
    public void display() {

        //if(directory == null) return;

        chooser = new JFileChooser();
        chooser.setDialogTitle("Choose DICOM directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //
        // disable the "All files" option.
        //
        chooser.setAcceptAllFileFilterUsed(false);
        //
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            directory = chooser.getSelectedFile();
            directoryText.setText(chooser.getSelectedFile().toString());
            if(nkDebug.DEBUG)System.out.println("getCurrentDirectory(): "
                    +  chooser.getCurrentDirectory());
            if(nkDebug.DEBUG) System.out.println("getSelectedFile() : "
             +  chooser.getSelectedFile());
          }
        else {
            if(nkDebug.DEBUG) System.out.println("No Selection ");
            
        }
    }

    /** Is there be a next panel?
     * @return true if there is a panel to move to next
     */
    public boolean hasNext() {
        return true;
    }

    /** Called to validate the panel before moving to next panel.
     * @param list a List of error messages to be displayed.
     * @return true if the panel is valid,
     */
    public boolean validateNext(List list) {
        boolean valid = true;
        if(directory == null){
            JOptionPane.showMessageDialog(this,"A Directory must be selected to go forward", "No Directory Selected", JOptionPane.ERROR_MESSAGE);
            valid = false;
            //list.add("Select a DICOM directory first.");
        }
        return valid;
    }

    /** Get the next panel to go to. */
    
    public WizardPanel next() {
        nkWizardPage3 treeDicomFiles2 = new nkWizardPage3(directory);
        treeDicomFiles2.setDir(directory);
        return treeDicomFiles2;
    }

    /** Can this panel finish the wizard?
     * @return true if this panel can finish the wizard.
     */
    public boolean canFinish() {
        return false;
    }

    /** Called to validate the panel before finishing the wizard. Should
     * return false if canFinish returns false.
     * @param list a List of error messages to be displayed.
     * @return true if it is valid for this wizard to finish.
     */
    public boolean validateFinish(List list) {
        return false;
    }

    /** Handle finishing the wizard. */
    public void finish() {
    }

}
