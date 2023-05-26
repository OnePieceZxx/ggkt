package com.zhang.ggkt.activity.service.impl;


import com.atguigu.ggkt.model.activity.CouponInfo;
import com.atguigu.ggkt.model.activity.CouponUse;
import com.atguigu.ggkt.model.user.UserInfo;
import com.atguigu.ggkt.vo.activity.CouponUseQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhang.ggkt.activity.mapper.CouponInfoMapper;
import com.zhang.ggkt.activity.service.CouponInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhang.ggkt.activity.service.CouponUseService;
import com.zhang.ggkt.client.user.UserInfoFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 优惠券信息 服务实现类
 * </p>
 *
 * @author zhang
 * @since 2023-05-13
 */
@Service
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo> implements CouponInfoService {

    @Autowired
    private CouponUseService couponUseService;

    @Autowired
    private UserInfoFeignClient userInfoFeignClient;
    //获取已使用优惠券分页列表
    @Override
    public IPage<CouponUse> selectCouponUsePage(Page<CouponUse> couponUsePage, CouponUseQueryVo couponUseQueryVo) {

        //获取条件
        Long couponId = couponUseQueryVo.getCouponId();
        String couponStatus = couponUseQueryVo.getCouponStatus();
        String getTimeBegin = couponUseQueryVo.getGetTimeBegin();
        String getTimeEnd = couponUseQueryVo.getGetTimeEnd();

        //封装条件
        LambdaQueryWrapper<CouponUse> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(!StringUtils.isEmpty(couponId),CouponUse::getCouponId,couponId)
                .eq(!StringUtils.isEmpty(couponStatus),CouponUse::getCouponStatus,couponStatus)
                .ge(!StringUtils.isEmpty(getTimeBegin),CouponUse::getGetTime,getTimeBegin)
                .le(!StringUtils.isEmpty(getTimeEnd),CouponUse::getGetTime,getTimeEnd);

        //调用方法条件查询
        Page<CouponUse> usePage = couponUseService.page(couponUsePage, lambdaQueryWrapper);
        List<CouponUse> records = usePage.getRecords();
        //遍历
        records.stream().forEach(item ->{
            this.getUserInfoById(item);
        });
        return usePage;
    }

    //更新优惠卷状态
    @Override
    public void updateCouponInfoUseStatus(Long couponUseId, Long orderId) {
        CouponUse couponUse = new CouponUse();
        couponUse.setId(couponUseId);
        couponUse.setOrderId(orderId);
        couponUse.setCouponStatus("1");
        couponUse.setUsingTime(new Date());
        couponUseService.updateById(couponUse);
    }

    //根据用户id，通过远程调用得到用户信息
    private CouponUse getUserInfoById(CouponUse couponUse) {
        //获取用户id
        Long userId = couponUse.getUserId();
        if (!StringUtils.isEmpty(userId)){
            //远程调用
            UserInfo userInfo = userInfoFeignClient.getById(userId);
            if (userInfo != null){
                couponUse.getParam().put("nickName", userInfo.getNickName());
                couponUse.getParam().put("phone", userInfo.getPhone());
            }
        }
        return couponUse;
    }
}
