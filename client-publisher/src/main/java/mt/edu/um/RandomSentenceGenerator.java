package mt.edu.um;

import java.util.Random;
import java.util.stream.Stream;

/**
 * Created by matthew on 09/01/2016.
 */
public class RandomSentenceGenerator {

    String[] englishWords = {"the","of","and","a","to","in","is","you","that","it","he","was","for","on","are","as","with","his","they","I","at","be","this","have","from","or","one","had","by","word","but","not","what","all","were","we","when","your","can","said","there","use","an","each","which","she","do","how","their","if","will","up","other","about","out","many","then","them","these","so","some","her","would","make","like","him","into","time","has","look","two","more","write","go","see","number","no","way","could","people","my","than","first","water","been","call","who","oil","its","now","find","long","down","day","did","get","come","made","may","part"};

    public String generate(int length) {
        StringBuilder builder = new StringBuilder();
        Stream.generate(() -> englishWords[new Random().nextInt(englishWords.length)])
                .limit(length)
                .forEach(word -> builder.append(word + " "));
        return builder.toString();
    }
}
