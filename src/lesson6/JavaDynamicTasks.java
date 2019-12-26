package lesson6;

import kotlin.NotImplementedError;

import java.util.List;

@SuppressWarnings("unused")
public class JavaDynamicTasks {
    /**
     * Наибольшая общая подпоследовательность.
     * Средняя
     *
     * Дано две строки, например "nematode knowledge" и "empty bottle".
     * Найти их самую длинную общую подпоследовательность -- в примере это "emt ole".
     * Подпоследовательность отличается от подстроки тем, что её символы не обязаны идти подряд
     * (но по-прежнему должны быть расположены в исходной строке в том же порядке).
     * Если общей подпоследовательности нет, вернуть пустую строку.
     * Если есть несколько самых длинных общих подпоследовательностей, вернуть любую из них.
     * При сравнении подстрок, регистр символов *имеет* значение.
     */
    public static String longestCommonSubSequence(String first, String second)
    {
        int fLeng = first.length();
        int sLeng = second.length();

        if (fLeng < 1 || sLeng < 1)
            return "";
        if (fLeng == 1 && second.contains(first))
            return first;
        else if (fLeng == 1 && !second.contains(first))
            return "";
        if (sLeng == 1 && first.contains(second))
            return second;
        else if (sLeng == 1 && !first.contains(second))
            return "";

        int[][] m = new int[fLeng][sLeng];

        for (int i = 1; i < sLeng; i++)
        {
            if (first.charAt(0) == second.charAt(i) || m[0][i-1] == 1)
                m[0][i] = 1;
        }
        for (int i = 1; i < fLeng; i++)
        {
            if (second.charAt(0) == first.charAt(i) || m[i-1][0] == 1)
                m[i][0] = 1;
        }

        int prevV;
        int prevH;
        int prevD;

        for (int i = 1; i < fLeng; i++)
        {
            for (int j = 1; j < sLeng; j++)
            {
                prevV = m[i - 1][j];
                prevH = m[i][j - 1];
                prevD = m[i - 1][j - 1];

                if (first.charAt(i) == second.charAt(j))
                    m[i][j] = prevD + 1;
                else
                    m[i][j] = Math.max(prevH, prevV);
            }
        }

        int l = m[fLeng - 1][sLeng - 1];
        StringBuilder sb = new StringBuilder();

        for (int i = fLeng - 1; i >= 0; i--)
        {
            for (int j = sLeng - 1; j >= 0; j--)
            {
                if (m[i][j] == l
                        && (l == 1
                        && (j == 0 && i == 0
                        || j == 0 && m[i - 1][j] == 0
                        || i == 0 && m[i][j - 1] == 0)
                        || i > 0 && j > 0
                        && m[i][j] != m[i - 1][j]
                        && m[i][j] != m[i][j - 1]
                        && m[i][j] == m[i - 1][j - 1] + 1))
                {
                    l--;
                    sb.append(first.charAt(i));
                    i--;
                }
                if (l == 0)
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * Наибольшая возрастающая подпоследовательность
     * Сложная
     *
     * Дан список целых чисел, например, [2 8 5 9 12 6].
     * Найти в нём самую длинную возрастающую подпоследовательность.
     * Элементы подпоследовательности не обязаны идти подряд,
     * но должны быть расположены в исходном списке в том же порядке.
     * Если самых длинных возрастающих подпоследовательностей несколько (как в примере),
     * то вернуть ту, в которой числа расположены раньше (приоритет имеют первые числа).
     * В примере ответами являются 2, 8, 9, 12 или 2, 5, 9, 12 -- выбираем первую из них.
     */
    public static List<Integer> longestIncreasingSubSequence(List<Integer> list) {
        throw new NotImplementedError();
    }

    /**
     * Самый короткий маршрут на прямоугольном поле.
     * Средняя
     *
     * В файле с именем inputName задано прямоугольное поле:
     *
     * 0 2 3 2 4 1
     * 1 5 3 4 6 2
     * 2 6 2 5 1 3
     * 1 4 3 2 6 2
     * 4 2 3 1 5 0
     *
     * Можно совершать шаги длиной в одну клетку вправо, вниз или по диагонали вправо-вниз.
     * В каждой клетке записано некоторое натуральное число или нуль.
     * Необходимо попасть из верхней левой клетки в правую нижнюю.
     * Вес маршрута вычисляется как сумма чисел со всех посещенных клеток.
     * Необходимо найти маршрут с минимальным весом и вернуть этот минимальный вес.
     *
     * Здесь ответ 2 + 3 + 4 + 1 + 2 = 12
     */
    public static int shortestPathOnField(String inputName) {
        throw new NotImplementedError();
    }

    // Задачу "Максимальное независимое множество вершин в графе без циклов"
    // смотрите в уроке 5
}
