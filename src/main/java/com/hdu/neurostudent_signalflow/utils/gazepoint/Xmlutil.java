package com.hdu.neurostudent_signalflow.utils.gazepoint;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public class Xmlutil {
    public static Element parseXml(String xmlData) {
        try {
            // 创建 DocumentBuilderFactory 和 DocumentBuilder 对象
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // 将 XML 字符串解析为 Document 对象
            Document doc = builder.parse(new InputSource(new StringReader(xmlData)));

            // 获取根元素
            Element root = doc.getDocumentElement();

            return root;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
