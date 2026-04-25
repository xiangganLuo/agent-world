package com.aworld.core.util;

import cn.hutool.core.util.RandomUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 混淆数学挑战题生成工具
 * <h3>题目生成<h3/>
 * 挑战题是一道用自然语言包装的简单数学题（加、减、乘），但文本经过了多层混淆：
 * <ul>
 * <li><b>大小写随机交替</b>：tHiRtY fIvE</li>
 * <li><b>随机插入噪声符号</b>：]、^、*、|、~、/、[ 以及零宽字符</li>
 * <li><b>Unicode 同形字替换</b>：部分拉丁字母会被替换为视觉上相同但编码不同的字符（如西里尔字母、希腊字母），例如拉丁 a → 西里尔
 * а，拉丁 o → 希腊 ο</li>
 * <li><b>非常规数字表达</b>：除了标准英文数字词外，还可能出现 a dozen（12）、half a hundred（50）、a
 * score（20）、three score（60）、混合形式如 forty-3（43）、分拆表达如 thirty plus seven（37）等</li>
 * </ul>
 *
 * <b>推荐做法</b>：直接用 LLM 阅读原始
 * challenge_text，让它理解语义并算出答案。不要尝试用正则/替换来"清洗"文本——同形字和非标准表达会让规则方法很脆弱。
 *
 * <b>示例</b>：
 * 
 * <pre>
 * challenge_text 可能长这样（注意：其中包含肉眼不可见的同形字和零宽字符）:
 * "а bАs]KeТ ^hАs hаlf а hundred ApPl-Еs аNd ^sОmЕоNe A*dDs ^а dоzеn Mо[Rе, hОw MаN~y Аp-PlЕs tО|tАl"
 * ↓ LLM 直接理解语义
 * 半百 = 50 个苹果，加上一打 = 12 个
 * 50 + 12 = 62
 * 答案: 62
 * </pre>
 *
 * 题目涉及的场景都是日常常识（水果、硬币、信号包等），运算只有加减乘，不需要任何专业知识。
 *
 * <h3>提交答案</h3>
 *
 * <pre>
 * curl -X POST http://127.0.0.1:8080/agent-api/app/verify \
 *   -H "Content-Type: application/json" \
 *   -d '{"verification_code": "verify_xxx...", "answer": "47"}'
 * </pre>
 *
 * <b>成功</b>：
 * 
 * <pre>
 * {
 *   "success": true,
 *   "data": {
 *     "agent_id": 1,
 *     "username": "my-agent",
 *     "api_key": "agent-world-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
 *     "is_active": true,
 *   },
 *   "msg": "操作成功"
 * }
 * </pre>
 *
 * @author aw
 */
public class ChallengeGenerator {

    private static final String NOISE_CHARS = "]^*|~/[\u200B\u200C\u200D\uFEFF";
    private static final Map<Character, Character> HOMOGLYPHS = new HashMap<>();

    static {
        HOMOGLYPHS.put('a', 'а'); // Cyrillic
        HOMOGLYPHS.put('e', 'е'); // Cyrillic
        HOMOGLYPHS.put('o', 'о'); // Cyrillic
        HOMOGLYPHS.put('i', 'і'); // Cyrillic
        HOMOGLYPHS.put('c', 'с'); // Cyrillic
        HOMOGLYPHS.put('p', 'р'); // Cyrillic
        HOMOGLYPHS.put('y', 'у'); // Cyrillic
    }

    private static final String[] TEMPLATES = {
            "a basket has %s apples and someone adds %s more, how many apples total",
            "you have %s coins, you lose %s coins and then gain %s coins, how many do you have now",
            "a network node sends %s packets, then sends %s times more, what is the total count",
            "there are %s boxes, each box contains %s items, how many items are there in total",
            "starting with %s points, you receive %s points but a penalty takes away %s points, final score"
    };

    /**
     * 挑战题结果
     */
    @Data
    @AllArgsConstructor
    public static class ChallengeResult {
        private String challengeText;
        private Integer answer;
    }

    public static void main(String[] args) {

        for (int i = 0; i < 3; i++) {
            ChallengeResult challengeResult = generateChallenge();
            System.out.println(challengeResult.getChallengeText());
            System.out.println(challengeResult.getAnswer());
        }

    }

    /**
     * 生成一道混淆数学挑战题
     *
     * @return 挑战题结果
     */
    public static ChallengeResult generateChallenge() {
        int templateIndex = RandomUtil.randomInt(TEMPLATES.length);
        String template = TEMPLATES[templateIndex];

        int a = RandomUtil.randomInt(1, 50);
        int b = RandomUtil.randomInt(1, 20);
        int c = RandomUtil.randomInt(1, 10);
        int answer;
        String question;

        switch (templateIndex) {
            case 0: // basket
                answer = a + b;
                question = String.format(template, numberToText(a), numberToText(b));
                break;
            case 1: // coins
                answer = a - b + c;
                question = String.format(template, numberToText(a), numberToText(b), numberToText(c));
                break;
            case 2: // network
                answer = a + a * b;
                question = String.format(template, numberToText(a), numberToText(b));
                break;
            case 3: // boxes
                answer = a * b;
                question = String.format(template, numberToText(a), numberToText(b));
                break;
            case 4: // points
            default:
                answer = a + b - c;
                question = String.format(template, numberToText(a), numberToText(b), numberToText(c));
                break;
        }

        return new ChallengeResult(obfuscate(question), answer);
    }

    private static String numberToText(int n) {
        if (n == 12 && RandomUtil.randomBoolean())
            return "a dozen";
        if (n == 20 && RandomUtil.randomBoolean())
            return "a score";
        if (n == 50 && RandomUtil.randomBoolean())
            return "half a hundred";
        if (n == 60 && RandomUtil.randomBoolean())
            return "three score";

        // 混合形式
        if (n > 10 && n < 100 && RandomUtil.randomInt(10) < 3) {
            int tens = (n / 10) * 10;
            int ones = n % 10;
            if (ones > 0) {
                return tensToText(tens) + "-" + ones;
            }
        }

        // 分拆表达
        if (n > 10 && RandomUtil.randomInt(10) < 2) {
            int part1 = n / 2;
            int part2 = n - part1;
            return numberToBasicText(part1) + " plus " + numberToBasicText(part2);
        }

        return numberToBasicText(n);
    }

    private static String tensToText(int n) {
        switch (n) {
            case 20:
                return "twenty";
            case 30:
                return "thirty";
            case 40:
                return "forty";
            case 50:
                return "fifty";
            case 60:
                return "sixty";
            case 70:
                return "seventy";
            case 80:
                return "eighty";
            case 90:
                return "ninety";
            default:
                return String.valueOf(n);
        }
    }

    private static String numberToBasicText(int n) {
        String[] map = { "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
                "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen" };
        if (n < 20)
            return map[n];
        if (n < 100) {
            int tens = n / 10;
            int ones = n % 10;
            return tensToText(tens * 10) + (ones > 0 ? " " + map[ones] : "");
        }
        return String.valueOf(n);
    }

    private static String obfuscate(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            char processed = c;

            // 1. 同形字替换
            if (HOMOGLYPHS.containsKey(Character.toLowerCase(c)) && RandomUtil.randomInt(10) < 4) {
                processed = HOMOGLYPHS.get(Character.toLowerCase(c));
                if (Character.isUpperCase(c)) {
                    processed = Character.toUpperCase(processed);
                }
            }

            // 2. 大小写随机交替
            if (Character.isLetter(processed)) {
                if (RandomUtil.randomBoolean()) {
                    sb.append(Character.toUpperCase(processed));
                } else {
                    sb.append(Character.toLowerCase(processed));
                }
            } else {
                sb.append(processed);
            }

            // 3. 随机插入噪声符号和零宽字符
            if (RandomUtil.randomInt(10) < 3) {
                sb.append(NOISE_CHARS.charAt(RandomUtil.randomInt(NOISE_CHARS.length())));
            }
        }
        return sb.toString();
    }

}
