package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Reservation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预约Mapper接口
 * 
 * @author library-system
 */
@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {
}

