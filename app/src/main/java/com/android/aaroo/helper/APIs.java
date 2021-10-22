package com.android.aaroo.helper;

public class APIs {

    // 1: CREDIT,2:PAYMENT
    // 1: Customer , 2 Supplier
    // 1:popular , 2:other
    public static final String BASE_URL = "http://aminbroilerhouse.in/AarooApp/api/";
    public static final String BASE_IMAGE_URL = "http://aminbroilerhouse.in/AarooApp/";
    public static final String BASE_URL_WEBVIEW = "";

    public static final String LOGIN_API = BASE_URL + "login"; //mobile
    public static final String CHECK_OTP_API = BASE_URL + "checkOtp"; //mobile,otp

    public static final String INTRO_API = BASE_URL + "intro"; //get
    public static final String POST_INTRO_SLIDER_API = BASE_URL + "intro"; //title,description,image

    public static final String SINGLE_USER_DATA_API = BASE_URL + "getUserInfo/"; //id,name,mobile,email,about,image,address,bussinessName,bussinessCategory
    public static final String USER_DATA_SAVE_API = BASE_URL + "userDetailSave";
    public static final String CUSTOMER_ALL_DATA_API = BASE_URL + "cust";//id,customerName,mobile,customerType,address,image,smsStatus,status,createdBy,ipaddress
    public static final String CUSTOMER_ADD_API = BASE_URL + "cust";
    public static final String SUPPLIER_ALL_DATA_API = BASE_URL + "supplier";
    public static final String SUPPLIER_ADD_API = BASE_URL + "cust";
    public static final String CUSTOMER_AND_SUPPLIER_EDIT_API = BASE_URL + "cust/";
    public static final String CUSTOMER_AND_SUPPLIER_DELETE_API = BASE_URL + "custDelete";//{"id":9}
    public static final String CUSTOMER_AND_SUPPLIER_UPDATE_API = BASE_URL + "custUpdate";//id
    public static final String TRANSACTION_ADD_API = BASE_URL + "trans"; //customerID,paymentMethod,amount,paymentTime,paymentDate,addNote,userid,billImage
    public static final String TRANSACTION_DATA_API = BASE_URL + "trans";//customerID,userid
    public static final String TRANSACTION_SINGLE_EDIT_API = BASE_URL + "trans/id/edit";//id
  //  public static final String TRANSACTION_SINGLE_UPDATE_API = BASE_URL + "trans/id/edit";
   // public static final String TRANSACTION_SINGLE_DELETE_API = BASE_URL + "transDelete";//id

    public static final String TRANSACTION_SINGLE_UPDATE_API = BASE_URL + "transUpdate";
    public static final String TRANSACTION_SINGLE_DELETE_API = BASE_URL + "transDelete";
    public static final String CUSTOM_CASHBOOK_API = BASE_URL + "customerCashBook";//userid,fromdate,todate  GET
    public static final String SUPPLIER_CASHBOOK_API = BASE_URL + "supplierCashBook";//userid,fromdate,todate  GET
    public static final String CATEGORY_API = BASE_URL + "cate"; // type 1:popular , 2:other
    public static final String CUSTOMERCashBookBalance_API = BASE_URL + "customerCashBookBalance";//userid
    public static final String SUPPLIERCashBookBalance_API = BASE_URL + "supplierCashBookBalance";//userid
    public static final String CUSTOMER_BALANCE_API = BASE_URL + "customerBalance";
    public static final String CHANGE_OTP_API = BASE_URL + "changeOTP";
    public static final String CHECK_MOBILE_OTP_API = BASE_URL + "checkMobileOTP";
    public static final String CHANGE_MOBILE_API = BASE_URL + "changeMobile";
    public static final String _API = BASE_URL + "";

}
