import jAudioFeatureExtractor.ACE.DataTypes.Batch;

import java.io.File;
import java.util.LinkedList;

/**
 * Created by Daniel McEnnis on 2/21/2015.
 * Psuedo-Locale Sensitive Hashing Algorithm
 * Similarity algorithm: cast back to an array of doubles and use a similarity measure like Euclidean.
 * This roughly corresponds to timbral similarity of two items.
 *
 * Licensed under the LGPL
 */
public class LocaleSenstiiveHash {

    public static int main(String[] args) throws Exception{
        if (args.length < 2){
            throw new java.io.IOException("Hashing requires a file list to execute");
        }
        boolean raw = false;
        int i=0;
        if (args[0] == "--bytes"){
            i++;
            raw = true;
        }
        LinkedList<File> files = new LinkedList<File>();
        for(int a=0;a<args.length;++a){
            Batch b = new Batch();
            b.setRecordings(new File[]{new File(args[a])});
            b.execute();
            double[][] results = b.getDataModel().container.getResults();
            for(int j=0;j<results.length;++j){
                for(int k=0;k<results[j].length;++k){
                    if(raw){
                        System.out.print(Double.toHexString(results[j][k]));
                    }else {
                        if ((k != 0) || (j != 0)) {
                            System.out.print("\t");
                        }
                        System.out.print(results[k][j]);
                    }
                }
            }
            System.out.println();
        }
        return 0;
    }
}
