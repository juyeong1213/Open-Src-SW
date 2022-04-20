package Indexer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

public class indexer {
    public indexer(){
    }
    public void find_kwd2(String kwd,String[][][] str,int y,int[] nn,int N){
        //i번째 파일에서 키워드 찾기!
        //len = nn[i]
        int df=0;
        double w;
        int tfy;

        int n=-1;
        //df == 단어 kwd가 몇 개의 문서에서 등장하는지
        for(int j=0;j<N;j++){
            for(int q=0;q<nn[j];q++){
                if (str[j][q][0].equals(kwd)) {
                    df++;
                }
            }
        }

        //문서 y에서 단어 kwd가 등장하는 횟수
        for(int j=0;j<nn[y];j++) {
            if (str[y][j][0].equals(kwd)) {
                n=j;
            }
        }
        if(n!=-1){
            tfy = Integer.parseInt(str[y][n][1]);
            w = tfy * Math.log((double) N/df);
            w = Math.round(w*100)/100.0;
        }


    }

    public String[][] find_kwd(String kwd,String[][][] str,int[] nn,int N){
        //i번째 파일에서 키워드 찾기!
        //len = nn[i]
        int df = 0;
        double[] w;
        w = new double[]{0,0,0,0,0};
        int[] tfy;
        tfy= new int[]{0, 0, 0, 0, 0};

        //df == 단어 kwd가 몇 개의 문서에서 등장하는지
        for(int j=0;j<N;j++){
            for(int q=0;q<nn[j];q++){
                if (str[j][q][0].equals(kwd)) {
                    df++;
                    tfy[j] = Integer.parseInt(str[j][q][1]);
                }
            }
        }
        String[][] list=new String[df][2];
        int nu=0;
        for(int i=0;i<N;i++){
            if(tfy[i]!=0){
//                System.out.print("파일: "+i+"  "+kwd+"의 빈도수:" +tfy[i]);
                w[i] = tfy[i] * Math.log((double) N/df);
//                System.out.println("  가중치는"+Math.round(w[i]*100)/100.0);
                list[nu][0] = i+"";
                list[nu][1] = Math.round(w[i]*100)/100.0+"";
                nu++;
            }
        }

        return list;
    }
    public void indexerRun(String pathname) throws IOException{
        FileOutputStream filestream = new FileOutputStream(pathname);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(filestream);

        HashMap indexMap=new HashMap();

        String filename = "C:\\SimpleIR\\index.xml";

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
            InputStreamReader isr = new InputStreamReader(fis,"EUC_KR");
            InputSource is = new InputSource(isr);
            is.setEncoding("EUC_KR");
            document = documentBuilder.parse(is);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[][] file = new String[num][2];
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

        int[] nn;
        nn= new int[]{0, 0, 0, 0, 0};
        for(int i=0;i<num;i++ ){
            String[] arr=file[i][1].split("#");
            for(String a : arr){
                nn[i]++;
            }
        }
        String[][][] splitfile = new String[num][][];

        for(int i=0;i<num;i++){
            splitfile[i] = new String[nn[i]][2];
        }
        //splitfile[1][3][0] == 1번파일의 3번째 키워드
        //splitfile[1][3][1] == 1번파일의 3번째 키워드의 빈도수
        for(int i=0;i<num;i++){
            String[] arr=file[i][1].split("#");
            for(int j=0;j<nn[i];j++){
                splitfile[i][j]=arr[j].split(":");
            }
        }
//        find_kwd(splitfile[0][0][0],splitfile,0,nn[0]);
        for(int i=0;i<num;i++){
            for(int j=0;j<nn[i];j++){
                String[][] list=find_kwd(splitfile[i][j][0], splitfile, nn, num);
                indexMap.put(splitfile[i][j][0],list);
            }
        }

//        indexMap.put();

        objectOutputStream.writeObject(indexMap);
        objectOutputStream.close();




    }

    public void postView(String pathname) throws IOException {
        FileInputStream filestream = new FileInputStream(pathname);
        ObjectInputStream objectInputStream = new ObjectInputStream(filestream);

        Object object = null;
        try {
            object = objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        objectInputStream.close();

        System.out.println("읽어온 객체의 type → "+object.getClass());

        HashMap hashMap = (HashMap)object;
        Iterator<String> it = hashMap.keySet().iterator();

        while(it.hasNext()){
            String key = it.next();
            String[][] value = (String[][])hashMap.get(key);
            int n = value.length;
            System.out.print(key+"->"+"[");
            for(int i=0;i<n;i++){
                if(i<n-1) {
                    System.out.print(value[i][0] + ", " + value[i][1]+"  ");
                }else{
                    System.out.print(value[i][0] + ", " + value[i][1]);

                }
            }
            System.out.println("]");

        }

    }


    public static void main(String[] args) throws IOException {

        indexer ie = new indexer();
        ie.indexerRun("C:\\SimpleIR\\index.post");
        ie.postView("C:\\SimpleIR\\index.post");
    }
}
