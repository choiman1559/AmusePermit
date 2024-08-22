package com.amuse.permit.wrapper.telephony;

import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.telecom.PhoneAccountHandle;
import android.telephony.CellInfo;
import android.telephony.IccOpenLogicalChannelResponse;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SignalStrengthUpdateRequest;
import android.telephony.UiccCardInfo;
import android.telephony.VisualVoicemailSmsFilterSettings;

import androidx.annotation.RequiresApi;

import com.amuse.permit.Instance;
import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.data.PacketData;
import com.amuse.permit.model.Annotations;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.model.Wrappable;
import com.amuse.permit.process.ProcessConst;
import com.amuse.permit.process.action.ResultCreator;

import java.util.ArrayList;

@SuppressWarnings("unused")
@Annotations.RequesterSide
public class TelephonyManager extends Wrappable {

    private final Context context;
    private Integer subId;
    private PhoneAccountHandle phoneAccountHandle;

    private TelephonyManager(Context context) {
        this.context = context;
    }

    @Annotations.Constructor
    public static TelephonyManager getDefaultTelephonyManager(Context context) {
        return new TelephonyManager(context);
    }

    @Annotations.Constructor
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static TelephonyManager createForSubscriptionId(Context context, Integer subId) {
        TelephonyManager manager = new TelephonyManager(context);
        manager.subId = subId;
        return manager;
    }

    @Annotations.Constructor
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static TelephonyManager createForPhoneAccountHandle(Context context, PhoneAccountHandle phoneAccountHandle) {
        TelephonyManager manager = new TelephonyManager(context);
        manager.phoneAccountHandle = phoneAccountHandle;
        return manager;
    }

    public ResultTask<Boolean> canChangeDtmfToneLength() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("canChangeDtmfToneLength"
                , Boolean.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public ResultTask<Void> clearSignalStrengthUpdateRequest(SignalStrengthUpdateRequest request) {
        return new ResultCreator<Void>(buildMethodCallPacketData("clearSignalStrengthUpdateRequest"
                , Void.class,
                new Class[]{SignalStrengthUpdateRequest.class},
                request)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ResultTask<Boolean> doesSwitchMultiSimConfigTriggerReboot() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("doesSwitchMultiSimConfigTriggerReboot"
                , Boolean.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public ResultTask<Integer> getActiveModemCount() {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getActiveModemCount"
                , Integer.class)).postMethodProcess(context);
    }

    public ResultTask<ArrayList<CellInfo>> getAllCellInfo() {
        return new ResultCreator<ArrayList<CellInfo>>(buildMethodCallPacketData("getAllCellInfo"
                , ArrayList.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public ResultTask<Long> getAllowedNetworkTypesForReason(Integer reason) {
        return new ResultCreator<Long>(buildMethodCallPacketData("getAllowedNetworkTypesForReason"
                , Long.class,
                new Class[]{Integer.class},
                reason)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public ResultTask<Integer> getCallComposerStatus() {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getCallComposerStatus"
                , Integer.class)).postMethodProcess(context);
    }

    public ResultTask<Integer> getCallState() {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getCallState"
                , Integer.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public ResultTask<Integer> getCallStateForSubscription() {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getCallStateForSubscription"
                , Integer.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ResultTask<Integer> getCardIdForDefaultEuicc() {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getCardIdForDefaultEuicc"
                , Integer.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ResultTask<PersistableBundle> getCarrierConfig() {
        return new ResultCreator<PersistableBundle>(buildMethodCallPacketData("getCarrierConfig"
                , PersistableBundle.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ResultTask<Integer> getCarrierIdFromSimMccMnc() {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getCarrierIdFromSimMccMnc"
                , Integer.class)).postMethodProcess(context);
    }

    public ResultTask<Integer> getDataActivity() {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getDataActivity"
                , Integer.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ResultTask<Integer> getDataNetworkType() {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getDataNetworkType"
                , Integer.class)).postMethodProcess(context);
    }

    public ResultTask<Integer> getDataState() {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getDataState"
                , Integer.class)).postMethodProcess(context);
    }

    public ResultTask<String> getDeviceId() {
        return new ResultCreator<String>(buildMethodCallPacketData("getDeviceId"
                , String.class)).postMethodProcess(context);
    }

    public ResultTask<String> getDeviceId(Integer slotIndex) {
        return new ResultCreator<String>(buildMethodCallPacketData("getDeviceId"
                , String.class,
                new Class[]{Integer.class},
                slotIndex)).postMethodProcess(context);
    }

    public ResultTask<String> getDeviceSoftwareVersion() {
        return new ResultCreator<String>(buildMethodCallPacketData("getDeviceSoftwareVersion"
                , String.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public ResultTask<ArrayList<String>> getEquivalentHomePlmns() {
        return new ResultCreator<ArrayList<String>>(buildMethodCallPacketData("getEquivalentHomePlmns"
                , ArrayList.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ResultTask<String[]> getForbiddenPlmns() {
        return new ResultCreator<String[]>(buildMethodCallPacketData("getForbiddenPlmns"
                , String[].class)).postMethodProcess(context);
    }

    public ResultTask<String> getGroupIdLevel1() {
        return new ResultCreator<String>(buildMethodCallPacketData("getGroupIdLevel1"
                , String.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ResultTask<String> getIccAuthentication(Integer appType, Integer authType, String data) {
        return new ResultCreator<String>(buildMethodCallPacketData("getIccAuthentication"
                , String.class,
                new Class[]{Integer.class, Integer.class, String.class},
                appType, authType, data)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ResultTask<String> getImei(Integer slotIndex) {
        return new ResultCreator<String>(buildMethodCallPacketData("getImei"
                , String.class,
                new Class[]{Integer.class},
                slotIndex)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ResultTask<String> getImei() {
        return new ResultCreator<String>(buildMethodCallPacketData("getImei"
                , String.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public ResultTask<String> getLine1Number() {
        return new ResultCreator<String>(buildMethodCallPacketData("getLine1Number"
                , String.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ResultTask<String> getManualNetworkSelectionPlmn() {
        return new ResultCreator<String>(buildMethodCallPacketData("getManualNetworkSelectionPlmn"
                , String.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ResultTask<String> getManufacturerCode(Integer slotIndex) {
        return new ResultCreator<String>(buildMethodCallPacketData("getManufacturerCode"
                , String.class,
                new Class[]{Integer.class},
                slotIndex)).postMethodProcess(context);
    }

    public ResultTask<String> getManufacturerCode() {
        return new ResultCreator<String>(buildMethodCallPacketData("getManufacturerCode"
                , String.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public ResultTask<Long> getMaximumCallComposerPictureSize() {
        return new ResultCreator<Long>(buildMethodCallPacketData("getMaximumCallComposerPictureSize"
                , Long.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ResultTask<String> getMeid() {
        return new ResultCreator<String>(buildMethodCallPacketData("getMeid"
                , String.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ResultTask<String> getMeid(Integer slotIndex) {
        return new ResultCreator<String>(buildMethodCallPacketData("getMeid"
                , String.class,
                new Class[]{Integer.class},
                slotIndex)).postMethodProcess(context);
    }

    public ResultTask<String> getMmsUAProfUrl() {
        return new ResultCreator<String>(buildMethodCallPacketData("getMmsUAProfUrl"
                , String.class)).postMethodProcess(context);
    }

    public ResultTask<String> getMmsUserAgent() {
        return new ResultCreator<String>(buildMethodCallPacketData("getMmsUserAgent"
                , String.class)).postMethodProcess(context);
    }
    @RequiresApi(api = Build.VERSION_CODES.P)
    public ResultTask<String> getNai() {
        return new ResultCreator<String>(buildMethodCallPacketData("getNai"
                , String.class)).postMethodProcess(context);
    }

    public ResultTask<String> getNetworkCountryIso() {
        return new ResultCreator<String>(buildMethodCallPacketData("getNetworkCountryIso"
                , String.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public ResultTask<String> getNetworkCountryIso(Integer slotIndex) {
        return new ResultCreator<String>(buildMethodCallPacketData("getNetworkCountryIso"
                , String.class,
                new Class[]{Integer.class},
                slotIndex)).postMethodProcess(context);
    }

    public ResultTask<String> getNetworkOperator() {
        return new ResultCreator<String>(buildMethodCallPacketData("getNetworkOperator"
                , String.class)).postMethodProcess(context);
    }

    public ResultTask<String> getNetworkOperatorName() {
        return new ResultCreator<String>(buildMethodCallPacketData("getNetworkOperatorName"
                , String.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public ResultTask<Integer> getNetworkSelectionMode() {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getNetworkSelectionMode"
                , Integer.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ResultTask<String> getNetworkSpecifier() {
        return new ResultCreator<String>(buildMethodCallPacketData("getNetworkSpecifier"
                , String.class)).postMethodProcess(context);
    }

    public ResultTask<Integer> getNetworkType() {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getNetworkType"
                , Integer.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public ResultTask<PhoneAccountHandle> getPhoneAccountHandle() {
        return new ResultCreator<PhoneAccountHandle>(buildMethodCallPacketData("getPhoneAccountHandle"
                , PhoneAccountHandle.class)).postMethodProcess(context);
    }

    public ResultTask<Integer> getPhoneCount() {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getPhoneCount"
                , Integer.class)).postMethodProcess(context);
    }

    public ResultTask<Integer> getPhoneType() {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getPhoneType"
                , Integer.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ResultTask<Integer> getPreferredOpportunisticDataSubscription() {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getPreferredOpportunisticDataSubscription"
                , Integer.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public ResultTask<String> getPrimaryImei() {
        return new ResultCreator<String>(buildMethodCallPacketData("getPrimaryImei"
                , String.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public ResultTask<ServiceState> getServiceState(Integer includeLocationData) {
        return new ResultCreator<ServiceState>(buildMethodCallPacketData("getServiceState"
                , ServiceState.class,
                new Class[]{Integer.class},
                includeLocationData)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ResultTask<ServiceState> getServiceState() {
        return new ResultCreator<ServiceState>(buildMethodCallPacketData("getServiceState"
                , ServiceState.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public ResultTask<SignalStrength> getSignalStrength() {
        return new ResultCreator<SignalStrength>(buildMethodCallPacketData("getSignalStrength"
                , SignalStrength.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public ResultTask<Integer> getSimCarrierId() {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getSimCarrierId"
                , Integer.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public ResultTask<CharSequence> getSimCarrierIdName() {
        return new ResultCreator<CharSequence>(buildMethodCallPacketData("getSimCarrierIdName"
                , CharSequence.class)).postMethodProcess(context);
    }

    public ResultTask<String> getSimCountryIso() {
        return new ResultCreator<String>(buildMethodCallPacketData("getSimCountryIso"
                , String.class)).postMethodProcess(context);
    }

    public ResultTask<String> getSimOperator() {
        return new ResultCreator<String>(buildMethodCallPacketData("getSimOperator"
                , String.class)).postMethodProcess(context);
    }

    public ResultTask<String> getSimOperatorName() {
        return new ResultCreator<String>(buildMethodCallPacketData("getSimOperatorName"
                , String.class)).postMethodProcess(context);
    }

    public ResultTask<String> getSimSerialNumber() {
        return new ResultCreator<String>(buildMethodCallPacketData("getSimSerialNumber"
                , String.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ResultTask<Integer> getSimSpecificCarrierId() {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getSimSpecificCarrierId"
                , Integer.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ResultTask<CharSequence> getSimSpecificCarrierIdName() {
        return new ResultCreator<CharSequence>(buildMethodCallPacketData("getSimSpecificCarrierIdName"
                , CharSequence.class)).postMethodProcess(context);
    }

    public ResultTask<Integer> getSimState() {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getSimState"
                , Integer.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ResultTask<Integer> getSimState(Integer slotIndex) {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getSimState"
                , Integer.class,
                new Class[]{Integer.class},
                slotIndex)).postMethodProcess(context);
    }

    public ResultTask<String> getSubscriberId() {
        return new ResultCreator<String>(buildMethodCallPacketData("getSubscriberId"
                , String.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public ResultTask<Integer> getSubscriptionId(PhoneAccountHandle phoneAccountHandle) {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getSubscriptionId"
                , Integer.class,
                new Class[]{PhoneAccountHandle.class},
                phoneAccountHandle)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public ResultTask<Integer> getSubscriptionId() {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getSubscriptionId"
                , Integer.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public ResultTask<Integer> getSupportedModemCount() {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getSupportedModemCount"
                , Integer.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public ResultTask<Long> getSupportedRadioAccessFamily() {
        return new ResultCreator<Long>(buildMethodCallPacketData("getSupportedRadioAccessFamily"
                , Long.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ResultTask<String> getTypeAllocationCode() {
        return new ResultCreator<String>(buildMethodCallPacketData("getTypeAllocationCode"
                , String.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ResultTask<String> getTypeAllocationCode(Integer slotIndex) {
        return new ResultCreator<String>(buildMethodCallPacketData("getTypeAllocationCode"
                , String.class,
                new Class[]{Integer.class},
                slotIndex)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ResultTask<ArrayList<UiccCardInfo>> getUiccCardsInfo() {
        return new ResultCreator<ArrayList<UiccCardInfo>>(buildMethodCallPacketData("getUiccCardsInfo"
                , ArrayList.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ResultTask<String> getVisualVoicemailPackageName() {
        return new ResultCreator<String>(buildMethodCallPacketData("getVisualVoicemailPackageName"
                , String.class)).postMethodProcess(context);
    }

    public ResultTask<String> getVoiceMailAlphaTag() {
        return new ResultCreator<String>(buildMethodCallPacketData("getVoiceMailAlphaTag"
                , String.class)).postMethodProcess(context);
    }

    public ResultTask<String> getVoiceMailNumber() {
        return new ResultCreator<String>(buildMethodCallPacketData("getVoiceMailNumber"
                , String.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ResultTask<Integer> getVoiceNetworkType() {
        return new ResultCreator<Integer>(buildMethodCallPacketData("getVoiceNetworkType"
                , Integer.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ResultTask<Uri> getVoicemailRingtoneUri(PhoneAccountHandle accountHandle) {
        return new ResultCreator<Uri>(buildMethodCallPacketData("getVoicemailRingtoneUri"
                , Uri.class,
                new Class[]{PhoneAccountHandle.class},
                accountHandle)).postMethodProcess(context);
    }

    public ResultTask<Boolean> hasCarrierPrivileges() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("hasCarrierPrivileges"
                , Boolean.class)).postMethodProcess(context);
    }

    public ResultTask<Boolean> hasIccCard() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("hasIccCard"
                , Boolean.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ResultTask<Boolean> iccCloseLogicalChannel(Integer channel) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("iccCloseLogicalChannel"
                , Boolean.class,
                new Class[]{Integer.class},
                channel)).postMethodProcess(context);
    }

    public ResultTask<IccOpenLogicalChannelResponse> iccOpenLogicalChannel(String AID, Integer p2) {
        return new ResultCreator<IccOpenLogicalChannelResponse>(buildMethodCallPacketData("iccOpenLogicalChannel"
                , IccOpenLogicalChannelResponse.class,
                new Class[]{String.class, Integer.class},
                AID, p2)).postMethodProcess(context);
    }

    public ResultTask<IccOpenLogicalChannelResponse> iccOpenLogicalChannel(String AID) {
        return new ResultCreator<IccOpenLogicalChannelResponse>(buildMethodCallPacketData("iccOpenLogicalChannel"
                , IccOpenLogicalChannelResponse.class,
                new Class[]{String.class},
                AID)).postMethodProcess(context);
    }

    public ResultTask<String> iccTransmitApduBasicChannel(Integer cla, Integer instruction, Integer p1, Integer p2, Integer p3, String data) {
        return new ResultCreator<String>(buildMethodCallPacketData("iccTransmitApduBasicChannel"
                , String.class,
                new Class[]{Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, String.class},
                cla, instruction, p1, p2, p3, data)).postMethodProcess(context);
    }

    public ResultTask<String> iccTransmitApduLogicalChannel(Integer channel, Integer cla, Integer instruction, Integer p1, Integer p2, Integer p3, String data) {
        return new ResultCreator<String>(buildMethodCallPacketData("iccTransmitApduLogicalChannel"
                , String.class,
                new Class[]{Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, String.class},
                channel, cla, instruction, p1, p2, p3, data)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ResultTask<Boolean> isConcurrentVoiceAndDataSupported() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("isConcurrentVoiceAndDataSupported"
                , Boolean.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public ResultTask<Boolean> isDataCapable() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("isDataCapable"
                , Boolean.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public ResultTask<Boolean> isDataConnectionAllowed() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("isDataConnectionAllowed"
                , Boolean.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ResultTask<Boolean> isDataEnabled() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("isDataEnabled"
                , Boolean.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public ResultTask<Boolean> isDataEnabledForReason(Integer reason) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("isDataEnabledForReason"
                , Boolean.class,
                new Class[]{Integer.class},
                reason)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ResultTask<Boolean> isDataRoamingEnabled() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("isDataRoamingEnabled"
                , Boolean.class)).postMethodProcess(context);
    }

    public ResultTask<Boolean> isDeviceSmsCapable() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("isDeviceSmsCapable"
                , Boolean.class)).postMethodProcess(context);
    }

    public ResultTask<Boolean> isDeviceVoiceCapable() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("isDeviceVoiceCapable"
                , Boolean.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ResultTask<Boolean> isEmergencyNumber(String number) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("isEmergencyNumber"
                , Boolean.class,
                new Class[]{String.class},
                number)).postMethodProcess(context);
    }

    public ResultTask<Boolean> isHearingAidCompatibilitySupported() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("isHearingAidCompatibilitySupported"
                , Boolean.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public ResultTask<Boolean> isManualNetworkSelectionAllowed() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("isManualNetworkSelectionAllowed"
                , Boolean.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public ResultTask<Boolean> isModemEnabledForSlot(Integer slotIndex) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("isModemEnabledForSlot"
                , Boolean.class,
                new Class[]{Integer.class},
                slotIndex)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ResultTask<Integer> isMultiSimSupported() {
        return new ResultCreator<Integer>(buildMethodCallPacketData("isMultiSimSupported"
                , Integer.class)).postMethodProcess(context);
    }

    public ResultTask<Boolean> isNetworkRoaming() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("isNetworkRoaming"
                , Boolean.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public ResultTask<Boolean> isPremiumCapabilityAvailableForPurchase(Integer capability) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("isPremiumCapabilityAvailableForPurchase"
                , Boolean.class,
                new Class[]{Integer.class},
                capability)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public ResultTask<Boolean> isRadioInterfaceCapabilitySupported(String capability) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("isRadioInterfaceCapabilitySupported"
                , Boolean.class,
                new Class[]{String.class},
                capability)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ResultTask<Boolean> isRttSupported() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("isRttSupported"
                , Boolean.class)).postMethodProcess(context);
    }

    @Deprecated(since = "Replaced to #isDeviceSmsCapable Since Android 15")
    public ResultTask<Boolean> isSmsCapable() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("isSmsCapable"
                , Boolean.class)).postMethodProcess(context);
    }

    public ResultTask<Boolean> isTtyModeSupported() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("isTtyModeSupported"
                , Boolean.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ResultTask<Boolean> isVoicemailVibrationEnabled(PhoneAccountHandle accountHandle) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("isVoicemailVibrationEnabled"
                , Boolean.class,
                new Class[]{PhoneAccountHandle.class},
                accountHandle)).postMethodProcess(context);
    }

    public ResultTask<Boolean> isWorldPhone() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("isWorldPhone"
                , Boolean.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public ResultTask<Void> rebootModem() {
        return new ResultCreator<Void>(buildMethodCallPacketData("rebootModem"
                , Void.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ResultTask<Void> sendDialerSpecialCode(String inputCode) {
        return new ResultCreator<Void>(buildMethodCallPacketData("sendDialerSpecialCode"
                , Void.class,
                new Class[]{String.class},
                inputCode)).postMethodProcess(context);
    }

    public ResultTask<String> sendEnvelopeWithStatus(String content) {
        return new ResultCreator<String>(buildMethodCallPacketData("sendEnvelopeWithStatus"
                , String.class,
                new Class[]{String.class},
                content)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ResultTask<Void> sendVisualVoicemailSms(String number, Integer port, String text, PendingIntent sentIntent) {
        return new ResultCreator<Void>(buildMethodCallPacketData("sendVisualVoicemailSms"
                , Void.class,
                new Class[]{String.class, Integer.class, String.class, PendingIntent.class},
                number, port, text, sentIntent)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public ResultTask<Void> setAllowedNetworkTypesForReason(Integer reason, Long allowedNetworkTypes) {
        return new ResultCreator<Void>(buildMethodCallPacketData("setAllowedNetworkTypesForReason"
                , Void.class,
                new Class[]{Integer.class, Long.class},
                reason, allowedNetworkTypes)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public ResultTask<Void> setCallComposerStatus(Integer status) {
        return new ResultCreator<Void>(buildMethodCallPacketData("setCallComposerStatus"
                , Void.class,
                new Class[]{Integer.class},
                status)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ResultTask<Void> setDataEnabled(Boolean enable) {
        return new ResultCreator<Void>(buildMethodCallPacketData("setDataEnabled"
                , Void.class,
                new Class[]{Boolean.class},
                enable)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public ResultTask<Void> setDataEnabledForReason(Integer reason, Boolean enabled) {
        return new ResultCreator<Void>(buildMethodCallPacketData("setDataEnabledForReason"
                , Void.class,
                new Class[]{Integer.class, Boolean.class},
                reason, enabled)).postMethodProcess(context);
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    public ResultTask<Integer> setForbiddenPlmns(ArrayList<String> fplmns) {
        return new ResultCreator<Integer>(buildMethodCallPacketData("setForbiddenPlmns"
                , Integer.class,
                new Class[]{ArrayList.class},
                fplmns)).postMethodProcess(context);
    }

    public ResultTask<Boolean> setLine1NumberForDisplay(String alphaTag, String number) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("setLine1NumberForDisplay"
                , Boolean.class,
                new Class[]{String.class, String.class},
                alphaTag, number)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public ResultTask<Void> setNetworkSelectionModeAutomatic() {
        return new ResultCreator<Void>(buildMethodCallPacketData("setNetworkSelectionModeAutomatic"
                , Void.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public ResultTask<Boolean> setNetworkSelectionModeManual(String operatorNumeric, Boolean persistSelection, Integer ran) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("setNetworkSelectionModeManual"
                , Boolean.class,
                new Class[]{String.class, Boolean.class, Integer.class},
                operatorNumeric, persistSelection, ran)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public ResultTask<Boolean> setNetworkSelectionModeManual(String operatorNumeric, Boolean persistSelection) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("setNetworkSelectionModeManual"
                , Boolean.class,
                new Class[]{String.class, Boolean.class},
                operatorNumeric, persistSelection)).postMethodProcess(context);
    }

    public ResultTask<Boolean> setOperatorBrandOverride(String brand) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("setOperatorBrandOverride"
                , Boolean.class,
                new Class[]{String.class},
                brand)).postMethodProcess(context);
    }

    public ResultTask<Boolean> setPreferredNetworkTypeToGlobal() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("setPreferredNetworkTypeToGlobal"
                , Boolean.class)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public ResultTask<Void> setSignalStrengthUpdateRequest(SignalStrengthUpdateRequest request) {
        return new ResultCreator<Void>(buildMethodCallPacketData("setSignalStrengthUpdateRequest"
                , Void.class,
                new Class[]{SignalStrengthUpdateRequest.class},
                request)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ResultTask<Void> setVisualVoicemailSmsFilterSettings(VisualVoicemailSmsFilterSettings settings) {
        return new ResultCreator<Void>(buildMethodCallPacketData("setVisualVoicemailSmsFilterSettings"
                , Void.class,
                new Class[]{VisualVoicemailSmsFilterSettings.class},
                settings)).postMethodProcess(context);
    }

    public ResultTask<Boolean> setVoiceMailNumber(String alphaTag, String number) {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("setVoiceMailNumber"
                , Boolean.class,
                new Class[]{String.class, String.class},
                alphaTag, number)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ResultTask<Void> setVoicemailRingtoneUri(PhoneAccountHandle phoneAccountHandle, Uri uri) {
        return new ResultCreator<Void>(buildMethodCallPacketData("setVoicemailRingtoneUri"
                , Void.class,
                new Class[]{PhoneAccountHandle.class, Uri.class},
                phoneAccountHandle, uri)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ResultTask<Void> setVoicemailVibrationEnabled(PhoneAccountHandle phoneAccountHandle, Boolean enabled) {
        return new ResultCreator<Void>(buildMethodCallPacketData("setVoicemailVibrationEnabled"
                , Void.class,
                new Class[]{PhoneAccountHandle.class, Boolean.class},
                phoneAccountHandle, enabled)).postMethodProcess(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ResultTask<Void> switchMultiSimConfig(Integer numOfSims) {
        return new ResultCreator<Void>(buildMethodCallPacketData("switchMultiSimConfig"
                , Void.class,
                new Class[]{Integer.class},
                numOfSims)).postMethodProcess(context);
    }

    private PacketData buildMethodCallPacketData(String methodName, Class<?> parameterCls, Object... args) {
        return buildMethodCallPacketData(methodName, parameterCls, null, args);
    }

    private PacketData buildMethodCallPacketData(String methodName, Class<?> parameterCls, Class<?>[] classHint, Object... args) {
        Instance instance = Instance.getInstance();
        PacketData packet = new PacketData();
        String ticket = String.format("%s@%s", methodName, System.currentTimeMillis());

        ArgsInfo argsInfo = new ArgsInfo();
        if(subId != null) {
            argsInfo.put(Integer.class, subId);
        } else if (phoneAccountHandle != null) {
            argsInfo.put(PhoneAccountHandle.class, ProcessConst.KEY_PARCEL_REPLACED);
            packet.parcelableList.add(phoneAccountHandle);
        } else {
            argsInfo.put(Void.class, null);
        }
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
        packet.apiType = ProcessConst.ACTION_TYPE_TELEPHONY;
        packet.actionType = ProcessConst.ACTION_REQUEST_METHOD;

        return packet;
    }
}