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

//    	Using QTS-test-corpus
      String[] example = {"以 茲 遊 觀 極   悠 然 獨 長 想   披 卷 覽 前 蹤   撫 躬 尋 既 往   望 古 茅 茨 約   瞻 今 蘭 殿 廣   人 道 惡 高 危   虛 心 戒 盈 蕩   奉 天 竭 誠 敬   臨 民 思 惠 養   納 善 察 忠 諫   明 科 慎 刑 賞   六 五 誠 難 繼   四 三 非 易 仰   廣 待 淳 化 敷   方 嗣 云 亭 響", // 1_1_10
    		  "秦 川 雄 帝 宅   函 谷 壯 皇 居   綺 殿 千 尋 起   離 宮 百 雉 餘   連 甍 遙 接 漢   飛 觀 迥 凌 虛 雲 日 隱 層 闕   風 煙 出 綺 疏", // 1_1_1
    		  "巖 廊 罷 機 務   崇 文 聊 駐 輦   玉 匣 啟 龍 圖   金 繩 披 鳳 篆   韋 編 斷 仍 續   縹 帙 舒 還 卷 對 此 乃 淹 留   欹 案 觀 墳 典", // 1_1_2
    		  "移 步 出 詞 林   停 輿 欣 武 宴   琱 弓 寫 明 月   駿 馬 疑 流 電   驚 雁 落 虛 弦   啼 猿 悲 急 箭 閱 賞 誠 多 美   於 茲 乃 忘 倦", // 1_1_3
      "鳴 笳 臨 樂 館   眺 聽 歡 芳 節   急 管 韻 朱 絃   清 歌 凝 白 雪   彩 鳳 肅 來 儀   玄 鶴 紛 成 列 去 茲 鄭 衛 聲   雅 音 方 可 悅", // 1_1_4
      "芳 辰 追 逸 趣   禁 苑 信 多 奇   橋 形 通 漢 上   峰 勢 接 雲 危   煙 霞 交 隱 映   花 鳥 自 參 差 何 如 肆 轍 跡   萬 里 賞 瑤 池", // 1_1_5
      "飛 蓋 去 芳 園   蘭 橈 遊 翠 渚   萍 間 日 彩 亂   荷 處 香 風 舉   桂 楫 滿 中 川   弦 歌 振 長 嶼 豈 必 汾 河 曲   方 為 歡 宴 所", // 1_1_6
      "落 日 雙 闕 昏   回 輿 九 重 暮   長 煙 散 初 碧   皎 月 澄 輕 素   搴 幌 玩 琴 書   開 軒 引 雲 霧 斜 漢 耿 層 閣   清 風 搖 玉 樹", // 1_1_7
      "歡 樂 難 再 逢   芳 辰 良 可 惜   玉 酒 泛 雲 罍   蘭 殽 陳 綺 席   千 鍾 合 堯 禹   百 獸 諧 金 石 得 志 重 寸 陰   忘 懷 輕 尺 璧", // 1_1_8
      "建 章 歡 賞 夕   二 八 盡 妖 妍   羅 綺 昭 陽 殿   芬 芳 玳 瑁 筵   珮 移 星 正 動   扇 掩 月 初 圓 無 勞 上 懸 圃   即 此 對 神 仙"}; // 1_1_9
      
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