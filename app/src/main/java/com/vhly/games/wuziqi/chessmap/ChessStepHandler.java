package com.vhly.games.wuziqi.chessmap;

import com.vhly.games.wuziqi.game.StepData;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: xhzhang
 * Date: 13-11-14
 */
public class ChessStepHandler extends DefaultHandler {
    private Stack<StepData> stepData;


    public Stack<StepData> getStepData() {
        return stepData;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        stepData = new Stack<StepData>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        String tagName = localName;
        if (localName == null) {
            tagName = qName;
        }
        if (tagName != null) {
            if (tagName.equals("step")) {

                String panx = attributes.getValue("panx");
                String pany = attributes.getValue("pany");
                String turn = attributes.getValue("turn");
                String desc = attributes.getValue("desc");
                if (panx != null && pany != null) {
                    try {
                        int px = Integer.parseInt(panx);
                        int py = Integer.parseInt(pany);
                        int t = Integer.parseInt(turn);
                        StepData step = new StepData();
                        step.panX = px;
                        step.panY = py;
                        step.turn = t;
                        step.desc = desc;
                        stepData.push(step);
                    } catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                    }
                }
            }
        }
    }
}
