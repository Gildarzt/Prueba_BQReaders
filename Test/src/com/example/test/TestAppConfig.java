package com.example.test;

import android.content.Context;

import com.dropbox.sync.android.DbxAccountManager;

public class TestAppConfig {
	private TestAppConfig() {}

	private static String appKey = "4rsxxjd1d0sczfu";
	private static String appSecret = "hmmwmf5uzeduy37";

    public static DbxAccountManager getAccountManager(Context context)
    {
        return DbxAccountManager.getInstance(context.getApplicationContext(), appKey, appSecret);
    }
}
