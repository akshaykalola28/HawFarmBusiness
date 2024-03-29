package com.project.hawfarmbusiness;

public class ServerData {

    private static final String LOCAL_IP = "http://192.168.47.1:8000/";
    private static final String SERVER_HOSTNAME = "https://us-central1-hawfarm-2019.cloudfunctions.net/api/";

    //Change this only for choose localhost or server host
    public static final String CURRENT_HOST = SERVER_HOSTNAME;

    public static final String REGISTER_URL = CURRENT_HOST + "user/register";
    public static final String LOGIN_URL = CURRENT_HOST + "user/login";

    public static final String ADD_STOCK_URL = CURRENT_HOST + "stock/add";
    public static final String PRODUCT_URL = CURRENT_HOST + "product.json";

    public static final String ALL_STOCK_URL = CURRENT_HOST + "stock/all/";

    public static final String GET_ORDER_URL = CURRENT_HOST + "order/seller/";
    public static final String STATUS_UPDATE_URL = CURRENT_HOST + "order/status/";

}
