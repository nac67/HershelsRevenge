package model;

import java.util.Arrays;

public class Encryption {
    static int min = 48;
    static int max = 122;
    static int[] donotuse = {92,33,32};

    static int smear(int hashCode) {
        hashCode *= 10;
        hashCode ^= (hashCode >>> 20) ^ (hashCode >>> 12);
        return hashCode ^ (hashCode >>> 7) ^ (hashCode >>> 4);
    }
    
    static int randomInt(){
        int r = 101;
        while (r == 101 || Arrays.asList(donotuse).contains(r)){
            r = min + (int)(Math.random() * ((max - min) + 1));
        }
        return r;
    }

    public static String encode (int level){
        String result = "";
        int smeared = smear(level);
        
        for(int i=0;i<smeared;i++){
            while(Math.random()>.1){
                result += (char) (randomInt());
            }
            result += (char) 101;
        }
        return result;
    }
    
    public static int decode (String gibberish){
        int count = 0;
        for(int i=0;i<gibberish.length();i++){
            if(gibberish.charAt(i) == (char) 101){
                count ++;
            }
        }
        
        for(int i=0;i<50;i++){
            if (smear(i) == count){
                return i;
            }
        }
        return -1;
    }
}
