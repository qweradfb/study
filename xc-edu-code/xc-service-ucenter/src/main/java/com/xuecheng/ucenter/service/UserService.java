package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private XcCompanyUserRepository xcCompanyUserRepository;

    @Autowired
    private XcUserRepository xcUserRepository;

    @Autowired
    private XcMenuMapper xcMenuMapper;


    public XcUserExt getUserExt(String username){
        XcUser xcUser = xcUserRepository.findXcUserByUsername(username);
        if (xcUser==null){
            return null;
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser,xcUserExt);
        String id = xcUser.getId();
        if (StringUtils.isEmpty(id)) return null;
        XcCompanyUser byUserId = xcCompanyUserRepository.findByUserId(id);
        if (byUserId!=null){
            String companyId = byUserId.getCompanyId();
            xcUserExt.setCompanyId(companyId);
        }
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(id);
        if (xcMenus==null) xcMenus = new ArrayList<>();
        xcUserExt.setPermissions(xcMenus);
        return xcUserExt;

    }


}
