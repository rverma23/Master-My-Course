package com.mastermycourse.paypal;

/**
 * Created by JamesDeCarlo on 4/16/17.
 */

import java.util.HashMap;
import java.util.Map;

/**
 *  For a full list of configuration parameters refer in wiki page.(https://github.com/paypal/sdk-core-java/blob/master/README.md)
 */
public class Configuration {

    // Creates a configuration map containing credentials and other required configuration parameters.
    public static final Map<String,String> getAcctAndConfig(){
        Map<String,String> configMap = new HashMap<String,String>();
        configMap.putAll(getConfig());

        // Account Credential
        configMap.put("acct1.UserName", "james.decarlo.sr-facilitator_api1.gmail.com");
        configMap.put("acct1.Password", "9R9HFQ5RVVBH2BYE");
        configMap.put("acct1.Signature", "AFcWxV21C7fd0v3bYYYRCpSSRl31APCoCswmJRJ4iO-TK7F88eylGidB");

        return configMap;
    }

    public static final Map<String,String> getConfig(){
        Map<String,String> configMap = new HashMap<String,String>();

        // Endpoints are varied depending on whether sandbox OR live is chosen for mode
        configMap.put("mode", "sandbox");

        return configMap;
    }
}