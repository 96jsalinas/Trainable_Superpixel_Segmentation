package eus.ehu.tss;

import ij.IJ;
import ij.ImagePlus;
import weka.classifiers.lazy.IBk;

import java.util.ArrayList;

public class TestSuperpixelSegmentation{
    public static void main(final String[] args){
        ImagePlus inputImage = IJ.openImage( TestSuperpixelSegmentation.class.getResource( "/grains.png" ).getFile() );
        inputImage.show();
        ImagePlus labelImage = IJ.openImage( TestSuperpixelSegmentation.class.getResource( "/grains-catchment-basins.png").getFile() );
        labelImage.show();
        ArrayList<RegionFeatures.Feature> selectedFeatures = new ArrayList<>();
        selectedFeatures.add(RegionFeatures.Feature.fromLabel("Mean"));
        selectedFeatures.add(RegionFeatures.Feature.fromLabel("Median"));
        selectedFeatures.add(RegionFeatures.Feature.fromLabel("Mode"));
        selectedFeatures.add(RegionFeatures.Feature.fromLabel("Skewness"));
        selectedFeatures.add(RegionFeatures.Feature.fromLabel("Kurtosis"));
        selectedFeatures.add(RegionFeatures.Feature.fromLabel("StdDev"));
        selectedFeatures.add(RegionFeatures.Feature.fromLabel("Max"));
        selectedFeatures.add(RegionFeatures.Feature.fromLabel("Min"));

        IBk exampleClassifier = new IBk();
        TrainableSuperpixelSegmentation test = new TrainableSuperpixelSegmentation(inputImage,labelImage, selectedFeatures, exampleClassifier);
        System.out.println(test.getFeaturesByRegion());
        int[] rice = new int[4];
        rice[0] = 30; rice[1]=43; rice[2]=96;rice[3]=99;
        int[] background = new int[1];
        background[0] = 1;
        ArrayList<int[]> tags = new ArrayList<>();
        tags.add(background); tags.add(rice);
        final ArrayList<String> classes = new ArrayList<String>();
        for(int i=0;i<2;++i){
            classes.add("class "+(i+1));
        }
        if(test.trainClassifier(tags, classes)){
            ImagePlus result = test.applyClassifier();
            result.show();
        }
    }
}