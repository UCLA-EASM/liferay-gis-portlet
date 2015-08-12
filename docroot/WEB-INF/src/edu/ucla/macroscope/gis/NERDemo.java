package edu.ucla.macroscope.gis;

import java.util.List;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.DocumentReaderAndWriter;
import edu.stanford.nlp.util.Triple;


/** This is a demo of calling CRFClassifier programmatically.
 *  <p>
 *  Usage: {@code java -mx400m -cp "*" NERDemo [serializedClassifier [fileName]] }
 *  <p>
 *  If arguments aren't specified, they default to
 *  classifiers/english.all.3class.distsim.crf.ser.gz and some hardcoded sample text.
 *  If run with arguments, it shows some of the ways to get k-best labelings and
 *  probabilities out with CRFClassifier. If run without arguments, it shows some of
 *  the alternative output formats that you can get.
 *  <p>
 *  To use CRFClassifier from the command line:
 *  </p><blockquote>
 *  {@code java -mx400m edu.stanford.nlp.ie.crf.CRFClassifier -loadClassifier [classifier] -textFile [file] }
 *  </blockquote><p>
 *  Or if the file is already tokenized and one word per line, perhaps in
 *  a tab-separated value format with extra columns for part-of-speech tag,
 *  etc., use the version below (note the 's' instead of the 'x'):
 *  </p><blockquote>
 *  {@code java -mx400m edu.stanford.nlp.ie.crf.CRFClassifier -loadClassifier [classifier] -testFile [file] }
 *  </blockquote>
 *
 */

public class NERDemo {

  public static void main(String[] args) throws Exception {
	  
//    String serializedClassifier = "classifiers/english.all.3class.distsim.crf.ser.gz";
    String serializedClassifier = "classifiers/chinese.misc.distsim.crf.ser.gz";
    
    if (args.length > 0) {
      serializedClassifier = args[0];
    }

    AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);

    /* For either a file to annotate or for the hardcoded text example, this
       demo file shows several ways to process the input, for teaching purposes.
    */

    if (args.length > 1) {

      /* For the file, it shows (1) how to run NER on a String, (2) how
         to get the entities in the String with character offsets, and
         (3) how to run NER on a whole file (without loading it into a String).
      */

      String fileContents = IOUtils.slurpFile(args[1]);
      List<List<CoreLabel>> out = classifier.classify(fileContents);
      for (List<CoreLabel> sentence : out) {
        for (CoreLabel word : sentence) {
          System.out.print(word.word() + '/' + word.get(CoreAnnotations.AnswerAnnotation.class) + ' ');
        }
        System.out.println();
      }

      System.out.println("---");
      out = classifier.classifyFile(args[1]);
      for (List<CoreLabel> sentence : out) {
        for (CoreLabel word : sentence) {
          System.out.print(word.word() + '/' + word.get(CoreAnnotations.AnswerAnnotation.class) + ' ');
        }
        System.out.println();
      }

      System.out.println("---");
      List<Triple<String, Integer, Integer>> list = classifier.classifyToCharacterOffsets(fileContents);
      for (Triple<String, Integer, Integer> item : list) {
        System.out.println(item.first() + ": " + fileContents.substring(item.second(), item.third()));
      }
      System.out.println("---");
      System.out.println("Ten best entity labelings");
      DocumentReaderAndWriter<CoreLabel> readerAndWriter = classifier.makePlainTextReaderAndWriter();
      classifier.classifyAndWriteAnswersKBest(args[1], 10, readerAndWriter);

      System.out.println("---");
      System.out.println("Per-token marginalized probabilities");
      classifier.printProbs(args[1], readerAndWriter);

      // -- This code prints out the first order (token pair) clique probabilities.
      // -- But that output is a bit overwhelming, so we leave it commented out by default.
      // System.out.println("---");
      // System.out.println("First Order Clique Probabilities");
      // ((CRFClassifier) classifier).printFirstOrderProbs(args[1], readerAndWriter);

    } else {

      /* For the hard-coded String, it shows how to run it on a single
         sentence, and how to do this and produce several formats, including
         slash tags and an inline XML output format. It also shows the full
         contents of the {@code CoreLabel}s that are constructed by the
         classifier. And it shows getting out the probabilities of different
         assignments and an n-best list of classifications with probabilities.
      */

//      String[] example = {"Good afternoon Rajat Raina, how are you today?",
//                          "I go to school at Stanford University, which is located in California." };
//      String[] example = {"貣 摲 鵁麍, 楩椼 涬淠淉 犈犆犅 潧 腠腶舝 碢禗禈 箛箙舕 廞徲 鉌, 灉礭 溔 蒝蒧蓏 忷扴汥 姴怤昢 鳱 萐菿 莔莋莥 洷炟砏, 魵 絒翗腏 瞗穇縍 蓩蔮, 嶢嶜 灉礭蘠 痑祣筇 炾笀耔 踣 抰枅 燚璒瘭 醳鏻鐆 膣, 皵碡碙 灊灅甗 鍎鞚韕 樀樛 嗂, 餤駰鬳 裺觨誖 翀胲胵 蒛 雘雝 皾籈譧 擙樲橚 郺鋋錋 穊 罫蓱, 嵥 黐曮禷 鑏鑆驈 趏跮 "};
      
//    	Using QTS-test-corpus
      String[] example = {"崤 函 稱 地 險   襟 帶 壯 兩 京   霜 峰 直 臨 道   冰 河 曲 繞 城   古 木 參 差 影   寒 猿 斷 續 聲   冠 蓋 往 來 合   風 塵 朝 夕 驚   高 談 先 馬 度   偽 曉 預 雞 鳴   棄 繻 懷 遠 志   封 泥 負 壯 情   別 有 真 人 氣   安 知 名 不 名", // 1_10
    		  "翠 野 駐 戎 軒   盧 龍 轉 征 旆   遙 山 麗 如 綺   長 流 縈 似 帶   海 氣 百 重 樓   巖 松 千 丈 蓋   茲 焉 可 遊 賞   何 必 襄 城 外", // 1_11
    		  "玄 兔 月 初 明   澄 輝 照 遼 碣   映 雲 光 暫 隱   隔 樹 花 如 綴   魄 滿 桂 枝 圓   輪 虧 鏡 彩 缺   臨 城 卻 影 散   帶 暈 重 圍 結   駐 蹕 俯 九 都   停 觀 妖 氛 滅", // 1_12
    		  "碧 原 開 霧 隰   綺 嶺 峻 霞 城   煙 峰 高 下 翠   日 浪 淺 深 明   斑 紅 妝 蕊 樹   圓 青 壓 溜 荊   跡 巖 勞 傅 想   窺 野 訪 莘 情   巨 川 何 以 濟   舟 楫 佇 時 英"}; // 1_13
      
      for (String str : example) {
        System.out.println(classifier.classifyToString(str));
      }
      System.out.println("---");

      for (String str : example) {
        // This one puts in spaces and newlines between tokens, so just print not println.
        System.out.print(classifier.classifyToString(str, "slashTags", false));
      }
      System.out.println("---");

      for (String str : example) {
        // This one is best for dealing with the output as a TSV (tab-separated column) file.
        // The first column gives entities, the second their classes, and the third the remaining text in a document
        System.out.print(classifier.classifyToString(str, "tabbedEntities", false));
      }
      System.out.println("---");

      for (String str : example) {
        System.out.println(classifier.classifyWithInlineXML(str));
      }
      System.out.println("---");

      for (String str : example) {
        System.out.println(classifier.classifyToString(str, "xml", true));
      }
      System.out.println("---");

      for (String str : example) {
        System.out.print(classifier.classifyToString(str, "tsv", false));
      }
      System.out.println("---");

      // This gets out entities with character offsets
      int j = 0;
      for (String str : example) {
        j++;
        List<Triple<String,Integer,Integer>> triples = classifier.classifyToCharacterOffsets(str);
        for (Triple<String,Integer,Integer> trip : triples) {
          System.out.printf("%s over character offsets [%d, %d) in sentence %d.%n",
                  trip.first(), trip.second(), trip.third, j);
        }
      }
      System.out.println("---");

      // This prints out all the details of what is stored for each token
      int i=0;
      for (String str : example) {
        for (List<CoreLabel> lcl : classifier.classify(str)) {
          for (CoreLabel cl : lcl) {
            System.out.print(i++ + ": ");
            System.out.println(cl.toShorterString());
          }
        }
      }

      System.out.println("---");

    }
  }

}