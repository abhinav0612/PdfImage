package com.example.pdfimage;

import android.graphics.Matrix;
/**
 * Created by Abhinav Singh on 18,August,2019
 */

public class ImageList {
   private String url;
   private Boolean isZoomed=false;
   private Matrix matrix;

    public ImageList(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public Boolean getZoomed() {
        return isZoomed;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public void setZoomed(Boolean zoomed) {
        isZoomed = zoomed;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
