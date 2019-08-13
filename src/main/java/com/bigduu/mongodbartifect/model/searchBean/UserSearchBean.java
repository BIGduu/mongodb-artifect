package com.bigduu.mongodbartifect.model.searchBean;

import com.bigduu.mongodbartifect.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
public class UserSearchBean extends User {

    private Timestamp timestamp;
}
