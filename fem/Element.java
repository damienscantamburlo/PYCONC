package net.cercis.jconc.fem;

public interface Element {

    String type ();

    int id ();

    int getNumNodes ();

    int getNode (int k);

    Node node (int k);

    int material ();

    double parameter (int k);

    double getPercentS1 ();

    double getEta1 ();

    double getS1 ();

    double getEps1 ();

    double getPercentS2 ();

    double getEta2 ();

    double getS2 ();

    double getEps2 ();

    double getAlpha ();

    double getForceDif (String dir, int k);

    double getNodalForce (String dir, int node);
    
    void imposeDisp (double[] u, double[] v);

    void imposeIncrementalDisp (int node,String direction,double disp);

    String toString ();

}
