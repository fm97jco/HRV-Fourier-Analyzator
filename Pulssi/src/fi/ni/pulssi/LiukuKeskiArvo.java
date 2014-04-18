package fi.ni.pulssi;


/*
 * @author Jyrki Oraskari
 * @license This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 */

public class LiukuKeskiArvo
{
	double arvo = 0;

	public void lisaaArvo(double luku)
	{
		arvo = arvo * 0.5 + luku;
	}

	public double getArvo()
	{
		return arvo;
	}
}
