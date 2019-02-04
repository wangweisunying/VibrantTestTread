/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.List;

/**
 *
 * @author Wei Wang
 */
public class StringOperation {
    public static StringBuilder getSampleSql(List<Integer> sampleIdList) throws Exception{
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
}
