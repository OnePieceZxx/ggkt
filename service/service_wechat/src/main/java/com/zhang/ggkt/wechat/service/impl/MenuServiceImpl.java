package com.zhang.ggkt.wechat.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.ggkt.model.wechat.Menu;
import com.atguigu.ggkt.vo.wechat.MenuVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhang.ggkt.exception.GgktException;
import com.zhang.ggkt.wechat.mapper.MenuMapper;
import com.zhang.ggkt.wechat.service.MenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单明细 订单明细 服务实现类
 * </p>
 *
 * @author zhang
 * @since 2023-05-14
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private WxMpService wxMpService;

    //获取所有菜单，按照一级和二级菜单封装
    @Override
    public List<MenuVo> findMenuInfo() {
        //创建list集合，最终封装
        List<MenuVo> menuVoList = new ArrayList<>();
        //查询出所有菜单数据
        List<Menu> menuList = baseMapper.selectList(null);
        //从所有菜单数据中获取一级菜单数据
        List<Menu> collect = menuList.stream().filter(menu -> menu.getParentId().longValue() == 0)
                .collect(Collectors.toList());
        //封装一级菜单数据，封装
        collect.stream().forEach(menu -> {
            MenuVo menuVo = new MenuVo();
            BeanUtils.copyProperties(menu,menuVo);

            //封装二级菜单数据
            List<Menu> menus = menuList.stream().filter(item -> item.getParentId().longValue() == menu.getId())
                    .collect(Collectors.toList());
            List<MenuVo> children = new ArrayList<>();
            menus.stream().forEach(data ->{
                MenuVo newMenuVo = new MenuVo();
                BeanUtils.copyProperties(data,newMenuVo);
                children.add(newMenuVo);
            });
            menuVo.setChildren(children);
            //数据封装
            menuVoList.add(menuVo);
        });
        return menuVoList;
    }

    //获取所有一级菜单
    @Override
    public List<Menu> findMenuOneInfo() {
        LambdaQueryWrapper<Menu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Menu::getParentId,0);
        List<Menu> menus = baseMapper.selectList(lambdaQueryWrapper);
        return menus;
    }

    //同步菜单
    @Override
    public void syncMenu() {
        List<MenuVo> menuVoList = this.findMenuInfo();
        //菜单
        JSONArray buttonList = new JSONArray();
        for(MenuVo oneMenuVo : menuVoList) {
            JSONObject one = new JSONObject();
            one.put("name", oneMenuVo.getName());
            JSONArray subButton = new JSONArray();
            for(MenuVo twoMenuVo : oneMenuVo.getChildren()) {
                JSONObject view = new JSONObject();
                view.put("type", twoMenuVo.getType());
                if(twoMenuVo.getType().equals("view")) {
                    view.put("name", twoMenuVo.getName());
                    view.put("url", "http://zhangxinxin.viphk.91tunnel.com/#"
                            +twoMenuVo.getUrl());
                } else {
                    view.put("name", twoMenuVo.getName());
                    view.put("key", twoMenuVo.getMeunKey());
                }
                subButton.add(view);
            }
            one.put("sub_button", subButton);
            buttonList.add(one);
        }
        //菜单
        JSONObject button = new JSONObject();
        button.put("button", buttonList);
        try {
            this.wxMpService.getMenuService().menuCreate(button.toJSONString());
        } catch (WxErrorException e) {
           throw new GgktException(20001,"公众号菜单同步失败！");
        }
    }

    @Override
    public void removeMenu() {
        try {
            wxMpService.getMenuService().menuDelete();
        } catch (WxErrorException e) {
            throw new GgktException(20001,"公众号菜单同步失败！");
        }
    }
}
