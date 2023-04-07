package com.ks.sd.util.sort;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SortCriteriaUtil {
    public static Sort processSort(String sortJson) {
        List<SortCriteria> sort = null;
        
        if (sortJson != null && !sortJson.isEmpty()) {
            sort = deserializeSortJson(sortJson);
        }

        if (sort == null || sort.isEmpty()) {
            return Sort.unsorted();
        }
    
        List<Sort.Order> orders = new ArrayList<>();
    
        for (SortCriteria criteria : sort) {
            Sort.Direction direction = Sort.Direction.fromString(criteria.getDirection());
            orders.add(new Sort.Order(direction, criteria.getColumn()));
        }
    
        return Sort.by(orders);
    }

    private static List<SortCriteria> deserializeSortJson(String sortJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<SortCriteria> sort = null;
        try {
            sort = objectMapper.readValue(sortJson, new TypeReference<List<SortCriteria>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid sort JSON format", e);
        }
        return sort;
    }
}
