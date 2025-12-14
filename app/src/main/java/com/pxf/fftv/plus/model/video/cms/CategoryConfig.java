package com.pxf.fftv.plus.model.video.cms;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Category configuration loader and manager
 * Loads category data from JSON configuration file
 */
public class CategoryConfig {
    private static final String TAG = "CategoryConfig";
    private static final String CONFIG_FILE = "categories.json";

    private static CategoryConfig instance;
    private CategoryData data;
    private Map<Integer, L1Category> l1Map;
    private Map<Integer, L2Category> l2Map;

    private CategoryConfig(Context context) {
        loadConfig(context);
        buildMaps();
    }

    public static CategoryConfig getInstance(Context context) {
        if (instance == null) {
            synchronized (CategoryConfig.class) {
                if (instance == null) {
                    instance = new CategoryConfig(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private void loadConfig(Context context) {
        try {
            InputStream is = context.getAssets().open(CONFIG_FILE);
            InputStreamReader reader = new InputStreamReader(is, "UTF-8");
            Gson gson = new Gson();
            data = gson.fromJson(reader, CategoryData.class);
            reader.close();
            Log.d(TAG, "Loaded " + data.l1Categories.size() + " L1 categories and " +
                    data.l2Categories.size() + " L2 categories");
        } catch (IOException e) {
            Log.e(TAG, "Failed to load categories config", e);
            // Initialize empty data as fallback
            data = new CategoryData();
            data.l1Categories = new ArrayList<>();
            data.l2Categories = new ArrayList<>();
        }
    }

    private void buildMaps() {
        l1Map = new HashMap<>();
        l2Map = new HashMap<>();

        if (data.l1Categories != null) {
            for (L1Category category : data.l1Categories) {
                l1Map.put(category.id, category);
            }
        }

        if (data.l2Categories != null) {
            for (L2Category category : data.l2Categories) {
                l2Map.put(category.id, category);
            }
        }
    }

    /**
     * Get category name by ID (searches both L1 and L2)
     */
    public String getCategoryName(int categoryId) {
        if (l1Map.containsKey(categoryId)) {
            return l1Map.get(categoryId).name;
        }
        if (l2Map.containsKey(categoryId)) {
            return l2Map.get(categoryId).name;
        }
        return "Unknown Category " + categoryId;
    }

    /**
     * Get all L1 (parent) categories
     */
    public List<L1Category> getL1Categories() {
        return data.l1Categories;
    }

    /**
     * Get subcategories for a given L1 category
     */
    public List<L2Category> getSubcategories(int parentId) {
        List<L2Category> subcategories = new ArrayList<>();
        if (data.l2Categories != null) {
            for (L2Category category : data.l2Categories) {
                if (category.parent == parentId) {
                    subcategories.add(category);
                }
            }
        }
        return subcategories;
    }

    /**
     * Get L1 category by ID
     */
    public L1Category getL1Category(int categoryId) {
        return l1Map.get(categoryId);
    }

    /**
     * Get L2 category by ID
     */
    public L2Category getL2Category(int categoryId) {
        return l2Map.get(categoryId);
    }

    // Data classes for JSON deserialization
    private static class CategoryData {
        @SerializedName("l1_categories")
        List<L1Category> l1Categories;

        @SerializedName("l2_categories")
        List<L2Category> l2Categories;
    }

    public static class L1Category {
        @SerializedName("id")
        public int id;

        @SerializedName("name")
        public String name;

        @SerializedName("subcategories")
        public List<Integer> subcategories;
    }

    public static class L2Category {
        @SerializedName("id")
        public int id;

        @SerializedName("name")
        public String name;

        @SerializedName("parent")
        public int parent;
    }
}
