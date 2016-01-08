/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Encoder;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Pol i Oriol 
 */
public class Tesela {
    private int idOriginal;
    private ArrayList<Integer> xCoordDest;
    private ArrayList<Integer> yCoordDest;
    private BufferedImage tesela;

    public Tesela(BufferedImage tesela, int idOriginal) {
        this.idOriginal = idOriginal;
        this.tesela = tesela;
        this.xCoordDest = new ArrayList<>();
        this.yCoordDest = new ArrayList<>();
    }

    public int getIdOriginal() {
        return idOriginal;
    }

    public int getxCoordDest(int idx) {
        return xCoordDest.get(idx);
    }

    public int getyCoordDest(int idx) {
        return yCoordDest.get(idx);
    }

    public ArrayList<Integer> getxCoordDest() {
        return xCoordDest;
    }

    public ArrayList<Integer> getyCoordDest() {
        return yCoordDest;
    }

    public void setIdOriginal(int idOriginal) {
        this.idOriginal = idOriginal;
    }

    public void addxCoordDest(int xCoordDest) {
        this.xCoordDest.add(xCoordDest);
    }

    public void addyCoordDest(int yCoordDest) {
        this.yCoordDest.add(yCoordDest);
    }

    public BufferedImage getTesela() {
        return tesela;
    }
    
}
