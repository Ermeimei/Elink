package cn.nju.ws.test;

import junit.framework.TestCase;
import java.util.Map;

import cn.nju.ws.sim.Corpus;
import cn.nju.ws.sim.LdaGibbsSampler;
import cn.nju.ws.sim.LdaUtil;

public class TestLda extends TestCase {
    public void testAll() throws Exception {
       /* List<String> doc1 = new ArrayList<String>();
        doc1.add("hello");
        doc1.add("word");
        List<String> doc2 = new ArrayList<String>();
        doc2.add("hankcs");
        Corpus corpus = new Corpus();
        corpus.addDocument(doc1);
        corpus.addDocument(doc2);*/
        // 1. Load corpus from disk
        Corpus corpus = Corpus.load("data/mini");
        // 2. Create a LDA sampler
        LdaGibbsSampler ldaGibbsSampler = new LdaGibbsSampler(corpus.getDocument(), corpus.getVocabularySize());
        // 3. Train it
        ldaGibbsSampler.gibbs(10);
        // 4. The phi matrix is a LDA model, you can use LdaUtil to explain it.
        double[][] phi = ldaGibbsSampler.getPhi();
        Map<String, Double>[] topicMap = LdaUtil.translate(phi, corpus.getVocabulary(), 10);
        LdaUtil.explain(topicMap);
        // 5. TODO:Predict. I'm not sure whether it works, it is not stable.
      //  int[] document = Corpus.loadDocument("data/1.txt", corpus.getVocabulary());
        int[] document = Corpus.loadDocument("data/mini/军事_510.txt", corpus.getVocabulary());
        double[] tp = LdaGibbsSampler.inference(phi, document);
        Map<String, Double> topic = LdaUtil.translate(tp, phi, corpus.getVocabulary(), 10);
        LdaUtil.explain(topic);
    }
}
