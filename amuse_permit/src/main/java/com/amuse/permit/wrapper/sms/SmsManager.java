package com.amuse.permit.wrapper.sms;

import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.telephony.SmsMessage;
import android.telephony.SubscriptionManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.amuse.permit.Instance;
import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.data.PacketData;
import com.amuse.permit.model.Annotations;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.process.ProcessConst;
import com.amuse.permit.process.action.ResultCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages SMS operations such as sending data, text, and pdu SMS messages.
 *
 * @see SubscriptionManager#getActiveSubscriptionInfoList()
 * @see android.telephony.SmsManager
 */
@SuppressWarnings("unused")
@Annotations.RequesterSide
public class SmsManager extends SmsTokenModel {

    private final Context context;

    private SmsManager(Context context, @Nullable Integer subId) {
        this.context = context;
        this.smsSubscriptionId = subId;
    }

    /**
     * Get default {@link SmsManager} instance,
     * equivalent constructor with {@link Context#getSystemService(String)} and <code>SmsManager.class</code>
     *
     * @param context Application context instance
     * @return the default {@link SmsManager} instance
     */
    public static SmsManager getDefaultSmsManager(Context context) {
        return new SmsManager(context, null);
    }

    /**
     * Get the instance of the SmsManager associated with a particular subscription ID.
     *
     * <p class="note"><strong>Note:</strong> Constructing an {@link android.telephony.SmsManager} in this manner will
     * never cause an SMS disambiguation dialog to appear, unlike {@link #getDefaultSmsManager(Context)}.
     * </p>
     *
     * @param context Application context instance
     * @param subId an SMS subscription ID, typically accessed using {@link SubscriptionManager}
     * @return the instance of the SmsManager associated with subscription
     *
     * @see android.telephony.SmsManager#createForSubscriptionId(int)
     */
    public static SmsManager createForSubscriptionId(Context context, int subId) {
        return new SmsManager(context, subId);
    }

    /**
     * Get the associated subscription id. If the instance was returned by {@link #getDefaultSmsManager(Context)} ()},
     * then this method may return different values at different points in time (if the user
     * changes the default subscription id).
     *
     * @return associated subscription ID or {@link SubscriptionManager#INVALID_SUBSCRIPTION_ID} if
     * the default subscription id cannot be determined or the device has multiple active
     * subscriptions and and no default is set ("ask every time") by the user.
     * @see android.telephony.SmsManager#getSubscriptionId()
     */
    @Nullable
    public Integer getSubscriptionId() {
        return smsSubscriptionId;
    }

    /**
     * Create a single use app specific incoming SMS request for the calling package.
     * This method returns a token that if included in a subsequent incoming SMS message will cause
     * {@code intent} to be sent with the SMS data.
     * </p>
     * The token is only good for one use, after an SMS has been received containing the token all
     * subsequent SMS messages with the token will be routed as normal.
     * An app can only have one request at a time, if the app already has a request pending it will
     * be replaced with a new request.
     *
     * @return Token to include in an SMS message. The token will be 11 characters long.
     * @see android.provider.Telephony.Sms.Intents#getMessagesFromIntent
     * @see android.telephony.SmsManager#createForSubscriptionId(int)
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ResultTask<String> createAppSpecificSmsToken(PendingIntent intent) {
        return new ResultCreator<String>(buildMethodCallPacketData("createAppSpecificSmsToken", String.class,
                new Class[]{PendingIntent.class}, intent)).postMethodProcess(context);
    }

    /**
     * Create a single use app specific incoming SMS request for the calling package.
     * This method returns a token that if included in a subsequent incoming SMS message, and the
     * SMS message has a prefix from the given prefixes list, the provided {@code intent} will be
     * sent with the SMS data to the calling package.
     * The token is only good for one use within a reasonable amount of time. After an SMS has been
     * received containing the token all subsequent SMS messages with the token will be routed as
     * normal. An app can only have one request at a time, if the app already has a request pending it will
     * be replaced with a new request.
     *
     * @param prefixes this is a list of prefixes string separated by REGEX_PREFIX_DELIMITER. The
     *  matching SMS message should have at least one of the prefixes in the beginning of the
     *  message.
     * @param intent this intent is sent when the matching SMS message is received.
     * @return Token to include in an SMS message.
     * @see android.telephony.SmsManager#createAppSpecificSmsTokenWithPackageInfo(String, PendingIntent)
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ResultTask<String> createAppSpecificSmsTokenWithPackageInfo(String prefixes, PendingIntent intent) {
        return new ResultCreator<String>(buildMethodCallPacketData("createAppSpecificSmsTokenWithPackageInfo", String.class,
                new Class[]{String.class, PendingIntent.class}, prefixes, intent)).postMethodProcess(context);
    }

    /**
     * Divide a message text into several fragments, none bigger than the maximum SMS message size.
     *
     * @param text the original message. Must not be null.
     * @return an <code>ArrayList</code> of strings that, in order, comprise the original message.
     * @see android.telephony.SmsManager#divideMessage(String)
     */
    public ResultTask<ArrayList<String>> divideMessage(@NonNull String text) {
        return new ResultCreator<ArrayList<String>>(buildMethodCallPacketData("divideMessage", ArrayList.class, text)).postMethodProcess(context);
    }

    /**
     * Download an MMS message from carrier by a given location URL
     *
     * <p class="note"><strong>Note:</strong> If {@link #getDefaultSmsManager(Context)} ()} is used to instantiate this
     * manager on a multi-SIM device, this operation may fail downloading the MMS message because no
     * suitable default subscription could be found. In this case, if {@code downloadedIntent} is
     * non-null, then the {@link PendingIntent} will be sent with an error code
     * {@code RESULT_NO_DEFAULT_SMS_APP}. See {@link #getDefaultSmsManager(Context)} ()} for more information on the
     * conditions where this operation may fail.
     * </p>
     *
     * @param locationUrl the location URL of the MMS message to be downloaded, usually obtained
     *  from the MMS WAP push notification
     * @param contentUri the content uri to which the downloaded pdu will be written
     * @param configOverrides the carrier-specific messaging configuration values to override for
     *  downloading the message.
     * @param downloadedIntent if not NULL this <code>PendingIntent</code> is
     *  broadcast when the message is downloaded, or the download is failed
     * The result code will be <code>Activity.RESULT_OK</code> for success
     * or one of these errors:<br>
     * <code>MMS_ERROR_UNSPECIFIED</code><br>
     * <code>MMS_ERROR_INVALID_APN</code><br>
     * <code>MMS_ERROR_UNABLE_CONNECT_MMS</code><br>
     * <code>MMS_ERROR_HTTP_FAILURE</code><br>
     * <code>MMS_ERROR_IO_ERROR</code><br>
     * <code>MMS_ERROR_RETRY</code><br>
     * <code>MMS_ERROR_CONFIGURATION_ERROR</code><br>
     * <code>MMS_ERROR_NO_DATA_NETWORK</code><br>
     * <code>MMS_ERROR_INVALID_SUBSCRIPTION_ID</code><br>
     * <code>MMS_ERROR_INACTIVE_SUBSCRIPTION</code><br>
     * <code>MMS_ERROR_DATA_DISABLED</code><br>
     * @see android.telephony.SmsManager#downloadMultimediaMessage(Context, String, Uri, Bundle, PendingIntent)
     */
    public ResultTask<Void> downloadMultimediaMessage(String locationUrl, Uri contentUri, Bundle configOverrides, PendingIntent downloadedIntent) {
        return new ResultCreator<Void>(buildMethodCallPacketData("downloadMultimediaMessage", Void.class,
                new Class[]{String.class, Uri.class, Bundle.class, PendingIntent.class},
                locationUrl, configOverrides, configOverrides, downloadedIntent)).postMethodProcess(context);
    }


    /**
     * Download an MMS message from carrier by a given location URL
     * </p>
     * Same as {@link #downloadMultimediaMessage(String, Uri, Bundle, PendingIntent)},
     *      but adds an optional messageId.
     * <p class="note"><strong>Note:</strong> If {@link #getDefaultSmsManager(Context)} ()} is used to instantiate this
     * manager on a multi-SIM device, this operation may fail downloading the MMS message because no
     * suitable default subscription could be found. In this case, if {@code downloadedIntent} is
     * non-null, then the {@link PendingIntent} will be sent with an error code
     * {@code RESULT_NO_DEFAULT_SMS_APP}. See {@link #getDefaultSmsManager(Context)} ()} for more information on the
     * conditions where this operation may fail.
     * </p>
     *
     * @param locationUrl the location URL of the MMS message to be downloaded, usually obtained
     *  from the MMS WAP push notification
     * @param contentUri the content uri to which the downloaded pdu will be written
     * @param configOverrides the carrier-specific messaging configuration values to override for
     *  downloading the message.
     * @param downloadedIntent if not NULL this <code>PendingIntent</code> is
     *  broadcast when the message is downloaded, or the download is failed
     * The result code will be <code>Activity.RESULT_OK</code> for success
     * or one of these errors:<br>
     * <code>MMS_ERROR_UNSPECIFIED</code><br>
     * <code>MMS_ERROR_INVALID_APN</code><br>
     * <code>MMS_ERROR_UNABLE_CONNECT_MMS</code><br>
     * <code>MMS_ERROR_HTTP_FAILURE</code><br>
     * <code>MMS_ERROR_IO_ERROR</code><br>
     * <code>MMS_ERROR_RETRY</code><br>
     * <code>MMS_ERROR_CONFIGURATION_ERROR</code><br>
     * <code>MMS_ERROR_NO_DATA_NETWORK</code><br>
     * <code>MMS_ERROR_INVALID_SUBSCRIPTION_ID</code><br>
     * <code>MMS_ERROR_INACTIVE_SUBSCRIPTION</code><br>
     * <code>MMS_ERROR_DATA_DISABLED</code><br>
     * @param messageId an id that uniquely identifies the message requested to be downloaded.
     * Used for logging and diagnostics purposes. The id may be 0.
     * @see android.telephony.SmsManager#downloadMultimediaMessage(Context, String, Uri, Bundle, PendingIntent, long)
     */
    @RequiresApi(api = Build.VERSION_CODES.S)
    public ResultTask<Void> downloadMultimediaMessage(String locationUrl, Uri contentUri, Bundle configOverrides, PendingIntent downloadedIntent, Long messageId) {
        return new ResultCreator<Void>(buildMethodCallPacketData("downloadMultimediaMessage", Void.class,
                new Class[]{String.class, Uri.class, Bundle.class, PendingIntent.class, Long.class},
                locationUrl, configOverrides, configOverrides, downloadedIntent, messageId)).postMethodProcess(context);
    }

    /**
     * Get carrier-dependent MMS configuration values.
     *
     * @return the bundle key/values pairs that contains MMS configuration values
     *  or an empty Bundle if they cannot be found.
     * @see android.telephony.SmsManager#getCarrierConfigValues()
     */
    public ResultTask<Bundle> getCarrierConfigValues() {
        return new ResultCreator<Bundle>(buildMethodCallPacketData("getCarrierConfigValues", Bundle.class)).postMethodProcess(context);
    }

    /**
     * Gets the total capacity of SMS storage on the SIM card.
     *
     * <p>
     * This is the number of 176 byte EF-SMS records which can be stored on the SIM card.
     * See 3GPP TS 31.102 - 4.2.25 - EF-SMS for more information.
     * </p>
     *
     * <p class="note"><strong>Note:</strong> This method will never trigger an SMS disambiguation
     * dialog. If this method is called on a device that has multiple active subscriptions, this
     * {@link SmsManager} instance has been created with {@link #getDefaultSmsManager(Context)} ()}, and no user-defined
     * default subscription is defined, the subscription ID associated with this method will be
     * INVALID, which will result in the operation being completed on the subscription associated
     * with logical slot 0. Use {@link #createForSubscriptionId(Context, int)} ()}} to ensure the operation
     * is performed on the correct subscription.
     * </p>
     *
     * @return the total number of SMS records which can be stored on the SIM card.
     * @see android.telephony.SmsManager#getSmsCapacityOnIcc()
     */
    @RequiresApi(api = Build.VERSION_CODES.S)
    public ResultTask<Integer> getSmsCapacityOnIcc() {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getSmsCapacityOnIcc", Integer.class)).postMethodProcess(context);
    }

    /**
     * Get default sms subscription id.
     *
     * <p class="note"><strong>Note:</strong>This returns a value different from
     * {@link SubscriptionManager#getDefaultSmsSubscriptionId} if the user has not chosen a default.
     * In this case it returns the active subscription id if there's only one active subscription
     * available.
     *
     * @return the user-defined default SMS subscription id, or the active subscription id if
     * there's only one active subscription available, otherwise
     * {@link SubscriptionManager#INVALID_SUBSCRIPTION_ID}.
     * @see android.telephony.SmsManager#getDefaultSmsSubscriptionId()
     */
    public ResultTask<Integer> getDefaultSmsSubscriptionId() {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getDefaultSmsSubscriptionId", Integer.class)).postMethodProcess(context);
    }

    /**
     * Gets the SMSC address from (U)SIM.
     *
     * <p class="note"><strong>Note:</strong> Using this method requires that your app is the
     * default SMS application, or READ_PRIVILEGED_PHONE_STATE permission, or has the carrier
     * privileges.</p>
     *
     * <p class="note"><strong>Note:</strong> This method will never trigger an SMS disambiguation
     * dialog. If this method is called on a device that has multiple active subscriptions, this
     * {@link SmsManager} instance has been created with {@link #getDefaultSmsManager(Context)} ()}, and no user-defined
     * default subscription is defined, the subscription ID associated with this method will be
     * INVALID, which will result in the operation being completed on the subscription associated
     * with logical slot 0. Use {@link #createForSubscriptionId(Context, int)} (int)} to ensure the operation
     * is performed on the correct subscription.
     * </p>
     *
     * @return the SMSC address string, null if failed.
     * @see android.telephony.SmsManager#getSmscAddress()
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public ResultTask<String> getSmscAddress() {
        return new ResultCreator<String>(buildMethodCallPacketData("getSmscAddress", String.class)).postMethodProcess(context);
    }

    /**
     *
     * Inject an SMS PDU into the android application framework.
     *
     * <p>Requires permission: {@link android.Manifest.permission#MODIFY_PHONE_STATE} or carrier
     * privileges per {@link com.amuse.permit.wrapper.telephony.TelephonyManager#hasCarrierPrivileges}.
     *
     * <p class="note"><strong>Note:</strong> This method is intended for internal use by carrier
     * applications or the Telephony framework and will never trigger an SMS disambiguation
     * dialog. If this method is called on a device that has multiple active subscriptions, this
     * {@link SmsManager} instance has been created with {@link #getDefaultSmsManager(Context)} ()}, and no user-defined
     * default subscription is defined, the subscription ID associated with this message will be
     * INVALID, which will result in the SMS being injected on the subscription associated with
     * logical slot 0. Use {@link #createForSubscriptionId(Context, int)} (int)} to ensure the SMS is
     * delivered to the correct subscription.
     * </p>
     *
     * @param pdu is the byte array of pdu to be injected into android application framework
     * @param format is the format of SMS pdu ({@link SmsMessage#FORMAT_3GPP} or
     *  {@link SmsMessage#FORMAT_3GPP2})
     * @param receivedIntent if not NULL this <code>PendingIntent</code> is
     *  broadcast when the message is successfully received by the
     *  android application framework, or failed. This intent is broadcast at
     *  the same time an SMS received from radio is acknowledged back.
     *  The result code will be {@link android.provider.Telephony.Sms.Intents#RESULT_SMS_HANDLED}
     *  for success, or {@link android.provider.Telephony.Sms.Intents#RESULT_SMS_GENERIC_ERROR} or
     *  {@link android.telephony.SmsManager#RESULT_REMOTE_EXCEPTION} for error.
     *
     * @see android.telephony.SmsManager#injectSmsPdu(byte[], String, PendingIntent)
     */
    public ResultTask<Void> injectSmsPdu(Byte[] pdu, String format, @Nullable PendingIntent receivedIntent) {
        return new ResultCreator<Void>(buildMethodCallPacketData("injectSmsPdu", Void.class,
                new Class[]{Byte.class, String.class, PendingIntent.class},
                pdu, format, receivedIntent)).postMethodProcess(context);
    }

    /**
     * Send a data based SMS to a specific application port.
     *
     * <p class="note"><strong>Note:</strong> Using this method requires that your app has the
     * {@link android.Manifest.permission#SEND_SMS} permission.</p>
     *
     * <p class="note"><strong>Note:</strong> If {@link #getDefaultSmsManager(Context)} ()} is used to instantiate this
     * manager on a multi-SIM device, this operation may fail sending the SMS message because no
     * suitable default subscription could be found. In this case, if {@code sentIntent} is
     * non-null, then the {@link PendingIntent} will be sent with an error code
     * {@code RESULT_ERROR_GENERIC_FAILURE} and an extra string {@code "noDefault"} containing the
     * boolean value {@code true}. See {@link #getDefaultSmsManager(Context)} ()} for more information on the conditions
     * where this operation may fail.
     * </p>
     *
     * @param destinationAddress the address to send the message to
     * @param scAddress is the service center address or null to use
     *  the current default SMSC
     * @param destinationPort the port to deliver the message to
     * @param data the body of the message to send
     * @param sentIntent if not NULL this <code>PendingIntent</code> is
     *  broadcast when the message is successfully sent, or failed.
     *  The result code will be <code>Activity.RESULT_OK</code> for success,
     *  or one of these errors:<br>
     *  <code>RESULT_ERROR_GENERIC_FAILURE</code><br>
     *  <code>RESULT_ERROR_RADIO_OFF</code><br>
     *  <code>RESULT_ERROR_NULL_PDU</code><br>
     *  <code>RESULT_ERROR_NO_SERVICE</code><br>
     *  <code>RESULT_ERROR_LIMIT_EXCEEDED</code><br>
     *  <code>RESULT_ERROR_FDN_CHECK_FAILURE</code><br>
     *  <code>RESULT_ERROR_SHORT_CODE_NOT_ALLOWED</code><br>
     *  <code>RESULT_ERROR_SHORT_CODE_NEVER_ALLOWED</code><br>
     *  <code>RESULT_RADIO_NOT_AVAILABLE</code><br>
     *  <code>RESULT_NETWORK_REJECT</code><br>
     *  <code>RESULT_INVALID_ARGUMENTS</code><br>
     *  <code>RESULT_INVALID_STATE</code><br>
     *  <code>RESULT_NO_MEMORY</code><br>
     *  <code>RESULT_INVALID_SMS_FORMAT</code><br>
     *  <code>RESULT_SYSTEM_ERROR</code><br>
     *  <code>RESULT_MODEM_ERROR</code><br>
     *  <code>RESULT_NETWORK_ERROR</code><br>
     *  <code>RESULT_ENCODING_ERROR</code><br>
     *  <code>RESULT_INVALID_SMSC_ADDRESS</code><br>
     *  <code>RESULT_OPERATION_NOT_ALLOWED</code><br>
     *  <code>RESULT_INTERNAL_ERROR</code><br>
     *  <code>RESULT_NO_RESOURCES</code><br>
     *  <code>RESULT_CANCELLED</code><br>
     *  <code>RESULT_REQUEST_NOT_SUPPORTED</code><br>
     *  <code>RESULT_NO_BLUETOOTH_SERVICE</code><br>
     *  <code>RESULT_INVALID_BLUETOOTH_ADDRESS</code><br>
     *  <code>RESULT_BLUETOOTH_DISCONNECTED</code><br>
     *  <code>RESULT_UNEXPECTED_EVENT_STOP_SENDING</code><br>
     *  <code>RESULT_SMS_BLOCKED_DURING_EMERGENCY</code><br>
     *  <code>RESULT_SMS_SEND_RETRY_FAILED</code><br>
     *  <code>RESULT_REMOTE_EXCEPTION</code><br>
     *  <code>RESULT_NO_DEFAULT_SMS_APP</code><br>
     *  <code>RESULT_RIL_RADIO_NOT_AVAILABLE</code><br>
     *  <code>RESULT_RIL_SMS_SEND_FAIL_RETRY</code><br>
     *  <code>RESULT_RIL_NETWORK_REJECT</code><br>
     *  <code>RESULT_RIL_INVALID_STATE</code><br>
     *  <code>RESULT_RIL_INVALID_ARGUMENTS</code><br>
     *  <code>RESULT_RIL_NO_MEMORY</code><br>
     *  <code>RESULT_RIL_REQUEST_RATE_LIMITED</code><br>
     *  <code>RESULT_RIL_INVALID_SMS_FORMAT</code><br>
     *  <code>RESULT_RIL_SYSTEM_ERR</code><br>
     *  <code>RESULT_RIL_ENCODING_ERR</code><br>
     *  <code>RESULT_RIL_INVALID_SMSC_ADDRESS</code><br>
     *  <code>RESULT_RIL_MODEM_ERR</code><br>
     *  <code>RESULT_RIL_NETWORK_ERR</code><br>
     *  <code>RESULT_RIL_INTERNAL_ERR</code><br>
     *  <code>RESULT_RIL_REQUEST_NOT_SUPPORTED</code><br>
     *  <code>RESULT_RIL_INVALID_MODEM_STATE</code><br>
     *  <code>RESULT_RIL_NETWORK_NOT_READY</code><br>
     *  <code>RESULT_RIL_OPERATION_NOT_ALLOWED</code><br>
     *  <code>RESULT_RIL_NO_RESOURCES</code><br>
     *  <code>RESULT_RIL_CANCELLED</code><br>
     *  <code>RESULT_RIL_SIM_ABSENT</code><br>
     *  <code>RESULT_RIL_SIMULTANEOUS_SMS_AND_CALL_NOT_ALLOWED</code><br>
     *  <code>RESULT_RIL_ACCESS_BARRED</code><br>
     *  <code>RESULT_RIL_BLOCKED_DUE_TO_CALL</code><br>
     *  For <code>RESULT_ERROR_GENERIC_FAILURE</code> or any of the RESULT_RIL errors,
     *  the sentIntent may include the extra "errorCode" containing a radio technology specific
     *  value, generally only useful for troubleshooting.<br>
     * @param deliveryIntent if not NULL this <code>PendingIntent</code> is
     *  broadcast when the message is delivered to the recipient.  The
     *  raw pdu of the status report is in the extended data ("pdu").
     * @see android.telephony.SmsManager#sendDataMessage(String, String, short, byte[], PendingIntent, PendingIntent)
     */
    public ResultTask<Void> sendDataMessage(String destinationAddress, String scAddress, Short destinationPort, Byte[] data, PendingIntent sentIntent, PendingIntent deliveryIntent) {
        return new ResultCreator<Void>(buildMethodCallPacketData("sendDataMessage", Void.class,
                new Class<?>[]{String.class, String.class, String.class, Byte[].class, PendingIntent.class, PendingIntent.class},
                destinationAddress, scAddress, destinationPort, data, sentIntent, deliveryIntent)).postMethodProcess(context);
    }

    /**
     * Send an MMS message
     *
     * <p class="note"><strong>Note:</strong> If {@link #getDefaultSmsManager(Context)} ()} is used to instantiate this
     * manager on a multi-SIM device, this operation may fail sending the MMS message because no
     * suitable default subscription could be found. In this case, if {@code sentIntent} is
     * non-null, then the {@link PendingIntent} will be sent with an error code
     * {@code RESULT_NO_DEFAULT_SMS_APP}. See {@link #getDefaultSmsManager(Context)} ()} for more information on the
     * conditions where this operation may fail.
     * </p>
     *
     * @param contentUri the content Uri from which the message pdu will be read
     * @param locationUrl the optional location url where message should be sent to
     * @param configOverrides the carrier-specific messaging configuration values to override for
     *  sending the message.
     * @param sentIntent if not NULL this <code>PendingIntent</code> is
     *  broadcast when the message is successfully sent, or failed
     * The result code will be <code>Activity.RESULT_OK</code> for success
     * or one of these errors:<br>
     * <code>MMS_ERROR_UNSPECIFIED</code><br>
     * <code>MMS_ERROR_INVALID_APN</code><br>
     * <code>MMS_ERROR_UNABLE_CONNECT_MMS</code><br>
     * <code>MMS_ERROR_HTTP_FAILURE</code><br>
     * <code>MMS_ERROR_IO_ERROR</code><br>
     * <code>MMS_ERROR_RETRY</code><br>
     * <code>MMS_ERROR_CONFIGURATION_ERROR</code><br>
     * <code>MMS_ERROR_NO_DATA_NETWORK</code><br>
     * <code>MMS_ERROR_INVALID_SUBSCRIPTION_ID</code><br>
     * <code>MMS_ERROR_INACTIVE_SUBSCRIPTION</code><br>
     * <code>MMS_ERROR_DATA_DISABLED</code><br>
     * @see android.telephony.SmsManager#sendMultimediaMessage(Context, Uri, String, Bundle, PendingIntent)
     */
    public ResultTask<Void> sendMultimediaMessage(Uri contentUri, String locationUrl, Bundle configOverrides, PendingIntent sentIntent) {
        return new ResultCreator<Void>(buildMethodCallPacketData("sendMultimediaMessage", Void.class,
                new Class<?>[]{Uri.class, String.class, Bundle.class, PendingIntent.class},
                contentUri, locationUrl, configOverrides, sentIntent)).postMethodProcess(context);
    }

    /**
     * Send an MMS message
     * </p>
     * Same as {@link #sendMultimediaMessage(Uri, String, Bundle, PendingIntent)}, but adds an optional messageId.
     * <p class="note"><strong>Note:</strong> If {@link #getDefaultSmsManager(Context)} ()} is used to instantiate this
     * manager on a multi-SIM device, this operation may fail sending the MMS message because no
     * suitable default subscription could be found. In this case, if {@code sentIntent} is
     * non-null, then the {@link PendingIntent} will be sent with an error code
     * {@code RESULT_NO_DEFAULT_SMS_APP}. See {@link #getDefaultSmsManager(Context)} ()} for more information on the
     * conditions where this operation may fail.
     * </p>
     *
     * @param contentUri the content Uri from which the message pdu will be read
     * @param locationUrl the optional location url where message should be sent to
     * @param configOverrides the carrier-specific messaging configuration values to override for
     *  sending the message.
     * @param sentIntent if not NULL this <code>PendingIntent</code> is
     *  broadcast when the message is successfully sent, or failed
     * The result code will be <code>Activity.RESULT_OK</code> for success
     * or one of these errors:<br>
     * <code>MMS_ERROR_UNSPECIFIED</code><br>
     * <code>MMS_ERROR_INVALID_APN</code><br>
     * <code>MMS_ERROR_UNABLE_CONNECT_MMS</code><br>
     * <code>MMS_ERROR_HTTP_FAILURE</code><br>
     * <code>MMS_ERROR_IO_ERROR</code><br>
     * <code>MMS_ERROR_RETRY</code><br>
     * <code>MMS_ERROR_CONFIGURATION_ERROR</code><br>
     * <code>MMS_ERROR_NO_DATA_NETWORK</code><br>
     * <code>MMS_ERROR_INVALID_SUBSCRIPTION_ID</code><br>
     * <code>MMS_ERROR_INACTIVE_SUBSCRIPTION</code><br>
     * <code>MMS_ERROR_DATA_DISABLED</code><br>
     * @param messageId an id that uniquely identifies the message requested to be sent.
     * Used for logging and diagnostics purposes. The id may be 0.
     * @see android.telephony.SmsManager#sendMultimediaMessage(Context, Uri, String, Bundle, PendingIntent, long)
     */
    @RequiresApi(api = Build.VERSION_CODES.S)
    public ResultTask<Void> sendMultimediaMessage(Uri contentUri, String locationUrl, Bundle configOverrides, PendingIntent sentIntent, Long messageId) {
        return new ResultCreator<Void>(buildMethodCallPacketData("sendMultimediaMessage", Void.class,
                new Class<?>[]{Uri.class, String.class, Bundle.class, PendingIntent.class, Long.class},
                contentUri, locationUrl, configOverrides, sentIntent, messageId)).postMethodProcess(context);
    }

    /**
     * Send a multi-part text based SMS. Same as {@link #sendMultipartTextMessage(String, String,ArrayList, ArrayList, ArrayList)},
     * but adds an optional messageId.
     *
     * @param messageId An id that uniquely identifies the message requested to be sent.
     * Used for logging and diagnostics purposes. The id may be 0.
     * @see android.telephony.SmsManager#sendMultipartTextMessage(String, String, List, List, List, long)
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public ResultTask<Void> sendMultipartTextMessage(String destinationAddress, String scAddress, ArrayList<String> parts, ArrayList<PendingIntent> sentIntents, ArrayList<PendingIntent> deliveryIntents, Long messageId) {
        return new ResultCreator<Void>(buildMethodCallPacketData("sendMultipartTextMessage", Void.class,
                new Class<?>[]{String.class, String.class, ArrayList.class, ArrayList.class, ArrayList.class, Long.class},
                destinationAddress, scAddress, parts, sentIntents, deliveryIntents, messageId)).postMethodProcess(context);
    }

    /**
     * Send a multi-part text based SMS.  The callee should have already
     * divided the message into correctly sized parts by calling
     * <code>divideMessage</code>.
     *
     * <p class="note"><strong>Note:</strong> Beginning with Android 4.4 (API level 19), if
     * <em>and only if</em> an app is not selected as the default SMS app, the system automatically
     * writes messages sent using this method to the SMS Provider (the default SMS app is always
     * responsible for writing its sent messages to the SMS Provider). For information about
     * how to behave as the default SMS app, see {@link android.provider.Telephony}.</p>
     *
     * <p class="note"><strong>Note:</strong> If {@link #getDefaultSmsManager(Context)} ()} is used to instantiate this
     * manager on a multi-SIM device, this operation may fail sending the SMS message because no
     * suitable default subscription could be found. In this case, if {@code sentIntent} is
     * non-null, then the {@link PendingIntent} will be sent with an error code
     * {@code RESULT_ERROR_GENERIC_FAILURE} and an extra string {@code "noDefault"} containing the
     * boolean value {@code true}. See {@link #getDefaultSmsManager(Context)} ()} for more information on the conditions
     * where this operation may fail.
     * </p>
     *
     * @param destinationAddress the address to send the message to
     * @param scAddress is the service center address or null to use
     *  the current default SMSC
     * @param parts an <code>ArrayList</code> of strings that, in order,
     *  comprise the original message
     * @param sentIntents if not null, an <code>ArrayList</code> of
     *  <code>PendingIntent</code>s (one for each message part) that is
     *  broadcast when the corresponding message part has been sent.
     *  The result code will be <code>Activity.RESULT_OK</code> for success,
     *  or one of these errors:<br>
     *  <code>RESULT_ERROR_GENERIC_FAILURE</code><br>
     *  <code>RESULT_ERROR_RADIO_OFF</code><br>
     *  <code>RESULT_ERROR_NULL_PDU</code><br>
     *  <code>RESULT_ERROR_NO_SERVICE</code><br>
     *  <code>RESULT_ERROR_LIMIT_EXCEEDED</code><br>
     *  <code>RESULT_ERROR_FDN_CHECK_FAILURE</code><br>
     *  <code>RESULT_ERROR_SHORT_CODE_NOT_ALLOWED</code><br>
     *  <code>RESULT_ERROR_SHORT_CODE_NEVER_ALLOWED</code><br>
     *  <code>RESULT_RADIO_NOT_AVAILABLE</code><br>
     *  <code>RESULT_NETWORK_REJECT</code><br>
     *  <code>RESULT_INVALID_ARGUMENTS</code><br>
     *  <code>RESULT_INVALID_STATE</code><br>
     *  <code>RESULT_NO_MEMORY</code><br>
     *  <code>RESULT_INVALID_SMS_FORMAT</code><br>
     *  <code>RESULT_SYSTEM_ERROR</code><br>
     *  <code>RESULT_MODEM_ERROR</code><br>
     *  <code>RESULT_NETWORK_ERROR</code><br>
     *  <code>RESULT_ENCODING_ERROR</code><br>
     *  <code>RESULT_INVALID_SMSC_ADDRESS</code><br>
     *  <code>RESULT_OPERATION_NOT_ALLOWED</code><br>
     *  <code>RESULT_INTERNAL_ERROR</code><br>
     *  <code>RESULT_NO_RESOURCES</code><br>
     *  <code>RESULT_CANCELLED</code><br>
     *  <code>RESULT_REQUEST_NOT_SUPPORTED</code><br>
     *  <code>RESULT_NO_BLUETOOTH_SERVICE</code><br>
     *  <code>RESULT_INVALID_BLUETOOTH_ADDRESS</code><br>
     *  <code>RESULT_BLUETOOTH_DISCONNECTED</code><br>
     *  <code>RESULT_UNEXPECTED_EVENT_STOP_SENDING</code><br>
     *  <code>RESULT_SMS_BLOCKED_DURING_EMERGENCY</code><br>
     *  <code>RESULT_SMS_SEND_RETRY_FAILED</code><br>
     *  <code>RESULT_REMOTE_EXCEPTION</code><br>
     *  <code>RESULT_NO_DEFAULT_SMS_APP</code><br>
     *  <code>RESULT_RIL_RADIO_NOT_AVAILABLE</code><br>
     *  <code>RESULT_RIL_SMS_SEND_FAIL_RETRY</code><br>
     *  <code>RESULT_RIL_NETWORK_REJECT</code><br>
     *  <code>RESULT_RIL_INVALID_STATE</code><br>
     *  <code>RESULT_RIL_INVALID_ARGUMENTS</code><br>
     *  <code>RESULT_RIL_NO_MEMORY</code><br>
     *  <code>RESULT_RIL_REQUEST_RATE_LIMITED</code><br>
     *  <code>RESULT_RIL_INVALID_SMS_FORMAT</code><br>
     *  <code>RESULT_RIL_SYSTEM_ERR</code><br>
     *  <code>RESULT_RIL_ENCODING_ERR</code><br>
     *  <code>RESULT_RIL_INVALID_SMSC_ADDRESS</code><br>
     *  <code>RESULT_RIL_MODEM_ERR</code><br>
     *  <code>RESULT_RIL_NETWORK_ERR</code><br>
     *  <code>RESULT_RIL_INTERNAL_ERR</code><br>
     *  <code>RESULT_RIL_REQUEST_NOT_SUPPORTED</code><br>
     *  <code>RESULT_RIL_INVALID_MODEM_STATE</code><br>
     *  <code>RESULT_RIL_NETWORK_NOT_READY</code><br>
     *  <code>RESULT_RIL_OPERATION_NOT_ALLOWED</code><br>
     *  <code>RESULT_RIL_NO_RESOURCES</code><br>
     *  <code>RESULT_RIL_CANCELLED</code><br>
     *  <code>RESULT_RIL_SIM_ABSENT</code><br>
     *  <code>RESULT_RIL_SIMULTANEOUS_SMS_AND_CALL_NOT_ALLOWED</code><br>
     *  <code>RESULT_RIL_ACCESS_BARRED</code><br>
     *  <code>RESULT_RIL_BLOCKED_DUE_TO_CALL</code><br>
     *  For <code>RESULT_ERROR_GENERIC_FAILURE</code> or any of the RESULT_RIL errors,
     *  the sentIntent may include the extra "errorCode" containing a radio technology specific
     *  value, generally only useful for troubleshooting.<br>
     * @param deliveryIntents if not null, an <code>ArrayList</code> of
     *  <code>PendingIntent</code>s (one for each message part) that is
     *  broadcast when the corresponding message part has been delivered
     *  to the recipient.  The raw pdu of the status report is in the
     *  extended data ("pdu").
     *
     * @see android.telephony.SmsManager#sendMultipartTextMessage(String, String, ArrayList, ArrayList, ArrayList)
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public ResultTask<Void> sendMultipartTextMessage(String destinationAddress, String scAddress, ArrayList<String> parts, ArrayList<PendingIntent> sentIntents, ArrayList<PendingIntent> deliveryIntents) {
        return new ResultCreator<Void>(buildMethodCallPacketData("sendMultipartTextMessage", Void.class,
                new Class<?>[]{String.class, String.class, ArrayList.class, ArrayList.class, ArrayList.class},
                destinationAddress, scAddress, parts, sentIntents, deliveryIntents)).postMethodProcess(context);
    }

    /**
     * Similar method as {@link #sendMultipartTextMessage(String, String, ArrayList, ArrayList, ArrayList)}
     * With an additional argument.
     *
     * <p class="note"><strong>Note:</strong> This method is intended for internal use the Telephony
     * framework and will never trigger an SMS disambiguation dialog. If this method is called on a
     * device that has multiple active subscriptions, this {@link SmsManager} instance has been
     * created with {@link #getDefaultSmsManager(Context)} ()}, and no user-defined default subscription is defined, the
     * subscription ID associated with this message will be INVALID, which will result in the SMS
     * being sent on the subscription associated with logical slot 0. Use
     * {@link #createForSubscriptionId(Context, int)} (int)} to ensure the SMS is sent on the correct
     * subscription.
     * </p>
     *
     * @param packageName serves as the default package name if the package name that is
     *        associated with the user id is null.
     * @see android.telephony.SmsManager#sendMultipartTextMessage(String, String, List, List, List, String, String)
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public ResultTask<Void> sendMultipartTextMessage(String destinationAddress, String scAddress, ArrayList<String> parts, ArrayList<PendingIntent> sentIntents, ArrayList<PendingIntent> deliveryIntents, String packageName, String attributionTag) {
        return new ResultCreator<Void>(buildMethodCallPacketData("sendMultipartTextMessage", Void.class,
                new Class<?>[]{String.class, String.class, ArrayList.class, ArrayList.class, ArrayList.class, String.class, String.class},
                destinationAddress, scAddress, parts, sentIntents, deliveryIntents, packageName, attributionTag)).postMethodProcess(context);
    }

    /**
     * Send a text based SMS. Same as {@link #sendTextMessage( String destinationAddress,
     * String scAddress, String text, PendingIntent sentIntent, PendingIntent deliveryIntent)}, but
     * adds an optional messageId.
     * @param messageId An id that uniquely identifies the message requested to be sent.
     * Used for logging and diagnostics purposes. The id may be 0.
     *
     * @see android.telephony.SmsManager#sendTextMessage(String, String, String, PendingIntent, PendingIntent, long)
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public ResultTask<Void> sendTextMessage(String destinationAddress, String scAddress, String text, PendingIntent sentIntent, PendingIntent deliveryIntent, Long messageId) {
        return new ResultCreator<Void>(buildMethodCallPacketData("sendTextMessage", Void.class,
                new Class<?>[]{String.class, String.class, String.class, PendingIntent.class, PendingIntent.class, Long.class},
                destinationAddress, scAddress, text, sentIntent, deliveryIntent, messageId)).postMethodProcess(context);
    }

    /**
     * Send a text based SMS.
     *
     * <p class="note"><strong>Note:</strong> Beginning with Android 4.4 (API level 19), if
     * <em>and only if</em> an app is not selected as the default SMS app, the system automatically
     * writes messages sent using this method to the SMS Provider (the default SMS app is always
     * responsible for writing its sent messages to the SMS Provider). For information about
     * how to behave as the default SMS app, see {@link android.provider.Telephony}.</p>
     *
     * <p class="note"><strong>Note:</strong> If {@link #getDefaultSmsManager(Context)} ()} is used to instantiate this
     * manager on a multi-SIM device, this operation may fail sending the SMS message because no
     * suitable default subscription could be found. In this case, if {@code sentIntent} is
     * non-null, then the {@link PendingIntent} will be sent with an error code
     * {@code RESULT_ERROR_GENERIC_FAILURE} and an extra string {@code "noDefault"} containing the
     * boolean value {@code true}. See {@link #getDefaultSmsManager(Context)} ()} for more information on the conditions
     * where this operation may fail.
     * </p>
     *
     * @param destinationAddress the address to send the message to
     * @param scAddress is the service center address or null to use
     *  the current default SMSC
     * @param text the body of the message to send
     * @param sentIntent if not NULL this <code>PendingIntent</code> is
     *  broadcast when the message is successfully sent, or failed.
     *  The result code will be <code>Activity.RESULT_OK</code> for success,
     *  or one of these errors:<br>
     *  <code>RESULT_ERROR_GENERIC_FAILURE</code><br>
     *  <code>RESULT_ERROR_RADIO_OFF</code><br>
     *  <code>RESULT_ERROR_NULL_PDU</code><br>
     *  <code>RESULT_ERROR_NO_SERVICE</code><br>
     *  <code>RESULT_ERROR_LIMIT_EXCEEDED</code><br>
     *  <code>RESULT_ERROR_FDN_CHECK_FAILURE</code><br>
     *  <code>RESULT_ERROR_SHORT_CODE_NOT_ALLOWED</code><br>
     *  <code>RESULT_ERROR_SHORT_CODE_NEVER_ALLOWED</code><br>
     *  <code>RESULT_RADIO_NOT_AVAILABLE</code><br>
     *  <code>RESULT_NETWORK_REJECT</code><br>
     *  <code>RESULT_INVALID_ARGUMENTS</code><br>
     *  <code>RESULT_INVALID_STATE</code><br>
     *  <code>RESULT_NO_MEMORY</code><br>
     *  <code>RESULT_INVALID_SMS_FORMAT</code><br>
     *  <code>RESULT_SYSTEM_ERROR</code><br>
     *  <code>RESULT_MODEM_ERROR</code><br>
     *  <code>RESULT_NETWORK_ERROR</code><br>
     *  <code>RESULT_ENCODING_ERROR</code><br>
     *  <code>RESULT_INVALID_SMSC_ADDRESS</code><br>
     *  <code>RESULT_OPERATION_NOT_ALLOWED</code><br>
     *  <code>RESULT_INTERNAL_ERROR</code><br>
     *  <code>RESULT_NO_RESOURCES</code><br>
     *  <code>RESULT_CANCELLED</code><br>
     *  <code>RESULT_REQUEST_NOT_SUPPORTED</code><br>
     *  <code>RESULT_NO_BLUETOOTH_SERVICE</code><br>
     *  <code>RESULT_INVALID_BLUETOOTH_ADDRESS</code><br>
     *  <code>RESULT_BLUETOOTH_DISCONNECTED</code><br>
     *  <code>RESULT_UNEXPECTED_EVENT_STOP_SENDING</code><br>
     *  <code>RESULT_SMS_BLOCKED_DURING_EMERGENCY</code><br>
     *  <code>RESULT_SMS_SEND_RETRY_FAILED</code><br>
     *  <code>RESULT_REMOTE_EXCEPTION</code><br>
     *  <code>RESULT_NO_DEFAULT_SMS_APP</code><br>
     *  <code>RESULT_RIL_RADIO_NOT_AVAILABLE</code><br>
     *  <code>RESULT_RIL_SMS_SEND_FAIL_RETRY</code><br>
     *  <code>RESULT_RIL_NETWORK_REJECT</code><br>
     *  <code>RESULT_RIL_INVALID_STATE</code><br>
     *  <code>RESULT_RIL_INVALID_ARGUMENTS</code><br>
     *  <code>RESULT_RIL_NO_MEMORY</code><br>
     *  <code>RESULT_RIL_REQUEST_RATE_LIMITED</code><br>
     *  <code>RESULT_RIL_INVALID_SMS_FORMAT</code><br>
     *  <code>RESULT_RIL_SYSTEM_ERR</code><br>
     *  <code>RESULT_RIL_ENCODING_ERR</code><br>
     *  <code>RESULT_RIL_INVALID_SMSC_ADDRESS</code><br>
     *  <code>RESULT_RIL_MODEM_ERR</code><br>
     *  <code>RESULT_RIL_NETWORK_ERR</code><br>
     *  <code>RESULT_RIL_INTERNAL_ERR</code><br>
     *  <code>RESULT_RIL_REQUEST_NOT_SUPPORTED</code><br>
     *  <code>RESULT_RIL_INVALID_MODEM_STATE</code><br>
     *  <code>RESULT_RIL_NETWORK_NOT_READY</code><br>
     *  <code>RESULT_RIL_OPERATION_NOT_ALLOWED</code><br>
     *  <code>RESULT_RIL_NO_RESOURCES</code><br>
     *  <code>RESULT_RIL_CANCELLED</code><br>
     *  <code>RESULT_RIL_SIM_ABSENT</code><br>
     *  <code>RESULT_RIL_SIMULTANEOUS_SMS_AND_CALL_NOT_ALLOWED</code><br>
     *  <code>RESULT_RIL_ACCESS_BARRED</code><br>
     *  <code>RESULT_RIL_BLOCKED_DUE_TO_CALL</code><br>
     *  For <code>RESULT_ERROR_GENERIC_FAILURE</code> or any of the RESULT_RIL errors,
     *  the sentIntent may include the extra "errorCode" containing a radio technology specific
     *  value, generally only useful for troubleshooting.<br>
     * @param deliveryIntent if not NULL this <code>PendingIntent</code> is
     *  broadcast when the message is delivered to the recipient.  The
     *  raw pdu of the status report is in the extended data ("pdu").
     *
     * @see android.telephony.SmsManager#sendTextMessage(String, String, String, PendingIntent, PendingIntent)
     */
    public ResultTask<Void> sendTextMessage(String destinationAddress, String scAddress, String text, PendingIntent sentIntent, PendingIntent deliveryIntent) {
        return new ResultCreator<Void>(buildMethodCallPacketData("sendTextMessage", Void.class,
                new Class<?>[]{String.class, String.class, String.class, PendingIntent.class, PendingIntent.class},
                destinationAddress, scAddress, text, sentIntent, deliveryIntent)).postMethodProcess(context);
    }

    /**
     * Sets the SMSC address on (U)SIM.
     *
     * <p class="note"><strong>Note:</strong> Using this method requires that your app is the
     * default SMS application, or has {@link android.Manifest.permission#MODIFY_PHONE_STATE}
     * permission, or has the carrier privileges.</p>
     *
     * @param smsc the SMSC address string.
     * @return true for success, false otherwise. Failure can be due modem returning an error.
     * @see android.telephony.SmsManager#setSmscAddress(String)
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public ResultTask<Boolean> setSmscAddress(@NonNull String smsc) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("setSmscAddress", Boolean.class, smsc)).postMethodProcess(context);
    }

    private PacketData buildMethodCallPacketData(String methodName, Class<?> parameterCls, Object... args) {
        return buildMethodCallPacketData(methodName, parameterCls, null, args);
    }

    private PacketData buildMethodCallPacketData(String methodName, Class<?> parameterCls, Class<?>[] classHint, Object... args) {
        Instance instance = Instance.getInstance();
        PacketData packet = new PacketData();
        String ticket = String.format("%s@%s", methodName, System.currentTimeMillis());

        ArgsInfo argsInfo = new ArgsInfo();
        argsInfo.put(smsSubscriptionId);
        argsInfo.put(parameterCls, methodName);

        int argCount = 0;
        for (Object arg : args) {
            if (arg == null) {
                argsInfo.put(classHint[argCount], null);
            } else if (arg instanceof ArrayList) {
                ArrayList<?> argsList = ((ArrayList<?>) arg);
                if (!argsList.isEmpty() && argsList.get(0) instanceof Parcelable) {
                    argsInfo.put(arg.getClass(), ProcessConst.KEY_PARCEL_REPLACED);
                    packet.parcelableList.add((Parcelable) arg);
                } else {
                    argsInfo.put(arg);
                    if (argsList.isEmpty()) {
                        Instance.printLog("Invalid Arguments: ArrayList is empty, therefore cannot determine list's generic type is Parcelable or not");
                    }
                }
            } else if (arg instanceof Parcelable) {
                argsInfo.put(arg.getClass(), ProcessConst.KEY_PARCEL_REPLACED);
                packet.parcelableList.add((Parcelable) arg);
            } else {
                argsInfo.put(arg);
            }
            argCount += 1;
        }

        packet.argsInfo = argsInfo;
        packet.fromPackageName = instance.getServerPeer().getPackageName();
        packet.ticketId = ticket;
        packet.apiType = ProcessConst.ACTION_TYPE_SMS;
        packet.actionType = ProcessConst.ACTION_REQUEST_METHOD;

        return packet;
    }
}
