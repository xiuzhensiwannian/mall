package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * author  TZQ
 * date  2020/2/25 21:19
 */

public class Const {
    public static final String CURRENT_USER = "currentUser";
    public static final String EMAIL = "emailc";
    public static final String USERNAME= "username";

    public  interface  Role{
        int ROLE_CUSTOMER=0;
        int ROLE_ADMIN=1;
    }

    public  interface  ProductListOrderBy{
        Set<String > PRICE_ASC_DESC= Sets.newHashSet("price_asc", "price_desc");
    }

    public enum ProductStatusEnum{
        ON_SALE(1, "在线");

        private  String  value;
        private  int code;

        ProductStatusEnum( int code, String  value) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }
}
