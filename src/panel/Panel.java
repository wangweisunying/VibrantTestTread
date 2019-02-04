/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panel;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Wei Wang
 */
public abstract class Panel {
    protected String testName , DBRefRangeTable;
    protected String[] DBTableNameArr , DBMasterListTableNameArr , testCodeArr;
    protected double[][] treadRangeArr;
    
    
    public String getTestName(){
        return this.testName;
    }
    
    
    public double[][] getTreadRange(){
        return this.treadRangeArr;
    }
    
    
    public String[] getTestCodeArr(){
        return this.testCodeArr;
    }
    
    public abstract Map<String, Map<String , Double>> getUnitMap();
    
    
    public abstract Map<String , double[]> getRefMap(List<String> list) throws Exception;
    
    
    public abstract Map<String , String> getDupMap();
    
    public abstract Map<String, Map<String, Double>> getPillarUnitMap(String pillarId) throws Exception;
}
