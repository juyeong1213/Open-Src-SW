package makeCollection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class makeCollection {
    String basic = "";
    public makeCollection(){
        basic = "";
    }

    public void makeCollectionRun(String pathname) throws TransformerException, ParserConfigurationException, IOException {
//        System.out.println(pathname);
        File path = new File(pathname);
        File[] fileList = path.listFiles();
        String filename;
        String[][] file = new String[fileList.length][2];
        if (fileList.length > 0) {
            for (int i = 0; i < fileList.length; i++) {
                file[i][0] = fileList[i].getName().replaceAll(".html",""); // file name
                file[i][1] = "";
                filename = fileList[i].getPath();
                try{
//                    System.out.println(filename);
                    File f = new File(filename);
                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f),"utf-8"));

                    String line = "";
                    while((line = br.readLine())!=null){
                        Pattern pattern = Pattern.compile("<p>(.*?)</p>");
                        Matcher matcher = pattern.matcher(line);

                        while(matcher.find()){
                            file[i][1] += matcher.group(1)+"\n";
                            if (matcher.group(1) == null)
                                break;
                        }
                    }
                    br.close();
                }catch (IOException e){
                    e.printStackTrace();
                    System.err.println("파일 이름을 확인하세요.");
                }
            }
        }


        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();


        Document docu = docBuilder.newDocument();

        Element docs = docu.createElement("docs");
        docu.appendChild(docs);


        for(int i=0;i<fileList.length;i++){
            Element doc = docu.createElement("doc");
            docs.appendChild(doc);
            doc.setAttribute("id", Integer.toString(i));

            Element title = docu.createElement("title");
            doc.appendChild(title);
            title.appendChild(docu.createTextNode(file[i][0]));

            Element body = docu.createElement("body");
            doc.appendChild(body);
            body.appendChild(docu.createTextNode(file[i][1]));
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT,"yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount","2");

        DOMSource source = new DOMSource(docu);
        StreamResult result = null;
        result = new StreamResult(new FileWriter(new File("C:\\SimpleIR\\collection.xml")));



        transformer.transform(source,result);
    }

    public static void main(String[] args) throws ParserConfigurationException, TransformerException, IOException, SAXException {
        makeCollection mc = new makeCollection();
        mc.makeCollectionRun("C:\\SimpleIR\\data");

    }

}

