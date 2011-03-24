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

package edu.co.unal.bioing.jnukak3d.ui;

import edu.co.unal.bioing.jnukak3d.nkUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

/**
 *
 * @author Alexander Pinzon Fernandez
 */
public class nkAbout extends JFrame{

    public nkAbout() {
        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints c=new GridBagConstraints();
        c.gridx=0;
        c.gridy=0;
        c.gridheight=3;

        ImageIcon logo=nkUtil.getImageIcon("edu.co.unal.bioing.jnukak3d.ui.nkAbout", "../resources/icons/clouds_Small.jpg");
        JPanel iconPanel=new JPanel();
        JLabel iconLabel=new JLabel();
        iconLabel.setIcon(logo);
        iconPanel.add(iconLabel,BorderLayout.CENTER);
        iconPanel.setPreferredSize(new Dimension(logo.getIconWidth(),logo.getIconHeight()));

        getContentPane().add(iconPanel,c);
        c.gridx=1;
        c.gridheight=1;
        JLabel title=new JLabel("JNukak 3d");
        title.setFont(new Font(Font.SERIF, Font.BOLD, 14));
        c.insets=new Insets(15, 15, 0, 10);
        c.anchor=GridBagConstraints.NORTHWEST;
        getContentPane().add(title,c);

        JTextArea textDescription=new JTextArea("Nukak about Nukak about Nukak about \n" +
                "Nukak about Nukak about Nukak about  \n" +
                "Nukak about Nukak about Nukak about \n\n" +
                "Bioingenium Research Group\n" +
                "http://www.bioingenium.unal.edu.co");
        textDescription.setEditable(false);
        textDescription.setBackground(null);
        c.gridy=1;
        c.insets.top=15;
        getContentPane().add(textDescription,c);

        JButton okButton=new JButton("Ok");
        okButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        }
                );
        c.gridy=2;
        c.anchor=GridBagConstraints.BASELINE_TRAILING;
        c.insets.right=10;
        getContentPane().add(okButton,c);


        //setSize(300, 200);
        this.pack();
        this.setTitle("About");
        this.setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    }
}
