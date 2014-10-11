package com.delta.delta;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by phalgun on 10/11/14.
 */
public class Bucketify {

    public static void main(String[] args) {

        System.out.println(getBucket("artist"));
        System.out.println(getBucket("law"));
        System.out.println(getBucket("lw"));
        System.out.println(getBucket("medical"));
    }

    public static String getBucket(String occupation) {

        if (occupation == "") {
            return "";
        }

        Set<String> tech = new HashSet<String>(Arrays.asList(
                new String[]{"developer", "developers", "technical", "software", "hardware", "engineer",
                        "engineers", "tech"}
        ));

        Set<String> law = new HashSet<String>(Arrays.asList(
                new String[]{"lawyer", "barrister", "advocate", "criminal", "attorney", "defender",
                        "counsel", "legal", "attorney-at-law", "jurist", "jury"}
        ));

        Set<String> art = new HashSet<String>(Arrays.asList(
                new String[]{"artists", "artist", "dancer", "dancers", "musician", "musicians", "music",
                        "dance", "ballet", "tap-dancer", "dance", "sing", "singer", "opera", "painter"}
        ));

        Set<String> journalism = new HashSet<String>(Arrays.asList(
                new String[]{"journalists", "journalist", "reporter", "news", "media", "writing", "press",
                        "newspaper", "broadcast", "tv", "anchor", "journalism"}
        ));

        Set<String> medicine = new HashSet<String>(Arrays.asList(
                new String[]{"doctor", "medical", "medicine", "healing", "healer", "therapeutical",
                        "doctors", "healers"}
        ));

        Set<String> finance = new HashSet<String>(Arrays.asList(
                new String[]{"analyst", "finance", "analysts", "market", "marketing", "financial",
                        "markets", "retail", "broker", "stock", "stocks"}
        ));

        if (tech.contains(occupation)) {
            return "tech";
        }

        if (law.contains(occupation)) {
            return "law";
        }

        if (art.contains(occupation)) {
            return "art";
        }

        if (journalism.contains(occupation)) {
            return "journalism";
        }


        if (medicine.contains(occupation)) {
            return "medicine";
        }

        if (finance.contains(occupation)) {
            return "finance";
        }


        return "none";

    }
}
