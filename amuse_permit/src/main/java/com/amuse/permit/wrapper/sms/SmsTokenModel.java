package com.amuse.permit.wrapper.sms;

import com.amuse.permit.model.Wrappable;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class SmsTokenModel extends Wrappable {
    @JsonIgnore
    private static final long serialVersionUID = 31415926545L;
    protected Integer smsSubscriptionId;
}
