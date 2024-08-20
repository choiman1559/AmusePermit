package com.amuse.permit.process;

@SuppressWarnings("unused")
public class ProcessConst {
    public static final String PACKAGE_MODULE = "com.amuse.permit";
    public static final String PACKAGE_BROADCAST = PACKAGE_MODULE + ".process.ProcessRoute";
    public static final String PACKAGE_BROADCAST_ACTION = PACKAGE_MODULE + ".DATA_TRANSFER";
    public static final String PACKAGE_STREAM = PACKAGE_MODULE + ".process.ProcessStream";

    public static final String ACTION_TYPE_HANDSHAKE = "handshake";
    public static final String ACTION_TYPE_FILE = "file";
    public static final String ACTION_TYPE_LOCATION = "locate";
    public static final String ACTION_TYPE_PACKAGE = "pkg";
    public static final String ACTION_TYPE_TELEPHONY = "telephony";
    public static final String ACTION_TYPE_SMS = "sms";
    public static final String ACTION_TYPE_CURSOR = "cursor";

    public static final String ACTION_REQUEST_CLASS = "action_request_class";
    public static final String ACTION_REQUEST_METHOD = "action_request_method";
    public static final String ACTION_REQUEST_MEMBER = "action_request_member";
    public static final String ACTION_REQUEST_STREAM = "action_request_stream";
    public static final String ACTION_REQUEST_HANDSHAKE = "action_request_handshake";

    public static final String ACTION_RESPONSE_CLASS = "action_response_class";
    public static final String ACTION_RESPONSE_METHOD = "action_response_method";
    public static final String ACTION_RESPONSE_MEMBER = "action_response_member";
    public static final String ACTION_RESPONSE_STREAM = "action_response_stream";
    public static final String ACTION_RESPONSE_HANDSHAKE = "action_response_handshake";
    public static final String ACTION_RESPONSE_EXCEPTION = "action_response_exception";

    public static final String KEY_API_TYPE = "key_api_type";
    public static final String KEY_ACTION_TYPE = "key_action_type";
    public static final String KEY_PACKAGE_NAME = "key_package_name";
    public static final String KEY_ARGS = "key_args";
    public static final String KEY_EXTRA_DATA = "key_extra_data";
    public static final String KEY_STREAM_KEY = "key_stream_key";
    public static final String KEY_TICKET_ID = "key_ticket_id";

    public static final String KEY_PARCEL_REPLACED = "key_parcel_replaced";
    public static final String KEY_PARCEL_LIST_REPLACED = "key_parcel_list_replaced";
    public static final String KEY_EXTRA_PARCEL_DATA = "key_extra_parcel_data";
    public static final String KEY_EXTRA_PARCEL_LIST_DATA = "key_extra_parcel_list_data";

    public static final String STREAM_KEY_PREFIX = "stream_%s_%s";
    public static final String STREAM_INPUT = "input";
    public static final String STREAM_OUTPUT = "output";
    public static final String STREAM_AUTH_URI = "content://%s$%s/%s";
}
