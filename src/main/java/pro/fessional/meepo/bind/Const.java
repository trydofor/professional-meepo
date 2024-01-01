package pro.fessional.meepo.bind;

import java.util.regex.Pattern;

/**
 * @author trydofor
 * @since 2020-10-13
 */
public interface Const {

    String TXT$EMPTY = "";
    char[] ARR$EMPTY_CHARS = new char[0];
    Object[] ARR$EMPTY_OBJECT = new Object[0];
    String[] ARR$EMPTY_STRING = new String[0];

    // Placeholder
    String TKN$VAR_PRE = "{{";
    String TKN$VAR_SUF = "}}";
    String TKN$VAR_ESC = "";

    // Parse Token
    String TKN$HIMEEPO = "HI-MEEPO";

    String TKN$DNA_ = "DNA:";
    String TKN$DNA_SET = "SET";
    String TKN$DNA_END = "END";
    String TKN$DNA_BKB = "BKB";
    String TKN$DNA_RAW = "RAW";
    String TKN$DNA_SON = "SON";

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

    // Execution Engine
    String ENGINE$MAP = "map";
    String ENGINE$RAW = "raw";
    String ENGINE$URI = "uri";
    String ENGINE$EXE = "exe";
    String ENGINE$CMD = "cmd";
    String ENGINE$SH = "sh";
    String ENGINE$JS = "js";
    String ENGINE$JAVA = "java";
    String ENGINE$FUN = "fun";

    // Object
    char OBJ$NAVI_DOT = '.';
    char OBJ$PIPE_BAR = '|';
    char OBJ$CHAR_ESC = '\\';

    // Each loop
    String BLT$EACH_COUNT = "_count";
    String BLT$EACH_TOTAL = "_total";
    String BLT$EACH_FIRST = "_first";
    String BLT$EACH_LAST = "_last";

    // Literal
    String ARG$BOOL_TRUE = "TRUE";
    String ARG$BOOL_FALSE = "FALSE";

    String ARG$NUMBER_DSUF = "D";
    String ARG$NUMBER_FSUF = "F";
    String ARG$NUMBER_NSUF = "N";
    String ARG$NUMBER_LSUF = "L";
    Pattern ARG$NUMBER_REGEX = Pattern.compile("^([-+])?([0-9_,.]+)([DFNL]?)$", Pattern.CASE_INSENSITIVE);

}
