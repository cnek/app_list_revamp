
package com.demo.ken.listing_revamp.mvp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TopAppPojo {

    @SerializedName("feed")
    @Expose
    public Feed feed;

    public class Feed {

        @SerializedName("entry")
        @Expose
        public List<Entry> entry = null;

        public  class Entry {

            @SerializedName("im:artist")
            @Expose
            public ImName imArtist;
            @SerializedName("summary")
            @Expose
            public ImName summary;
            @SerializedName("category")
            @Expose
            public Category category;
            @SerializedName("im:name")
            @Expose
            public ImName imName;
            @SerializedName("im:image")
            @Expose
            public List<ImName> imImage = null;
            @SerializedName("id")
            @Expose
            public Id id;

            @SerializedName("averageUserRating")
            @Expose
            public Float averageUserRating;
            @SerializedName("userRatingCount")
            @Expose
            public Integer userRatingCount;

            public class ImName {

                @SerializedName("label")
                @Expose
                public String label;

                @Override
                public String toString() {
                    return "ImName{" +
                            "label='" + label + '\'' +
                            '}';
                }
            }

            public class Id {

                @SerializedName("label")
                @Expose
                public String label;
                @SerializedName("attributes")
                @Expose
                public Attributes attributes;

                public class Attributes {

                    @SerializedName("im:id")
                    @Expose
                    public String imId;
                    @SerializedName("im:bundleId")
                    @Expose
                    public String imBundleId;

                    @Override
                    public String toString() {
                        return "Attributes{" +
                                "imId='" + imId + '\'' +
                                ", imBundleId='" + imBundleId + '\'' +
                                '}';
                    }
                }

                @Override
                public String toString() {
                    return "Id{" +
                            "label='" + label + '\'' +
                            ", attributes=" + attributes +
                            '}';
                }
            }

            public class Category {

                @SerializedName("attributes")
                @Expose
                public Attributes attributes;

                public class Attributes {

                    @SerializedName("label")
                    @Expose
                    public String label;

                    @Override
                    public String toString() {
                        return "Attributes{" +
                                "label='" + label + '\'' +
                                '}';
                    }
                }

                @Override
                public String toString() {
                    return "Category{" +
                            "attributes=" + attributes +
                            '}';
                }
            }

            @Override
            public String toString() {
                return "Entry{" +
                        "imArtist=" + imArtist +
                        ", summary=" + summary +
                        ", category=" + category +
                        ", imName=" + imName +
                        ", imImage=" + imImage +
                        ", id=" + id +
                        ", averageUserRating=" + averageUserRating +
                        ", userRatingCount=" + userRatingCount +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "Feed{" +
                    "entry=" + entry +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TopAppPojo{" +
                "feed=" + feed +
                '}';
    }
}
