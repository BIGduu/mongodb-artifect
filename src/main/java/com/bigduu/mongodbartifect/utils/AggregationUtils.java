package com.bigduu.mongodbartifect.utils;


import com.bigduu.mongodbartifect.Dictionary.ObjectType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

/**
 * @author mugeng.du
 */
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

    private List<AggregationOperation> operations = new ArrayList<>();

    private List<List<String>> test = new ArrayList<>();


    public List<String> getAggregation() {
        //初始化变量
        init();
        //遍历实体
        List<String> path = getPath(supperClassFields , searchBean);

        int i = path.indexOf("|");
        List<String> strings = path.subList(0 , i);

        return path;

        //        return newAggregation(operations);
    }


    private void project(Field field) {

    }

    private void unwind(Field field) {

    }

    private Criteria match(Field field , Object targetObj , String path) {
        String typeName = field.getType().getName();
        Criteria criteria = new Criteria(path);
        //        try {
        //            //            Object matchObj = field.get(this.searchBean);
        //
        //            //            if (ObjectType.STRING.equals(typeName)) {
        //            //
        //            //
        //            //            } else {
        //            //
        //            //            }
        //
        //        } catch (Exception e) {
        //            log.error("match {}" , (Object) e.getStackTrace());
        //        }
        return criteria;
    }


    private void listMatch(Field field , Criteria criteria , Object targetBean) {
        try {
            Object list = field.get(targetBean);
            Field sizeField = list.getClass().getDeclaredField("size");
            sizeField.setAccessible(true);
            Integer size = (Integer) sizeField.get(list);
            if (size != 0) {

            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            log.error("listMatch {}" , (Object) e.getStackTrace());
        }
    }

    //    private List<Criteria> run(Field[] fields , Object targetBean) {
    //        List<Criteria> criteriaList = new ArrayList<>();
    //        for (Field field : fields) {
    //            field.setAccessible(true);
    //            String typeName = field.getType().getName();
    //            try {
    //                if (field.get(targetBean) != null && !ObjectType.LOGER.equals(typeName)) {
    //                    Criteria criteria = new Criteria(field.getName());
    //                    if (ObjectType.LIST.equals(typeName)) {
    //                        listMatch(field , criteria , targetBean);
    //                    } else {
    //                        Criteria match = match(field , criteria);
    //                        criteriaList.add(match);
    //                    }
    //
    //                }
    //            } catch (IllegalAccessException e) {
    //                log.error("run {}" , (Object) e.getStackTrace());
    //            }
    //
    //        }
    //        return criteriaList;
    //    }


    private List<String> getListPath(List<Object> list , String name) {
        List<String> path = new ArrayList<>();
        for (Object o : list) {
            path.add(name);
            Field[] declaredFields = o.getClass().getDeclaredFields();
            List<String> path1 = getPath(declaredFields , o);
            path.addAll(path1);
        }
        return path;
    }

    private List<String> getPath(Field[] fields , Object targetBean) {
        List<String> path = new ArrayList<>();
        for (Field field : fields) {
            field.setAccessible(true);
            String typeName = field.getType().getName();
            try {
                //判空
                if (field.get(targetBean) != null && !ObjectType.LOGER.equals(typeName)) {
                    if (ObjectType.LIST.equals(typeName)) {
                        String name = field.getName();
                        List<String> listPath = getListPath((List<Object>) field.get(targetBean) , name);
                        path.addAll(listPath);
                    }
                    if (ObjectType.STRING.equals(typeName) || ObjectType.DOUBLE.equals(typeName) || ObjectType.FLOAT.equals(typeName) || ObjectType.LONG.equals(typeName) || ObjectType.INTEGER.equals(typeName) || ObjectType.BOOLEAN.equals(typeName) || ObjectType.DATE.equals(typeName)) {
                        path.add(field.getName());
                        path.add(",");
                        path.add(field.get(targetBean).toString());
                        path.add(typeName);
                        path.add("|");

                    }
                }
            } catch (IllegalAccessException e) {
                log.error("run {}" , (Object) e.getStackTrace());
            }
        }
        return path;
    }


    private void init() {
        this.searchBeanClass = searchBean.getClass();
        this.fields = this.searchBeanClass.getDeclaredFields();
        this.superClass = this.searchBeanClass.getSuperclass();
        this.supperClassFields = this.superClass.getDeclaredFields();
    }


}
