package net.cercis.jconc.fem;

import net.cercis.jconc.ui.Jconc;


public class MaterialConcWithfct implements Material {

    private final int id;
    private final double modEl, fc, fct;
    private final double epsyc, epsyt, modHard, epsut;
    private double eta;
    private final int digits = Jconc.getDigits();

    public MaterialConcWithfct (int id, double fc, double fct, double modEl) {
	this.id = id;
	this.modEl = modEl;
	modHard = -modEl*1e-2;
	this.fc = fc;
	this.fct = fct;
	epsyc = -fc/modEl;
	epsyt = fct/modEl;
	eta = 1.0;
        epsut = 1.0;
    }

    public MaterialConcWithfct (int id, double fc, double fct, double modEl, double epsut) {
	this.id = id;
	this.modEl = modEl;
	modHard = -modEl*1e-2;
	this.fc = fc;
	this.fct = fct;
	epsyc = -fc/modEl;
	epsyt = fct/modEl;
	eta = 1.0;
        this.epsut = epsut;
    }
    
    public int id () {
	return id;
    }

    public String type () {
	return "concWithfct";
    }

    public double modEl () {
	return modEl;
    }

    public double fc () {
	return fc;
    }

    public double fct () {
       return fct;
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
        else if (eps <= epsut) {
	    return (modEl*epsyt + modHard/100*(eps-epsyt));
	}
        else {
            return (modHard/100*eps);
        }
    }

    public double stress (double epsL, double epsT) {

	double div = (0.8+170*epsT);
	if (div < 1.0) div = 1.0;
	eta = 1.0 / div;
	if (epsL <= (epsyc*eta)) {
	    return (modEl*(epsyc*eta) + modHard*(epsL-(epsyc*eta)));
	}
	else if (epsL <= epsyt) {
	    return (modEl*epsL);
	}
        else if (epsL <=epsut) {
	    return (modEl*epsyt + Math.abs(modHard)/100*(epsL-epsyt));
	}
        else {
            return (Math.abs(modHard)/100*(epsL));
        }
    }

    public String toString () {
	return " Concrete with fct: fc = " + Jconc.formatNumber(fc, digits) +
                " fct = " + Jconc.formatNumber(fct, digits) +
                " E = " + Jconc.formatNumber(modEl, digits);
    }

}
