package fi.ni.pulssi.vo;

import java.util.Calendar;


/*
 * @author Jyrki Oraskari
 * @license This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 */


public class SValiVO {
	Calendar TM;
	float svali;
	public float getSvali() {
		return svali;
	}
	public void setSvali(float svali) {
		this.svali = svali;
	}
	public Calendar getTM() {
		return TM;
	}
	public void setTM(Calendar tm) {
		TM = tm;
	}
}
