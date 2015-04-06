package com.rafa.codered;
import android.graphics.Bitmap;

public class DetectEdge
{
    public int[] getColors(Bitmap BM, int x, int y) {
        int[] colors = new int[9];

        colors[0] = BM.getPixel(x-1, y+1);  //top left
        colors[1] = BM.getPixel(x, y+1);  //top center
        colors[2] = BM.getPixel(x+1, y+1);  //top right
        colors[3] = BM.getPixel(x-1, y);  //middle left
        colors[4] = BM.getPixel(x, y);  //center point
        colors[5] = BM.getPixel(x+1, y);  //middle right
        colors[6] = BM.getPixel(x-1, y-1);  //bottom left
        colors[7] = BM.getPixel(x, y-1);  //bottom center
        colors[8] = BM.getPixel(x+1, y-1);  //bottom right

        return colors;
    }

    public int[] getDifferenceInColor(int[] colors) {
        int[] colorDiffs = new int[9];

        int temp;

        int colorTemp = colors[4];

        int centerBlue = colorTemp & 0xff;
        int centerGreen = (colorTemp>>8) & 0xff;
        int centerRed = (colorTemp>>8) & 0xff;

        int testBlue;
        int testGreen;
        int testRed;

        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 3; j++){
                colorTemp = colors[i];
                testBlue = colorTemp & 0xff;
                testGreen = (colorTemp>>8) & 0xff;
                testRed = (colorTemp>>8) & 0xff;

                colorDiffs[i] = Math.abs(testBlue - centerBlue);

                temp = Math.abs(testGreen - centerGreen);
                if(colorDiffs[i] < temp)
                    colorDiffs[i] = temp;

                temp = Math.abs(testRed - centerRed);
                if(colorDiffs[i] < temp)
                    colorDiffs[i] = temp;
            }
        }

        return colorDiffs;
    }

    public Bitmap parseEdges(Bitmap BM) {
        final int bWidth = BM.getWidth();
        final int bHeight = BM.getHeight();

        Bitmap gaussianTransform = Bitmap.createBitmap(bWidth-1, bHeight-1, Bitmap.Config.ARGB_8888);
        Bitmap ret = Bitmap.createBitmap(bWidth-2, bHeight-2, Bitmap.Config.ARGB_8888);

        for(int x = 1; x < bWidth-1; x++) {
            for(int y = 1; y < bHeight-1; y++) {
                int[] colors = getColors(BM, x, y);

                for(int k = 0; k < 9; k++) {
                    int blue = colors[k] & 0xff;
                    int green = (colors[k]>>8) & 0xff;
                    int red = (colors[k]>>16) & 0xff;

                    int dividend;

                    if(k % 2 == 1)
                        dividend = 8;
                    else {
                        if(k != 4)
                            dividend = 16;
                        else
                            dividend = 4;
                    }

                    blue /= dividend;
                    green /= dividend;
                    red /= dividend;

                    colors[k] = 0xff;
                    colors[k] = (colors[k] << 8) + red;
                    colors[k] = (colors[k] << 8) + green;
                    colors[k] = (colors[k] << 8) + blue;
                }

                int blur = colors[0]+colors[1]+colors[2]+colors[3]+colors[4]+colors[5]+colors[6]+colors[7]+colors[8];

                gaussianTransform.setPixel(x, y, blur);
            }
        }

        for(int x = 2; x < bWidth-2; x++) {
            for(int y = 2; y < bHeight-2; y++) {
                int[] colorsY = getColors(gaussianTransform, x, y);
                int[] colorsX = getColors(gaussianTransform, x, y);

                for(int k = 0; k < 3; k++) {
                    int blue = colorsX[k] & 0xff;
                    int green = (colorsX[k]>>8) & 0xff;
                    int red = (colorsX[k]>>16) & 0xff;

                    int blue2 = colorsX[k+6] & 0xff;
                    int green2 = (colorsX[k+6]>>8) & 0xff;
                    int red2 = (colorsX[k+6]>>16) & 0xff;

                    int dividend;

                    if(k % 2 == 0)
                        dividend = 4;
                    else {
                        dividend = 2;
                    }

                    blue /= dividend;
                    green /= dividend;
                    red /= dividend;
                    blue2 /= dividend;
                    green2 /= dividend;
                    red2 /= dividend;

                    blue = Math.abs(blue - blue2);
                    green = Math.abs(green - green2);
                    red = Math.abs(red - red2);

                    int biggest = blue;
                    if(biggest < green)
                        biggest = green;
                    if(biggest< red)
                        biggest = red;

                    colorsX[k] = 0xff;
                    colorsX[k] = (colorsX[k] << 8) + biggest;
                    colorsX[k] = (colorsX[k] << 8) + biggest;
                    colorsX[k] = (colorsX[k] << 8) + biggest;
                }

                for(int k = 0; k < 7; k++) {
                    if(k == 1)
                        k = 3;
                    if(k == 4)
                        k = 6;

                    int blue = colorsY[k] & 0xff;
                    int green = (colorsY[k]>>8) & 0xff;
                    int red = (colorsY[k]>>16) & 0xff;

                    int blue2 = colorsY[k+2] & 0xff;
                    int green2 = (colorsY[k+2]>>8) & 0xff;
                    int red2 = (colorsY[k+2]>>16) & 0xff;

                    int dividend;

                    if(k % 2 == 0)
                        dividend = 4;
                    else {
                        dividend = 2;
                    }

                    blue /= dividend;
                    green /= dividend;
                    red /= dividend;
                    blue2 /= dividend;
                    green2 /= dividend;
                    red2 /= dividend;

                    blue = Math.abs(blue - blue2);
                    green = Math.abs(green - green2);
                    red = Math.abs(red - red2);

                    int biggest = blue;
                    if(biggest < green)
                        biggest = green;
                    if(biggest< red)
                        biggest = red;

                    colorsY[k] = 0xff;
                    colorsY[k] = (colorsY[k] << 8) + biggest;
                    colorsY[k] = (colorsY[k] << 8) + biggest;
                    colorsY[k] = (colorsY[k] << 8) + biggest;
                }

                int derivativeOfY = colorsY[0]+colorsY[1]+colorsY[2];

                int derivativeOfX = colorsX[0]+colorsX[3]+colorsX[6];

                int greyY = derivativeOfY & 0xff;
                int greyX = derivativeOfX & 0xff;
                int greyAvg = (greyY + greyX)/2;

                //int greyMagnitude = (int) Math.sqrt(Math.pow(greyY, 2) + Math.pow(greyX, 2));
                //(0xff<<24) + (greyMagnitude<<16) + (greyMagnitude<<8) + greyMagnitude;

                int magnitude =  (0xff<<24) + (greyAvg<<16) + (greyAvg<<8) + greyAvg;

                ret.setPixel(x, y, magnitude);
            }
        }

        return ret;
    }
}