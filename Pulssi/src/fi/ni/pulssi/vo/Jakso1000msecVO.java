package fi.ni.pulssi.vo;

/*
 * @author Jyrki Oraskari
 * @license This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 */

public class Jakso1000msecVO {	
	long alku_num=0;
	long loppu_num=0;
	double RR_KA;
	
	private int RR_kpl=0;  
	private double RR_sum=0;

	public boolean in_Jakso(long time_num)
	{
		if((time_num>=alku_num)&&(time_num<=loppu_num))
			return true;
		else
			return false;
	}

	public void addRR(float rr)
	{
		RR_kpl++;
		RR_sum+=rr;
		RR_KA=RR_sum/((double)RR_kpl);
	}


	public double getRRKA() {
		return RR_KA;
	}

	public void setAlku_num(long alku_num)
	{
		this.alku_num = alku_num;
	}

	public void setLoppu_num(long loppu_num)
	{
		this.loppu_num = loppu_num;
	}

	public double getRR_sum()
	{
		return RR_sum;
	}

}
