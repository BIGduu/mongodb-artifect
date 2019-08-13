package com.bigduu.mongodbartifect.utils;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

@Data
@AllArgsConstructor
@Log4j2
@NoArgsConstructor
public class AggregationUtils {

    @Autowired
    private MongoTemplate template;

    private Object searchBean;

    private Class<?> searchBeanClass;
    private Field[] fields;
    private Class<?> superClass;
    private Field[] supperClassFields;


    public Aggregation getAggregation() {
        //初始化变量
        init();
        List<UnwindOperation> unwind = this.unwind();
        ProjectionOperation id = project("id");
        Aggregation aggregation = newAggregation((AggregationOperation) unwind ,id);


        return aggregation;
    }

    private List<UnwindOperation> unwind() {
        List<UnwindOperation> unwindOperations = new ArrayList<>();
        for (Field field : this.supperClassFields) {
            field.setAccessible(true);
            if ("java.util.List".equals(field.getType().getName())) {
                try {
                    Object o = field.get(this.searchBean);
                    Field sizeField = o.getClass().getDeclaredField("size");
                    sizeField.setAccessible(true);
                    Integer size = (Integer) sizeField.get(o);
                    if (size != 0) {
                        unwindOperations.add(Aggregation.unwind(field.getName()));
                    }
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    log.info("unwind {}" , (Object) e.getStackTrace());
                }
            }
        }

        return unwindOperations;
    }


    private void init() {
        this.searchBeanClass = searchBean.getClass();
        this.fields = this.searchBeanClass.getDeclaredFields();
        this.superClass = this.searchBeanClass.getSuperclass();
        this.supperClassFields = this.superClass.getDeclaredFields();
    }


}
