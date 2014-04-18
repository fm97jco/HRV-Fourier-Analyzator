package fi.ni.pulssi.vo;

import java.util.Calendar;

/*
 * @author Jyrki Oraskari
 * @license This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 */


public class SampleVO {
Calendar TM;   // Aikaleima
short HR;   // Syke
short VENT; // Ventilation  l/min
float NRG;  // kCal
float EF;   // EPOC ml/kg
short VO2;  // VO2
float FOM;  // hengitystiheys kpl/min
public float getEF() {
	return EF;
}
public void setEF(float ef) {
	EF = ef;
}
public float getFOM() {
	return FOM;
}
public void setFOM(float fom) {
	FOM = fom;
}
public short getHR() {
	return HR;
}
public void setHR(short hr) {
	HR = hr;
}
public float getNRG() {
	return NRG;
}
public void setNRG(float nrg) {
	NRG = nrg;
}
public Calendar getTM() {
	return TM;
}
public void setTM(Calendar tm) {
	TM = tm;
}
public short getVENT() {
	return VENT;
}
public void setVENT(short vent) {
	VENT = vent;
}
public short getVO2() {
	return VO2;
}
public void setVO2(short vo2) {
	VO2 = vo2;
}

}
