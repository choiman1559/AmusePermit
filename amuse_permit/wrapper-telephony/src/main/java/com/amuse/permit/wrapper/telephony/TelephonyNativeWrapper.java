package com.amuse.permit.wrapper.telephony;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.telecom.PhoneAccountHandle;
import android.telephony.CellInfo;
import android.telephony.IccOpenLogicalChannelResponse;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SignalStrengthUpdateRequest;
import android.telephony.TelephonyManager;
import android.telephony.UiccCardInfo;
import android.telephony.VisualVoicemailSmsFilterSettings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.model.Annotations;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.model.Wrappable;

import java.util.ArrayList;

@SuppressWarnings("unused")
@SuppressLint({"MissingPermission", "HardwareIds"})
@Annotations.ResponserSide
public class TelephonyNativeWrapper extends Wrappable {

    private Context context;
    private TelephonyManager telephonyManager;

    @Override
    public ResultTask<Wrappable> createServerInstance(@NonNull Context context, @NonNull ArgsInfo argsInfo) {
        ResultTask<Wrappable> nativeWrapperResultTask = new ResultTask<>();
        nativeWrapperResultTask.mOnInvokeAttached = result -> {
            ResultTask.Result<Wrappable> nativeResult = new ResultTask.Result<>();
            TelephonyNativeWrapper nativeWrapper = new TelephonyNativeWrapper();
            nativeWrapper.context = context;

            Class<?> constructorClass = argsInfo.getCls(0);
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (constructorClass.equals(Integer.class) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                telephonyManager = telephonyManager.createForSubscriptionId((Integer) argsInfo.getData(0));
            } else if (constructorClass.equals(PhoneAccountHandle.class) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                telephonyManager = telephonyManager.createForPhoneAccountHandle((PhoneAccountHandle) argsInfo.getData(0));
            }

            nativeResult.setResultData(nativeWrapper);
            nativeResult.setSuccess(nativeWrapper.telephonyManager != null);
            nativeWrapperResultTask.callCompleteListener(nativeResult);
        };
        return nativeWrapperResultTask;
    }

    public Boolean canChangeDtmfToneLength() {
        return telephonyManager.canChangeDtmfToneLength();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public Void clearSignalStrengthUpdateRequest(SignalStrengthUpdateRequest request) {
        telephonyManager.clearSignalStrengthUpdateRequest(request);
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Boolean doesSwitchMultiSimConfigTriggerReboot() {
        return telephonyManager.doesSwitchMultiSimConfigTriggerReboot();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public Integer getActiveModemCount() {
        return telephonyManager.getActiveModemCount();
    }

    public ArrayList<CellInfo> getAllCellInfo() {
        return (ArrayList<CellInfo>) telephonyManager.getAllCellInfo();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public Long getAllowedNetworkTypesForReason(Integer reason) {
        return telephonyManager.getAllowedNetworkTypesForReason(reason);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public Integer getCallComposerStatus() {
        return telephonyManager.getCallComposerStatus();
    }

    public Integer getCallState() {
        return telephonyManager.getCallState();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public Integer getCallStateForSubscription() {
        return telephonyManager.getCallStateForSubscription();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Integer getCardIdForDefaultEuicc() {
        return telephonyManager.getCardIdForDefaultEuicc();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public PersistableBundle getCarrierConfig() {
        return telephonyManager.getCarrierConfig();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Integer getCarrierIdFromSimMccMnc() {
        return telephonyManager.getCarrierIdFromSimMccMnc();
    }

    public Integer getDataActivity() {
        return telephonyManager.getDataActivity();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Integer getDataNetworkType() {
        return telephonyManager.getDataNetworkType();
    }

    public Integer getDataState() {
        return telephonyManager.getDataState();
    }

    public String getDeviceId() {
        return telephonyManager.getDeviceId();
    }

    public String getDeviceId(Integer slotIndex) {
        return telephonyManager.getDeviceId(slotIndex);
    }

    public String getDeviceSoftwareVersion() {
        return telephonyManager.getDeviceSoftwareVersion();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public ArrayList<String> getEquivalentHomePlmns() {
        return (ArrayList<String>) telephonyManager.getEquivalentHomePlmns();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String[] getForbiddenPlmns() {
        return telephonyManager.getForbiddenPlmns();
    }

    public String getGroupIdLevel1() {
        return telephonyManager.getGroupIdLevel1();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getIccAuthentication(Integer appType, Integer authType, String data) {
        return telephonyManager.getIccAuthentication(appType, authType, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getImei(Integer slotIndex) {
        return telephonyManager.getImei(slotIndex);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getImei() {
        return telephonyManager.getImei();
    }

    public String getLine1Number() {
        return telephonyManager.getLine1Number();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public String getManualNetworkSelectionPlmn() {
        return telephonyManager.getManualNetworkSelectionPlmn();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public String getManufacturerCode(Integer slotIndex) {
        return telephonyManager.getManufacturerCode(slotIndex);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public String getManufacturerCode() {
        return telephonyManager.getManufacturerCode();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public Long getMaximumCallComposerPictureSize() {
        return TelephonyManager.getMaximumCallComposerPictureSize();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getMeid() {
        return telephonyManager.getMeid();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getMeid(Integer slotIndex) {
        return telephonyManager.getMeid(slotIndex);
    }

    public String getMmsUAProfUrl() {
        return telephonyManager.getMmsUAProfUrl();
    }

    public String getMmsUserAgent() {
        return telephonyManager.getMmsUserAgent();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public String getNai() {
        return telephonyManager.getNai();
    }

    public String getNetworkCountryIso() {
        return telephonyManager.getNetworkCountryIso();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public String getNetworkCountryIso(Integer slotIndex) {
        return telephonyManager.getNetworkCountryIso(slotIndex);
    }

    public String getNetworkOperator() {
        return telephonyManager.getNetworkOperator();
    }

    public String getNetworkOperatorName() {
        return telephonyManager.getNetworkOperatorName();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public Integer getNetworkSelectionMode() {
        return telephonyManager.getNetworkSelectionMode();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getNetworkSpecifier() {
        return telephonyManager.getNetworkSpecifier();
    }

    public Integer getNetworkType() {
        return telephonyManager.getNetworkType();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public PhoneAccountHandle getPhoneAccountHandle() {
        return telephonyManager.getPhoneAccountHandle();
    }

    public Integer getPhoneCount() {
        return telephonyManager.getPhoneCount();
    }

    public Integer getPhoneType() {
        return telephonyManager.getPhoneType();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Integer getPreferredOpportunisticDataSubscription() {
        return telephonyManager.getPreferredOpportunisticDataSubscription();
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public String getPrimaryImei() {
        return telephonyManager.getPrimaryImei();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public ServiceState getServiceState(Integer includeLocationData) {
        return telephonyManager.getServiceState(includeLocationData);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public SignalStrength getSignalStrength() {
        return telephonyManager.getSignalStrength();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public Integer getSimCarrierId() {
        return telephonyManager.getSimCarrierId();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public CharSequence getSimCarrierIdName() {
        return telephonyManager.getSimCarrierIdName();
    }

    public String getSimCountryIso() {
        return telephonyManager.getSimCountryIso();
    }

    public String getSimOperator() {
        return telephonyManager.getSimOperator();
    }

    public String getSimOperatorName() {
        return telephonyManager.getSimOperatorName();
    }

    public String getSimSerialNumber() {
        return telephonyManager.getSimSerialNumber();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Integer getSimSpecificCarrierId() {
        return telephonyManager.getSimSpecificCarrierId();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public CharSequence getSimSpecificCarrierIdName() {
        return telephonyManager.getSimSpecificCarrierIdName();
    }

    public Integer getSimState() {
        return telephonyManager.getSimState();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Integer getSimState(Integer slotIndex) {
        return telephonyManager.getSimState(slotIndex);
    }

    public String getSubscriberId() {
        return telephonyManager.getSubscriberId();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public Integer getSubscriptionId(PhoneAccountHandle phoneAccountHandle) {
        return telephonyManager.getSubscriptionId(phoneAccountHandle);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public Integer getSubscriptionId() {
        return telephonyManager.getSubscriptionId();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public Integer getSupportedModemCount() {
        return telephonyManager.getSupportedModemCount();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public Long getSupportedRadioAccessFamily() {
        return telephonyManager.getSupportedRadioAccessFamily();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public String getTypeAllocationCode() {
        return telephonyManager.getTypeAllocationCode();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public String getTypeAllocationCode(Integer slotIndex) {
        return telephonyManager.getTypeAllocationCode(slotIndex);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ArrayList<UiccCardInfo> getUiccCardsInfo() {
        return (ArrayList<UiccCardInfo>) telephonyManager.getUiccCardsInfo();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getVisualVoicemailPackageName() {
        return telephonyManager.getVisualVoicemailPackageName();
    }

    public String getVoiceMailAlphaTag() {
        return telephonyManager.getVoiceMailAlphaTag();
    }

    public String getVoiceMailNumber() {
        return telephonyManager.getVoiceMailNumber();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Integer getVoiceNetworkType() {
        return telephonyManager.getVoiceNetworkType();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Uri getVoicemailRingtoneUri(PhoneAccountHandle accountHandle) {
        return telephonyManager.getVoicemailRingtoneUri(accountHandle);
    }

    public Boolean hasCarrierPrivileges() {
        return telephonyManager.hasCarrierPrivileges();
    }

    public Boolean hasIccCard() {
        return telephonyManager.hasIccCard();
    }

    public Boolean iccCloseLogicalChannel(Integer channel) {
        return telephonyManager.iccCloseLogicalChannel(channel);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public IccOpenLogicalChannelResponse iccOpenLogicalChannel(String AID, Integer p2) {
        return telephonyManager.iccOpenLogicalChannel(AID, p2);
    }

    public IccOpenLogicalChannelResponse iccOpenLogicalChannel(String AID) {
        return telephonyManager.iccOpenLogicalChannel(AID);
    }

    public String iccTransmitApduBasicChannel(Integer cla, Integer instruction, Integer p1, Integer p2, Integer p3, String data) {
        return telephonyManager.iccTransmitApduBasicChannel(cla, instruction, p1, p2, p3, data);
    }

    public String iccTransmitApduLogicalChannel(Integer channel, Integer cla, Integer instruction, Integer p1, Integer p2, Integer p3, String data) {
        return telephonyManager.iccTransmitApduLogicalChannel(channel, cla, instruction, p1, p2, p3, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Boolean isConcurrentVoiceAndDataSupported() {
        return telephonyManager.isConcurrentVoiceAndDataSupported();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public Boolean isDataCapable() {
        return telephonyManager.isDataCapable();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public Boolean isDataConnectionAllowed() {
        return telephonyManager.isDataConnectionAllowed();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Boolean isDataEnabled() {
        return telephonyManager.isDataEnabled();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public Boolean isDataEnabledForReason(Integer reason) {
        return telephonyManager.isDataEnabledForReason(reason);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Boolean isDataRoamingEnabled() {
        return telephonyManager.isDataRoamingEnabled();
    }

    public Boolean isDeviceSmsCapable() {
        return telephonyManager.isSmsCapable();
    }

    public Boolean isDeviceVoiceCapable() {
        return telephonyManager.isVoiceCapable();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Boolean isEmergencyNumber(String number) {
        return telephonyManager.isEmergencyNumber(number);
    }

    public Boolean isHearingAidCompatibilitySupported() {
        return telephonyManager.isHearingAidCompatibilitySupported();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public Boolean isManualNetworkSelectionAllowed() {
        return telephonyManager.isManualNetworkSelectionAllowed();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public Boolean isModemEnabledForSlot(Integer slotIndex) {
        return telephonyManager.isModemEnabledForSlot(slotIndex);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Integer isMultiSimSupported() {
        return telephonyManager.isMultiSimSupported();
    }

    public Boolean isNetworkRoaming() {
        return telephonyManager.isNetworkRoaming();
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public Boolean isPremiumCapabilityAvailableForPurchase(Integer capability) {
        return telephonyManager.isPremiumCapabilityAvailableForPurchase(capability);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public Boolean isRadioInterfaceCapabilitySupported(String capability) {
        return telephonyManager.isRadioInterfaceCapabilitySupported(capability);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Boolean isRttSupported() {
        return telephonyManager.isRttSupported();
    }

    public Boolean isSmsCapable() {
        return telephonyManager.isSmsCapable();
    }

    public Boolean isTtyModeSupported() {
        return telephonyManager.isTtyModeSupported();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Boolean isVoicemailVibrationEnabled(PhoneAccountHandle accountHandle) {
        return telephonyManager.isVoicemailVibrationEnabled(accountHandle);
    }

    public Boolean isWorldPhone() {
        return telephonyManager.isWorldPhone();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public Void rebootModem() {
        telephonyManager.rebootModem();
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Void sendDialerSpecialCode(String inputCode) {
        telephonyManager.sendDialerSpecialCode(inputCode);
        return null;
    }

    public String sendEnvelopeWithStatus(String content) {
        return telephonyManager.sendEnvelopeWithStatus(content);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Void sendVisualVoicemailSms(String number, Integer port, String text, PendingIntent sentIntent) {
        telephonyManager.sendVisualVoicemailSms(number, port, text, sentIntent);
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public Void setAllowedNetworkTypesForReason(Integer reason, Long allowedNetworkTypes) {
        telephonyManager.setAllowedNetworkTypesForReason(reason, allowedNetworkTypes);
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public Void setCallComposerStatus(Integer status) {
        telephonyManager.setCallComposerStatus(status);
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Void setDataEnabled(Boolean enable) {
        telephonyManager.setDataEnabled(enable);
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public Void setDataEnabledForReason(Integer reason, Boolean enabled) {
        telephonyManager.setDataEnabledForReason(reason, enabled);
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    public Integer setForbiddenPlmns(ArrayList<String> fplmns) {
        return telephonyManager.setForbiddenPlmns(fplmns);
    }

    public Boolean setLine1NumberForDisplay(String alphaTag, String number) {
        return telephonyManager.setLine1NumberForDisplay(alphaTag, number);
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    public Void setNetworkSelectionModeAutomatic() {
        telephonyManager.setNetworkSelectionModeAutomatic();
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    public Boolean setNetworkSelectionModeManual(String operatorNumeric, Boolean persistSelection, Integer ran) {
        return telephonyManager.setNetworkSelectionModeManual(operatorNumeric, persistSelection, ran);
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    public Boolean setNetworkSelectionModeManual(String operatorNumeric, Boolean persistSelection) {
        return telephonyManager.setNetworkSelectionModeManual(operatorNumeric, persistSelection);
    }

    public Boolean setOperatorBrandOverride(String brand) {
        return telephonyManager.setOperatorBrandOverride(brand);
    }

    public Boolean setPreferredNetworkTypeToGlobal() {
        return telephonyManager.setPreferredNetworkTypeToGlobal();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public Void setSignalStrengthUpdateRequest(SignalStrengthUpdateRequest request) {
        telephonyManager.setSignalStrengthUpdateRequest(request);
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Void setVisualVoicemailSmsFilterSettings(VisualVoicemailSmsFilterSettings settings) {
        telephonyManager.setVisualVoicemailSmsFilterSettings(settings);
        return null;
    }

    public Boolean setVoiceMailNumber(String alphaTag, String number) {
        return telephonyManager.setVoiceMailNumber(alphaTag, number);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Void setVoicemailRingtoneUri(PhoneAccountHandle phoneAccountHandle, Uri uri) {
        telephonyManager.setVoicemailRingtoneUri(phoneAccountHandle, uri);
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Void setVoicemailVibrationEnabled(PhoneAccountHandle phoneAccountHandle, Boolean enabled) {
        telephonyManager.setVoicemailVibrationEnabled(phoneAccountHandle, enabled);
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Void switchMultiSimConfig(Integer numOfSims) {
        telephonyManager.switchMultiSimConfig(numOfSims);
        return null;
    }
}
