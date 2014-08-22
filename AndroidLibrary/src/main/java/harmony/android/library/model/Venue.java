package harmony.android.library.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by HarmonyLee on 8/20/14.
 */
public class Venue extends NetworkResult {
    @SerializedName("id")
    String			id;

    @SerializedName("name")
    String			name;

    @SerializedName("location")
    Location		location;

    @SerializedName("categories")
    List<Category>	categories;

    public class Location extends NetworkResult {
        @SerializedName("lat")
        String	lat;

        @SerializedName("lng")
        String	lng;

        @SerializedName("distance")
        String	distance;

        @SerializedName("address")
        String	address;

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

    }

    public class Category extends NetworkResult {
        @SerializedName("icon")
        Icon	icon;

        public class Icon extends NetworkResult {
            @SerializedName("prefix")
            String	prefix;

            @SerializedName("suffix")
            String	suffix;

            public String getPrefix() {
                return prefix;
            }

            public void setPrefix(String prefix) {
                this.prefix = prefix;
            }

            public String getSuffix() {
                return suffix;
            }

            public void setSuffix(String suffix) {
                this.suffix = suffix;
            }
        }

        public Icon getIcon() {
            return icon;
        }

        public void setIcon(Icon icon) {
            this.icon = icon;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

}
