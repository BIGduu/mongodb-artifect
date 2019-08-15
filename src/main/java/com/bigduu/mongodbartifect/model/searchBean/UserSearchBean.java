package com.bigduu.mongodbartifect.model.searchBean;

import com.bigduu.mongodbartifect.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;


/**
 * @author mugeng.du
 */
@EqualsAndHashCode (callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
public class UserSearchBean extends User {

    private Timestamp timestamp;
}
