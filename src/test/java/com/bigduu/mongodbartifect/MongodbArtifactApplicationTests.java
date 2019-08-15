package com.bigduu.mongodbartifect;

import com.bigduu.mongodbartifect.model.Department;
import com.bigduu.mongodbartifect.model.User;
import com.bigduu.mongodbartifect.model.searchBean.UserSearchBean;
import com.bigduu.mongodbartifect.service.DepartmentService;
import com.bigduu.mongodbartifect.service.UserService;
import com.bigduu.mongodbartifect.utils.AggregationUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith (SpringRunner.class)
@SpringBootTest
public class MongodbArtifactApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void add() {
        Department testDepartment1 = Department.builder().name("testnumber").number(11).build();
        Department testDepartment2 = Department.builder().name("testnumber2").number(22).build();

        User userSearchBean = new User();
        userSearchBean.setName("bigduuNumberTest1");

//        userSearchBean.setDepartmentList(departments);
        userSearchBean.setAge(26);
        userSearchBean.setDepartment(testDepartment2);
//        userSearchBean.setDepartment1(testDepartment1);

        userService.save(userSearchBean);
    }


    @Test
    public void contextLoads() {
        Department testDepartment1 = Department.builder().name("testDepartment1").build();
        Department testDepartment2 = Department.builder().name("testDepartment2").build();
        List<Department> departments = new ArrayList<>();
        departments.add(testDepartment1);
        departments.add(testDepartment2);

        UserSearchBean userSearchBean = new UserSearchBean();
//        userSearchBean.setName("test1");

//        userSearchBean.setDepartmentList(departments);
//        userSearchBean.setGreatThanAge(24);
//        userSearchBean.setLessThanAge(28);
        userSearchBean.setLessThanDepartmentNumber(15);
//        userSearchBean.setDepartment(testDepartment1);
//        userSearchBean.setDepartment1(testDepartment1);


        AggregationUtils aggregationUtils = new AggregationUtils();
        aggregationUtils.setSearchBean(userSearchBean);
        Aggregation aggregation = aggregationUtils.getAggregation();


        AggregationResults<Object> user = mongoTemplate.aggregate(aggregation , "user" , Object.class);
        user.getMappedResults().forEach(System.out::println);

    }

}
