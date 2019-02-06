/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vibranttesttread;

import java.io.IOException;
import java.sql.SQLException;
import model.DataBaseCon;
import model.ExcelOperation;
import model.V7DataBaseCon;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author Wei Wang
 */
public class UpdateDB {
    public static void main(String[] args) throws IOException, SQLException{
        UpdateDB test = new UpdateDB();
        //                  modify pillar_id ,                modify fileName
        test.run("MNWW201901291_WBCWSV3_pillar_20190201201654" , "MNWW201901291_WBCWSV3_pillar_20190201201654_1549390666642");
    }
    private void run(String pillarId , String fileName) throws IOException, SQLException{
        Workbook wb = ExcelOperation.getReadConnection("C:\\Users\\Wei Wang\\Desktop\\TestTread\\"+ fileName +".xlsx", ExcelOperation.ExcelType.XLSX);
        Sheet sheet = wb.getSheetAt(0);
        Row rowTitle = sheet.getRow(0);
        DataBaseCon db = new V7DataBaseCon();
        int colCt = 5;
        int rowCt = 1;
        while(sheet.getRow(rowCt) != null){
            Row curRow = sheet.getRow(rowCt++);
            String testCode  = curRow.getCell(0).getStringCellValue();
            while(curRow.getCell(colCt)!= null){
                String julien = rowTitle.getCell(colCt).getStringCellValue();
                double unit = curRow.getCell(colCt++).getNumericCellValue();
                if(julien.startsWith("Dup")) continue;
                String sql = "update tsp_test_unit_data.test_unit_data set unit = '"+ unit +"' where pillar_plate_id = '"+ pillarId +"' and test_name = '"+ testCode +"' and julien_barcode = '"+ julien +"';";
                db.write(sql);
                System.out.println(sql);
                
            
            }
            colCt = 5;
        }
        db.close();
    }
}
