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

package net.dzzd.core;

import edu.co.unal.bioing.jnukak3d.nkDebug;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import net.dzzd.access.IProgressListener;
import net.dzzd.access.IURLTexture;
import net.dzzd.utils.Log;

/**
 *
 * @author Alexander Pinzon Fernandez
 */
public final class nkTexture extends Texture implements ImageObserver,IURLTexture,Runnable {
    String baseURL;				//Texture base URL
	String sourceFile;			//Source file name
	private Image imageLoad;	//Source picture
	private boolean reload; 	//True if texture need to be reloaded
    private int width;
    private int height;
    private static boolean DEBUG=nkDebug.DEBUG;


	public nkTexture (int a_width, int a_height)
	{
		super();
        this.width = a_width;
        this.height = a_height;
		this.sourceFile=null;
		//this.baseURL=null;
		this.reload=true;
	}

	public void build()
	{

		if(this.reload)
			this.load();
		else
			super.build();

	}


	public void  run(){
		//if(this.sourceFile==null) return;
		//if(this.baseURL==null) return;

		//Log.log("Loading : " + this.baseURL+this.sourceFile.replace(' ','+'));

		Toolkit t =Toolkit.getDefaultToolkit();
        int w = this.width;
        int h = this.height;
        int pix[] = new int[w * h];
        int index = 0;
        for (int y = 0; y < h; y++) {
                if(DEBUG)
                    System.out.println(" nkTexture.run()H:"+h);
                int red = 255 ;
                for (int x = 0; x < w; x++) {
                int blue = 255;
                pix[index++] = (255 << 24) | (red << 16) | blue;
            }
        }
        this.imageLoad = t.createImage(new MemoryImageSource(w, h, pix, 0, w));


		if(this.imageLoad == null)
		{
			this.endLoading(true);
			return;
		}

		if(t.prepareImage(imageLoad,-1,-1,this))
		{
			this.endLoading(false);
			return;
		}

    	int flag=0;
    	do
    	{
			try
		  	{
		  		//System.out.println("check " + sourceFile);
		  		Thread.sleep(10);
		  		Thread.yield();
		  	}
		  	catch(InterruptedException ie)
		  	{
		  		this.endLoading(false);
		  	}


    		flag=t.checkImage(imageLoad,-1,-1,this);

    	}
    	while( (flag & ( ImageObserver.ALLBITS |  ImageObserver.ABORT |  ImageObserver.ERROR)) ==0);

    	if((flag & ImageObserver.ALLBITS) !=0)
	   		this.endLoading(false);
		else
			this.endLoading(true);
	}

   void endLoading(boolean error)
	{
		if(!error)
		{
			//Log.log("Loading finished : ("+this.baseURL+this.sourceFile+")");
			this.imageToTexturePixels();
			this.setError(false);
			this.setFinished(true);
			this.build();
			return;
		}
		this.setError(true);
		this.setFinished(true);

		Log.log("Loading error (nkTexture)");
	}

	public boolean imageUpdate(Image img,int infoflags,int x,int y,int width,int height)
	{
		  	switch(infoflags)
		  	{
		  		case ImageObserver.WIDTH|ImageObserver.HEIGHT:
		  			this.setMaximumProgress(width*height);
		  		return true;

		  		case ImageObserver.SOMEBITS:
		  			this.setProgress(x+y*width);
		  			 //uncomment to simulate network latency

		  			 /*
		  			try
				  	{
				  		Thread.sleep(1);
				  	}
				  	catch(InterruptedException ie)
				  	{
				  		return false;
				  	}*/
				return true;


		  		case ImageObserver.PROPERTIES:

		  		return true;


		  		case ImageObserver.FRAMEBITS:
		  			//Prevent animated gif to hang return false
		  		return false;

		  		case ImageObserver.ALLBITS:
		  			this.setProgress(this.getMaximumProgress());
		  		return false;

		  		case ImageObserver.ERROR:
		  		return false;

		  		case ImageObserver.ABORT:
		  		return false;
		  	}
		return false;
	}


   void prepareTextureSize()
	{
		this.largeur=this.imageLoad.getWidth(null);
		this.hauteur=this.imageLoad.getHeight(null);
		this.maskLargeur=1;
		this.decalLargeur=0;
		this.largeurImage=1;
		while(this.largeurImage<this.largeur)
		{
			this.largeurImage<<=1;
			this.decalLargeur++;
		}
		this.maskLargeur=this.largeurImage-1;

		this.maskHauteur=1;
		this.decalHauteur=0;
		this.hauteurImage=1;
		while(this.hauteurImage<this.hauteur)
		{
			this.hauteurImage<<=1;
			this.decalHauteur++;
		}
		this.maskHauteur=this.hauteurImage-1;

		if(this.largeur!=this.largeurImage || this.hauteur!=this.hauteurImage)
		{
			Image i=this.imageLoad.getScaledInstance(this.largeurImage,this.hauteurImage,Image.SCALE_SMOOTH);
			this.imageLoad = i;
			this.largeur=this.largeurImage;
			this.hauteur=this.hauteurImage;
		}

	}

   void imageToTexturePixels()
   {
   		this.prepareTextureSize();
		this.pixels=new int[this.largeurImage*this.hauteurImage];
		PixelGrabber pg= new PixelGrabber(this.imageLoad,0,0,this.largeur,this.hauteur,this.pixels,0,this.largeurImage);
    	try
    	{
    		pg.grabPixels();
    	}
    	catch (InterruptedException ie)
    	{
    		ie.printStackTrace();
    	}
		this.imageLoad=null;
		System.gc();
	}


    /*
     *INTERFACE ITexture
     */
    public void setSourceFile(String sourceFile)
    {
    	this.reload=true;
    	this.sourceFile=sourceFile;
    }

    public String getSourceFile()
    {
    	return this.sourceFile;
    }

    public void setBaseURL(String baseURL)
    {
    	this.reload=true;
    	this.baseURL=baseURL;
    }

    public String getBaseURL()
    {
    	return this.baseURL;
    }

  	public void load (String baseURL,String sourceFile)
	{
		this.baseURL=baseURL;
		this.sourceFile=sourceFile;
		this.load();
	}

  	public void load(String baseURL,String sourceFile,boolean useMipMap)
	{
		this.baseURL=baseURL;
		this.sourceFile=sourceFile;
		this.load();
	}

	public void load()
	{
		//Log.log("loading :"+this.sourceFile);
		this.reset();
		this.setAction(IProgressListener.ACTION_FILE_DOWNLOAD);
		this.reload=false;
	 	Thread tr=new Thread(this);
	 	tr.start();
	}


}
