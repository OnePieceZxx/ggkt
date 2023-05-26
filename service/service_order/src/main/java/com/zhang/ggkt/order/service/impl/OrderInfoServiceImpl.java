package com.zhang.ggkt.order.service.impl;


import com.atguigu.ggkt.model.activity.CouponInfo;
import com.atguigu.ggkt.model.order.OrderDetail;
import com.atguigu.ggkt.model.order.OrderInfo;
import com.atguigu.ggkt.model.user.UserInfo;
import com.atguigu.ggkt.model.vod.Course;
import com.atguigu.ggkt.vo.order.OrderFormVo;
import com.atguigu.ggkt.vo.order.OrderInfoQueryVo;
import com.atguigu.ggkt.vo.order.OrderInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhang.ggkt.client.activity.CouponInfoFeignClient;
import com.zhang.ggkt.client.course.CourseFeignClient;
import com.zhang.ggkt.client.user.UserInfoFeignClient;
import com.zhang.ggkt.exception.GgktException;
import com.zhang.ggkt.order.mapper.OrderInfoMapper;
import com.zhang.ggkt.order.service.OrderDetailService;
import com.zhang.ggkt.order.service.OrderInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhang.ggkt.result.ResultCodeEnum;
import com.zhang.ggkt.utils.AuthContextHolder;
import com.zhang.ggkt.utils.OrderNoUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单表 订单表 服务实现类
 * </p>
 *
 * @author zhang
 * @since 2023-05-13
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private CourseFeignClient courseFeignClient;

    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    @Autowired
    private CouponInfoFeignClient couponInfoFeignClient;


    //订单列表
    @Override
    public Map<String, Object> selectOrderInfoPage(Page<OrderInfo> orderInfoPage, OrderInfoQueryVo orderInfoQueryVo) {

        //获取查询条件
        Long userId = orderInfoQueryVo.getUserId();
        String outTradeNo = orderInfoQueryVo.getOutTradeNo();
        String phone = orderInfoQueryVo.getPhone();
        String createTimeEnd = orderInfoQueryVo.getCreateTimeEnd();
        String createTimeBegin = orderInfoQueryVo.getCreateTimeBegin();
        Integer orderStatus = orderInfoQueryVo.getOrderStatus();

        //判断条件值是否为空，进行条件封装
        LambdaQueryWrapper<OrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(!StringUtils.isEmpty(orderStatus),OrderInfo::getOrderStatus,orderStatus)
                .eq(!StringUtils.isEmpty(userId),OrderInfo::getUserId,userId)
                .eq(!StringUtils.isEmpty(outTradeNo),OrderInfo::getOutTradeNo,outTradeNo)
                .eq(!StringUtils.isEmpty(phone),OrderInfo::getPhone,phone)
                .ge(!StringUtils.isEmpty(createTimeBegin),OrderInfo::getCreateTime,createTimeBegin)
                .le(!StringUtils.isEmpty(createTimeEnd),OrderInfo::getCreateTime,createTimeEnd);
        //调用方法实现分页查询
        Page<OrderInfo> selectPage = baseMapper.selectPage(orderInfoPage, lambdaQueryWrapper);
        long totalCount = selectPage.getTotal();
        long pagesCount = selectPage.getPages();
        List<OrderInfo> orderInfoList = selectPage.getRecords();

        //封装详情数据
        orderInfoList.stream().forEach(item ->{
            this.getOrderDetail(item);
        });
        //将所有数据封装的map中
        Map<String,Object> map = new HashMap<>();
        map.put("total",totalCount);
        map.put("pageCount",pagesCount);
        map.put("records",orderInfoList);
        return map;
    }

    //生成订单方法
    @Override
    public Long submitOrder(OrderFormVo orderFormVo) {
        //获取生成订单值
        Long userId = AuthContextHolder.getUserId();
        Long courseId = orderFormVo.getCourseId();
        Long couponId = orderFormVo.getCouponId();

        //判断当前用户是否已经生成订单
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getCourseId, courseId)
                    .eq(OrderDetail::getUserId, userId);
        OrderDetail orderDetailExist = orderDetailService.getOne(queryWrapper);
        if(orderDetailExist != null){
            return orderDetailExist.getId(); //如果订单已存在，则直接返回订单id
        }

        //根据课程id查询课程信息
        Course course = courseFeignClient.getById(courseId);
        if (course == null) {
            throw new GgktException(20001,"课程不存在！");
        }

        //根据用户id查询用户信息
        UserInfo userInfo = userInfoFeignClient.getById(userId);
        if (userInfo == null) {
            throw new GgktException(20001,"用户不存在！");
        }

        //根据优惠卷id查询优惠卷信息
        BigDecimal couponReduce = new BigDecimal(0);
        if(null != couponId) {
            CouponInfo couponInfo = couponInfoFeignClient.getById(couponId);
            couponReduce = couponInfo.getAmount();
        }


        //封装订单生成需要数据对象，完成添加订单
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserId(userId);
        orderInfo.setNickName(userInfo.getNickName());
        orderInfo.setPhone(userInfo.getPhone());
        orderInfo.setProvince(userInfo.getProvince());
        orderInfo.setOriginAmount(course.getPrice());
        orderInfo.setCouponReduce(couponReduce);
        orderInfo.setFinalAmount(orderInfo.getOriginAmount().subtract(orderInfo.getCouponReduce()));
        orderInfo.setOutTradeNo(OrderNoUtils.getOrderNo());
        orderInfo.setTradeBody(course.getTitle());
        orderInfo.setOrderStatus("0");
        this.save(orderInfo);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderInfo.getId());
        orderDetail.setUserId(userId);
        orderDetail.setCourseId(courseId);
        orderDetail.setCourseName(course.getTitle());
        orderDetail.setCover(course.getCover());
        orderDetail.setOriginAmount(course.getPrice());
        orderDetail.setCouponReduce(new BigDecimal(0));
        orderDetail.setFinalAmount(orderDetail.getOriginAmount().subtract(orderDetail.getCouponReduce()));
        orderDetailService.save(orderDetail);

        //更新优惠卷状态
        if(null != orderFormVo.getCouponUseId()) {
            couponInfoFeignClient.updateCouponInfoUseStatus(orderFormVo.getCouponUseId(), orderInfo.getId());
        }

        //返回订单id
        return orderInfo.getId();
    }

    @Override
    public OrderInfoVo getOrderInfoVoById(Long id) {
        OrderInfo orderInfo = this.getById(id);
        OrderDetail orderDetail = orderDetailService.getById(id);

        OrderInfoVo orderInfoVo = new OrderInfoVo();
        BeanUtils.copyProperties(orderInfo, orderInfoVo);
        orderInfoVo.setCourseId(orderDetail.getCourseId());
        orderInfoVo.setCourseName(orderDetail.getCourseName());
        return orderInfoVo;
    }

    @Override
    public void updateOrderStatus(String out_trade_no) {
        //根据out_trade_no查询订单
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfo::getOutTradeNo,out_trade_no);
        OrderInfo orderInfo = baseMapper.selectOne(wrapper);
        //更新订单状态 1 已经支付
        orderInfo.setOrderStatus("1");
        baseMapper.updateById(orderInfo);
    }

    //查询订单详情数据
    private OrderInfo getOrderDetail(OrderInfo orderInfo) {
        //订单id
        Long id = orderInfo.getId();
        //查询订单详情
        OrderDetail detail = orderDetailService.getById(id);
        if (detail != null){
            String courseName = detail.getCourseName();
            orderInfo.getParam().put("courseName",courseName);
        }
        return orderInfo;
    }
}
