package net.cercis.jconc.fem;
import net.cercis.jconc.ui.Jconc;


public class MaterialSteelLinSoft implements Material {

    private final int id;
    private final double modEl, fc;
    private final double modHard, epsmax, sigma_res;
    private final double epsyc, epsyt, modE3;
    private double sigma_tens, epsmax1, sigma_res1;
    private final int digits = Jconc.getDigits();

    public MaterialSteelLinSoft (int id, double fc, double modEl, double modHard) {
	this.id = id;
	this.modEl = modEl;
	this.modHard = modHard;
	this.fc = fc;
	epsyt = fc/modEl;
	epsyc = -epsyt*1e-4;
	epsmax = epsyt*1e+10;
	modE3 = -modEl*1e+2;
	sigma_res = fc*1e-4;
    }

    public MaterialSteelLinSoft (int id, double fc, double modEl, double modHard, double epsmax) {
	this.id = id;
	this.modEl = modEl;
	this.modHard = modHard;
	this.fc = fc;
	epsyt = fc/modEl;
	epsyc = -epsyt*1e-4;
	this.epsmax = epsmax;
	modE3 = -modEl*1e+2;
	sigma_res = fc*1e-4;
    }

    public MaterialSteelLinSoft (int id, double fc, double modEl, double modHard, double epsmax, double sigma_res) {
	this.id = id;
	this.modEl = modEl;
	this.modHard = modHard;
	this.fc = fc;
	epsyt = fc/modEl;
	epsyc = -epsyt*1e-4;
	this.epsmax = epsmax;
	modE3 = -modEl*1e+2;
	this.sigma_res = sigma_res;
    }

    public int id () {
	return id;
    }

    public String type () {
	return "steelLinSoft";
    }

    public double modEl () {
	return modEl;
    }
    
    public double epsmax () {
	return epsmax;
    }

    public double sigma_res () {
	return sigma_res;
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
        epsmax1 = Math.max(epsyt, epsmax);
        sigma_res1 = Math.max(fc*1e-4,(Math.min(sigma_res,fc)));
	if (eps < epsyc) {
	    return (modEl*epsyc);
	}
	else if (eps <= epsyt) {
	    return (modEl*eps);
	}
        else {
            if (eps <= epsmax1) {
                sigma_tens = modEl*epsyt + modHard*(eps-epsyt);
            }
            else {
                sigma_tens = modEl*epsyt + modHard*(epsmax1-epsyt) + modE3*(eps-epsmax1);
            }
            if (sigma_tens < sigma_res1) {
                return (sigma_res1);
            }
            else {
                return (sigma_tens);
            }
        }
    }

    
    public double stress (double eps, double epsT) {
	//Same as the other, no reduction with epsT
        epsmax1 = Math.max(epsyt, epsmax);
        sigma_res1 = Math.max(fc*1e-4,(Math.min(sigma_res,fc)));
	if (eps < epsyc) {
	    return (modEl*epsyc);
	}
	else if (eps <= epsyt) {
	    return (modEl*eps);
	}
        else {
            if (eps <= epsmax1) {
                sigma_tens = modEl*epsyt + modHard*(eps-epsyt);
            }
            else {
                sigma_tens = modEl*epsyt + modHard*(epsmax1-epsyt) + modE3*(eps-epsmax1);
            }
            if (sigma_tens < sigma_res1) {
                return (sigma_res1);
            }
            else {
                return (sigma_tens);
            }
        }
    }
    
    public String toString () {
	return " Steel Tension Only & Linear Soft: fc = " + Jconc.formatNumber(fc, digits) +
                " E = " + Jconc.formatNumber(modEl, digits);
    }

}
