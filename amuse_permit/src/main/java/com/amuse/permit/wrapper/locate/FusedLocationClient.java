package com.amuse.permit.wrapper.locate;

import android.content.Context;
import android.location.Location;
import android.os.Parcelable;

import com.amuse.permit.Instance;
import com.amuse.permit.data.ArgsInfo;
import com.amuse.permit.data.PacketData;
import com.amuse.permit.model.Annotations;
import com.amuse.permit.model.ResultTask;
import com.amuse.permit.process.ProcessConst;
import com.amuse.permit.process.action.ResultCreator;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The main-wrapper entry point for interacting with the Fused Location Provider (FLP).
 */
@Annotations.RequesterSide
@SuppressWarnings("unused")
public class FusedLocationClient extends LocateModel {

    @JsonIgnore
    private Context context;

    /**
     * Get default {@link Location} instance,
     *
     * @see android.location.LocationProvider
     * @param context Application context instance
     * @return the default {@link FusedLocationClient} instance
     */
    @Annotations.StaticMethod
    @Annotations.Constructor
    public static FusedLocationClient getFusedLocationClient(Context context) {
        FusedLocationClient client = new FusedLocationClient();
        client.context = context;
        return client;
    }

    /**
     * Flushes any locations currently being batched and sends them to all registered {@link android.location.LocationListener},
     * and {@link android.app.PendingIntent}. This call is only useful when batching is specified using <code>LocationRequest.setMaxWaitTime(long)</code>,
     * otherwise locations are already delivered immediately when available
     *
     * @return whether the flush operation is finished successfully or not
     */
    public ResultTask<Boolean> flushLocations() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("flushLocations", Boolean.class)).postMethodProcess(context);
    }

    /**
     * Returns the most recent cached location from the past currently available.
     * Will return null if no such cached location is available. The returned location may be of an arbitrary age,
     * so clients should check how old the location is to see if it suits their purposes.
     * Since this method simply checks caches for pre-computed locations it is generally cheap and quick to return.
     *
     * @return the cached location object
     */
    public ResultTask<Location> getLastLocation() {
        return new ResultCreator<Location>(buildMethodCallPacketData("getLastLocation", Location.class)).postMethodProcess(context);
    }

    /**
     * Returns a single location fix representing the best estimate of the current location of the device.
     * This may return a cached location if a recent enough location fix exists, or may compute a fresh location.
     * If unable to retrieve a current location fix before timing out, null will be returned.
     *
     * @return the current location object
     */
    public ResultTask<Location> getCurrentLocation(@LocationPriority Integer priority) {
        return new ResultCreator<Location>(buildMethodCallPacketData("getCurrentLocation", Location.class, priority)).postMethodProcess(context);
    }

    /**
     * Returns the estimated availability of location data.
     * If <code>LocationAvailability.isLocationAvailable()</code> returns true then it is likely (but not guaranteed)
     * that Fused Location Provider APIs will be able to derive and return fresh location updates.
     * If <code>LocationAvailability.isLocationAvailable()</code> returns false, then it is likely (but not guaranteed)
     * that Fused Location Provider APIs will be unable to derive and return fresh location updates, though there may be cached locations available.
     *
     * @return returns true if location is available
     */
    public ResultTask<Boolean> getLocationAvailability() {
        return new ResultCreator<Boolean>(buildMethodCallPacketData("getLocationAvailability", Boolean.class)).postMethodProcess(context);
    }

    /**
     * Sets whether or not the Fused Location Provider is in mock mode.
     * <p>
     * Entering mock mode clears the FLP's cached locations, and ensures that the FLP will only report locations set through {@link #setMockLocation(Location)}.
     * Exiting mock mode will clear any mock locations set from the FLP's cache as well.
     * Mock mode affects all location clients using the FLP, including location clients in other processes and derivative APIs such as geofencing and so forth.
     * Because this affects all FLP usage, clients should always ensure they properly set the mock mode to false when finished.
     * <p>
     * Successfully using this API on devices running Android M+ requires the client to request the <code>android.permission.ACCESS_MOCK_LOCATION</code> permission
     * and to be selected as the mock location app within the device developer settings.
     * Using this API on pre-M devices requires the <code>Settings.Secure.ALLOW_MOCK_LOCATION</code> setting to be enabled.
     *
     * @param mockMode whether to perform the mock location mode or not
     */
    public void setMockMode(boolean mockMode) {
        this.isMock = mockMode;
    }

    /**
     * Sets the mock location of the Fused Location Provider.
     * <p>
     * Delivers the given location to the FLP as if it was coming from an underlying location source.
     * Normal FLP logic around receiving and delivering location will generally apply.
     * For this reason the timestamps of the location should be set appropriately,
     * as the FLP may expect monotonically increasing timestamps. When this location is reported to FLP clients it will be marked as a mock location
     * (see {@link Location#isMock()} ()} or {@link androidx.core.location.LocationCompat#isMock(Location)} from the compat libraries).
     * <p>
     * This API can only be successfully used while the FLP is in mock mode.
     * Clients must fulfill the same security requirements as for {@link #setMockMode(boolean)} as well.
     *
     * @param location an location object to use as mock location
     */
    public void setMockLocation(Location location) {
        this.mockLocation = location;
    }

    /**
     * Gets whether or not the Fused Location Provider is in mock mode.
     *
     * @return whether the location is mock mode or not
     * @see #setMockMode(boolean)
     */
    @JsonGetter("isMock")
    protected boolean getMockMode() {
        return isMock;
    }

    /**
     * Gets location object that used as mock location
     *
     * @return whether the location is mock mode or not
     * @see #setMockLocation(Location)
     */
    @JsonGetter("mockLocation")
    protected Location getMockLocation() {
        return mockLocation;
    }

    private PacketData buildMethodCallPacketData(String methodName, Class<?> parameterCls, Object... args) {
        Instance instance = Instance.getInstance();
        PacketData packet = new PacketData();
        ArgsInfo argsInfo = new ArgsInfo();
        String ticket = String.format("%s@%s", methodName, System.currentTimeMillis());

        packet.parcelableList.add(mockLocation);
        argsInfo.put(Location.class, ProcessConst.KEY_PARCEL_REPLACED);
        argsInfo.put(isMock);
        argsInfo.put(parameterCls, methodName);

        for(Object arg : args) {
            if(arg instanceof Parcelable) {
                argsInfo.put(arg.getClass(), ProcessConst.KEY_PARCEL_REPLACED);
                packet.parcelableList.add((Parcelable) arg);
            } else {
                argsInfo.put(arg);
            }
        }

        packet.argsInfo = argsInfo;
        packet.fromPackageName = instance.getServerPeer().getPackageName();
        packet.ticketId = ticket;
        packet.apiType = ProcessConst.ACTION_TYPE_LOCATION;
        packet.actionType = ProcessConst.ACTION_REQUEST_METHOD;

        return packet;
    }
}
