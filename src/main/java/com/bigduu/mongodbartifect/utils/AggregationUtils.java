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
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
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


    public Aggregation getAggregation() {
        //初始化变量
        init();
        //遍历实体
        List<String> strings1 = new ArrayList<>();
        getPath(supperClassFields , searchBean , strings1);


        //        return operations;
        return newAggregation(operations);
    }


    private void project(Field field) {

    }

    private void unwind(Field field) {

    }

    private void match(String path , Object value , String type) {
        Criteria criteria = new Criteria(path);
        try {
            if (ObjectType.STRING.equals(type)) {
                criteria.regex(value.toString());

            } else {
                criteria.is(value);
            }

        } catch (Exception e) {
            log.error("match {}" , (Object) e.getStackTrace());
        }
        MatchOperation match = Aggregation.match(criteria);
        operations.add(match);
    }


    /**
     * @description: 递归循环遍历PATH
     * @param list 对象中需要遍历取path的list
     * @param name 改list的成员名称
     * @param path 承接path继续构造
     * @return java.util.List<java.lang.String>
     * @author mugeng.du
     * @date 2019-08-15 2:38 PM
     */
    private List<String> getListPath(List<Object> list , String name , List<String> path) {
        for (Object o : list) {
            path.add(name);
            Field[] declaredFields = o.getClass().getDeclaredFields();
            path = getPath(declaredFields , o , path);
        }
        return path;
    }

    private List<String> getPath(Field[] fields , Object targetBean , List<String> path) {
        for (Field field : fields) {
            field.setAccessible(true);
            String typeName = field.getType().getName();
            try {
                //判空
                if (field.get(targetBean) != null && !ObjectType.LOGER.equals(typeName)) {
                    //判断是否是LIST类型 如果是list进入递归循环找到path
                    if (ObjectType.LIST.equals(typeName)) {
                        String name = field.getName();
                        getListPath((List<Object>) field.get(targetBean) , name , path);
                        continue;
                    }
                    //如果是基本类型就直接进入跳出阶段 直接match
                    if (ObjectType.STRING.equals(typeName) || ObjectType.DOUBLE.equals(typeName) || ObjectType.FLOAT.equals(typeName) || ObjectType.LONG.equals(typeName) || ObjectType.INTEGER.equals(typeName) || ObjectType.BOOLEAN.equals(typeName) || ObjectType.DATE.equals(typeName)) {
                        path.add(field.getName());
                        String join = String.join("." , path);
                        match(join , field.get(targetBean) , typeName);
                        test.add(path);
                        path = new ArrayList<>();
                    }else {
                        //如果不是基本类型 则封装一下List 让其遍历path
                        String name = field.getName();
                        Object o = field.get(targetBean);
                        List<Object>  list = new ArrayList<>();
                        list.add(o);
                        getListPath(list, name , path);
                        path = new ArrayList<>();
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
