package com.mright.discuss.framework.security;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author zhaochuanzhen
 * @date 2021/2/17 17:16
 * @desc
 */
public class GrantedAuthorityImpl implements GrantedAuthority {

    private static final long serialVersionUID = 1L;

    private String authority;

    public GrantedAuthorityImpl(String authority) {
        this.authority = authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }
}
