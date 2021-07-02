package pro.fessional.meepo.util;

/**
 * @author trydofor
 * @since 2020-10-16
 */
public class Seek {
    /**
     * 区域内查找特征字符
     *
     * @param txt  源
     * @param off  开始位置，包含
     * @param end  结束位置，不包含
     * @param tkn  特征
     * @param echo 是否叠字
     * @return -1 如果没有
     */
    public static int seekToken(String txt, int off, int end, String tkn, boolean echo) {
        int tln = tkn.length();
        boolean fd = false;
        for (int i = off, len = end - tln; i <= len; i++) {
            if (txt.regionMatches(true, i, tkn, 0, tln)) {
                fd = true;
                if (!echo) {
                    return i;
                }
            }
            else {
                if (fd) {
                    return i - 1;
                }
            }
        }
        return -1;
    }

    public static int seekFollow(String txt, int off, int end, String tkn) {
        int tln = tkn.length();
        for (int i = off, len = end - tln; i <= len; i++) {
            if (notWhite(txt.charAt(i))) {
                if (txt.regionMatches(true, i, tkn, 0, tln)) {
                    return i;
                }
                else {
                    return -1;
                }
            }
        }
        return -1;
    }

    public static int seekPrevGrace(CharSequence txt, int off, int end) {
        if (off < 0 || end < 0) return -1;
        for (int i = end - 1; i >= off; i--) {
            char c = txt.charAt(i);
            if (c == '\n') {
                break;
            }
            else if (notWhite(c)) {
                return i;
            }
        }
        return -1;
    }

    public static int seekNextGrace(CharSequence txt, int off, int end) {
        if (off < 0 || end < 0) return -1;
        for (int i = off; i < end; i++) {
            char c = txt.charAt(i);
            if (c == '\n') {
                break;
            }
            else if (notWhite(c)) {
                return i;
            }
        }
        return -1;
    }

    public static int seekPrevWhite(CharSequence txt, int off, int end) {
        if (off < 0 || end < 0) return -1;
        for (int i = end - 1; i >= off; i--) {
            char c = txt.charAt(i);
            if (c == '\n') {
                break;
            }
            else if (isWhite(c)) {
                return i;
            }
        }
        return -1;
    }

    public static int seekNextWhite(CharSequence txt, int off, int end) {
        if (off < 0 || end < 0) return -1;
        for (int i = off; i < end; i++) {
            char c = txt.charAt(i);
            if (c == '\n') {
                break;
            }
            else if (isWhite(c)) {
                return i;
            }
        }
        return -1;
    }

    public static int[] seekPrevWords(CharSequence txt, int off, int end) {
        if (off < 0 || end < 0) return new int[]{-1, -1};

        int pos1, pos0;
        for (pos1 = end - 1; pos1 >= off; pos1--) {
            if (notWhite(txt.charAt(pos1))) {
                break;
            }
        }
        for (pos0 = pos1; pos0 >= off; pos0--) {
            char c = txt.charAt(pos0);
            if (isWhite(c) || c == '\n') {
                break;
            }
        }

        return new int[]{pos0 + 1, pos1 + 1};
    }

    public static int[] seekNextWords(CharSequence txt, int off, int end) {
        if (off < 0 || end < 0) return new int[]{-1, -1};

        int pos0, pos1;
        for (pos0 = off; pos0 < end; pos0++) {
            if (notWhite(txt.charAt(pos0))) {
                break;
            }
        }
        for (pos1 = pos0; pos1 < end; pos1++) {
            char c = txt.charAt(pos1);
            if (isWhite(c) || c == '\n') {
                break;
            }
        }

        return new int[]{pos0, pos1};
    }

    public static int seekNextSplit(CharSequence txt, int off, int end) {
        for (int i = off; i < end; i++) {
            if (isSplit(txt.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    public static int[] seekNextAlnum(CharSequence txt, int off, int end) {
        if (off < 0 || end < 0) return new int[]{-1, -1};

        int pos0, pos1;
        for (pos0 = off; pos0 < end; pos0++) {
            if (isAlnum(txt.charAt(pos0))) {
                break;
            }
        }
        for (pos1 = pos0; pos1 < end; pos1++) {
            char c = txt.charAt(pos1);
            if (!isAlnum(c)) {
                break;
            }
        }

        return new int[]{pos0, pos1};
    }


    public static int[] trimBlank(CharSequence txt, int off, int end) {
        for (int i = off; i < end; i++) {
            if (notBlank(txt.charAt(i))) {
                off = i;
                break;
            }
        }
        for (int i = end - 1; i > off; i--) {
            if (notBlank(txt.charAt(i))) {
                end = i + 1;
                break;
            }
        }

        return new int[]{off, end};
    }

    /**
     * 左侧不包含`\n`，index为start
     *
     * @param txt 文本
     * @param end 结束，不含
     * @return 边缘位置
     */
    public static int seekPrevEdge(CharSequence txt, int end) {
        for (int i = end - 1; i >= 0; i--) {
            char c = txt.charAt(i);
            if (notWhite(c)) {
                return c == '\n' ? i + 1 : -1;
            }
        }
        return 0;
    }

    /**
     * 右侧包含`\n`，index为end
     *
     * @param txt 文本
     * @param off 开始，含
     * @return 边缘位置
     */
    public static int seekNextEdge(CharSequence txt, int off) {
        int len = txt.length();
        for (int i = off; i < len; i++) {
            char c = txt.charAt(i);
            if (notWhite(c)) {
                return c == '\n' ? i + 1 : -1;
            }
        }
        return len;
    }

    public static boolean isAlnum(char c) {
        return (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z');
    }

    public static boolean isSplit(char c) {
        return c != '!' && !isWhite(c) && !isAlnum(c);
    }

    public static boolean isWhite(char c) {
        return c == ' ' || c == '\t' || c == '\r';
    }

    public static boolean notWhite(char c) {
        return c != ' ' && c != '\t' && c != '\r';
    }

    public static boolean notBlank(char c) {
        return c != ' ' && c != '\t' && c != '\n' && c != '\r';
    }

    public static int indent(CharSequence text, int end) {
        if (end <= 0) return 0;
        int eg = Seek.seekPrevEdge(text, end);
        return eg >= 0 ? end - eg : 0;
    }

    //////

    /**
     * 特征字符的次数
     *
     * @param txt 源
     * @param off 开始位置，包含
     * @param tkn 特征字符
     * @return 特征字符的次数
     */
    public static int countPreToken(String txt, int off, String tkn) {
        if (tkn == null || tkn.isEmpty()) return 0;
        int cnt = 0;
        for (int ln = tkn.length(), i = off - ln; i >= 0; i -= ln) {
            if (txt.regionMatches(i, tkn, 0, ln)) {
                cnt++;
            }
            else {
                break;
            }
        }
        return cnt;
    }

    /**
     * 向前找特征字符（未被转义）及转义数量
     *
     * @param txt 源
     * @param off 开始位置，包含
     * @param end 结束位置，不包含
     * @param tkn 特征字符
     * @param esc 转义字符
     * @return 特征字符的offset及转义数量
     */
    public static int[] seekPrevToken(String txt, int off, int end, String tkn, String esc) {
        for (int ln = tkn.length(), i = end - 1; i >= off; i--) {
            if (txt.regionMatches(i, tkn, 0, ln)) {
                int ct = countPreToken(txt, i, esc);
                return new int[]{i, ct};
            }
        }

        return new int[]{-1, 0};
    }

    /**
     * 向后找特征字符（未被转义）及转义数量
     *
     * @param txt 源
     * @param off 开始位置，包含
     * @param end 结束位置，不包含
     * @param tkn 特征字符
     * @param esc 转义字符
     * @return 特征字符的offset及转义数量
     */
    public static int[] seekNextToken(String txt, int off, int end, String tkn, String esc) {
        for (int ln = tkn.length(), i = off; i < end; i++) {
            if (txt.regionMatches(i, tkn, 0, ln)) {
                int ct = countPreToken(txt, i, esc);
                return new int[]{i, ct};
            }
        }
        return new int[]{-1, 0};
    }
}
