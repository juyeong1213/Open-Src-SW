package Main;

import OSHW3.makeKeyword;
import Searcher.searcher;
import makeCollection.makeCollection;
import makePost.indexer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

//in cmd
//javac -encoding utf-8 -cp C:\kkma-2.1.jar -classpath ".;lib" Main\kuir.java

public class kuir {
    public static void main(String[] args) throws IOException, TransformerException, ParserConfigurationException {
        makeCollection mc = new makeCollection();
        makeKeyword mk = new makeKeyword();
        indexer ie = new indexer();
        searcher sc = new searcher();

        if(args.length==2) {
            if (args[0].equals("-c")) {
                mc.makeCollectionRun(args[1]);
            } else if (args[0].equals("-k")) {
                mk.makeKeywordRun(args[1]);
            } else if(args[0].equals("-i")){
                ie.indexerRun(args[1]);
//                ie.postView(args[1]);
            }else {
                System.err.println("argument를 확인해주세요.");
            }
        }else if(args.length==4){
            if(args[0].equals("-s")&&args[2].equals("-q")){
                sc.CalcSim(args[1],args[3]);
            }
        }
    }
}
