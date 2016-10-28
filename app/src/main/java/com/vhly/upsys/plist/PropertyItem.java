package com.vhly.upsys.plist;

/**
 * Created by IntelliJ IDEA.
 * User: xhzhang
 * Date: 13-11-8
 */
public class PropertyItem {
    private ItemType type;
    private String key;
    private Object value;

    public PropertyItem(ItemType type) {
        this.type = type;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public ItemType getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}
