/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vibranttesttread;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.DataBaseCon;
import model.ExcelOperation;
import model.V7DataBaseCon;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import panel.Micronutrients_V3_Panel;
import panel.Panel;

/**
 *
 * @author Wei Wang
 */
public class VibrantTestTread {

    /**
     * @param args the command line arguments
     */
    private final String pillarId = "MNWM201901291_WBCMETALV3_pillar_20190202160423";
    private final String path = "C:\\Users\\Wei Wang\\Desktop\\TestTread\\";

    // //Map<testcode ,Map< julienBarcode , unit>>
    private Map<String, Map<String, Double>> unitMap, pillarUnitMap;
    
 
    
    // Map<testCode , range[]> 
    private Map<String, double[]> refMap;
    private Map<String, String> newOldMap;
    private List<String> list;

    public static void main(String[] args) throws SQLException, IOException, Exception {
        VibrantTestTread test = new VibrantTestTread(new Micronutrients_V3_Panel());
        test.run();
//        test.ExportExcel(test.path);
//    System.out.println(in3Month("1901230034" ,"1901150003"));
    }
    public void run() throws IOException{
        getDupUnit(list);
        ExportExcel(path , getTreadMap(unitMap, refMap, 20, 7));
    }
    
    VibrantTestTread(Panel panel) throws SQLException, Exception {
        this.list = getJulienBarcode(pillarId);
        this.unitMap = panel.getUnitMap();
        this.pillarUnitMap = panel.getPillarUnitMap(pillarId);
        this.refMap = panel.getRefMap(list);
        this.newOldMap = panel.getDupMap();
    }

    // //Map<testcode ,Map< julienBarcode , unit>>
    public Map<String, Map<String, Double>> getDupUnit(List<String> list) {
        List<String> dupExsistList = new ArrayList();
        for (String newJun : list) {
            if (newOldMap.containsKey(newJun)) {
                dupExsistList.add(newOldMap.get(newJun));
            }
        }

        if (!dupExsistList.isEmpty()) {
            Map<String, Map<String, Double>> map = new LinkedHashMap();

            for (String oldJun : dupExsistList) {
                for (String testCode : unitMap.keySet()) {
                    if (unitMap.get(testCode).containsKey(oldJun)) {
                        map.computeIfAbsent(testCode, x -> new HashMap()).put(oldJun, unitMap.get(testCode).get(oldJun));
                    } else {
                        break;
                    }
                }
            }
            if (map.isEmpty()) {
                return null;
            } else {
                return map;
            }
        } else {
            return null;
        }
    }

    private List<String> getJulienBarcode(String pillarId) throws SQLException {
        DataBaseCon dbV7 = new V7DataBaseCon();
        String sql = "select julien_barcode from vibrant_test_tracking.well_info where well_plate_id = (select well_plate_id from vibrant_test_tracking.pillar_plate_info where pillar_plate_id = '" + pillarId + "');";
        List<String> list = new ArrayList();
        ResultSet rs = dbV7.read(sql);
        while (rs.next()) {
            list.add(rs.getString(1));
        }
        dbV7.close();
        return list;
    }

    private void ExportExcel(String path , Map<String, Map<String, Map<Integer, TreadUnit>>> treadMap) throws IOException {

        Workbook wb = ExcelOperation.getWriteConnection(ExcelOperation.ExcelType.XLSX);
        Sheet sheet = wb.createSheet("Unit");
        Sheet sheetT = wb.createSheet("Tread");
        
        
        CellStyle cs = wb.createCellStyle();
        cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cs.setFillForegroundColor(IndexedColors.RED.getIndex());
        
        CellStyle csBlue = wb.createCellStyle();
        csBlue.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        csBlue.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        
        DecimalFormat df = new DecimalFormat("#.00");
        int rowCt = 0, colCt = 0;
        Row rowTitle = sheet.createRow(rowCt++);
        rowTitle.createCell(0).setCellValue("TestCode");
        rowTitle.createCell(1).setCellValue("low");
        rowTitle.createCell(2).setCellValue("high");
        rowTitle.createCell(3).setCellValue("lowPercentage");
        rowTitle.createCell(4).setCellValue("highPercentage");
        for (String testCode : pillarUnitMap.keySet()) {
            colCt = 0;
            
            double[] arr = refMap.get(testCode);
            if(arr == null){
                continue;
            }
//            if(arr[0] == 0 && arr[1] == 0){
//                System.out.println(123);
//                continue;  
//            } 

            Row curRow = sheet.createRow(rowCt++);
            curRow.createCell(colCt++).setCellValue(testCode);
            curRow.createCell(colCt++).setCellValue(arr[0]);
            curRow.createCell(colCt++).setCellValue(arr[1]);
            int lowPCol = colCt++;
            int HighPCol = colCt++;
            
            Map<String, Double> map = pillarUnitMap.get(testCode);
            int lowCt = 0 , highCt = 0;
            for (String newJun : list) {
                if (map.containsKey(newJun)) {
                    rowTitle.createCell(colCt).setCellValue(newJun);
                    Cell curCell = curRow.createCell(colCt++);
                    curCell.setCellValue(map.get(newJun));
                    if (map.get(newJun) > arr[1] && arr[1] != 0) {
                        curCell.setCellStyle(cs);
                        ++ highCt;
                    }
                    if (map.get(newJun) < arr[0] && arr[0] != 0) {
                        if(map.get(newJun) > 0){
                            curCell.setCellStyle(csBlue);
                            ++ lowCt;
                        }
                    }
                    if (newOldMap.containsKey(newJun)) {

                        String oldJun = newOldMap.get(newJun);
                        System.out.println(newJun + oldJun);
                        if (unitMap.get(testCode).containsKey(oldJun)) {
                            Cell cell = rowTitle.createCell(colCt);
                            cell.setCellValue("Dup-" + oldJun);
                            cell.setCellStyle(cs);
                            curRow.createCell(colCt++).setCellValue(unitMap.get(testCode).get(oldJun));
                        }
                    }

                }

            }
            curRow.createCell(lowPCol).setCellValue(df.format((double)lowCt * 100 /list.size()) + "%");
            curRow.createCell(HighPCol).setCellValue(df.format((double)highCt * 100 /list.size()) + "%");
            
            
        }
        
        
        
        rowCt = 0;
        colCt = 0;
        rowTitle = sheetT.createRow(rowCt++);
        
        
        
        for(String testCode : treadMap.keySet()){   
            for(String range : treadMap.get(testCode).keySet()){
                Row curRow = sheetT.createRow(rowCt++);
                colCt = 0;
                curRow.createCell(colCt++).setCellValue(testCode);
                curRow.createCell(colCt++).setCellValue(range);
                
                for(int week : treadMap.get(testCode).get(range).keySet()){
                    TreadUnit unit = treadMap.get(testCode).get(range).get(week);
                    rowTitle.createCell(colCt).setCellValue("week" + week);
                    curRow.createCell(colCt++).setCellValue(unit.count);
                    rowTitle.createCell(colCt).setCellValue("week" + week);
                    Cell cell = curRow.createCell(colCt++);
                    cell.setCellValue(df.format(unit.percent * 100 )+ "%");
                    cell.setCellStyle(cs);
                }
            
            }
        
        }
        
        
        
        
        
        ExcelOperation.writeExcel(path + pillarId + "_" + System.currentTimeMillis() +".xlsx", wb);
    }

    private int dateDif(String newJun, String oldJun) {
        String newDate = newJun.substring(0, 6);
        String oldDate = oldJun.substring(0, 6);

        int dif = (Integer.parseInt(newDate.substring(0, 2)) - Integer.parseInt(oldDate.substring(0, 2))) * 365
                + (Integer.parseInt(newDate.substring(2, 4)) - Integer.parseInt(oldDate.substring(2, 4))) * 30
                + (Integer.parseInt(newDate.substring(4)) - Integer.parseInt(oldDate.substring(4)));
//        System.out.println(dif);
        return dif ;
    }
    
    
    //
    class TreadUnit{
        int count;
        float percent;
        TreadUnit(int count , float percent){
            this.count = count;
            this.percent = percent;
        }
    }
    // Map<testCode<range , <week , (Integer , float)>>>
    private Map<String, Map<String, Map<Integer, TreadUnit>>> getTreadMap(Map<String, Map<String, Double>> unitMap , Map<String, double[]> refMap ,int numOfUnit , int days ) {
        Map<String, Map<String, Map<Integer, TreadUnit>>> map = new LinkedHashMap();
        DateFormat dateFormat = new SimpleDateFormat("yyMMdd0000");
//        System.out.println(dateFormat.format(new Date()));
        
        String curJun = dateFormat.format(new Date());
        for(String  testCode : unitMap.keySet()){
            Map<String, Map<Integer, TreadUnit>> curMap  =  new LinkedHashMap();
            List<String> list = new ArrayList(unitMap.get(testCode).keySet());
            
            curMap.put("low" , new LinkedHashMap());
            curMap.put("normal" , new LinkedHashMap());
            curMap.put("high" , new LinkedHashMap());
            for(int i = 0 ; i < numOfUnit ; ++i){
                curMap.get("low").put(i, new TreadUnit(0 , 0));
                curMap.get("normal").put(i, new TreadUnit(0 , 0));
                curMap.get("high").put(i, new TreadUnit(0 , 0));
            } 
            
            double[] arr = refMap.get(testCode);
            
            // sort julien  high to low
            Collections.sort(list ,(a , b) -> (Integer.parseInt(b) - Integer.parseInt(a)));
            for(String julien : list){
//                datedif(curJun , julien) % 7
                double unit = unitMap.get(testCode).get(julien);
                int index = dateDif(curJun , julien) / days;
                if(index >= numOfUnit || index < 0) continue;
                if(unit < arr[0] && unit >= 0){
                    curMap.get("low").get(index).count++;
                }
                else if(unit > arr[1]){
                    curMap.get("high").get(index).count++;
                }
                else{
                    curMap.get("normal").get(index).count++;
                }
            }
            
            Map<Integer , Integer> mapSum = new HashMap();
            for(String range : curMap.keySet()){
                Map<Integer , TreadUnit> tmpMap = curMap.get(range);
                for(int i : tmpMap.keySet()){
                    mapSum.put(i , mapSum.getOrDefault(i , 0) + tmpMap.get(i).count );
                }
            }
            for(String range : curMap.keySet()){
                for(int i : curMap.get(range).keySet()){
                    curMap.get(range).get(i).percent = (float)curMap.get(range).get(i).count / (float)mapSum.get(i);
                }
            }
            
            map.put(testCode , curMap);
        
        
        }
//        for(String testcode : map.keySet()){
//            for(String range : map.get(testcode).keySet()){
//                for(int i :  map.get(testcode).get(range).keySet()){
//                   System.out.println(testcode  + "   " + range + " week " + i +"  count "+ map.get(testcode).get(range).get(i).count + "   " + map.get(testcode).get(range).get(i).percent);
//                }
//            }
//        
//        }
        
        
        
        return map;
        

    }

}
