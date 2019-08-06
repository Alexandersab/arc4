
import java.util.Arrays;

public class RC4Utils {
    /**
     * 进制转换使用功能的字符数组集合
     */
    private static final char[] HEX_ELEMENTS = "0123456789abcdef".toCharArray();

    /**
     * byte转String时byte为负值的偏移量处理
     */
    private static final int OFFSET = 256;
    /**
     * 加解密需要的基础数组大小
     */
    private static final int CAPACITY = 256;
    /**
     * 十六进制字符串转换时的进位信息
     */
    private static final int RADIX = 16;

    /**
     * 加解密时的秘钥信息
     */
    private static final String SECURE_KEY;

    static {
        // 秘钥信息默认在环境变量中， 也可以写死或者从配置文件读取
        SECURE_KEY = System.getenv("RC4KEY");
        if (SECURE_KEY == null) {
            throw new IllegalArgumentException("RC4KEY is not in the environmental variables!");
        }
    }

    /**
     * 字符串加密过程
     *
     * @param src
     * @return
     */
    public static String encrypt(String src) {
        if (src == null || src.trim().length() == 0) {
            return src;
        }
        int[] codePoints = getCodePoints(src.toCharArray());
        String convert = convert(codePoints);
        return bytesToHex(convert.getBytes());
    }

    /**
     * 解密功能
     *
     * @param src
     * @return
     */
    public static String decrypt(String src) {
        if (src == null || src.trim().length() == 0) {
            return src;
        }
        int[] codePoints = getCodePoints(hexToCharArray(src));
        return convert(codePoints);
    }

    /**
     * 加解密核心处理过程
     *
     * @param codePoints
     * @return
     */
    private static String convert(int[] codePoints) {
        StringBuilder builder = new StringBuilder();
        int[] seeds = initSeed();
        int cur = 0, next = 0;
        for (int item : codePoints) {
            cur = (cur + 1) % CAPACITY;
            next = (next + seeds[cur]) % CAPACITY;
            swapElements(seeds, cur, next);
            int codePoint = item ^ seeds[(seeds[cur] + seeds[next]) % CAPACITY];
            builder.append(new String(Character.toChars(codePoint)));
        }
        return builder.toString();
    }

    /**
     * 获取字符数组实际的码点信息， 主要是兼容非UTF-8的场景
     *
     * @param message
     * @return
     */
    private static int[] getCodePoints(char[] message) {
        int[] codePoints = new int[Character.codePointCount(message, 0, message.length)];
        int i = 0, start = 0;
        while (i < message.length) {
            if (Character.isHighSurrogate(message[i]) && i < message.length - 1 && Character.isLowSurrogate(message[i + 1])) {
                codePoints[start++] = Character.toCodePoint(message[i], message[i + 1]);
                i++;
            } else {
                codePoints[start++] = message[i];
            }
            i++;
        }
        return codePoints;
    }

    /**
     * @param numbers
     * @param src
     * @param dst     交换数组特定两个索引对应的元素值
     */
    private static void swapElements(int[] numbers, int src, int dst) {
        int temp = numbers[src];
        numbers[src] = numbers[dst];
        numbers[dst] = temp;
    }

    /**
     * 加解密基本数组初始化
     *
     * @return
     */
    private static int[] initSeed() {
        byte[] secureKeyBytes = SECURE_KEY.getBytes();
        int[] seed = new int[CAPACITY];
        Arrays.parallelSetAll(seed, i -> i);

        int next = 0;
        for (int current = 0; current < seed.length; current++) {
            next = (next + seed[current] + secureKeyBytes[current % SECURE_KEY.length()]) % CAPACITY;
            swapElements(seed, current, next);
        }
        return seed;
    }

    /**
     * 二进制转换为十六进制字符串
     *
     * @param src
     * @return
     */
    private static String bytesToHex(byte[] src) {
        char[] buffer = new char[src.length * 2];
        int i = 0;
        for (byte item : src) {
            int convertedItem = item < 0 ? item + OFFSET : item;
            buffer[i++] = HEX_ELEMENTS[convertedItem / RADIX];
            buffer[i++] = HEX_ELEMENTS[convertedItem % RADIX];
        }
        return new String(buffer);
    }

    /**
     * 二进制字符串转换为char数组
     *
     * @param src
     * @return
     */
    private static char[] hexToCharArray(String src) {
        if (src == null || src.trim().isEmpty()) {
            return new char[]{};
        }
        byte[] bytes = new byte[src.length() >> 1];
        char[] chars = src.toCharArray();
        for (int i = 0; i < bytes.length; i++) {
            String subStr = new String(chars, i << 1, 2);
//            String subStr = src.substring(i << 1, (i << 1) + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, RADIX);
        }
        return new String(bytes).toCharArray();
    }


    public static void main(String[] args) {
        System.out.println(decrypt("e6b7a5e58db4e8be8cf0a4bf80e994b8e6b2aee5bf91e4ba81e5bfaee8a7b6e6948ce58f85e69e96e99b87e58587e58dba"));
        System.out.println(encrypt("海南辉\uD853\uDF97长江影业影视文化有限公司"));
        System.out.println(encrypt("今天天气特别好"));
    }

}
