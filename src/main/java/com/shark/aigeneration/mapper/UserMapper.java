package com.shark.aigeneration.mapper;

import com.mybatisflex.core.BaseMapper;
import com.shark.aigeneration.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 映射层。
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
