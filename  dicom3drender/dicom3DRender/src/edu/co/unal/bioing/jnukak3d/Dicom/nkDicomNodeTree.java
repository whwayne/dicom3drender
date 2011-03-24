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

package edu.co.unal.bioing.jnukak3d.Dicom;

/**
 *
 * @author Alexander Pinzon Fernandez
 */
public class nkDicomNodeTree{
        public final static int TYPE_ROOT = 0;
        public final static int TYPE_STUDY = 1;
        public final static int TYPE_SERIE = 2;
        public final static int TYPE_ACQUISITION = 3;
        public final static int TYPE_IMAGE = 4;
        String studyId;
        String serieId;
        String acquisitionId;
        String directory;
        String text;
        int type;

        public nkDicomNodeTree(String text, String studyId, String serieId, String acquisitionId, String directory, int type) {
            this.text = text;
            this.studyId = studyId;
            this.serieId = serieId;
            this.acquisitionId = acquisitionId;
            this.directory = directory;
            this.type = type;
        }

        public String getText() {
            return text;
        }

        public String getStudyId() {
            return studyId;
        }

        public String getSerieId() {
            return serieId;
        }

        public String getAcquisitionId() {
            return acquisitionId;
        }

        public int getType() {
            return type;
        }

        @Override
        public String toString() {
            String cad = "";
            if(getType() >= TYPE_STUDY) cad = cad + studyId;
            if(getType() >= TYPE_SERIE) cad = cad + " : " + serieId;
            if(getType() >= TYPE_ACQUISITION) cad = cad + " : " + acquisitionId;
            return cad.length() == 0 ? text: cad;
        }



    }