package net.cercis.jconc.fem;

public interface Material {

    int id ();
    
    String type ();

    double fc ();

    double modEl ();

    double getEta ();

    double stress (double eps);

    double stress (double epsL, double epsT);

    String toString ();

}
