package Searcher;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class searcher {
    public void InnerProduct(String pathname, String query) {
        //=======kkma 형태소 분석기로 Query문에서 키워드 찾기!=======
        KeywordExtractor ke = new KeywordExtractor();
        KeywordList kl = ke.extractKeyword(query,true);
        int queryKnum=kl.size();
        String[] QueryK = new String[queryKnum];
        int[] QueryW = new int[queryKnum];
        for(int i=0;i<queryKnum;i++){
            Keyword kwrd = kl.get(i);
            QueryK[i]=kwrd.getString();
            QueryW[i]=kwrd.getCnt();
        }

        //=====================키워드 찾기 끝====================


        //=====================문서개수========================
        int num = 0;
        String ti = "";
        String filename = "C:\\SimpleIR\\collection.xml";
        try{
            File f = new File(filename);
            FileInputStream fis = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line = "";
            while((line = br.readLine())!=null){
                if(line.contains("<title>")){
                    num++;
                    Pattern pattern = Pattern.compile("<title>(.*?)</title>");
                    Matcher matcher = pattern.matcher(line);
                    while(matcher.find()){
                        ti += matcher.group(1)+"#";
                        if (matcher.group(1) == null)
                            break;
                    }
                }
            }
            br.close();
        }catch (IOException e){
            e.printStackTrace();
            System.err.println("파일 이름을 확인하세요.");
        }

        String[] title = ti.split("#");

        //=====================문서개수 끝======================

        Double[] simqid = new Double[num];
        for(int i=0;i<num;i++){simqid[i]=0.0;}

        //=============================유사도================================
        try{
            FileInputStream filestream = new FileInputStream(pathname);
            ObjectInputStream objectInputStream = new ObjectInputStream(filestream);

            Object object = null;
            try {
                object = objectInputStream.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            objectInputStream.close();

//            System.out.println("읽어온 객체의 type → "+object.getClass());


            for(int k=0;k<queryKnum;k++){
                HashMap hashMap = (HashMap)object;
                Iterator<String> it = hashMap.keySet().iterator();

                while(it.hasNext()){
                    String key = it.next();
                    String[][] value = (String[][])hashMap.get(key);
                    if(QueryK[k].equals(key)){
                        // i번째 문서의 가중치가 존재하면 weight값, 존재하지 않으면 0.0으로 초기화
                        Double[] val = new Double[num];

                        int n = value.length;

                        for(int x=0;x<num;x++){ val[x]=0.0;}
                        for(int i=0;i<n;i++){
                            val[Integer.parseInt(value[i][0])]=Double.parseDouble(value[i][1]);
                        }

                        for(int i=0;i<num;i++){
                            //k번째 키워드 TF * i번째 문서의 가중치를
                            //i번째 문서와 쿼리의 내적 결과에 더해준다!

                            simqid[i] += QueryW[k]*val[i];
                        }
                        break;
                    }
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        int[][] seq= new int[num][2];
        for(int i=0;i<num;i++){
            simqid[i] = Math.round(simqid[i]*100)/100.0;
//            System.out.println(simqid[i]);
        }
        Double[] arr = simqid.clone();
        int[] titlenum = {-1,-1,-1};
        Arrays.sort(arr, Collections.reverseOrder());
        for(int i=0;i<3;i++){
            for(int j=0;j<num;j++){
                if(arr[i]==simqid[j]){
                    if(titlenum[0]!=j && titlenum[1]!=j && titlenum[2]!=j)
                        titlenum[i]=j;
                }
            }
            System.out.println((i+1)+". "+title[titlenum[i]]);
        }
    }

    public void CalcSim(String pathname, String query) {
        //=======kkma 형태소 분석기로 Query문에서 키워드 찾기!=======
        KeywordExtractor ke = new KeywordExtractor();
        KeywordList kl = ke.extractKeyword(query,true);
        int queryKnum=kl.size();
        String[] QueryK = new String[queryKnum];
        int[] QueryW = new int[queryKnum];
        for(int i=0;i<queryKnum;i++){
            Keyword kwrd = kl.get(i);
            QueryK[i]=kwrd.getString();
            QueryW[i]=kwrd.getCnt();
        }

        //=====================키워드 찾기 끝====================


        //=====================문서개수========================
        int num = 0;
        String ti = "";
        String filename = "C:\\SimpleIR\\collection.xml";
        try{
            File f = new File(filename);
            FileInputStream fis = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line = "";
            while((line = br.readLine())!=null){
                if(line.contains("<title>")){
                    num++;
                    Pattern pattern = Pattern.compile("<title>(.*?)</title>");
                    Matcher matcher = pattern.matcher(line);
                    while(matcher.find()){
                        ti += matcher.group(1)+"#";
                        if (matcher.group(1) == null)
                            break;
                    }
                }
            }
            br.close();
        }catch (IOException e){
            e.printStackTrace();
            System.err.println("파일 이름을 확인하세요.");
        }

        String[] title = ti.split("#");

        //=====================문서개수 끝======================

        Double[] simqid = new Double[num];
        for(int i=0;i<num;i++){simqid[i]=0.0;}

        double[] id_scale=new double[num];
        for(int i=0;i<num;i++){id_scale[i]=0.0;}

        double[] q_scale=new double[num];
        for(int i=0;i<num;i++){q_scale[i]=0.0;}

        double[] id_val=new double[num];
        for(int i=0;i<num;i++){id_scale[i]=0.0;}

        //=============================유사도================================
        try{
            FileInputStream filestream = new FileInputStream(pathname);
            ObjectInputStream objectInputStream = new ObjectInputStream(filestream);

            Object object = null;
            try {
                object = objectInputStream.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            objectInputStream.close();

//            System.out.println("읽어온 객체의 type → "+object.getClass());

            for(int k=0;k<queryKnum;k++){
                HashMap hashMap = (HashMap)object;
                Iterator<String> it = hashMap.keySet().iterator();
                int exist=0;
                while(it.hasNext()){
                    String key = it.next();
                    String[][] value = (String[][])hashMap.get(key);
                    if(QueryK[k].equals(key)){
                        exist=1;
                        // i번째 문서의 가중치가 존재하면 weight값, 존재하지 않으면 0.0으로 초기화
                        Double[] val = new Double[num];

                        int n = value.length;

                        for(int x=0;x<num;x++){ val[x]=0.0;}
                        for(int i=0;i<n;i++){
                            val[Integer.parseInt(value[i][0])]=Double.parseDouble(value[i][1]);
                        }

                        for(int i=0;i<num;i++){
                            //k번째 키워드 TF * i번째 문서의 가중치를
                            //i번째 문서와 쿼리의 내적 결과에 더해준다!
                            simqid[i] += QueryW[k]*val[i];

                            //제곱해서 더해준 후 나중에 제곱근을 취해 크기를 구한다.
                            id_scale[i] += val[i]*val[i];
                            q_scale[i] += QueryW[k]*QueryW[k];
                        }
                        break;
                    }
                }

                //단어가 아예 없을 경우
                if(exist==0){
                    for(int i=0;i<num;i++){
                        simqid[i] += QueryW[k]*0.0;
                        id_scale[i] += 0.0*0.0;
                        q_scale[i] += QueryW[k]*QueryW[k];
                    }
                }

            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        int[][] seq= new int[num][2];
        for(int i=0;i<num;i++){
            simqid[i] = Math.round(simqid[i]*100)/100.0;
//            System.out.println("simqid["+i+"] : "+simqid[i]);
        }

        //**********************  cos 유사도  ************************
        Double[] cos=new Double[num];
        for(int i=0;i<num;i++) {
            simqid[i] = Math.round(simqid[i]*100)/100.0;
            cos[i] = simqid[i]/(Math.sqrt(id_scale[i])*Math.sqrt(q_scale[i]));
            cos[i] = Math.round(cos[i]*100)/100.0;
        }

        //상위 3개 문서
        Double[] arr = cos.clone();
        int[] titlenum = {-1,-1,-1};
        Arrays.sort(arr, Collections.reverseOrder());

        int k=0;
        for(int i=0;i<3;i++){
            for(int j=0;j<num;j++){
                if(arr[i]==cos[j]){
                    if (titlenum[0] != j && titlenum[1] != j && titlenum[2] != j) {
                        titlenum[i] = j;
                    }
                }
            }
            if(cos[titlenum[i]]!=0) { //cos값이 0이라면 순위를 매기지 않는다.
                k++;
                System.out.println(k + ". " + title[titlenum[i]]);
            }
        }
    }

    public static void main(String[] args) {
        searcher sc = new searcher();
        String query = "라면에는 면, 분말스프가 있다.";
        String query2 = "물을 담을 때 원통 모양의 컵을 사용한다.";
        sc.CalcSim("C:\\SimpleIR\\index.post",query2);
    }
}
