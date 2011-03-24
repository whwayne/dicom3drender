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

import edu.co.unal.bioing.jnukak3d.nkUtil;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;
import java.net.URL;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import jwf.WizardPanel;

/**
 *
 * @author Alexander Pinzon Fernandez
 */
public class nkWizardPage1 extends WizardPanel{

    private JLabel blankSpace;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JLabel jLabel9;

    private JLabel welcomeTitle;
    private JPanel contentPanel;

    private JLabel iconLabel;
    private ImageIcon icon;

    private final WizardPanel chooseDirectory =
            new nkWizardPage2();

    public nkWizardPage1() {

        iconLabel = new JLabel();
        contentPanel = getContentPanel();
        contentPanel.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));

        icon = nkUtil.getImageIcon("edu.co.unal.bioing.jnukak3d.Dicom.ui.nkWizardPage1", "../../resources/icons/clouds.jpg");

        setLayout(new java.awt.BorderLayout());

        if (icon != null)
            iconLabel.setIcon(icon);

        iconLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));

        add(iconLabel, BorderLayout.WEST);

        JPanel secondaryPanel = new JPanel();
        secondaryPanel.add(contentPanel, BorderLayout.NORTH);
        add(secondaryPanel, BorderLayout.CENTER);
    }


    private JPanel getContentPanel() {

        JPanel contentPanel1 = new JPanel();
        JPanel jPanel1 = new JPanel();

        welcomeTitle = new JLabel();
        blankSpace = new JLabel();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        jLabel5 = new JLabel();
        jLabel7 = new JLabel();
        jLabel6 = new JLabel();
        jLabel8 = new JLabel();
        jLabel9 = new JLabel();

        contentPanel1.setLayout(new java.awt.BorderLayout());

        welcomeTitle.setFont(new java.awt.Font("MS Sans Serif", Font.BOLD, 12));
        welcomeTitle.setText("        Open Dicom Volume wizard!");
        contentPanel1.add(welcomeTitle, java.awt.BorderLayout.NORTH);

        jPanel1.setLayout(new java.awt.GridLayout(0, 1));

        jPanel1.add(blankSpace);
        jLabel1.setText("        With this wizard you can select a local folder, in");
        jPanel1.add(jLabel1);
        jLabel2.setText("        order to see all aviable DICOM series on it, from a ");
        jPanel1.add(jLabel2);
        jLabel3.setText("        selected series a volume will be displayed");
        jPanel1.add(jLabel3);
        jLabel4.setText(" ");
        jPanel1.add(jLabel4);
        jLabel5.setText("        Click on Next to Choose a Directory");
        jPanel1.add(jLabel5);
      /*  jLabel7.setText("'Finish' button, and it depends on the user's entries as to how they ");
        jPanel1.add(jLabel7);
        jLabel6.setText("traverse the path). That's not the case with this example, however.");
        jPanel1.add(jLabel6);
        jPanel1.add(jLabel8);
        jLabel9.setText("Press the 'Next' button to continue....");
        jPanel1.add(jLabel9);*/

        contentPanel1.add(jPanel1, java.awt.BorderLayout.CENTER);

        return contentPanel1;

    }

    @Override
    public void finish() {
        
    }

    @Override
    public boolean validateFinish(List list) {
        return true;
    }

    @Override
    public boolean validateNext(List list) {
        return true;
    }

    @Override
    public boolean canFinish() {
        return true;
    }


    @Override
    public WizardPanel next() {
        return chooseDirectory;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public void display() {
        
    }

    

}
