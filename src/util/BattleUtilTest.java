package util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
public class BattleUtilTest {
    public BattleUtilTest() {}

    @Test
    void isFileXML() {
        Assertions.assertTrue(XMLUtil.getXmlFile().getName().equals("ships.xml"));
    }
}
