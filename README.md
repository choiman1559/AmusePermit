# AmusePermit

IPC framework for unifying permissions for Android sensitive APIs

## What is this for?

To put it bluntly, to bypass the [Play Store's sensitive permissions policy](https://support.google.com/googleplay/android-developer/answer/9888170?hl=en).

In detail, Google has recently expanded the regulations and restrictions on the use of sensitive APIs,
thereby expanding the restrictions on granting permissions for functions that may be necessary for developing apps 
that access sensitive API information such as files, SMS, and locations.
In addition, as the SDK version of Android is gradually updated, the implementation specifications for granting access to these sensitive APIs are gradually changing,
which is expanding fragmentation by SDK version and making the use of these APIs increasingly difficult. 
This leaves significant difficulties in app development and maintenance, including backward compatibility,
and therefore this project was developed to integrate and avoid the restrictions that exist in sensitive API permissions to solve these problems.

Hereby this framework allows non-privileged applications (clients) to use privileged sensitive APIs through applications (servers) that have already been granted
the privilege. It is also designed to be as 1:1 as possible with native APIs, 
allowing you to use as many APIs as possible with minimal time and effort for migration.

## How It Works?
Please refer to the paper [here](https://github.com/choiman1559/AmusePermit/raw/master/AmusePermit_Paper_(KR).pdf) </br>
(Sorry, we only offer Korean language papers at the moment!)

## Getting start
### Gradle Settings

First, [Download](https://github.com/choiman1559/AmusePermit/archive/refs/heads/master.zip) repository as zip or clone to your preferred location, and then copy `amuse_permit` directory to your project folder

Secondly, Sync the framework to your project Gradle by adding the following line to your root level `settings.gradle`:

```groovy
include ':amuse_permit'
include ':amuse_permit:wrapper-file'
include ':amuse_permit:wrapper-locate'
include ':amuse_permit:wrapper-querypkg'
include ':amuse_permit:wrapper-sms'
include ':amuse_permit:wrapper-telephony'
```

Next, add the following dependencies to your application-level `build.gradle` file:
```groovy
android {
    // Requires JDK 8 or later
    compileOptions {
        targetCompatibility JavaVersion.VERSION_1_8
        sourceCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    //ADD THIS LINE
    implementation project(":amuse_permit")
}
```

**_optional:_** Select the module that suits your purpose and add the following dependencies to your server application-level `build.gradle` file
in order to use API wrappers
```groovy
    implementation project(":amuse_permit:wrapper-file")        // For File implementation
    implementation project(":amuse_permit:wrapper-locate")      // For Geo-Locate implementation 
    implementation project(":amuse_permit:wrapper-querypkg")    // For Package Query implementation
    implementation project(":amuse_permit:wrapper-sms")         // For Sms implementation
    implementation project(":amuse_permit:wrapper-telephony")   // For Telephony implementation
```

And then re-sync whole project with gradle files to use them in the source codes.

### Application configuration

First, Add `Application` class to your AndroidManifest.xml like:
```
 <application
        · · ·
        android:name="YOUR_APPLICATION_CLASS" <!-- Add This line --> 
        />
```

Then, initialize AmusePermit Instance using `Instance.initialize(context, int)` method
```java
    public class YOUR_APPLICATION_CLASS extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        try {
             Instance instance = Instance.initialize(context, TODO_SELECT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```
The `TODO_SELECT` in the example code takes `Instance.OPERATION_MODE_CLIENT` or
`Instance.OPERATION_MODE_SERVER` as arguments, which determine the operating mode of AmusePermit.</br>
Here is a description of each mode:

`Instance.OPERATION_MODE_SERVER` : Share the permissions that this application has with other applications. 
To share permissions, you must register the Native Wrapper module for each API type in gradle.

`Instance.OPERATION_MODE_CLIENT` : Obtain permissions from other server applications to use sensitive APIs without special permission restrictions.

**_Note:_** If you want to use both server and client modes at the same time, you can nest the two flags using the Or(`|`) operator. </br>
Example: 
```java
Instance.initialize(context, Instance.OPERATION_MODE_CLIENT | Instance.OPERATION_MODE_SERVER)
```
This method allows you to share permissions with other apps while also transferring permissions from them.

### API Types description

AmusePermit provides wrappers for several sensitive APIs.  
These wrapper classes have the package name `com.amuse.permit.wrapper`.
Basically, they each have an independent Native Wrapper Gradle module, and without
these modules on server side applications, these api won't work.

| API Type        | Native API                                                  | Client Implementation                               | Required Native Wrapper Module | ProcessConst Type     |
|-----------------|-------------------------------------------------------------|-----------------------------------------------------|--------------------------------|-----------------------|
| File            | java.io.File                                                | com.amuse.permit.wrapper.file.File                  | :amuse_permit:wrapper-file     | ACTION_TYPE_FILE      |
| ContentProvider | android.content.ContentResolver                             | com.amuse.permit.wrapper.cursor.ProviderManager     |   None                         | ACTION_TYPE_CURSOR    |
| Geo-Location    | com.google.android.gms.location.FusedLocationProviderClient | com.amuse.permit.wrapper.locate.FusedLocationClient | :amuse_permit:locate           | ACTION_TYPE_LOCATION  |
| Package Query   | android.content.pm.PackageManager                           | com.amuse.permit.wrapper.pkg.PackageManager         | :amuse_permit:querypkg         | ACTION_TYPE_PACKAGE   |
| Sms             | android.telephony.SmsManager                                | com.amuse.permit.wrapper.sms.SmsManager             | :amuse_permit:sms              | ACTION_TYPE_SMS       |
| Telephony       | android.telephony.TelephonyManager                          | com.amuse.permit.wrapper.telephony.TelephonyManager | :amuse_permit:telephony        | ACTION_TYPE_TELEPHONY |

### Server-side specific configuration

#### Scopes allow range of API & Client
To determine which APIs and clients to allow and which not, 
AmusePermit provides scope functionality via the `NameFilters` interface.
Within this interface, returning true means allow, returning false means not allow.

Firstly, You can scope API types using `Instance.setFeaturedApiTypeScope(filters)`.
In this function, The arguments to `NameFilter` are constants of the API types listed in `ProcessConst`.
The code below shows an example that allows the File API but disallows the Location API.
</br>Code:
```java
    instance.setClientScope((NameFilters.NameFilter<String>) apiType -> {
         switch(apiType) {
            case ProcessConst.ACTION_TYPE_FILE:
                return true;
            case ProcessConst.ACTION_TYPE_LOCATION:
                return false;
                · · ·
         }
    });
```

Also, You can scope client using `Instance.setClientScope(filters)`.
In this function, The arguments to `NameFilter` are the package name of the client application requesting data 
from the server application is given. The code below shows an example that allows depending on specific-string match.
</br>Code:
```java
    instance.setClientScope((NameFilters.NameFilter<String>) clientPackageName -> {
         · · ·
         return clientPackageName.equals(YOUR_OWN_WHITE_LIST);
    });
```

#### Setting up ContentProvider
If you want to send large data such as File Stream or byte array to the client, 
or query it through `Cursor`, you need to register a `provider` in the manifest of the server application.
To do this, Copy-Paste below code to your `AndroidManifest.xml`
and Replace with your own application package name at `YOUR_APP_PACKAGE_NAME_HERE`.

Code:
```xml
    <provider
        android:name="com.amuse.permit.process.ProcessStream"
        android:authorities="com.amuse.permit.process.ProcessStream$[YOUR_APP_PACKAGE_NAME_HERE]"
        android:exported="true"
        android:grantUriPermissions="true"
        tools:ignore="ExportedContentProvider">
    </provider>
```

### Client-side API Usage

#### Set server peer

Before using the API, the client must first decide which server to retrieve data from. 
The following code shows the process of retrieving server information and registering it with the client's AmusePermit instance.
```java
ResultTask<AppPeer> serverPeer = AppPeer.fetchInformation(context, SERVER_PACKAGE_NAME_HERE);
serverPeer.setOnTaskCompleteListener(result -> {
        if(result.isSuccess()){
            instance.setServerPeer((AppPeer) result.getResultData());
        }
}).invokeTask();
```

**_Note:_** When the package name of the server is unknown, the client can use the `Instace.getAvailablePeers(context)` method to get a list of applications that have AmusePermit installed and try them out.
Then, see the `AppPeer.isAllowedByPeer()` method to see if this application is allowed by the server.

#### Default API constructors
Below is a list of client-target endpoints provided by AmusePermit and their default constructor names. 
Normally, you can use these to use the API, but depending on the API, there may be cases where you need to use a special constructor (e.g. `SmsManager.createForSubscriptionId(context, int)`)
other than the default constructor. In this case, please refer to the JavaDoc for each API.

- File :
```java
public static ResultTask<File> fileFrom(Context context, @NonNull String pathname)
```

- ProviderManager :
```java
public static ProviderManager getProviderManager(Context context)
 ```

- FusedLocationClient :
```java
public static FusedLocationClient getFusedLocationClient(Context context)
```

- PackageManager :
```java
public static PackageManager getPackageManager(Context context)
```

- SmsManager :
```java
public static SmsManager getDefaultSmsManager(Context context)
```

- TelephonyManager :
```java
public static TelephonyManager getDefaultTelephonyManager(Context context)
```