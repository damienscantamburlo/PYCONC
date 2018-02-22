package net.cercis.jconc.fem;
import net.cercis.jconc.ui.Jconc;


public class MaterialSteel implements Material {

    private final int id;
    private final double modEl, fc;
    private final double epsyc, epsyt, modHard;
    private final int digits = Jconc.getDigits();

    public MaterialSteel (int id, double fc,double modEl) {
	this.id = id;
	this.modEl = modEl;
	modHard = modEl*1e-4;
	this.fc = fc;
	epsyt = fc/modEl;
	epsyc = -epsyt;
    }

    public MaterialSteel (int id, double fc, double modEl, double modHard) {
	this.id = id;
	this.modEl = modEl;
	this.modHard = modHard;
	this.fc = fc;
	epsyt = fc/modEl;
	epsyc = -epsyt;
    }

    public int id () {
	return id;
    }

    public String type () {
	return "steel";
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
	return " Steel: fc = " + Jconc.formatNumber(fc, digits) +
                " E = " + Jconc.formatNumber(modEl, digits);
    }

}
