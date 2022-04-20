package MakeKeyword;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import java.io.IOException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class makeKeyword {
    String basic = "";
    public makeKeyword(){
        basic = "";
    }

    public void makeKeywordRun(String filename){
//        filename="collection.xml";
//        System.out.println(filename);

        int num=0;
        try{
            File f = new File(filename);
            FileInputStream fis = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line = "";
            while((line = br.readLine())!=null){
                if(line.contains("<doc id")){
                    num++;
                }
            }
            br.close();
        }catch (IOException e){
            e.printStackTrace();
            System.err.println("파일 이름을 확인하세요.");
        }



        String[][] file = new String[num][2];

        /*nodelist 가져오기*/
        //XML 문서 파싱
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = factory.newDocumentBuilder();

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Document document = null;
        try {
            File f = new File(filename);
            FileInputStream fis = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fis,"EUC-KR");
            InputSource is = new InputSource(isr);
            is.setEncoding("EUC-KR");

            document = documentBuilder.parse(is);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // root 구하기
        Element root = document.getDocumentElement();

        NodeList childeren = root.getChildNodes(); // 자식 노드 목록 get

        for(int i = 0; i < childeren.getLength(); i++){
            Node node = childeren.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE){ // 해당 노드의 종류 판정(Element일 때)
                Element ele = (Element)node;
                String nodeName = ele.getNodeName();
                if(nodeName.equals("doc")){
                    // 이름이 doc인 노드의 자식노드
                    NodeList childeren2 = ele.getChildNodes(); //자식노드리스트

                    for(int k=0;k<num;k++) {
                        if (ele.getAttribute("id").equals(k+"")){
                            for(int a = 0; a < childeren2.getLength(); a++){
                                Node node2 = childeren2.item(a);
                                if(node2.getNodeType() == Node.ELEMENT_NODE){
                                    Element ele2 = (Element)node2;

                                    String nodeName2 = ele2.getNodeName();
                                    if(nodeName2.equals("body")){
                                        file[k][0]=k+"";
                                        file[k][1]=ele2.getTextContent();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        //kkma 형태소 분석기
        String[] kkma = new String[num];
        for(int i=0;i<num;i++){
            kkma[i] = file[i][1];
            KeywordExtractor ke = new KeywordExtractor();
            KeywordList kl = ke.extractKeyword(kkma[i],true);
            String change="";
            for(int j=0;j<kl.size();j++){
                Keyword kwrd = kl.get(j);
                change += kwrd.getString()+":"+ kwrd.getCnt()+"#";
            }
            file[i][1]=change;
        }

        for(int i = 0; i < childeren.getLength(); i++){
            Node node = childeren.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE){ // 해당 노드의 종류 판정(Element일 때)
                Element ele = (Element)node;
                String nodeName = ele.getNodeName();

                if(nodeName.equals("doc")){
                    // 이름이 doc인 노드의 자식노드
                    NodeList childeren2 = ele.getChildNodes(); //자식노드리스트

                    for(int k=0;k<num;k++) {
                        if (ele.getAttribute("id").equals(k+"")){
                            for(int a = 0; a < childeren2.getLength(); a++){
                                Node node2 = childeren2.item(a);
                                if(node2.getNodeType() == Node.ELEMENT_NODE){
                                    Element ele2 = (Element)node2;

                                    String nodeName2 = ele2.getNodeName();
                                    if(nodeName2.equals("body")){
                                        ele2.setTextContent(file[k][1]);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        DOMSource source = new DOMSource(root);
        StreamResult result = null;
        try {
            result = new StreamResult(new FileWriter(new File("C:\\SimpleIR\\index.xml")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            transformer.transform(source,result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws ParserConfigurationException, TransformerException, IOException, SAXException {
//        makeKeyword mk = new makeKeyword();
//        mk.makeKeywordRun("collection.xml");
    }

}

