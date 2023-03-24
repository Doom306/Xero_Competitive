package com.general_hello.bot.objects;

import com.general_hello.Config;

/**
 * <p>Make a <b>.env</b> file with the following contents and replace the <b>?</b> with the proper values</p>
 */

public class GlobalVariables {
    public static final String LINK = Config.get("link");
    public static final String VERSION = "1.0.0";

}
