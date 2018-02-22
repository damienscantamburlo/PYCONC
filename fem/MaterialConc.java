package net.cercis.jconc.fem;

import net.cercis.jconc.ui.Jconc;


public class MaterialConc implements Material {

    private final int id;
    private final double modEl, fc;
    private final double epsyc, epsyt, modHard;
    private final double coefEta1, coefEta2;
    private double eta;
    private boolean inputOfEtas = false;
    private final int digits = Jconc.getDigits();

    public MaterialConc (int id, double fc,double modEl) {
	this.id = id;
	this.modEl = modEl;
	modHard = -modEl*1e-3;//OJO! NEGATIVO
	this.fc = fc;
	epsyc = -fc/modEl;
	epsyt = -epsyc*0.0001;//-epsyc;
	//Valores red. eta de Collins
	coefEta1 = 0.80;
	coefEta2 = 170.0;
	eta = 1.0; //Factor de reduccion por def. trans
    }

    public MaterialConc (int id, double fc, double modEl, 
			 double coefEta1, double coefEta2) {
	this.id = id;
	this.modEl = modEl;
	modHard = -modEl*1e-3;//OJO! NEGATIVO
	this.fc = fc;
	epsyc = -fc/modEl;
	epsyt = -epsyc*0.0001;//-epsyc;
	this.coefEta1 = coefEta1;
	this.coefEta2 = coefEta2;
        this.inputOfEtas = true;
	eta = 1.0; //Factor de reduccion por def. trans
    }

    public int id () {
	return id;
    }

    public String type () {
	return "concrete";
    }

    public double modEl () {
	return modEl;
    }

    public double fc () {
	return fc;
    }

    public boolean getInputCoefEtas() {
       return inputOfEtas;
    }
    
    public double getCoefEta1 () {
       return coefEta1;
    }
    
    public double getCoefEta2() {
       return coefEta2;
    }
    
    public double getEta () {
	return eta;
    }

    public double stress (double eps) {
	if (eps <= epsyc) {
	    return (modEl*epsyc + modHard*(eps-epsyc));
	}
	else if (eps <= epsyt) {
	    return (modEl*eps);
	}
	else {
	    return (modEl*epsyt + modHard/100*(eps-epsyt));
	}
    }

    public double stress (double epsL, double epsT) {

	double div = (coefEta1 + coefEta2*epsT);
	if (div < 1.0) div = 1.0;
	eta = 1.0 / div;
	//System.out.println("eta = " + eta);

	if (epsL <= (epsyc*eta)) {
	    return (modEl*(epsyc*eta) + modHard*(epsL-(epsyc*eta)));
	}
	else if (epsL <= epsyt) {
	    return (modEl*epsL);
	}
	else {
	    return (modEl*epsyt + Math.abs(modHard)/100*(epsL-epsyt));
	}
    }

    public String toString () {
	return " Concrete: fc = " + Jconc.formatNumber(fc, digits) +
                " E = " + Jconc.formatNumber(modEl, digits);
    }

}
