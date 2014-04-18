package fi.ni.pulssi;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fi.ni.pulssi.vo.Jakso1minVO;
import fi.ni.pulssi.vo.Jakso1000msecVO;
import fi.ni.pulssi.vo.SValiVO;
import fi.ni.pulssi.vo.SampleVO;
import flanagan.math.FourierTransform;

/*
 * @author Jyrki Oraskari
 * @license This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 */


public class PulssiLaskuri
{
	final int VAIHE_ALUSTUS = 1;

	final int VAIHE_FINAL = 2;

	DecimalFormat decimalFormat;

	SimpleDateFormat dateFrm;

	SAXParserFactory factory;

	List<SampleVO> sample_lista = new ArrayList<SampleVO>();

	List<SValiVO> svali_lista = new ArrayList<SValiVO>();

	List<Jakso1minVO> jakso_lista = new ArrayList<Jakso1minVO>();

	Calendar min_cal = Calendar.getInstance();

	Calendar max_cal = Calendar.getInstance();

	boolean is_timeset = false;

	short max_hr = 0;

	short max_fom = 0;

	String s_time;

	short s_hr;

	short s_fom;

	public int parsinta_vaihe = 0;

	int voimavara = 300;

	int day_r1_count = 0;

	int day_r2_count = 0;

	int day_p_count = 0;

	int day_l1_count = 0;

	int day_l2_count = 0;

	int day_l3_count = 0;

	int day_l4_count = 0;

	int day_s1_count = 0;

	int day_o_count = 0;

	int day_u_count = 0;

	int day_all_count = 0;

	Date sample_time = new Date();

	double svalisum = 0;

	double raaka_svalisum = 0;

	public PulssiLaskuri()
	{
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator(',');
		decimalFormat = new DecimalFormat("0.000000", dfs);
		dateFrm = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

		factory = SAXParserFactory.newInstance();
		parsi("c:\\suunto\\0.xml", VAIHE_ALUSTUS);
		alusta();
		svalisum = (double) min_cal.getTimeInMillis();
		raaka_svalisum = (double) min_cal.getTimeInMillis();

		Date t = new Date();
		t.setTime(((long) svalisum));

		parsi("c:\\suunto\\0.xml", VAIHE_FINAL);

		double[] data = new double[64];

		LiukuKeskiArvo hfKA = new LiukuKeskiArvo();
		LiukuKeskiArvo lfKA = new LiukuKeskiArvo();
		for (int i = 0; i < jakso_lista.size(); i++)
		{
			Jakso1minVO jakso = (Jakso1minVO) jakso_lista.get(i);
			if (jakso.isValidi())
			{
				jakso.tayta_tyhjat();
				for (int n = 0; n < 64; n++)
				{
					data[n] = 0;
				}

				for (int n = 0; n < 60; n++)
				{
					Jakso1000msecVO sekunti = (Jakso1000msecVO) jakso.sekunti_lista.get(n);

					data[n] = sekunti.getRRKA();
				}
				double ed_data = 0;
				for (int n = 0; n < 64; n++)
				{
					if (data[n] != 0)
						ed_data = data[n];
					else
						data[n] = ed_data;
				}

				FourierTransform ft0 = new FourierTransform(data);
				ft0.transform();
				double pointsPerCycle = 1;
				double deltaT = 1.0D / pointsPerCycle;

				ft0.setDeltaT(deltaT);

				double[][] powerSpectrum = ft0.powerSpectrum();
				double lf = 0;
				double hf = 0;

				for (int k = 0; k < powerSpectrum[1].length; k++)
				{
					double taajuus = powerSpectrum[0][k];
					double arvo = powerSpectrum[1][k];
					if ((taajuus >= 0.04) && (taajuus < 0.15))
					{
						lf += arvo;
					}
					else if ((taajuus >= 0.15) && (taajuus < 1.50))
					{
						hf += arvo;
					}
				}

				hfKA.lisaaArvo(hf);
				lfKA.lisaaArvo(lf);
				double erotus = hfKA.getArvo() / lfKA.getArvo();
				jakso.setLfhf_erotus(erotus * 100);
				jakso.setHf_summa(hf);
				jakso.setLf_summa(lf);
			}
		}
		listaa_jaksot();
		SimpleDateFormat dFrm1 = new SimpleDateFormat("E dd.MM.yyyy HH:mm:ss");
		SimpleDateFormat dFrm2 = new SimpleDateFormat("HH:mm:ss");
        System.out.println(dFrm1.format(new Date(min_cal.getTimeInMillis())) +" - "+ dFrm2.format(new Date(max_cal.getTimeInMillis())) );
        
		System.out.println("L1:  " + decimalFormat.format(100 * (double) day_l1_count / (double) day_all_count) + " %");
		System.out.println("L2:  " + decimalFormat.format(100 * (double) day_l2_count / (double) day_all_count) + " %");
		System.out.println("L3:  " + decimalFormat.format(100 * (double) day_l3_count / (double) day_all_count) + " %");
		System.out.println("L4:  " + decimalFormat.format(100 * (double) day_l4_count / (double) day_all_count) + " %");
		System.out.println("P:   " + decimalFormat.format(100 * (double) day_p_count / (double) day_all_count) + " %");
		System.out.println("R1:  " + decimalFormat.format(100 * (double) day_r1_count / (double) day_all_count) + " %");
		System.out.println("R2:  " + decimalFormat.format(100 * (double) day_r2_count / (double) day_all_count) + " %");
		System.out.println("U:   " + decimalFormat.format(100 * (double) day_u_count / (double) day_all_count) + " %");
		System.out.println("S1:  " + decimalFormat.format(100 * (double) day_s1_count / (double) day_all_count) + " %");
		System.out.println("O:  " + decimalFormat.format(100 * (double) day_o_count / (double) day_all_count) + " %");
	}

	private void nollaa_minutit(Calendar aika)
	{
		aika.set(Calendar.MINUTE, 0);
		aika.set(Calendar.SECOND, 0);
		aika.set(Calendar.MILLISECOND, 0);
	}

	public void alusta()
	{
		Calendar alku = Calendar.getInstance();
		Calendar loppu = Calendar.getInstance();

		Calendar min_tmp = Calendar.getInstance();
		min_tmp.setTimeInMillis(min_cal.getTimeInMillis());
		nollaa_minutit(min_tmp);
		alku.setTime(min_tmp.getTime());
		loppu.setTime(min_tmp.getTime());
		loppu.add(Calendar.MINUTE, 1);
		for (int n = 0; n < (60 * 13); n++)
		{
			Jakso1minVO jakso = new Jakso1minVO();
			jakso.setAlku(alku);
			jakso.setLoppu(loppu);
			jakso_lista.add(jakso);
			alku.add(Calendar.MINUTE, 1);
			loppu.add(Calendar.MINUTE, 1);
		}
	}

	private void sijoita_svaliero_jaksoon(Date date, float svaliero, int tyyppi, float svali)
	{
		for (int n = 0; n < jakso_lista.size(); n++)
		{
			Jakso1minVO jakso = (Jakso1minVO) jakso_lista.get(n);
			if (jakso.in_Jakso(date))
			{
				jakso.addSValiEro(svaliero);
				jakso.addSVali(svali);
				switch (tyyppi)
				{
				case 1:
					jakso.addSValiPlus();
					break;
				case 0:
					break;
				case -1:
					jakso.addSValiMiinus();
					break;
				}

			}
		}

	}

	private void disabloi_jakson_validius(Date date)
	{
		for (int n = 0; n < jakso_lista.size(); n++)
		{
			Jakso1minVO jakso = (Jakso1minVO) jakso_lista.get(n);
			if (jakso.in_Jakso(date))
			{
				jakso.setValidi(false);

			}
		}

	}

	private void sijoita_hr_jaksoon(Date date, short hr)
	{
		for (int n = 0; n < jakso_lista.size(); n++)
		{
			Jakso1minVO jakso = (Jakso1minVO) jakso_lista.get(n);
			if (jakso.in_Jakso(date))
			{
				jakso.addHR(hr);
			}
		}

	}

	private void sijoita_fom_jaksoon(Date date, short fom)
	{
		for (int n = 0; n < jakso_lista.size(); n++)
		{
			Jakso1minVO jakso = (Jakso1minVO) jakso_lista.get(n);
			if (jakso.in_Jakso(date))
			{
				jakso.addFOM(fom);
			}
		}

	}

	private void sijoita_ef_jaksoon(Date date, double ef)
	{
		for (int n = 0; n < jakso_lista.size(); n++)
		{
			Jakso1minVO jakso = (Jakso1minVO) jakso_lista.get(n);
			if (jakso.in_Jakso(date))
			{
				jakso.addEF(ef);
			}
		}

	}

	//

	double ed_SykeKA = 0;

	final static int LUOKKA_TUNTEMATON = 0;

	final static int LUOKKA_LIIKUNTA1 = 1;

	final static int LUOKKA_LIIKUNTA2 = 2;

	final static int LUOKKA_LIIKUNTA3 = 3;

	final static int LUOKKA_LIIKUNTA4 = 4;


	final static int LUOKKA_PALAUTUMINEN = 5;

	final static int LUOKKA_STRESSI1 = 6;

	final static int LUOKKA_RENTOUS1 = 8;

	final static int LUOKKA_RENTOUS2 = 9;

	final static int LUOKKA_UNI = 10;

	int ed_luokka = 0;

	double rasitustaso_syke_ka_sum=0;
	int    rasitustaso_syke_ka_kpl=0;
	
	
	public void listaa_jaksot()
	{
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator(',');
		DecimalFormat dFormat;
		dFormat = new DecimalFormat("0.000", dfs);
		Date t = new Date();
		PulssiView pview=new PulssiView();
		for (int n = 0; n < jakso_lista.size(); n++)
		{
			Jakso1minVO jakso = (Jakso1minVO) jakso_lista.get(n);
			t.setTime(jakso.getAlku().getTimeInMillis());

			if (!jakso.isValidi())
			{
				// Kerätään pois tilastoista
			}
			else
			{
				if (jakso.getSykeKA() > 0)
				{
					boolean syke_relax = false;
					boolean palautuminen = false;
					boolean relax_syke_esto = false;
					boolean relax_svaliKA = false;
					boolean relax_lfhf = false;
					boolean stress_lfhf = false;
					boolean stress_sykeKAero = false;

					double jakso_sykeKA = jakso.getSykeKA();
					double jakso_hengitysKA = jakso.getHengitysKA();
					double sykeKA_erotus = jakso_sykeKA - ed_SykeKA;
					double jakso_svaliKA = jakso.getSvaliKA();
					double jakso_lfhf_erotus0 = jakso.getLfhf_erotus();

					if (jakso_sykeKA < 70)
						syke_relax = true;
					if (jakso.getEpocKA() >= 1.5)
						palautuminen = true;
					if (sykeKA_erotus > 3)
					{
						relax_syke_esto = true;
						stress_sykeKAero = true;
					}
					if (jakso_svaliKA >= 20)
						relax_svaliKA = true;
					if (jakso_lfhf_erotus0 >= 20) // 0.05
						relax_lfhf = true;
					if (jakso_lfhf_erotus0 <= 8) // 0.125
						stress_lfhf = true;

					short luokitus = LUOKKA_TUNTEMATON;
					boolean lopullinen = false;

					if (jakso_sykeKA > 65)
					{
						if ((jakso.getHengitysKA() > 15) && (jakso.getHengitysKA() < 20))
						{
							luokitus = LUOKKA_LIIKUNTA1;
							lopullinen = true;
						}
						else if ((jakso.getHengitysKA() >= 20) && (jakso.getHengitysKA() < 35))
						{
							luokitus = LUOKKA_LIIKUNTA2;
							lopullinen = true;
						}
						else if ((jakso.getHengitysKA() >= 35) && (jakso.getHengitysKA() < 50))
						{
							luokitus = LUOKKA_LIIKUNTA3;
							lopullinen = true;

						}
						else if (jakso.getHengitysKA() >= 50)
						{
							luokitus = LUOKKA_LIIKUNTA4;
							lopullinen = true;
						}
					}
					else
					{
						if (jakso.getHengitysKA() > 15)
						{
							if(jakso_lfhf_erotus0>100)
							{
							  luokitus = LUOKKA_UNI;
							  lopullinen = true;
							}
						}
						
					}

					if (!lopullinen)
						if (palautuminen)
						{
							luokitus = LUOKKA_PALAUTUMINEN;
							lopullinen = true;
						}
					if (!lopullinen)
						if ((jakso_sykeKA < 70) && (jakso_lfhf_erotus0 > 100)) // uni
																				// 13.2.2008
						{
							luokitus = LUOKKA_RENTOUS2;
							lopullinen = true;
						}

					if (!lopullinen) // alle 70 sykkeellä iso sykevaihtelu
						if (relax_svaliKA)
						{
							if (syke_relax)
								if (!relax_syke_esto)
								{
									luokitus = LUOKKA_RENTOUS2;
									lopullinen = true;
								}
						}

					if (!lopullinen)
						if (relax_lfhf)
						{
							luokitus = LUOKKA_RENTOUS1;
							lopullinen = true;
						}

					if (!lopullinen)
						if ((jakso.getSykeKA() < 75) && jakso.getSvaliKA() < 7)
						{
							luokitus = LUOKKA_STRESSI1;
							lopullinen = true;
						}

					if (!lopullinen)
						if (stress_lfhf)
						{
							luokitus = LUOKKA_STRESSI1;
							lopullinen = true;
						}
					if (stress_lfhf)
					{
						if (sykeKA_erotus > 0)
						{
							luokitus = LUOKKA_STRESSI1;
						}
					}

					switch (luokitus)
					{
					case LUOKKA_TUNTEMATON:
						rasitustaso_syke_ka_kpl++;
						rasitustaso_syke_ka_sum+=jakso.getSykeKA();
						day_o_count++;
						break;
					case LUOKKA_UNI:
						rasitustaso_syke_ka_kpl++;
						rasitustaso_syke_ka_sum+=jakso.getSykeKA();
						day_u_count++;
						break;
					case LUOKKA_LIIKUNTA1:
						day_l1_count++;
						break;
					case LUOKKA_LIIKUNTA2:
						day_l2_count++;
						break;
					case LUOKKA_LIIKUNTA3:
						day_l3_count++;
						break;
					case LUOKKA_LIIKUNTA4:
						day_l4_count++;
						break;
					case LUOKKA_PALAUTUMINEN:
						day_p_count++;
						break;
					case LUOKKA_STRESSI1:
						rasitustaso_syke_ka_kpl++;
						rasitustaso_syke_ka_sum+=jakso.getSykeKA();
						day_s1_count++;
						break;

					case LUOKKA_RENTOUS1:
						rasitustaso_syke_ka_kpl++;
						rasitustaso_syke_ka_sum+=jakso.getSykeKA();
						day_r1_count++;
						break;
					case LUOKKA_RENTOUS2:
						rasitustaso_syke_ka_kpl++;
						rasitustaso_syke_ka_sum+=jakso.getSykeKA();
						day_r2_count++;
						break;
					}
					day_all_count++;
					Calendar cal=Calendar.getInstance();
					cal.setTime(t);
					pview.pk.lisaaPiirto(luokitus,(short)cal.get(Calendar.HOUR_OF_DAY),(short)cal.get(Calendar.MINUTE));
					ed_SykeKA = jakso_sykeKA;					
					ed_luokka = luokitus;
				}
			}
		}
		SimpleDateFormat dFrm1 = new SimpleDateFormat("E dd.MM.yyyy HH:mm:ss");
		SimpleDateFormat dFrm2 = new SimpleDateFormat("HH:mm:ss");
        String txtotsikko=(dFrm1.format(new Date(min_cal.getTimeInMillis())) +" - "+ dFrm2.format(new Date(max_cal.getTimeInMillis())) );
        pview.pk.setTeksti(txtotsikko);
        pview.pk.setStrss_sykeka( dFormat.format(rasitustaso_syke_ka_sum/(double)rasitustaso_syke_ka_kpl));
		pview.pk.julkaise();
	}

	StringBuffer string_buffer = new StringBuffer();

	float ed_svali = 0;

	private void kasittele_svalit(String st)
	{
		float luku = 0;
		try
		{
			for (int n = 0; n < st.length(); n++)
			{
				char chr = st.charAt(n);
				if (chr == ';')
				{
					try
					{
						luku = Float.parseFloat(string_buffer.toString());
					}
					catch (Exception e)
					{
						luku = 9999;
					}
					if (luku < 7000)
					{
						Date t = new Date();
						t.setTime(((long) svalisum));
						float svaliero = abs(luku - ed_svali);

						if (svaliero > 40)
						{
							sijoita_svaliero_jaksoon(t, svaliero, 1, luku);

						}
						else if (svaliero < 5)
						{
							sijoita_svaliero_jaksoon(t, svaliero, -1, luku);
						}
						else
						{
							sijoita_svaliero_jaksoon(t, svaliero, 0, luku);
						}
						svalisum += (double) luku;
						ed_svali = luku;
					}

					string_buffer.setLength(0);
				}
				else
				{
					string_buffer.append(chr);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private float abs(float luku)
	{
		if (luku < 0)
			return -luku;
		else
			return luku;
	}

	private void end_svalit()
	{
		if (string_buffer.length() > 0)
		{
			float luku = 0;
			try
			{
				luku = Float.parseFloat(string_buffer.toString());
			}
			catch (Exception e)
			{
				luku = 9999;
			}
			if (luku < 7000)
			{
				Date t = new Date();
				t.setTime(((long) svalisum));

				float svaliero = abs(luku - ed_svali);

				if (svaliero > 30)
				{
					sijoita_svaliero_jaksoon(t, svaliero, 1, luku);

				}
				else if (svaliero < 5)
				{
					sijoita_svaliero_jaksoon(t, svaliero, -1, luku);
				}
				else
				{
					sijoita_svaliero_jaksoon(t, svaliero, 0, luku);
				}
			}
		}
	}

	private void kasittele_raakasvalit(String st)
	{
		float luku = 0;
		try
		{
			for (int n = 0; n < st.length(); n++)
			{
				char chr = st.charAt(n);
				if (chr == ';')
				{
					try
					{
						luku = Float.parseFloat(string_buffer.toString());
					}
					catch (Exception e)
					{
						luku = 9999;
					}
					if (luku == 5000)
					{
						Date t = new Date();
						t.setTime(((long) raaka_svalisum));
						disabloi_jakson_validius(t);
					}
					raaka_svalisum += (double) luku;

					string_buffer.setLength(0);
				}
				else
				{
					string_buffer.append(chr);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void raaka_end_svalit()
	{
		if (string_buffer.length() > 0)
		{
			float luku = 0;
			try
			{
				luku = Float.parseFloat(string_buffer.toString());
			}
			catch (Exception e)
			{
				luku = 9999;
			}
			if (luku == 5000)
			{
				Date t = new Date();
				t.setTime(((long) raaka_svalisum));
				disabloi_jakson_validius(t);
			}
			raaka_svalisum += (double) luku;
		}
	}

	class HowToHandler extends DefaultHandler
	{
		boolean is_sample = false;

		boolean is_tm = false;

		boolean is_hr = false;

		boolean is_fom = false;

		boolean is_ef = false;

		boolean is_corrected = false;

		boolean is_ibidt = false;

		public void startElement(String nsURI, String strippedName, String tagName, Attributes attributes) throws SAXException
		{
			if (tagName.equalsIgnoreCase("SAMPLE"))
			{
				is_sample = true;
			}
			if (tagName.equalsIgnoreCase("HR"))
			{
				is_hr = true;
			}
			if (tagName.equalsIgnoreCase("TM"))
			{
				is_tm = true;
			}
			if (tagName.equalsIgnoreCase("FOM"))
			{
				is_fom = true;
			}

			if (tagName.equalsIgnoreCase("EF"))
			{
				is_ef = true;
			}

			if (tagName.equalsIgnoreCase("CORRECTED_DT"))
			{
				is_corrected = true;
			}
			if (tagName.equalsIgnoreCase("IBI_DT"))
			{
				is_ibidt = true;
			}

		}

		public void characters(char[] ch, int start, int length)
		{

			if (is_sample)
			{
				if (is_hr)
				{
					try
					{
						short lyhyt = (short) Integer.parseInt(new String(ch, start, length));
						if (lyhyt > max_hr)
							max_hr = lyhyt;
						s_hr = lyhyt;
						sijoita_hr_jaksoon(sample_time, lyhyt);
					}
					catch (Exception e)
					{
						// Nothing
					}
				}
				if (is_tm)
				{
					try
					{
						Date date = dateFrm.parse(new String(ch, start, length));
						Calendar cal = Calendar.getInstance();

						cal.setTime(date);
						sample_time.setTime(date.getTime());
						if (!is_timeset)
						{
							min_cal = cal;
							max_cal = cal;
							is_timeset = true;
						}
						else
						{
							if (cal.before(min_cal))
								min_cal = cal;
							if (cal.after(max_cal))
								max_cal = cal;
						}
						s_time = new String(ch, start, length);
					}
					catch (Exception e)
					{
						// Nothing
					}

				}

				if (is_fom)
				{
					try
					{
						short lyhyt = (short) Integer.parseInt(new String(ch, start, length));
						// System.out.println("fom: "+lyhyt);
						if (lyhyt > max_fom)
							max_fom = lyhyt;
						s_fom = lyhyt;
						sijoita_fom_jaksoon(sample_time, s_fom);
					}
					catch (Exception e)
					{
						// Nothing
					}
				}

				if (is_ef)
				{
					try
					{
						float ef = Float.parseFloat(new String(ch, start, length));
						if (ef < 10)
							sijoita_ef_jaksoon(sample_time, ef);
					}
					catch (Exception e)
					{
						// Nothing
					}
				}

			}// SAMPLE

			// Vaihe
			if (parsinta_vaihe == VAIHE_FINAL)
			{
				if (is_corrected)
				{
					String st = new String(ch, start, length);
					kasittele_svalit(st);
				}
				if (is_ibidt)
				{
					String st = new String(ch, start, length);
					kasittele_raakasvalit(st);
				}
			}

		}

		public void endElement(String uri, String localName, String qName) throws SAXException
		{
			if (qName.equalsIgnoreCase("SAMPLE"))
			{
				is_sample = false;
				if (parsinta_vaihe == VAIHE_FINAL)
				{
					float f_hr = ((float) s_hr) / (float) 130;
					float f_fom = ((float) s_fom) / (float) 70;
					float tulos = f_hr - f_fom;
					DecimalFormatSymbols dfs = new DecimalFormatSymbols();
					dfs.setDecimalSeparator(',');
				}
			}
			if (qName.equalsIgnoreCase("HR"))
			{
				is_hr = false;
			}

			if (qName.equalsIgnoreCase("TM"))
			{
				is_tm = false;
			}
			if (qName.equalsIgnoreCase("FOM"))
			{
				is_fom = false;
			}
			if (qName.equalsIgnoreCase("EF"))
			{
				is_ef = false;
			}

			if (qName.equalsIgnoreCase("CORRECTED_DT"))
			{
				is_corrected = false;
				end_svalit();
			}
			if (qName.equalsIgnoreCase("IBI_DT"))
			{
				is_ibidt = false;
				raaka_end_svalit();
			}

		}
	}

	public boolean parsi(String tiedosto_nimi, int vaihe)
	{

		this.parsinta_vaihe = vaihe;

		if (tiedosto_nimi == null)
			return false;

		try
		{
			SAXParser parser = factory.newSAXParser();
			parser.parse(new File(tiedosto_nimi), new HowToHandler());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args)
	{
		new PulssiLaskuri();
	}
}
