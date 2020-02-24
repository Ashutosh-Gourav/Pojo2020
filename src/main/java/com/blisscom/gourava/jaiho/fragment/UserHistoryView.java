package com.blisscom.gourava.jaiho.fragment;

import com.blisscom.gourava.jaiho.model.UserPoojaBookingHistoryItem;

import java.util.List;

/**
 * Created by gourava on 1/21/17.
 */

public interface UserHistoryView {

    void showHistoryItems(List<UserPoojaBookingHistoryItem> historyItems);
}
