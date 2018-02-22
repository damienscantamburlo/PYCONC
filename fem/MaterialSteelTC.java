package net.cercis.jconc.fem;

import java.text.NumberFormat;
import java.text.DecimalFormat;


public class MaterialSteelTC implements Material {

    private final int id;
    private final double modEl, fc, ft;
    private final double epsyc, epsyt, modHard;

    public MaterialSteelTC (int id, double fc, double ft, double modEl) {
	this.id = id;
	this.modEl = modEl;
	modHard = modEl*1e-4;
	this.fc = fc;
	this.ft = ft;
	epsyt = ft/modEl;
	epsyc = -fc/modEl;
    }

    public MaterialSteelTC (int id, double fc, double ft, double modEl, double modHard) {
	this.id = id;
	this.modEl = modEl;
	this.modHard = modHard;
	this.fc = fc;
	this.ft = ft;
	epsyt = ft/modEl;
	epsyc = -fc/modEl;
    }

    public int id () {
	return id;
    }

    public String type () {
	return "steelTC";
    }

    public double modEl () {
	return modEl;
    }

    public double modHard () {
       return modHard;
    }

    public double getEta () {
	return 1.0;
    }

    public double fc () {
	return fc;
    }

    public double ft () {
	return ft;
    }

    public double stress (double eps) {
	if (eps <= epsyc) {
	    return (modEl*epsyc + modHard*(eps-epsyc));
	}
	else if (eps <= epsyt) {
	    return (modEl*eps);
	}
	else {
	    return (modEl*epsyt + modHard*(eps-epsyt));
	}
    }

    public double stress (double eps, double epsT) {
	//Same as the other, no reduction with epsT
	if (eps <= epsyc) {
	    return (modEl*epsyc + modHard*(eps-epsyc));
	}
	else if (eps <= epsyt) {
	    return (modEl*eps);
	}
	else {
	    return (modEl*epsyt + modHard*(eps-epsyt));
	}
    }

    public String toString () {
	NumberFormat nf = new DecimalFormat("#.###");
	return " Steel: fc = " + nf.format(fc) + " ft = " + nf.format(ft) + " E = " + nf.format(modEl);
    }

}
