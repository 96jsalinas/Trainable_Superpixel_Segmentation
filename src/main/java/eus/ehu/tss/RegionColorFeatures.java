package eus.ehu.tss;

import ij.ImagePlus;
import ij.plugin.ChannelSplitter;
import ij.process.ColorSpaceConverter;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * This class will extract region features from colored images using the Lab color space
 */
public class RegionColorFeatures {

    /**
     * Creates Instances object based on the features of each color channel after converting the image to Lab
     * @param inputImage RGB input image
     * @param labelImage Label image
     * @param selectedFeatures ArrayList with selected features from RegionFeatures.Feature
     * @param classes ArrayList of Strings with names of the classes
     * @return
     */
    public static Instances calculateColorFeatures(ImagePlus inputImage,
                                                   ImagePlus labelImage,
                                                   ArrayList<RegionFeatures.Feature> selectedFeatures,
                                                   ArrayList<String> classes)
    {
        ColorSpaceConverter converter = new ColorSpaceConverter();
        ImagePlus lab = converter.RGBToLab(inputImage);
        ImagePlus[] channels = ChannelSplitter.split(lab);
        Instances lIns = RegionFeatures.calculateRegionFeatures(channels[0],labelImage,selectedFeatures,classes);
        Instances aIns = RegionFeatures.calculateRegionFeatures(channels[1],labelImage,selectedFeatures,classes);
        Instances bIns = RegionFeatures.calculateRegionFeatures(channels[2],labelImage,selectedFeatures,classes);
        for(int i=0;i<lIns.numAttributes();++i){//all channels should have the same number of attributes
            lIns.renameAttribute(i,lIns.attribute(i).name()+"-L");
            aIns.renameAttribute(i,aIns.attribute(i).name()+"-a");
            bIns.renameAttribute(i,bIns.attribute(i).name()+"-b");
        }
        ArrayList<Attribute> attributes = new ArrayList<Attribute>();
        int numAttributes = lIns.numAttributes()*3-3;//-3 to remove the class attributes
        for(int i=0;i<lIns.numAttributes()-1;++i){
            attributes.add(lIns.attribute(i));
        }
        for(int i=0;i<aIns.numAttributes()-1;++i){
            attributes.add(aIns.attribute(i));
        }
        for(int i=0;i<bIns.numAttributes()-1;++i){
            attributes.add(bIns.attribute(i));
        }
        attributes.add(new Attribute("Class",classes));
        Instances unlabeled = new Instances("dataset", attributes,0);
        for(int i=0;i<lIns.numInstances();++i){
            int k =0;
            Instance inst = new DenseInstance(numAttributes+1);
            for(int j=0;j<lIns.numAttributes()-1;++j){
                inst.setValue(j,lIns.get(i).value(j));
            }
            for(int j=lIns.numAttributes()-1;j<aIns.numAttributes()*2-2;++j){
                inst.setValue(j,aIns.get(i).value(k));
                k++;
            }
            k=0;
            for(int j=lIns.numAttributes()*2-2;j<bIns.numAttributes()*3-3;++j){
                inst.setValue(j,bIns.get(i).value(k));
                k++;
            }
            inst.setValue(numAttributes,0);//Set class as 0
            unlabeled.add(inst);
        }
        unlabeled.setClassIndex(numAttributes);

        return unlabeled;
    }


}