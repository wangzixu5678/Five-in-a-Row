package com.vhly.upsys.plist;

import android.util.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: xhzhang
 * Date: 13-11-8
 */
public class PlistHandler extends AbsSAXHandler {
    private Stack<PropertyItem> itemLevel = new Stack<PropertyItem>();

    private String currentKey;

    private PropertyItem root;

    public PropertyItem getRoot() {
        return root;
    }

    /**
     * 子类必须 在 startElement 中 最上方 调用 super.startElement
     *
     * @param uri
     * @param localName
     * @param qName
     * @param attributes
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (localName.equals("key")) {
            currentKey = null;
        } else if (localName.equals("dict")) {
            PropertyItem object = new PropertyItem(ItemType.dict);
            object.setValue(new Hashtable<String, PropertyItem>());
            object.setKey(currentKey);
            if (!itemLevel.isEmpty()) {
                appendItemToLastLevel(object);
                itemLevel.push(object);
            } else {
                root = object;
                itemLevel.push(object);
            }
        } else if (localName.equals("array")) {
            PropertyItem object = new PropertyItem(ItemType.array);
            object.setValue(new Vector<PropertyItem>());
            object.setKey(currentKey);
            if (!itemLevel.isEmpty()) {
                appendItemToLastLevel(object);
                itemLevel.push(object);
            } else {
                root = object;
                itemLevel.push(object);
            }
        }
    }


    /**
     * 子类必须在 endElement 最下方调用 super.endElement
     *
     * @param uri
     * @param localName
     * @param qName
     * @throws org.xml.sax.SAXException
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        Log.d("PlistHandler", "endElement localName:" + localName + " qName:" + qName);
        String text = null;
        if (currentText != null) {
            text = currentText.toString();
        }

        if (localName.equals("key")) {
            if (text != null) {
                currentKey = text.trim();
            }
        } else if (localName.equals("plist")) {
            // FINISH
        } else if (localName.equals("string")) {
            PropertyItem object = new PropertyItem(ItemType.str);
            object.setValue(text);
            appendItemToLastLevel(object);
        } else if (localName.equals("true") || localName.equals("false")) {
            PropertyItem object = new PropertyItem(ItemType.bool);
            object.setValue(Boolean.valueOf(localName));
            appendItemToLastLevel(object);
        } else if (localName.equals("integer")) {
            PropertyItem object = new PropertyItem(ItemType.integer);
            if (text != null) {
                object.setValue(Integer.parseInt(text.trim()));
            } else {
                object.setValue(0);
            }
            appendItemToLastLevel(object);
        } else if (localName.equals("real")) {
            PropertyItem object = new PropertyItem(ItemType.real);
            if (text != null) {
                object.setValue(Double.parseDouble(text.trim()));
            } else {
                object.setValue(0.0d);
            }
            appendItemToLastLevel(object);
        } else if (localName.equals("date")) {
            PropertyItem object = new PropertyItem(ItemType.date);
            object.setValue(text);
            appendItemToLastLevel(object);
        } else if (localName.equals("data")) {
            PropertyItem object = new PropertyItem(ItemType.data);
            object.setValue(text);
            appendItemToLastLevel(object);
        } else if (localName.equals("dict") || localName.equals("array")) {
            itemLevel.pop();
        }
        super.endElement(uri, localName, qName);
    }

    private void appendItemToLastLevel(PropertyItem object) {
        if (!itemLevel.isEmpty()) {
            PropertyItem peek = itemLevel.peek();
            ItemType type = peek.getType();
            switch (type) {
                case array:
                    Vector<PropertyItem> arr = (Vector<PropertyItem>) peek.getValue();
                    arr.add(object);
                    break;
                case dict:
                    Hashtable<String, PropertyItem> dict = (Hashtable<String, PropertyItem>) peek.getValue();
                    dict.put(currentKey, object);
                    break;
            }
        }
    }
}
