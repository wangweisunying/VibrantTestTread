/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vibranttesttread;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Wei Wang
 */
public class test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        DateFormat dateFormat = new SimpleDateFormat("yyMMdd0000");
//        
//        String cur =  dateFormat.format(new Date());
//        System.out.println(cur);
//        System.out.println(dateDif(cur , "1811010000"));
System.out.println( System.currentTimeMillis());
    }
    
    
    
    private static int dateDif(String newJun, String oldJun) {
        String newDate = newJun.substring(0, 6);
        String oldDate = oldJun.substring(0, 6);

        int dif = (Integer.parseInt(newDate.substring(0, 2)) - Integer.parseInt(oldDate.substring(0, 2))) * 365
                + (Integer.parseInt(newDate.substring(2, 4)) - Integer.parseInt(oldDate.substring(2, 4))) * 30
                + (Integer.parseInt(newDate.substring(4)) - Integer.parseInt(oldDate.substring(4)));
//        System.out.println(dif);
        return dif ;
    }
    
}
