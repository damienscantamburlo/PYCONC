package net.cercis.jconc.fem;
import net.cercis.jconc.ui.*;


public class MaterialCable implements Material {

    private final int id;
    private final double modEl, fc;
    private final double epsyc, epsyt, modHard, istrain;
    private final int digits = Jconc.getDigits();

    public MaterialCable (int id, double fc, double sigma0, double modEl) {
	this.id = id;
	this.modEl = modEl;
	this.modHard = modEl*1e-4;
	this.fc = fc;
	if (sigma0 < fc) this.istrain = sigma0/modEl;
	else this.istrain = fc/modEl;
	epsyt = fc/modEl - istrain;
	epsyc = -fc/modEl - istrain;
    }

    public MaterialCable (int id, double fc, double sigma0, double modEl, double modHard) {
	this.id = id;
	this.modEl = modEl;
	this.modHard = modHard;
	this.fc = fc;
	if (sigma0 < fc) this.istrain = sigma0/modEl;
	else this.istrain = fc/modEl;
	epsyt = fc/modEl - istrain;
	epsyc = -fc/modEl - istrain;
    }

    public int id () {
	return id;
    }

    public String type () {
	return "cable";
    }

    public double modEl () {
	return modEl;
    }
    
    public double modHard () {
       return modHard;
    }
    
    public double sig0 () {
       return istrain * modEl;
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
	return " Cable: fc = " + Jconc.formatNumber(fc, digits) +
               " E = " + Jconc.formatNumber(modEl, digits);
    }

}
