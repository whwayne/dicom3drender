package edu.co.unal.bioing.jnukak3d.VolumeRendering.transferFunction;


import edu.co.unal.bioing.jnukak3d.VolumeRendering.ui.ColorChooser;
import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class TransferFunctionCanvas extends Canvas implements MouseListener, MouseMotionListener, ActionListener{
    private int width=650;
    private int height=250;

    private int xAxisMargin=30;
    private int yAxisMargin=40;

    private int fontHeight=12;

    private int yAxisSize=height-50;
    private int xAxisSize=width-70;

    private int circletRadius=8;

    private BasicStroke defaultStroke=new BasicStroke(1.3f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_BEVEL, 1f, new float[]{1}, 0.0f);
    private BasicStroke dashedDtroke =new BasicStroke(1.3f, BasicStroke.CAP_ROUND,BasicStroke.JOIN_BEVEL, 1f, new float[]{6,3}, 0.0f);

    private Circlet[] circletArray;
    private ColorChooser colorChooser;

    private int colorFor;

    public TransferFunctionCanvas(){
        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(new Color(237, 236, 186));
        addMouseListener(this);
        addMouseMotionListener(this);
        setUpCirclets(6);
        colorChooser=new ColorChooser(this);
    }

    public void actionPerformed(ActionEvent e) {
        colorChooser.hideUI();
        circletArray[colorFor].setFillColor(this.colorChooser.getSelectedColor());
        repaint();
    }
    

    @Override
    public void paint(final Graphics g){
        super.paint(g);
        
        Graphics2D g2D=(Graphics2D) g;
        g2D.setStroke(this.defaultStroke);
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        
        //x axis
        g2D.drawLine(yAxisMargin, height-xAxisMargin, yAxisMargin+xAxisSize, height-xAxisMargin);
        g2D.drawString("Luminace", (width/2)-30, height-xAxisMargin+fontHeight);
        g2D.drawString("0", yAxisMargin-(fontHeight), height-xAxisMargin+fontHeight);
        g2D.drawString("255", yAxisMargin+xAxisSize, height-xAxisMargin+fontHeight);

        //y axis
        g2D.drawLine(yAxisMargin, height-xAxisMargin, yAxisMargin, (height-xAxisMargin)-yAxisSize);
        g2D.drawString("100%", yAxisMargin-(3*fontHeight), height-(yAxisMargin+yAxisSize)+fontHeight/2);


        //Draw circlets
        for(int i=0;i<circletArray.length;i++){
            g2D.setColor(circletArray[i].getFillColor());
            if(circletArray[i].isSelected()){
                g2D.fillOval(circletArray[i].getxCoordinate()-(circletRadius/2), circletArray[i].getyCoordinate()-(circletRadius/2), circletRadius, circletRadius);
            }
            else{
                g2D.drawOval(circletArray[i].getxCoordinate()-(circletRadius/2), circletArray[i].getyCoordinate()-(circletRadius/2), circletRadius, circletRadius);
            }
            g2D.setStroke(this.dashedDtroke);
            g2D.drawLine(circletArray[i].getxCoordinate(), circletArray[i].getyCoordinate(), circletArray[i].getxCoordinate(), height-xAxisMargin);
            g2D.setStroke(this.defaultStroke);
        }
  
         g2D.setColor(Color.black);

        Paint normalPaint=g2D.getPaint();

        for(int i=0;i<circletArray.length;i=i+2){
            //draw line inbetween circlets
            GradientPaint gardient=new GradientPaint(circletArray[i].getxCoordinate(),
                                                 circletArray[i].getyCoordinate(),
                                                 circletArray[i].getFillColor(),
                                                 circletArray[i+1].getxCoordinate(),
                                                 circletArray[i+1].getyCoordinate(),
                                                 circletArray[i+1].getFillColor());
            g2D.setPaint(gardient);
            g2D.drawLine(circletArray[i].getxCoordinate(), circletArray[i].getyCoordinate(), circletArray[i+1].getxCoordinate(),circletArray[i+1].getyCoordinate());

        }

        //Alpha Chanel label
        g2D.setPaint(normalPaint);
        AffineTransform at = new AffineTransform();
        at.setToRotation(-Math.PI/2.0, width/2.0, height/2.0);
        Point2D originalPoint=new Point2D.Double();
        originalPoint.setLocation((double)((width-yAxisMargin)+fontHeight), (double)(height-(yAxisSize)+55));
        g2D.setTransform(at);
        at.transform(originalPoint, originalPoint);
        g2D.drawString("Opacity", (int)originalPoint.getX(), (int)originalPoint.getY());
    }

    public void removeCirlcets(){
        if(circletArray.length>0){
            Circlet[] newCircletArray=new Circlet[circletArray.length-2];
            for(int i=0;i<circletArray.length-2;i++)
                newCircletArray[i]=circletArray[i];
            circletArray=newCircletArray;

            repaint();
        }
    }

    public void addCirlcets(){
        Circlet[] newCircletArray=new Circlet[circletArray.length+2];
            for(int i=0;i<circletArray.length;i++)
                newCircletArray[i]=circletArray[i];
        Circlet lowerTempCirclet=new Circlet(yAxisMargin+4, (height-xAxisMargin-4));
        Circlet upperTempCirclet=new Circlet(yAxisMargin+30, (height/2-xAxisMargin-4));
        newCircletArray[circletArray.length]=lowerTempCirclet;
        newCircletArray[circletArray.length+1]=upperTempCirclet;
        circletArray=newCircletArray;
        repaint();
    }

    public void setUpCirclets(int numCirclets){
        if((numCirclets%2)!=0)
            throw new IllegalArgumentException("numCirclets MUST be pair");
        circletArray=new Circlet[numCirclets];

        int xBaseMargin=yAxisMargin+4;
        int xMarginIncrement=(yAxisMargin+xAxisSize)/numCirclets;

        Circlet tempCirclet;
        for(int i=0;i<numCirclets;i++){
            if((i%2)==0){
                tempCirclet=new Circlet(xBaseMargin+(i*xMarginIncrement), (height-xAxisMargin-4));
            }else{
                tempCirclet=new Circlet(xBaseMargin+(i*xMarginIncrement), (height-xAxisMargin)-yAxisSize);
            }
            circletArray[i]=tempCirclet;
        }
    }

    public void mouseExited(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {
        for(int i=0;i<circletArray.length;i++){
             circletArray[i].setSelected(false);
        }
        repaint();
    }

    public void mouseMoved(MouseEvent e) {

    }

    public void mouseDragged(MouseEvent e) {
       for(int i=0;i<circletArray.length;i++){
            if(circletArray[i].isSelected()) {

                if(i%2==1){
                    //System.out.println("Upper Circlet");
                    if(e.getX()>circletArray[i-1].getxCoordinate() && e.getX()<yAxisMargin+xAxisSize)
                        circletArray[i].setxCoordinate(e.getX());
                    if(e.getY()<height-xAxisMargin && e.getY()>(height-xAxisMargin)-yAxisSize)
                        circletArray[i].setyCoordinate(e.getY());
                    break;
                }else{
                    //System.out.println("Lower Circlet");
                    if(e.getX()>yAxisMargin && e.getX()<circletArray[i+1].getxCoordinate())
                        circletArray[i].setxCoordinate(e.getX());
                    if(e.getY()<height-xAxisMargin && e.getY()>(height-xAxisMargin)-yAxisSize)
                        circletArray[i].setyCoordinate(e.getY());
                    break;
                }
                
            }
        }
       
       repaint();
    }

    public void mousePressed(MouseEvent e) {
        for(int i=0;i<circletArray.length;i++){
            int dxCirclet=(circletArray[i].getxCoordinate())-e.getX();
            int dyCirclet=(circletArray[i].getyCoordinate())-e.getY();

            double distanceToCirclet=Math.sqrt(Math.pow(dxCirclet, 2)+Math.pow(dyCirclet, 2));

            if(distanceToCirclet<=(circletRadius/2) && e.getButton()==1){
                circletArray[i].setSelected(true);
                break;
            }else if(distanceToCirclet<=(circletRadius/2) && e.getButton()==3){//just changing color
                colorFor=i;
                colorChooser.showUI();
                break;
            }
        }
        this.repaint();
    }

    public void mouseClicked(MouseEvent e){
      
    }

    public int[] getAlphaMap(){
        int[] bounds=new int[4];
        bounds[0]=yAxisMargin;
        bounds[1]=yAxisMargin+xAxisSize;
        bounds[2]=height-xAxisMargin;
        bounds[3]=(height-xAxisMargin)-yAxisSize;

        //superpose the resulting maps from each pair of circlets on a single map
        int[] result=new int[256];
        for(int i=0;i<circletArray.length;i=i+2){
            int[] tempAlpha=TransferFunctionOperations.circletsToAlphaMap(this.circletArray[i], this.circletArray[i+1],bounds);
            for(int e=0;e<255;e++){
                result[e]+=tempAlpha[e];
            }
        }

        //trim values values above 255 could be buggy
        for(int e=0;e<255;e++){
            if(result[e]>255)
            result[e]=255;
        }
        return result;
    }

    public int[] getColorMap(int desiredColorMap){
        int[] bounds=new int[4];
        bounds[0]=yAxisMargin;
        bounds[1]=yAxisMargin+xAxisSize;
        bounds[2]=height-xAxisMargin;
        bounds[3]=(height-xAxisMargin)-yAxisSize;

        //superpose the resulting maps from each pair of circlets on a single map
        int[] result=new int[256];
        for(int i=0;i<circletArray.length;i=i+2){
            int[] tempColor=TransferFunctionOperations.circletsToColorMap(this.circletArray[i], this.circletArray[i+1], bounds, desiredColorMap);
            for(int e=0;e<255;e++){
                result[e]+=tempColor[e];
            }
        }

        return result;
    }

}

