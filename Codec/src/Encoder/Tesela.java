/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Encoder;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * This class represents a tile
 * @author Pol i Oriol 
 */
public class Tesela {
    private int idOriginal;
    private int xCoordDest;
    private int yCoordDest;
    private BufferedImage tesela;
    private int x;
    private int y;
    
    /**
     * Construct
     * @param tesela
     * @param idOriginal 
     */
    public Tesela(BufferedImage tesela, int idOriginal) {
        this.idOriginal = idOriginal;
        this.tesela = tesela;
    }

    /**
     * Get I-frame ID
     * @return idOriginal
     */
    public int getIdOriginal() {
        return idOriginal;
    }

    /**
     * Get P-frames X coordinates array
     * @return xCoordDest
     */
    public int getxCoordDest() {
        return xCoordDest;
    }

    /**
     * Get P-frames Y coordinates array
     * @return yCoordDest
     */
    public int getyCoordDest() {
        return yCoordDest;
    }

    /**
     * Set I-frame id
     * @param idOriginal 
     */
    public void setIdOriginal(int idOriginal) {
        this.idOriginal = idOriginal;
    }
    
    /**
     * Get tile
     * @return tesela
     */
    public BufferedImage getTesela() {
        return tesela;
    }

    public void setxCoordDest(int xCoordDest) {
        this.xCoordDest = xCoordDest;
    }

    public void setyCoordDest(int yCoordDest) {
        this.yCoordDest = yCoordDest;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
    
}
