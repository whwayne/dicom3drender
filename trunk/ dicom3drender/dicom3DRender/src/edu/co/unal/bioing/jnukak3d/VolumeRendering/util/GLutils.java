/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.co.unal.bioing.jnukak3d.VolumeRendering.util;

import javax.media.opengl.GLCapabilities;

/**
 *
 * @author jleon
 */
public class GLutils {

    public static GLCapabilities get8BitRGBAHardwareAceleratedCapabilities() {
        GLCapabilities capabilities = new GLCapabilities();
        capabilities.setRedBits(8);
        capabilities.setBlueBits(8);
        capabilities.setGreenBits(8);
        capabilities.setAlphaBits(8);
        capabilities.setHardwareAccelerated(true);
        return capabilities;
    }

}
