package com.bigduu.mongodbartifect.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author mugeng.du
 */
@Document
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {

    @Id
    private String id;

    private String name;

    private String name1;
}
