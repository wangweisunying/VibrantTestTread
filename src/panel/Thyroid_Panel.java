/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.DataBaseCon;
import model.LXDataBaseCon;
import model.V7DataBaseCon;

/**
 *
 * @author Wei Wang
 */
public class Thyroid_Panel extends Panel{
        public Thyroid_Panel(){
        super();
        this.testName = "Thyroid";
        this.DBTableNameArr = new String[]{"`vibrant_america_test_result`.`Result_Thyroid_Panel`"};
        this.DBMasterListTableNameArr = new String[]{"`vibrant_america_test_result_ml`.`master_list_Thyroid_Panel`"};
        this.DBRefRangeTable = "vibrant_america_information.report_master_list_tracking";
        
        this.testCodeArr = new String[]{"`T4`",
                                        "`FT4`",
                                        "`FT3`",
                                        "`T3`",
                                        "`TSH`",
                                        "`ATPO`",
                                        "`RT3`",
                                        "`A-TG`"

                                        };
    }
    
    
    
    @Override
    public Map<String, Map<String, Double>> getPillarUnitMap(String pillarId) throws Exception{
        Map<String, Map<String, Double>> map = new HashMap<>();
        DataBaseCon db = new V7DataBaseCon();
        String sql = "select * from tsp_test_unit_data.test_unit_data where pillar_plate_id = '"+ pillarId +"' order by test_name,julien_barcode ;";
        ResultSet rs = db.read(sql);
        while(rs.next()){
            String testName = rs.getString(1);
            String julien = rs.getString(2);
            double unit = rs.getDouble(4);
            map.computeIfAbsent(testName, x -> new HashMap()).put(julien , unit);
        }
//        for(String key : map.keySet()){
//                System.out.println(key + "  " + map.get(key));
//            }
        db.close();
        
        
        return map;
    }
    
    
    @Override //Map<testcode ,Map< julienBarcode , unit>>
    public Map<String, Map<String, Double>> getUnitMap() {
        Map<String, Map<String, Double>> map = new LinkedHashMap<>();
        try {
            DataBaseCon db = new LXDataBaseCon();
            
            ResultSet rs = db.read(sqlGenerater(DBTableNameArr));
            int columnCt = rs.getMetaData().getColumnCount();
            for (int i = 3; i <= columnCt; ++i) {
                map.put(rs.getMetaData().getColumnLabel(i), new HashMap());
            }
            while (rs.next()) {
                String julienBarcode = rs.getString(2);
                for (int i = 3; i <= columnCt; ++i) {
                    String testCode = rs.getMetaData().getColumnLabel(i);
                    map.get(testCode).put(julienBarcode, rs.getDouble(i));
                }
            }
            
//            for(String key : map.keySet()){
//                System.out.println(key + "  " + map.get(key));
//            }
            db.close();
            

        } catch (SQLException ex) {
            System.out.println(ex.getClass() + ex.getMessage());
        }
        return map;
    }
    
    private String sqlGenerater(String[] tableArr){
        StringBuilder sb = new StringBuilder("select sd.sample_id  , sd.julien_barcode , ");
        for (String testCode : testCodeArr) {
            sb.append(testCode).append(" ,");
        }
        sb.setLength(sb.length() - 1);
        sb.append("from ").append(tableArr[0]);
        String tmp = tableArr[0] + ".sample_id = ";
        for (int i = 1; i < tableArr.length; ++i) {
            sb.append(" join ").append(tableArr[i]).append(" on ").append(tmp).append(tableArr[i]).append(".sample_id ");
        }

        sb.append(" join vibrant_america_information.sample_data as sd on ").append(tmp).append("sd.sample_id ");
        System.out.println(sb.toString());
        return sb.toString();
    }
    
    
    
    
    @Override
    public Map<String, double[]> getRefMap(List<String> list) throws SQLException{
        Map<String, double[]> map = new HashMap();      
     
            
            
        DataBaseCon db = new LXDataBaseCon();

        StringBuilder sb = new StringBuilder();
        for(String testCode : testCodeArr){
            sb.append(testCode).append(",");
        }
        sb.setLength(sb.length() - 1);
        String tmp = sb.toString().replaceAll("`", "'");
        System.out.println(tmp);
        String sql = "select test_code ,  substring_index(group_concat(normal_min order by tracking_time desc),',',1) , substring_index(group_concat(normal_max order by tracking_time desc),',',1)  from `vibrant_america_information`.`report_master_list_tracking` where test_code in ("+ tmp +")  group by test_code  order by tracking_time desc;";
        System.out.println(sql);
        ResultSet rs = db.read(sql);
        while(rs.next()){
            map.put(rs.getString(1), new double[]{rs.getDouble(2) ,rs.getDouble(3)});
        }          
        db.close();
            
            
       
        
        return map;
    }
    
    
    private StringBuilder getSampleSql(List<String> sampleIdList) throws Exception{
        StringBuilder sbSampleId = new StringBuilder();
        if(sampleIdList == null || sampleIdList.size() == 0){
            throw new Exception("sampleIdList can not be empty!");
        }
        for (String sampleId : sampleIdList) {
            sbSampleId.append("'").append(sampleId).append("',");
        }
        sbSampleId.setLength(sbSampleId.length() - 1);
        return sbSampleId;
    }
    
    
    private StringBuilder getSampleIntgerSql(List<Integer> sampleIdList) throws Exception{
        StringBuilder sbSampleId = new StringBuilder();
        if(sampleIdList == null || sampleIdList.size() == 0){
            throw new Exception("sampleIdList can not be empty!");
        }
        for (int sampleId : sampleIdList) {
            sbSampleId.append(sampleId).append(",");
        }
        sbSampleId.setLength(sbSampleId.length() - 1);
        return sbSampleId;
    }

    @Override
    public Map<String, String> getDupMap() {
        Map<String, String> map = new HashMap();
        try {
            String sql = "SELECT\n" +
"  substring_index(group_concat(sd.julien_barcode order by sd.julien_barcode desc),',',2) as julien_barcode,substring_index(group_concat(pd.patient_firstname,' ',pd.patient_lastname),',',2) as name\n" +
"FROM\n" +
"    vibrant_america_information.`patient_details` pd\n" +
"       JOIN\n" +
"    vibrant_america_information.`sample_data` sd ON sd.`patient_id` = pd.`patient_id`\n" +
"        JOIN\n" +
"         vibrant_america_information.selected_test_list slt on slt.sample_id = sd.sample_id\n" +
"WHERE\n" +
"   slt.Order_Thyroid_Panel!= 0 \n" +
"   group by pd.`patient_id` having count(*)>1  order by julien_barcode desc;";
            System.out.println(sql);
            ResultSet rs =  new LXDataBaseCon().read(sql);
            while(rs.next()){
                String[] julienArr = rs.getString(1).split(",");
                map.put(julienArr[0] , julienArr[1]);
            
            }
        } catch (SQLException ex) {
            System.out.println(ex.getClass() + ex.getMessage());
            ex.printStackTrace();
        }
        return map;
    }
}
