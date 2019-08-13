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
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith (SpringRunner.class)
@SpringBootTest
public class MongodbArtifectApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private MongoTemplate mongoTemplate;



    @Test
    public void contextLoads() {
        Department testDepartment1 = Department.builder().name("testDepartment1").build();
        Department testDepartment2 = Department.builder().name("testDepartment2").build();
        List<Department> departments = new ArrayList<>();
        departments.add(testDepartment1);
        departments.add(testDepartment2);

        UserSearchBean userSearchBean = new UserSearchBean();
        userSearchBean.setName("bigduu");
        userSearchBean.setDepartmentList(departments);

        AggregationUtils aggregationUtils = new AggregationUtils();
        aggregationUtils.setSearchBean(userSearchBean);
        Aggregation aggregation = aggregationUtils.getAggregation();

        AggregationResults<Object> user = mongoTemplate.aggregate(aggregation , "user" , Object.class);
        user.getMappedResults().forEach(System.out::println);

    }

}
