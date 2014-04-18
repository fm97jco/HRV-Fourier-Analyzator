package fi.ni.pulssi.vo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/*
 * @author Jyrki Oraskari
 * @license This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 */

public class Jakso1minVO {	
	Calendar alku;
	Calendar loppu;
	long alku_num=0;
	long loppu_num=0;
	double svaliKA;
	float sykeKA;
	private double epocKA;
	float ventilationKA;
	float hengitysKA;
	boolean validi=true;
	double lfhf_erotus=0;
	double hf_summa=0;
	double lf_summa=0;
	
	List<Float> svali_lista=new ArrayList<Float>();
	public List<Jakso1000msecVO> sekunti_lista=new ArrayList<Jakso1000msecVO>();
	
	private int hr_kpl=0;
	private long hr_sum=0;

	private int fom_kpl=0;  // Hengitys
	private long fom_sum=0;

	private double ef_kpl=0;  // EPOC
	private double ef_sum=0;

	
	private int svali_kpl=0;  
	private double svali_sum=0;
	private int svali_plus=0;
	private int svali_miinus=0;

	public Jakso1minVO()
	{
		long time=0;
		for(int n=0;n<60;n++)
		{
			Jakso1000msecVO puoli_sekunti=new Jakso1000msecVO();
			puoli_sekunti.setAlku_num(time);
			time+=1000;
			puoli_sekunti.setLoppu_num(time);
	        sekunti_lista.add(puoli_sekunti);
		}
	}

	public void tayta_tyhjat()
	{
		for(int n=0;n<60;n++)
		{
			double edRR_sum=0;
			Jakso1000msecVO puoli_sekunti=(Jakso1000msecVO)sekunti_lista.get(n);
			if(puoli_sekunti.getRR_sum()==0)
			{
				puoli_sekunti.addRR((float) edRR_sum);
			}
			edRR_sum=puoli_sekunti.getRRKA();
		}
	}

	
	public boolean in_Jakso(Date time)
	{
		long time_num=time.getTime();
		if((time_num>=alku_num)&&(time_num<=loppu_num))
			return true;
		else
			return false;
	}

	
	public void addHR(short hr)
	{
		hr_kpl++;
		hr_sum+=(int)hr;
	    sykeKA=((float)hr_sum)/((float)hr_kpl);
	}

	public void addFOM(short fom)
	{
		fom_kpl++;
		fom_sum+=(int)fom;
	    hengitysKA=((float)fom_sum)/((float)fom_kpl);
	}

	public void addEF(double ef)
	{
		ef_kpl++;
		ef_sum+=(double)ef;
	    epocKA=ef_sum/ef_kpl;
	}

	public void addSValiEro(float svaliero)
	{
		svali_kpl++;
		svali_sum+=svaliero;
		svaliKA=svali_sum/((double)svali_kpl);
	}

	private void sijoita_rr_sekuntiin(float svali,long aika)
	{
		for (int n=0;n<sekunti_lista.size();n++)
		{			
			Jakso1000msecVO sekunti = (Jakso1000msecVO) sekunti_lista.get(n);
			if(sekunti.in_Jakso(aika))
			{
               sekunti.addRR(svali);				
			}
		}
		
	}


	float aika_pointer=0;
	public void addSVali(float svali)
	{
		aika_pointer+=svali;
		sijoita_rr_sekuntiin(svali, (long)aika_pointer);
		svali_lista.add(new Float(svali));
	}

	public void addSValiPlus()
	{
		svali_plus++;
	}
	public void addSValiMiinus()
	{
		svali_miinus++;
	}
	
	public Calendar getAlku() {
		return alku;
	}
	public void setAlku(Calendar alku) {
		Calendar uusi=Calendar.getInstance();
		uusi.setTimeInMillis(alku.getTimeInMillis());
		this.alku = uusi;
		this.alku_num=alku.getTimeInMillis();
	}
	public Calendar getLoppu() {
		return loppu;
	}
	public void setLoppu(Calendar loppu) {
		Calendar uusi=Calendar.getInstance();
		uusi.setTimeInMillis(alku.getTimeInMillis());
		this.loppu = uusi;
		this.loppu_num=loppu.getTimeInMillis();
	}

	public float getHengitysKA() {
		return hengitysKA;
	}

	public double getSvaliKA() {
		return svaliKA;
	}
	public float getSykeKA() {
		return sykeKA;
	}
	public float getVentilationKA() {
		return ventilationKA;
	}
	public void setVentilationKA(float ventilationKA) {
		this.ventilationKA = ventilationKA;
	}


	public int getSvali_miinus()
	{
		return svali_miinus;
	}

	public int getSvali_plus()
	{
		return svali_plus;
	}


	public boolean isValidi()
	{
		return validi;
	}


	public void setValidi(boolean validi)
	{
		this.validi = validi;
	}

	public double getLfhf_erotus()
	{
		return lfhf_erotus;
	}

	public void setLfhf_erotus(double lfhf_erotus)
	{
		this.lfhf_erotus = lfhf_erotus;
	}

	public double getHf_summa()
	{
		return hf_summa;
	}

	public void setHf_summa(double hf_summa)
	{
		this.hf_summa = hf_summa;
	}

	public double getLf_summa()
	{
		return lf_summa;
	}

	public void setLf_summa(double lf_summa)
	{
		this.lf_summa = lf_summa;
	}

	public double getEpocKA() {
		return epocKA;
	}


}
