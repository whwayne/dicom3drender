/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.co.unal.bioing.jnukak3d.VolumeRendering.transferFunction;

/**
 *
 * @author jleon
 */
public class TransferFunctionOperations {

    public static int[] circletsToAlphaMap(Circlet startCirclet, Circlet endCirclet,int[] bounds){
        int[] alphaMap=new int[256];

        double startX=(double)(startCirclet.getxCoordinate()-bounds[0])/(double)(bounds[1]-bounds[0]);
        double startY=-1*(double)(startCirclet.getyCoordinate()-bounds[2])/(double)(bounds[2]-bounds[3]);
       
        double endX=((double)(endCirclet.getxCoordinate()-bounds[0])/(double)(bounds[1]-bounds[0]));
        double endY=-1*((double)(endCirclet.getyCoordinate()-bounds[2])/(double)(bounds[2]-bounds[3]));

        double range=endX-startX;
        double step=(endY-startY)/(range*255);
   
        for(int i=0;i<(int)(startX*255);i++)
            alphaMap[i]=0;
        for(int i=(int)(startX*255);i<(int)(endX*255);i++){
            alphaMap[i]=(short)(startY*255);
            startY+=step;
            //System.out.println("counter on"+startY);
        }
        for(int i=(int)(endX*255)+1;i<alphaMap.length;i++)
            alphaMap[i]=0;
        
        return alphaMap;
    }

     public static int[] circletsToColorMap(Circlet startCirclet, Circlet endCirclet,int[] bounds,int selectedColorMap){
        int[] colorMap;
  
        double startX=(double)(startCirclet.getxCoordinate()-bounds[0])/(double)(bounds[1]-bounds[0]);
        double endX=((double)(endCirclet.getxCoordinate()-bounds[0])/(double)(bounds[1]-bounds[0]));

        int startXAsInt=(int)(startX*255);
        int endXAsInt=(int)(endX*255);

        if(selectedColorMap==1)
            colorMap=chanelLine(startCirclet.getFillColor().getRed(), endCirclet.getFillColor().getRed(), startXAsInt, endXAsInt);
        else if(selectedColorMap==2)
            colorMap=chanelLine(startCirclet.getFillColor().getGreen(), endCirclet.getFillColor().getGreen(), startXAsInt, endXAsInt);
        else if(selectedColorMap==3)
            colorMap=chanelLine(startCirclet.getFillColor().getBlue(), endCirclet.getFillColor().getBlue(), startXAsInt, endXAsInt);
        else
            throw new IllegalArgumentException(" selected color map is not a valid argument");

        return colorMap;
     }

    public static int[] chanelLine(int startIntensity, int endIntensity,int startRange,int endrange){
        int[] chanel=new int[256];
        for(int i=0;i<startRange;i++)
            chanel[i]=0;

        double range=endrange-startRange;
        double step=((double)(endIntensity-startIntensity))/(double)range;
        double startIntensityAsDouble=(double)startIntensity;

        for(int i=startRange;i<=endrange;i++){
            chanel[i]=(int)startIntensityAsDouble;
            startIntensityAsDouble+=step;
        }

        for(int i=endrange+1;i<chanel.length;i++)
            chanel[i]=0;

        return chanel;
    }

    /*public static void main (String args[]){
        int test[]=chanelLine(0, 255, 100, 255);
        for(int i=0;i<test.length;i++)
            System.out.println(i+"->"+test[i]);
    }*/
}
