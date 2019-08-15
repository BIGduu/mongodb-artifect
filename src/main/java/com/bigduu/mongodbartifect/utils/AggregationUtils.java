package com.bigduu.mongodbartifect.utils;


import com.bigduu.mongodbartifect.Dictionary.ObjectType;
import com.bigduu.mongodbartifect.annotation.GreatThan;
import com.bigduu.mongodbartifect.annotation.LessThan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
        //获取super匹配路径
        getAllPath(supperClassFields , searchBean , new ArrayList<>(),false);
        getMatch();
        return newAggregation(operations);
    }

    private void supperMatch(String path , Object value , String type) {
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
     * @param list 对象中需要遍历取path的list
     * @param name 改list的成员名称
     * @param path 承接path继续构造
     * @return java.util.List<java.lang.String>
     * @description: 递归循环遍历PATH
     * @author mugeng.du
     * @date 2019-08-15 2:38 PM
     */
    private void getListPath(List<Object> list , String name , List<String> path) {
        for (Object o : list) {
            path.add(name);
            Field[] declaredFields = o.getClass().getDeclaredFields();
            getAllPath(declaredFields , o , path,true);
            path = new ArrayList<>();
        }
    }

    private void getAllPath(Field[] fields , Object targetBean , List<String> path,Boolean isInList) {
        for (Field field : fields) {
            field.setAccessible(true);
            String typeName = field.getType().getName();
            try {
                //判空
                if (field.get(targetBean) != null && !"".equals(field.get(targetBean).toString()) && !ObjectType.LOGER.equals(typeName)) {
                    //判断是否是LIST类型 如果是list进入递归循环找到path
                    if (ObjectType.LIST.equals(typeName)) {
                        String name = field.getName();
                        getListPath((List<Object>) field.get(targetBean) , name , path);
                        continue;
                    }
                    //如果是基本类型就直接进入跳出阶段 直接match
                    if (ObjectType.STRING.equals(typeName) || ObjectType.DOUBLE.equals(typeName) || ObjectType.FLOAT.equals(typeName) || ObjectType.LONG.equals(typeName) || ObjectType.INTEGER.equals(typeName) || ObjectType.BOOLEAN.equals(typeName) || ObjectType.DATE.equals(typeName)) {
                        List<String> tmp = new ArrayList<>();
                        //如果再list中遍历
                        if (isInList) {
                            tmp = new ArrayList<>(path);
                        }
                        path.add(field.getName());
                        String join = String.join("." , path);
                        supperMatch(join , field.get(targetBean) , typeName);
                        test.add(path);
                        //如果在list中遍历 则不应该清空path
                        if (isInList) {
                            path = new ArrayList<>(tmp);
                        } else {
                            path = new ArrayList<>();
                        }
                    } else {
                        //如果不是基本类型 则封装一下List 让其遍历path
                        String name = field.getName();
                        Object o = field.get(targetBean);
                        List<Object> list = new ArrayList<>();
                        list.add(o);
                        getListPath(list , name , path);
                        path = new ArrayList<>();
                    }

                }
            } catch (IllegalAccessException e) {
                log.error("run {}" , (Object) e.getStackTrace());
            }
        }
        path = new ArrayList<>();
    }

    private void getMatch() {
        for (Field field : this.fields) {
            field.setAccessible(true);
            String typeName = field.getType().getName();
            Object value = null;
            try {
                value = field.get(this.searchBean);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if (value != null && !"".equals(value.toString()) && !ObjectType.LOGER.equals(typeName)) {
                GreatThan greatThan = field.getAnnotation(GreatThan.class);
                if (greatThan != null) {
                    String targetField = greatThan.value();
                    targetField = getTargetFieldPath(targetField);
                    Criteria gt = new Criteria(targetField).gt(value);
                    MatchOperation match = Aggregation.match(gt);
                    this.operations.add(match);
                    continue;
                }
                LessThan lessThan = field.getAnnotation(LessThan.class);
                if (lessThan != null) {
                    String targetField = lessThan.value();
                    targetField = getTargetFieldPath(targetField);
                    Criteria lt = new Criteria(targetField).lt(value);
                    MatchOperation match = Aggregation.match(lt);
                    this.operations.add(match);
                }

            }


        }

    }

    private String getTargetFieldPath(String s) {
        String[] s1 = s.split("_");
        return String.join("." , s1);
    }


    private void init() {
        this.searchBeanClass = searchBean.getClass();
        this.fields = this.searchBeanClass.getDeclaredFields();
        this.superClass = this.searchBeanClass.getSuperclass();
        this.supperClassFields = this.superClass.getDeclaredFields();
    }


}
