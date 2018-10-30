package com.maxplus1.db.starter.config;

public class Const {
    public enum BEAN_SUFFIX{
        TransactionManager("TransactionManager"),
        DataSource("DataSource"),
        SqlSessionFactory("SqlSessionFactory"),
        MyBatisConfiguration("MyBatisConfiguration"),
        SqlSessionTemplate("SqlSessionTemplate"),
        SqlSessionFactoryBean("SqlSessionFactoryBean"),
        MapperScannerConfigurer("MapperScannerConfigurer"),
        ;

        private String val;
        BEAN_SUFFIX(String val) {
            this.val = val;
        }

        public String val() {
            return val;
        }
    }


    public enum PROP_PREFIX{
        MyBatis("spring.maxplus1.mybatis.data-sources"),
        Druid("spring.maxplus1.druid.data-sources"),
        MyBatisCommon("spring.maxplus1.mybatis"),
        ;

        private String val;
        PROP_PREFIX(String val) {
            this.val = val;
        }

        public String val() {
            return val;
        }
    }
}
