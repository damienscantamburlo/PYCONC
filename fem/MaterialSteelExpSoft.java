package net.cercis.jconc.fem;
import net.cercis.jconc.ui.Jconc;


public class MaterialSteelExpSoft implements Material {

    private final int id;
    private final double modEl, fc, epsut;
    private final double epsyc, epsyt;
    private final int digits = Jconc.getDigits();

    public MaterialSteelExpSoft (int id, double fc, double modEl, double epsut) {
	this.id = id;
	this.modEl = modEl;
	this.fc = fc;
	this.epsut = epsut;
	epsyt = fc/modEl;
    epsyc = -epsyt*1e-4;
    }

    public int id () {
	return id;
    }

    public String type () {
	return "steelExpSoft";
    }

    public double modEl () {
	return modEl;
    }

    public double epsut () {
    return epsut;
    }
	
    public double getEta () {
	return 1.0;
    }

    public double fc () {
	return fc;
    }

    public double stress (double eps) {
	if (eps < epsyc) {
	    return (modEl*epsyc);
	}
	else if (eps <= epsyt) {
	    return (modEl*eps);
	}
	else {
            return fc*Math.exp(-(eps-epsyt)/epsut);
	}
	}
    

    public double stress (double eps, double epsT) {
	//Same as the other, no reduction with epsT
	if (eps < epsyc) {
	    return (modEl*epsyc);
	}
	else if (eps <= epsyt) {
	    return (modEl*eps);
	}
	else {
            return fc*Math.exp(-(eps-epsyt)/epsut);
	}
	}

	
    public String toString () {
	return " Steel Tension Only & Exponential Softening: fc = " + Jconc.formatNumber(fc, digits) +
                " E = " + Jconc.formatNumber(modEl, digits);
    }

}
