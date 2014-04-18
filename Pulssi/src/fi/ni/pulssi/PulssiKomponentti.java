/*
 * Created on 2.1.2008
 */
package fi.ni.pulssi;

import java.awt.Color;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;


/*
 * @author Jyrki Oraskari
 * @license This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 */

public class PulssiKomponentti extends JComponent implements Serializable, MouseMotionListener, MouseListener
{
	Image backbuffer;

	Graphics backg;

	boolean grafiikka_alustettu = false;

	int leveys = 220;

	int korkeus = 100;

	byte[] puskuri = new byte[1000];

	int pinx = 0;

	class Piirto
	{
		Color vari = Color.WHITE;
		short hh=0;
		short mm=0;

		short pituus = 1;
	}

	List<Piirto> piirrot = new ArrayList<Piirto>();
	String strss_sykeka="";
	public void setStrss_sykeka(String strss_sykeka)
	{
		this.strss_sykeka = strss_sykeka;
	}
	public PulssiKomponentti()
	{
		super();
		this.leveys = 1000;
		this.korkeus = 150;
		setMinimumSize(new Dimension(leveys, korkeus));
		setPreferredSize(new Dimension(leveys, korkeus));
		setMaximumSize(new Dimension(leveys, korkeus));
		addMouseListener(this);
		addMouseMotionListener(this);
		alusta();
	}
	
	String teksti="";
	public void setTeksti(String txt)
	{
		teksti=txt;
	}

	public void lisaaPiirto(short luokitus,short hh,short mm)
	{
		Piirto pi=new Piirto();
		pi.pituus=1;
		pi.hh=hh;
		pi.mm=mm;
		switch(luokitus)
		{
		case PulssiLaskuri.LUOKKA_TUNTEMATON:
			pi.vari=Color.WHITE;
			break;
		case PulssiLaskuri.LUOKKA_LIIKUNTA1:
			pi.vari=new Color(0,0,200);
			break;
		case PulssiLaskuri.LUOKKA_LIIKUNTA2:
			pi.vari=new Color(0,0,150);
			break;
		case PulssiLaskuri.LUOKKA_LIIKUNTA3:
			pi.vari=new Color(0,0,100);
			break;
		case PulssiLaskuri.LUOKKA_LIIKUNTA4:
			pi.vari=new Color(0,0,75);
			break;
		case PulssiLaskuri.LUOKKA_PALAUTUMINEN:
			pi.vari=new Color(100,100,255);
			break;
		case PulssiLaskuri.LUOKKA_STRESSI1:
			pi.vari=Color.RED;
			break;
		case PulssiLaskuri.LUOKKA_RENTOUS1:
			pi.vari=new Color(0,200,0);
			break;
		case PulssiLaskuri.LUOKKA_RENTOUS2:
			pi.vari=new Color(0,150,0);
			break;
		case PulssiLaskuri.LUOKKA_UNI:
			pi.vari=Color.MAGENTA;
			break;
	    default:
		    pi.vari=Color.GRAY;
		break;
	    	
		}
		piirrot.add(pi);		
	}

	public void julkaise()
	{
		repaint();
	}
	
	private void alusta()
	{
		int w = leveys;
		int h = korkeus;

		backbuffer = createImage(w, h);
		if (backbuffer == null)
		{
			return;
		}
		backg = backbuffer.getGraphics();
		backg.setColor(Color.white);
		backg.fillRect(0, 0, w, h);
		backg.setColor(Color.black);

		grafiikka_alustettu = true;
		repaint();
	}

	public byte[] kuva_biteiksi()
	{
		return puskuri;
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mouseClicked(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
		repaint();
		e.consume();
	}

	public void mouseReleased(MouseEvent e)
	{
		repaint();
		e.consume();
	}

	public void mouseMoved(MouseEvent e)
	{
	}

	public void mouseDragged(MouseEvent e)
	{
	}

	public void piirra()
	{
		backg.setColor(Color.BLACK);
		backg.drawString(teksti, 30, 20);
		backg.drawString("r sykeka: "+strss_sykeka, 500, 20);
		for(int n=0;n<piirrot.size();n++)
		{	
		  Piirto pi=(Piirto) piirrot.get(n);
		  backg.setColor(pi.vari);
		  backg.fillRect(n+30, 70-(pi.pituus*30), 1, 70);
		  if(pi.mm==0)
		  {
		    backg.setColor(Color.BLACK);
		    backg.drawLine(n+30,115,n+30, 130);
		    backg.drawString(""+pi.hh,n+35,130);
		  }
		  if(pi.mm==30)
		  {
		    backg.setColor(Color.BLACK);
		    backg.drawLine(n+30, 115,n+30, 125);
		  }
		}
	}

	protected void paintComponent(Graphics g)
	{
		if (grafiikka_alustettu)
		{
			piirra();
			g.drawImage(backbuffer, 0, 0, this);
		}
		else
			alusta();

	}


}
