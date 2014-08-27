package harmony.android.library.model;

import java.io.Serializable;

/**
 * Created by HarmonyLee on 8/26/14.
 */
public class VenueMobile implements Serializable {
    private String name;
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
