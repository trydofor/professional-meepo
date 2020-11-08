package pro.fessional.meepo.bind;

/**
 * @author trydofor
 * @since 2020-10-13
 */
public interface Const {

    String TXT$EMPTY = "";
    char[] ARR$EMPTY_CHARS = new char[0];
    Object[] ARR$EMPTY_OBJECT = new Object[0];
    String[] ARR$EMPTY_STRING = new String[0];


    // 解析
    String TKN$HIMEEPO = "HI-MEEPO";

    String TKN$DNA_ = "DNA:";
    String TKN$DNA_SET = "SET";
    String TKN$DNA_END = "END";
    String TKN$DNA_BKB = "BKB";
    String TKN$DNA_RAW = "RAW";

    String TKN$RNA_ = "RNA:";
    String TKN$RNA_PUT = "PUT";
    String TKN$RNA_USE = "USE";
    String TKN$RNA_RUN = "RUN";

    String TKN$RNA_WHEN = "WHEN";
    String TKN$RNA_EACH = "EACH";
    String TKN$RNA_ELSE = "ELSE";
    String TKN$RNA_DONE = "DONE";

    String TKN$WHEN_YES = "y|yes";
    String TKN$WHEN_NOT = "n|no|not";

    // 引擎
    String ENGINE$MAP = "map";
    String ENGINE$RAW = "raw";
    String ENGINE$URI = "uri";
    String ENGINE$EXE = "exe";
    String ENGINE$CMD = "cmd";
    String ENGINE$SH = "sh";
    String ENGINE$JS = "js";
    String ENGINE$JAVA = "java";

    // map 变量
    String KEY$ENVS_NOW = "now";
    String KEY$ENVS_NOW_DATE = "now.date";
    String KEY$ENVS_NOW_TIME = "now.time";

    // 对象
    char OBJ$NAVIGATOR = '.';

    // each
    String BLT$EACH_COUNT = "_count";
    String BLT$EACH_TOTAL = "_total";
}
