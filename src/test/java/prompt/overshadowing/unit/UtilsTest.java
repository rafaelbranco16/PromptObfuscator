package prompt.overshadowing.unit;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import prompt.overshadowing.utils.Utils;

@QuarkusTest
public class UtilsTest {
    @Test
    public void replaceWordOutOfId() {
        String sentence = "My name is {name_1_73e19eb7-5626-421b-b757-b2e2e857fa9d}. I work with " +
                "{name_2_73e19eb7-5626-421b-b757-b2e2e857fa9d}. {name_3_73e19eb7-5626-421b-b757-b2e2e857fa9d} " +
                "works here as well. My age is 26.";
        String toReplace = "26";
        String replacement = "newAge";
        String expected = "My name is {name_1_73e19eb7-5626-421b-b757-b2e2e857fa9d}. I work with " +
                "{name_2_73e19eb7-5626-421b-b757-b2e2e857fa9d}. {name_3_73e19eb7-5626-421b-b757-b2e2e857fa9d} " +
                "works here as well. My age is newAge.";
        String actual = Utils.replaceInBetweenBrackets(sentence, toReplace, replacement);

        Assertions.assertEquals(expected, actual);
    }
    @Test
    public void replaceWordOutOfId2() {
        String sentence = "My name is Rafael. I work with " +
                "{name_2_73e19eb7-5626-421b-b757-b2e2e857fa9d}. {name_3_73e19eb7-5626-421b-b757-b2e2e857fa9d} " +
                "works here as well. My age is 26.";
        String toReplace = "Rafael";
        String replacement = "newName";
        String expected = "My name is newName. I work with " +
                "{name_2_73e19eb7-5626-421b-b757-b2e2e857fa9d}. {name_3_73e19eb7-5626-421b-b757-b2e2e857fa9d} " +
                "works here as well. My age is 26.";
        String actual = Utils.replaceInBetweenBrackets(sentence, toReplace, replacement);

        Assertions.assertEquals(expected, actual);
    }
    @Test
    public void replaceWordOutOfId3() {
        String sentence = "My name is Rafael. I work with " +
                "{name_2_73e19eb7-5626-421b-b757-b2e2e857fa9d}. {name_3_73e19eb7-5626-421b-b757-b2e2e857fa9d} " +
                "works here as well. My age is 26.";
        String toReplace = "name_2_73e19eb7-5626-421b-b757-b2e2e857fa9d";
        String replacement = "newName";
        String expected = "My name is Rafael. I work with " +
                "{name_2_73e19eb7-5626-421b-b757-b2e2e857fa9d}. {name_3_73e19eb7-5626-421b-b757-b2e2e857fa9d} " +
                "works here as well. My age is 26.";
        String actual = Utils.replaceInBetweenBrackets(sentence, toReplace, replacement);

            Assertions.assertEquals(expected, actual);
    }
}
