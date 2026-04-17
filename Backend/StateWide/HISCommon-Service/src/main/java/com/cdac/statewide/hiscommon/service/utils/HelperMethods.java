package com.cdac.statewide.hiscommon.service.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HelperMethods {

    public static List<Map<String, Object>> toListWithColumnNames(
            List<Object[]> resultList,
            String[] columns) {

        List<Map<String, Object>> records = new ArrayList<>();

        for (Object[] row : resultList) {
            Map<String, Object> rowMap = new LinkedHashMap<>(); // keeps order
            for (int i = 0; i < columns.length; i++) {
                rowMap.put(columns[i], row[i]);
            }
            records.add(rowMap);
        }
        return records;
    }
}
