package com.jinan.ladongjiguan.anjiantong.PublicClass;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Doc2Xml {
    public static boolean saveParam2Xml(String ip_value, String socket_value) {

        // 文档生成器工厂
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // 实例化文档生成器
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();

            File f = new File("../Test/xml/text.xml");
            if (!f.exists()) {
                System.out.println("=======");

                f.createNewFile();

                // 生成一个文档
                Document document = builder.newDocument();

                // 创建根节点
                Element configs = document.createElement("configs");

                // 创建XML文件所需的各种对象并序列化(元素)
                Element config = document.createElement("config");// 创建元素节点

                Element ip = document.createElement("ip");
                Element socket = document.createElement("socket");

                Text ip_text = document.createTextNode(ip_value);// 创建text文本
                Text socket_text = document.createTextNode(socket_value);

                ip.appendChild(ip_text);
                socket.appendChild(socket_text);

                config.appendChild(ip);
                config.appendChild(socket);

                configs.appendChild(config);

                document.appendChild(configs);// 添加到文档中

                // 调用方法，将文档写入xml文件中
                return Doc2Xml.write2Xml(document, f);
            } else {

                // 解析文档
                Document document = builder.parse(f);
                Element configs = document.getDocumentElement();// 得到根节点，把后面创建的子节点加入这个跟节点中

                // 创建XML文件所需的各种对象并序列化(元素)
                Element config = document.createElement("config");// 创建元素节点

                Element ip = document.createElement("ip");
                Element socket = document.createElement("socket");

                Text ip_text = document.createTextNode(ip_value);// 创建text
                Text socket_text = document.createTextNode(socket_value);

                ip.appendChild(ip_text);
                socket.appendChild(socket_text);

                config.appendChild(ip);
                config.appendChild(socket);

                configs.appendChild(config);// 添加到根节点中

                // 调用方法，将文档写入xml文件中
                return Doc2Xml.write2Xml(document, f);
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();

            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;

    }

    public static boolean write2Xml(Document document, File file) {
        // 创建转化工厂
        TransformerFactory factory = TransformerFactory.newInstance();
        // 创建转换实例
        try {
            Transformer transformer = factory.newTransformer();

            // 将建立好的DOM放入DOM源中
            DOMSource domSource = new DOMSource(document);

            // 创建输出流
            StreamResult result = new StreamResult(file);

            // 开始转换
            transformer.transform(domSource, result);

            return true;

        } catch (TransformerConfigurationException e) {
            e.printStackTrace();

            return false;
        } catch (TransformerException e) {
            e.printStackTrace();

            return false;
        }
    }

}
