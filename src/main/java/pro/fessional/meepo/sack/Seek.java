package pro.fessional.meepo.sack;

/**
 * @author trydofor
 * @since 2020-10-16
 */
public class Seek {
    /**
     * 区域内查找特征字符
     *
     * @param txt 源
     * @param off 开始位置，包含
     * @param end 结束位置，不包含
     * @param tkn 特征
     * @return -1 如果没有
     */
    public static int seekToken(String txt, int off, int end, String tkn, boolean echo) {
        int tln = tkn.length();
        boolean fd = false;
        for (int i = off, len = end - tln; i <= len; i++) {
            if (txt.regionMatches(i, tkn, 0, tln)) {
                fd = true;
                if (!echo) {
                    return i;
                }
            } else {
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
                if (txt.regionMatches(i, tkn, 0, tln)) {
                    return i;
                } else {
                    return -1;
                }
            }
        }
        return -1;
    }

    public static int seekPrevGrace(String txt, int off, int end) {
        if (off < 0 || end < 0) return -1;
        for (int i = end - 1; i >= off; i--) {
            char c = txt.charAt(i);
            if (c == '\n') {
                break;
            } else if (notWhite(c)) {
                return i;
            }
        }
        return -1;
    }

    public static int seekNextGrace(String txt, int off, int end) {
        if (off < 0 || end < 0) return -1;
        for (int i = off; i < end; i++) {
            char c = txt.charAt(i);
            if (c == '\n') {
                break;
            } else if (notWhite(c)) {
                return i;
            }
        }
        return -1;
    }

    public static int seekPrevWhite(String txt, int off, int end) {
        if (off < 0 || end < 0) return -1;
        for (int i = end - 1; i >= off; i--) {
            char c = txt.charAt(i);
            if (c == '\n') {
                break;
            } else if (isWhite(c)) {
                return i;
            }
        }
        return -1;
    }

    public static int seekNextWhite(String txt, int off, int end) {
        if (off < 0 || end < 0) return -1;
        for (int i = off; i < end; i++) {
            char c = txt.charAt(i);
            if (c == '\n') {
                break;
            } else if (isWhite(c)) {
                return i;
            }
        }
        return -1;
    }

    public static int[] seekPrevWords(String txt, int off, int end) {
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

    public static int[] seekNextWords(String txt, int off, int end) {
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

    public static int seekNextSplit(String txt, int off, int end) {
        for (int i = off; i < end; i++) {
            if (isSplit(txt.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    public static int[] seekNextAlnum(String txt, int off, int end) {
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


    public static int[] trimBlank(String txt, int off, int end) {
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
     */
    public static int seekPrevEdge(String txt, int end) {
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
     */
    public static int seekNextEdge(String txt, int off) {
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
}